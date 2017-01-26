package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageEventFactory;

@Service
public class MessageEventFactoryImpl implements MessageEventFactory {

  private static Logger log = LoggerFactory.getLogger(MessageEventFactory.class);

  @Autowired
  private TableRepository tableRepository;

  public TableRepository getTableRepository() {
    return tableRepository;
  }

  public void setTableRepository(TableRepository tableRepository) {
    this.tableRepository = tableRepository;
  }

  @Override
  public List<MessageEvents> createMessageEvents(Messages msgs, String hl7Version) {

    Table tableO354 = getTable0354(hl7Version);
    List<MessageEvents> list = new ArrayList<MessageEvents>();
    if (tableO354 != null) {
      for (Message msg : msgs.getChildren()) {
        String id = msg.getId();
        String structID = msg.getStructID();
        Set<String> events = findEvents(structID, tableO354);
        String description = msg.getDescription();
        list.add(new MessageEvents(id, structID, events, description));
      }
    }
    return list;
  }

  public Set<String> findEvents(String structID, Table tableO354) {
    Set<String> events = new HashSet<String>();
    String structID1 = fixUnderscore(structID);
    Code code = tableO354.findOneCodeByValue(structID1);
    if (code != null) {
      String label = code.getLabel();
      label = label == null ? "Varies" : label; // Handle ACK
      String[] ss = label.split(",");
      Collections.addAll(events, ss);
    } else {
      log.error("No code found for structID=" + structID1);
    }

    return events;
  }

  public String fixUnderscore(String structID) {
    if (structID.endsWith("_")) {
      int pos = structID.length();
      return structID.substring(0, pos - 1);
    } else {
      return structID;
    }
  }

  public Table getTable0354(String hl7Version) {
    return tableRepository.findByBindingIdentifierAndHL7VersionAndScope("0354", hl7Version,
        SCOPE.HL7STANDARD);
  }
}
