package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;


// @Entity
// @Table(name = "BYNAME")
public class ByName extends ByNameOrByID {

  /**
	 *  
	 */
  private static final long serialVersionUID = -7656473923145117910L;

  // @NotNull
  // @Column(nullable = false, name = "BYNAME")
  private String byName;

  public String getByName() {
    return byName;
  }

  public void setByName(String byName) {
    this.byName = byName;
  }

  @Override
  public String toString() {
    return "ByName [id=" + id + ", byName=" + byName + "]";
  }

}
