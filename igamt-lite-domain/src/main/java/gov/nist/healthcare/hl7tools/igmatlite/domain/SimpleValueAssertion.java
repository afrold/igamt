package gov.nist.healthcare.hl7tools.igmatlite.domain;

public class SimpleValueAssertion {
	private String path;
	private String operator;
	private String value;
	private String type;
	public SimpleValueAssertion() {
		super();
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
