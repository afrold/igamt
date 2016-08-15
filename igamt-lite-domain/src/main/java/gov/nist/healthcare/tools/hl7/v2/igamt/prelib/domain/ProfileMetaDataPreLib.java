/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProfileMetaDataPreLib {

  private static final long serialVersionUID = 1L;

  public ProfileMetaDataPreLib() {
    super();
  }

  /* XSD Attributes */

  private String profileID = ""; // ConformanceProfile/@ID

  private String type = ""; // ConformanceProfile/@Type

  private String hl7Version = ""; // ConformanceProfile/@HL7Version

  private String schemaVersion = ""; // ConformanceProfile/@SchemaVersion

  private String specificationName = ""; // ConformanceProfile/MetaData/@SpecificationName

  private String status = ""; // ConformanceProfile/MetaData/@Status

  private String topics = ""; // ConformanceProfile/MetaData/@Topics
  
  private String XmlId;

  /* XSD Attributes END */



  @JsonIgnore
  private String subTitle = "";

  @JsonIgnore
  private String ext = "";

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

  public String getHl7Version() {
    return hl7Version;
  }

  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
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

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public ProfileMetaDataPreLib clone() throws CloneNotSupportedException {
    ProfileMetaDataPreLib clonedProfileMetaData = new ProfileMetaDataPreLib();

    // clonedProfileMetaData.setEncodings(new HashSet<String>());
    // for (String s : this.encodings) {
    // clonedProfileMetaData.getEncodings().add(s);
    // }
    //
    // clonedProfileMetaData.setHl7Version(hl7Version);
    // clonedProfileMetaData.setProfileID(profileID);
    // clonedProfileMetaData.setName(this.getName());
    // clonedProfileMetaData.setOrgName(this.getOrgName());
    // clonedProfileMetaData.setSchemaVersion(schemaVersion);
    // clonedProfileMetaData.setStatus(status);
    // clonedProfileMetaData.setTopics(topics);
    // clonedProfileMetaData.setType(type);
    // clonedProfileMetaData.setDate(this.getDate());
    // clonedProfileMetaData.setVersion(this.getVersion());
    // clonedProfileMetaData.setSubTitle(subTitle);
    // clonedProfileMetaData.setVersion(hl7Version);
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

public String getXmlId() {
	return XmlId;
}

public void setXmlId(String xmlId) {
	XmlId = xmlId;
}

}
