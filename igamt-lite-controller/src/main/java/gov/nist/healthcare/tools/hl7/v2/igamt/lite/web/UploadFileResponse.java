package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web;

public class UploadFileResponse {
	private String link;

	/**
	 * @param link
	 */
	public UploadFileResponse(String link) {
		super();
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
