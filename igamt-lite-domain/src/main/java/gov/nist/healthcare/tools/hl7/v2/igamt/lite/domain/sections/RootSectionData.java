package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;


public class RootSectionData extends SectionData{
	
	DocumentMetaData data;
	
	public DocumentMetaData getMetaData() {
		return data;
	}

	public void setMetaData(DocumentMetaData metaData) {
		this.data= metaData;
	}

	public RootSectionData() {
		
		// TODO Auto-generated constructor stub
	}

}
