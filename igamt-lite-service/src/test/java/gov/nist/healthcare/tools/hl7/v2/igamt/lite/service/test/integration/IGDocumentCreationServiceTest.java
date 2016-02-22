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
import static org.junit.Assert.assertNotNull;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessagesRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.assemblers.MessageEvents;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
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
	
	@Test
	public void testFindHl7VersionsTest() {
		List<String> list = igDocumentCreation.findHl7Versions();
		assertNotNull(list);
	}

	//	@Test
	public void testProfileStandardProfilePreloaded() {
		//FIXME for now mongo db is loaded with 2 profiles; ultimately version 2.5 until 2.8 should be preloaded
		assertEquals(9, igDocumentCreation.findIGDocumentsByHl7Versions().size());
	}

	@Test
	public void testSummary() {
		String[] arr = {"565f3ab6d4c6e52cfd43fc40", "565f3ab6d4c6e52cfd43f71a", "565f3ab6d4c6e52cfd43f5fa", "565f3ab6d4c6e52cfd43fd8d", "565f3ab6d4c6e52cfd43fa1f"};
		List<MessageEvents> msgsAll = igDocumentCreation.summary("2.5.1", new ArrayList<String>());
		List<MessageEvents> msgsSansArr = igDocumentCreation.summary("2.5.1", Arrays.asList(arr));
		assertNotNull(msgsAll);
		assertTrue(msgsAll.size() > msgsSansArr.size());
	}

//	@Test
	public void testigDocumentCreation() throws IOException, ProfileException {
		// Collect version numbers
		assertEquals(7, igDocumentCreation.findHl7Versions().size());

		String[] hl7Versions = {"2.5.1", "2.7"};
		Long accountId = 45L;

		// Collect standard messages and message descriptions
		// There should be only one HL7STANDARD profile for each version
		for (String hl7Version : Arrays.asList(hl7Versions)) {
			int found = igDocumentRepository.findStandardByVersion(hl7Version).size();
			//			assertEquals(1, found);
		}
		IGDocument igDocumentSource = igDocumentRepository.findStandardByVersion(hl7Versions[1]).get(0);
		assertEquals(193, igDocumentSource.getProfile().getMessages().getChildren().size());

		// Creation of a profile with five message ids
		Set<Message> msgs = igDocumentSource.getProfile().getMessages().getChildren();
		List<String> msgIds = selRandMsgIds(msgs, 5);

		IGDocument pNew = null;
		try {
			pNew = igDocumentCreation.createIntegratedIGDocument(msgIds, hl7Versions[1], accountId);
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

//	@Test
	public void testProfileUpdate() throws IOException, ProfileException, IGDocumentException {
		// Collect version numbers
		assertEquals(7, igDocumentCreation.findHl7Versions().size());

		String[] hl7Versions = {"2.5.1", "2.7"};
		Long accountId = 45L;

		// Collect standard messages and message descriptions
		//There should be only one HL7STANDARD profile for each version
		for (String hl7Version : Arrays.asList(hl7Versions)){
			int found = igDocumentRepository.findStandardByVersion(hl7Version).size();
			assertEquals(1, found);
		}
		IGDocument igDocumentSource = igDocumentRepository.findStandardByVersion(hl7Versions[1]).get(0);
		assertEquals(193, igDocumentSource.getProfile().getMessages().getChildren().size());

		// We're selecting our messages randomly here so we take care not to make two random calls 
		// and run the risk of duplication.
		Set<Message> msgs = igDocumentSource.getProfile().getMessages().getChildren();
		List<String> msgIds = selRandMsgIds(msgs, 8);
		String[] ss = msgIds.toArray(new String[8]);
		String[] ss5 = Arrays.copyOfRange(ss, 0, 5);
		String[] ss3 = Arrays.copyOfRange(ss, 5, 8);

		IGDocument pNew = igDocumentCreation.createIntegratedIGDocument(Arrays.asList(ss5), hl7Versions[1], accountId);
		assertEquals(5, pNew.getProfile().getMessages().getChildren().size());

		IGDocument pExNew = igDocumentCreation.updateIntegratedIGDocument(Arrays.asList(ss3), pNew);
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
}