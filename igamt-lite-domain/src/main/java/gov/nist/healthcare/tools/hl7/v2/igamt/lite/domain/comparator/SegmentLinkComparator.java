/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Aug 24, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator;

import java.util.Comparator;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
public class SegmentLinkComparator implements Comparator<SegmentLink> {

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(SegmentLink o1, SegmentLink o2) {
    // TODO Auto-generated method stub
    return o1.compareTo(o2);
  }

}
