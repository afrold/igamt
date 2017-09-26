package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.Date;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;

public abstract class DisplayModelMetaData {
  protected String id;
  protected String label;
  protected String ext;
  protected String type;
  protected String name;
  protected String description;
  protected String text1 = "";
  protected String text2 = "";
  protected Date dateUpdated;
  protected boolean duplicated = false;
  protected String publicationDate;
  protected int publicationVersion = 0;
  protected String createdFrom;
  protected String hl7Section;
  protected String authorNotes = "";
  protected SourceType sourceType = SourceType.INTERNAL;
  protected String sourceUrl;
  protected String hl7Version;
  protected Long accountId;
  protected SCOPE scope;
  protected String parentVersion;
  protected String version;
  protected STATUS status = STATUS.UNPUBLISHED;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getText1() {
    return text1;
  }

  public void setText1(String text1) {
    this.text1 = text1;
  }

  public String getText2() {
    return text2;
  }

  public void setText2(String text2) {
    this.text2 = text2;
  }

  public Date getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated(Date dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  public boolean isDuplicated() {
    return duplicated;
  }

  public void setDuplicated(boolean duplicated) {
    this.duplicated = duplicated;
  }

  public String getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(String publicationDate) {
    this.publicationDate = publicationDate;
  }

  public int getPublicationVersion() {
    return publicationVersion;
  }

  public void setPublicationVersion(int publicationVersion) {
    this.publicationVersion = publicationVersion;
  }

  public String getCreatedFrom() {
    return createdFrom;
  }

  public void setCreatedFrom(String createdFrom) {
    this.createdFrom = createdFrom;
  }

  public String getHl7Section() {
    return hl7Section;
  }

  public void setHl7Section(String hl7Section) {
    this.hl7Section = hl7Section;
  }

  public String getAuthorNotes() {
    return authorNotes;
  }

  public void setAuthorNotes(String authorNotes) {
    this.authorNotes = authorNotes;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public void setSourceType(SourceType sourceType) {
    this.sourceType = sourceType;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  public String getHl7Version() {
    return hl7Version;
  }

  public void setHl7Version(String hl7Version) {
    this.hl7Version = hl7Version;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public SCOPE getScope() {
    return scope;
  }

  public void setScope(SCOPE scope) {
    this.scope = scope;
  }

  public String getParentVersion() {
    return parentVersion;
  }

  public void setParentVersion(String parentVersion) {
    this.parentVersion = parentVersion;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public STATUS getStatus() {
    return status;
  }

  public void setStatus(STATUS status) {
    this.status = status;
  }


}
