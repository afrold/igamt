package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

public class ProfileNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProfileNotFoundException(String id) {
		super("Unknown Profile with id " + id);
	}

	public ProfileNotFoundException(Exception error) {
		super(error);
	}

}
