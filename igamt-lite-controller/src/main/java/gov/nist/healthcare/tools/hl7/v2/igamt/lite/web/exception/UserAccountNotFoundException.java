package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class UserAccountNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public UserAccountNotFoundException() {
		super();
	}

	public UserAccountNotFoundException(String error) {
		super(error);
	}

	public UserAccountNotFoundException(Exception error) {
		super(error);
	}

}
