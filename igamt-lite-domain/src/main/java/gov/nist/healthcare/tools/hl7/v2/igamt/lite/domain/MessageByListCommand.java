package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;

public class MessageByListCommand {

  private String hl7Version;

  private List<MessageEvents> messageEvents = new ArrayList<MessageEvents>();

  public String getHl7Version() {
    return hl7Version;
  }

  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
  }

  public List<MessageEvents> getMessageEvents() {
    return messageEvents;
  }

  public void setMessageEvents(List<MessageEvents> messageEvents) {
    this.messageEvents = messageEvents;
  }
}
