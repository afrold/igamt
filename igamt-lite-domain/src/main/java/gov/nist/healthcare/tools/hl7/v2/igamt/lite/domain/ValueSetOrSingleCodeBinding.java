package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = ValueSetBinding.class, name = Constant.VALUESET),
    		   @JsonSubTypes.Type(value = SingleCodeBinding.class, name = Constant.SINGLECODE)})
public abstract class ValueSetOrSingleCodeBinding {
	@Id
	protected String id;

	protected String location;
	protected String tableId;
	@Deprecated
	protected Usage usage;
	protected String type;
	
	public ValueSetOrSingleCodeBinding() {
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

	@Deprecated
	public Usage getUsage() {
		return usage;
	}

	@Deprecated
	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

}
