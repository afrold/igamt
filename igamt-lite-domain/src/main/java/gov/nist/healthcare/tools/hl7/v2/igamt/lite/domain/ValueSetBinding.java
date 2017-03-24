package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author Jungyub Woo
 *
 */
public class ValueSetBinding {
	protected String location;
	protected String tableId;
	protected String bindingLocation;
	protected ValueSetBindingStrength bindingStrength;
	protected Usage usage;

	public ValueSetBinding() {
		super();
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

	public String getBindingLocation() {
		return bindingLocation;
	}

	public void setBindingLocation(String bindingLocation) {
		this.bindingLocation = bindingLocation;
	}

	public ValueSetBindingStrength getBindingStrength() {
		return bindingStrength;
	}

	public void setBindingStrength(ValueSetBindingStrength bindingStrength) {
		this.bindingStrength = bindingStrength;
	}
	
	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public void setBindingStrength(String bindingStrength) {
		if(bindingStrength == null){
			this.bindingStrength = ValueSetBindingStrength.R;
		}else if(bindingStrength.equals("R")){
			this.bindingStrength = ValueSetBindingStrength.R;
		}else if(bindingStrength.equals("S")){
			this.bindingStrength = ValueSetBindingStrength.S;
		}else if(bindingStrength.equals("U")){
			this.bindingStrength = ValueSetBindingStrength.U;
		}else {
			this.bindingStrength = ValueSetBindingStrength.R;			
		}
	}

}
