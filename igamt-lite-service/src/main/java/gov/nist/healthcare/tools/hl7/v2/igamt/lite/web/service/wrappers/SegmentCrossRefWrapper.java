/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Jun 6, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class SegmentCrossRefWrapper {

  private String segmentId;
  private String igDocumentId;

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
   * @return the segmentId
   */
  public String getSegmentId() {
    return segmentId;
  }

  /**
   * @param segmentId the segmentId to set
   */
  public void setSegmentId(String segmentId) {
    this.segmentId = segmentId;
  }


}
