package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.ProfilePropertySaveError;

import java.util.List;

public class ProfileSaveException extends Exception {
	private static final long serialVersionUID = 1L;

	private List<ProfilePropertySaveError> errors = null;

	public ProfileSaveException(String error) {
		super(error);
	}

	public ProfileSaveException(List<ProfilePropertySaveError> errors) {
		super();
		this.errors = errors;
	}

	public List<ProfilePropertySaveError> getErrors() {
		return errors;
	}

	public void setErrors(List<ProfilePropertySaveError> errors) {
		this.errors = errors;
	}

}
