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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;

import java.io.IOException;
import java.io.InputStream;

public interface IGDocumentExportService {

  InputStream exportAsPdf(IGDocument d) throws IOException, SerializationException;

  InputStream exportAsValidationForSelectedMessages(IGDocument d, String[] mids) throws IOException, CloneNotSupportedException, SerializationException;
  
  InputStream exportAsGazelleForSelectedMessages(IGDocument d, String[] mids) throws IOException, CloneNotSupportedException;

  InputStream exportAsDisplayForSelectedMessage(IGDocument d, String[] mids) throws IOException, CloneNotSupportedException;
  
  InputStream exportAsDisplayForSelectedCompositeProfiles(IGDocument d, String[] mids) throws IOException, CloneNotSupportedException;
  
  InputStream exportAsValidationForSelectedCompositeProfiles(IGDocument d, String[] cids) throws IOException, CloneNotSupportedException;

  InputStream exportAsGazelleForSelectedCompositeProfiles(IGDocument d, String[] cids) throws IOException, CloneNotSupportedException;

  InputStream exportAsXlsx(IGDocument d) throws IOException;

  InputStream exportAsXmlDisplay(IGDocument d) throws IOException, SerializationException;

  InputStream exportAsXmlSegment(SegmentLink sl) throws IOException;

  InputStream exportAsXmlDatatype(DatatypeLink dl) throws IOException;

  InputStream exportAsXmlTable(TableLink tl) throws IOException;

  InputStream exportAsDocx(IGDocument d) throws IOException;

  InputStream exportAsDocxSegment(SegmentLink sl) throws IOException;

  InputStream exportAsDocxDatatypes(IGDocument d) throws IOException;

  InputStream exportAsDocxDatatype(DatatypeLink dl) throws IOException;

  InputStream exportAsDocxTable(TableLink tl) throws IOException;

  InputStream exportAsHtml(IGDocument d) throws IOException, SerializationException;
  
  InputStream exportAsHtmlSegment(SegmentLink sl) throws IOException;

  InputStream exportAsHtmlDatatypes(IGDocument d) throws IOException;

  InputStream exportAsHtmlDatatype(DatatypeLink dl) throws IOException;
  
  InputStream exportAsHtmlTable(TableLink tl) throws IOException;
  
  InputStream exportAsHtmlSections(IGDocument ig);
  
  InputStream exportAsDocxSections(IGDocument ig);

  InputStream exportAsHtmlDatatypeLibraryDocument(DatatypeLibraryDocument datatypeLibraryDocument);

  InputStream exportAsDocxDatatypeLibraryDocument(DatatypeLibraryDocument datatypeLibraryDocument);


}
