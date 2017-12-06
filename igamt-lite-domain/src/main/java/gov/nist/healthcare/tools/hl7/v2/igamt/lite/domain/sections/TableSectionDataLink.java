package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;

public class TableSectionDataLink extends SectionDataLink {
	private String bindingIdentifier;
	private String name;
	private SCOPE scope;
	private STATUS status;
	private String description;
	private Integer numberOfCodes;
	
	public String getBindingIdentifier() {
		return bindingIdentifier;
	}
	public void setBindingIdentifier(String bindingIdentifier) {
		this.bindingIdentifier = bindingIdentifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SCOPE getScope() {
		return scope;
	}
	public void setScope(SCOPE scope) {
		this.scope = scope;
	}
	public STATUS getStatus() {
		return status;
	}
	public void setStatus(STATUS status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getNumberOfCodes() {
		return numberOfCodes;
	}
	public void setNumberOfCodes(Integer numberOfCodes) {
		this.numberOfCodes = numberOfCodes;
	}
	public TableSectionDataLink() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}
