package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Date;

public class ApplyInfo implements java.io.Serializable, Comparable<ApplyInfo>{
  private static final long serialVersionUID = 1L;

  public ApplyInfo() {

  }

  private String id;
  private Date pcDate;
  private Integer position;

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Date getPcDate() {
    return pcDate;
  }

  public void setPcDate(Date pcDate) {
    this.pcDate = pcDate;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

@Override
public int compareTo(ApplyInfo o) {
	// TODO Auto-generated method stub
	return this.getPosition()-o.getPosition();
}
  


}
