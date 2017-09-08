package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

public class CoConstraintTHENColumnData implements java.io.Serializable, Cloneable {
  /**
   * 
   */
  private static final long serialVersionUID = 560352192578381760L;
  private ValueData valueData = new ValueData();
  private List<ValueSetData> valueSets = new ArrayList<ValueSetData>();
  private String datatypeId;

  public CoConstraintTHENColumnData() {
    super();
  }

  public ValueData getValueData() {
    return valueData;
  }

  public void setValue(ValueData valueData) {
    this.valueData = valueData;
  }

  public List<ValueSetData> getValueSets() {
    return valueSets;
  }

  public void setValueSets(List<ValueSetData> valueSets) {
    this.valueSets = valueSets;
  }

  public String getDatatypeId() {
    return datatypeId;
  }

  public void setDatatypeId(String datatypeId) {
    this.datatypeId = datatypeId;
  }

  @Override
  public CoConstraintTHENColumnData clone() throws CloneNotSupportedException {
    CoConstraintTHENColumnData cloned = new CoConstraintTHENColumnData();
    cloned.setDatatypeId(datatypeId);
    if(this.valueData != null) cloned.setValue(valueData.clone());
    cloned.setValueSets(new ArrayList<ValueSetData>());
    for (ValueSetData data : valueSets) {
      if(data != null) cloned.getValueSets().add(data.clone());
    }
    return cloned;
  }

}
