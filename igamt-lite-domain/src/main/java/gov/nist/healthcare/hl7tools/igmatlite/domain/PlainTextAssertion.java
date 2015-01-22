package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class PlainTextAssertion extends Assertion {
	private String path;
	private String text;
	private boolean ignoreCase;
	
	public PlainTextAssertion() {
		super();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	
}
