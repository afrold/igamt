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

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "compositeProfileStructure")
public class CompositeProfileStructure {

  @Id
  private String id;
  private String name;
  private String description;
  private String coreProfileId;
  private List<ApplyInfo> profileComponentsInfo;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCoreProfileId() {
    return coreProfileId;
  }

  public void setCoreProfileId(String coreProfileId) {
    this.coreProfileId = coreProfileId;
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



}
