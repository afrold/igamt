package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import org.springframework.data.annotation.Id;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

public class CoConstraintColumnDefinition {

	@Id
	private String id;
	private String path;
	private String constraintPath;
	private String type; // field or component
	private String constraintType; //valueset or value
	private String name;
	private Usage usage;
	private String dtId;
	private boolean isPrimitive;
	private boolean isDMReference;
	private boolean isDMTarget;

	public CoConstraintColumnDefinition() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getConstraintPath() {
		return constraintPath;
	}

	public void setConstraintPath(String constraintPath) {
		this.constraintPath = constraintPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Usage getUsage() {
		return usage;
	}

	public void setUsage(Usage usage) {
		this.usage = usage;
	}

	public String getDtId() {
		return dtId;
	}

	public void setDtId(String dtId) {
		this.dtId = dtId;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public void setPrimitive(boolean isPrimitive) {
		this.isPrimitive = isPrimitive;
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}

	public boolean isDMReference() {
		return isDMReference;
	}

	public void setDMReference(boolean isDMReference) {
		this.isDMReference = isDMReference;
	}

	public boolean isDMTarget() {
		return isDMTarget;
	}

	public void setDMTarget(boolean isDMTarget) {
		this.isDMTarget = isDMTarget;
	}

}
