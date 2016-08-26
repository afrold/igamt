package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.EventWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.ScopesAndVersionWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController extends CommonController {
	static final Logger logger = LoggerFactory
			.getLogger(MessageController.class);
	Logger log = LoggerFactory.getLogger(MessageController.class);

	@Autowired
	private MessageService messageService;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Message getMessageById(@PathVariable("id") String id) {
		log.info("Fetching messageById..." + id);
		Message result = messageService.findById(id);
		return result;
	}
	@RequestMapping(value = "/findByIds", method = RequestMethod.POST, produces = "application/json")
	  public List<Message> findByIds(@RequestBody Set<String> ids) {
	    log.info("Fetching messageByIds..." + ids);
	    List<Message> result = messageService.findByIds(ids);
	    return result;
	  }


	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Message save(@RequestBody Message message) {
		log.debug("message=" + message);
		log.debug("message.getId()=" + message.getId());
		log.info("Saving the " + message.getScope() + " message.");
		message.setDate(DateUtils.getCurrentTime());
		Message saved = messageService.save(message);
		log.debug("saved.getId()=" + saved.getId());
		log.debug("saved.getScope()=" + saved.getScope());
		return message;

	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
	public boolean save(@PathVariable("id") String messageId) {
		log.info("Deleting message " + messageId);
		messageService.delete(messageId);
		// TODO:
		return true;
	}

}
