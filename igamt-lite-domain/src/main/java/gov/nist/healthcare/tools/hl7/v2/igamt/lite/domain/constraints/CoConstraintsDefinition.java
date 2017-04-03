package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoConstraintsDefinition {

	private SimpleConstraint ifConstraint;
	private List<SimpleConstraint> thenConstraintList = new ArrayList<SimpleConstraint>();
	private List<CoConstraintDesc> listCoConstraintDesc = new ArrayList<CoConstraintDesc>();
	
	private int rowSize = 0;
	private List<CCCellValueObjIf> columnDataIf = new ArrayList<CCCellValueObjIf>();
	private Map<String, List<CCCellValueObjThen>> mapDataThen = new HashMap<String, List<CCCellValueObjThen>>();
	private Map<String, List<CCCellValueObjDesc>> mapDataDesc = new HashMap<String, List<CCCellValueObjDesc>>();

	public CoConstraintsDefinition() {
		super();
	}

	public List<SimpleConstraint> getThenConstraintList() {
		return thenConstraintList;
	}

	public void setThenConstraintList(List<SimpleConstraint> thenConstraintList) {
		this.thenConstraintList = thenConstraintList;
	}

	public List<CoConstraintDesc> getListCoConstraintDesc() {
		return listCoConstraintDesc;
	}

	public void setListCoConstraintDesc(List<CoConstraintDesc> listCoConstraintDesc) {
		this.listCoConstraintDesc = listCoConstraintDesc;
	}

	public SimpleConstraint getIfConstraint() {
		return ifConstraint;
	}

	public void setIfConstraint(SimpleConstraint ifConstraint) {
		this.ifConstraint = ifConstraint;
	}

	public int getRowSize() {
		return rowSize;
	}

	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}

	public List<CCCellValueObjIf> getColumnDataIf() {
		return columnDataIf;
	}

	public void setColumnDataIf(List<CCCellValueObjIf> columnDataIf) {
		this.columnDataIf = columnDataIf;
	}

	public Map<String, List<CCCellValueObjThen>> getMapDataThen() {
		return mapDataThen;
	}

	public void setMapDataThen(Map<String, List<CCCellValueObjThen>> mapDataThen) {
		this.mapDataThen = mapDataThen;
	}

	public Map<String, List<CCCellValueObjDesc>> getMapDataDesc() {
		return mapDataDesc;
	}

	public void setMapDataDesc(Map<String, List<CCCellValueObjDesc>> mapDataDesc) {
		this.mapDataDesc = mapDataDesc;
	}
}
