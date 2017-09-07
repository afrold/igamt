package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class DynamicMappingDefinition implements java.io.Serializable, Cloneable {

  /**
   * 
   */
  private static final long serialVersionUID = -2599483305053533142L;
  
  private VariesMapItem mappingStructure = new VariesMapItem();
  private List<DynamicMappingItem> dynamicMappingItems = new ArrayList<DynamicMappingItem>();

  public DynamicMappingDefinition() {
    super();
  }

  public VariesMapItem getMappingStructure() {
    return mappingStructure;
  }

  public void setMappingStructure(VariesMapItem mappingStructure) {
    this.mappingStructure = mappingStructure;
  }

  public List<DynamicMappingItem> getDynamicMappingItems() {
    return dynamicMappingItems;
  }

  public void setDynamicMappingItems(List<DynamicMappingItem> dynamicMappingItems) {
    this.dynamicMappingItems = dynamicMappingItems;
  }

  @Override
  public DynamicMappingDefinition clone() throws CloneNotSupportedException {
    DynamicMappingDefinition cloned = new DynamicMappingDefinition();
    cloned.setDynamicMappingItems(new ArrayList<DynamicMappingItem>());
    for(DynamicMappingItem item:dynamicMappingItems){
      if(item != null) cloned.getDynamicMappingItems().add(item.clone()); 
    }
   
    if(mappingStructure != null) cloned.setMappingStructure(this.mappingStructure.clone());
    return cloned;
  }

}
