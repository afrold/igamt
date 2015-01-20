package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Datatypes implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@OneToMany(mappedBy = "datatypes", cascade = CascadeType.ALL)
	private Set<Datatype> datatypes = new HashSet<Datatype>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Profile profile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Datatype> getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Set<Datatype> datatypes) {
		this.datatypes = datatypes;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void addDatatype(Datatype d) {
		if (d.getDatatypes() != null) {
			throw new IllegalArgumentException(
					"This datatype already below to a different datatypes");
		}
		datatypes.add(d);
		d.setDatatypes(this);
	}

}
