package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class ValueData {

	private String value;
	private String bindingLocation;

	public ValueData() {
		super();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getBindingLocation() {
		return bindingLocation;
	}

	public void setBindingLocation(String bindingLocation) {
		this.bindingLocation = bindingLocation;
	}

}
