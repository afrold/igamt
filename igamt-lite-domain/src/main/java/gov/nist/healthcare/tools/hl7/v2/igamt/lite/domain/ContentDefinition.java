package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public enum ContentDefinition {
  Extensional, Intensional, Undefined;

  public String value() {
    return name();
  }

  public static ContentDefinition fromValue(String v) {
    return !"".equals(v) && v != null ? valueOf(v) : ContentDefinition.Intensional;
  }
}
