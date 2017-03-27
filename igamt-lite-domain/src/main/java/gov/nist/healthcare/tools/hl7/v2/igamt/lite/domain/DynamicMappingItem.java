package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class DynamicMappingItem {

	private String firstReferenceValue;
	private String secondReferenceValue;
	private String datatypeId;

	public DynamicMappingItem() {
		super();
	}

	public String getFirstReferenceValue() {
		return firstReferenceValue;
	}

	public void setFirstReferenceValue(String firstReferenceValue) {
		this.firstReferenceValue = firstReferenceValue;
	}

	public String getSecondReferenceValue() {
		return secondReferenceValue;
	}

	public void setSecondReferenceValue(String secondReferenceValue) {
		this.secondReferenceValue = secondReferenceValue;
	}

	public String getDatatypeId() {
		return datatypeId;
	}

	public void setDatatypeId(String datatypeId) {
		this.datatypeId = datatypeId;
	}

}
