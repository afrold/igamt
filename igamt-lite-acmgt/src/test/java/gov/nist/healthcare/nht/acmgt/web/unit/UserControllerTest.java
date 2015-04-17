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

import static org.junit.Assert.fail;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.dto.domain.AccountPasswordReset;
import gov.nist.healthcare.nht.acmgt.general.SecurityRequestPostProcessors;
import gov.nist.healthcare.nht.acmgt.repo.AccountPasswordResetRepository;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;

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
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
public class UserControllerTest {

	static final Logger logger = LoggerFactory
			.getLogger(UserControllerTest.class);

	@Value("${server.scheme}")
	private String SERVER_SCHEME;

	@Value("${server.hostname}")
	private String SERVER_HOSTNAME;

	@Value("${server.port}")
	private String SERVER_PORT;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

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
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilters(this.springSecurityFilterChain).build();

		testSmtp = new GreenMail(ServerSetupTest.SMTP);
		testSmtp.start();

		// don't forget to set the test port!
		((JavaMailSenderImpl) mailSender).setPort(3025);
		((JavaMailSenderImpl) mailSender).setHost("localhost");
		((JavaMailSenderImpl) mailSender).setProtocol("smtp");
		((JavaMailSenderImpl) mailSender).setUsername("");
		((JavaMailSenderImpl) mailSender).setPassword("");

		Account acc = new Account();
		acc.setUsername("ut-user00");
		acc.setEmail("ut-user00@nist.gov");
		acc.setEmployer("company00");

		accountRepository.save(acc);
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
	public void testRegisterUserWhenAuthenticated() throws Exception {

		String userId = "";
		try {

			// test success
			MvcResult res = this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/accounts/register")
									.with(SecurityRequestPostProcessors
											.user("ut-user00").rolePrefix("")
											.roles("supervisor"))
									.content(
											" {\"accountType\":\"authorizedVendor\" ,"
													+ " \"email\":\"ut-user1@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"userAdded")).andReturn();
			;

			// test email duplicate
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/accounts/register")
									.with(SecurityRequestPostProcessors
											.user("ut-user00").rolePrefix("")
											.roles("supervisor"))
									.content(
											" {\"accountType\":\"authorizedVendor\" ,"
													+ " \"email\":\"ut-user1@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"duplicateInformation"));

			// test missing account type
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/accounts/register")
									.with(SecurityRequestPostProcessors
											.user("ut-user00").rolePrefix("")
											.roles("supervisor"))
									.content(
											" {"
													+ " \"email\":\"ut-user2@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"accountTypeMissing"));

			// test wrong account type
			this.mockMvc
					.perform(
							MockMvcRequestBuilders
									.post("/accounts/register")
									.with(SecurityRequestPostProcessors
											.user("ut-user00").rolePrefix("")
											.roles("supervisor"))
									.content(
											" {\"accountType\":\"authorizedV\" ,"
													+ " \"email\":\"ut-user2@nist.gov\" ,"
													+ "\"signedConfidentialityAgreement\":\"true\" }")
									.accept(MediaType.APPLICATION_JSON)
									.contentType(MediaType.APPLICATION_JSON))
					// .andDo(print())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(
							MockMvcResultMatchers.jsonPath("$.text").value(
									"accountTypeNotValid"));

			// check user and account store
			// System.out.println(res.getResponse().getContentAsString());
			String successResponseJson = res.getResponse().getContentAsString();
			userId = successResponseJson.split(":")[successResponseJson
					.split(":").length - 1];
			userId = userId.replaceAll("\"", "");
			userId = userId.replaceAll("}", "");
			// System.out.println(userId);

			Assert.assertNotNull(userService.retrieveUserByUsername(userId));
			Account account = accountRepository
					.findByTheAccountsUsername(userId);
			Assert.assertNotNull(account);
			Assert.assertTrue(account.isPending());

			// check the agreement hasn't been touched
			Assert.assertFalse(account.getSignedConfidentialityAgreement());

			// check account password reset
			AccountPasswordReset apr = accountPassworResetRepository
					.findByTheAccountsUsername(userId);
			Assert.assertTrue(apr.getNumberOfReset() == 1);

			// check email

			String port = "";
			if (SERVER_PORT != null && !SERVER_PORT.isEmpty()) {
				port = ":" + SERVER_PORT;
			}

			// Generate url and email
			String url = SERVER_SCHEME + "://" + SERVER_HOSTNAME + port
					+ "/ehr-randomizer-app"
					+ "/#/registerResetPassword?userId="
					+ account.getUsername() + "&username="
					+ account.getUsername() + "&token="
					+ UriUtils.encodeQueryParam(apr.getCurrentToken(), "UTF-8");

			Message[] messages = testSmtp.getReceivedMessages();
			Assert.assertEquals(1, messages.length);
			try {
				Assert.assertEquals(
						"ehr randomizer notification for Cross Vendor Exchange Demonstration Pilot",
						messages[0].getSubject());
			} catch (MessagingException e) {
				logger.error(e.getMessage(), e);
			}
			String body = GreenMailUtil.getBody(messages[0]).replaceAll(
					"=\r?\n", "");
			Assert.assertTrue(body.contains(userId));
			Assert.assertTrue(body
					.contains("Your account request has been processed"));
			Assert.assertTrue(body.contains(url));
		} finally {
			this.afterTestDeleteUser(userId);
		}
	}

	@Test
	public void testChangeAccountPassword() throws Exception {
		// TODO figure out how to simulate basic authentication
	}

	// @Test
	public void testGetCurrentUser() throws Exception {
		fail("Not yet implemented");
	}
}
