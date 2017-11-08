package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.IntegrationTestApplicationConfig;
import nu.xom.Document;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
 * Created by Maxence Lefort on 12/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IntegrationTestApplicationConfig.class})
public class SerializationServiceTest {

    private static final String IG_DOCUMENT_TEST_ID = "57c8371a84ae6827fcec5488";

    @Autowired
    SerializationService serializationService;

    @Autowired
    IGDocumentService igDocumentService;

    @Test
    public void testSerializeCompactIGDocument(){
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        assertTrue(igDocument!=null);
        Document document = null;
        try {
            document =
                serializationService.serializeIGDocument(igDocument, SerializationLayout.IGDOCUMENT, ExportConfig
                    .getBasicExportConfig(true));
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        String xmlDocument = document.toXML();
        System.out.println(xmlDocument);
    }
    @Test
    public void testSerializeVerboseIGDocument(){
        IGDocument igDocument = igDocumentService.findById(IG_DOCUMENT_TEST_ID);
        assertTrue(igDocument!=null);
        Document document = null;
        try {
            document =
                serializationService.serializeIGDocument(igDocument, SerializationLayout.PROFILE, ExportConfig
                    .getBasicExportConfig(true));
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        String xmlDocument = document.toXML();
        System.out.println(xmlDocument);
    }
    @Test
    public void testSerializeDatatypeLibrary(){

    }
    @Test
    public void testSerializeElement(){

    }
}
