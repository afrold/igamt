package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "composite-message")

public class CompositeMessage extends DataModelWithConstraints implements java.io.Serializable, Cloneable,
Comparable<Message> {

	
	 private static final long serialVersionUID = 1L;

	  public CompositeMessage() {
	    super();
	    this.type = Constant.COMPOSITEMESSAGE;
	  }
	  @Id
	  private String id;

	  private String identifier; // Message/@Identifier

	  private String messageID;

	  private String name; // Message/@Name

	  private String messageType; // Message/@Type

	  private String event; // Message/@Event

	  private String structID; // Message/@StructID

	  private String description; // Message/@Description
	  private List<ApplyInfo> appliedPcs;
	  private List<SegmentOrGroupLink> children = new ArrayList<SegmentOrGroupLink>();

	  protected Integer position = 0;

	  protected String comment = "";

	  protected String usageNote = "";
	  
	  
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getIdentifier() {
		return identifier;
	}


	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public String getMessageID() {
		return messageID;
	}


	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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


	public List<ApplyInfo> getAppliedPcs() {
		return appliedPcs;
	}


	public void setAppliedPcs(List<ApplyInfo> appliedPcs) {
		this.appliedPcs = appliedPcs;
	}


	public List<SegmentOrGroupLink> getChildren() {
		return children;
	}


	public void setChildren(List<SegmentOrGroupLink> children) {
		this.children = children;
	}


	public Integer getPosition() {
		return position;
	}


	public void setPosition(Integer position) {
		this.position = position;
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
	public int compareTo(Message o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
