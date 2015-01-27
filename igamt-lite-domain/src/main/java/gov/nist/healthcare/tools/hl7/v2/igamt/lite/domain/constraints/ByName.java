package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class ByName extends ByNameOrByID{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7656473923145117910L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;
	
	@NotNull
	@Column(nullable = false)
	protected String byName;
	
	@OneToMany(mappedBy = "constraints", cascade = CascadeType.ALL)
	protected Set<Constraint> constraints = new HashSet<Constraint>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getByName() {
		return byName;
	}

	public void setByName(String byName) {
		this.byName = byName;
	}

	public Set<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Set<Constraint> constraints) {
		this.constraints = constraints;
	}

	
	
}
