package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class Segment extends SectionModelWithConstraints implements java.io.Serializable,
Cloneable, Comparable<Segment> {

	private static final long serialVersionUID = 1L;

	public Segment() {
		super();
		type = Constant.SEGMENT;
		this.id = ObjectId.get().toString();
	}

	private String id;

	//
	// @DBRef
	// private Segments segments;

	// //@NotNull
	private String label;

	private List<Field> fields = new ArrayList<Field>();

	private DynamicMapping dynamicMapping = new DynamicMapping();

	// //@NotNull
	private String name;

	private String description;

	private String hl7Version;

	protected String comment = "";

	private String text1 = "";

	private String text2 = "";

	private String segLibExt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void addField(Field field) {
		field.setPosition(fields.size() + 1);
		fields.add(field);
	}

	public Field findOneField(String id) {
		if (this.fields != null)
			for (Field m : this.fields) {
				if (id.equals(m.getId())) {
					return m;
				}
			}
		return null;
	}

	public Field findOneFieldByName(String name) {
		if (this.fields != null)
			for (Field m : this.fields) {
				if (name.equals(m.getName())) {
					return m;
				}
			}
		return null;
	}

	public Mapping findOneMappingByPositionAndByReference(int position, int reference) {
		if (this.dynamicMapping != null){
			for (Mapping m: this.dynamicMapping.getMappings()){
				if (m.getPosition() == position && m.getReference() == reference){
					return m;
				}
			}
		}
		return null;
	}
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "Segment [id=" + getId() + ", label=" + label + ", name=" + name
				+ ", description=" + description +  ", comment=" + comment + "]";
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}
	
	public String getSegLibExt() {
		return segLibExt;
	}

	public void setSegLibExt(String segLibExt) {
		this.segLibExt = segLibExt;
	}

	public DynamicMapping getDynamicMapping() {
		return dynamicMapping;
	}

	public void setDynamicMapping(DynamicMapping dynamicMapping) {
		this.dynamicMapping = dynamicMapping;
	}

	public Segment clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Table> tableRecords)
					throws CloneNotSupportedException {
		Segment clonedSegment = new Segment();
		clonedSegment.setComment(comment);
		clonedSegment
		.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedSegment.addConformanceStatement(cs.clone());
		}
		clonedSegment.setDescription(description);
		
		clonedSegment.setDynamicMapping(new DynamicMapping());
		for (Mapping mapping : this.dynamicMapping.getMappings()) {
			clonedSegment.getDynamicMapping().addMapping(mapping.clone());
		}
		clonedSegment.setFields(new ArrayList<Field>());
		for (Field f : this.fields) {
			clonedSegment.addField(f.clone(dtRecords, tableRecords));
		}
		clonedSegment.setLabel(label);
		clonedSegment.setName(name);
		clonedSegment.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedSegment.addPredicate(cp.clone());
		}
		clonedSegment.setText1(text1);
		clonedSegment.setText2(text2);

		return clonedSegment;
	}

	@Override
	public int compareTo(Segment o) {
		int x = String.CASE_INSENSITIVE_ORDER.compare(this.getName() != null && this.label != null ? this.getName() + this.getLabel() : "",
				o.getName() != null && this.getLabel() != null ? o.getName() + this.getLabel() : "");
		if (x == 0) {
			x = (this.getName() != null  && this.getLabel() != null ? this.getName() + this.getLabel() : "").compareTo(o.getName() != null && this.getLabel() != null ? o.getName()+o.getLabel(): "");
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
       if (!(obj instanceof Segment))
            return false;
        if (obj == this)
            return true;

        Segment rhs = (Segment) obj;
        return new EqualsBuilder().
            append(id, rhs.id).
            isEquals();
    }
}
