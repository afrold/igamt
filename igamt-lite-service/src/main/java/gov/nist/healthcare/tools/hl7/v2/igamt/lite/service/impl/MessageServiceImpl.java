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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

/**
 * @author gcr1
 *
 */
@Service
public class MessageServiceImpl implements MessageService {

  Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

  @Autowired
  private MessageRepository messageRepository;

  @Override
  public Message findById(String id) {
    log.info("MessageServiceImpl.findById=" + id);
    return messageRepository.findOne(id);
  }

  @Override
  public List<Message> findByIds(Set<String> ids) {
    log.info("MessageServiceImpl.findByIds=" + ids);
    return messageRepository.findByIds(ids);
  }

  @Override
  public void save(Set<Message> messages) {
    // TODO Auto-generated method stub
    messageRepository.save(messages);
  }

  @Override
  public Message save(Message message) {
    return save(message, DateUtils.getCurrentDate());
  }

  @Override
  public Message save(Message message, Date dateUpdated) {
    message.setDateUpdated(dateUpdated);
    return messageRepository.save(message);
  }

  @Override
  public void delete(Message segment) {
    messageRepository.delete(segment);
  }

  @Override
  public void delete(String id) {
    messageRepository.delete(id);
  }

  @Override
  public List<Message> findByNamesScopeAndVersion(String name, String structId, String scope,
      String hl7Version) {
    List<Message> messages =
        messageRepository.findByNamesScopeAndVersion(name, structId, scope, hl7Version);
    log.info("MessageServiceImpl.findByNamesScopeAndVersion=" + messages.size());
    return messages;
  }

  @Override
  public Message findByStructIdAndScopeAndVersion(String structId, String scope,
      String hl7Version) {
    Message message =
        messageRepository.findByStructIdAndScopeAndVersion(structId, scope, hl7Version);
    return message;
  }

  @Override
  public int findMaxPosition(Messages msgs) {
    int maxPos = 0;
    for (Message msg : msgs.getChildren()) {
      maxPos = Math.max(maxPos, msg.getPosition());
    }
    return maxPos;
  }

  @Override
  public List<Message> findAll() {
    return messageRepository.findAll();
  }

  @Override
  public Date updateDate(String id, Date date) {
    return messageRepository.updateDate(id, date);
  }

  @Override
  public List<Message> findByNameAndScope(String name, String scope) {
    return messageRepository.findByNameAndScope(name, scope);
  }

  @Override
  public List<Message> findByScope(String scope) {
    return messageRepository.findByScope(scope);
  }

  @Override
  public Message findByNameAndVersionAndScope(String name, String hl7Version, String scope) {
    return messageRepository.findByNameAndVersionAndScope(name, hl7Version, scope);
  }

  @Override
  public List<Message> findByScopeAndVersion(String scope, String hl7Version) {
    return messageRepository.findByScopeAndVersion(scope, hl7Version);
  }

  @Override
  public Message findByMessageTypeAndEventAndVersionAndScope(String messageType, String event,
      String hl7Version, String scope) {
    return messageRepository.findByMessageTypeAndEventAndVersionAndScope(messageType, event,
        hl7Version, scope);
  }

  @Override
  public List<Message> findAllByMessageTypeAndEventAndVersionAndScope(String messageType,
      String event, String hl7Version, String scope) {
    return messageRepository.findAllByMessageTypeAndEventAndVersionAndScope(messageType, event,
        hl7Version, scope);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService#updateAttribute(java.lang.
   * String, java.lang.String, java.lang.Object)
   */
  @Override
  public void updateAttribute(String id, String attributeName, Object value) {
    // TODO Auto-generated method stub
    messageRepository.updateAttribute(id, attributeName, value);
  }
}
