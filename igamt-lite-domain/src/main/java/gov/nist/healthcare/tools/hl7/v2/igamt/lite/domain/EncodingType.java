package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum EncodingType {

  XML, ER7;

  public String value() {
    return name();
  }

  public static EncodingType fromValue(String v) {
    return valueOf(v);
  }

}
