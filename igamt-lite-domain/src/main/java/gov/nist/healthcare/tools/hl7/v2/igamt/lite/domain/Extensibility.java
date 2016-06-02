package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum Extensibility {
  Open, Closed;

  public String value() {
    return name();
  }

  public static Extensibility fromValue(String v) {
    return !"".equals(v) && v != null ? valueOf(v) : Extensibility.Open;
  }
}
