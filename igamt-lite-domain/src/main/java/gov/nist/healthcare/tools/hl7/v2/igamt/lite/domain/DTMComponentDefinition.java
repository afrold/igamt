package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class DTMComponentDefinition {

  private Integer position;
  private String name;
  private String description;
  private Usage usage;
  private DTMPredicate dtmPredicate;

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Usage getUsage() {
    return usage;
  }

  public void setUsage(Usage usage) {
    this.usage = usage;
  }

  public DTMPredicate getDtmPredicate() {
    return dtmPredicate;
  }

  public void setDtmPredicate(DTMPredicate dtmPredicate) {
    this.dtmPredicate = dtmPredicate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


}
