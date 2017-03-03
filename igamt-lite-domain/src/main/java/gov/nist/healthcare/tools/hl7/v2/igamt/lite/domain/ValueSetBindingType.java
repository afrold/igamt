package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum ValueSetBindingType {

  M, S, D;

  public String value() {
    return name();
  }
}
