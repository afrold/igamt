package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class DatatypeNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatatypeNotFoundException(String error) {
		super(error);
	}

	public DatatypeNotFoundException(Exception error) {
		super(error);
	}

}
