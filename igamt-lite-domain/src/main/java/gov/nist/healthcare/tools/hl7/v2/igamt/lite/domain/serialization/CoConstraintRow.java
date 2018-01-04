package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

public class CoConstraintRow {

	public CoConstraintRow() {
		super();

		 this.colspan=1;

		// TODO Auto-generated constructor stub
	}
	private int colspan=1;

	private String key;
	private String value;
	private String type;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
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
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	
}
