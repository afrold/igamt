package gov.nist.healthcare.nht.acmgt.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CSRFHandlerInterceptor implements HandlerInterceptor {

	protected final Log logger = LogFactory.getLog(getClass());

 	private CSRFTokenManager csrfTokenManager;

	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {

		// LoginController -- the only service which doesn't get checked for sign on.
//		if (handler.getClass().getName().equals(LOGIN_CONTROLLER_CLASS_NAME)) {
//			return true;
//		}
		logger.debug("^^^^^^^^^^^^^^^^^^ preHandling request URI "+request.getRequestURI());
		if (request.getRequestURI().contains("login")) {
			return true;
		}

		if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())
				|| "DELETE".equalsIgnoreCase(request.getMethod())) {
			String sessionToken = csrfTokenManager.getTokenForSession(request.getSession());
			String requestToken = csrfTokenManager.getTokenFromRequest(request);
			if (sessionToken.equals(requestToken)) {
				return true;
			} else {
				logger.error("^^^^^^^^^^^^^^^^^^^^^^^^ Possible CSRF attack! " + request.getRequestURI());
				String requestURI = request.getRequestURI();
				if (requestURI.contains("ignoreURL")) {
					return true;
				}
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad Request");
				return false;
			}
		} else {
			// idempotent request. Pass through
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		logger.debug("^^^^^^^^^^^^^^^^^^ postHandling request URI "+request.getRequestURI());
		//Set the CSRF token in the session.
		if (request.getSession() != null) {
			request.setAttribute(csrfTokenManager.CSRF_PARAM_NAME,
					csrfTokenManager.getTokenForSession(request.getSession()));
			
			logger.debug("^^^^^^^^^^^^^^^^^^ creating cookie ");
			Cookie cookie = new Cookie(csrfTokenManager.CSRF_PARAM_NAME,
					csrfTokenManager.getTokenForSession(request.getSession()));
			cookie.setMaxAge(60*60); //1 hour
			response.addCookie(cookie);
		} else {
			logger.debug("^^^^^^^^^^^^^^^^^^ NULL session ");
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {}

	public CSRFTokenManager getCsrfTokenManager() {
		return csrfTokenManager;
	}

	public void setCsrfTokenManager(CSRFTokenManager csrfTokenManager) {
		this.csrfTokenManager = csrfTokenManager;
	}
	
	
	
	
	
}
