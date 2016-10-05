package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class BindingParametersForMessage {
	private String messageId;
	private SegmentLink newSegmentLink;
	private String positionPath;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public SegmentLink getNewSegmentLink() {
		return newSegmentLink;
	}

	public void setNewSegmentLink(SegmentLink newSegmentLink) {
		this.newSegmentLink = newSegmentLink;
	}

	public String getPositionPath() {
		return positionPath;
	}

	public void setPositionPath(String positionPath) {
		this.positionPath = positionPath;
	}

}
