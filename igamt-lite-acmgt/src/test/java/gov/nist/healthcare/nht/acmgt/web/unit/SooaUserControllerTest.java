/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.web.unit;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.dto.domain.AccountPasswordReset;
import gov.nist.healthcare.nht.acmgt.repo.AccountPasswordResetRepository;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

/**
 * @author fdevaulx
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration("classpath:app-config.xml"),
		@ContextConfiguration("classpath:action-servlet.xml") })
@Transactional
public class SooaUserControllerTest {

	static final Logger logger = LoggerFactory
			.getLogger(UserControllerTest.class);

	@Value("${server.hostname}")
	private String SERVER_HOSTNAME;

	@Value("${server.port}")
	private String SERVER_PORT;

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccountPasswordResetRepository accountPassworResetRepository;

	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;

	@Autowired
	private MailSender mailSender;

	private GreenMail testSmtp;

	// private boolean useAuthDB = false;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

		testSmtp = new GreenMail(ServerSetupTest.SMTP);
		testSmtp.start();

		// don't forget to set the test port!
		((JavaMailSenderImpl) mailSender).setPort(3025);
		((JavaMailSenderImpl) mailSender).setHost("localhost");
		((JavaMailSenderImpl) mailSender).setProtocol("smtp");
		((JavaMailSenderImpl) mailSender).setUsername("");
		((JavaMailSenderImpl) mailSender).setPassword("");

	}

	public void afterTestDeleteUser(String userId) {
		JdbcTemplate jt = jdbcUserDetailsManager.getJdbcTemplate();
		jt.execute("DELETE FROM ehrauth.authorities WHERE username='" + userId
				+ "'");
		jt.execute("DELETE FROM ehrauth.users WHERE username='" + userId + "'");
	}

	@After
	public void cleanup() {
		testSmtp.stop();
	}

	@Test
	public void testRegisterUserWhenNotAuthenticated() throws Exception {

		String userId = "ut-user1";
		try {

			userService.createUserWithAuthorities("ut-user2", "pass",
					"user,supervisor");
			User utuser2 = userService.retrieveUserByUsername("ut-user2");
			Account utaccount2 = new Account();
			utaccount2.setUsername("ut-user2");
			utaccount2.setEmail("ut-user2@nist.gov");
			utaccount2.setAccountType("supervisor");
			accountRepository.save(utaccount2);

			// check success
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/register")
									.content(
											" {\"accountType\":\"provider\" ,"
													+ "\"username\":\"ut-user1\" ,"
													+ " \"password\":\"testpass\" ,"
													+ " \"firstname\":\"\" ,"
													+ " \"lastname\":\"\" ,"
													+ " \"email\":\"ut-user1@nist.gov\" ,"
													+ "\"company\":\"company1\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					// .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"userAdded"));

			// check duplicate account / user username
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/register")
									.content(
											" {\"accountType\":\"supervisor\" ,"
													+ "\"username\":\"ut-user2\" ,"
													+ " \"password\":\"testpass\" ,"
													+ " \"email\":\"ut-user3@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"duplicateInformation"));

			// check duplicate account email
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/register")
									.content(
											" {\"accountType\":\"supervisor\" ,"
													+ "\"username\":\"ut-user3\" ,"
													+ " \"password\":\"testpass\" ,"
													+ " \"email\":\"ut-user2@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"duplicateInformation"));

			// check wrong account type
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/register")
									.content(
											" {\"accountType\":\"supervis\" ,"
													+ "\"username\":\"ut-user3\" ,"
													+ " \"password\":\"testpass\" ,"
													+ " \"email\":\"ut-user3@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"accountTypeNotValid"));

			// check user and account store
			Assert.assertNotNull(userService.retrieveUserByUsername("ut-user1"));
			Account account = accountRepository
					.findByTheAccountsUsername("ut-user1");
			Assert.assertNotNull(account);
			Assert.assertTrue(account.getSignedConfidentialityAgreement());

			// check email
			Message[] messages = testSmtp.getReceivedMessages();
			Assert.assertEquals(1, messages.length);
			try {
				Assert.assertEquals("ehr randomizer notification",
						messages[0].getSubject());
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
			}
			String body = GreenMailUtil.getBody(messages[0]).replaceAll(
					"=\r?\n", "");
			Assert.assertTrue(body.contains("ut-user1"));
			Assert.assertTrue(body
					.contains("Your account has been successfully created."));
		} finally {
			this.afterTestDeleteUser(userId);
			this.afterTestDeleteUser("ut-user2");
		}

	}

	@Test
	public void testRequestAccountPasswordReset() throws Exception {

		String userId = "ut-user1";
		try {

			userService.createUserWithAuthorities(userId, "pass",
					"user,supervisor");
			User utuser1 = userService.retrieveUserByUsername(userId);
			Account acc = new Account();
			acc.setUsername(userId);
			acc.setEmail("ut-user1@nist.gov");
			accountRepository.save(acc);

			// check no username of email
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/passwordreset")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"noUsernameOrPassoword"));

			// check existing username of email
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/passwordreset?username=ut-user00")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"wrongUsernameOrPassoword"));

			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/passwordreset?email=ut-user00")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"wrongUsernameOrPassoword"));

			// check success case
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/passwordreset?username=ut-user1")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"resetRequestProcessed"));

			AccountPasswordReset apr = accountPassworResetRepository
					.findByTheAccountsUsername("ut-user1");
			Assert.assertNotNull(apr);

			// check email
			String port = "";
			if (SERVER_PORT != null && !SERVER_PORT.isEmpty()) {
				port = ":" + SERVER_PORT;
			}

			// Generate url and email
			String url = "http" + "://" + SERVER_HOSTNAME + port
					+ "/ehr-randomizer-app" + "/#/resetPassword?userId="
					+ userId + "&username=" + userId + "&token="
					+ UriUtils.encodeQueryParam(apr.getCurrentToken(), "UTF-8");

			Message[] messages = testSmtp.getReceivedMessages();
			Assert.assertEquals(1, messages.length);
			try {
				Assert.assertEquals("ehr randomizer notification",
						messages[0].getSubject());
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
			}
			String body = GreenMailUtil.getBody(messages[0]).replaceAll(
					"=\r?\n", "");
			Assert.assertTrue(body.contains(userId));
			Assert.assertTrue(body
					.contains("You password reset request has been processed."));
			Assert.assertTrue(body.contains(url));

		} finally {
			this.afterTestDeleteUser(userId);
		}
	}

	@Test
	public void testRetrieveForgottenUsername() throws Exception {

		String userId = "ut-user1";
		try {

			Account acc = new Account();
			acc.setUsername(userId);
			acc.setEmail("ut-user1@nist.gov");
			accountRepository.save(acc);

			// check success
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.get("/sooa/accounts/forgottenusername?email=ut-user1@nist.gov")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"usernameFound"));

			// check success
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.get("/sooa/accounts/forgottenusername?email=")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"badEmail"));

			// check no email in records
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.get("/sooa/accounts/forgottenusername?email=ut-user0000@nist.gov")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"noEmailRecords"));

			// check email
			Message[] messages = testSmtp.getReceivedMessages();
			Assert.assertEquals(1, messages.length);
			try {
				Assert.assertEquals("ehr randomizer notification",
						messages[0].getSubject());
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
			}
			String body = GreenMailUtil.getBody(messages[0]).replaceAll(
					"=\r?\n", "");
			Assert.assertTrue(body.contains(userId));
			Assert.assertTrue(body.contains("Your username is:"));

		} finally {
			this.afterTestDeleteUser(userId);
		}
	}

	@Test
	public void testResetAccountPassword() throws Exception {
		String userId = "ut-user1";
		try {

			userService.createUserWithAuthorities(userId, "pass",
					"user,supervisor");
			User utuser1 = userService.retrieveUserByUsername(userId);
			Account acc = new Account();
			acc.setUsername(userId);
			acc.setEmail("ut-user1@nist.gov");
			accountRepository.save(acc);

			// start password reset process (for reset)
			// Create reset token. First get accountPasswordReset element from
			// the repository. If null create it.
			AccountPasswordReset arp = accountPassworResetRepository
					.findByTheAccountsUsername(acc.getUsername());
			if (arp == null) {
				arp = new AccountPasswordReset();
				arp.setUsername(acc.getUsername());
			}

			arp.setCurrentToken(arp.getNewToken());
			arp.setTimestamp(new Date());
			arp.setNumberOfReset(arp.getNumberOfReset() + 1);

			accountPassworResetRepository.save(arp);

			// check success
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/" + userId
											+ "/passwordreset?token="
											+ arp.getCurrentToken())
									.content(
											" {"
													+ "\"username\":\"ut-user1\" ,"
													+ " \"password\":\"testpass\" "
													+ " }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					.andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"accountPasswordReset"));

			// check username missing
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/" + userId
											+ "/passwordreset?token="
											+ arp.getCurrentToken())
									.content(
											" {" + " \"password\":\"testpass\""
													+ " }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"usernameMissing"));

			// check reset request
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/" + userId
											+ "/passwordreset?token="
											+ arp.getCurrentToken())
									.content(
											" {"
													+ "\"username\":\"ut-user0000\" ,"
													+ " \"password\":\"testpass\" "
													+ " }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"noResetRequestFound"));

			// check incorrect token
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/sooa/accounts/" + userId
											+ "/passwordreset?token=test")
									.content(
											" {"
													+ "\"username\":\"ut-user1\" ,"
													+ " \"password\":\"testpass\" "
													+ " }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"incorrectToken"));

			// TODO check token expired

			// check email
			Message[] messages = testSmtp.getReceivedMessages();
			Assert.assertEquals(1, messages.length);
			try {
				Assert.assertEquals("ehr randomizer notification",
						messages[0].getSubject());
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
			}
			String body = GreenMailUtil.getBody(messages[0]).replaceAll(
					"=\r?\n", "");
			Assert.assertTrue(body.contains(userId));
			Assert.assertTrue(body
					.contains("Your password has been successfully reset."));

		} finally {
			this.afterTestDeleteUser(userId);
		}

	}

}
