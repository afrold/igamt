package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.clone;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;

public class DatatypeClone {	
	public Datatype clone(Datatype original) throws CloneNotSupportedException{
		return original.clone();
	}
}
