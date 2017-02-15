/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Jan 30, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.InvalidObjectException;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValidationResult;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ValidationService;

@RestController
@RequestMapping("/validation")
public class ValidationController extends CommonController {


  Logger log = LoggerFactory.getLogger(TableLibraryController.class);



  @Autowired
  UserService userService;
  @Autowired
  private IGDocumentService igDocumentService;
  @Autowired
  private MessageService messageService;
  @Autowired
  private DatatypeService datatypeService;
  @Autowired
  private SegmentService segmentService;
  @Autowired
  AccountRepository accountRepository;

  @Autowired
  ValidationService validationService;



  @RequestMapping(value = "/validateIG/{igId}", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateIG(@PathVariable("igId") String igId) {
    log.info("Validation ig..." + igId);
    IGDocument userIg = igDocumentService.findById(igId);
    List<IGDocument> hl7Igs = igDocumentService.findByScopeAndVersion(IGDocumentScope.HL7STANDARD,
        userIg.getProfile().getMetaData().getHl7Version());
    if (!hl7Igs.isEmpty()) {
      IGDocument hl7Ig = hl7Igs.get(0);

    }
    ValidationResult result = new ValidationResult();
    return result;
  }

  @RequestMapping(value = "/validateDatatype", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateDatatype(@RequestBody Datatype userDatatype)
      throws InvalidObjectException {
    log.info("Validation ig..." + userDatatype.getId());
    Datatype hl7Datatype = datatypeService.findByNameAndVersionAndScope(userDatatype.getName(),
        userDatatype.getHl7Version(), "HL7STANDARD");


    ValidationResult result =
        validationService.validateDatatype(hl7Datatype, userDatatype, userDatatype.getId());

    return result;
  }

  @RequestMapping(value = "/validateSegment", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateSegment(@RequestBody Segment userSegment)
      throws InvalidObjectException {
    log.info("Validation ig..." + userSegment.getId());
    Segment hl7Segment = segmentService.findByNameAndVersionAndScope(userSegment.getName(),
        userSegment.getHl7Version(), "HL7STANDARD");



    ValidationResult result = validationService.validateSegment(hl7Segment, userSegment, true);

    return result;
  }

  @RequestMapping(value = "/validateMessage", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateMessage(@RequestBody Message userMessage)
      throws InvalidObjectException {
    log.info("Validation ig..." + userMessage.getId() + " " + userMessage.getHl7Version());

    Message hl7Message = messageService.findByStructIdAndScopeAndVersion(userMessage.getStructID(),
        "HL7STANDARD", userMessage.getHl7Version());
    System.out.println("++++++" + hl7Message.getId());



    ValidationResult result = validationService.validateMessage(hl7Message, userMessage, true);

    return result;
  }


  @RequestMapping(value = "/validateIg", method = RequestMethod.POST, produces = "application/json")
  public ValidationResult validateMessage(@RequestBody IGDocument userIg)
      throws InvalidObjectException {
    log.info("Validation ig..." + userIg.getMetaData().getHl7Version() + " " + userIg.getId());

    ValidationResult result = validationService.validateIg(userIg);

    return result;
  }


}
