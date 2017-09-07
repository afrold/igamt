package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class ValueData implements java.io.Serializable, Cloneable {

  /**
  * 
  */
  private static final long serialVersionUID = -5718346689779507994L;
  private String value;
  private String bindingLocation;

  public ValueData() {
    super();
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getBindingLocation() {
    return bindingLocation;
  }

  public void setBindingLocation(String bindingLocation) {
    this.bindingLocation = bindingLocation;
  }

  @Override
  public ValueData clone() throws CloneNotSupportedException {
    ValueData cloned = new ValueData();
    cloned.setBindingLocation(bindingLocation);
    cloned.setValue(value);
    return cloned;
  }
}
