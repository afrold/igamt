package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class NumberListAssertion extends Assertion {
	private String path;
	private String csv;
	public NumberListAssertion() {
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
	
	
}
