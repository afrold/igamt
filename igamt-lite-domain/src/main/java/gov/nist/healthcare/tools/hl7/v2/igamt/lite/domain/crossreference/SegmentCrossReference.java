package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageFound;

public class SegmentCrossReference {

  private List<MessageFound> messageFounds;
  private boolean empty;

  public List<MessageFound> getMessageFounds() {
    return messageFounds;
  }

  public void setMessageFounds(List<MessageFound> messageFounds) {
    this.messageFounds = messageFounds;
  }

  /**
   * @return the empty
   */
  public boolean isEmpty() {
    return empty;
  }

  /**
   * @param empty the empty to set
   */
  public void setEmpty() {
    this.empty = messageFounds.isEmpty();
  }
}
