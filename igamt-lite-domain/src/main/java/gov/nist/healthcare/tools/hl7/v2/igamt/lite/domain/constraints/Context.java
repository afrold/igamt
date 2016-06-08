package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

// @Entity
// @Table(name = "CONTEXT")
public class Context implements Serializable, Cloneable {

  private static final long serialVersionUID = -3037628238620317355L;

  // @Id
  // @Column(name = "ID")
  // @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  // @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  // @JoinTable(name = "CONTEXT_BYNAMEORBYID", joinColumns =
  // //@JoinColumn(name = "CONTEXT"), inverseJoinColumns = //@JoinColumn(name
  // = "BYNAMEORBYID"))
  private Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<ByNameOrByID> getByNameOrByIDs() {
    return byNameOrByIDs;
  }

  public void setByNameOrByIDs(Set<ByNameOrByID> byNameOrByIDs) {
    this.byNameOrByIDs = byNameOrByIDs;
  }

  @Override
  public String toString() {
    return "Context [id=" + id + "]";
  }

  @Override
  public Context clone() throws CloneNotSupportedException {
    Context clonedContext = (Context) super.clone();
    clonedContext.setByNameOrByIDs(new HashSet<ByNameOrByID>(byNameOrByIDs));
    clonedContext.setId(null);
    return clonedContext;
  }

}
