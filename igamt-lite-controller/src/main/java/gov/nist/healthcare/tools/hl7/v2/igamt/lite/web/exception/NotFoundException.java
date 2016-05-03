package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class NotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public NotFoundException(String error) {
		super(error);
	}

	public NotFoundException(Exception error) {
		super(error);
	}

}
