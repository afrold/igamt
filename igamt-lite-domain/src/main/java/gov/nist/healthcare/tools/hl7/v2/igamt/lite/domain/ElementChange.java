package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.Map;


public class ElementChange {

	/*
	 * Id of the element that has modifications 
	 */
	private String id;
	
	/*
	 * Dictionary where :
	 * 		key is the name of the getter of the field that has changed 
	 * 		value is a dictionary with keys "old" and "new" for the new and old values
	 */
	private Map<String, Map<String, String>> change; 
	
	/*
	 * Record the type of data corresponding to the id (Message, Datatype, Segment...
	 */
	private String type;

	public ElementChange(String id, String type) {
		super();
		this.id = id;
		this.type = type;
		this.change = new HashMap<String, Map<String,String>>();
	}
	
	public void recordChange(String field, String oldValue, String newValue){
		Map<String,String> values = new HashMap<String,String>();
		values.put("old", oldValue);
		values.put("new", newValue);
		this.change.put(field, values);
	}
	
	public Integer countChanges(){
		return this.change.size();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ElementChange [id=" + id + ", type="
				+ type + ", change=" + change +  "]";
	}



}
