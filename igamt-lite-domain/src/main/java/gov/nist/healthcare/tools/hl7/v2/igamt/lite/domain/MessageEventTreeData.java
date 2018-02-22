package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public class MessageEventTreeData {
	  String id;
	  String name;
	  String structId;
	  String parentStructId;
	  String type;
	  String description;

	  
	  public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStructId() {
		return structId;
	}

	public void setStructId(String structId) {
		this.structId = structId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentStructId() {
		return parentStructId;
	}

	public void setParentStructId(String parentStructId) {
		this.parentStructId = parentStructId;
	}

	  public MessageEventTreeData() {
		super();
		// TODO Auto-generated constructor stub
	}


}
