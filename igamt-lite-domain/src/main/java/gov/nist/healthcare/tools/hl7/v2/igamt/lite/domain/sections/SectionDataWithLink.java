package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AbstractLink;

public class SectionDataWithLink extends SectionData {
	
	private SectionDataLink ref;


	public SectionDataWithLink() {
		super();
	}

	public SectionDataLink getRef() {
		return ref;
	}

	public void setRef(SectionDataLink ref) {
		this.ref = ref;
	}
	
}
