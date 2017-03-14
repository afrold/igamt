package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProfileMetaData extends MetaData {

  private static final long serialVersionUID = 1L;

  public ProfileMetaData() {
    super();
  }

  /* XSD Attributes */

  private String type = ""; // ConformanceProfile/@Type

  private String schemaVersion = ""; // ConformanceProfile/@SchemaVersion

  private String specificationName = ""; // ConformanceProfile/MetaData/@SpecificationName

  private String status = ""; // ConformanceProfile/MetaData/@Status

  private String topics = ""; // ConformanceProfile/MetaData/@Topics

  private String profileID = "";

  /* XSD Attributes END */



  @JsonIgnore
  private String subTitle = "";

  private Set<String> encodings = new HashSet<String>();

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTopics() {
    return topics;
  }

  public void setTopics(String topics) {
    this.topics = topics;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSchemaVersion() {
    return schemaVersion;
  }

  public void setSchemaVersion(String schemaVersion) {
    this.schemaVersion = schemaVersion;
  }

  public Set<String> getEncodings() {
    return encodings;
  }

  public void setEncodings(Set<String> encodings) {
    this.encodings = encodings;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  @Override
  public ProfileMetaData clone() throws CloneNotSupportedException {
    ProfileMetaData clonedProfileMetaData = new ProfileMetaData();

    clonedProfileMetaData.setEncodings(new HashSet<String>());
    for (String s : this.encodings) {
      clonedProfileMetaData.getEncodings().add(s);
    }

    clonedProfileMetaData.setHl7Version(getHl7Version());
    clonedProfileMetaData.setName(this.getName());
    clonedProfileMetaData.setOrgName(this.getOrgName());
    clonedProfileMetaData.setSchemaVersion(schemaVersion);
    clonedProfileMetaData.setStatus(status);
    clonedProfileMetaData.setTopics(topics);
    clonedProfileMetaData.setType(type);
    clonedProfileMetaData.setVersion(this.getVersion());
    clonedProfileMetaData.setSubTitle(subTitle);
    clonedProfileMetaData.setVersion(getHl7Version());
    clonedProfileMetaData.setProfileID(UUID.randomUUID().toString());
    return clonedProfileMetaData;
  }

  public String getSpecificationName() {
    return specificationName;
  }

  public void setSpecificationName(String specificationName) {
    this.specificationName = specificationName;
  }

  public String getProfileID() {
    return profileID;
  }

  public void setProfileID(String profileID) {
    this.profileID = profileID;
  }
}
