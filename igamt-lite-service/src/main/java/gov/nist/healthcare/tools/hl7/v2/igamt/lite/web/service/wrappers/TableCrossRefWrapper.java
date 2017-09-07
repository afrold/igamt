/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Jun 7, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class TableCrossRefWrapper {
  private String tableId;
  private String igDocumentId;
  private String assertionId;

  /**
   * @return the tableId
   */
  public String getTableId() {
    return tableId;
  }

  /**
   * @param tableId the tableId to set
   */
  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  /**
   * @return the igDocumentId
   */
  public String getIgDocumentId() {
    return igDocumentId;
  }

  /**
   * @param igDocumentId the igDocumentId to set
   */
  public void setIgDocumentId(String igDocumentId) {
    this.igDocumentId = igDocumentId;
  }

  /**
   * @return the assertionId
   */
  public String getAssertionId() {
    return assertionId;
  }

  /**
   * @param assertionId the assertionId to set
   */
  public void setAssertionId(String assertionId) {
    this.assertionId = assertionId;
  }

}
