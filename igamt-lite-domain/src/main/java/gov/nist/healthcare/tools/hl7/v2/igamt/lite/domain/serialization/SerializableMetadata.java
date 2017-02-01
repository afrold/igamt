package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MetaData;
import nu.xom.Attribute;
import nu.xom.Element;

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
public class SerializableMetadata extends SerializableElement{

    private MetaData metaData;
    private Date dateUpdated;

    public SerializableMetadata(MetaData metaData,
        Date dateUpdated) {
        this.metaData = metaData;
        this.dateUpdated = dateUpdated;
    }

    @Override
    public Element serializeElement() {
        Element elmMetaData = new Element("MetaData");
        if(this.metaData !=null){
            if(metaData instanceof DocumentMetaData){
                DocumentMetaData documentMetaData = (DocumentMetaData) metaData;
                addDocumentMetaData(documentMetaData,elmMetaData);
            } else if(metaData instanceof DatatypeLibraryMetaData){
                DatatypeLibraryMetaData datatypeLibraryMetaData = (DatatypeLibraryMetaData) metaData;
                addDatatypeLibraryMetaData(datatypeLibraryMetaData,elmMetaData);
            }
            if (metaData.getVersion() != null)
                elmMetaData.addAttribute(new Attribute("DocumentVersion", metaData.getVersion()));
            if (this.dateUpdated != null)
                elmMetaData.addAttribute(new Attribute("Date", this.format(this.dateUpdated)));
            if (metaData.getExt() != null)
                elmMetaData.addAttribute(new Attribute("Ext", metaData.getExt()));
            if (this.metaData.getOrgName() != null)
                elmMetaData.addAttribute(new Attribute("OrgName", this.metaData.getOrgName()));
            if (this.metaData.getHl7Version() != null)
                elmMetaData.addAttribute(new Attribute("HL7Version", this.metaData.getHl7Version()));
        }
        return elmMetaData;
    }

    private void addDatatypeLibraryMetaData(DatatypeLibraryMetaData datatypeLibraryMetaData,
        Element elmMetaData) {
        if(datatypeLibraryMetaData.getName() != null && !"".equals(datatypeLibraryMetaData.getName())){
            elmMetaData.addAttribute(new Attribute("Name", datatypeLibraryMetaData.getName()));
        }
        if(datatypeLibraryMetaData.getDescription()!=null && !"".equals(datatypeLibraryMetaData.getDescription())){
            elmMetaData.addAttribute(new Attribute("Description", datatypeLibraryMetaData.getDescription()));
        }
    }

    private void addDocumentMetaData(DocumentMetaData documentMetaData, Element elmMetaData) {
        if (documentMetaData.getTitle() != null)
            elmMetaData.addAttribute(new Attribute("Name", documentMetaData.getTitle()));
        if (documentMetaData.getSubTitle() != null)
            elmMetaData.addAttribute(new Attribute("Subtitle", documentMetaData.getSubTitle()));
        if (documentMetaData.getStatus() != null)
            elmMetaData.addAttribute(new Attribute("Status", documentMetaData.getStatus()));
        if (documentMetaData.getTopics() != null)
            elmMetaData.addAttribute(new Attribute("Topics", documentMetaData.getTopics()));
    }
}
