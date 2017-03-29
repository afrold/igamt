package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

// @Entity
// @Table(name = "MAPPING")
@Deprecated
public class Mapping implements Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public Mapping() {
    super();
    this.id = ObjectId.get().toString();
  }

  private String id;

  // @OneToMany(cascade = CascadeType.ALL)
  // @JoinTable(name = "MAPPING_CASE", joinColumns = //@JoinColumn(name =
  // "MAPPING"), inverseJoinColumns = //@JoinColumn(name = "CASE"))
  protected List<Case> cases = new ArrayList<Case>();

  // @NotNull
  // @Column(name = "MAPPING_POSITION")
  protected Integer position;

  // @NotNull
  // @Column(name = "REFERENCE")
  protected Integer reference;
  
  protected Integer secondReference;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Case> getCases() {
    return cases;
  }

  public void setCases(List<Case> cases) {
    this.cases = cases;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Integer getReference() {
    return reference;
  }

  public void setReference(Integer reference) {
    this.reference = reference;
  }
  
  public Integer getSecondReference() {
	return secondReference;
  }

  public void setSecondReference(Integer secondReference) {
	this.secondReference = secondReference;
  }

  public void addCase(Case c) {
    cases.add(c);
  }

  @Override
  public Mapping clone() throws CloneNotSupportedException {
    Mapping clonedMapping = new Mapping();

    clonedMapping.setCases(new ArrayList<Case>());
    for (Case c : this.cases) {
      clonedMapping.getCases().add(c.clone());
    }

    clonedMapping.setPosition(position);
    clonedMapping.setReference(reference);
    clonedMapping.setSecondReference(secondReference);

    return clonedMapping;
  }

}
