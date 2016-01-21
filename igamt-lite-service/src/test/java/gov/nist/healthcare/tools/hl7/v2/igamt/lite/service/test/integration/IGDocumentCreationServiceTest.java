/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessagesRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class IGDocumentCreationServiceTest {

	@Autowired
	IGDocumentRepository igDocumentRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	IGDocumentService igDocumentService;

	@Autowired
	IGDocumentCreationService igDocumentCreation;

	@Autowired
	MessagesRepository messagesRepository;

	@Autowired
	MessageRepository messageRepository;
	
	static ProfileCreationReferentialIntegrityTest refIneteg;

	@BeforeClass
	public static void setup() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = IGDocumentCreationServiceTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);
			refIneteg = new ProfileCreationReferentialIntegrityTest();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void testProfileStandardProfilePreloaded() {
		//FIXME for now mongo db is loaded with 2 profiles; ultimately version 2.5 until 2.8 should be preloaded
		assertEquals(9, igDocumentCreation.findProfilesByHl7Versions().size());
	}
	
	@Test
	public void testSummary() {
		String[] arr = {"ADT", "ACK", "RCI", "QRY", "OML"};
		List<String[]> msgsAll = igDocumentCreation.summary("2.7", new ArrayList<String>());
		List<String[]> msgsSansArr = igDocumentCreation.summary("2.7", Arrays.asList(arr));
		
		assertTrue(msgsAll.size() > msgsSansArr.size());
	}
	
	@Test
	public void testigDocumentCreation() throws IOException, ProfileException {
		// Collect version numbers
		assertEquals(7, igDocumentCreation.findHl7Versions().size());

		// Collect standard messages and message descriptions
		// There should be only one HL7STANDARD profile for each version
		for (String hl7Version : Arrays.asList("2.5.1", "2.7")) {
			int found = igDocumentRepository.findStandardByVersion(hl7Version).size();
			assertEquals(1, found);
		}
		IGDocument igDocumentSource = igDocumentRepository.findStandardByVersion("2.7").get(0);
		assertEquals(193, igDocumentSource.getProfile().getMessages().getChildren().size());

		// Each description has 4 items: id, event, strucId, description
		List<String[]> msgDesc = igDocumentCreation.summary("2.7", new ArrayList<String>());
		assertEquals(4, msgDesc.get(0).length);
		
		// Creation of a profile with five message ids
		Set<Message> msgs = igDocumentSource.getProfile().getMessages().getChildren();
		List<String> msgIds = selRandMsgIds(msgs, 5);
		
		IGDocument pNew = null;
		try {
			pNew = igDocumentCreation.createIntegratedProfile(msgIds, "2.7", 45L);
		} catch (IGDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(5, pNew.getProfile().getMessages().getChildren().size());

		refIneteg.testMessagesVsSegments(pNew.getProfile());
		refIneteg.testFieldDatatypes(pNew.getProfile());
		refIneteg.testComponentDataypes(pNew.getProfile());
		
		// Captures the newly created profile.
		ObjectMapper mapper = new ObjectMapper();
		File OUTPUT_DIR = new File(System.getenv("IGAMT") + "/document");
		File outfile = new File(OUTPUT_DIR, "igdocument-" + "2.7.5" + ".json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(outfile, pNew);
	}
	
	@Test
	public void testProfileUpdate() throws IOException, ProfileException, IGDocumentException {
		// Collect version numbers
		assertEquals(7, igDocumentCreation.findHl7Versions().size());

		// Collect standard messages and message descriptions
		//There should be only one HL7STANDARD profile for each version
		for (String hl7Version : Arrays.asList("2.5.1", "2.7")){
			int found = igDocumentRepository.findStandardByVersion(hl7Version).size();
			assertEquals(1, found);
		}
		IGDocument igDocumentSource = igDocumentRepository.findStandardByVersion("2.7").get(0);
		assertEquals(193, igDocumentSource.getProfile().getMessages().getChildren().size());

		// Each description has 4 items: id, event, strucId, description
		List<String[]> msgDesc = igDocumentCreation.summary("2.7", new ArrayList<String>());
		assertEquals(4, msgDesc.get(0).length);
		
		// We're selecting our messages randomly here so we take care not to make two random calls 
		// and run the risk of duplication.
		Set<Message> msgs = igDocumentSource.getProfile().getMessages().getChildren();
		List<String> msgIds = selRandMsgIds(msgs, 8);
		String[] ss = msgIds.toArray(new String[8]);
		String[] ss5 = Arrays.copyOfRange(ss, 0, 5);
		String[] ss3 = Arrays.copyOfRange(ss, 5, 8);
		
		IGDocument pNew = igDocumentCreation.createIntegratedProfile(Arrays.asList(ss5), "2.7", 45L);
		assertEquals(5, pNew.getProfile().getMessages().getChildren().size());

		IGDocument pExNew = igDocumentCreation.updateIntegratedProfile(Arrays.asList(ss3), pNew);
		assertEquals(8, pExNew.getProfile().getMessages().getChildren().size());

		refIneteg.testMessagesVsSegments(pExNew.getProfile());
		refIneteg.testFieldDatatypes(pExNew.getProfile());
		refIneteg.testComponentDataypes(pExNew.getProfile());
		
		// Captures the newly updated IGDocument.
		ObjectMapper mapper = new ObjectMapper();
		File OUTPUT_DIR = new File(System.getenv("IGAMT") + "/document");
		File outfile = new File(OUTPUT_DIR, "igdocument-" + "2.7.8" + ".json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(outfile, pNew);
	}
	
	public List<String> selRandMsgIds(Set<Message> msgs, int selSize) {
		List<String> msgIds = new ArrayList<String>();
		int limit = msgs.size();
		Message[] msgsArr = msgs.toArray(new Message[limit]);
		for (int i = 0; i < selSize; i++) {
			msgIds.add(msgsArr[randInt(0, limit)].getId());
		}
		return msgIds;
	}
	
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min)) + min;

	    return randomNum;
	}

	@Configuration
	@EnableMongoRepositories(basePackages = "gov.nist.healthcare.tools")
	@ComponentScan(basePackages = "gov.nist.healthcare.tools")
	static class ProfileTestConfiguration extends AbstractMongoConfiguration {

		@Override
		public Mongo mongo() {
			// uses fongo for in-memory tests
			return new Fongo("igl").getMongo();
		}

		@Override
		@Bean
		public CustomConversions customConversions() {
			List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
			converterList.add(new FieldWriteConverter());
			converterList.add(new ComponentWriteConverter());
			converterList.add(new SegmentRefWriteConverter());
			converterList.add(new ProfileReadConverter());
			return new CustomConversions(converterList);
		}

		@Override
		protected String getDatabaseName() {
			return "igl";
		}

		@Override
		public String getMappingBasePackage() {
			return "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain";
		}
	}
}