package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.messageevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.Event;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageEventFactory;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContext.class})
public class MessageEventFactoryTest {

  @Autowired
  IGDocumentRepository igDocumentRepository;

  @Autowired
  TableRepository tableRepository;


  @Test
  public void testSortOrder() {
    List<IGDocument> igds =
        igDocumentRepository.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD,
            "2.5.1");
    IGDocument igd = igds.get(0);
    Messages msgs = igd.getProfile().getMessages();
    MessageEventFactory sut0 = new MessageEventFactory(tableRepository);
    List<MessageEvents> mes0 = sut0.createMessageEvents(msgs);
    MessageEventFactory sut1 = new MessageEventFactory(tableRepository);
    List<MessageEvents> mes1 = sut1.createMessageEvents(msgs);
    assertNotNull(mes0);
    assertNotNull(mes1);
    assertEquals(mes0.size(), mes1.size());
    assertEquals(mes0.get(0).getId(), mes1.get(0).getId());
    assertEquals(mes0.get(1).getId(), mes1.get(1).getId());
    assertEquals(mes0.get(0).getName(), mes1.get(0).getName());
    assertEquals(mes0.get(1).getName(), mes1.get(1).getName());
    assertEquals(-1, mes0.get(0).getName().compareTo(mes0.get(1).getName()));
    Set<Event> evts = mes0.get(0).getChildren();

    boolean firstTime = true;
    Event thisEvt = null;
    Event thatEvt = null;
    for (Event evt : evts) {
      if (firstTime) {
        thisEvt = evt;
        firstTime = false;
      } else {
        thatEvt = evt;
        assertEquals(-1, thisEvt.getName().compareTo(thatEvt.getName()));
        thisEvt = thatEvt;
      }
    }
  }

  @Test
  public void testCreateMessageEvents() {
    List<IGDocument> igds =
        igDocumentRepository.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD,
            "2.5.1");
    IGDocument igd = igds.get(0);
    Messages msgs = igd.getProfile().getMessages();
    MessageEventFactory sut = new MessageEventFactory(tableRepository);
    List<MessageEvents> mes = sut.createMessageEvents(msgs);
    assertNotNull(mes);
    assertEquals(msgs.getChildren().size(), mes.size());
  }

  @Test
  public void testFindEvents() {
    List<IGDocument> igds =
        igDocumentRepository.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD,
            "2.5.1");
    IGDocument igd = igds.get(0);
    Set<Message> msgs = igd.getProfile().getMessages().getChildren();
    Message msg = msgs.iterator().next();
    String structID = msg.getStructID();
    MessageEventFactory sut = new MessageEventFactory(tableRepository);
    Set<String> events = sut.findEvents(structID);
    assertNotNull(events);
    assertTrue(events.size() > 0);
  }

  @Test
  public void testGet0354Table() {
    List<IGDocument> igds =
        igDocumentRepository.findByScopeAndProfile_MetaData_Hl7Version(IGDocumentScope.HL7STANDARD,
            "2.5.1");
    IGDocument igd = igds.get(0);
    MessageEventFactory sut = new MessageEventFactory(tableRepository);
    Table tab = sut.get0354Table();
    assertEquals("0354", tab.getBindingIdentifier());
  }

}
