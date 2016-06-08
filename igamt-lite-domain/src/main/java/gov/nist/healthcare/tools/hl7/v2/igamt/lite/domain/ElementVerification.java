package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class ElementVerification {
  String id;

  String type;

  List<ElementVerificationResult> elementVerifications;

  List<ElementVerification> childrenVerification;



  public ElementVerification(String id, String type) {
    super();
    this.id = id;
    this.type = type;
    this.elementVerifications = new ArrayList<ElementVerificationResult>();
    this.childrenVerification = new ArrayList<ElementVerification>();
  }



  public ElementVerification(String id, String type,
      List<ElementVerificationResult> elementVerifications,
      List<ElementVerification> childrenVerification) {
    super();
    this.id = id;
    this.type = type;
    this.elementVerifications = elementVerifications;
    this.childrenVerification = childrenVerification;
  }



  public String getId() {
    return id;
  }



  public void setId(String id) {
    this.id = id;
  }



  public String getType() {
    return type;
  }



  public void setType(String type) {
    this.type = type;
  }



  public List<ElementVerificationResult> getElementVerifications() {
    return elementVerifications;
  }



  public void setElementVerifications(List<ElementVerificationResult> elementVerifications) {
    this.elementVerifications = elementVerifications;
  }



  public List<ElementVerification> getChildrenVerification() {
    return childrenVerification;
  }



  public void setChildrenVerification(List<ElementVerification> childrenVerification) {
    this.childrenVerification = childrenVerification;
  }

  public void addElementVerifications(ElementVerificationResult elementVerifications) {
    if (this.elementVerifications != null) {
      this.elementVerifications.add(elementVerifications);
    }
  }

  public void addChildrenVerification(ElementVerification childrenVerification) {
    if (this.childrenVerification != null) {
      this.childrenVerification.add(childrenVerification);
    }
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ElementVerification [id=" + id + ", type=" + type);
    sb.append(", elementVerifications=[");
    for (ElementVerificationResult evr : elementVerifications) {
      sb.append(evr.toString());
      sb.append("\t");
    }
    sb.append("]");
    sb.append(", childrenVerification=[");
    for (ElementVerification evr : childrenVerification) {
      sb.append(evr.toString());
      sb.append("\t");
    }
    sb.append("]");
    // return "ElementVerification [id=" + id + ", type=" + type
    // + ", elementVerifications=" + elementVerifications
    // + ", childrenVerification=" + childrenVerification + "]";
    return sb.toString();
  }



}
