package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AbstractLink;

@JsonTypeName("withLink")

public class SectionDataWithLink extends SectionData {
	
	private AbstractLink ref;


	public SectionDataWithLink() {
		// TODO Auto-generated constructor stub
		super();
	}

	public AbstractLink getRef() {
		return ref;
	}

	public void setRef(AbstractLink ref) {
		this.ref = ref;
	}
	
}
