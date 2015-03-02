package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
@Entity
@Table(name="SEGMENT")
public class Segment extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public Segment() {
		super();
		type = Constant.SEGMENT;
	}

	@Column(name="ID")
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

 	@NotNull
	@Column(nullable = false,name="LABEL")
	private String label;

 	@OneToMany(mappedBy = "segment",fetch = FetchType.EAGER, cascade=CascadeType.ALL)
 	@OrderBy(value="position")
	private final Set<Field> fields = new HashSet<Field>();

 	@OneToMany(mappedBy = "segment",fetch = FetchType.EAGER, cascade=CascadeType.ALL)
 	@OrderBy(value="position")
	private final Set<DynamicMapping> dynamicMappings = new HashSet<DynamicMapping>();
	
	@NotNull
 	@Column(nullable = false,name="SEGMENT_NAME")
	private String name;

 	@Column(nullable = true,name="SEGMENT_DESC")
	private String description;
	
  	@OneToMany(fetch = FetchType.EAGER)
  	@OrderBy(value="position")
 	@JoinTable(name = "SEGMENT_PREDICATE", joinColumns = @JoinColumn(name = "SEGMENT"), inverseJoinColumns = @JoinColumn(name = "PREDICATE"))
	protected Set<Constraint> predicates = new HashSet<Constraint>();

  	@OneToMany(fetch = FetchType.EAGER)
  	@OrderBy(value="position")
 	@JoinTable(name = "SEGMENT_CONFSTATEMENT", joinColumns = @JoinColumn(name = "SEGMENT"), inverseJoinColumns = @JoinColumn(name = "CONFSTATEMENT"))
	protected Set<Constraint> conformanceStatements = new HashSet<Constraint>();

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SEGMENTS_ID")
	private Segments segments; 


	@NotNull
	@Column(nullable = false,name="POSITION")
	private Integer position;
	

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

	public Segments getSegments() {
		return segments;
	}

	public void setSegments(Segments segments) {
		this.segments = segments;
	}
	
	
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void addPredicate(Constraint p) {
		p.setPosition(predicates.size() +1);
		predicates.add(p);
 	}
	
	public void addConformanceStatement(Constraint cs) {
		cs.setPosition(conformanceStatements.size() +1);
		conformanceStatements.add(cs);
 	}
	
	
	public void addField(Field field) {
		if (field.getSegment() != null) {
			throw new IllegalArgumentException("The field " + field.getName()
					+ " already belongs to segment "
					+ field.getSegment().getLabel());
		}
		field.setPosition(fields.size() +1);
		fields.add(field);
		field.setSegment(this);
	}

	public void addDynamicMapping(DynamicMapping d) {
		if (d.getSegment() != null) {
			throw new IllegalArgumentException("The DynamicMapping "
					+ " already belongs to a different segment "
					+ d.getSegment().getLabel());
		}
		d.setPosition(dynamicMappings.size() +1);
 		dynamicMappings.add(d);
		d.setSegment(this);
	}

	public Set<DynamicMapping> getDynamicMappings() {
		return dynamicMappings;
	}

	public Set<Constraint> getPredicates() {
		return predicates;
	}

	public void setPredicates(Set<Constraint> predicates) {
		this.predicates = predicates;
	}

	public Set<Constraint> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(Set<Constraint> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	@Override
	public String toString() {
		return "Segment [id=" + id + "label=" + label + ", fields=" + fields
				+ ", name=" + name + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		if (id == null) {
			//FIXME [Woo] Check this.
//			if (other.id != null) 
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
