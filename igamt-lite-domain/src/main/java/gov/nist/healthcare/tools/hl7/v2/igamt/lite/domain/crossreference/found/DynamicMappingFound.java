package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VariesMapItem;

public class DynamicMappingFound {
  private SegmentFound segmentFound;
  private VariesMapItem mappingStructure;
  private DynamicMappingItem dynamicMappingItem;

  public SegmentFound getSegmentFound() {
    return segmentFound;
  }

  public void setSegmentFound(SegmentFound segmentFound) {
    this.segmentFound = segmentFound;
  }

  public VariesMapItem getMappingStructure() {
    return mappingStructure;
  }

  public void setMappingStructure(VariesMapItem mappingStructure) {
    this.mappingStructure = mappingStructure;
  }

  public DynamicMappingItem getDynamicMappingItem() {
    return dynamicMappingItem;
  }

  public void setDynamicMappingItem(DynamicMappingItem dynamicMappingItem) {
    this.dynamicMappingItem = dynamicMappingItem;
  }


}
