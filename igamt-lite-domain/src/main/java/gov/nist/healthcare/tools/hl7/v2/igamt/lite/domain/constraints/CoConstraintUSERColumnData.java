package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class CoConstraintUSERColumnData implements java.io.Serializable, Cloneable {
  /**
  * 
  */
  private static final long serialVersionUID = -1479682732180138569L;
  private String text;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public CoConstraintUSERColumnData clone() throws CloneNotSupportedException {
    CoConstraintUSERColumnData cloned = new CoConstraintUSERColumnData();
    cloned.setText(text);
    return cloned;
  }

}
