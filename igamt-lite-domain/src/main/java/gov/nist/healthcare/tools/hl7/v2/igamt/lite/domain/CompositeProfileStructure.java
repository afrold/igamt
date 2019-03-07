/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 6, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

@Document(collection = "compositeProfileStructure")
public class CompositeProfileStructure {

  public CompositeProfileStructure() {
    this.type = Constant.COMPOSITEPROFILESTRUCTURE;
  }

  @Id
  private String id;
  private String name;
  private String ext;
  private String description;
  private String comment;
  private String defPreText = "";
  private String defPostText = "";
  private String coreProfileId;
  private Date dateUpdated;
  private List<ApplyInfo> profileComponentsInfo;
  private String type;
  private SCOPE scope;
  private STATUS status;

  protected String authorNotes = ""; 
  
  private Long accountId;


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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }


  public String getDefPreText() {
    return defPreText;
  }

  public void setDefPreText(String defPreText) {
    this.defPreText = defPreText;
  }

  public String getDefPostText() {
    return defPostText;
  }

  public void setDefPostText(String defPostText) {
    this.defPostText = defPostText;
  }

  public String getCoreProfileId() {
    return coreProfileId;
  }

  public void setCoreProfileId(String coreProfileId) {
    this.coreProfileId = coreProfileId;
  }


  public Date getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated(Date dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  public List<ApplyInfo> getProfileComponentsInfo() {
    return profileComponentsInfo;
  }

  public void setProfileComponentsInfo(List<ApplyInfo> profileComponentsInfo) {
    this.profileComponentsInfo = profileComponentsInfo;
  }

  public void addProfileComponent(ApplyInfo pc) {
    profileComponentsInfo.add(pc);
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  
  
  

  public Long getAccountId() {
	return accountId;
}

public void setAccountId(Long accountId) {
	this.accountId = accountId;
}

public List<String> getProfileComponentIds() {
    List<String> result = new ArrayList<String>();
    for (ApplyInfo appInfo : this.profileComponentsInfo) {
      result.add(appInfo.getId());
    }
    return result;
  }

  public SCOPE getScope() {
    return scope;
  }

  public void setScope(SCOPE scope) {
    this.scope = scope;
  }

public STATUS getStatus() {
	return status;
}

public void setStatus(STATUS status) {
	this.status = status;
}



}
