package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Notification {
  @Id
  private String id;

  private TargetType targetType;

  private String targetId;

  private Date changedDate;

  private String byWhom;

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
}
