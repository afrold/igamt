package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;
import nu.xom.Document;

public interface TableSerialization {
	TableLibrary deserializeXMLToTableLibrary(String xmlContents);
	
	TableLibrary deserializeXMLToTableLibrary(Document xmlDoc);
	
	String serializeTableLibraryToXML(TableLibrary tableLibrary);
	
	Document serializeTableLibraryToDoc(TableLibrary tableLibrary);
}
