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



  private String name;
  private Long accountId;
  private String type;

  private UsageConfig segmentORGroup;
  private UsageConfig segmentsExport;
  private UsageConfig fieldsExport;
  private UsageConfig fieldsDisplay;

  private UsageConfig valueSetExport;
  private UsageConfig codeExport;

  private ValueSetUsageConfig codes;
  private UsageConfig datatypeExport;
  private UsageConfig componentExport;

  public UsageConfig getSegmentORGroup() {
    return segmentORGroup;
  }

  public void setSegmentORGroup(UsageConfig segmentORGroup) {
    this.segmentORGroup = segmentORGroup;
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

  public UsageConfig getFieldsDisplay() {
    return fieldsDisplay;
  }

  public void setFieldsDisplay(UsageConfig fieldsDisplay) {
    this.fieldsDisplay = fieldsDisplay;
  }

  public UsageConfig getValueSetExport() {
    return valueSetExport;
  }

  public void setValueSetExport(UsageConfig valueSetExport) {
    this.valueSetExport = valueSetExport;
  }

  public ValueSetUsageConfig getCodes() {
    return codes;
  }

  public void setCodes(ValueSetUsageConfig codes) {
    this.codes = codes;
  }

  public UsageConfig getDatatypeExport() {
    return datatypeExport;
  }

  public void setDatatypeExport(UsageConfig datatypeExport) {
    this.datatypeExport = datatypeExport;
  }

  public UsageConfig getComponentExport() {
    return componentExport;
  }

  public void setComponentExport(UsageConfig componentExport) {
    this.componentExport = componentExport;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private GeneralColumnConfig messageColumn;
  private GeneralColumnConfig segmentColumn;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  public GeneralColumnConfig getMessageColumn() {
    return messageColumn;
  }

  public void setMessageColumn(GeneralColumnConfig messageColumn) {
    this.messageColumn = messageColumn;
  }

  public GeneralColumnConfig getSegmentColumn() {
    return segmentColumn;
  }

  public void setSegmentColumn(GeneralColumnConfig segmentColumn) {
    this.segmentColumn = segmentColumn;
  }

  public GeneralColumnConfig getDatatypeColumn() {
    return datatypeColumn;
  }

  public void setDatatypeColumn(GeneralColumnConfig datatypeColumn) {
    this.datatypeColumn = datatypeColumn;
  }

  public ValueSetColumnConfig getValueSetColumn() {
    return valueSetColumn;
  }

  public void setValueSetColumn(ValueSetColumnConfig valueSetColumn) {
    this.valueSetColumn = valueSetColumn;
  }

  public GeneralColumnConfig datatypeColumn;
  public ValueSetColumnConfig valueSetColumn;


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }



  public ExportConfig() {
    super();
    // TODO Auto-generated constructor stub
  }


  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }



}
