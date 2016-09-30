package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class BindingParametersForDatatype {	
	private String datatypeId;
	private String componentId;
	private TableLink tableLink;
	private DatatypeLink datatypeLink;
	private String key;

	public DatatypeLink getDatatypeLink() {
		return datatypeLink;
	}

	public void setDatatypeLink(DatatypeLink datatypeLink) {
		this.datatypeLink = datatypeLink;
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

	public String getDatatypeId() {
		return datatypeId;
	}

	public void setDatatypeId(String datatypeId) {
		this.datatypeId = datatypeId;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
}
