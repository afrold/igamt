package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

public class ConstantDisplay {
  protected String value;
  protected DisplayLevel level;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public DisplayLevel getLevel() {
    return level;
  }

  public void setLevel(DisplayLevel level) {
    this.level = level;
  }


}
