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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessagesRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ProfileCreationServiceTest {

	@Autowired
	ProfileRepository profileRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	ProfileService profileService;

	@Autowired
	ProfileCreationService profileCreation;

	@Autowired
	MessagesRepository messagesRepository;

	@Autowired
	MessageRepository messageRepository;

	@BeforeClass
	public static void setup() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileCreationServiceTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void testProfileStandardProfilePreloaded() {
		//FIXME for now mongo db is loaded with 2 profiles; ultimately version 2.5 until 2.8 should be preloaded
		assertEquals(9, profileCreation.findProfilesByHl7Versions().size());
	}
	
//	@Test
	public void testSummary() {
		String[] ss = {"ACK", "RCI", "QRY"};
		List<String[]> msgDesc = profileCreation.summary("2.7", new ArrayList<String>());
		List<String[]> msgDesc1 = profileCreation.summary("2.7", Arrays.asList(ss));
		assertEquals(msgDesc.size(), msgDesc1.size() + ss.length);
	}
	
//	@Test
	public void testProfileCreation() throws IOException, ProfileException {
		// Collect version numbers
		assertEquals(7, profileCreation.findHl7Versions().size());

		// Collect standard messages and message descriptions
		//There should be only one HL7STANDARD profile for each version
		for (String hl7Version : Arrays.asList("2.5.1", "2.7")){
			assertEquals(1, profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7Version).size());
		}
		Profile profileSource = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, "2.7").get(0);
		assertEquals(193, profileSource.getMessages().getChildren().size());

		// Each description has 4 items: id, event, strucId, description
		List<String[]> msgDesc = profileCreation.summary("2.7", new ArrayList<String>());
		assertEquals(4, msgDesc.get(0).length);
		
		// Creation of a profile with three message ids
		List<String> msgIds = new ArrayList<String>();
		msgIds.add(msgDesc.get(0)[0]);
		msgIds.add(msgDesc.get(1)[0]);
		msgIds.add(msgDesc.get(2)[0]);
		Profile pNew = profileCreation.createIntegratedProfile(msgIds, "2.7");
		assertEquals(3, pNew.getMessages().getChildren().size());
		File OUTPUT_DIR = new File(System.getenv("IGAMT") + "/profiles");
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File(OUTPUT_DIR + ".3", "profile-" + "2.7" + ".json"),
				pNew);
	}
	
	@Test
	public void testProfileUpdate() throws IOException, ProfileException {
		// Collect version numbers
		assertEquals(7, profileCreation.findHl7Versions().size());

		// Collect standard messages and message descriptions
		//There should be only one HL7STANDARD profile for each version
		for (String hl7Version : Arrays.asList("2.5.1", "2.7")){
			assertEquals(1, profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, hl7Version).size());
		}
		Profile profileSource = profileRepository.findByScopeAndMetaData_Hl7Version(ProfileScope.HL7STANDARD, "2.7").get(0);
		assertEquals(193, profileSource.getMessages().getChildren().size());

		// Each description has 4 items: id, event, strucId, description
		List<String[]> msgDesc = profileCreation.summary("2.7", new ArrayList<String>());
		assertEquals(4, msgDesc.get(0).length);
		
		// Creation of a profile with three message ids
		List<String> msgIds = new ArrayList<String>();
		msgIds.add(msgDesc.get(0)[0]);
		msgIds.add(msgDesc.get(1)[0]);
		msgIds.add(msgDesc.get(2)[0]);
		Profile pNew = profileCreation.createIntegratedProfile(msgIds, "2.7");
		assertEquals(3, pNew.getMessages().getChildren().size());
		List<String> msgIds1 = new ArrayList<String>();
		msgIds1.add(msgDesc.get(3)[0]);
		msgIds1.add(msgDesc.get(4)[0]);
		msgIds1.add(msgDesc.get(5)[0]);
		Profile pExNew = profileCreation.updateIntegratedProfile(msgIds, pNew);
		assertEquals(6, pNew.getMessages().getChildren().size());
		File OUTPUT_DIR = new File(System.getenv("IGAMT") + "/profiles");
		ObjectMapper mapper = new ObjectMapper();
		mapper.writerWithDefaultPrettyPrinter().writeValue(new File(OUTPUT_DIR + ".3.3", "profile-" + "2.7" + ".json"),
				pNew);
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.data.mongodb.config.AbstractMongoConfiguration#
		 * getDatabaseName()
		 */
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
