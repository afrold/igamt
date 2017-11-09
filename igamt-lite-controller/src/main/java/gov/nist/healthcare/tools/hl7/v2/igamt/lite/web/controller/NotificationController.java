package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.NotificationsRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/notifications")
public class NotificationController extends CommonController {
  Logger log = LoggerFactory.getLogger(NotificationController.class);
  @Autowired
  NotificationsRepository notificationsRepository;

  @RequestMapping(value = "/igdocument/{id}", method = RequestMethod.GET,
      produces = "application/json")
  public Notifications findNotificationByIgId(@PathVariable("id") String id)
      throws DataNotFoundException {
    log.info("Fetching findNotificationByIgId..." + id);
    return notificationsRepository.findByIgDocumentId(id);
  }


  @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET, produces = "application/json")
  public void deleteNotification(@PathVariable("id") String id) throws DataNotFoundException {
    notificationsRepository.delete(id);
  }
}
