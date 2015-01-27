package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class SimpleValueAssertion extends Assertion{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4832620459321582705L;
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
