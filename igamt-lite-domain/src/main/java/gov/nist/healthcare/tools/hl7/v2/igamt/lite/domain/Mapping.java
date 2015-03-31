package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

//@Entity
//@Table(name = "MAPPING")
public class Mapping implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id 
	// @GeneratedValue(strategy = GenerationType.AUTO)
	protected String id;

	// @OneToMany(cascade = CascadeType.ALL)
	// @JoinTable(name = "MAPPING_CASE", joinColumns = //@JoinColumn(name =
	// "MAPPING"), inverseJoinColumns = //@JoinColumn(name = "CASE"))
	protected List<Case> cases = new ArrayList<Case>();

	// @NotNull
	// @Column(name = "MAPPING_POSITION")
	protected Integer position = 0;

	// @NotNull
	// @Column(name = "REFERENCE")
	protected Integer reference;

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

}
