package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

public class SimpleConstraint {

	private String id;
	private String targetPath;
	private String targetConstraintPath;
	private String targetType;
	private String targetName;
	private String type = "value"; // valueset, value, dm
	private boolean isEditable = false;

	public SimpleConstraint() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getTargetConstraintPath() {
		return targetConstraintPath;
	}

	public void setTargetConstraintPath(String targetConstraintPath) {
		this.targetConstraintPath = targetConstraintPath;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

}
