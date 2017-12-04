package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFont;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.IntegrationTestApplicationConfig;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * Created by Maxence Lefort on 11/14/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})
public class IGDocumentExportTest {

    //57450d2ed4c6f57e6980e821

    @Autowired IGDocumentService igDocumentService;
    @Autowired ExportService exportService;
    @Autowired ExportConfigService exportConfigService;
    @Autowired ExportFontConfigService exportFontConfigService;

    private static final String IG_DOCUMENT_TEST_ID = "579a2fd784aeb9b94367d325";
    private static final String EXPORT_BASE_PATH = "test/ig_test";
    private static final String FONT = "\"Trebuchet MS\", Helvetica, sans-serif";

    private ExportFontConfig exportFontConfig = new ExportFontConfig(new ExportFont(FONT, FONT+";"), 14, false);

    @Test
    public void testHtmlCompactExport() throws SerializationException{
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        try {
            //File htmlFile = new File("tmp/dtLib_"+new Date().toString()+".html");
            ExportConfig exportConfig = ExportConfig.getBasicExportConfig(true);
            File htmlFile = new File(EXPORT_BASE_PATH+"_compact.html");
            if(htmlFile.exists()){
                htmlFile.delete();
            }
            if(htmlFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService
                    .exportIGDocumentAsHtml(igDocument, SerializationLayout.IGDOCUMENT,exportConfig,exportFontConfig), htmlFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testHtmlVerboseExport() throws SerializationException{
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        try {
            ExportConfig exportConfig = ExportConfig.getBasicExportConfig(true);
            //File htmlFile = new File("tmp/dtLib_"+new Date().toString()+".html");
            File htmlFile = new File(EXPORT_BASE_PATH+"_verbose.html");
            if(htmlFile.exists()){
                htmlFile.delete();
            }
            if(htmlFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService
                    .exportIGDocumentAsHtml(igDocument, SerializationLayout.PROFILE,exportConfig,exportFontConfig), htmlFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHtmlTablesExport() throws SerializationException{
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        try {
            ExportConfig exportConfig = ExportConfig.getBasicExportConfig(true);
            //File htmlFile = new File("tmp/dtLib_"+new Date().toString()+".html");
            File htmlFile = new File(EXPORT_BASE_PATH+"_table.html");
            if(htmlFile.exists()){
                htmlFile.delete();
            }
            if(htmlFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService
                    .exportIGDocumentAsHtml(igDocument, SerializationLayout.TABLES,exportConfig,exportFontConfig), htmlFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDocxCompactExport() throws SerializationException{
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        try {
            File wordFile = new File(EXPORT_BASE_PATH+"_compact.docx");
            ExportConfig exportConfig = ExportConfig.getBasicExportConfig(true);
            if(wordFile.exists()){
                wordFile.delete();
            }
            if(wordFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService
                    .exportIGDocumentAsDocx(igDocument, SerializationLayout.IGDOCUMENT,exportConfig,this.exportFontConfig), wordFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDocxVerboseExport() throws SerializationException{
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        try {
            File wordFile = new File(EXPORT_BASE_PATH+"_verbose.docx");
            ExportConfig exportConfig = ExportConfig.getBasicExportConfig(true);
            if(wordFile.exists()){
                wordFile.delete();
            }
            if(wordFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService.exportIGDocumentAsDocx(igDocument,
                    SerializationLayout.PROFILE,exportConfig,exportFontConfig), wordFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testDocxTablesExport() throws SerializationException{
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        try {
            File wordFile = new File(EXPORT_BASE_PATH+"_table.docx");
            ExportConfig exportConfig = ExportConfig.getBasicExportConfig(true);
            if(wordFile.exists()){
                wordFile.delete();
            }
            if(wordFile.createNewFile()) {
                FileUtils.copyInputStreamToFile(exportService.exportIGDocumentAsDocx(igDocument,
                    SerializationLayout.TABLES,exportConfig,exportFontConfig), wordFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
