package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.sections;

import com.fasterxml.jackson.annotation.JsonTypeName;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AbstractLink;

@JsonTypeName("withLink")

public class SectionDataWithLink<T extends AbstractLink> extends SectionData {
	
	private T ref;


	public SectionDataWithLink() {
		// TODO Auto-generated constructor stub
		super();
	}

	public T getRef() {
		return ref;
	}

	public void setRef(T ref) {
		this.ref = ref;
	}
	
}
