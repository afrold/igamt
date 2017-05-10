package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class DTMPredicate {

  private Usage trueUsage;
  private Usage falseUsage;
  private String targetName;
  private String verb;
  private String value;

  public Usage getTrueUsage() {
    return trueUsage;
  }

  public void setTrueUsage(Usage trueUsage) {
    this.trueUsage = trueUsage;
  }

  public Usage getFalseUsage() {
    return falseUsage;
  }

  public void setFalseUsage(Usage falseUsage) {
    this.falseUsage = falseUsage;
  }

  public String getTargetName() {
    return targetName;
  }

  public void setTargetName(String targetName) {
    this.targetName = targetName;
  }

  public String getVerb() {
    return verb;
  }

  public void setVerb(String verb) {
    this.verb = verb;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
