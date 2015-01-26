package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

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
