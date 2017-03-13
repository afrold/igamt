package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum ValueSetBindingStrength {

  R, S, U;

  public String value() {
    return name();
  }
}
