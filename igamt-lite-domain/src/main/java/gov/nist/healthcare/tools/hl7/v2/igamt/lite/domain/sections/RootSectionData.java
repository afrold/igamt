package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;

@JsonTypeName("root")

public class RootSectionData extends SectionData{
	
	DocumentMetaData metaData;
	
	public DocumentMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DocumentMetaData metaData) {
		this.metaData = metaData;
	}

	public RootSectionData() {
		
		// TODO Auto-generated constructor stub
	}

}
