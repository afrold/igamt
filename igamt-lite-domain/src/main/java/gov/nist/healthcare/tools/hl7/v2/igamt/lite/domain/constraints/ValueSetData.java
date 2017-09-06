package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBindingStrength;

public class ValueSetData implements java.io.Serializable, Cloneable {

  /**
   * 
   */
  private static final long serialVersionUID = 1652369528819677954L;

  private String tableId;
  private ValueSetBindingStrength bindingStrength;
  private String bindingLocation;

  public ValueSetData() {
    super();
  }

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  public ValueSetBindingStrength getBindingStrength() {
    if (bindingStrength == null)
      return ValueSetBindingStrength.R;
    return bindingStrength;
  }

  public void setBindingStrength(ValueSetBindingStrength bindingStrength) {
    this.bindingStrength = bindingStrength;
  }

  public String getBindingLocation() {
    return bindingLocation;
  }

  public void setBindingLocation(String bindingLocation) {
    this.bindingLocation = bindingLocation;
  }

  @Override
  public ValueSetData clone() throws CloneNotSupportedException {
    ValueSetData cloned = new ValueSetData();
    cloned.setBindingLocation(bindingLocation);
    cloned.setBindingStrength(bindingStrength);
    cloned.setTableId(tableId);
    return cloned;
  }

}
