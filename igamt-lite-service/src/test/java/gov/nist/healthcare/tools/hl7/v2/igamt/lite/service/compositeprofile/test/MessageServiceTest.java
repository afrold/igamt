package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.compositeprofile.test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;

public class MessageServiceTest implements MessageService {


  @Override
  public Message findById(String id) {

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#save(gov.nist.healthcare.
   * tools.hl7.v2.igamt.lite.domain.Message)
   */
  @Override
  public Message save(Message message) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#delete(gov.nist.healthcare.
   * tools.hl7.v2.igamt.lite.domain.Message)
   */
  @Override
  public void delete(Message message) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#delete(java.lang.String)
   */
  @Override
  public void delete(String id) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#save(java.util.Set)
   */
  @Override
  public void save(Set<Message> messages) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#findByIds(java.util.Set)
   */
  @Override
  public List<Message> findByIds(Set<String> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#findMaxPosition(gov.nist.
   * healthcare.tools.hl7.v2.igamt.lite.domain.Messages)
   */
  @Override
  public int findMaxPosition(Messages msgs) {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#findByNamesScopeAndVersion(
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public List<Message> findByNamesScopeAndVersion(String name, String structId, String scope,
      String hl7Version) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#
   * findByStructIdAndScopeAndVersion(java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Message findByStructIdAndScopeAndVersion(String structId, String scope,
      String hl7Version) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#findAll()
   */
  @Override
  public List<Message> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#updateDate(java.lang.String,
   * java.util.Date)
   */
  @Override
  public Date updateDate(String id, Date date) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#save(gov.nist.healthcare.
   * tools.hl7.v2.igamt.lite.domain.Message, java.util.Date)
   */
  @Override
  public Message save(Message message, Date dateUpdated) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Message> findByNameAndScope(String name, String scope) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Message> findByScope(String scope) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Message findByNameAndVersionAndScope(String name, String hl7Version, String scope) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Message> findByScopeAndVersion(String name, String hl7Version) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Message findByMessageTypeAndEventAndVersionAndScope(String messageType, String event,
      String hl7Version, String scope) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Message> findAllByMessageTypeAndEventAndVersionAndScope(String messageType,
      String event, String hl7Version, String scope) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
  }

}
