package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class ShareParticipant {
	
	private String username;
	private String fullname;
	
	public ShareParticipant(String username, String fullname) {
		super();
		this.username = username;
		this.fullname = fullname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
}
