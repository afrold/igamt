/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import static org.springframework.data.jpa.domain.Specifications.where;
import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.ShortAccount;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.general.CustomSortHandler;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.repo.AccountSpecsHelper;
import gov.nist.healthcare.nht.acmgt.service.UserService;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fdevaulx
 * @author Harold Affo
 * 
 */
@RestController
public class AccountController {

	static final Logger logger = LoggerFactory
			.getLogger(AccountController.class);

	public final String DEFAULT_PAGE_SIZE = "0";

	// private List<String> skippedValidationEmails = new ArrayList<String>();

	public AccountController() {
		// skippedValidationEmails = new ArrayList<String>();
		// skippedValidationEmails.add("haffo@nist.gov");
		// skippedValidationEmails.add("rsnelick@nist.gov");
	}

	@Inject
	AccountRepository accountRepository;

	@Autowired
	UserService userService;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private SimpleMailMessage templateMessage;

	/* Account */

	@PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public List<Account> getAccounts() {

		List<Account> accs = new LinkedList<Account>();

		for (Account acc : accountRepository.findAll()) {
			if (!acc.isEntityDisabled()) {
				accs.add(acc);
			}
		}

		return accs;

	}

	@PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	@RequestMapping(value = "/authors/page", method = RequestMethod.GET)
	public Page<ShortAccount> getProvidersPage(
			@RequestParam(required = false, defaultValue = "0") int value,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(required = false) List<String> sort,
			@RequestParam(required = false) List<String> filter) {

		List<ShortAccount> saccs = new LinkedList<ShortAccount>();

		AccountSpecsHelper accH = new AccountSpecsHelper();

		Page<Account> pa = accountRepository
				.findAll((where(accH.getSpecificationFromString("accountType",
						"author")).and(accH.getSpecification(filter))),
						new PageRequest(value, size, (new CustomSortHandler(
								sort)).getSort()));

		if (pa.getContent() != null && !pa.getContent().isEmpty()) {
			for (Account acc : pa.getContent()) {
				if (!acc.isEntityDisabled()) {

					ShortAccount sacc = new ShortAccount();
					sacc.setId(acc.getId());
					sacc.setEmail(acc.getEmail());
					sacc.setFullName(acc.getFullName());
					sacc.setEmployer(acc.getEmployer());
					sacc.setJuridiction(acc.getJuridiction());
					sacc.setPhone(acc.getPhone());
					sacc.setTitle(acc.getTitle());
					sacc.setUsername(acc.getUsername());
					saccs.add(sacc);
				}
			}
		}

		Pageable p = new PageRequest(pa.getNumber(), pa.getSize(), pa.getSort());
		Page<ShortAccount> sap = new PageImpl<ShortAccount>(saccs, p,
				pa.getTotalElements());

		return sap;
	}

	// @PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	// @RequestMapping(value = "/authorizedVendors/page", method =
	// RequestMethod.GET)
	// public Page<ShortAccount> getAuthorizedVendorsPage(
	// @RequestParam(required = false, defaultValue = "0") int value,
	// @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int
	// size,
	// @RequestParam(required = false) List<String> sort,
	// @RequestParam(required = false) List<String> filter) {
	//
	// List<ShortAccount> saccs = new LinkedList<ShortAccount>();
	//
	// AccountSpecsHelper accH = new AccountSpecsHelper();
	//
	// Page<Account> pa = accountRepository.findAll((where(accH
	// .getSpecificationFromString("accountType", "authorizedVendor"))
	// .and(accH.getSpecification(filter))), new PageRequest(value,
	// size, (new CustomSortHandler(sort)).getSort()));
	//
	// if (pa.getContent() != null && !pa.getContent().isEmpty()) {
	// for (Account acc : pa.getContent()) {
	// if (!acc.isEntityDisabled()) {
	//
	// ShortAccount sacc = new ShortAccount();
	// sacc.setId(acc.getId());
	// sacc.setEmail(acc.getEmail());
	// sacc.setFirstname(acc.getFirstname());
	// sacc.setLastname(acc.getLastname());
	// sacc.setCompany(acc.getCompany());
	//
	// saccs.add(sacc);
	// }
	// }
	// }
	//
	// Pageable p = new PageRequest(pa.getNumber(), pa.getSize(), pa.getSort());
	// Page<ShortAccount> sap = new PageImpl<ShortAccount>(saccs, p,
	// pa.getTotalElements());
	//
	// return sap;
	// }

