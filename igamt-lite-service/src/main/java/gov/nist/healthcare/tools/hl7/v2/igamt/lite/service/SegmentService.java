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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;

public interface SegmentService {

  Segment findById(String id);

  Segment save(Segment segment);

  List<Segment> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

  public Segment findByNameAndVersionAndScope(String name, String version, String scope);

  void delete(Segment segment);

  void delete(List<Segment> segments);

  void delete(String id);

  void save(List<Segment> segments);

  List<Segment> findAll();

  List<Segment> findByIds(Set<String> ids);

  List<Segment> findByScope(String scope);

  public List<Segment> findByNameAndScope(String name, String scope);

  public Date updateDate(String id, Date date) throws IGDocumentException;

  Segment save(Segment segment, Date date);

  void updateStatus(String id, STATUS unpublished);

  List<Segment> findByScopeAndVersion(String name, String hl7Version);

  void updateAttribute(String id, String attributeName, Object value);


}
