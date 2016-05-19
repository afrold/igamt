package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datatype")
public class Datatype extends DataModelWithConstraints implements java.io.Serializable,
Cloneable, Comparable<Datatype> {

	private static final long serialVersionUID = 1L;
	
	public Datatype() {
		super();
		this.type = Constant.DATATYPE;
	}

	@Id
	private String id;

	private String label;

	private String ext;
	
	private String purposeAndUse;

	protected List<Component> components = new ArrayList<Component>();

	private String name;

	private String description;

	protected String comment = "";

	protected String usageNote = "";

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		if (components != null) {
			this.components.clear();
			Iterator<Component> it = components.iterator();
			while (it.hasNext()) {
				addComponent(it.next());
			}
		} else {
			this.components = null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPurposeAndUse() {
		return purposeAndUse;
	}

	public void setPurposeAndUse(String purposeAndUse) {
		this.purposeAndUse = purposeAndUse;
	}

	public void addComponent(Component c) {
		c.setPosition(components.size() + 1);
		components.add(c);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUsageNote() {
		return usageNote;
	}

	public void setUsageNote(String usageNote) {
		this.usageNote = usageNote;
	}
	
 

	@Override
	public String toString() {
		return "Datatype [id=" + id + ", label=" + label + ", name=" + name
				+ ", description=" + description + ", components=" + components + "]";
	}

	@Override
	public Datatype clone() throws CloneNotSupportedException {
		Datatype clonedDT = new Datatype();

		clonedDT.setComment(comment);
		clonedDT.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedDT.addConformanceStatement(cs.clone());
		}

		clonedDT.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedDT.addPredicate(cp.clone());
		}

		clonedDT.setComponents(new ArrayList<Component>());
		for (Component c : this.components) {
			clonedDT.addComponent(c.clone());
		}
		clonedDT.setDescription(description);
		clonedDT.setLabel(label);
		clonedDT.setName(name);
		clonedDT.setUsageNote(usageNote);

		return clonedDT;
	}

	public boolean isEqual(Datatype dt) {
		if (dt == null)
			return false;
		if (hl7Version == null) {
			if (dt.hl7Version != null)
				return false;
		} else if (!hl7Version.equals(dt.hl7Version))
			return false;
		if (label == null) {
			if (dt.label != null)
				return false;
		} else if (!label.equals(dt.label))
			return false;
		if (name == null) {
			if (dt.name != null)
				return false;
		} else if (!name.equals(dt.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Datatype o) {
		int x = String.CASE_INSENSITIVE_ORDER.compare(this.getName() != null && this.label != null ? this.getName() + this.getLabel() : "",
				o.getName() != null && o.getLabel() != null ? o.getName() + o.getLabel() : "");
		if (x == 0) {
			x = (this.getName() != null  && this.getLabel() != null ? this.getName() + this.getLabel() : "").compareTo(o.getName() != null && o.getLabel() != null ? o.getName()+o.getLabel(): "");
		}
		return x;
	}
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
            append(id).
            toHashCode();
    }
	
    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof Datatype))
            return false;
        if (obj == this)
            return true;

        Datatype rhs = (Datatype) obj;
        return new EqualsBuilder().
            append(id, rhs.id).
            isEquals();
    }
    
    public String getLabel() {
		return name + (ext != null ? "_" + ext : "");
	}
}
