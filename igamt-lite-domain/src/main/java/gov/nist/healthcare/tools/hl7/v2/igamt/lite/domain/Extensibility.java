package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum Extensibility {
  Open, Closed, Undefined;

  public String value() {
    return name();
  }

  public static Extensibility fromValue(String v) {
    if (v.equals("Not Defined")) {
      return Extensibility.Undefined;
    }
    return !"".equals(v) && v != null ? valueOf(v) : Extensibility.Open;
  }
}
