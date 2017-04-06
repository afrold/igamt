package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.util.ArrayList;
import java.util.List;

public class CoConstraintTHENColumnData {
	private ValueData valueData;
	private List<ValueSetData> valueSets = new ArrayList<ValueSetData>();
	private String datatypeId;

	public CoConstraintTHENColumnData() {
		super();
	}

	public ValueData getValueData() {
		return valueData;
	}

	public void setValue(ValueData valueData) {
		this.valueData = valueData;
	}

	public List<ValueSetData> getValueSets() {
		return valueSets;
	}

	public void setValueSets(List<ValueSetData> valueSets) {
		this.valueSets = valueSets;
	}

	public String getDatatypeId() {
		return datatypeId;
	}

	public void setDatatypeId(String datatypeId) {
		this.datatypeId = datatypeId;
	}

}
