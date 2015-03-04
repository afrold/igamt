package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "SEGMENT")
public class Segment extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Segment() {
		super();
		type = Constant.SEGMENT;
	}

	@Column(name = "ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false, name = "LABEL")
	private String label;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@javax.persistence.JoinTable(name = "SEGMENT_FIELD", joinColumns = @JoinColumn(name = "SEGMENT"), inverseJoinColumns = @JoinColumn(name = "FIELD"))
	@OrderBy(value = "position")
	private Set<Field> fields = new HashSet<Field>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@javax.persistence.JoinTable(name = "SEGMENT_DYNAMICMAPPING", joinColumns = @JoinColumn(name = "SEGMENT"), inverseJoinColumns = @JoinColumn(name = "DYNAMICMAPPING"))
	@OrderBy(value = "position")
	private Set<DynamicMapping> dynamicMappings = new HashSet<DynamicMapping>();

	@NotNull
	@Column(nullable = false, name = "SEGMENT_NAME")
	private String name;

	@Column(nullable = true, name = "SEGMENT_DESC")
	private String description;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(name = "SEGMENT_PREDICATE", joinColumns = @JoinColumn(name = "SEGMENT"), inverseJoinColumns = @JoinColumn(name = "PREDICATE"))
	protected Set<Predicate> predicates = new HashSet<Predicate>();

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinTable(name = "SEGMENT_CONFSTATEMENT", joinColumns = @JoinColumn(name = "SEGMENT"), inverseJoinColumns = @JoinColumn(name = "CONFSTATEMENT"))
	protected Set<ConformanceStatement> conformanceStatements = new HashSet<ConformanceStatement>();

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEGMENTS_ID")
	private Segments segments;

	//
	// @NotNull
	// @Column(nullable = false,name="POSITION")
	// private Integer position = 0;

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

	public Set<Field> getFields() {
		return fields;
	}

	public void setFields(Set<Field> fields) {
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

	public Segments getSegments() {
		return segments;
	}

	public void setSegments(Segments segments) {
		this.segments = segments;
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

	@Override
	public String toString() {
		return "Segment [id=" + id + "label=" + label + ", fields=" + fields
				+ ", name=" + name + ", description=" + description + "]";
	}

}
