package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class LibraryNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public LibraryNotFoundException(String error) {
		super(error);
	}

	public LibraryNotFoundException(Exception error) {
		super(error);
	}

}
