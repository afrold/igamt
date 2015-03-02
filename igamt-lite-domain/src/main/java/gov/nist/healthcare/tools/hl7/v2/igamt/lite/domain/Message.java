package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="MESSAGE")
public class Message implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false, name="TYPE")
	private String type;
	@NotNull
	@Column(nullable = false, name="EVENT")
	private String event;

	@NotNull
	@Column(nullable = false, name="STRUCTID")
	private String structID;

	@Column(nullable = true, name="MESSAGE_DESC")
	private String description;

	@JsonProperty("children")
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy(value="position")
	private Set<SegmentRefOrGroup> segmentRefOrGroups = new HashSet<SegmentRefOrGroup>();

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MESSAGES_ID")
	private Messages messages; 
	
	@NotNull
	@Column(nullable = false,name="POSITION")
	protected Integer position;
	

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

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public Set<SegmentRefOrGroup> getSegmentRefOrGroups() {
		return segmentRefOrGroups;
	}
	
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	} 
	
	
	public void addChild(SegmentRefOrGroup e) { 
		e.setPosition(segmentRefOrGroups.size()+1);
		segmentRefOrGroups.add(e);
	}


	@Override
	public String toString() {
		return "Message [id=" + id + ", type=" + type + ", event=" + event
				+ ", structID=" + structID + ", description=" + description
				+ ", segmentRefOrGroups=" + segmentRefOrGroups + "]";
	}

}
