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



  @RequestMapping(value = "/validateDatatype/{igHl7Version}", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateDatatype(@PathVariable("igHl7Version") String igHl7Version,
      @RequestBody Datatype userDatatype) throws InvalidObjectException {
    List<Datatype> hl7Datatypes = datatypeService.findByNameAndVersionAndScope(
        userDatatype.getName(), userDatatype.getHl7Version(), "HL7STANDARD");
    return validationService.validateDatatype(
        hl7Datatypes != null && !hl7Datatypes.isEmpty() ? hl7Datatypes.get(0) : null, userDatatype,
        userDatatype.getId(), igHl7Version, null);

  }

  @RequestMapping(value = "/validateSegment/{igHl7Version}", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateSegment(@PathVariable("igHl7Version") String igHl7Version,
      @RequestBody Segment userSegment) throws InvalidObjectException {
    log.info("Validation ig..." + userSegment.getId());
    Segment hl7Segment = segmentService.findByNameAndVersionAndScope(userSegment.getName(),
        userSegment.getHl7Version(), "HL7STANDARD");
    return validationService.validateSegment(hl7Segment, userSegment, true, igHl7Version, null);
  }

  @RequestMapping(value = "/validateMessage", method = RequestMethod.POST,
      produces = "application/json")
  public ValidationResult validateMessage(@RequestBody Message userMessage)
      throws InvalidObjectException {
    log.info("Validation ig..." + userMessage.getId() + " " + userMessage.getHl7Version());

    Message hl7Message = messageService.findByStructIdAndScopeAndVersion(userMessage.getStructID(),
        "HL7STANDARD", userMessage.getHl7Version());
    return validationService.validateMessage(hl7Message, userMessage, true);

  }


  @RequestMapping(value = "/validateIg", method = RequestMethod.POST, produces = "application/json")
  public ValidationResult validateMessage(@RequestBody IGDocument userIg)
      throws InvalidObjectException {
    log.info("Validation ig..." + userIg.getMetaData().getHl7Version() + " " + userIg.getId());

    ValidationResult result = validationService.validateIg(userIg);

    return result;
  }


}
