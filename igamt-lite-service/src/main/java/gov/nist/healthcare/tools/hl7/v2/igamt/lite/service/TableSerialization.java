/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.Date;
import java.util.Map;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import nu.xom.Document;
import nu.xom.Element;

public interface TableSerialization {
	TableLibrary deserializeXMLToTableLibrary(String xmlContents, String hl7Version);

	TableLibrary deserializeXMLToTableLibrary(Document xmlDoc, String hl7Version);

	String serializeTableLibraryToXML(TableLibrary tableLibrary, DocumentMetaData metadata, Date dateUpdated);

	String serializeTableLibraryToXML(Profile profile, DocumentMetaData metadata, Date dateUpdated);
	
	String serializeTableLibraryUsingMapToXML(Profile profile, DocumentMetaData metadata, Map<String, Table> tablesMap, Date dateUpdated);

	String serializeTableLibraryToXML(DatatypeLibrary datatypeLibrary);

	Document serializeTableLibraryToDoc(TableLibrary tableLibrary, DocumentMetaData metadata, Date dateUpdated);

	Document serializeTableLibraryToDoc(Profile profile, DocumentMetaData metadata, Date dateUpdated);

	String serializeTableLibraryToGazelleXML(Profile profile);

	Document serializeTableLibraryToGazelleDoc(Profile profile);

	Element serializeTableLibraryToElement(Profile profile, DocumentMetaData metadata, Date dateUpdated);


}
