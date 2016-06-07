package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

public enum ConstraintType implements Serializable {
  ByName("ByName"), ByID("ByID");

  private String value;

  ConstraintType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
