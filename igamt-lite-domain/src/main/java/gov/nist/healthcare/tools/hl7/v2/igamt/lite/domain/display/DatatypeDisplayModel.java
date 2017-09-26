package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.Set;

public class DatatypeDisplayModel {
  private DisplayModelMetaData metadata;
  private Set<ComponentTreeNode> structure;

  public DisplayModelMetaData getMetadata() {
    return metadata;
  }

  public void setMetadata(DisplayModelMetaData metadata) {
    this.metadata = metadata;
  }

  public Set<ComponentTreeNode> getStructure() {
    return structure;
  }

  public void setStructure(Set<ComponentTreeNode> structure) {
    this.structure = structure;
  }
}
