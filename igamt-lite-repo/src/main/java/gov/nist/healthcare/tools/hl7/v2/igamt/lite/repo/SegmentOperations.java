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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.Date;
import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;

/**
 * @author gcr1
 *
 */
public interface SegmentOperations {

  List<Segment> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  public Segment findByNameAndVersionAndScope(String name, String version, String scope);


  public List<Segment> findByIds(Set<String> ids);

  List<Segment> findUserSegmentsByIds(Set<String> ids);

  public Date updateDate(String id, Date date);

  void updateStatus(String id, STATUS status);

  List<Segment> findByScope(String scope);

  List<Segment> findByNameAndScope(String name, String scope);

  public List<Segment> findByScopeAndVersion(String scope, String version);

  void updateAttribute(String id, String attributeName, Object value);


}
