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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 17, 2015
 */
// @Component("csrfHeaderFilter")
// @WebFilter
public class CsrfHeaderFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
		// .getName());
		// if (csrf != null) {
		// Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
		// String token = csrf.getToken();
		// if (cookie == null || token != null
		// && !token.equals(cookie.getValue())) {
		// cookie = new Cookie("XSRF-TOKEN", token);
		// cookie.setPath("/");
		// response.addCookie(cookie);
		// }
		// }
		// filterChain.doFilter(request, response);
	}
}