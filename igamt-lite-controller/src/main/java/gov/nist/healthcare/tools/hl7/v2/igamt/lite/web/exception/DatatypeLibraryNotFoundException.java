package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class DatatypeLibraryNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatatypeLibraryNotFoundException(String error) {
		super(error);
	}

	public DatatypeLibraryNotFoundException(Exception error) {
		super(error);
	}

}
