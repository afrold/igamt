package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.VersionAndUseService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/versionAndUse")
public class VersionAndUseController {
  Logger log = LoggerFactory.getLogger(VersionAndUseController.class);

  @Autowired
  private VersionAndUseService versionAndUseService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(value = "/findByIds", method = RequestMethod.POST, produces = "application/json")
  public List<VersionAndUse> findByIds(@RequestBody List<String> ids) {
    log.info("Fetching VersionAndUseByIds..." + ids);
    List<VersionAndUse> result = versionAndUseService.findAllByIds(ids);
    return result;
  }

  @RequestMapping(value = "/findAll", method = RequestMethod.POST, produces = "application/json")
  public List<VersionAndUse> findByIds() {

    List<VersionAndUse> toReturn = new ArrayList<VersionAndUse>();

    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    List<VersionAndUse> result = versionAndUseService.findByAccountId(account.getId());



    return result;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public VersionAndUse getVersionAndUseById(@PathVariable("id") String id)
      throws DataNotFoundException {
    log.info("Fetching VersionAndUseById..." + id);
    VersionAndUse result = versionAndUseService.findById(id);
    return result;
  }


  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public VersionAndUse save(@RequestBody VersionAndUse versionAndUse)
      throws VersionAndUseSaveException, ForbiddenOperationException {

    User u = userService.getCurrentUser();
    Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
    versionAndUse.setAccountId(account.getId());
    versionAndUseService.save(versionAndUse);

    return versionAndUse;

  }

}
