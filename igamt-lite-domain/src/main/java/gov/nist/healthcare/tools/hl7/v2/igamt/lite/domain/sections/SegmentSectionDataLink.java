package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;

public class SegmentSectionDataLink extends SectionDataLink {
	private String name;
	private String description;
	private String hl7Version;
	private SCOPE scope;
	private Integer numberOfChildren;
	private String ext; 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHl7Version() {
		return hl7Version;
	}
	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}
	public SCOPE getScope() {
		return scope;
	}
	public void setScope(SCOPE scope) {
		this.scope = scope;
	}
	public Integer getNumberOfChildren() {
		return numberOfChildren;
	}
	public void setNumberOfChildren(Integer numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	
	
	
	
	

}
