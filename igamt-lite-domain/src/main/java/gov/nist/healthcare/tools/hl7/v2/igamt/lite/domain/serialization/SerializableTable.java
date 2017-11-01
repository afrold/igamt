package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetMetadataConfig;
import nu.xom.Attribute;
import nu.xom.Element;

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
 * Created by Maxence Lefort on 12/9/16.
 */
public class SerializableTable extends SerializableSection {

    private Table table;
    private String bindingIdentifier, defPreText, defPostText;
    private ValueSetMetadataConfig valueSetMetadataConfig;
    private String referenceUrl;
    private int maxCodeNumber;
    private boolean exportCodes;

    public SerializableTable(String id, String prefix, String position, String headerLevel, String title, Table table,
        String bindingIdentifier, String defPreText, String defPostText, ValueSetMetadataConfig valueSetMetadataConfig, int maxCodeNumber, boolean exportCodes, String referenceUrl) {
        super(id, prefix, position, headerLevel, title);
        this.table = table;
        this.bindingIdentifier = bindingIdentifier;
        this.defPreText = defPreText;
        this.defPostText = defPostText;
        this.valueSetMetadataConfig = valueSetMetadataConfig;
        this.referenceUrl = referenceUrl;
        this.maxCodeNumber = maxCodeNumber;
        this.exportCodes = exportCodes;
    }

    @Override public Element serializeElement() {
        Element valueSetDefinitionElement = new Element("ValueSetDefinition");
        if (this.table != null) {
            valueSetDefinitionElement.addAttribute(new Attribute("Id",
                (this.table.getBindingIdentifier() == null) ?
                    "" :
                    this.table.getBindingIdentifier()));
            valueSetDefinitionElement.addAttribute(new Attribute("BindingIdentifier",
                (this.bindingIdentifier == null) ? "" : table.getBindingIdentifier()));
            if((this.table.getName() != null && !this.table.getName().isEmpty())){
              valueSetDefinitionElement.addAttribute(new Attribute("Name", this.table.getName()));
            }
            if(this.table.getSourceType()!=null){
            	
        		valueSetDefinitionElement.addAttribute(new Attribute("SourceType",table.getSourceType().value));
            	
        		if(this.referenceUrl != null && !this.referenceUrl.isEmpty()){
        			valueSetDefinitionElement.addAttribute(new Attribute("ReferenceUrl",this.referenceUrl));
        		}
        		if(this.table.getContentDefinition() != null && this.table.getContentDefinition().equals(ContentDefinition.Intensional) && this.table.getInfoForExternal() != null && !this.table.getInfoForExternal().isEmpty()){
                	valueSetDefinitionElement.addAttribute(new Attribute("InfoForExternal",this.table.getInfoForExternal()));
                }
            }
            valueSetDefinitionElement.addAttribute(new Attribute("Version",
                (this.table.getVersion() == null) ? "" : "" + this.table.getVersion()));
            valueSetDefinitionElement.addAttribute(
                new Attribute("Oid", (this.table.getOid() == null) ? "" : this.table.getOid()));
            if(this.valueSetMetadataConfig!=null){
	            if(valueSetMetadataConfig.isStability()){
	            	valueSetDefinitionElement.addAttribute(new Attribute("Stability",
		                (this.table.getStability() == null) ? "" : this.table.getStability().value));
	            }
	            if(valueSetMetadataConfig.isExtensibility()){
		            valueSetDefinitionElement.addAttribute(new Attribute("Extensibility",
		                (this.table.getExtensibility() == null) ?
		                    "" :
		                    this.table.getExtensibility().value));
	            }
	            if(valueSetMetadataConfig.isContentDefinition()){
		            valueSetDefinitionElement.addAttribute(new Attribute("ContentDefinition",
		                (this.table.getContentDefinition() == null) ?
		                    "" :
		                    this.table.getContentDefinition().value));
	            }
            }
            valueSetDefinitionElement.addAttribute(new Attribute("id", this.table.getId()));

            if (this.table.getCodes() != null && this.exportCodes) {
                List<Code> codes;
                if(maxCodeNumber >= 0 && this.table.getCodes().size()>maxCodeNumber){
                    codes = this.table.getCodes().subList(0,maxCodeNumber);
                } else {
                    codes = this.table.getCodes();
                }
                if(codes != null && !codes.isEmpty()) {
                    for (Code c : codes) {
                        Element valueElement = new Element("ValueElement");
                        valueElement.addAttribute(new Attribute("Value", (c.getValue() == null) ? "" : c.getValue()));
                        valueElement.addAttribute(new Attribute("Label", (c.getLabel() == null) ? "" : c.getLabel()));
                        valueElement.addAttribute(new Attribute("CodeSystem",
                            (c.getCodeSystem() == null) ? "" : c.getCodeSystem()));
                        valueElement.addAttribute(new Attribute("Usage",
                            (c.getCodeUsage() == null) || c.getValue() == null || ""
                                .equals(c.getValue()) || "...".equals(c.getValue()) ? "" : c.getCodeUsage()));
                        valueElement.addAttribute(new Attribute("Comment",
                            (c.getComments() == null) ? "" : c.getComments()));
                        valueSetDefinitionElement.appendChild(valueElement);
                    }
                }
            }
            if (this.defPreText != null && !this.defPreText.isEmpty()) {
                valueSetDefinitionElement
                    .appendChild(this.createTextElement("DefPreText", this.defPreText));
            }
            if (this.defPostText != null && !this.defPostText.isEmpty()) {
                valueSetDefinitionElement.appendChild(
                    this.createTextElement("DefPostText", this.defPostText));
            }
        }
        Element sectionElement = super.getSectionElement();
        sectionElement.appendChild(valueSetDefinitionElement);
        return sectionElement;
    }
}
