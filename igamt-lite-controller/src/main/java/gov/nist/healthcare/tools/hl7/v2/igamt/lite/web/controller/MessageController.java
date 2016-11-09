package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.BindingParametersForMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

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
		message.setDateUpdated(DateUtils.getCurrentDate());
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
	
	@RequestMapping(value = "/updateSegmentBinding", method = RequestMethod.POST)
	  public void updateSegmentBinding(@RequestBody List<BindingParametersForMessage> bindingParametersList) throws DataNotFoundException {
		  for(BindingParametersForMessage paras : bindingParametersList){
			  Message message = this.messageService.findById(paras.getMessageId());
 			  String[] paths = paras.getPositionPath().split("\\.");
			  List<String> strs = new LinkedList<String>(Arrays.asList(paths));
			  this.updateSegmentBindingForMessage(message.getChildren(), strs, paras.getNewSegmentLink());
			  messageService.save(message);
		  }
	  }
	
	private void updateSegmentBindingForMessage(List<SegmentRefOrGroup> children, List<String> paths, SegmentLink newSegmentLink){
		int position = Integer.parseInt(paths.get(1));
		SegmentRefOrGroup child = this.findChildByPosition(children, position);
		
		if(paths.size() == 2) {
            if(child.getType().equals("segmentRef")){
            	SegmentRef sr = (SegmentRef)child;
            	sr.setRef(newSegmentLink);
            }
        }else{
        	Group g = (Group)child;
        	paths.remove(0);
            this.updateSegmentBindingForMessage(g.getChildren(), paths, newSegmentLink);
        }
	}
	
	private SegmentRefOrGroup findChildByPosition(List<SegmentRefOrGroup> children, int position) {
		for (int i = 0; i < children.size(); i++) {
            if(children.get(i).getPosition() == position) return children.get(i);
        }
        return null;
	}
	

}
