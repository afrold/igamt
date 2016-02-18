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

import gov.nist.healthcare.nht.acmgt.dto.AccountChangeCredentials;
import gov.nist.healthcare.nht.acmgt.dto.CurrentUser;
import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.dto.domain.AccountPasswordReset;
import gov.nist.healthcare.nht.acmgt.general.RandomPasswordGenerator;
import gov.nist.healthcare.nht.acmgt.general.UserUtil;
import gov.nist.healthcare.nht.acmgt.repo.AccountPasswordResetRepository;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;

import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

@RestController
public class UserController {

	static final Logger logger = LoggerFactory.getLogger(UserController.class);

	// private List<String> skippedValidationEmails = new ArrayList<String>();

	public UserController() {
		// skippedValidationEmails = new ArrayList<String>();
		// skippedValidationEmails.add("haffo@nist.gov");
		// skippedValidationEmails.add("rsnelick@nist.gov");
	}

	@Value("${unauthenticated.registration.authorized.accountType}")
	private String AUTHORIZED_ACCOUNT_TYPE_UNAUTH_REG;

	// @Value("${server.scheme}")
	// private String SERVER_SCHEME;
	//
	// @Value("${server.hostname}")
	// private String SERVER_HOSTNAME;
	//
	// @Value("${server.port}")
	// private String SERVER_PORT;

	@Value("${server.email}")
	private String SERVER_EMAIL;

	@Value("${admin.email}")
	private String ADMIN_EMAIL;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccountPasswordResetRepository accountResetPasswordRepository;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private SimpleMailMessage templateMessage;

