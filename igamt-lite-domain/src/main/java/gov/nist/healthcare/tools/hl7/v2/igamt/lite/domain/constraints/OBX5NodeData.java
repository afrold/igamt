package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

public class OBX5NodeData {

	private String location;
	private String type;
	private List<String> values = new ArrayList<String>();
	private List<ValueSetData> valueSets = new ArrayList<ValueSetData>();

	public OBX5NodeData() {
		super();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public List<ValueSetData> getValueSets() {
		return valueSets;
	}

	public void setValueSets(List<ValueSetData> valueSets) {
		this.valueSets = valueSets;
	}

}
