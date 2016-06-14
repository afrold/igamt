package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class ProfileException extends Exception {
	private static final long serialVersionUID = 1L;

	public ProfileException(String error) {
		super(error);
	}

	public ProfileException(Exception error) {
		super(error);
	}

}
