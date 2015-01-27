package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ByNameOrByID implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212340093784881862L;

	@OneToMany(mappedBy = "constraints", cascade = CascadeType.ALL)
	protected Set<Constraint> constraints = new HashSet<Constraint>();

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	
}
