/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Feb 2, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@Document(collection = "exportConfig")
public class ExportConfig {
  private static final long serialVersionUID = 734059059225906039L;

  @Id
  private String id;
  boolean defaultType = false;
  private String name;
  private Long accountId;
  private String type;
  private boolean unboundHL7 = false;
  private boolean unboundCustom = false;
  private boolean includePC = true;

  private UsageConfig segmentORGroupsExport;
  private UsageConfig segmentORGroupsExportPC;
  private UsageConfig segmentsExport;
  private UsageConfig segmentsExportPC;

  private UsageConfig fieldsExport;
  private UsageConfig fieldsExportPC;

  private UsageConfig valueSetsExport;
  private CodeUsageConfig codesExport;

  private UsageConfig datatypesExport;
  private UsageConfig componentExport;
  private ColumnsConfig messageColumn;

  public boolean isIncludePC() {
    return includePC;
  }

  public void setIncludePC(boolean includePC) {
    this.includePC = includePC;
  }

  public UsageConfig getSegmentORGroupsExportPC() {
    return segmentORGroupsExportPC;
  }

  public void setSegmentORGroupsExportPC(UsageConfig segmentORGroupsExportPC) {
    this.segmentORGroupsExportPC = segmentORGroupsExportPC;
  }

  public UsageConfig getSegmentsExportPC() {
    return segmentsExportPC;
  }

  public void setSegmentsExportPC(UsageConfig segmentsExportPC) {
    this.segmentsExportPC = segmentsExportPC;
  }

  public ColumnsConfig getMessageColumnPC() {
    return messageColumnPC;
  }

  public void setMessageColumnPC(ColumnsConfig messageColumnPC) {
    this.messageColumnPC = messageColumnPC;
  }

  public ColumnsConfig getSegmentColumnPC() {
    return segmentColumnPC;
  }

  public void setSegmentColumnPC(ColumnsConfig segmentColumnPC) {
    this.segmentColumnPC = segmentColumnPC;
  }

  private ColumnsConfig messageColumnPC;
  private ColumnsConfig segmentColumn;
  private ColumnsConfig segmentColumnPC;
  public ColumnsConfig datatypeColumn;
  public ColumnsConfig valueSetColumn;


  public ExportConfig() {
    super();
    // TODO Auto-generated constructor stub
  }

  public boolean isDefaultType() {
    return defaultType;
  }

