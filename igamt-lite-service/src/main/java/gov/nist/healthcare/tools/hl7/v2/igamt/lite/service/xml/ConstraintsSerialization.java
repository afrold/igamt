package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceContext;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

public interface ConstraintsSerialization {
	ConformanceContext deserializeXMLToConformanceContext(String xmlContents);
	
	String serializeTableLibraryToXML(TableLibrary tableLibrary);
}
