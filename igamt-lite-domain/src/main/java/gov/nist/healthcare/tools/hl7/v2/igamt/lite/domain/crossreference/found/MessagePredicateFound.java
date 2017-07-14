package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class MessagePredicateFound {
  private MessageFound messageFound;
  private Predicate predicate;

  public MessageFound getMessageFound() {
    return messageFound;
  }

  public void setMessageFound(MessageFound messageFound) {
    this.messageFound = messageFound;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;
  }


}
