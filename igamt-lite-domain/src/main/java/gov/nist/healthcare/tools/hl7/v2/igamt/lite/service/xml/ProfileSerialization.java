package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;

public interface ProfileSerialization {
	Profile deserializeXMLToProfile(String xmlContents);
	
	String serializeProfileToXML(Profile profile);
}
