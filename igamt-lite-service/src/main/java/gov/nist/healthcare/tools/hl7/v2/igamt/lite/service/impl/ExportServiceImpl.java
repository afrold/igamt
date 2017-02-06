package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportParameters;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

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
public class ExportServiceImpl implements ExportService {

    private static String DOCUMENT_TITLE_IMPLEMENTATION_GUIDE = "Implementation Guide";
    private static String DOCUMENT_TITLE_DATATYPE_LIBRARY = "Datatype Library";
    private static String EXPORT_FORMAT_HTML = "html";
    private static String EXPORT_FORMAT_WORD = "word";
    private static String GLOBAL_STYLESHEET = "/rendering/generalExport.xsl";

    Logger logger = LoggerFactory.getLogger(ExportServiceImpl.class);


    @Autowired
    private ExportUtil exportUtil;

    @Autowired
    private IGDocumentSerialization igDocumentSerializationService;

    @Autowired
    private SerializationService serializationService;

    @Autowired
    private ProfileSerialization profileSerializationService;

    @Override public InputStream exportIGDocumentAsDocx(IGDocument igDocument,
        SerializationLayout serializationLayout) throws IOException {
        if (igDocument != null) {
            ExportParameters exportParameters = exportUtil.setExportParameters(
                DOCUMENT_TITLE_IMPLEMENTATION_GUIDE, true, true, EXPORT_FORMAT_WORD);
            igDocument.getMetaData().setHl7Version(igDocument.getProfile().getMetaData().getHl7Version());
            return exportUtil.exportAsDocxFromXml(
                serializationService.serializeIGDocument(igDocument, serializationLayout).toXML(),
                GLOBAL_STYLESHEET, exportParameters,
                igDocument.getMetaData(),igDocument.getDateUpdated());
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override public InputStream exportIGDocumentAsHtml(IGDocument igDocument,
        SerializationLayout serializationLayout) throws IOException {
        if (igDocument != null) {
            ExportParameters exportParameters = exportUtil.setExportParameters(DOCUMENT_TITLE_IMPLEMENTATION_GUIDE,true,false,EXPORT_FORMAT_HTML);
            return exportUtil.exportAsHtmlFromXsl(serializationService.serializeIGDocument(igDocument,
                    serializationLayout).toXML(),
                GLOBAL_STYLESHEET, exportParameters,igDocument.getMetaData());
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override public InputStream exportIGDocumentAsXml(IGDocument d) throws IOException {
        if (d != null) {
            return IOUtils.toInputStream(
                profileSerializationService.serializeProfileToXML(d.getProfile(), d.getMetaData(),d.getDateUpdated()));
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override public InputStream exportDatatypeLibraryDocumentAsHtml(
        DatatypeLibraryDocument datatypeLibraryDocument) {
        if (datatypeLibraryDocument != null) {
            ExportParameters exportParameters = exportUtil.setExportParameters(DOCUMENT_TITLE_DATATYPE_LIBRARY,true,false,EXPORT_FORMAT_HTML);
            return exportUtil.exportAsHtmlFromXsl(serializationService
                    .serializeDatatypeLibrary(datatypeLibraryDocument).toXML(),
                GLOBAL_STYLESHEET, exportParameters,datatypeLibraryDocument.getMetaData());
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override public InputStream exportDatatypeLibraryDocumentAsDocx(
        DatatypeLibraryDocument datatypeLibraryDocument) {
        if (datatypeLibraryDocument != null) {
            ExportParameters exportParameters = exportUtil.setExportParameters(DOCUMENT_TITLE_DATATYPE_LIBRARY,true,true,EXPORT_FORMAT_WORD);
            return exportUtil.exportAsDocxFromXml(serializationService
                    .serializeDatatypeLibrary(datatypeLibraryDocument).toXML(),
                GLOBAL_STYLESHEET, exportParameters, datatypeLibraryDocument.getMetaData(),datatypeLibraryDocument.getDateUpdated());
        } else {
            return new NullInputStream(1L);
        }
    }
}
