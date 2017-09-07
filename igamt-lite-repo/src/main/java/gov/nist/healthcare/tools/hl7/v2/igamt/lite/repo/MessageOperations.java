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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;

/**
 * @author gcr1
 *
 */
public interface MessageOperations {

  public List<Message> findByIds(Set<String> ids);

  List<Message> findByNamesScopeAndVersion(String name, String structId, String scope,
      String hl7Version);

  Message findByStructIdAndScopeAndVersion(String structId, String scope, String hl7Version);

  public Date updateDate(String id, Date date);

  List<Message> findByNameAndScope(String name, String scope);

  Message findByNameAndVersionAndScope(String name, String hl7Version, String scope);

  List<Message> findByScope(String scope);

  List<Message> findByScopeAndVersion(String scope, String hl7Version);

  public Message findByMessageTypeAndEventAndVersionAndScope(String messageType, String event,
      String hl7Version, String scope);

  public List<Message> findAllByMessageTypeAndEventAndVersionAndScope(String messageType,
      String event, String hl7Version, String scope);

  void updateAttribute(String id, String attributeName, Object value);


}
