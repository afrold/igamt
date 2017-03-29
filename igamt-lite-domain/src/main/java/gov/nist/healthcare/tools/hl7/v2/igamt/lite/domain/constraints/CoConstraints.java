package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class CoConstraints {

	public CoConstraints() {
		super();
		
		columnList = new ArrayList<CoConstraintsColumn>();
		constraints = new ArrayList<CoConstraint>();
	}
	
	private List<CoConstraintsColumn> columnList = new ArrayList<CoConstraintsColumn>();
	private List<CoConstraint> constraints = new ArrayList<CoConstraint>();
	
	public List<CoConstraintsColumn> getColumnList() {
		return columnList;
	}
	public void setColumnList(List<CoConstraintsColumn> columnList) {
		this.columnList = columnList;
	}
	public List<CoConstraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<CoConstraint> constraints) {
		this.constraints = constraints;
	}
}
