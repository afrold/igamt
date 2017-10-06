/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web;

import java.util.List;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DatatypeLibrarySaveError;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 16, 2015
 */
public class LibrarySaveResponse extends ResponseMessage {

  private List<DatatypeLibrarySaveError> errors = null;

  private String date;

  private String scope;

  /**
   * @param type
   * @param text
   * @param resourceId
   * @param manualHandle
   * @param date
   * @param version
   */
  public LibrarySaveResponse(String date, String scope) {
    super(Type.success, "DatatypeLibrarySaved");
    this.date = date;
    this.scope = scope;
  }

  /**
   * @param type
   * @param text
   */
  public LibrarySaveResponse(Type type, String text) {
    super(type, text);
  }

  public LibrarySaveResponse(Type type, String text, String resourceId, String manualHandle,
      List<DatatypeLibrarySaveError> errors) {
    super(type, text, resourceId, manualHandle);
    this.errors = errors;
  }

  public LibrarySaveResponse(Type type, String text, String resourceId,
      List<DatatypeLibrarySaveError> errors) {
    super(type, text, resourceId);
    this.errors = errors;
  }

  public LibrarySaveResponse(Type type, String text, List<DatatypeLibrarySaveError> errors) {
    super(type, text);
    this.errors = errors;
  }

  public List<DatatypeLibrarySaveError> getErrors() {
    return errors;
  }

  public void setErrors(List<DatatypeLibrarySaveError> errors) {
    this.errors = errors;
  }

  @Override
  public String getDate() {
    return date;
  }

  @Override
  public void setDate(String date) {
    this.date = date;
  }

  @Override
  public String getScope() {
    return scope;
  }

  @Override
  public void setScope(String scope) {
    this.scope = scope;
  }
}
