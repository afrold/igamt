package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class PresenceAssertion extends Assertion {
	private String path;

	
	public PresenceAssertion() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
