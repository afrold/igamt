package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.assertion;

public class StringListAssertion extends Assertion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2993825918981103718L;
	private String path;
	private String csv;
	public StringListAssertion() {
		super();
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	@Override
	public String toString() {
		return "StringListAssertion [path=" + path + ", csv=" + csv + "]";
	}
}
