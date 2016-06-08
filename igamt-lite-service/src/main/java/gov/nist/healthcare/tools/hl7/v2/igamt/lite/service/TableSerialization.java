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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import nu.xom.Document;
import nu.xom.Element;

public interface TableSerialization {
  TableLibrary deserializeXMLToTableLibrary(String xmlContents);

  TableLibrary deserializeXMLToTableLibrary(Document xmlDoc);

  String serializeTableLibraryToXML(TableLibrary tableLibrary);

  String serializeTableLibraryToXML(Profile profile);

  String serializeTableLibraryToXML(DatatypeLibrary datatypeLibrary);

  Document serializeTableLibraryToDoc(TableLibrary tableLibrary);

  Document serializeTableLibraryToDoc(Profile profile);

  String serializeTableLibraryToGazelleXML(Profile profile);

  Document serializeTableLibraryToGazelleDoc(Profile profile);

  Element serializeTableLibraryToElement(Profile profile);

}
