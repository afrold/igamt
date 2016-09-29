package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	  public List<ShareParticipant> userList() {

		  List<ShareParticipant> users = new ArrayList<ShareParticipant>();

		    for (Account acc : accountRepository.findAll()) {
		      if (!acc.isEntityDisabled()) {
		    	  users.add(new ShareParticipant(acc.getUsername(), acc.getFullName()));
		      }
		    }

		    return users;
	  }

}
