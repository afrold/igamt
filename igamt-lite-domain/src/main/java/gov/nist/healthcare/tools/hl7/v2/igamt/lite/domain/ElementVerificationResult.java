package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ElementVerificationResult {

  String attName;

  String attValue;

  String result;

  public ElementVerificationResult(String attName, String attValue, String result) {
    super();
    this.attName = attName;
    this.attValue = attValue;
    this.result = result;
  }

  public String getAttName() {
    return attName;
  }

  public void setAttName(String attName) {
    this.attName = attName;
  }

  public String getAttValue() {
    return attValue;
  }

  public void setAttValue(String attValue) {
    this.attValue = attValue;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  public String toString() {
    return "ElementVerificationResult [attName=" + attName + ", attValue=" + attValue + ", result="
        + result + "]";
  }

}
