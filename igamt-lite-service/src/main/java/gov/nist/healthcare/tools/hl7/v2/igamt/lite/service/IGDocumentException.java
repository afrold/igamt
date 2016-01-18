package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

public class IGDocumentException extends Exception {
	private static final long serialVersionUID = 1L;

	public IGDocumentException(String id) {
		super("Unknown IGDocument with id " + id);
	}

	public IGDocumentException(Exception error) {
		super(error);
	}

}
