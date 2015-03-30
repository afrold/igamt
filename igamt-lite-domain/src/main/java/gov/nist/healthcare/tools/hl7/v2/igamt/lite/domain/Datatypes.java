package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatypes")
public class Datatypes implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Transient
	private final Set<Datatype> children = new HashSet<Datatype>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Datatype> getChildren() {
		return children;
	}

	public void addDatatype(Datatype d) {
		if (d.getDatatypes() != null) {
			throw new IllegalArgumentException("This datatype " + d.getLabel()
					+ " already belongs to library " + d.getDatatypes().getId());
		}
		children.add(d);
		d.setDatatypes(this);
	}

	// public Datatype find(String label) {
	// Iterator<Datatype> it = this.children.iterator();
	// while (it.hasNext()) {
	// Datatype tmp = it.next();
	// if (tmp.getLabel().equals(label)) {
	// return tmp;
	// }
	// }
	// return null;
	// }

	@Override
	public Datatypes clone() throws CloneNotSupportedException {
		Datatypes clonedDatatypes = (Datatypes) super.clone();
		clonedDatatypes.setId(null);
		// NOT for FINAL
		return clonedDatatypes;
	}

}
