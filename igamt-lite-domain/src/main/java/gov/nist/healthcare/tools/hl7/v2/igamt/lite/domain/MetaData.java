package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class MetaData implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public MetaData() {
    super();
  }

  private String coverPicture;

  private String name;

  private String orgName;

  private String version;

  private String hl7Version;

  @Deprecated
  private String date;


  private String description;


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  private String ext;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getHl7Version() {
    return hl7Version;
  }

  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
  }


  public String getDate() {
    return date;
  }


  public void setDate(String date) {
    this.date = date;
  }

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getCoverPicture() {
    return coverPicture;
  }

  public void setCoverPicture(String coverPicture) {
    this.coverPicture = coverPicture;
  }


  @Override
  public MetaData clone() throws CloneNotSupportedException {
    MetaData clonedProfileMetaData = new MetaData();

    clonedProfileMetaData.setName(name);
    clonedProfileMetaData.setOrgName(orgName);
    clonedProfileMetaData.setDate(date);
    clonedProfileMetaData.setVersion(version);
    return clonedProfileMetaData;
  }
}
