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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessagesRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ComponentWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.FieldWriteConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.SegmentRefWriteConverter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class IGDCreateTestData {

	Logger log = LoggerFactory.getLogger(IGDCreateTestData.class);
	
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
	
	@BeforeClass
	public static void setup() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = IGDCreateTestData.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min)) + min;

	    return randomNum;
	}
	
	@Test
	public void testProfileCreation() throws IOException, ProfileException {
		// Collect version numbers
		String hl7Version = "2.7";
		log.info("preload=" + igDocumentRepository.findPreloaded().size());
		log.info("standard=" + igDocumentRepository.findStandard().size());
		IGDocument igDocumentSource = igDocumentRepository.findStandardByVersion("2.7").get(0);
		Set<Message> msgs = igDocumentSource.getProfile().getMessages().getChildren();
		int limit = msgs.size();
		Message[] msgsArr = msgs.toArray(new Message[limit]);
		List<String> msgIds = new ArrayList<String>();
		for (int i = 0; i < limit; i++) {
			msgIds.add(msgsArr[randInt(0, limit)].getId());
		}
		ObjectMapper mapper = new ObjectMapper();
//		URL url = IGDCreateTestData.class.getResource("/igdocument/igdocument-2.7.json");
//		IGDocument pOld = new IGDocument();
//		InputStream is = url.openStream();
//		pOld = mapper.readValue(is, IGDocument.class);
//		List<String> msgIds = new ArrayList<String>();
//		Message[] msgs = pOld.getProfile().getMessages().getChildren().toArray(new Message[0]);
//		msgIds.add(msgs[4].getId());
//		msgIds.add(msgs[14].getId());
//		msgIds.add(msgs[24].getId());
		IGDocument pNew = null;
//		try {
//			pNew = igDocumentCreation.createIntegratedIGDocument(pOld, msgIds, "2.7", 45L);
//		} catch (IGDocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// Captures the newly created igdocument.
		File OUTPUT_DIR = new File("/Users/gcr1/Documents/nistWorkplace");
		File outfile = new File(OUTPUT_DIR, "igdocument-" + "2.7.5" + ".json");
		mapper.writerWithDefaultPrettyPrinter().writeValue(outfile, pNew);

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
