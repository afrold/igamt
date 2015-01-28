package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion;


public class PresenceAssertion extends Assertion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4116237163848338922L;
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

	@Override
	public String toString() {
		return "PresenceAssertion [path=" + path + "]";
	}
}
