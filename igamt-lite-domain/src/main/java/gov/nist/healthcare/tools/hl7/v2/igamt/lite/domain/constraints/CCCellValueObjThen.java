package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

public class CCCellValueObjThen {
	private String type; // v, vs, dmr, dmd

	private List<String> values = new ArrayList<String>();
	private List<ValueSetInfo> valueSets = new ArrayList<ValueSetInfo>();

	private String datatypeBaseName;
	private String datatypeId;
	
	private List<OBX5NodeData> listOBX5NodeData = new ArrayList<OBX5NodeData>();

	public CCCellValueObjThen() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public List<ValueSetInfo> getValueSets() {
		return valueSets;
	}

	public void setValueSets(List<ValueSetInfo> valueSets) {
		this.valueSets = valueSets;
	}

	public String getDatatypeBaseName() {
		return datatypeBaseName;
	}

	public void setDatatypeBaseName(String datatypeBaseName) {
		this.datatypeBaseName = datatypeBaseName;
	}

	public String getDatatypeId() {
		return datatypeId;
	}

	public void setDatatypeId(String datatypeId) {
		this.datatypeId = datatypeId;
	}

	public List<OBX5NodeData> getListOBX5NodeData() {
		return listOBX5NodeData;
	}

	public void setListOBX5NodeData(List<OBX5NodeData> listOBX5NodeData) {
		this.listOBX5NodeData = listOBX5NodeData;
	}
}
