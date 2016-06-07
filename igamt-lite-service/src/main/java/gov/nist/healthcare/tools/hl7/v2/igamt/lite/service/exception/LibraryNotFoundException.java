package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception;

public class LibraryNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  public LibraryNotFoundException(String id) {
    super("Unknown datatype library with id " + id);
  }

  public LibraryNotFoundException(Exception error) {
    super(error);
  }

}
