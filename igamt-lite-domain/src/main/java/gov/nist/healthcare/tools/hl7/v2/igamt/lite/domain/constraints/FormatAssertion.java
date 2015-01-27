package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class FormatAssertion extends Assertion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5904203671235654865L;
	private String path;
	private String regex;
	
	public FormatAssertion() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	
}
