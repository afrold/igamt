package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Segment implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "SEGMENT_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.SegmentIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_segment"))
	@GeneratedValue(generator = "SEGMENT_ID_GENERATOR")
	protected String id;

	// TODO. Only for backward compatibility. Remove later
	protected String uuid;

	@NotNull
	@Column(nullable = false)
	protected String displayName;

	@OneToMany(mappedBy = "segment", cascade = CascadeType.ALL)
	protected List<Field> fields = new ArrayList<Field>();

	@NotNull
	@Column(nullable = false)
	protected String name;

	@Column(nullable = true)
	protected String description;

	@ManyToOne(fetch = FetchType.LAZY)
	protected Segments segments;
	
	//FIXME DynamicMapping is missing for Segment

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
		this.fields = fields;
	}

	public Segments getSegments() {
		return segments;
	}

	public void setSegments(Segments segments) {
		this.segments = segments;
	}

	public void addField(Field field) {
		fields.add(field);
		field.setSegment(this);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "Segment [id=" + id + ", uuid=" + uuid + ", displayName="
				+ displayName + ", fields=" + fields + ", name=" + name
				+ ", description=" + description + "]";
	}
	
	

}
