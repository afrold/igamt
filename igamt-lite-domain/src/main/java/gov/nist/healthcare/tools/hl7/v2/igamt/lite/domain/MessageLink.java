package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class MessageLink extends AbstractLink implements Comparable<MessageLink> {

	private String name;

	String identifier;
	String messageType;
	String structID;
	Integer position;
	
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public MessageLink() {
		this.type=Constant.MESSAGELINK;
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


	public String getStructID() {
		return structID;
	}
	public void setStructID(String structID) {
		this.structID = structID;
	}
	@Override
	public int compareTo(MessageLink o) {
		// TODO Auto-generated method stub
		return this.position-o.position;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
