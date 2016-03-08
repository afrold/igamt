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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceContext.class })
public class MessageEventFactoryTest {

	Logger log = LoggerFactory.getLogger(MessageEventFactoryTest.class);

	@Autowired
	IGDocumentRepository igDocumentRepository;

	// @Test
	public void testFixUnderscore() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		MessageEventFactory sut = new MessageEventFactory(igd);
		assertEquals("ACK", sut.fixUnderscore("ACK_"));
		assertEquals("ACK", sut.fixUnderscore("ACK"));
	}

	// @Test
	public void testCreateMessageEvents() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		List<Message> msgs = new ArrayList<Message>();
		Collections.addAll(msgs, igd.getProfile().getMessages().getChildren()
				.toArray(new Message[igd.getProfile().getMessages().getChildren().size()]));
		MessageEventFactory sut = new MessageEventFactory(igd);
		List<MessageEvents> mes = sut.createMessageEvents(msgs);
		assertNotNull(mes);
		assertEquals(msgs.size(), mes.size());
	}

//	@Test
	public void testFindEvents() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		Set<Message> msgs = igd.getProfile().getMessages().getChildren();
		for (Message msg : msgs) {
			String structID = msg.getStructID();
			MessageEventFactory sut = new MessageEventFactory(igd);
			Set<String> events = sut.findEvents(structID);
			assertNotNull(events);
			assertTrue(events.size() > 0);
		}
	}

	@Test
	public void testFindEvents4ACK() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		String structID = "ACK_";
		MessageEventFactory sut = new MessageEventFactory(igd);
		Set<String> events = sut.findEvents(structID);
		for (String event : events) {
			assertEquals("ACK", event);
		}
		assertNotNull(events);
		assertTrue(events.size() > 0);
	}

	// @Test
	public void testGet0354Table() {
		List<IGDocument> igds = igDocumentRepository
				.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD, "2.5.1");
		IGDocument igd = igds.get(0);
		MessageEventFactory sut = new MessageEventFactory(igd);
		Table tab = sut.get0354Table();
		assertEquals("0354", tab.getBindingIdentifier());
	}

}
