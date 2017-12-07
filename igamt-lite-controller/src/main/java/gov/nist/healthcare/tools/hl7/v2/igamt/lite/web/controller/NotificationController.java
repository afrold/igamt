package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.Base64;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Notifications;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.NotificationsRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/notifications")
public class NotificationController extends CommonController {
  Logger log = LoggerFactory.getLogger(NotificationController.class);

  @Autowired
  NotificationsRepository notificationsRepository;

  @Inject
  private AccountRepository accountRepository;

  @Autowired
  private IGDocumentService igDocumentService;

  @Autowired
  private ExportConfigService exportConfigService;
  
  @Autowired
  private TableService tableService;

  @Value("${server.email}")
  private String SERVER_EMAIL;

  @Value("${admin.email}")
  private String ADMIN_EMAIL;

  @Autowired
  private MailSender mailSender;

  @Autowired
  private SimpleMailMessage templateMessage;

  @RequestMapping(value = "/igdocument/{id}", method = RequestMethod.GET,
      produces = "application/json")
  public Notifications findNotificationByIgId(@PathVariable("id") String id)
      throws DataNotFoundException {
    log.info("Fetching findNotificationByIgId..." + id);
    return notificationsRepository.findByIgDocumentId(id);
  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST,
      produces = "application/json")
  public void deleteNotification(@PathVariable("id") String id) throws DataNotFoundException {
    notificationsRepository.delete(id);
  }

  @RequestMapping(value = "/{id}/sendEmail", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody boolean notifyEmailForPHINVADS(@PathVariable("id") String id)
      throws Exception {
    if(id != null){
      String decodedId = new String(Base64.getDecoder().decode(id));
      Notifications notifications = notificationsRepository.findOne(decodedId);
      if (notifications != null) {
        IGDocument igDoc = igDocumentService.findById(notifications.getIgDocumentId());
        if (igDoc != null) {
          ExportConfig config = exportConfigService.findOneByAccountId(igDoc.getAccountId());
          if(config != null && config.isPhinvadsUpdateEmailNotification()){
            sendNotificationPhinvadsUpdateEmail(igDoc, notifications);
            return true;
          }
        }
      }      
    }
    return false;
  }

  private void sendNotificationPhinvadsUpdateEmail(IGDocument doc, Notifications notis) {
    String notificationitemString = "";
    for(Notification item: notis.getItems()) {
      Table t = tableService.findById(item.getTargetId());
      if(t != null) {
        notificationitemString = notificationitemString + "\n * " + t.getBindingIdentifier();
      }
      
    }
    
    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
    Account targetAccount = accountRepository.findOne(doc.getAccountId());
    msg.setSubject("NIST IGAMT IGDocument Share");
    msg.setTo(targetAccount.getEmail());
    msg.setText(
        "Dear " + targetAccount.getUsername() 
            + " \n\n" + "The following phinvads valuesets have been updated in your IG Document: " + doc.getMetaData().getTitle() 
            + notificationitemString
            + "\n\n" + "P.S: If you need help, contact us at '"
            + ADMIN_EMAIL + "'");
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      log.error(ex.getMessage(), ex);
    }
  }
}
