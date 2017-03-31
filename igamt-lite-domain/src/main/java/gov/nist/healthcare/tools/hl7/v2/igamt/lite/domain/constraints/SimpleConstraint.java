package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import org.springframework.data.annotation.Id;

public class SimpleConstraint {

	@Id
	private String id;
	private String targetPath;
	private String targetConstraintPath;
	private String targetType;
	private String targetName;

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

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
}
