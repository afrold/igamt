/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 * */
package gov.nist.healthcare.nht.acmgt.web;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Just return 401-unauthorized for every unauthorized request. The client side
 * catches this and handles login itself.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    public final void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//    }
    
	
	static final Logger logger = LoggerFactory
			.getLogger(RestAuthenticationEntryPoint.class);

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public final void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException {
		try {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			if (authException instanceof BadCredentialsException) {
				logger.error("ERROR: Bad Credentials", authException);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, authException.getMessage()));
			} else if (authException instanceof DisabledException) {
				logger.error("ERROR: Disabled User", authException);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, authException.getMessage()));
			} else if (authException instanceof LockedException) {
				logger.error("ERROR: Locked User", authException);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, authException.getMessage()));
			} else if (authException instanceof CredentialsExpiredException) {
				logger.error("ERROR: Credentials Expired", authException);
				mapper.writeValue(response.getWriter(),
						new ResponseMessage(ResponseMessage.Type.danger,
								"accountCredentialsExpired"));
			} else if (authException instanceof AccountExpiredException) {
				logger.error("ERROR: Account Expired", authException);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, authException.getMessage()));
			} else {
				logger.debug("[Exception]: "
						+ authException.getClass().getSimpleName());
				logger.error("ERROR: Other Error", authException);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, "accessDenied"));
			}

		} catch (IOException e) {
			logger.error("ERROR: GAVE UP: " + e.getMessage(), e);
		}
		// response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
		// authException.getMessage());
	}
}
