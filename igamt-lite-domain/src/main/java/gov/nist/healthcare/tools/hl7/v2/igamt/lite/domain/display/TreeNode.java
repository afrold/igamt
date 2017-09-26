package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

public class TreeNode {
  /*
   * label?: string; data?: any; icon?: any; expandedIcon?: any; collapsedIcon?: any; children?:
   * TreeNode[]; leaf?: boolean; expanded?: boolean; type?: string; parent?: TreeNode;
   * partialSelected?: boolean; styleClass?: string; draggable?: boolean; droppable?: boolean;
   * selectable?: boolean;
   */

  private String label;
  private String icon;
  private String expandedIcon;
  private String collapsedIcon;
  private boolean leaf;
  private boolean expanded;
  private boolean type;
  private TreeNode parent;
  private boolean partialSelected;
  private String styleClass;
  private boolean draggable;
  private boolean droppable;
  private boolean selectable;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getExpandedIcon() {
    return expandedIcon;
  }

  public void setExpandedIcon(String expandedIcon) {
    this.expandedIcon = expandedIcon;
  }

  public String getCollapsedIcon() {
    return collapsedIcon;
  }

  public void setCollapsedIcon(String collapsedIcon) {
    this.collapsedIcon = collapsedIcon;
  }

  public boolean isLeaf() {
    return leaf;
  }

  public void setLeaf(boolean leaf) {
    this.leaf = leaf;
  }

  public boolean isExpanded() {
    return expanded;
  }

  public void setExpanded(boolean expanded) {
    this.expanded = expanded;
  }

  public boolean isType() {
    return type;
  }

  public void setType(boolean type) {
    this.type = type;
  }

  public TreeNode getParent() {
    return parent;
  }

  public void setParent(TreeNode parent) {
    this.parent = parent;
  }

  public boolean isPartialSelected() {
    return partialSelected;
  }

  public void setPartialSelected(boolean partialSelected) {
    this.partialSelected = partialSelected;
  }

  public String getStyleClass() {
    return styleClass;
  }

  public void setStyleClass(String styleClass) {
    this.styleClass = styleClass;
  }

  public boolean isDraggable() {
    return draggable;
  }

  public void setDraggable(boolean draggable) {
    this.draggable = draggable;
  }

  public boolean isDroppable() {
    return droppable;
  }

  public void setDroppable(boolean droppable) {
    this.droppable = droppable;
  }

  public boolean isSelectable() {
    return selectable;
  }

  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
  }


}
