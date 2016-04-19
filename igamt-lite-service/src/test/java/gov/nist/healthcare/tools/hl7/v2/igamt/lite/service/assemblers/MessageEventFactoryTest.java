package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.assemblers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageEventFactory;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceContext.class })
public class MessageEventFactoryTest {

	Logger log = LoggerFactory.getLogger(MessageEventFactoryTest.class);

	@Autowired
	IGDocumentRepository igDocumentRepository;

	@Autowired
	TableRepository tableRepository;

	@Test
	public void testFixUnderscore() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		MessageEventFactory sut = new MessageEventFactory(tableRepository);
//		assertEquals("ACK", sut.fixUnderscore("ACK_"));
//		assertEquals("ACK", sut.fixUnderscore("ACK"));
	}

	@Test
	public void testCreateMessageEvents() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		Messages msgs = new Messages();
		Collections.addAll(msgs.getChildren(), igd.getProfile().getMessages().getChildren()
				.toArray(new Message[igd.getProfile().getMessages().getChildren().size()]));
		MessageEventFactory sut = new MessageEventFactory(tableRepository);
		List<MessageEvents> mes = sut.createMessageEvents(msgs);
		assertNotNull(mes);
		assertEquals(msgs.getChildren().size(), mes.size());
	}

	@Test
	public void testFindEvents() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		Set<Message> msgs = igd.getProfile().getMessages().getChildren();
		for (Message msg : msgs) {
			String structID = msg.getStructID();
			MessageEventFactory sut = new MessageEventFactory(tableRepository);
			Set<String> events = sut.findEvents(structID);
			assertNotNull(events);
			assertTrue(events.size() > 0);
		}
	}

//	@Test
	public void testFindEvents4ACK() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		String structID = "ACK_";
		MessageEventFactory sut = new MessageEventFactory(tableRepository);
		Set<String> events = sut.findEvents(structID);
		for (String event : events) {
			assertEquals("ACK", event);
		}
		assertNotNull(events);
		assertTrue(events.size() > 0);
	}

	@Test
	public void testGet0354Table() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		MessageEventFactory sut = new MessageEventFactory(tableRepository);
		Table tab = sut.get0354Table();
		assertEquals("0354", tab.getBindingIdentifier());
	}
}
