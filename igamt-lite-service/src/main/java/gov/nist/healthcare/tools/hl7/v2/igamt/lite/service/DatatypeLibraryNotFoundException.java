package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

public class DatatypeLibraryNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatatypeLibraryNotFoundException(String id) {
		super("Unknown datatype library with id " + id);
	}

	public DatatypeLibraryNotFoundException(Exception error) {
		super(error);
	}

}
