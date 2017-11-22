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
  private boolean unboundHL7 = false;
  private boolean unboundCustom = false;
  private boolean includeVaries = false;
  private boolean includeMessageTable = true;
  private boolean includeSegmentTable = true;
  private boolean includeDatatypeTable = true;
  private boolean includeValueSetsTable = true;
  private boolean includeCompositeProfileTable = true;
  private boolean includeProfileComponentTable = true;

  private boolean duplicateOBXDataTypeWhenFlavorNull = false;


  private UsageConfig segmentORGroupsMessageExport;
  private UsageConfig segmentORGroupsCompositeProfileExport;
  private UsageConfig segmentsExport;

  private UsageConfig fieldsExport;

  private UsageConfig profileComponentItemsExport;

  private UsageConfig valueSetsExport;
  private CodeUsageConfig codesExport;
  private boolean phinvadsUpdateEmailNotification;

  private UsageConfig datatypesExport;
  private UsageConfig componentExport;
  private ColumnsConfig messageColumn;
  private ColumnsConfig compositeProfileColumn;
  private ColumnsConfig segmentColumn;
  private ColumnsConfig profileComponentColumn;
  private ColumnsConfig datatypeColumn;
  private ColumnsConfig valueSetColumn;
  private ValueSetMetadataConfig valueSetsMetadata;
  private final static int MAX_CODE = 500;
  private int maxCodeNumber = MAX_CODE;



  public static ExportConfig getBasicExportConfig(boolean setAllTrue) {
    ExportConfig defaultConfiguration = new ExportConfig();
    defaultConfiguration.setDefaultType(true);
    defaultConfiguration.setAccountId(null);
    defaultConfiguration.setIncludeMessageTable(true);
    defaultConfiguration.setIncludeSegmentTable(true);
    defaultConfiguration.setIncludeDatatypeTable(true);
    defaultConfiguration.setIncludeValueSetsTable(true);
    defaultConfiguration.setIncludeCompositeProfileTable(true);
    defaultConfiguration.setIncludeProfileComponentTable(true);
    // Default Usages
    UsageConfig displayAll = new UsageConfig();
    UsageConfig displaySelectives = new UsageConfig();
    displaySelectives.setC(true);
    displaySelectives.setX(setAllTrue);
    displaySelectives.setO(setAllTrue);
    displaySelectives.setR(true);
    displaySelectives.setRe(true);
    CodeUsageConfig codeUsageExport = new CodeUsageConfig();
    codeUsageExport.setE(setAllTrue);
    codeUsageExport.setP(true);
    codeUsageExport.setR(true);

    displayAll.setC(true);
    displayAll.setRe(true);
    displayAll.setX(true);
    displayAll.setO(true);
    displayAll.setR(true);

    defaultConfiguration.setSegmentORGroupsMessageExport(displayAll);
    defaultConfiguration.setSegmentORGroupsCompositeProfileExport(displayAll);

    defaultConfiguration.setComponentExport(displayAll);

    defaultConfiguration.setFieldsExport(displayAll);
    defaultConfiguration.setProfileComponentItemsExport(displayAll);

    defaultConfiguration.setCodesExport(codeUsageExport);
    defaultConfiguration.setPhinvadsUpdateEmailNotification(false);
    defaultConfiguration.setDatatypesExport(displaySelectives);
    defaultConfiguration.setSegmentsExport(displaySelectives);

    defaultConfiguration.setValueSetsExport(displaySelectives);

    ValueSetMetadataConfig valueSetMetadataConfig =
        new ValueSetMetadataConfig(true, true, true, true, true);
    defaultConfiguration.setValueSetsMetadata(valueSetMetadataConfig);

    // Default column
    ArrayList<NameAndPositionAndPresence> messageColumnsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();

    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Segment", 1, true, true));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Flavor", 2, true, true));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Element Name", 3, true, true));
    messageColumnsDefaultList
        .add(new NameAndPositionAndPresence("Cardinality", 4, true, setAllTrue));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 5, true, setAllTrue));
    messageColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 1, true, setAllTrue));

    ArrayList<NameAndPositionAndPresence> segmentColumnsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Name", 1, true, true));
    segmentColumnsDefaultList
        .add(new NameAndPositionAndPresence("Conformance Length", 2, setAllTrue, setAllTrue));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Data Type", 3, true, setAllTrue));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 4, true, setAllTrue));
    segmentColumnsDefaultList
        .add(new NameAndPositionAndPresence("Cardinality", 5, true, setAllTrue));
    segmentColumnsDefaultList
        .add(new NameAndPositionAndPresence("Length", 6, setAllTrue, setAllTrue));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Value Set", 7, true, setAllTrue));
    segmentColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 8, true, setAllTrue));



    ArrayList<NameAndPositionAndPresence> dataTypeColumnsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();

    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Name", 1, true, true));
    dataTypeColumnsDefaultList
        .add(new NameAndPositionAndPresence("Conformance Length", 2, setAllTrue, setAllTrue));
    dataTypeColumnsDefaultList
        .add(new NameAndPositionAndPresence("Data Type", 3, true, setAllTrue));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Usage", 4, true, setAllTrue));
    dataTypeColumnsDefaultList
        .add(new NameAndPositionAndPresence("Length", 5, setAllTrue, setAllTrue));
    dataTypeColumnsDefaultList
        .add(new NameAndPositionAndPresence("Value Set", 6, true, setAllTrue));
    dataTypeColumnsDefaultList.add(new NameAndPositionAndPresence("Comment", 7, true, setAllTrue));



    defaultConfiguration.setDatatypeColumn(new ColumnsConfig(dataTypeColumnsDefaultList));
    defaultConfiguration.setSegmentColumn(new ColumnsConfig(segmentColumnsDefaultList));
    defaultConfiguration.setProfileComponentColumn(new ColumnsConfig(segmentColumnsDefaultList));
    defaultConfiguration.setMessageColumn(new ColumnsConfig(messageColumnsDefaultList));
    defaultConfiguration.setCompositeProfileColumn(new ColumnsConfig(messageColumnsDefaultList));

    ArrayList<NameAndPositionAndPresence> valueSetsDefaultList =
        new ArrayList<NameAndPositionAndPresence>();

    valueSetsDefaultList.add(new NameAndPositionAndPresence("Value", 1, true, true));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Code System", 2, true, true));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Usage", 3, setAllTrue, setAllTrue));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Description", 4, true, true));
    valueSetsDefaultList.add(new NameAndPositionAndPresence("Comment", 5, setAllTrue, setAllTrue));

    defaultConfiguration.setValueSetColumn(new ColumnsConfig(valueSetsDefaultList));
    defaultConfiguration.setMaxCodeNumber(MAX_CODE);
    return defaultConfiguration;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isDefaultType() {
    return defaultType;
  }

  public void setDefaultType(boolean defaultType) {
    this.defaultType = defaultType;
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

  public boolean isUnboundHL7() {
    return unboundHL7;
  }

  public void setUnboundHL7(boolean unboundHL7) {
    this.unboundHL7 = unboundHL7;
  }

  public boolean isUnboundCustom() {
    return unboundCustom;
  }

  public void setUnboundCustom(boolean unboundCustom) {
    this.unboundCustom = unboundCustom;
  }

  public boolean isIncludeMessageTable() {
    return includeMessageTable;
  }

  public void setIncludeMessageTable(boolean includeMessageTable) {
    this.includeMessageTable = includeMessageTable;
  }

  public boolean isIncludeSegmentTable() {
    return includeSegmentTable;
  }

  public void setIncludeSegmentTable(boolean includeSegmentTable) {
    this.includeSegmentTable = includeSegmentTable;
  }

  public boolean isIncludeDatatypeTable() {
    return includeDatatypeTable;
  }

  public void setIncludeDatatypeTable(boolean includeDatatypeTable) {
    this.includeDatatypeTable = includeDatatypeTable;
  }

  public boolean isIncludeValueSetsTable() {
    return includeValueSetsTable;
  }

  public void setIncludeValueSetsTable(boolean includeValueSetsTable) {
    this.includeValueSetsTable = includeValueSetsTable;
  }

  public boolean isIncludeCompositeProfileTable() {
    return includeCompositeProfileTable;
  }

  public void setIncludeCompositeProfileTable(boolean includeCompositeProfileTable) {
    this.includeCompositeProfileTable = includeCompositeProfileTable;
  }

  public boolean isIncludeProfileComponentTable() {
    return includeProfileComponentTable;
  }

  public void setIncludeProfileComponentTable(boolean includeProfileComponentTable) {
    this.includeProfileComponentTable = includeProfileComponentTable;
  }

  public UsageConfig getSegmentORGroupsMessageExport() {
    return segmentORGroupsMessageExport;
  }

  public void setSegmentORGroupsMessageExport(UsageConfig segmentORGroupsMessageExport) {
    this.segmentORGroupsMessageExport = segmentORGroupsMessageExport;
  }

  public UsageConfig getSegmentORGroupsCompositeProfileExport() {
    return segmentORGroupsCompositeProfileExport;
  }

  public void setSegmentORGroupsCompositeProfileExport(
      UsageConfig segmentORGroupsCompositeProfileExport) {
    this.segmentORGroupsCompositeProfileExport = segmentORGroupsCompositeProfileExport;
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

  public ColumnsConfig getCompositeProfileColumn() {
    return compositeProfileColumn;
  }

  public void setCompositeProfileColumn(ColumnsConfig compositeProfileColumn) {
    this.compositeProfileColumn = compositeProfileColumn;
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

  public UsageConfig getProfileComponentItemsExport() {
    return profileComponentItemsExport;
  }

  public void setProfileComponentItemsExport(UsageConfig profileComponentItemsExport) {
    this.profileComponentItemsExport = profileComponentItemsExport;
  }

  public ColumnsConfig getProfileComponentColumn() {
    return profileComponentColumn;
  }

  public void setProfileComponentColumn(ColumnsConfig profileComponentColumn) {
    this.profileComponentColumn = profileComponentColumn;
  }

  public ValueSetMetadataConfig getValueSetsMetadata() {
    return valueSetsMetadata;
  }

  public void setValueSetsMetadata(ValueSetMetadataConfig valueSetsMetadata) {
    this.valueSetsMetadata = valueSetsMetadata;
  }

  public boolean isDuplicateOBXDataTypeWhenFlavorNull() {
    return duplicateOBXDataTypeWhenFlavorNull;
  }

  public void setDuplicateOBXDataTypeWhenFlavorNull(boolean duplicateOBXDataTypeWhenFlavorNull) {
    this.duplicateOBXDataTypeWhenFlavorNull = duplicateOBXDataTypeWhenFlavorNull;
  }

  /**
   * @return the includeVaries
   */
  public boolean isIncludeVaries() {
    return includeVaries;
  }

  /**
   * @param includeVaries the includeVaries to set
   */
  public void setIncludeVaries(boolean includeVaries) {
    this.includeVaries = includeVaries;
  }

  public int getMaxCodeNumber() {
    return maxCodeNumber;
  }

  public void setMaxCodeNumber(int maxCodeNumber) {
    this.maxCodeNumber = maxCodeNumber;
  }

  public boolean isPhinvadsUpdateEmailNotification() {
    return phinvadsUpdateEmailNotification;
  }

  public void setPhinvadsUpdateEmailNotification(boolean phinvadsUpdateEmailNotification) {
    this.phinvadsUpdateEmailNotification = phinvadsUpdateEmailNotification;
  }
}
