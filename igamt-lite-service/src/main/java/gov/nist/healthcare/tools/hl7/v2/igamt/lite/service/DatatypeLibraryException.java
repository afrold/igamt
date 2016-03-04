package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

public class DatatypeLibraryException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatatypeLibraryException(String id) {
		super("Unknown datatype library with id " + id);
	}

	public DatatypeLibraryException(Exception error) {
		super(error);
	}

}
