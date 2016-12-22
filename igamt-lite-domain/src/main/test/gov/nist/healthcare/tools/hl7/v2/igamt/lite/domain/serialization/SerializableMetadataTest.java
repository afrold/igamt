package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import nu.xom.Element;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

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
public class SerializableMetadataTest {

    private static final String documentMetadataTitle = "aDocumentMetadataTitle";
    private static final String documentMetadataSubtitle = "aDocumentMetadataSubtitle";
    private static final String documentMetadataExt = "aDocumentMetadataExt";
    private static final String documentMetadataVersion = "1.0.2";
    private static final String profileMetadataOrgName = "profileMetadataOrgName";
    private static final String profileMetadataStatus = "profileMetadataStatus";
    private static final String profileMetadataTopics = "profileMetadataTopics";
    private static final String profileMetadataHL7Version = "1.4.3";

    @Test
    public void testSerialize(){
        DocumentMetaData documentMetaData = new DocumentMetaData();
        documentMetaData.setTitle(documentMetadataTitle);
        documentMetaData.setSubTitle(documentMetadataSubtitle);
        documentMetaData.setVersion(documentMetadataVersion);
        documentMetaData.setExt(documentMetadataExt);
        Date dateUpdated = new Date();
        ProfileMetaData profileMetaData = new ProfileMetaData();
        profileMetaData.setOrgName(profileMetadataOrgName);
        profileMetaData.setStatus(profileMetadataStatus);
        profileMetaData.setTopics(profileMetadataTopics);
        profileMetaData.setHl7Version(profileMetadataHL7Version);
        SerializableMetadata serializableMetadata = new SerializableMetadata(documentMetaData,profileMetaData,dateUpdated);
        Element element = serializableMetadata.serializeElement();
        assertEquals(element.getAttribute("Name").getValue(),documentMetadataTitle);
        assertEquals(element.getAttribute("Subtitle").getValue(),documentMetadataSubtitle);
        assertEquals(element.getAttribute("DocumentVersion").getValue(),documentMetadataVersion);
        assertEquals(element.getAttribute("Date").getValue(),SerializableElement.format(dateUpdated));
        assertEquals(element.getAttribute("Ext").getValue(),documentMetadataExt);
        assertEquals(element.getAttribute("OrgName").getValue(),profileMetadataOrgName);
        assertEquals(element.getAttribute("Status").getValue(),profileMetadataStatus);
        assertEquals(element.getAttribute("Topics").getValue(),profileMetadataTopics);
        assertEquals(element.getAttribute("HL7Version").getValue(), profileMetadataHL7Version);
    }
}
