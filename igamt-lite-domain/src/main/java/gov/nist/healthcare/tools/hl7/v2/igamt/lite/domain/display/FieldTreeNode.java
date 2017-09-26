package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.HashSet;
import java.util.Set;

public class FieldTreeNode extends TreeNode {

  private FieldDisplayModel data;
  private Set<ComponentTreeNode> children = new HashSet<ComponentTreeNode>();

  public FieldDisplayModel getData() {
    return data;
  }

  public void setData(FieldDisplayModel data) {
    this.data = data;
  }

  public Set<ComponentTreeNode> getChildren() {
    return children;
  }

  public void setChildren(Set<ComponentTreeNode> children) {
    this.children = children;
  }


}
