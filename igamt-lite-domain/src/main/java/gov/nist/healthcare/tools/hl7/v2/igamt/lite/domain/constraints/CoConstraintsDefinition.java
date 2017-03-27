package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

public class CoConstraintsDefinition {

	private List<SimpleConstraint> ifConstraintList = new ArrayList<SimpleConstraint>();
	private List<SimpleConstraint> thenConstraintList = new ArrayList<SimpleConstraint>();
	private SimpleConstraint dynamicDatatypeConstrinat;
	private List<CoConstraintDesc> listCoConstraintDesc = new ArrayList<CoConstraintDesc>();

	public CoConstraintsDefinition() {
		super();
	}

	public List<SimpleConstraint> getIfConstraintList() {
		return ifConstraintList;
	}

	public void setIfConstraintList(List<SimpleConstraint> ifConstraintList) {
		this.ifConstraintList = ifConstraintList;
	}

	public List<SimpleConstraint> getThenConstraintList() {
		return thenConstraintList;
	}

	public void setThenConstraintList(List<SimpleConstraint> thenConstraintList) {
		this.thenConstraintList = thenConstraintList;
	}

	public SimpleConstraint getDynamicDatatypeConstrinat() {
		return dynamicDatatypeConstrinat;
	}

	public void setDynamicDatatypeConstrinat(SimpleConstraint dynamicDatatypeConstrinat) {
		this.dynamicDatatypeConstrinat = dynamicDatatypeConstrinat;
	}

	public List<CoConstraintDesc> getListCoConstraintDesc() {
		return listCoConstraintDesc;
	}

	public void setListCoConstraintDesc(List<CoConstraintDesc> listCoConstraintDesc) {
		this.listCoConstraintDesc = listCoConstraintDesc;
	}

}
