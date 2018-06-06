package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers;

import java.io.Serializable;

public class XMLProfileRequestWrapper implements Serializable {

  public XMLProfileRequestWrapper() {
    super();
  }

  private static final long serialVersionUID = -8337269625916897011L;

  private String title;
  private String subTitle;
  private String profileXML;
  private String valuesetXML;
  private String constraintXML;


  public String getProfileXML() {
    return profileXML;
  }

  public void setProfileXML(String profileXML) {
    this.profileXML = profileXML;
  }

  public String getValuesetXML() {
    return valuesetXML;
  }

  public void setValuesetXML(String valuesetXML) {
    this.valuesetXML = valuesetXML;
  }

  public String getConstraintXML() {
    return constraintXML;
  }

  public void setConstraintXML(String constraintXML) {
    this.constraintXML = constraintXML;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }
}
