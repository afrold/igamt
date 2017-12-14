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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;

/**
 * @author Harold Affo (harold.affo@nist.gov) Feb 13, 2015
 */
public abstract class DataModel {

  protected Date dateUpdated;
  protected boolean temporary =false;
  public boolean isTemporary() {
	return temporary;
}

public void setTemporary(boolean temporary) {
	this.temporary = temporary;
}

protected boolean duplicated = false;

  /**
   * @return the duplicated
   */
  public boolean isDuplicated() {
    return duplicated;
  }

  /**
   * @param duplicated the duplicated to set
   */
  public void setDuplicated(boolean duplicated) {
    this.duplicated = duplicated;
  }

  protected String publicationDate;
  protected int publicationVersion = 0;
  protected String createdFrom;
  private String hl7Section;
  protected String authorNotes = "";
  protected SourceType sourceType = SourceType.INTERNAL;
  protected String sourceUrl;


  /**
   * @return the sourceUrl
   */
  public String getSourceUrl() {
    return sourceUrl;
  }

  /**
   * @param sourceUrl the sourceUrl to set
   */
  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  /**
   * @return the sourceType
   */
  public SourceType getSourceType() {
    return sourceType;
  }

  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(SourceType sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * @return the authorNotes
   */
  public String getAuthorNotes() {
    return authorNotes;
  }

  /**
   * @param authorNotes the authorNotes to set
   */
  public void setAuthorNotes(String authorNotes) {
    this.authorNotes = authorNotes;
  }

  public DataModel() {
    this.dateUpdated = new Date();
  }

  protected String type;

  public String dt() {
    return type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated(Date dateUpdated) {
    this.dateUpdated = dateUpdated;
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



}
