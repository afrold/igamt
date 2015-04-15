package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ConstraintsSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.TableSerializationImpl;
import nu.xom.Document;

import org.springframework.stereotype.Service;

@Service
public class ProfileClone {
	public Profile clone(Profile original) throws CloneNotSupportedException {
		return original.clone();

	}
}
