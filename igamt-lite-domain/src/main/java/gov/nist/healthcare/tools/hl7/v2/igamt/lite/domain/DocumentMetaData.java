package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;



public class DocumentMetaData extends MetaData implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  private String subTitle;
  private String title;
  private String status;
  private String topics;
  private String specificationName;
  private String identifier;
  private String implementationNotes;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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
  public DocumentMetaData clone() throws CloneNotSupportedException {
    DocumentMetaData clonedDocumentMetaData = new DocumentMetaData();

    clonedDocumentMetaData.setSubTitle(subTitle);
    clonedDocumentMetaData.setTitle(title);
    clonedDocumentMetaData.setVersion(this.getHl7Version());
    clonedDocumentMetaData.setExt(this.getExt());
    clonedDocumentMetaData.setIdentifier(identifier);
    clonedDocumentMetaData.setOrgName(this.getOrgName());
    clonedDocumentMetaData.setSpecificationName(specificationName);
    clonedDocumentMetaData.setStatus(status);
    clonedDocumentMetaData.setTopics(topics);
    clonedDocumentMetaData.setImplementationNotes(implementationNotes);

    return clonedDocumentMetaData;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }

  public String getImplementationNotes() {
    return implementationNotes;
  }

  public void setImplementationNotes(String implementationNotes) {
    this.implementationNotes = implementationNotes;
  }



}
