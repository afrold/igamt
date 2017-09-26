package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import java.util.HashSet;
import java.util.Set;

public class ComponentTreeNode {
  private ComponentDisplayModel data;
  private Set<ComponentTreeNode> children = new HashSet<ComponentTreeNode>();

  public ComponentDisplayModel getData() {
    return data;
  }

  public void setData(ComponentDisplayModel data) {
    this.data = data;
  }

  public Set<ComponentTreeNode> getChildren() {
    return children;
  }

  public void setChildren(Set<ComponentTreeNode> children) {
    this.children = children;
  }


}
