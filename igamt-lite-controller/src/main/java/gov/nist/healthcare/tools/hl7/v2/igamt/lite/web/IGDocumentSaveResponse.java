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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.IGDocumentPropertySaveError;

import java.util.List;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 16, 2015
 */
public class IGDocumentSaveResponse extends ResponseMessage {

	private List<IGDocumentPropertySaveError> errors = null;

	private String date;

	private String version;

	/**
	 * @param type
	 * @param text
	 * @param resourceId
	 * @param manualHandle
	 * @param date
	 * @param version
	 */
	public IGDocumentSaveResponse(String date, String version) {
		super(Type.success, "igDocumentSaved");
		this.date = date;
		this.version = version;
	}

	/**
	 * @param type
	 * @param text
	 */
	public IGDocumentSaveResponse(Type type, String text) {
		super(type, text);
	}

	public IGDocumentSaveResponse(Type type, String text, String resourceId,
			String manualHandle, List<IGDocumentPropertySaveError> errors) {
		super(type, text, resourceId, manualHandle);
		this.errors = errors;
	}

	public IGDocumentSaveResponse(Type type, String text, String resourceId,
			List<IGDocumentPropertySaveError> errors) {
		super(type, text, resourceId);
		this.errors = errors;
	}

	public IGDocumentSaveResponse(Type type, String text,
			List<IGDocumentPropertySaveError> errors) {
		super(type, text);
		this.errors = errors;
	}

	public List<IGDocumentPropertySaveError> getErrors() {
		return errors;
	}

	public void setErrors(List<IGDocumentPropertySaveError> errors) {
		this.errors = errors;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
