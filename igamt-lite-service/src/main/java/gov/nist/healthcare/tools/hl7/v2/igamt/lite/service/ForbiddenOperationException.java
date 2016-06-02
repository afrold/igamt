package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.IGDocumentPropertySaveError;

import java.util.List;

public class ForbiddenOperationException extends Exception {
  private static final long serialVersionUID = 1L;

  public ForbiddenOperationException() {
    super();
  }


  public ForbiddenOperationException(String error) {
    super(error);
  }

  public ForbiddenOperationException(Exception error) {
    super(error);
  }



}
