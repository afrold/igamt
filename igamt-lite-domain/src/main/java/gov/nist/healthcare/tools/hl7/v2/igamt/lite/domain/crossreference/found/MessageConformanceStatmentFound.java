package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;

public class MessageConformanceStatmentFound {
  private MessageFound messageFound;
  private ConformanceStatement conformanceStatement;

  public MessageFound getMessageFound() {
    return messageFound;
  }

  public void setMessageFound(MessageFound messageFound) {
    this.messageFound = messageFound;
  }

  public ConformanceStatement getConformanceStatement() {
    return conformanceStatement;
  }

  public void setConformanceStatement(ConformanceStatement conformanceStatement) {
    this.conformanceStatement = conformanceStatement;
  }


}
