package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.io.InputStream;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;

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
 * <p>
 * Created by Maxence Lefort on 11/01/16.
 */
@Service
public interface ExportService {

    InputStream exportIGDocumentAsDocx(IGDocument igDocument, SerializationLayout serializationLayout, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException;
    InputStream exportIGDocumentAsHtml(IGDocument igDocument, SerializationLayout serializationLayout, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException;
    InputStream exportDatatypeLibraryDocumentAsHtml(DatatypeLibraryDocument datatypeLibraryDocument, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException;
    InputStream exportDatatypeLibraryDocumentAsDocx(DatatypeLibraryDocument datatypeLibraryDocument, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException;
	String exportDataModelAsHtml(Object dataModel, String title, String host)
      throws SerializationException;

}
