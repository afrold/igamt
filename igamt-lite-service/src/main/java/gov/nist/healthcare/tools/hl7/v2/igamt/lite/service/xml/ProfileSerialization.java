package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import nu.xom.Document;

public interface ProfileSerialization {
	Profile deserializeXMLToProfile(String xmlContentsProfile, String xmlValueSet, String xmlConstraints);
	
	Profile deserializeXMLToProfile(Document docProfile, Document docValueSet, Document docConstraints);
	
	String serializeProfileToXML(Profile profile);
	
	Document serializeProfileToDoc(Profile profile);
}
