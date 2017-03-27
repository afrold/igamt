package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

public class DynamicMappingDefinition {

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

}
