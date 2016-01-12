/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 15, 2015
 */
public class IGDocumentPropertySaveError {

	private String targetId;
	private String propertyName;
	private String targetType;
	private String propertyValue;
	private String command;
	private String errorMsg;

	/**
	 * @param targetId
	 * @param fieldName
	 * @param targetType
	 * @param fieldValue
	 * @param command
	 */
	public IGDocumentPropertySaveError(String targetId, String targetType,
			String propertyName, String propertyValue, String command) {
		super();
		this.targetId = targetId;
		this.propertyName = propertyName;
		this.targetType = targetType;
		this.propertyValue = propertyValue;
		this.command = command;
	}

	/**
	 * @param targetId
	 * @param errorMsg
	 */
	public IGDocumentPropertySaveError(String targetId, String targetType,
			String errorMsg) {
		super();
		this.targetId = targetId;
		this.targetType = targetType;
		this.errorMsg = errorMsg;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "IGDocumentPropertySaveError [targetId=" + targetId
				+ ", propertyName=" + propertyName + ", targetType="
				+ targetType + ", propertyValue=" + propertyValue
				+ ", command=" + command + ", errorMsg=" + errorMsg + "]";
	}

}
