package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MetadataConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.NameAndPositionAndPresence;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetMetadataConfig;

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
 * Created by Maxence Lefort on 10/5/16.
 */
public class ExportParameters {
    //Define parameters with a default value
    private boolean inlineConstraints = false;
    private boolean includeTOC = true;
    private String targetFormat = "html";
    private String documentTitle = "Implementation Guide";
    private String imageLogo;
    private List<NameAndPositionAndPresence> messageColumns;
    private List<NameAndPositionAndPresence> compositeProfileColumns;
    private List<NameAndPositionAndPresence> profileComponentColumns;
    private List<NameAndPositionAndPresence> segmentsColumns;
    private List<NameAndPositionAndPresence> dataTypeColumns;
    private List<NameAndPositionAndPresence> valueSetColumns;
    private ValueSetMetadataConfig valueSetMetadataConfig;
    private MetadataConfig datatypeMetadataConfig;
    private MetadataConfig segmentMetadataConfig;
    private MetadataConfig messageMetadataConfig;
    private MetadataConfig compositeProfileMetadataConfig;
    private ExportFontConfig exportFontConfig;
    private String appVersion;

    public ExportParameters(boolean inlineConstraints, boolean includeTOC, String targetFormat,
        String documentTitle, String appVersion) {
        this(inlineConstraints,includeTOC,targetFormat,documentTitle,null, appVersion);
    }

    public ExportParameters(boolean inlineConstraints, boolean includeTOC, String targetFormat,
        String documentTitle,String imageLogo, String appVersion) {
        this(inlineConstraints,includeTOC,targetFormat,documentTitle,imageLogo,null,null,null,null,null,null,null,null,null,null,null,null, appVersion);
    }

    public ExportParameters(boolean inlineConstraints, boolean includeTOC, String targetFormat,
        String documentTitle, String imageLogo, List<NameAndPositionAndPresence> messageColumns,
        List<NameAndPositionAndPresence> compositeProfileColumns,
        List<NameAndPositionAndPresence> profileComponentColumns,
        List<NameAndPositionAndPresence> segmentsColumns,
        List<NameAndPositionAndPresence> dataTypeColumns,
        List<NameAndPositionAndPresence> valueSetColumns,
        ValueSetMetadataConfig valueSetMetadataConfig,
        MetadataConfig datatypeMetadataConfig,
        MetadataConfig segmentMetadataConfig,
        MetadataConfig messageMetadataConfig,
        MetadataConfig compositeProfileMetadataConfig,
        ExportFontConfig exportFontConfig, String appVersion) {
        this.inlineConstraints = inlineConstraints;
        this.includeTOC = includeTOC;
        this.targetFormat = targetFormat;
        this.documentTitle = documentTitle;
        this.imageLogo = imageLogo;
        this.messageColumns = messageColumns;
        this.compositeProfileColumns = compositeProfileColumns;
        this.profileComponentColumns = profileComponentColumns;
        this.segmentsColumns = segmentsColumns;
        this.dataTypeColumns = dataTypeColumns;
        this.valueSetColumns = valueSetColumns;
        this.valueSetMetadataConfig = valueSetMetadataConfig;
        this.datatypeMetadataConfig = datatypeMetadataConfig;
        this.segmentMetadataConfig = segmentMetadataConfig;
        this.messageMetadataConfig = messageMetadataConfig;
        this.compositeProfileMetadataConfig = compositeProfileMetadataConfig;
        this.exportFontConfig = exportFontConfig;
        this.appVersion = appVersion;
    }

    public ExportParameters() {
    }

    public boolean isInlineConstraints() {
        return inlineConstraints;
    }

    public void setInlineConstraints(boolean inlineConstraints) {
        this.inlineConstraints = inlineConstraints;
    }

    public boolean isIncludeTOC() {
        return includeTOC;
    }

