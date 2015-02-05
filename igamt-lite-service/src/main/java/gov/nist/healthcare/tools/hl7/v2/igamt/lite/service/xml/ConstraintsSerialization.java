package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceContext;
import nu.xom.Document;

public interface ConstraintsSerialization {
	ConformanceContext deserializeXMLToConformanceContext(String xmlContents);
	
	ConformanceContext deserializeXMLToConformanceContext(Document xmlDoc);
	
	String serializeTableLibraryToXML(ConformanceContext conformanceContext);
	
	Document serializeTableLibraryToDoc(ConformanceContext conformanceContext);
}
