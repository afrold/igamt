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
package gov.nist.healthcare.nht.acmgt.dto.unit;

import gov.nist.healthcare.nht.acmgt.dto.domain.AccountPasswordReset;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

/**
 * @author fdevaulx
 * 
 */
public class AccountPasswordResetTest {

	static final Logger logger = LoggerFactory
			.getLogger(AccountPasswordResetTest.class);

	@Test
	public void testIsTokenExpired() {
		// AccountPasswordReset apr = new AccountPasswordReset();
		// apr.setTimestamp(new Date(0L));
		// Assert.assertTrue(apr.isTokenExpired());
		//
		// apr.setTimestamp(new Date());
		// Assert.assertTrue(!apr.isTokenExpired());
	}

	// @Test
	public void testGetNewToken() {
		AccountPasswordReset apr = new AccountPasswordReset();
		@SuppressWarnings("unused")
		boolean gotToken = false;
		try {
			apr.getCurrentToken();
			gotToken = true;
		} catch (Exception e) {
			Assert.assertTrue(gotToken = false);
			Assert.assertTrue("usernameIsNull".equals(e.getMessage()));
		}

		apr.setUsername("testu");
		String token1 = "";
		String token2 = "";
		try {
			token1 = apr.getNewToken();
			token2 = apr.getNewToken();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// System.out.println(token1);
			// System.out.println(new String(Base64.decode(token1.getBytes()),
			// "UTF-8"));
			Assert.assertTrue(new String(Base64.decode(token1.getBytes()),
					"UTF-8").startsWith(apr.getUsername()));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		Assert.assertTrue(!token1.equals(token2));
		Assert.assertTrue(Base64.isBase64(token1.getBytes()));

	}
}
