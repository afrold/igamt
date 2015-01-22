package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class PathValueAssertion extends Assertion {
	private String path1;
	private String path2;
	private String operator;
	
	public PathValueAssertion() {
		super();
	}
	
	
	public String getPath1() {
		return path1;
	}
	public void setPath1(String path1) {
		this.path1 = path1;
	}
	public String getPath2() {
		return path2;
	}
	public void setPath2(String path2) {
		this.path2 = path2;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
}
