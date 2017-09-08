package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class CoConstraintIFColumnData implements java.io.Serializable, Cloneable {

  /**
  * 
  */
  private static final long serialVersionUID = -6741327235918302466L;
  private ValueData valueData;

  public CoConstraintIFColumnData() {
    super();
  }

  public ValueData getValueData() {
    return valueData;
  }

  public void setValueData(ValueData valueData) {
    this.valueData = valueData;
  }
  
  @Override
  public CoConstraintIFColumnData clone() throws CloneNotSupportedException {
    CoConstraintIFColumnData cloned = new CoConstraintIFColumnData();
    if(valueData != null) cloned.setValueData(valueData.clone());
    return cloned;
  }
}
