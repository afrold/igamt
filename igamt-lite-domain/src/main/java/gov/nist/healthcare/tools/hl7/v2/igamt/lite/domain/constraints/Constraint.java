package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

// @Entity
// @Table(name = "IGCONSTRAINT")
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Constraint implements Serializable, Cloneable {

  private static final long serialVersionUID = 5723342171557075960L;

  public Constraint() {
    super();
    this.id = ObjectId.get().toString();
  }

  // @Id
  // @Column(name = "ID")
  // @GeneratedValue(strategy = GenerationType.TABLE)
  @Id
  protected String id;

  // @NotNull
  // @Column(nullable = false, name = "CONSTRAINT_ID")
  protected String constraintId;

  // @Column(name = "CONSTRAINT_TARGET")
  protected String constraintTarget;

  protected String constraintClassification;

  protected Reference reference;

  // @NotNull
  // @Column(nullable = false, name = "CONSTRAINT_DEC")
  // ?? Should this be removed since there is already description in reference
  protected String description;

  // @NotNull
  // @Column(nullable = false, columnDefinition = "LONGTEXT", name =
  // "CONSTRAINT_ASSERTION")
  protected String assertion;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getConstraintId() {
    return constraintId;
  }

  public void setConstraintId(String constraintId) {
    this.constraintId = constraintId;
  }

  public String getConstraintTarget() {
    return constraintTarget;
  }

  public void setConstraintTarget(String constraintTarget) {
    this.constraintTarget = constraintTarget;
  }

  public Reference getReference() {
    return reference;
  }

  public void setReference(Reference reference) {
    this.reference = reference;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAssertion() {
    return assertion;
  }

  public void setAssertion(String assertion) {
    this.assertion = assertion;
  }

  public String getConstraintClassification() {
    return constraintClassification;
  }

  public void setConstraintClassification(String constraintClassification) {
    this.constraintClassification = constraintClassification;
  }

  @Override
  public String toString() {
    return "Constraint [id=" + id + ", constraintId=" + constraintId + ", constraintTarget="
        + constraintTarget + ", reference=" + reference + ", description=" + description
        + ", assertion=" + assertion + "]";
  }

  @Override
  protected Constraint clone() throws CloneNotSupportedException {
    Constraint c = (Constraint) super.clone();
    c.setId(this.id);
    if (reference != null)
      c.setReference(this.reference.clone());
    return c;
  }
}
