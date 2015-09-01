package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segment")
public class Segment extends DataModel implements java.io.Serializable,
		Cloneable {

	private static final long serialVersionUID = 1L;

	public Segment() {
		super();
		type = Constant.SEGMENT;
		this.id = ObjectId.get().toString();
	}

	@Id
	private String id;
	//
	// @DBRef
	// private Segments segments;

	// //@NotNull
	private String label;

	private List<Field> fields = new ArrayList<Field>();

	private List<DynamicMapping> dynamicMappings = new ArrayList<DynamicMapping>();

	// //@NotNull
	private String name;

	private String description;

	private String hl7Version;

	protected List<Predicate> predicates = new ArrayList<Predicate>();

	protected List<ConformanceStatement> conformanceStatements = new ArrayList<ConformanceStatement>();

	protected String comment = "";

	private String text1 = "";

	private String text2 = "";

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

	public void addPredicate(Predicate p) {
		predicates.add(p);
	}

	public void addConformanceStatement(ConformanceStatement cs) {
		conformanceStatements.add(cs);
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

	public void addDynamicMapping(DynamicMapping d) {
		d.setPosition(dynamicMappings.size() + 1);
		dynamicMappings.add(d);
	}

	public List<DynamicMapping> getDynamicMappings() {
		return dynamicMappings;
	}

	public void setDynamicMappings(List<DynamicMapping> dynamicMappings) {
		if (dynamicMappings != null) {
			this.dynamicMappings.clear();
			Iterator<DynamicMapping> it = dynamicMappings.iterator();
			while (it.hasNext()) {
				addDynamicMapping(it.next());
			}
		} else {
			this.dynamicMappings = null;
		}
	}

	public List<Predicate> getPredicates() {
		return predicates;
	}

	// public void setPredicates(Set<Predicate> predicates) {
	// if (predicates != null) {
	// this.predicates.clear();
	// Iterator<Predicate> it = predicates.iterator();
	// while (it.hasNext()) {
	// addPredicate(it.next());
	// }
	// }
	// }

	public List<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}

	// public void setConformanceStatements(
	// Set<ConformanceStatement> conformanceStatements) {
	// if (conformanceStatements != null) {
	// this.conformanceStatements.clear();
	// Iterator<ConformanceStatement> it = conformanceStatements
	// .iterator();
	// while (it.hasNext()) {
	// addConformanceStatement(it.next());
	// }
	// }
	// }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	public void setConformanceStatements(
			List<ConformanceStatement> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	@Override
	public String toString() {
		return "Segment [id=" + id + ", label=" + label + ", name=" + name
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

	public Predicate findOnePredicate(String predicateId) {
		for (Predicate predicate : this.getPredicates()) {
			if (predicate.getId().equals(predicateId)) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(String confId) {
		for (ConformanceStatement conf : this.getConformanceStatements()) {
			if (conf.getId().equals(confId)) {
				return conf;
			}
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		Predicate p = findOnePredicate(predicateId);
		return p != null && this.getPredicates().remove(p);
	}

	public boolean deleteConformanceStatement(String cId) {
		ConformanceStatement c = findOneConformanceStatement(cId);
		return c != null && this.getConformanceStatements().remove(c);
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
		clonedSegment.setDynamicMappings(new ArrayList<DynamicMapping>());
		for (DynamicMapping dm : this.dynamicMappings) {
			clonedSegment.addDynamicMapping(dm.clone());
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

}
