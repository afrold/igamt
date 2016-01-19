package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

public class IGDocumentNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public IGDocumentNotFoundException(String id) {
		super("Unknown IGDocument with id " + id);
	}

	public IGDocumentNotFoundException(Exception error) {
		super(error);
	}

}
