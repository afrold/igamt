package gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;

public class DocumentMetaDataPreLib extends DataModel implements java.io.Serializable, Cloneable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  private String subTitle;
  private String title;
  private String version;
  private String date;
  private String ext;
  private String orgName;
  private String status;
  private String topics;
  private String specificationName;
  private String identifier;



  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

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

  public String getSpecificationName() {
    return specificationName;
  }

  public void setSpecificationName(String specificationName) {
    this.specificationName = specificationName;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public DocumentMetaDataPreLib clone() throws CloneNotSupportedException {
    DocumentMetaDataPreLib clonedDocumentMetaData = new DocumentMetaDataPreLib();

    clonedDocumentMetaData.setSubTitle(subTitle);
    clonedDocumentMetaData.setTitle(title);
    clonedDocumentMetaData.setVersion(version);
    clonedDocumentMetaData.setDate(date);
    clonedDocumentMetaData.setExt(ext);
    clonedDocumentMetaData.setIdentifier(identifier);
    clonedDocumentMetaData.setOrgName(orgName);
    clonedDocumentMetaData.setSpecificationName(specificationName);
    clonedDocumentMetaData.setStatus(status);
    clonedDocumentMetaData.setTopics(topics);
    clonedDocumentMetaData.setType(date);

    return clonedDocumentMetaData;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }
}
