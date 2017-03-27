package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;


@Deprecated
public class CoConstraintsColumn implements Comparable<CoConstraintsColumn> {

  private Field field;
  private String constraintType;
  private int columnPosition;

  public Field getField() {
    return field;
  }
  public void setField(Field field) {
    this.field = field;
  }
  public String getConstraintType() {
    return constraintType;
  }
  public void setConstraintType(String constraintType) {
    this.constraintType = constraintType;
  }
  public int getColumnPosition() {
    return columnPosition;
  }
  public void setColumnPosition(int columnPosition) {
    this.columnPosition = columnPosition;
  }
  @Override
  public int compareTo(CoConstraintsColumn o) {
    if (this.getColumnPosition() == o.getColumnPosition()){
      return 0;
    } else {
      return this.getColumnPosition() > o.getColumnPosition() ? 1 : -1;
    }
    
  }


}
