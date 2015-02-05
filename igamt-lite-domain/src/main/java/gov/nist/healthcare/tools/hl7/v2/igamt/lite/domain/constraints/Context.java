package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class Context implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3037628238620317355L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany
	@JoinTable(name = "Context_ByNameOrByID", joinColumns = @JoinColumn(name = "Context"), inverseJoinColumns = @JoinColumn(name = "ByNameOrByID"))
	private Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
		return "Context [id=" + id + ", byNameOrByIDs=" + byNameOrByIDs + "]";
	}

}
