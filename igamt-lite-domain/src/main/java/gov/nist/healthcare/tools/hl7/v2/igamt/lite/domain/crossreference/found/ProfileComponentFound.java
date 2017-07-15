package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

public class ProfileComponentFound {
  private String id;
  private String name;
  private String description;
  private Integer targetPosition;
  private String where;

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

  public Integer getTargetPosition() {
    return targetPosition;
  }

  public void setTargetPosition(Integer targetPosition) {
    this.targetPosition = targetPosition;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

}