    public void setIncludeTOC(boolean includeTOC) {
        this.includeTOC = includeTOC;
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("includeTOC", String.valueOf(includeTOC));
        params.put("inlineConstraints", String.valueOf(inlineConstraints));
        params.put("targetFormat", targetFormat);
        params.put("documentTitle", documentTitle);
        if(imageLogo!=null) {
            params.put("imageLogo", imageLogo);
        }
        if(messageColumns!=null && !messageColumns.isEmpty()){
            String messageColumn = "messageColumn";
            for(NameAndPositionAndPresence currentColumn : messageColumns){
                params.put(messageColumn+currentColumn.getName().replace(" ",""),String.valueOf(currentColumn.isPresent()));
            }
        }
        if(compositeProfileColumns!=null && !compositeProfileColumns.isEmpty()){
            String compositeProfileColumn = "compositeProfileColumn";
            for(NameAndPositionAndPresence currentColumn : compositeProfileColumns){
                params.put(compositeProfileColumn+currentColumn.getName().replace(" ",""),String.valueOf(currentColumn.isPresent()));
            }
        }
        if(profileComponentColumns!=null && !profileComponentColumns.isEmpty()){
            String profileComponentColumn = "profileComponentColumn";
            for(NameAndPositionAndPresence currentColumn : profileComponentColumns){
                params.put(profileComponentColumn+currentColumn.getName().replace(" ",""),String.valueOf(currentColumn.isPresent()));
            }
        }
        if(dataTypeColumns!=null && !dataTypeColumns.isEmpty()){
            String dataTypeColumn = "dataTypeColumn";
            for(NameAndPositionAndPresence currentColumn : dataTypeColumns){
                params.put(dataTypeColumn+currentColumn.getName().replace(" ",""),String.valueOf(currentColumn.isPresent()));
            }
        }
        if(valueSetColumns!=null && !valueSetColumns.isEmpty()){
            String valueSetColumn = "valueSetColumn";
            for(NameAndPositionAndPresence currentColumn : valueSetColumns){
                params.put(valueSetColumn+currentColumn.getName().replace(" ",""),String.valueOf(currentColumn.isPresent()));
            }
        }
        if(segmentsColumns!=null && !segmentsColumns.isEmpty()){
            String segmentsColumn = "segmentColumn";
            for(NameAndPositionAndPresence currentColumn : segmentsColumns){
                params.put(segmentsColumn+currentColumn.getName().replace(" ",""),String.valueOf(currentColumn.isPresent()));
            }
        }
        if(valueSetMetadataConfig != null){
            params.put("valueSetMetadataStability",String.valueOf(valueSetMetadataConfig.isStability()));
            params.put("valueSetMetadataExtensibility",String.valueOf(valueSetMetadataConfig.isExtensibility()));
            params.put("valueSetMetadataContentDefinition",String.valueOf(valueSetMetadataConfig.isContentDefinition()));
            params.put("valueSetMetadataOid",String.valueOf(valueSetMetadataConfig.isOid()));
            params.put("valueSetMetadataType",String.valueOf(valueSetMetadataConfig.isType()));
        }
        if(datatypeMetadataConfig != null){
        	params.put("datatypeMetadataDisplay", String.valueOf(hasMetadata(datatypeMetadataConfig)));
        	params.put("datatypeMetadataHL7Version", String.valueOf(datatypeMetadataConfig.isHl7version()));
        	params.put("datatypeMetadataPublicationDate", String.valueOf(datatypeMetadataConfig.isPublicationDate()));
        	params.put("datatypeMetadataPublicationVersion", String.valueOf(datatypeMetadataConfig.isPublicationVersion()));
        	params.put("datatypeMetadataScope", String.valueOf(datatypeMetadataConfig.isScope()));
        }
        if(segmentMetadataConfig != null){
        	params.put("segmentMetadataDisplay", String.valueOf(hasMetadata(segmentMetadataConfig)));
        	params.put("segmentMetadataHL7Version", String.valueOf(segmentMetadataConfig.isHl7version()));
        	params.put("segmentMetadataPublicationDate", String.valueOf(segmentMetadataConfig.isPublicationDate()));
        	params.put("segmentMetadataPublicationVersion", String.valueOf(segmentMetadataConfig.isPublicationVersion()));
        	params.put("segmentMetadataScope", String.valueOf(segmentMetadataConfig.isScope()));
        }
        if(messageMetadataConfig != null){
        	params.put("messageMetadataDisplay", String.valueOf(hasMetadata(messageMetadataConfig)));
        	params.put("messageMetadataHL7Version", String.valueOf(messageMetadataConfig.isHl7version()));
        	params.put("messageMetadataPublicationDate", String.valueOf(messageMetadataConfig.isPublicationDate()));
        	params.put("messageMetadataPublicationVersion", String.valueOf(messageMetadataConfig.isPublicationVersion()));
        	params.put("messageMetadataScope", String.valueOf(messageMetadataConfig.isScope()));
        }
        if(compositeProfileMetadataConfig != null){
        	params.put("compositeProfileMetadataDisplay", String.valueOf(hasMetadata(compositeProfileMetadataConfig)));
        	params.put("compositeProfileMetadataHL7Version", String.valueOf(compositeProfileMetadataConfig.isHl7version()));
        	params.put("compositeProfileMetadataPublicationDate", String.valueOf(compositeProfileMetadataConfig.isPublicationDate()));
        	params.put("compositeProfileMetadataPublicationVersion", String.valueOf(compositeProfileMetadataConfig.isPublicationVersion()));
        	params.put("compositeProfileMetadataScope", String.valueOf(compositeProfileMetadataConfig.isScope()));
        }
        if(exportFontConfig!=null) {
            params.put("userFontFamily", exportFontConfig.getExportFont().getValue());
            params.put("userFontSize", String.valueOf(exportFontConfig.getFontSize()));
        }
        params.put("appCurrentVersion", this.appVersion);
        return params;
    }
    
    private boolean hasMetadata(MetadataConfig metadataConfig){
    	return (metadataConfig.isHl7version() || metadataConfig.isPublicationDate() || metadataConfig.isPublicationVersion() || metadataConfig.isScope());
    }

    public void setImageLogo(String imageLogo) {
        this.imageLogo = imageLogo;
    }

    public ExportFontConfig getExportFontConfig() {
        return exportFontConfig;
    }
}
