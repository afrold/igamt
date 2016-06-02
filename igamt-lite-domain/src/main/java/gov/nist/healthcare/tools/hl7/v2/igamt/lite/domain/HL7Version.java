package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum HL7Version {

  V2_0("2.0"), V2_1("2.1"), V2_2("2.2"), V2_3("2.3"), V2_3_1("2.3.1"), V2_4("2.4"), V2_5("2.5"), V2_5_1(
      "2.5.1"), V2_6("2.6"), V2_7("2.7"), V2_8("2.8");

  private String value;

  private HL7Version(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
