package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class ProfileNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProfileNotFoundException(String error) {
		super(error);
	}

	public ProfileNotFoundException(Exception error) {
		super(error);
	}

}
