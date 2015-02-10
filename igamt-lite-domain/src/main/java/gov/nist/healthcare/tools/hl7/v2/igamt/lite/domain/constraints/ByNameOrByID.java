package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ByNameOrByID implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212340093784881862L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@OneToMany
	@JoinTable(name = "ByNameOrByID_Constraint", joinColumns = @JoinColumn(name = "ByNameOrByID"), inverseJoinColumns = @JoinColumn(name = "Constraint"))
	protected Set<Constraint> constraints = new HashSet<Constraint>();

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
    public ByNameOrByID clone() throws CloneNotSupportedException {
		ByNameOrByID clonedByNameOrByID = (ByNameOrByID) super.clone();
		clonedByNameOrByID.setConstraints(new HashSet<Constraint>(constraints));
		clonedByNameOrByID.setId(null);
        return clonedByNameOrByID;
    }

}
