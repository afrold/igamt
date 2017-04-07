package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.List;
import java.util.Map;

public class CoConstraintsTable {

	private CoConstraintColumnDefinition ifColumnDefinition;
	private List<CoConstraintColumnDefinition> thenColumnDefinitionList;
	private List<CoConstraintUserColumnDefinition> userColumnDefinitionList;

	private int rowSize = 0;
	private List<CoConstraintIFColumnData> ifColumnData;
	private Map<String, List<CoConstraintTHENColumnData>> thenMapData;
	private Map<String, List<CoConstraintUSERColumnData>> userMapData;

	public CoConstraintsTable() {
		super();
	}

	public int getRowSize() {
		return rowSize;
	}

	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}

	public CoConstraintColumnDefinition getIfColumnDefinition() {
		return ifColumnDefinition;
	}

	public void setIfColumnDefinition(CoConstraintColumnDefinition ifColumnDefinition) {
		this.ifColumnDefinition = ifColumnDefinition;
	}

	public List<CoConstraintColumnDefinition> getThenColumnDefinitionList() {
		return thenColumnDefinitionList;
	}

	public void setThenColumnDefinitionList(List<CoConstraintColumnDefinition> thenColumnDefinitionList) {
		this.thenColumnDefinitionList = thenColumnDefinitionList;
	}

	public List<CoConstraintUserColumnDefinition> getUserColumnDefinitionList() {
		return userColumnDefinitionList;
	}

	public void setUserColumnDefinitionList(List<CoConstraintUserColumnDefinition> userColumnDefinitionList) {
		this.userColumnDefinitionList = userColumnDefinitionList;
	}

	public List<CoConstraintIFColumnData> getIfColumnData() {
		return ifColumnData;
	}

	public void setIfColumnData(List<CoConstraintIFColumnData> ifColumnData) {
		this.ifColumnData = ifColumnData;
	}

	public Map<String, List<CoConstraintTHENColumnData>> getThenMapData() {
		return thenMapData;
	}

	public void setThenMapData(Map<String, List<CoConstraintTHENColumnData>> thenMapData) {
		this.thenMapData = thenMapData;
	}

	public Map<String, List<CoConstraintUSERColumnData>> getUserMapData() {
		return userMapData;
	}

	public void setUserMapData(Map<String, List<CoConstraintUSERColumnData>> userMapData) {
		this.userMapData = userMapData;
	}

}
