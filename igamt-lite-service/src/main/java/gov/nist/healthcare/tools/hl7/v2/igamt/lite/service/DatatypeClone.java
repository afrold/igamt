package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;

import org.springframework.stereotype.Service;

@Service
public class DatatypeClone {
	public Datatype clone(Datatype original) throws CloneNotSupportedException {
		return original.clone();
	}
}
