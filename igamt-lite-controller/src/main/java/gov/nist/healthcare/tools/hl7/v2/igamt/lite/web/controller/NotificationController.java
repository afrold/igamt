package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.NotificationRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/notifications")
public class NotificationController extends CommonController {
  Logger log = LoggerFactory.getLogger(NotificationController.class);
  @Autowired
  NotificationRepository notificationRepository;
  
  @RequestMapping(value = "/igdocument/{id}", method = RequestMethod.GET, produces = "application/json")
  public List<Notification> findNotificationByIgId(@PathVariable("id") String id) throws DataNotFoundException {
    log.info("Fetching findNotificationByIgId..." + id);
    List<Notification> result= new ArrayList<Notification>();
    List<Notification> allNotifications=notificationRepository.findAll();
    
    for(Notification n:allNotifications){
      if(n.containIgId(id)) result.add(n);
    }
    return result;
  }
  
  
  @RequestMapping(value = "/delnotification/{nid}/igdocument/{igid}", method = RequestMethod.GET, produces = "application/json")
  public void deleteIGDocumentEntry(@PathVariable("nid") String nid, @PathVariable("igid") String igid) throws DataNotFoundException {
    Notification n = notificationRepository.findOne(nid);
    if(n != null){
      n.removeIgId(igid);
      notificationRepository.save(n); 
    }
  }
}
