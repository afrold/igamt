package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.List;

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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentOrGroupLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.SegOrGrpWrapper;

@RestController
@RequestMapping("/composite-messages")
public class CompositeMessageController extends CommonController{

  
  static final Logger logger = LoggerFactory
      .getLogger(MessageController.class);
  Logger log = LoggerFactory.getLogger(MessageController.class);
  @Autowired
  private CompositeMessageService compositeMessageService;
  @Autowired
  UserService userService;
  
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  private IGDocumentService igDocumentService;
  
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public CompositeMessage getCompositeMessageById(@PathVariable("id") String id) {
      log.info("Fetching CompositeMessageById..." + id);
      CompositeMessage result = compositeMessageService.findById(id);
      
      return result;
  }
  @RequestMapping(value = "/getsegorgrp", method = RequestMethod.POST, produces = "application/json")
  public List<SegmentOrGroup> getSegOrGrps(@RequestBody List<SegmentOrGroupLink> segmentOrGroupLinks) {
     
    List<SegmentOrGroup> segOrGrp= new ArrayList<SegmentOrGroup>();

      for(SegmentOrGroupLink segOrGrpLink:segmentOrGroupLinks){
        segOrGrp.add(compositeMessageService.getSegOrGrp(segOrGrpLink.getId()));
      }
      
      return segOrGrp;
  }
  
  
  @RequestMapping(value = "/create/{igId}", method = RequestMethod.POST)
  public CompositeMessage save(@PathVariable("igId") String igId, @RequestBody CompositeMessage compositeMessage) throws IGDocumentException {
      log.debug("compositeMessage=" + compositeMessage);
      log.debug("compositeMessage.getId()=" + compositeMessage.getId());
      log.info("Saving the " + compositeMessage.getScope() + " compositeMessage.");
      compositeMessage.setDateUpdated(DateUtils.getCurrentDate());
      compositeMessage.setType(Constant.COMPOSITEMESSAGE);
      CompositeMessage saved = compositeMessageService.save(compositeMessage);
      IGDocument ig =igDocumentService.findById(igId);
      ig.getProfile().getCompositeMessages().addChild(compositeMessage);
      igDocumentService.save(ig);

      log.debug("saved.getId()=" + saved.getId());
      log.debug("saved.getScope()=" + saved.getScope());
      return compositeMessage;

  }
  @RequestMapping(value = "/save", method = RequestMethod.POST)
	public CompositeMessage save(@RequestBody CompositeMessage message) {
		log.debug("message=" + message);
		log.debug("message.getId()=" + message.getId());
		log.info("Saving the " + message.getScope() + " message.");
		message.setDateUpdated(DateUtils.getCurrentDate());
		CompositeMessage saved = compositeMessageService.save(message);
		log.debug("saved.getId()=" + saved.getId());
		log.debug("saved.getScope()=" + saved.getScope());
		return message;

	}
  @RequestMapping(value = "/savegrporseg", method = RequestMethod.POST)
  public List<SegmentOrGroup> save( @RequestBody List<SegmentOrGroup> segOrGrps) throws IGDocumentException {
    System.out.println("+++++++++++++++++++++++++++++++++");

      System.out.println(segOrGrps);
      for(SegmentOrGroup segOrGrp:segOrGrps){
        segOrGrp.setDateUpdated(DateUtils.getCurrentDate());
        compositeMessageService.saveSegOrGrp(segOrGrp);
        
      }
      return segOrGrps;

  }

}
