package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;


// @Embeddable
public class Standard implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  // TODO: WOO,can this be null?
  // //@NotNull
  // //@Column(nullable = false)
  // @Column(name = "STANDARDID")
  private String standardId;

  // //@NotNull
  // //@Column(nullable = false)
  // @Column(name = "STANDARD_VERSION")
  private String standardVersion;

  // @Column(name = "STANDARD_DATE")
  private String standardDate;
  // @Column(name = "STANDARD_URL")
  private String standardURL;
  // @Column(name = "STANDARD_DESC")
  private String standardDescription;

  public String getStandardId() {
    return standardId;
  }

  public void setStandardId(String standardId) {
    this.standardId = standardId;
  }

  public String getStandardVersion() {
    return standardVersion;
  }

  public void setStandardVersion(String standardVersion) {
    this.standardVersion = standardVersion;
  }

  public String getStandardDate() {
    return standardDate;
  }

  public void setStandardDate(String standardDate) {
    this.standardDate = standardDate;
  }

  public String getStandardURL() {
    return standardURL;
  }

  public void setStandardURL(String standardURL) {
    this.standardURL = standardURL;
  }

  public String getStandardDescription() {
    return standardDescription;
  }

  public void setStandardDescription(String standardDescription) {
    this.standardDescription = standardDescription;
  }

  @Override
  public String toString() {
    return "Standard [standardId=" + standardId + ", standardVersion=" + standardVersion
        + ", standardDate=" + standardDate + ", standardURL=" + standardURL
        + ", standardDescription=" + standardDescription + "]";
  }

  @Override
  public Standard clone() throws CloneNotSupportedException {
    Standard clonedStandard = (Standard) super.clone();
    return clonedStandard;
  }

}
