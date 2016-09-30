package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class BindingParametersForSegment {	
	private String segmentId;
	private String fieldId;
	private TableLink tableLink;
	private DatatypeLink datatypeLink;
	private String key;
	
	
	
	public DatatypeLink getDatatypeLink() {
		return datatypeLink;
	}

	public void setDatatypeLink(DatatypeLink datatypeLink) {
		this.datatypeLink = datatypeLink;
	}

	public String getSegmentId() {
		return segmentId;
	}
	
	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}
	
	public String getFieldId() {
		return fieldId;
	}
	
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	
	public TableLink getTableLink() {
		return tableLink;
	}
	
	public void setTableLink(TableLink tableLink) {
		this.tableLink = tableLink;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
