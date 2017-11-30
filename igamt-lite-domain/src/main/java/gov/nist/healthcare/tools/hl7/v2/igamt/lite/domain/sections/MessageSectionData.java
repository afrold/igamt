package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import com.fasterxml.jackson.annotation.JsonTypeName;
@JsonTypeName("message")
public class MessageSectionData extends SectionData{
	
	String name;
	String identifier;
	String messageType;
	String description;
	String structID;
	

	public MessageSectionData() {
		// TODO Auto-generated constructor stub
	}


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

}
