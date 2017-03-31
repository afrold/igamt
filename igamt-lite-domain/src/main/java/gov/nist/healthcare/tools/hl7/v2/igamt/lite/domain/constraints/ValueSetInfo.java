package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBindingStrength;

public class ValueSetInfo {

	private String tableId;
	private ValueSetBindingStrength bindingStrength;

	public ValueSetInfo() {
		super();
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public ValueSetBindingStrength getBindingStrength() {
		return bindingStrength;
	}

	public void setBindingStrength(ValueSetBindingStrength bindingStrength) {
		this.bindingStrength = bindingStrength;
	}

}
