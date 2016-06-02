package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum SchemaVersion {

  V1_0("1.0"), V1_5("1.5"), V2_0("2.0"), V2_5("2.5");

  private String value;

  private SchemaVersion(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }



}
