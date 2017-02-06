package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
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
    DatatypeLibraryDocumentService datatypeLibraryDocumentService;

    @Autowired ExportService exportService;

    private static final String EXPORT_BASE_PATH = "test/dl_test";
    private static final String TEST_DOCUMENT_ID = "57b758a884aebc6c9d582cd3";

    @Test
    public void testHtmlExport(){
        DatatypeLibraryDocument datatypeLibraryDocument = datatypeLibraryDocumentService.findById(TEST_DOCUMENT_ID);
        try {
            //File htmlFile = new File("tmp/dtLib_"+new Date().toString()+".html");
            File htmlFile = new File(EXPORT_BASE_PATH+".html");
            if(htmlFile.exists()){
                htmlFile.delete();
            }
            if(htmlFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService.exportDatatypeLibraryDocumentAsHtml(datatypeLibraryDocument), htmlFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDocxExport(){
        DatatypeLibraryDocument datatypeLibraryDocument = datatypeLibraryDocumentService.findById(TEST_DOCUMENT_ID);
        try {
            File wordFile = new File(EXPORT_BASE_PATH+".docx");
            if(wordFile.exists()){
                wordFile.delete();
            }
            if(wordFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService.exportDatatypeLibraryDocumentAsDocx(datatypeLibraryDocument), wordFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
