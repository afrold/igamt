package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="IGCONSTRAINT") // Constraint is a keyword
public class Constraint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5723342171557075960L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false, name="CONSTRAINT_ID")
	private String constraintId;

	@Column(name="CONSTRAINT_TRUEUSAGE")
	private Usage trueUsage;

	@Column(name="CONSTRAINT_FALSEUSAGE")
	private Usage falseUsage;
	
	@Column(name="CONSTRAINT_TARGET")
	private String constraintTarget;

	private Reference reference;

	@NotNull
 	@Column(nullable = false,name="CONSTRAINT_DEC") // ?? Should this be removed since there is already  description in reference
	private String description;

	@NotNull
	@Column(nullable = false, columnDefinition = "LONGTEXT",name="CONSTRAINT_ASSERTION")
	private String assertion;

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
	
	public Usage getTrueUsage() {
		return trueUsage;
	}

	public void setTrueUsage(Usage trueUsage) {
		this.trueUsage = trueUsage;
	}

	public Usage getFalseUsage() {
		return falseUsage;
	}

	public void setFalseUsage(Usage falseUsage) {
		this.falseUsage = falseUsage;
	}

	@Override
	public String toString() {
		return "Constraint [id=" + id + ", constraintId=" + constraintId
				+ ", constraintTarget=" + constraintTarget + ", reference="
				+ reference + ", description=" + description + ", assertion="
				+ assertion + "]";
	}

}
