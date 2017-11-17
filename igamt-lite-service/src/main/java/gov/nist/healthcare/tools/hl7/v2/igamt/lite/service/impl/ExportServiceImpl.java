package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportParameters;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ExportUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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
    private ApplicationContext applicationContext;

//    @Autowired
//    private SerializationService serializationService;

    @Autowired
    private ExportFontConfigService exportFontConfigService;

    @Override public InputStream exportIGDocumentAsDocx(IGDocument igDocument,
        SerializationLayout serializationLayout, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException {
            SerializationService serializationService = new SerializationServiceImpl(applicationContext);
            if (igDocument != null) {
                ExportParameters exportParameters = exportUtil.setExportParameters(
                    DOCUMENT_TITLE_IMPLEMENTATION_GUIDE, true, true, EXPORT_FORMAT_WORD, exportConfig, exportFontConfig);
                igDocument.getMetaData().setHl7Version(igDocument.getProfile().getMetaData().getHl7Version());
                return exportUtil.exportAsDocxFromXml(
                    serializationService.serializeIGDocument(igDocument, serializationLayout, exportConfig).toXML(),
                    GLOBAL_STYLESHEET, exportParameters,
                    igDocument.getMetaData(),igDocument.getDateUpdated());
            } else {
                return new NullInputStream(1L);
            }
        }

    @Override public InputStream exportIGDocumentAsHtml(IGDocument igDocument,
        SerializationLayout serializationLayout, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException {
        if (igDocument != null) {
            SerializationService serializationService = new SerializationServiceImpl(applicationContext);
        	ExportParameters exportParameters = exportUtil.setExportParameters(DOCUMENT_TITLE_IMPLEMENTATION_GUIDE,true,false,EXPORT_FORMAT_HTML, exportConfig, exportFontConfig);
            return exportUtil.exportAsHtmlFromXsl(serializationService.serializeIGDocument(igDocument,
                    serializationLayout, exportConfig).toXML(),
                GLOBAL_STYLESHEET, exportParameters,igDocument.getMetaData());
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override public InputStream exportDatatypeLibraryDocumentAsHtml(
        DatatypeLibraryDocument datatypeLibraryDocument, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException {
        if (datatypeLibraryDocument != null) {
            SerializationService serializationService = new SerializationServiceImpl(applicationContext);
        	ExportParameters exportParameters = exportUtil.setExportParameters(DOCUMENT_TITLE_DATATYPE_LIBRARY,true,false,EXPORT_FORMAT_HTML,exportConfig, exportFontConfig);
            return exportUtil.exportAsHtmlFromXsl(serializationService
                    .serializeDatatypeLibrary(datatypeLibraryDocument, exportConfig).toXML(),
                GLOBAL_STYLESHEET, exportParameters,datatypeLibraryDocument.getMetaData());
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override public InputStream exportDatatypeLibraryDocumentAsDocx(
        DatatypeLibraryDocument datatypeLibraryDocument, ExportConfig exportConfig, ExportFontConfig exportFontConfig)
        throws SerializationException {
        if (datatypeLibraryDocument != null) {
            SerializationService serializationService = new SerializationServiceImpl(applicationContext);
        	ExportParameters exportParameters = exportUtil.setExportParameters(DOCUMENT_TITLE_DATATYPE_LIBRARY,true,true,EXPORT_FORMAT_WORD, exportConfig, exportFontConfig);
            return exportUtil.exportAsDocxFromXml(serializationService
                    .serializeDatatypeLibrary(datatypeLibraryDocument, exportConfig).toXML(),
                GLOBAL_STYLESHEET, exportParameters, datatypeLibraryDocument.getMetaData(),datatypeLibraryDocument.getDateUpdated());
        } else {
            return new NullInputStream(1L);
        }
    }

    @Override
    public String exportDataModelAsHtml(Object dataModel, String title, String host)
        throws SerializationException {
        SerializationService serializationService = new SerializationServiceImpl(applicationContext);
        nu.xom.Document document = serializationService.serializeDataModel(dataModel, host);
        if(document!=null){
            try {
                ExportFontConfig exportFontConfig = exportFontConfigService.getDefaultExportFontConfig();
                ExportParameters exportParameters = exportUtil.setExportParameters(title, false, false, EXPORT_FORMAT_HTML, ExportConfig.getBasicExportConfig(true), exportFontConfig);
                return IOUtils.toString(exportUtil.exportAsHtmlFromXsl(document.toXML(), GLOBAL_STYLESHEET, exportParameters, null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
