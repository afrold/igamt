package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Iterator;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "MESSAGE")
public class Message extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Message() {
		super();
		this.type = Constant.MESSAGE;
	}

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "MESSAGE_IDENTIFIER")
	private Long identifier;

	@NotNull
	@Column(nullable = false, name = "TYPE")
	private String messageType;
	@NotNull
	@Column(nullable = false, name = "EVENT")
	private String event;

	@NotNull
	@Column(nullable = false, name = "STRUCTID")
	private String structID;

	@Column(nullable = true, name = "MESSAGE_DESC")
	private String description;

	@JsonProperty("children")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	// @org.hibernate.annotations.OrderBy(clause = "position asc")
	private Set<SegmentRefOrGroup> segmentRefOrGroups = new LinkedHashSet<SegmentRefOrGroup>();

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MESSAGES_ID")
	private Messages messages;

	@NotNull
	@Column(nullable = false, name = "MESSAGE_POSITION")
	protected Integer position = 0;

	@Column(name = "COMMENT", columnDefinition = "TEXT")
	protected String comment;

	@Column(name = "USAGE_NOTE", columnDefinition = "TEXT")
	protected String usageNote;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
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

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public Set<SegmentRefOrGroup> getSegmentRefOrGroups() {
		return segmentRefOrGroups;
	}

	public void setSegmentRefOrGroups(Set<SegmentRefOrGroup> segmentRefOrGroups) {
		if (segmentRefOrGroups != null) {
			this.segmentRefOrGroups.clear();
			Iterator<SegmentRefOrGroup> it = segmentRefOrGroups.iterator();
			while (it.hasNext()) {
				addSegmentRefOrGroup(it.next());
			}
		} else {
			this.segmentRefOrGroups = null;
		}
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void addSegmentRefOrGroup(SegmentRefOrGroup e) {
		e.setPosition(segmentRefOrGroups.size() + 1);
		segmentRefOrGroups.add(e);
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
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
		return "Message [id=" + id + ", type=" + messageType + ", event="
				+ event + ", structID=" + structID + ", description="
				+ description + "]";
	}

}
