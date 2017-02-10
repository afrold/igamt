/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Feb 7, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */

@Document(collection = "documentation")
public class Documentation extends DataModel {



  @Id
  protected String id;

  protected String content;

  protected String title;

  protected Long AccountId;

  private Long owner;

  protected String username;

  private Date updateDate;


  public String getUsername() {
    return username;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Documentation() {
    super();
    this.updateDate = super.dateUpdated;


    // TODO Auto-generated constructor stub
  }


  public String getTitle() {
    return title;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return the accountId
   */
  public Long getAccountId() {
    return AccountId;
  }

  /**
   * @param accountId the accountId to set
   */
  public void setAccountId(Long accountId) {
    AccountId = accountId;
  }

  /**
   * @param username
   */
  public void setUsername(String username) {
    // TODO Auto-generated method stub
    this.username = username;
  }


  /**
   * @return the updateDate
   */
  public Date getUpdateDate() {
    return updateDate;
  }


  /**
   * @param updateDate the updateDate to set
   */
  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }


  /**
   * @return the owner
   */
  public Long getOwner() {
    return owner;
  }


  /**
   * @param owner the owner to set
   */
  public void setOwner(Long owner) {
    this.owner = owner;
  }
}

