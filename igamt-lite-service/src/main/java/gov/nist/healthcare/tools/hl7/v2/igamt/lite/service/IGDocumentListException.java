package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.IGDocumentPropertySaveError;

import java.util.List;

public class IGDocumentListException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<IGDocumentPropertySaveError> errors = null;

	public IGDocumentListException(String error) {
		super(error);
	} 
	
	public IGDocumentListException(Exception error) {
		super(error);
	}
	

	public IGDocumentListException(List<IGDocumentPropertySaveError> errors) {
		super();
		this.errors = errors;
	}

	public List<IGDocumentPropertySaveError> getErrors() {
		return errors;
	}

	public void setErrors(List<IGDocumentPropertySaveError> errors) {
		this.errors = errors;
	}

}
