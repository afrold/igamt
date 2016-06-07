package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception;

public class LibraryException extends Exception {
  private static final long serialVersionUID = 1L;

  public LibraryException(String id) {
    super("Unknown datatype library with id " + id);
  }

  public LibraryException(Exception error) {
    super(error);
  }

}
