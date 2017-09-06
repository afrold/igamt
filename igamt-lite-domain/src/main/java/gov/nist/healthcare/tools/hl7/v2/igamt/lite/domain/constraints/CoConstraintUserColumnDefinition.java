package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import org.springframework.data.annotation.Id;

public class CoConstraintUserColumnDefinition implements java.io.Serializable, Cloneable {

  /**
  * 
  */
  private static final long serialVersionUID = -6405970850316993978L;
  @Id
  private String id;
  private String title;

  public CoConstraintUserColumnDefinition() {
    super();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public CoConstraintUserColumnDefinition clone() throws CloneNotSupportedException {
    CoConstraintUserColumnDefinition cloned = new CoConstraintUserColumnDefinition();
    cloned.setId(id);
    cloned.setTitle(title);
    return cloned;
  }
}
