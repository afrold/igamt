package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="BYNAME_OR_BYID")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ByNameOrByID implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212340093784881862L;

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;

	@OneToMany( fetch = FetchType.EAGER,cascade=CascadeType.ALL)
//	@OrderBy(value="position")
	@JoinTable(name = "BYNAME_OR_BYID_IGCONSTRAINT", joinColumns = @JoinColumn(name = "BYNAME_OR_BYID"), inverseJoinColumns = @JoinColumn(name = "IGCONSTRAINT"))
	protected Set<Constraint> constraints = new HashSet<Constraint>();

	public Set<Constraint> getConstraints() {
		return constraints;
	}
	// Called only by hibernate. Do not use.
	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void addConstraint(Constraint e){
		if(e.getId() != null){
			throw new IllegalArgumentException("Constraint " + e.toString() + " is already persisted");
		}
//		e.setPosition(constraints.size() +1);
		constraints.add(e);
	}

}
