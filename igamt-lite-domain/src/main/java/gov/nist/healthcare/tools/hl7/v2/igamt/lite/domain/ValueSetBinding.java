package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

/**
 * @author Jungyub Woo
 *
 */
public class ValueSetBinding extends ValueSetOrSingleCodeBinding  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7475781965031235387L;
	private String bindingLocation;
	private ValueSetBindingStrength bindingStrength;

	public ValueSetBinding() {
		super();
		this.type = Constant.VALUESET;
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

//	public void setBindingStrength(String bindingStrength) {
//		if (bindingStrength == null) {
//			this.bindingStrength = ValueSetBindingStrength.R;
//		} else if (bindingStrength.equals("R")) {
//			this.bindingStrength = ValueSetBindingStrength.R;
//		} else if (bindingStrength.equals("S")) {
//			this.bindingStrength = ValueSetBindingStrength.S;
//		} else if (bindingStrength.equals("U")) {
//			this.bindingStrength = ValueSetBindingStrength.U;
//		} else {
//			this.bindingStrength = ValueSetBindingStrength.R;
//		}
//	}
	
	public ValueSetBinding clone(){
		ValueSetBinding cloned = new ValueSetBinding();
		cloned.setBindingLocation(bindingLocation);
		cloned.setBindingStrength(bindingStrength);
		cloned.setId(id);
		cloned.setLocation(location);
		cloned.setTableId(tableId);
		cloned.setType(type);
		cloned.setUsage(usage);
		return cloned;
	}
	
}
