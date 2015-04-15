package gov.nist.healthcare.nht.acmgt.web.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

public class CSRFTokenManager {

	protected final Log logger = LogFactory.getLog(getClass());
	
	/**
	 * The token parameter name
	 */
	public static final String CSRF_PARAM_NAME = "XSRF-TOKEN";

	/**
	 * The location on the session which stores the token
	 */
	private final static String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class.getName() + ".tokenval";

	public String getTokenForSession (HttpSession session) {
		String token = null;
		// I cannot allow more than one token on a session - in the case of two requests trying to
		// init the token concurrently
		synchronized (session) {
			token = (String) session.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
			logger.debug("token from session " + token);
			if (null==token) {
				token=UUID.randomUUID().toString();
				logger.debug("new token for session " + token);
				session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
			}
		}
		return token;
	}

	/**
	 * Extracts the token value from the session
	 * @param request
	 * @return
	 */
	public String getTokenFromRequest(HttpServletRequest request) {
		return request.getParameter(CSRF_PARAM_NAME);
	}

	public CSRFTokenManager() {};

}