	@PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	@RequestMapping(value = "/shortaccounts/page", method = RequestMethod.GET)
	public Page<ShortAccount> getShortAccountsPage(
			@RequestParam(required = false, defaultValue = "0") int value,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(required = false) List<String> sort,
			@RequestParam(required = false) List<String> filter) {

		List<ShortAccount> saccs = new LinkedList<ShortAccount>();

		// adding default sort if necessary
		sort = sort != null ? sort : new LinkedList<String>();
		if (sort.isEmpty()) {
			sort.add("accountType::ASC");
		}

		Page<Account> pa = accountRepository.findAll((new AccountSpecsHelper())
				.getSpecification(filter), new PageRequest(value, size,
				(new CustomSortHandler(sort)).getSort()));

		if (pa.getContent() != null && !pa.getContent().isEmpty()) {
			for (Account acc : pa.getContent()) {
				if (!acc.isEntityDisabled()) {

					ShortAccount sacc = new ShortAccount();
					sacc.setId(acc.getId());
					sacc.setEmail(acc.getEmail());
					sacc.setFullName(acc.getFullName());
					sacc.setEmployer(acc.getEmployer());
					sacc.setJuridiction(acc.getJuridiction());
					sacc.setPhone(acc.getPhone());
					sacc.setAccountType(acc.getAccountType());
					sacc.setUsername(acc.getUsername());
					sacc.setTitle(acc.getTitle());

					saccs.add(sacc);
				}
			}
		}

		Pageable p = new PageRequest(pa.getNumber(), pa.getSize(), pa.getSort());
		Page<ShortAccount> sap = new PageImpl<ShortAccount>(saccs, p,
				pa.getTotalElements());

		return sap;
	}

	@RequestMapping(value = "/shortaccounts", method = RequestMethod.GET)
	public List<ShortAccount> getShortAccounts(
			@RequestParam(required = false) List<String> filter) {

		List<ShortAccount> saccs = new LinkedList<ShortAccount>();
		filter = filter != null ? filter : new LinkedList<String>();

		User authU = userService.getCurrentUser();
		if (authU != null && authU.isEnabled()) {
			if (authU.getAuthorities().contains(
					new SimpleGrantedAuthority("author"))) {
				filter.clear();
 			} else if (authU.getAuthorities().contains(
					new SimpleGrantedAuthority("supervisor"))
					|| authU.getAuthorities().contains(
							new SimpleGrantedAuthority("admin"))) {
				// Do nothing
			} else {
				return saccs;
			}
		} else {
			return saccs;
		}

		List<Account> accs = accountRepository
				.findAll((new AccountSpecsHelper()).getSpecification(filter));
		if (accs != null && !accs.isEmpty()) {
			for (Account acc : accs) {
				if (!acc.isEntityDisabled()) {
					ShortAccount sacc = new ShortAccount();
					sacc.setId(acc.getId());
					sacc.setEmail(acc.getEmail());
					sacc.setFullName(acc.getFullName());
					sacc.setEmployer(acc.getEmployer());
					sacc.setJuridiction(acc.getJuridiction());
					sacc.setPhone(acc.getPhone());
					sacc.setTitle(acc.getTitle());
					sacc.setPending(acc.isPending());
					sacc.setEntityDisabled(acc.isEntityDisabled());
					sacc.setUsername(acc.getUsername());
					saccs.add(sacc);
				}
			}
		}

		return saccs;

	}

	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
	public Account getAccountById(@PathVariable Long id) {

		Account acc = accountRepository.findOne(id);

		if (acc == null || acc.isEntityDisabled()) {
			return null;
		} else {
			return acc;
		}
	}

	@PreAuthorize("hasPermission(#id, 'accessAccountBasedResource')")
	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.POST)
	public ResponseMessage updateAccountById(@PathVariable Long id,
			@Valid @RequestBody Account account) {

		Account acc = accountRepository.findOne(id);
		if (acc == null || acc.isEntityDisabled()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"badAccount", id.toString());
		} else {
			// Validation
			if (account.getEmail() == null || account.getEmail().isEmpty()) {
				return new ResponseMessage(ResponseMessage.Type.danger,
						"emptyEmail", account.getEmail());
			}
			if (!acc.getEmail().equalsIgnoreCase(account.getEmail())
					&& accountRepository.findByTheAccountsEmail(account
							.getEmail()) != null) {
				return new ResponseMessage(ResponseMessage.Type.danger,
						"duplicateEmail", account.getEmail());
			}

			acc.setEmployer(account.getEmployer());
			acc.setFullName(account.getFullName());
			acc.setEmail(account.getEmail());
			acc.setJuridiction(account.getJuridiction());
			acc.setPhone(account.getPhone());
			acc.setTitle(account.getTitle());

			accountRepository.save(acc);

			return new ResponseMessage(ResponseMessage.Type.success,
					"accountUpdated", acc.getId().toString());
		}
	}

	/* Other */

	@RequestMapping(value = "/sooa/emails/{email:.*}", method = RequestMethod.GET)
	public ResponseMessage accountEmailExist(@PathVariable String email,
			@RequestParam(required = false) String email1) {

		if (accountRepository.findByTheAccountsEmail(email) != null) {
			return new ResponseMessage(ResponseMessage.Type.success,
					"emailFound", email);
		} else {
			return new ResponseMessage(ResponseMessage.Type.success,
					"emailNotFound", email);
		}
	}

	@RequestMapping(value = "/sooa/usernames/{username}", method = RequestMethod.GET)
	public ResponseMessage accountUsernameExist(@PathVariable String username) {

		if (accountRepository.findByTheAccountsUsername(username) != null) {
			return new ResponseMessage(ResponseMessage.Type.success,
					"usernameFound", username);
		} else {
			return new ResponseMessage(ResponseMessage.Type.success,
					"usernameNotFound", username);
		}
	}

}
