package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSerialization;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

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
 * Created by Maxence Lefort on 9/26/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class DataTypeLibraryExportTest  {

    Logger logger = LoggerFactory.getLogger(DataTypeLibraryExportTest.class);

    @Autowired
    IGDocumentSerialization igDocumentSerialization;

    @Autowired
    DatatypeLibraryDocumentService datatypeLibraryDocumentService;

    @Autowired
    IGDocumentExportService igDocumentExportService;

    @Test
    public void testXMLExport(){
        DatatypeLibraryDocument datatypeLibraryDocument = datatypeLibraryDocumentService.findById("57dbf3a6d4c6e51ff8736886");
        String xml = igDocumentSerialization.serializeDatatypeLibraryDocumentToXML(datatypeLibraryDocument);
        logger.info("Generated XML: "+xml);
        try {
            xml = IOUtils.toString(igDocumentExportService.exportAsXmlDatatypeLibraryDocument(datatypeLibraryDocument));
            logger.info("Generated XML (InputStream): "+xml);
            String html = IOUtils.toString(igDocumentExportService.exportAsHtmlDatatypeLibraryDocument(datatypeLibraryDocument));
            logger.info("Generated HTML: "+html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
