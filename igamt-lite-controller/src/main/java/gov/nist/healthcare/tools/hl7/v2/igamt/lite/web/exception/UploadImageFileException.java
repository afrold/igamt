package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception;

public class UploadImageFileException extends Exception {
	private static final long serialVersionUID = 1L;

	public UploadImageFileException(String error) {
		super(error);
	}

	public UploadImageFileException(Exception error) {
		super(error);
	}

}
