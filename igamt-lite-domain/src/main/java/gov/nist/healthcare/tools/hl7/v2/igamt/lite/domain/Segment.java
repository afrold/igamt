package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segment")
public class Segment extends DataModel implements java.io.Serializable {

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

	private final List<Field> fields = new ArrayList<Field>();

	private List<DynamicMapping> dynamicMappings = new ArrayList<DynamicMapping>();

	// //@NotNull
	private String name;

	private String description;

	protected List<Predicate> predicates = new ArrayList<Predicate>();

	protected List<ConformanceStatement> conformanceStatements = new ArrayList<ConformanceStatement>();

	protected String comment;

	protected String usageNote;

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

	public void setPredicates(Set<Predicate> predicates) {
		if (predicates != null) {
			this.predicates.clear();
			Iterator<Predicate> it = predicates.iterator();
			while (it.hasNext()) {
				addPredicate(it.next());
			}
		}
	}

	public List<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(
			Set<ConformanceStatement> conformanceStatements) {
		if (conformanceStatements != null) {
			this.conformanceStatements.clear();
			Iterator<ConformanceStatement> it = conformanceStatements
					.iterator();
			while (it.hasNext()) {
				addConformanceStatement(it.next());
			}
		}
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

	// public Segments getSegments() {
	// return segments;
	// }
	//
	// public void setSegments(Segments segments) {
	// this.segments = segments;
	// }

	@Override
	public String toString() {
		return "Segment [id=" + id + "label=" + label + ", name=" + name
				+ ", description=" + description + "]";
	}

}
