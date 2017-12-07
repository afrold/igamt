package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;


import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;

public class SectionDataWithText extends SectionData{
	

	private Integer position;
	private String sectionTitle;
	private String referenceId;
	private String referenceType;
	private String sectionContent;
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getSectionTitle() {
		return sectionTitle;
	}
	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getReferenceType() {
		return referenceType;
	}
	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}
	public SectionDataWithText() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getSectionContent() {
		return sectionContent;
	}
	public void setSectionContent(String sectionContent) {
		this.sectionContent = sectionContent;
	}

	
}
