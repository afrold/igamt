package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipant;

@RestController
public class ShareParticipantsController {

  @Inject
  AccountRepository accountRepository;

  /**
   * List of Username for share feature
   */
  @RequestMapping(value = "/usernames", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody List<ShareParticipant> userList() {

    List<ShareParticipant> users = new ArrayList<ShareParticipant>();

    for (Account acc : accountRepository.findAll()) {
      if (!acc.isEntityDisabled() && !acc.isPending()) {
        ShareParticipant participant = new ShareParticipant(acc.getId());
        participant.setUsername(acc.getUsername());
        participant.setFullname(acc.getFullName());
        users.add(participant);
      }
    }

    return users;
  }

  /**
   * Get participants by ID
   */
  @RequestMapping(value = "/shareparticipants", method = RequestMethod.GET,
      produces = "application/json")
  public @ResponseBody List<ShareParticipant> getParticipantsListById(
      @RequestParam(value = "ids") Set<Long> ids) {
    List<ShareParticipant> users = new ArrayList<ShareParticipant>();
    List<Account> accounts = accountRepository.findAllInIds(ids);
    for (Account acc : accounts) {
      ShareParticipant share = new ShareParticipant(acc.getId());
      if (!acc.isPending() && !acc.isEntityDisabled()) {
        share.setUsername(acc.getUsername());
        share.setFullname(acc.getFullName());
        users.add(share);
      }
    }
    return users;
  }
  
  /**
   * Get one participant by ID
   */
  @RequestMapping(value = "/shareparticipant", method = RequestMethod.GET,
      produces = "application/json")
  public @ResponseBody ShareParticipant getParticipantById(@RequestParam(value = "id") Long id) {
    Account account = accountRepository.findOne(id);
    ShareParticipant user = new ShareParticipant(account.getId());
    user.setUsername(account.getUsername());
    user.setFullname(account.getFullName());
    return user;
  }

}
