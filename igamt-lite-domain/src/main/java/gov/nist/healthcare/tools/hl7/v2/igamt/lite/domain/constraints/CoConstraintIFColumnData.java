package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class CoConstraintIFColumnData {

	private ValueData valueData;
	private String bindingLocation;

	public CoConstraintIFColumnData() {
		super();
	}

	public ValueData getValueData() {
		return valueData;
	}

	public void setValueData(ValueData valueData) {
		this.valueData = valueData;
	}

	public String getBindingLocation() {
		return bindingLocation;
	}

	public void setBindingLocation(String bindingLocation) {
		this.bindingLocation = bindingLocation;
	}

}
