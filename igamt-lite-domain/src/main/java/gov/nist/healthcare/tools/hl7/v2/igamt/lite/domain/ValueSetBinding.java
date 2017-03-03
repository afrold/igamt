package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author Jungyub Woo
 *
 */
public class ValueSetBinding {
	/*
	 * Example 1: MSH-9.1 <------ AAAA Table
	 * 
	 * bindingType: S targetId: ObjectId of MSH Segment location: 9.1 tableId:
	 * ObjectId of AAAA Table bindingLocation: according to Binding
	 * Configuration bindingStrength: One of R/S/U
	 */

	protected ValueSetBindingType bindingType;
	protected String targetId;
	protected String location;
	protected String tableId;
	protected String bindingLocatoin;
	protected ValueSetBindingStrength bindingStrength;

	public ValueSetBinding() {
		super();
	}

	public ValueSetBindingType getBindingType() {
		return bindingType;
	}

	public void setBindingType(ValueSetBindingType bindingType) {
		this.bindingType = bindingType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getBindingLocatoin() {
		return bindingLocatoin;
	}

	public void setBindingLocatoin(String bindingLocatoin) {
		this.bindingLocatoin = bindingLocatoin;
	}

	public ValueSetBindingStrength getBindingStrength() {
		return bindingStrength;
	}

	public void setBindingStrength(ValueSetBindingStrength bindingStrength) {
		this.bindingStrength = bindingStrength;
	}

}
