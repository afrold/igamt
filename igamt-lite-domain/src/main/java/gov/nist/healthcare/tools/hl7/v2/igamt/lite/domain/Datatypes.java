package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="DATATYPES")
public class Datatypes implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
 	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(mappedBy = "datatypes",fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy(value="position")
	private final Set<Datatype> datatypes = new HashSet<Datatype>();


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Datatype> getDatatypes() {
		return datatypes;
	}


	public void addDatatype(Datatype d) {
		if (d.getDatatypes() != null) {
			throw new IllegalArgumentException(
					"This datatype already belogs to a different datatypes");
		}
		d.setPosition(datatypes.size() +1);
		datatypes.add(d);
		d.setDatatypes(this);
	}
	
	@Override
    public Datatypes clone() throws CloneNotSupportedException {
		Datatypes clonedDatatypes = (Datatypes) super.clone();
		clonedDatatypes.setId(null);
		//NOT for FINAL
        return clonedDatatypes;
    }

}
