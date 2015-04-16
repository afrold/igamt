package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatypes")
public class Datatypes implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	/**
	 * 
	 */
	public Datatypes() {
		super();
		this.id = ObjectId.get().toString();
	}

	private Set<Datatype> children = new HashSet<Datatype>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Datatype> getChildren() {
		return children;
	}

	public void setChildren(Set<Datatype> children) {
		this.children = children;
	}

	public void addDatatype(Datatype d) {
		children.add(d);
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

	public Datatype save(Datatype d) {
		if (!this.children.contains(d)) {
			children.add(d);
		}
		return d;
	}

	public void delete(String id) {
		Datatype d = findOne(id);
		if (d != null)
			this.children.remove(d);
	}

	public Datatype findOne(String id) {
		if (this.children != null)
			for (Datatype m : this.children) {
				if (m.getId().equals(id)) {
					return m;
				}
			}

		return null;
	}

	public Component findOneComponent(String id) {
		if (this.children != null)
			for (Datatype m : this.children) {
				Component c = m.findOneComponent(id);
				if (c != null) {
					return c;
				}
			}

		return null;
	}

	public Predicate findOnePredicate(String predicateId) {
		for (Datatype datatype : this.getChildren()) {
			Predicate predicate = datatype.findOnePredicate(predicateId);
			if (predicate != null) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(
			String conformanceStatementId) {
		for (Datatype datatype : this.getChildren()) {
			ConformanceStatement conf = datatype
					.findOneConformanceStatement(conformanceStatementId);
			if (conf != null) {
				return conf;
			}
		}
		return null;
	}

	@Override
	public Datatypes clone() throws CloneNotSupportedException {
		Datatypes clonedDatatypes = new Datatypes();
		clonedDatatypes.setChildren(new HashSet<Datatype>());
		for (Datatype dt : this.children) {
			clonedDatatypes.addDatatype(dt.clone());
		}

		return clonedDatatypes;
	}

}
