package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.IGDocumentPropertySaveError;

import java.util.List;

public class IGDocumentDeleteException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<IGDocumentPropertySaveError> errors = null;

	public IGDocumentDeleteException(String error) {
		super(error);
	} 
	
	public IGDocumentDeleteException(Exception error) {
		super(error);
	}
	

	public IGDocumentDeleteException(List<IGDocumentPropertySaveError> errors) {
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
