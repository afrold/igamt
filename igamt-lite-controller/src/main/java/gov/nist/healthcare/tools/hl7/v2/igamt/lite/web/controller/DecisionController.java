/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Jan 26, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Decision;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DecisionService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@RestController
@RequestMapping("/decisions")
public class DecisionController {


  Logger log = LoggerFactory.getLogger(DecisionController.class);

  @Autowired
  private DecisionService decisionService;
  @Autowired
  private UserService userService;
  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(value = "/findAll", method = RequestMethod.POST, produces = "application/json")
  public List<Decision> findAll() {
    log.info("Fetching Decisions...");
    List<Decision> result = decisionService.findAll();
    return result;
  }



  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Decision save(@RequestBody Decision decision)
      throws DatatypeSaveException, ForbiddenOperationException {

    decision.setDateUpdated(new Date());
    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    decision.setAccountId(account.getId());

    decision.setUsername(account.getFullName());
    Decision saved = decisionService.save(decision);
    return saved;

  }

  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public ResponseMessage delete(@RequestBody Decision decision)
      throws DatatypeSaveException, ForbiddenOperationException {
    decisionService.delete(decision);
    return new ResponseMessage(ResponseMessage.Type.success, "decision Delete Success", null);



  }

}
