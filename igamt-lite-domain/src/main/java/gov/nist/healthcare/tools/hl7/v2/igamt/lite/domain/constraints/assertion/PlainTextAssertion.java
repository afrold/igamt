package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion;

public class PlainTextAssertion extends Assertion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8181670374107150272L;
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

	@Override
	public String toString() {
		return "PlainTextAssertion [path=" + path + ", text=" + text
				+ ", ignoreCase=" + ignoreCase + "]";
	}
	
	
}
