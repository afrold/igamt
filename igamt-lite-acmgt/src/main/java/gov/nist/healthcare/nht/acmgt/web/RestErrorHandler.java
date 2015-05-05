/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

package gov.nist.healthcare.nht.acmgt.web;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author fdevaulx
 */
@ControllerAdvice
public class RestErrorHandler {

	static final Logger logger = LoggerFactory
			.getLogger(RestErrorHandler.class);

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseMessage processValidationError(
			MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		String errorMessage = "validationError"
				+ this.processFieldErrors(result.getFieldErrors());
		return new ResponseMessage(ResponseMessage.Type.danger, errorMessage, "");
	}

	private String processFieldErrors(List<FieldError> fieldErrors) {

		StringBuilder sb = new StringBuilder();

		for (FieldError fieldError : fieldErrors) {
			String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
			sb.append("\n");
			sb.append(fieldError.getField()).append(": ")
					.append(localizedErrorMessage);
		}

		String errorString = sb.toString();
		if (errorString.contains("exception")
				|| errorString.contains("Exception")) {
			return "Internal Error";
		} else {
			return sb.toString();
		}
	}

	private String resolveLocalizedErrorMessage(FieldError fieldError) {
		Locale currentLocale = LocaleContextHolder.getLocale();
		String localizedErrorMessage = messageSource.getMessage(fieldError,
				currentLocale);
		// logger.debug(localizedErrorMessage);
		// If the message was not found, return the most accurate field error
		// code instead.
		// You can remove this check if you prefer to get the default error
		// message.
		// if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
		// String[] fieldErrorCodes = fieldError.getCodes();
		// localizedErrorMessage = fieldErrorCodes[0];
		// }

		return localizedErrorMessage;
	}

}
