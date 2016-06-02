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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 15, 2015
 */
public class ProfilePropertySaveError extends AbscractPropertySaveError {

  /**
   * @param targetId
   * @param fieldName
   * @param targetType
   * @param fieldValue
   * @param command
   */
  public ProfilePropertySaveError(String targetId, String targetType, String propertyName,
      String propertyValue, String command) {
    super(targetId, targetType, propertyName, propertyValue, command);
  }

  /**
   * @param targetId
   * @param errorMsg
   */
  public ProfilePropertySaveError(String targetId, String targetType, String errorMsg) {
    super(targetId, targetType, errorMsg);
  }

  @Override
  public String toString() {
    return "ProfilePropertySaveError [targetId=" + this.getTargetId() + ", propertyName="
        + this.getPropertyName() + ", targetType=" + this.getTargetType() + ", propertyValue="
        + this.getPropertyValue() + ", command=" + this.getCommand() + ", errorMsg="
        + this.getErrorMsg() + "]";
  }
}
