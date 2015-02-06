package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Segment implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false)
	private String label;

	@OneToMany(mappedBy = "segment", cascade = CascadeType.ALL)
	@OrderColumn(name = "position", nullable = false)
	private final Set<Field> fields = new LinkedHashSet<Field>();

	@OneToMany(mappedBy = "segment", cascade = CascadeType.ALL)
	@OrderColumn(name = "position", nullable = false)
	private final Set<DynamicMapping> dynamicMappings = new LinkedHashSet<DynamicMapping>();

	@NotNull
	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Segments segments;

	// FIXME DynamicMapping is missing for Segment

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

	public void addField(Field field) {
		if (field.getSegment() != null) {
			throw new IllegalArgumentException("The field " + field.getName()
					+ " already belongs to segment "
					+ field.getSegment().getLabel());
		}
		fields.add(field);
		field.setSegment(this);
	}

	public void addDynamicMapping(DynamicMapping d) {
		if (d.getSegment() != null) {
			throw new IllegalArgumentException("The DynamicMapping "
					+ " already belongs to a different segment "
					+ d.getSegment().getLabel());
		}
		dynamicMappings.add(d);
		d.setSegment(this);
	}

	public Set<DynamicMapping> getDynamicMappings() {
		return dynamicMappings;
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
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
