package gov.nist.healthcare.nht.acmgt.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

public class HTTPSecurityFilter implements Filter {

	/*
	 * flaw: Browser Mime Sniffing - fix: X-Content-Type-Options
	 * flaw: Cached SSL Content    - fix: Cache-Control
	 * flaw: Cross-Frame Scripting - fix: X-Frame-Options
	 * flaw: Cross-Site Scripting  - fix: X-XSS-Protection
	 * flaw: Force SSL			   - fix: Strict-Transport-Security
	 * 
	 * assure no-cache for login page to prevent IE from caching
	 * */

	protected final Log logger = LogFactory.getLog(getClass());

	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		String cache = "no-cache";
		if ( request instanceof HttpServletRequest){
			HttpServletRequest HttpRequest = (HttpServletRequest)request;
			try {
				cache = (HttpRequest.getRequestURI().contains("action")) ? "no-cache" : "private";
                                logger.debug("Cache-Control for URI:"+HttpRequest.getRequestURI()+" | "+cache);
			} catch (Exception e){
				logger.error(e.getMessage(), e);
			}
		}

		if ( response instanceof HttpServletResponse){
			HttpServletResponse HttpResponse = (HttpServletResponse)response;
			HttpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
			HttpResponse.setHeader("Cache-Control", cache);
			HttpResponse.setHeader("X-Content-Type-Options", "nosniff");
			HttpResponse.setHeader("Strict-Transport-Security", "max-age=31536000");
			HttpResponse.setHeader("X-XSS-Protection", "1; mode=block");
			
		}

		chain.doFilter(request,response);

	}

}