	/**
	 * Authenticated registering agent registers a new user account accountType
	 * -> (authorizedVendor, supervisor, provider, admin)
	 * 
	 * { \"accountType\":\"\" , \"email\":\"\" }
	 * */
	@PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	@RequestMapping(value = "/accounts/register", method = RequestMethod.POST)
	public ResponseMessage registerUserWhenAuthenticated(
			@RequestBody Account account, HttpServletRequest request)
			throws Exception {

		// validate entry
		boolean validEntry = true;
		// validEntry = userService.userExists(account.getUsername()) == true ?
		// validEntry = false : validEntry;
		validEntry = accountRepository.findByTheAccountsEmail(account
				.getEmail()) != null ? validEntry = false : validEntry;
		// validEntry =
		// accountRepository.findByTheAccountsUsername(account.getUsername()) !=
		// null ? validEntry = false : validEntry;

		if (!validEntry) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"duplicateInformation", null);
		}

		// verify account type
		if (account.getAccountType() == null
				|| account.getAccountType().isEmpty()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"accountTypeMissing", null);
		}
		boolean validAccountType = false;
		for (String acct : UserUtil.ACCOUNT_TYPE_LIST) {
			if (acct.equals(account.getAccountType())) {
				validAccountType = true;
			}
		}
		if (!validAccountType) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"accountTypeNotValid", null);
		}

		// generate username
		String generatedUsername = this.generateNextUsername(account
				.getAccountType());
		account.setUsername(generatedUsername);

		// generate password
		String generatedPassword = RandomPasswordGenerator.generatePassword(10,
				13, 4, 4, 2).toString();

		// create new user
		try {
			userService.createUserWithAuthorities(generatedUsername,
					generatedPassword, "user," + account.getAccountType());
			User user = userService.retrieveUserByUsername(generatedUsername);
		} catch (Exception e) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"errorWithUser", null);
		}

		// create account
		try {
			// Make sure only desired data gets persisted
			Account registeredAccount = new Account();
			registeredAccount.setUsername(generatedUsername);
			registeredAccount.setAccountType(account.getAccountType());
			registeredAccount.setEmail(account.getEmail());
			registeredAccount.setPending(true);

			accountRepository.save(registeredAccount);
		} catch (Exception e) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"errorWithAccount", null);
		}

		// start password reset process (for registration)
		// Create reset token. First get accountPasswordReset element from the
		// repository. If null create it.
		AccountPasswordReset arp = accountResetPasswordRepository
				.findByTheAccountsUsername(account.getUsername());
		if (arp == null) {
			arp = new AccountPasswordReset();
			arp.setUsername(account.getUsername());
		}

		arp.setCurrentToken(arp.getNewToken());
		arp.setTimestamp(new Date());
		arp.setNumberOfReset(arp.getNumberOfReset() + 1);

		accountResetPasswordRepository.save(arp);

		// String port = "";
		// if (SERVER_PORT != null && !SERVER_PORT.isEmpty()) {
		// port = ":" + SERVER_PORT;
		// }

		// Generate url and email

		String url = getUrl(request) + "/#/registerResetPassword?userId="
				+ account.getUsername() + "&username=" + account.getUsername()
				+ "&token="
				+ UriUtils.encodeQueryParam(arp.getCurrentToken(), "UTF-8");

		// generate and send email
		this.sendAccountRegistrationPasswordResetNotification(account, url);

		return new ResponseMessage(ResponseMessage.Type.success, "userAdded",
				account.getUsername());

	}

	@PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	@RequestMapping(value = "/accounts/{accountId}/resendregistrationinvite", method = RequestMethod.POST)
	public ResponseMessage resendRegistrationWhenAuthenticated(
			@PathVariable Long accountId, HttpServletRequest request)
			throws Exception {

		// get account
		Account acc = accountRepository.findOne(accountId);

		if (acc == null || acc.isEntityDisabled()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"badAccount", accountId.toString());
		}

		// verify account is pending
		if (!acc.isPending()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"accountIsNotPending", accountId.toString());
		}

		// generate a new token
		AccountPasswordReset arp = accountResetPasswordRepository
				.findByTheAccountsUsername(acc.getUsername());
		if (arp == null) {
			arp = new AccountPasswordReset();
			arp.setUsername(acc.getUsername());
		}

		arp.setCurrentToken(arp.getNewToken());
		arp.setTimestamp(new Date());
		arp.setNumberOfReset(arp.getNumberOfReset() + 1);

		accountResetPasswordRepository.save(arp);

		// // generate url
		// String port = "";
		// if (SERVER_PORT != null && !SERVER_PORT.isEmpty()) {
		// port = ":" + SERVER_PORT;
		// }

		String url = getUrl(request) + "/#/registerResetPassword?userId="
				+ acc.getUsername() + "&" + "username=" + acc.getUsername()
				+ "&" + "token="
				+ UriUtils.encodeQueryParam(arp.getCurrentToken(), "UTF-8");

		// generate and send email
		this.sendAccountRegistrationPasswordResetNotification(acc, url);

		return new ResponseMessage(ResponseMessage.Type.success,
				"resentRegistrationInvite", acc.getUsername());
	}

	@PreAuthorize("hasRole('admin')")
	@RequestMapping(value = "/accounts/{accountId}/approveaccount", method = RequestMethod.POST)
	public ResponseMessage approveAccount(@PathVariable Long accountId,
			HttpServletRequest request) throws Exception {

		// get account
		Account acc = accountRepository.findOne(accountId);

		if (acc == null || acc.isEntityDisabled()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"badAccount", accountId.toString(), true);
		}

		acc.setPending(false);
		accountRepository.save(acc);

		// generate and send email
		this.sendAccountApproveNotification(acc);

		return new ResponseMessage(ResponseMessage.Type.success,
				"accountApproved", acc.getUsername(), true);
	}

	@PreAuthorize("hasRole('admin')")
	@RequestMapping(value = "/accounts/{accountId}/suspendaccount", method = RequestMethod.POST)
	public ResponseMessage suspendAccount(@PathVariable Long accountId,
			HttpServletRequest request) throws Exception {
		// get account
		Account acc = accountRepository.findOne(accountId);

		if (acc == null || acc.isEntityDisabled()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"badAccount", accountId.toString(), true);
		}

		acc.setPending(true);
		accountRepository.save(acc);
		// generate and send email
		return new ResponseMessage(ResponseMessage.Type.success,
				"accountSuspended", acc.getUsername(), true);
	}

	/**
	 * Unauthenticated user registers himself accountType -> (provider)
	 * 
	 * {\"username\":\"\" , \"password\":\"\" , \"firstname\":\"\" ,
	 * \"lastname\":\"\" , \"email\":\"\" , \"company\":\"\" ,
	 * \"signedConfidentialAgreement\":\"\" }
	 * */
	@RequestMapping(value = "/sooa/accounts/register", method = RequestMethod.POST)
	public ResponseMessage registerUserWhenNotAuthenticated(
			@RequestBody Account account) {

		// validate entry
		boolean validEntry = true;
		validEntry = userService.userExists(account.getUsername()) == true ? validEntry = false
				: validEntry;
		validEntry = accountRepository.findByTheAccountsEmail(account
				.getEmail()) != null ? validEntry = false : validEntry;
		validEntry = accountRepository.findByTheAccountsUsername(account
				.getUsername()) != null ? validEntry = false : validEntry;

		if (!validEntry) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"duplicateInformation", null);
		}

		Set<String> authAccT = StringUtils
				.commaDelimitedListToSet(AUTHORIZED_ACCOUNT_TYPE_UNAUTH_REG);

		if (account.getAccountType() == null
				|| !authAccT.contains(account.getAccountType())) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"accountTypeNotValid", null);
		}

		// create new user with provider role
		try {
			userService.createUserWithAuthorities(account.getUsername(),
					account.getPassword(), "user," + account.getAccountType());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseMessage(ResponseMessage.Type.danger,
					"errorWithUser", null);
		}

		// create account
		Account registeredAccount = new Account();
		try {
			// Make sure only desired data gets persisted
			registeredAccount.setPending(true);
			registeredAccount.setUsername(account.getUsername());
			registeredAccount.setAccountType(account.getAccountType());
			registeredAccount.setEmployer(account.getEmployer());
			registeredAccount.setFullName(account.getFullName());
			registeredAccount.setPhone(account.getPhone());
			registeredAccount.setEmail(account.getEmail());
			registeredAccount.setTitle(account.getTitle());
			registeredAccount.setJuridiction(account.getJuridiction());
			registeredAccount.setSignedConfidentialityAgreement(account
					.getSignedConfidentialityAgreement());

			accountRepository.save(registeredAccount);
		} catch (Exception e) {
			userService.deleteUser(account.getUsername());
			logger.error(e.getMessage(), e);
			return new ResponseMessage(ResponseMessage.Type.danger,
					"errorWithAccount", null);
		}

		// generate and send email
		this.sendRegistrationNotificationToAdmin(account);
		this.sendApplicationConfirmationNotification(account);

		return new ResponseMessage(ResponseMessage.Type.success, "userAdded",
				registeredAccount.getId().toString(), "true");
	}

	/**
	 * User forgot his password and requests a password reset
	 * */
	@RequestMapping(value = "/sooa/accounts/passwordreset", method = RequestMethod.POST)
	public ResponseMessage requestAccountPasswordReset(
			@RequestParam(required = false) String username,
			HttpServletRequest request) throws Exception {

		Account acc = null;

		if (username != null) {
			acc = accountRepository.findByTheAccountsUsername(username);
			if (acc == null) {
				acc = accountRepository.findByTheAccountsEmail(username);
			}
		} else {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"noUsernameOrEmail", null);
		}

		if (acc == null) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"wrongUsernameOrEmail", null);
		}

		User user = userService.retrieveUserByUsername(acc.getUsername());

		// start password reset process (for reset)
		// Create reset token. First get accountPasswordReset element from the
		// repository. If null create it.
		AccountPasswordReset arp = accountResetPasswordRepository
				.findByTheAccountsUsername(acc.getUsername());
		if (arp == null) {
			arp = new AccountPasswordReset();
			arp.setUsername(acc.getUsername());
		}

		arp.setCurrentToken(arp.getNewToken());
		arp.setTimestamp(new Date());
		arp.setNumberOfReset(arp.getNumberOfReset() + 1);

		accountResetPasswordRepository.save(arp);
		//
		// String port = "";
		// if (SERVER_PORT != null && !SERVER_PORT.isEmpty()) {
		// port = ":" + SERVER_PORT;
		// }

		// Generate url and email
		String url = getUrl(request) + "/#/resetPassword?userId="
				+ user.getUsername() + "&username=" + acc.getUsername()
				+ "&token="
				+ UriUtils.encodeQueryParam(arp.getCurrentToken(), "UTF-8");

		// System.out.println("****************** "+url+" *******************");

		// generate and send email
		this.sendAccountPasswordResetRequestNotification(acc, url);

		return new ResponseMessage(ResponseMessage.Type.success,
				"resetRequestProcessed", acc.getId().toString(), true);
	}

	/**
	 * User wants to change his password when already logged in
	 * */
	@PreAuthorize("hasPermission(#accountId, 'accessAccountBasedResource')")
	@RequestMapping(value = "/accounts/{accountId}/passwordchange", method = RequestMethod.POST)
	public ResponseMessage changeAccountPassword(
			@RequestBody AccountChangeCredentials acc,
			@PathVariable Long accountId) {

		// check there is a username in the request
		if (acc.getUsername() == null || acc.getUsername().isEmpty()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"usernameMissing", null);
		}

		if (acc.getNewPassword() == null || acc.getNewPassword().length() < 4) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"invalidPassword", null);
		}

		Account onRecordAccount = accountRepository.findOne(accountId);
		if (!onRecordAccount.getUsername().equals(acc.getUsername())) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"invalidUsername", null);
		}

		userService.changePasswordForPrincipal(acc.getPassword(),
				acc.getNewPassword());

		// send email notification
		this.sendChangeAccountPasswordNotification(onRecordAccount);

		return new ResponseMessage(ResponseMessage.Type.success,
				"accountPasswordReset", onRecordAccount.getId().toString(),
				true);
	}

	/**
	 * Admin wants to change user password
	 * */
	@PreAuthorize("hasRole('admin')")
	@RequestMapping(value = "/accounts/{accountId}/userpasswordchange", method = RequestMethod.POST)
	public ResponseMessage adminChangeAccountPassword(
			@RequestBody AccountChangeCredentials acc,
			@PathVariable Long accountId) {
		String newPassword = acc.getNewPassword();
		// check there is a username in the request
		if (acc.getUsername() == null || acc.getUsername().isEmpty()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"usernameMissing", null);
		}

		if (acc.getNewPassword() == null || acc.getNewPassword().length() < 4) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"invalidPassword", null);
		}

		Account onRecordAccount = accountRepository.findOne(accountId);
		if (!onRecordAccount.getUsername().equals(acc.getUsername())) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"invalidUsername", null);
		}

		userService.changePasswordForUser(acc.getUsername(),
				acc.getNewPassword());

		// send email notification
		this.sendChangeAccountPasswordNotification(onRecordAccount, newPassword);

		return new ResponseMessage(ResponseMessage.Type.success,
				"accountPasswordReset", onRecordAccount.getId().toString(),
				true);
	}

	/**
	 * User has to change his password and accept the agreement to complete the
	 * registration process
	 * */
	@RequestMapping(value = "/sooa/accounts/{userId}/passwordreset", method = RequestMethod.POST, params = "token")
	public ResponseMessage resetAccountPassword(@RequestBody Account acc,
			@PathVariable String userId,
			@RequestParam(required = true) String token) {

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ -5 ^^^^^^^^^^^^^^^^^^");

		// check there is a username in the request
		if (acc.getUsername() == null || acc.getUsername().isEmpty()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"usernameMissing", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ -4 ^^^^^^^^^^^^^^^^^^");

		AccountPasswordReset apr = accountResetPasswordRepository
				.findByTheAccountsUsername(acc.getUsername());

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ -3 ^^^^^^^^^^^^^^^^^^");

		// check there is a reset request on record
		if (apr == null) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"noResetRequestFound", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ -2 ^^^^^^^^^^^^^^^^^^");

		// check that for username, the token in record is the token passed in
		// request
		if (!apr.getCurrentToken().equals(token)) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"incorrectToken", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ -1 ^^^^^^^^^^^^^^^^^^");

		// check token is not expired
		if (apr.isTokenExpired()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"expiredToken", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 0 ^^^^^^^^^^^^^^^^^^");

		User onRecordUser = userService.retrieveUserByUsername(userId);

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ "+onRecordUser.getPassword()+" ^^^^^^^^^^^^^^^^^^");

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 1 ^^^^^^^^^^^^^^^^^^");

		Account onRecordAccount = accountRepository
				.findByTheAccountsUsername(acc.getUsername());

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 2 ^^^^^^^^^^^^^^^^^^");

		userService.changePasswordForUser(onRecordUser.getPassword(),
				acc.getPassword(), userId);
		if (!onRecordUser.isCredentialsNonExpired()) {
			userService.enableUserCredentials(userId);
		}
		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 3 ^^^^^^^^^^^^^^^^^^");

		// send email notification
		this.sendResetAccountPasswordNotification(onRecordAccount);

		return new ResponseMessage(ResponseMessage.Type.success,
				"accountPasswordReset", onRecordAccount.getId().toString());
	}

	/**
	 * 
	 * */
	@RequestMapping(value = "/sooa/accounts/register/{userId}/passwordreset", method = RequestMethod.POST, params = "token")
	public ResponseMessage resetRegisteredAccountPassword(
			@RequestBody AccountChangeCredentials racc,
			@PathVariable String userId,
			@RequestParam(required = true) String token) {

		// check there is a username in the request
		if (racc.getUsername() == null || racc.getUsername().isEmpty()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"usernameMissing", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 0 ^^^^^^^^^^^^^^^^^^");

		AccountPasswordReset apr = accountResetPasswordRepository
				.findByTheAccountsUsername(racc.getUsername());

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 1 ^^^^^^^^^^^^^^^^^^");

		// check there is a reset request on record
		if (apr == null) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"noResetRequestFound", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 2 ^^^^^^^^^^^^^^^^^^");

		// check that for username, the token in record is the token passed in
		// request
		if (!apr.getCurrentToken().equals(token)) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"incorrectToken", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 3 ^^^^^^^^^^^^^^^^^^");

		// check token is not expired
		if (apr.isTokenExpired()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"expiredToken", null);
		}

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 4 "+userId+" ^^^^^^^^^^^^^^^^^^");

		User onRecordUser = userService.retrieveUserByUsername(userId);
		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ "+onRecordUser.getPassword()+" ^^^^^^^^^^^^^^^^^^");

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 5 ^^^^^^^^^^^^^^^^^^");

		Account onRecordAccount = accountRepository
				.findByTheAccountsUsername(racc.getUsername());

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 6 ^^^^^^^^^^^^^^^^^^");

		// change the password
		userService.changePasswordForUser(onRecordUser.getPassword(),
				racc.getPassword(), userId);
		if (!onRecordUser.isCredentialsNonExpired()) {
			userService.enableUserCredentials(userId);
		}
		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 7 ^^^^^^^^^^^^^^^^^^");

		// update the agreement
		onRecordAccount.setSignedConfidentialityAgreement(racc
				.getSignedConfidentialityAgreement());
		onRecordAccount.setPending(false);
		accountRepository.save(onRecordAccount);

		// logger.debug("^^^^^^^^^^^^^^^^^^^^^ 8 ^^^^^^^^^^^^^^^^^^");
		Long expireTokenTime = (new Date()).getTime()
				- AccountPasswordReset.tokenValidityTimeInMilis;
		Date expireTokenDate = new Date();
		expireTokenDate.setTime(expireTokenTime);
		apr.setTimestamp(expireTokenDate);
		accountResetPasswordRepository.save(apr);

		// send email notification
		this.sendResetRegistrationAccountPasswordNotification(onRecordAccount);

		return new ResponseMessage(ResponseMessage.Type.success,
				"registeredAccountPasswordReset", onRecordAccount.getId()
						.toString());
	}

	/**
	 * 
	 * */
	@RequestMapping(value = "/sooa/accounts/forgottenusername", method = RequestMethod.GET)
	public ResponseMessage retrieveForgottenUsername(@RequestParam String email) {

		if (email == null || email.isEmpty()) {
			return new ResponseMessage(ResponseMessage.Type.danger, "badEmail",
					email);
		}

		Account acc = accountRepository.findByTheAccountsEmail(email);
		if (acc == null) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"noEmailRecords", email);
		}

		// send email with username
		this.sendRetrieveForgottenUsernameNotification(acc);

		return new ResponseMessage(ResponseMessage.Type.success,
				"usernameFound", email);

	}

	/**
	 * 
	 * */
	@PreAuthorize("hasPermission(#id, 'accessAccountBasedResource')")
	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.DELETE)
	public ResponseMessage deleteAccountById(@PathVariable Long id) {

		Account acc = accountRepository.findOne(id);

		if (acc == null || acc.isEntityDisabled()) {
			return new ResponseMessage(ResponseMessage.Type.danger,
					"badAccount", id.toString());
		} else {
			User u = userService.retrieveUserByUsername(acc.getUsername());
			if (u == null || !u.isEnabled()) {
				return new ResponseMessage(ResponseMessage.Type.danger,
						"badAccount", id.toString());
			} else {
				logger.debug("^^^^^^^^^^^^^^^^ about to disable user "
						+ acc.getUsername() + " ^^^^^^^^^^^^^^^^^");
				userService.disableUser(acc.getUsername());
				acc.setEntityDisabled(true);
				logger.debug("^^^^^^^^^^^^^^^^ about to save ^^^^^^^^^^^^^^^^^");
				accountRepository.save(acc);
				logger.debug("^^^^^^^^^^^^^^^^ saved ^^^^^^^^^^^^^^^^^");
				return new ResponseMessage(ResponseMessage.Type.success,
						"deletedAccount", id.toString(), true);
			}
		}
	}

	/**
	 * User wants to log in
	 * */
	@RequestMapping(value = "/accounts/login", method = RequestMethod.GET)
	public ResponseMessage doNothing() {
		return new ResponseMessage(ResponseMessage.Type.success,
				"loginSuccess", "succes");
	}

	/**
	 * 
	 * */
	@RequestMapping(value = "/accounts/cuser", method = RequestMethod.GET)
	public CurrentUser getCUser() {
		User u = userService.getCurrentUser();
		CurrentUser cu = null;
		if (u != null && u.isEnabled()) {
			Account a = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (!a.isPending()) {
				cu = new CurrentUser();
				cu.setUsername(u.getUsername());
				cu.setAccountId(a.getId());
				cu.setAuthenticated(true);
				cu.setAuthorities(u.getAuthorities());
				cu.setPending(a.isPending());
				cu.setFullName(a.getFullName());
			}
		}
		return cu;
	}

	private void sendApplicationConfirmationNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
		msg.setSubject("NIST IGAMT Application Received");
		msg.setTo(acc.getEmail());
		msg.setText("Dear "
				+ acc.getUsername()
				+ " \n\n"
				+ "Thank you for submitting an application for use of the NIST IGAMT. You will be notified via email (using the email address you provided in your application) as to whether your application is approved or not approved."
				+ "\n\n" + "Sincerely, " + "\n\n" + "The NIST IGAMT Team"
				+ "\n\n" + "P.S: If you need help, contact us at '"
				+ ADMIN_EMAIL + "'");
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendAccountRegistrationNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setSubject("Welcome! You are successfully registered on NIST IGAMT");
		msg.setTo(acc.getEmail());
		msg.setText("Dear " + acc.getUsername() + " \n\n"
				+ "You've successfully registered on the NIST IGAMT Site."
				+ " \n" + "Your username is: " + acc.getUsername() + " \n\n"
				+ "Please refer to the user guide for the detailed steps. "
				+ "\n\n" + "Sincerely, " + "\n\n" + "The NIST IGAMT Team"
				+ "\n\n" + "P.S: If you need help, contact us at '"
				+ ADMIN_EMAIL + "'");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendRegistrationNotificationToAdmin(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
		msg.setSubject("New Registration Application on IGAMT");
		msg.setTo(ADMIN_EMAIL);
		msg.setText("Hello Admin,  \n A new application has been submitted and is waiting for approval. The user information are as follow: \n\n"
				+ "Name: "
				+ acc.getFullName()
				+ "\n"
				+ "Email: "
				+ acc.getEmail()
				+ "\n"
				+ "Username: "
				+ acc.getUsername()
				+ "\n"
				+ "Title/Position: "
				+ acc.getTitle()
				+ "\n"
				+ "Employer: "
				+ acc.getEmployer()
				+ "\n"
				+ "Juridiction: "
				+ acc.getJuridiction()
				+ "\n"
				+ "Phone Number: "
				+ acc.getPhone()
				+ "\n"
				+ " \n\n"
				+ "Sincerely, " + "\n\n" + "The NIST IGAMT Team" + "\n\n");
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendAccountApproveNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Account Approval Notification ");
		msg.setText("Dear "
				+ acc.getUsername()
				+ " \n\n"
				+ "**** If you have not requested a new account, please disregard this email **** \n\n\n"
				+ "Your account has been approved and you can proceed "
				+ "to login .\n" + "\n\n" + "Sincerely, " + "\n\n"
				+ "The NIST IGAMT Team" + "\n\n"
				+ "P.S: If you need help, contact us at '" + ADMIN_EMAIL + "'");
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendAccountRegistrationPasswordResetNotification(Account acc,
			String url) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Registration Notification ");
		msg.setText("Dear "
				+ acc.getUsername()
				+ " \n\n"
				+ "**** If you have not requested a new account, please disregard this email **** \n\n\n"
				+ "Your account request has been processed and you can proceed "
				+ "to login .\n"
				+ "You need to change your password in order to login.\n"
				+ "Copy and paste the following url to your browser to initiate the password change:\n"
				+ url + " \n\n"
				+ "Please refer to the user guide for the detailed steps. "
				+ "\n\n" + "Sincerely, " + "\n\n" + "The NIST IGAMT Team"
				+ "\n\n" + "P.S: If you need help, contact us at '"
				+ ADMIN_EMAIL + "'");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendAccountPasswordResetRequestNotification(Account acc,
			String url) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Password Reset Request Notification");
		msg.setText("Dear "
				+ acc.getUsername()
				+ " \n\n"
				+ "**** If you have not requested a password reset, please disregard this email **** \n\n\n"
				+ "You password reset request has been processed.\n"
				+ "Copy and paste the following url to your browser to initiate the password change:\n"
				+ url + " \n\n" + "Sincerely, " + "\n\n" + "The IGAMT Team"
				+ "\n\n" + "P.S: If you need help, contact us at '"
				+ ADMIN_EMAIL + "'");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendChangeAccountPasswordNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Password Change Notification");
		msg.setText("Dear " + acc.getUsername() + " \n\n"
				+ "Your password has been successfully changed." + " \n\n"
				+ "Sincerely,\n\n" + "The NIST IGAMT Team");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendChangeAccountPasswordNotification(Account acc,
			String newPassword) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Password Change Notification");
		msg.setText("Dear " + acc.getUsername() + " \n\n"
				+ "Your password has been successfully changed." + " \n\n"
				+ "Your new temporary password is ." + newPassword + " \n\n"
				+ "Please update your password once logged in. \n\n"
				+ "Sincerely,\n\n" + "The NIST IGAMT Team");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendResetAccountPasswordNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Password Rest Notification");
		msg.setText("Dear " + acc.getUsername() + " \n\n"
				+ "Your password has been successfully reset." + " \n"
				+ "Your username is: " + acc.getUsername() + " \n\n"
				+ "Sincerely,\n\n" + "The NIST IGAMT Team");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendResetRegistrationAccountPasswordNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Registration and Password Notification");
		msg.setText("Dear " + acc.getUsername() + " \n\n"
				+ "Your password has been successfully set." + " \n"
				+ "Your username is: " + acc.getUsername() + " \n"
				+ "Your registration with the NIST IGAMT is complete."
				+ " \n\n" + "Sincerely,\n\n" + "The NIST IGAMT Team");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void sendRetrieveForgottenUsernameNotification(Account acc) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		msg.setTo(acc.getEmail());
		msg.setSubject("NIST IGAMT Username Notification");
		msg.setText("Dear " + acc.getUsername() + " \n\n"
				+ "Your username is: " + acc.getUsername() + " \n\n"
				+ "Sincerely,\n\n" + "The NIST IGAMT Team");

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private String generateNextUsername(String accountType) throws Exception {

		StringBuffer result = new StringBuffer();

		if (accountType.equals("provider")) {
			result.append("P-");
		} else if (accountType.equals("authorizedVendor")) {
			result.append("AV-");
		} else if (accountType.equals("supervisor")) {
			result.append("S-");
		} else if (accountType.equals("admin")) {
			result.append("A-");
		} else {
			result.append("U-");
		}

		int MAX_RETRY = 10;
		int retry = 0;
		while (userService.userExists(result.append(UserUtil.generateRandom())
				.toString())) {
			if (retry == MAX_RETRY) {
				throw new Exception("Can't generate username");
			}
			result.append(UserUtil.generateRandom()).toString();
			retry++;
		}

		return result.toString();
	}

	private String getUrl(HttpServletRequest request) {
		String scheme = request.getScheme();
		String host = request.getHeader("Host");
		return scheme + "://" + host + "/igamt";
	}
}
