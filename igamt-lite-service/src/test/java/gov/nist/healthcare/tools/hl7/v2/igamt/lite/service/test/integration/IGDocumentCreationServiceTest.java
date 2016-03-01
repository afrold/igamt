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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.assemblers.Event;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.assemblers.MessageEvents;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceContext.class })
public class IGDocumentCreationServiceTest {

	@Autowired
	IGDocumentRepository igDocumentRepository;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Autowired
	IGDocumentService igDocumentService;

	@Autowired
	IGDocumentCreationService igDocumentCreation;

	static ProfileCreationReferentialIntegrityTest refIneteg;

	final String hl7Version = "2.5.1";
	final Long accountId = 45L;

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
		assertEquals(3, list.size());
	}

	// @Test
	public void testProfileStandardProfilePreloaded() {
		// FIXME for now mongo db is loaded with 2 profiles; ultimately version
		// 2.5 until 2.8 should be preloaded
		assertEquals(9, igDocumentCreation.findIGDocumentsByHl7Versions().size());
	}

	@Test
	public void testSummary() {
		List<MessageEvents> msgsAll = igDocumentCreation.summary(hl7Version, new ArrayList<String>());
		List<MessageEvents> msgEvts = selRandMsgEvts(msgsAll, 12);
		List<MessageEvents> msgEvts5 = msgEvts.subList(0, 5);
		List<String> msgIds = new ArrayList<String>();
		for (MessageEvents mes : msgEvts5) {
			msgIds.add(mes.getId());
		}
		List<MessageEvents> msgsSansArr = igDocumentCreation.summary(hl7Version, msgIds);
		assertNotNull(msgsAll);
		assertEquals(msgsAll.size() - msgIds.size(), msgsSansArr.size());
		for (MessageEvents mes : msgsSansArr) {
			assertFalse(msgIds.contains(mes.getId()));
		}
	}

	@Test
	public void testIGDocumentCreation() throws IOException, ProfileException {

		// Creation of a profile with five message ids
		List<MessageEvents> msgsAll = igDocumentCreation.summary("2.5.1", new ArrayList<String>());
		List<MessageEvents> msgEvts = selRandMsgEvts(msgsAll, 12);
		List<MessageEvents> msgEvts5 = msgEvts.subList(0, 5);

		IGDocument pNew = null;
		try {
			pNew = igDocumentCreation.createIntegratedIGDocument(msgEvts5, hl7Version, accountId);
		} catch (IGDocumentException e) {
			e.printStackTrace();
		}
		assertEquals(5, pNew.getProfile().getMessages().getChildren().size());

		refIneteg.testMessagesVsSegments(pNew.getProfile());
		refIneteg.testFieldDatatypes(pNew.getProfile());
		refIneteg.testComponentDataypes(pNew.getProfile());

		// TODO move this elsewhere.
		// Captures the newly created profile.
		// ObjectMapper mapper = new ObjectMapper();
		// File OUTPUT_DIR = new File(System.getenv("IGAMT") + "/IGDocuments");
		// File outfile = new File(OUTPUT_DIR, "igdocument-" + "2.7.5" +
		// ".json");
		// mapper.writerWithDefaultPrettyPrinter().writeValue(outfile, pNew);
	}

	@Test
	public void testIGDocumentUpdate() throws IOException, ProfileException {

		// Creation of a profile with five message ids
		List<MessageEvents> msgsAll = igDocumentCreation.summary("2.5.1", new ArrayList<String>());
		List<MessageEvents> msgEvts = selRandMsgEvts(msgsAll, 12);
		List<MessageEvents> msgEvts7 = msgEvts.subList(0, 7);
		List<MessageEvents> msgEvts5 = msgEvts7.subList(0, 5);
		assertEquals(5, msgEvts5.size());
		List<MessageEvents> msgEvts2 = msgEvts7.subList(5, 7);
		assertEquals(2, msgEvts2.size());
		IGDocument pNew = null;
		IGDocument pNewNew = null;
		try {
			pNew = igDocumentCreation.createIntegratedIGDocument(msgEvts5, hl7Version, accountId);
			assertEquals(5, pNew.getProfile().getMessages().getChildren().size());
			pNewNew = igDocumentCreation.updateIntegratedIGDocument(msgEvts2, pNew);
			assertEquals(7, pNewNew.getProfile().getMessages().getChildren().size());
			refIneteg.testMessagesVsSegments(pNewNew.getProfile());
			refIneteg.testFieldDatatypes(pNewNew.getProfile());
			refIneteg.testComponentDataypes(pNewNew.getProfile());
		} catch (IGDocumentException e) {
			e.printStackTrace();
		}
	}

	// Create and update are made with MessageEvents that have only one event, 
	// which is selected by the user in the UI.  This function returns the kind of 
	// terse objects the UI submits.
	public List<MessageEvents> selRandMsgEvts(List<MessageEvents> msgs, int selSize) {
		List<MessageEvents> msgEvts = new ArrayList<MessageEvents>();
		int limit = msgs.size();
		for (int i = 0; i < selSize; i++) {
			MessageEvents mesSource = msgs.get(randInt(0, limit));
			Iterator<Event> itr = mesSource.getChildren().iterator();
			if (itr.hasNext()) {
				String evt = itr.next().getName();
				Set<String> events = new TreeSet<String>();
				events.add(evt);
				MessageEvents mesTarget = new MessageEvents(mesSource.getId(), null, events, null);

				msgEvts.add(mesTarget);
			}
		}
		return msgEvts;
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