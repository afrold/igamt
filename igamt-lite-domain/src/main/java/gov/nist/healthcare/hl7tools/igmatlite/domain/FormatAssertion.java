package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class FormatAssertion extends Assertion {
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
