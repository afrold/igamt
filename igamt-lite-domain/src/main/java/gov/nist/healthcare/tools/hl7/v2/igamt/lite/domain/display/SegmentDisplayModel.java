package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;

public class SegmentDisplayModel {
  private DisplayModelMetaData metadata;
  private Set<FieldTreeNode> structure;
  private DynamicMappingDefinition dynamicMappingDefinition;
  private CoConstraintsTable coConstraintsTable = new CoConstraintsTable();

  public DisplayModelMetaData getMetadata() {
    return metadata;
  }

  public void setMetadata(DisplayModelMetaData metadata) {
    this.metadata = metadata;
  }

  public Set<FieldTreeNode> getStructure() {
    return structure;
  }

  public void setStructure(Set<FieldTreeNode> structure) {
    this.structure = structure;
  }

  public DynamicMappingDefinition getDynamicMappingDefinition() {
    return dynamicMappingDefinition;
  }

  public void setDynamicMappingDefinition(DynamicMappingDefinition dynamicMappingDefinition) {
    this.dynamicMappingDefinition = dynamicMappingDefinition;
  }

  public CoConstraintsTable getCoConstraintsTable() {
    return coConstraintsTable;
  }

  public void setCoConstraintsTable(CoConstraintsTable coConstraintsTable) {
    this.coConstraintsTable = coConstraintsTable;
  }


}
