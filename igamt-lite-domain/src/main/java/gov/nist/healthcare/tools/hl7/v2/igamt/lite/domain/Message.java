package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class Message implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@NotNull
	@Column(nullable = false)
	protected String type;
	@NotNull
	@Column(nullable = false)
	protected String event;

	@NotNull
	@Column(nullable = false)
	protected String structID;

	@Column(nullable = true)
	protected String description;

	@OneToMany(cascade = CascadeType.ALL)
	protected List<SegmentRefOrGroup> segmentRefOrGroups;

	@ManyToOne(fetch = FetchType.LAZY)
	protected Messages messages;

	// TODO. Only for backward compatibility. Remove later
	protected String uuid;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getStructID() {
		return structID;
	}

	public void setStructID(String structID) {
		this.structID = structID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SegmentRefOrGroup> getSegmentRefOrGroups() {
		return segmentRefOrGroups;
	}

	public void setSegmentRefOrGroups(List<SegmentRefOrGroup> segmentRefOrGroups) {
		this.segmentRefOrGroups = segmentRefOrGroups;
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
