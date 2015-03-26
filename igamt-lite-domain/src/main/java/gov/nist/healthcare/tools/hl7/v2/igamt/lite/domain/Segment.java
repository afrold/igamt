package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segments")
public class Segment extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Segment() {
		super();
		type = Constant.SEGMENT;
	}

	@Id
	private Long id;

	private Long segmentsId;

	// @NotNull
	private String label;

	private List<Field> fields = new ArrayList<Field>();

	private Set<DynamicMapping> dynamicMappings = new HashSet<DynamicMapping>();

	// @NotNull
	private String name;

	private String description;

	protected Set<Predicate> predicates = new HashSet<Predicate>();

	protected Set<ConformanceStatement> conformanceStatements = new HashSet<ConformanceStatement>();

	protected String comment;

	protected String usageNote;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public void setFields(List<Field> fields) {
		if (fields != null) {
			this.fields.clear();
			Iterator<Field> it = fields.iterator();
			while (it.hasNext()) {
				addField(it.next());
			}
		} else {
			this.fields = null;
		}
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

	public Set<DynamicMapping> getDynamicMappings() {
		return dynamicMappings;
	}

	public void setDynamicMappings(Set<DynamicMapping> dynamicMappings) {
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

	public Set<Predicate> getPredicates() {
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

	public Set<ConformanceStatement> getConformanceStatements() {
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

	public Long getSegmentsId() {
		return segmentsId;
	}

	public void setSegmentsId(Long segmentsId) {
		this.segmentsId = segmentsId;
	}

	@Override
	public String toString() {
		return "Segment [id=" + id + "label=" + label + ", name=" + name
				+ ", description=" + description + "]";
	}

}
