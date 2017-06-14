package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class DTMPredicate {

  private Usage trueUsage;
  private Usage falseUsage;
  private DTMComponentDefinition target;
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

  public String getPredicateDescription() {
    if (target == null) {
      return "Need target information.";
    } else if (this.verb == null) {
      return "Need verb information.";
    } else if (this.value == null) {
      return "If " + this.target.getName() + "(" + this.target.getDescription() + ")" + " "
          + this.verb + ".";
    } else {
      return "If " + this.target.getDescription() + "(" + this.target.getDescription() + ")" + " "
          + this.verb + " '" + this.value + "'.";
    }
  }

  public DTMComponentDefinition getTarget() {
    return target;
  }

  public void setTarget(DTMComponentDefinition target) {
    this.target = target;
  }
}
