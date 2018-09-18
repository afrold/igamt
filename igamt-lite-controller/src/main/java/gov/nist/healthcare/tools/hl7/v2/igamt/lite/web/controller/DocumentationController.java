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

import java.io.IOException;
import java.util.ArrayList;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Documentation;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DocumentationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@RestController
@RequestMapping("/documentations")
public class DocumentationController {


  Logger log = LoggerFactory.getLogger(DocumentationController.class);

  @Autowired
  private DocumentationService documentationService;
  @Autowired
  private UserService userService;
  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(value = "/findAll", method = RequestMethod.POST, produces = "application/json")
  public List<Documentation> findAll() {
    log.info("Fetching Documentations...");
    List<Documentation> temp = documentationService.findAll();
    List<Documentation> result = new ArrayList<Documentation>();

    if (!temp.isEmpty()) {
      for (Documentation doc : temp) {
        if (!doc.getType().equals("UserNote")) {
          result.add(doc);
        }
      }
    }
    return result;
  }

  @RequestMapping(value = "/findUserNotes", method = RequestMethod.POST,
      produces = "application/json")
  public List<Documentation> findByCreator() {
    List<Documentation> userNotes = new ArrayList<Documentation>();

    User u = userService.getCurrentUser();
    if (u == null) {
      return userNotes;
    }
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    userNotes = documentationService.findByOwner(account.getId());
    return userNotes;
  }


  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Documentation save(@RequestBody Documentation documentation)
      throws DatatypeSaveException, ForbiddenOperationException {

    documentation.setDateUpdated(new Date());
    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    documentation.setAccountId(account.getId());

    documentation.setUsername(account.getFullName());
    Documentation saved = documentationService.save(documentation);
    return saved;

  }

  @RequestMapping(value = "/delete", method = RequestMethod.POST)
  public ResponseMessage delete(@RequestBody Documentation documentation)
      throws DatatypeSaveException, ForbiddenOperationException {
    documentationService.delete(documentation);
    return new ResponseMessage(ResponseMessage.Type.success, "documentation Delete Success", null);
 }
  

  @RequestMapping(value = "/reorder", method = RequestMethod.POST)
  public List<PositionMap> reorder(@RequestBody List<PositionMap> posMap) {

    for (PositionMap i : posMap) {
    Documentation d = documentationService.findById(i.getId());
    
    if(d !=null){
    	d.setPosition(i.getPosition());
    documentationService.save(d);
      
    }
    }
    return posMap;

  }

}
