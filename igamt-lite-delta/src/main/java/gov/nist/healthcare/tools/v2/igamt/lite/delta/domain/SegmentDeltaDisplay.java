package gov.nist.healthcare.tools.v2.igamt.lite.delta.domain;

import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display.FieldTreeNode;

public class SegmentDeltaDisplay {
	  private Set<FieldTreeNode> structure;
	  private DynamicMappingDefinition leftDynamicMappingDefinition;
	  private CoConstraintsTable leftCoConstraintsTable = new CoConstraintsTable();
	  
	  private DynamicMappingDefinition rightDynamicMappingDefinition;
	  private CoConstraintsTable rightCoConstraintsTable = new CoConstraintsTable();
	  /**
	 * @return the leftDynamicMappingDefinition
	 */
	public DynamicMappingDefinition getLeftDynamicMappingDefinition() {
		return leftDynamicMappingDefinition;
	}

	/**
	 * @param leftDynamicMappingDefinition the leftDynamicMappingDefinition to set
	 */
	public void setLeftDynamicMappingDefinition(DynamicMappingDefinition leftDynamicMappingDefinition) {
		this.leftDynamicMappingDefinition = leftDynamicMappingDefinition;
	}

	/**
	 * @return the leftCoConstraintsTable
	 */
	public CoConstraintsTable getLeftCoConstraintsTable() {
		return leftCoConstraintsTable;
	}

	/**
	 * @param leftCoConstraintsTable the leftCoConstraintsTable to set
	 */
	public void setLeftCoConstraintsTable(CoConstraintsTable leftCoConstraintsTable) {
		this.leftCoConstraintsTable = leftCoConstraintsTable;
	}

	/**
	 * @return the rightDynamicMappingDefinition
	 */
	public DynamicMappingDefinition getRightDynamicMappingDefinition() {
		return rightDynamicMappingDefinition;
	}

	/**
	 * @param rightDynamicMappingDefinition the rightDynamicMappingDefinition to set
	 */
	public void setRightDynamicMappingDefinition(DynamicMappingDefinition rightDynamicMappingDefinition) {
		this.rightDynamicMappingDefinition = rightDynamicMappingDefinition;
	}

	/**
	 * @return the rightCoConstraintsTable
	 */
	public CoConstraintsTable getRightCoConstraintsTable() {
		return rightCoConstraintsTable;
	}

	/**
	 * @param rightCoConstraintsTable the rightCoConstraintsTable to set
	 */
	public void setRightCoConstraintsTable(CoConstraintsTable rightCoConstraintsTable) {
		this.rightCoConstraintsTable = rightCoConstraintsTable;
	}

	

	  public Set<FieldTreeNode> getStructure() {
	    return structure;
	  }

	  public void setStructure(Set<FieldTreeNode> structure) {
	    this.structure = structure;
	  }
	  


}
