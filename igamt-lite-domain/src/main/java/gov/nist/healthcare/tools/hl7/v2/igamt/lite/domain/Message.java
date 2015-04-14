package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message")
public class Message extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Message() {
		super();
		this.type = Constant.MESSAGE;
		this.id = ObjectId.get().toString();
	}

	@Id
	private String id;

	private String identifier;

	// @NotNull
	private String messageType;

	// @NotNull
	private String event;

	// @NotNull
	private String structID;

	// @Column(nullable = true, name = "MESSAGE_DESC")
	private String description;

	private String version;

	private String date;

	private String oid;

	private List<SegmentRefOrGroup> children = new ArrayList<SegmentRefOrGroup>();

	// @DBRef
	// private Messages messages;

	// @NotNull
	protected Integer position = 0;

	protected String comment;

	protected String usageNote;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	//
	// public Messages getMessages() {
	// return messages;
	// }
	//
	// public void setMessages(Messages messages) {
	// this.messages = messages;
	// }

	public List<SegmentRefOrGroup> getChildren() {
		return children;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void addSegmentRefOrGroup(SegmentRefOrGroup e) {
		e.setPosition(children.size() + 1);
		this.children.add(e);
	}

	public void setChildren(List<SegmentRefOrGroup> children) {
		this.children = new ArrayList<SegmentRefOrGroup>();
		for (SegmentRefOrGroup child : children) {
			addSegmentRefOrGroup(child);
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
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

	public Boolean deleteSegmentRefOrGroup(String id) {
		if (this.getChildren() != null) {
			for (int i = 0; i < this.getChildren().size(); i++) {
				SegmentRefOrGroup m = this.getChildren().get(i);
				if (m.getId().equals(id)) {
					return this.getChildren().remove(m);
				} else if (m instanceof Group) {
					Group gr = (Group) m;
					Boolean result = gr.deleteSegmentRefOrGroup(id);
					if (result) {
						return result;
					}
				}
			}
		}
		return false;
	}

	public SegmentRefOrGroup findOneSegmentRefOrGroup(String id) {
		if (this.getChildren() != null) {
			for (SegmentRefOrGroup m : this.getChildren()) {
				if (m instanceof SegmentRef) {
					if (m.getId().equals(id)) {
						return m;
					}
				} else if (m instanceof Group) {
					Group gr = (Group) m;
					SegmentRefOrGroup tmp = gr.findOneSegmentRefOrGroup(id);
					if (tmp != null) {
						return tmp;
					}
				}
			}
		}
		return null;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", type=" + messageType + ", event="
				+ event + ", structID=" + structID + ", description="
				+ description + "]";
	}

}
