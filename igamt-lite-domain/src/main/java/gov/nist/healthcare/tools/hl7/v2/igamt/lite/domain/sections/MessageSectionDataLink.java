package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

public class MessageSectionDataLink extends SectionDataLink {
	
	private String name;
	private String identifier;
	private String messageType;
	private String description;
	private String structID;
	private String messageId;
	private Integer position;
	private String ext;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStructID() {
		return structID;
	}
	public void setStructID(String structID) {
		this.structID = structID;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	

	

}
