/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 7, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class SubProfileComponentAttributes {

  private Usage usage;
  private Usage oldUsage;
  private String minLength;
  private String oldMinLength;
  private String maxLength;
  private String oldMaxLength;
  private String confLength;
  private String oldConfLength;
  private Integer min;
  private Integer oldMin;
  private String max;
  private String oldMax;

  private List<TableLink> tables = new ArrayList<TableLink>();
  private List<TableLink> oldTables = new ArrayList<TableLink>();
  private DatatypeLink datatype;
  private DatatypeLink oldDatatype;
  private List<Comment> oldComments = new ArrayList<Comment>();
  private List<Comment> comments = new ArrayList<Comment>();
  private String text = "";
  private SegmentLink ref;
  private SegmentLink oldRef;
  private List<ConformanceStatement> oldConformanceStatements = new ArrayList<ConformanceStatement>();
  private DynamicMappingDefinition oldDynamicMappingDefinition = new DynamicMappingDefinition();
  private CoConstraintsTable oldCoConstraintsTable = new CoConstraintsTable();
  private List<ConformanceStatement> conformanceStatements;
  private DynamicMappingDefinition dynamicMappingDefinition;
  private CoConstraintsTable coConstraintsTable;
  private Predicate predicate;

  public Usage getUsage() {
    return usage;
  }

  public void setUsage(Usage usage) {
    this.usage = usage;
  }

  public Usage getOldUsage() {
    return oldUsage;
  }

  public void setOldUsage(Usage oldUsage) {
    this.oldUsage = oldUsage;
  }

  public String getMinLength() {
    return minLength;
  }

  public void setMinLength(String minLength) {
    this.minLength = minLength;
  }

  public String getOldMinLength() {
    return DataElement.getFixedLength(oldMinLength);
  }

  public void setOldMinLength(String oldMinLength) {
    this.oldMinLength = oldMinLength;
  }

  public String getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(String maxLength) {
    this.maxLength = maxLength;
  }

  public String getOldMaxLength() {
    return DataElement.getFixedMaxLength(getOldMinLength(), oldMaxLength);
  }

  public void setOldMaxLength(String oldMaxLength) {
    this.oldMaxLength = oldMaxLength;
  }

  public String getConfLength() {
    return confLength;

  }

  public void setConfLength(String confLength) {
    this.confLength = confLength;
  }

  public String getOldConfLength() {
    return DataElement.getFixedLength(oldConfLength);
  }

  public void setOldConfLength(String oldConfLength) {
    this.oldConfLength = oldConfLength;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public Integer getOldMin() {
    return oldMin;
  }

  public void setOldMin(Integer oldMin) {
    this.oldMin = oldMin;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public String getOldMax() {
    return oldMax;
  }

  public void setOldMax(String oldMax) {
    this.oldMax = oldMax;
  }

  public List<TableLink> getTables() {
    return tables;
  }

  public void setTables(List<TableLink> tables) {
    this.tables = tables;
  }

  public List<TableLink> getOldTables() {
    return oldTables;
  }

  public void setOldTables(List<TableLink> oldTables) {
    this.oldTables = oldTables;
  }

  public DatatypeLink getDatatype() {
    return datatype;
  }

  public void setDatatype(DatatypeLink datatype) {
    this.datatype = datatype;
  }

  public DatatypeLink getOldDatatype() {
    return oldDatatype;
  }

  public void setOldDatatype(DatatypeLink oldDatatype) {
    this.oldDatatype = oldDatatype;
  }

  // public String getComment() {
  // return comment;
  // }
  //
  // public void setComment(String comment) {
  // this.comment = comment;
  // }
  //
  // public String getOldComment() {
  // return oldComment;
  // }
  //
  // public void setOldComment(String oldComment) {
  // this.oldComment = oldComment;
  // }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public SegmentLink getRef() {
    return ref;
  }

  public void setRef(SegmentLink ref) {
    this.ref = ref;
  }

  public SegmentLink getOldRef() {
    return oldRef;
  }

  public void setOldRef(SegmentLink oldRef) {
    this.oldRef = oldRef;
  }


  public List<ConformanceStatement> getOldConformanceStatements() {
    return oldConformanceStatements;
  }

  public void setOldConformanceStatements(List<ConformanceStatement> oldConformanceStatements) {
    this.oldConformanceStatements = oldConformanceStatements;
  }

  public List<ConformanceStatement> getConformanceStatements() {
    if (conformanceStatements != null && conformanceStatements.size() == 0)
      return null;
    return conformanceStatements;
  }

  public void setConformanceStatements(List<ConformanceStatement> conformanceStatements) {
    this.conformanceStatements = conformanceStatements;
  }


  public DynamicMappingDefinition getOldDynamicMappingDefinition() {
    return oldDynamicMappingDefinition;
  }

  public void setOldDynamicMappingDefinition(DynamicMappingDefinition oldDynamicMappingDefinition) {
    this.oldDynamicMappingDefinition = oldDynamicMappingDefinition;
  }

  public DynamicMappingDefinition getDynamicMappingDefinition() {
    return dynamicMappingDefinition;
  }

  public void setDynamicMappingDefinition(DynamicMappingDefinition dynamicMappingDefinition) {
    this.dynamicMappingDefinition = dynamicMappingDefinition;
  }


  public CoConstraintsTable getOldCoConstraintsTable() {
    return oldCoConstraintsTable;
  }

  public void setOldCoConstraintsTable(CoConstraintsTable oldCoConstraintsTable) {
    this.oldCoConstraintsTable = oldCoConstraintsTable;
  }

  public CoConstraintsTable getCoConstraintsTable() {
    return coConstraintsTable;
  }

  public void setCoConstraintsTable(CoConstraintsTable coConstraintsTable) {
    this.coConstraintsTable = coConstraintsTable;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  /**
   * @return the oldComments
   */
  public List<Comment> getOldComments() {
    return oldComments;
  }

  /**
   * @param oldComments the oldComments to set
   */
  public void setOldComments(List<Comment> oldComments) {
    this.oldComments = oldComments;
  }

  /**
   * @return the comments
   */
  public List<Comment> getComments() {
    return comments;
  }

  /**
   * @param comments the comments to set
   */
  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public Predicate getPredicate() {
    return predicate;
  }

  public void setPredicate(Predicate predicate) {
    this.predicate = predicate;

  }



}
