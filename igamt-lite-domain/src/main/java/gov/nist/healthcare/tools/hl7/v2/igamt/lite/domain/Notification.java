package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification")
public class Notification {
  @Id
  private String id;
  
  private TargetType targetType;
  
  private String targetId;
  
  private Date changedDate;
  
  private String byWhom;
  
  HashSet<String> igDocumentIds=new HashSet<String>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public TargetType getTargetType() {
    return targetType;
  }

  public void setTargetType(TargetType targetType) {
    this.targetType = targetType;
  }

  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public Date getChangedDate() {
    return changedDate;
  }

  public void setChangedDate(Date changedDate) {
    this.changedDate = changedDate;
  }

  public String getByWhom() {
    return byWhom;
  }

  public void setByWhom(String byWhom) {
    this.byWhom = byWhom;
  }

  public HashSet<String> getIgDocumentIds() {
    return igDocumentIds;
  }

  public void setIgDocumentIds(HashSet<String> igDocumentIds) {
    this.igDocumentIds = igDocumentIds;
  }  
  
  public void addIgId(String id){
    this.igDocumentIds.add(id);
  }
  
  public void removeIgId(String id){
    this.igDocumentIds.remove(id);
  }
  
  public boolean containIgId(String id){
    return this.igDocumentIds.contains(id);
  }

  @Override
  public String toString() {
    return "Notification [id=" + id + ", targetType=" + targetType + ", targetId=" + targetId
        + ", changedDate=" + changedDate + ", byWhom=" + byWhom + ", igDocumentIds=" + igDocumentIds
        + "]";
  }
}
