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
package gov.nist.healthcare.nht.acmgt.general.unit;

import gov.nist.healthcare.nht.acmgt.service.impl.UserServiceImpl;

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
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

/**
 * @author fdevaulx
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:app-config.xml")
public class MailTest {

	static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private MailSender mailSender;

	@Autowired
	private SimpleMailMessage templateMessage;

	private GreenMail testSmtp;

	@Before
	public void testSmtpInit() {
		testSmtp = new GreenMail(ServerSetupTest.SMTP);
		testSmtp.start();

		// don't forget to set the test port!
		((JavaMailSenderImpl) mailSender).setPort(3025);
		((JavaMailSenderImpl) mailSender).setHost("localhost");
		((JavaMailSenderImpl) mailSender).setProtocol("smtp");
		((JavaMailSenderImpl) mailSender).setUsername("");
		((JavaMailSenderImpl) mailSender).setPassword("");

	}

	@Test
	public void testSimpleMail() {
		// Create a thread safe "copy" of the template message and customize it
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
		msg.setText("This is a test message");
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			// simply log it and go on...
			System.err.println(ex.getMessage());
		}

		Message[] messages = testSmtp.getReceivedMessages();
		Assert.assertEquals(1, messages.length);
		try {
			Assert.assertEquals("ehr randomizer notification",
					messages[0].getSubject());
		} catch (MessagingException e) {
			logger.error(e.getMessage(), e);
		}
		String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n",
				"");
		Assert.assertEquals("This is a test message", body);
	}

	@After
	public void cleanup() {
		testSmtp.stop();
	}

}
