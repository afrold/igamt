package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class ChangesNotDoneException extends Exception {
	private static final long serialVersionUID = 1L;

	public ChangesNotDoneException(String error) {
		super(error);
	}

	public ChangesNotDoneException(Exception error) {
		super(error);
	}

}
