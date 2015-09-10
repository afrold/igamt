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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
 

/**
 * Called when an exception occurs during request processing. Transforms the
 * exception message into JSON format.
 */  
@Component
public class JsonExceptionHandler implements HandlerExceptionResolver {
	private final ObjectMapper mapper = new ObjectMapper();

	static final Logger logger = LoggerFactory
			.getLogger(JsonExceptionHandler.class); 

	@Override   
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		try {
			if (ex instanceof AccessDeniedException) { 
				logger.error("ERROR: Access Denied", ex);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, "accessDenied"));
			} else if (ex instanceof BadCredentialsException) {
				logger.error("ERROR: Bad Credentials", ex);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, ex.getMessage()));
			} else {
				logger.error("ERROR: " + ex.getMessage(), ex);
				mapper.writeValue(response.getWriter(), new ResponseMessage(
						ResponseMessage.Type.danger, "internalError"));
			}
		} catch (IOException e) {
			// give up
			logger.error("ERROR: GAVE UP: " + e.getMessage(), e);
		}
		return new ModelAndView();
	}
}
