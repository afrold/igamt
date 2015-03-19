package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "IGCONSTRAINT")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Constraint implements Serializable, Cloneable{

	private static final long serialVersionUID = 5723342171557075960L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;

	@NotNull
	@Column(nullable = false, name = "CONSTRAINT_ID")
	protected String constraintId;

	@Column(name = "CONSTRAINT_TARGET")
	protected String constraintTarget;

	protected Reference reference;

	@NotNull
	@Column(nullable = false, name = "CONSTRAINT_DEC")
	// ?? Should this be removed since there is already description in reference
	protected String description;

	@NotNull
	@Column(nullable = false, columnDefinition = "LONGTEXT", name = "CONSTRAINT_ASSERTION")
	protected String assertion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	@Override
	public String toString() {
		return "Constraint [id=" + id + ", constraintId=" + constraintId
				+ ", constraintTarget=" + constraintTarget + ", reference="
				+ reference + ", description=" + description + ", assertion="
				+ assertion + "]";
	}
	
	@Override
	protected Constraint clone() throws CloneNotSupportedException {
		Constraint c = (Constraint)super.clone();
		c.setId(null);
		c.setReference(this.reference.clone());
		return c;
	}

}
