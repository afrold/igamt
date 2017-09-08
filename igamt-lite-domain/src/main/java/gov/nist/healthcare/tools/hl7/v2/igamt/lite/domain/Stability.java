package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum Stability {
  Static, Dynamic, Undefined;

  public String value() {
    return name();
  }

  public static Stability fromValue(String v) {

    return !"".equals(v) && v != null ? valueOf(v) : Stability.Dynamic;
  }
}
