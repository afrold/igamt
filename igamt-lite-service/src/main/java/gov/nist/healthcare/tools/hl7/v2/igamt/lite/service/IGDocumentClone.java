package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;

import org.springframework.stereotype.Service;

@Service
public class IGDocumentClone {
	public IGDocument clone(IGDocument original) throws CloneNotSupportedException {
		return original.clone();

	}
}