  public void setDefaultType(boolean defaultType) {
    this.defaultType = defaultType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public UsageConfig getSegmentORGroupsExport() {
    return segmentORGroupsExport;
  }

  public void setSegmentORGroupsExport(UsageConfig segmentORGroupsExport) {
    this.segmentORGroupsExport = segmentORGroupsExport;
  }

  public UsageConfig getSegmentsExport() {
    return segmentsExport;
  }

  public void setSegmentsExport(UsageConfig segmentsExport) {
    this.segmentsExport = segmentsExport;
  }

  public UsageConfig getFieldsExport() {
    return fieldsExport;
  }

  public void setFieldsExport(UsageConfig fieldsExport) {
    this.fieldsExport = fieldsExport;
  }

  public UsageConfig getValueSetsExport() {
    return valueSetsExport;
  }

  public void setValueSetsExport(UsageConfig valueSetsExport) {
    this.valueSetsExport = valueSetsExport;
  }

  public CodeUsageConfig getCodesExport() {
    return codesExport;
  }

  public void setCodesExport(CodeUsageConfig codesExport) {
    this.codesExport = codesExport;
  }

  public UsageConfig getDatatypesExport() {
    return datatypesExport;
  }

  public void setDatatypesExport(UsageConfig datatypesExport) {
    this.datatypesExport = datatypesExport;
  }

  public UsageConfig getComponentExport() {
    return componentExport;
  }

  public void setComponentExport(UsageConfig componentExport) {
    this.componentExport = componentExport;
  }

  public ColumnsConfig getMessageColumn() {
    return messageColumn;
  }

  public void setMessageColumn(ColumnsConfig messageColumn) {
    this.messageColumn = messageColumn;
  }

  public ColumnsConfig getSegmentColumn() {
    return segmentColumn;
  }

  public void setSegmentColumn(ColumnsConfig segmentColumn) {
    this.segmentColumn = segmentColumn;
  }

  public ColumnsConfig getDatatypeColumn() {
    return datatypeColumn;
  }

  public void setDatatypeColumn(ColumnsConfig datatypeColumn) {
    this.datatypeColumn = datatypeColumn;
  }

  public ColumnsConfig getValueSetColumn() {
    return valueSetColumn;
  }

  public void setValueSetColumn(ColumnsConfig valueSetColumn) {
    this.valueSetColumn = valueSetColumn;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public static ExportConfig getBasicExportConfig(String type) {
    ExportConfig defaultConfiguration = new ExportConfig();
    defaultConfiguration.setDefaultType(true);
    defaultConfiguration.setAccountId(null);
    defaultConfiguration.setType(type);
    // Default Usages
    UsageConfig displayAll = new UsageConfig();
    UsageConfig displaySelectives = new UsageConfig();
    displaySelectives.setC(true);
    displaySelectives.setX(false);
    displaySelectives.setO(false);
    displaySelectives.setR(true);
    displaySelectives.setRe(true);
    CodeUsageConfig codeUsageExport = new CodeUsageConfig();
    codeUsageExport.setE(false);
    codeUsageExport.setP(true);
    codeUsageExport.setR(true);

    displayAll.setC(true);
    displayAll.setRe(true);
    displayAll.setX(true);
    displayAll.setO(true);
    displayAll.setR(true);

    defaultConfiguration.setSegmentORGroupsExport(displayAll);
    defaultConfiguration.setSegmentORGroupsExportPC(displayAll);

    defaultConfiguration.setComponentExport(displayAll);

    defaultConfiguration.setFieldsExport(displayAll);
    defaultConfiguration.setFieldsExportPC(displayAll);

    defaultConfiguration.setCodesExport(codeUsageExport);

    defaultConfiguration.setDatatypesExport(displaySelectives);
    defaultConfiguration.setSegmentsExport(displaySelectives);
    defaultConfiguration.setSegmentsExportPC(displaySelectives);

    defaultConfiguration.setValueSetsExport(displaySelectives);

    // Default column
    ArrayList<NameAndPositionAndPresence> messageColumnsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();

    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Segment", 1, true, true));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Flavor", 2, true, true));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Element Name", 3, true, true));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Cardinality", 4, true, false));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 5, true, false));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 1, true, false));

    ArrayList<NameAndPositionAndPresence> segmentColumnsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Name", 1, true, true));
    segmentColumnsDefaultList
        .add(new NameAndPositionAndPresence("Conformance Length", 2, false, false));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Data Type", 3, true, false));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 4, true, false));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Cardinality", 5, true, false));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Length", 6, false, false));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Value Set", 7, true, false));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 8, true, false));



    ArrayList<NameAndPositionAndPresence> dataTypeColumnsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();

    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Name", 1, true, true));
    dataTypeColumnsDefaultList
        .add(new NameAndPositionAndPresence("Conformance Length", 2, false, false));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Data Type", 3, true, false));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 4, true, false));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Length", 5, false, false));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Value Set", 6, true, false));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 7, true, false));



    defaultConfiguration.setDatatypeColumn(new ColumnsConfig(dataTypeColumnsDefaultList));
    defaultConfiguration.setSegmentColumn(new ColumnsConfig(segmentColumnsDefaultList));
    defaultConfiguration.setMessageColumn(new ColumnsConfig(messageColumnsDefaultList));
    defaultConfiguration.setSegmentColumnPC(new ColumnsConfig(segmentColumnsDefaultList));
    defaultConfiguration.setMessageColumnPC(new ColumnsConfig(messageColumnsDefaultList));

    ArrayList<NameAndPositionAndPresence> valueSetsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();

    valueSetsDefaultList.add(new NameAndPositionAndPresence("Value", 1, true, true));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Code System", 2, true, true));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Usage", 3, false, false));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Description", 4, true, true));

    defaultConfiguration.setValueSetColumn(new ColumnsConfig(valueSetsDefaultList));
    return defaultConfiguration;
  }

  /**
   * @return the unboundHL7
   */
  public boolean isUnboundHL7() {
    return unboundHL7;
  }

  /**
   * @param unboundHL7 the unboundHL7 to set
   */
  public void setUnboundHL7(boolean unboundHL7) {
    this.unboundHL7 = unboundHL7;
  }

  /**
   * @return the unboundCustom
   */
  public boolean isUnboundCustom() {
    return unboundCustom;
  }

  /**
   * @param unboundCustom the unboundCustom to set
   */
  public void setUnboundCustom(boolean unboundCustom) {
    this.unboundCustom = unboundCustom;
  }

  /**
   * @return the fieldsExportPC
   */
  public UsageConfig getFieldsExportPC() {
    return fieldsExportPC;
  }

  /**
   * @param fieldsExportPC the fieldsExportPC to set
   */
  public void setFieldsExportPC(UsageConfig fieldsExportPC) {
    this.fieldsExportPC = fieldsExportPC;
  }

}
