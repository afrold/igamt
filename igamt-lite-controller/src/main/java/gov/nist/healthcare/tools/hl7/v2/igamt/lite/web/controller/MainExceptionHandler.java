/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.IGDocumentSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.OperationNotAllowException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Harold Affo (NIST)
 * 
 */
@ControllerAdvice
public class MainExceptionHandler {
  static final Logger logger = LoggerFactory.getLogger(MainExceptionHandler.class);

  public MainExceptionHandler() {
    super();
  }



  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public String exception(RuntimeException ex) {
    logger.error(ex.getMessage(), ex);
    return "Sorry, something went wrong.\n";
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String exception(Exception ex) {
    logger.error(ex.getMessage(), ex);
    return "Sorry, something went wrong.\n";
  }

  @ExceptionHandler(UserAccountNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseMessage igDocumentNotFound(UserAccountNotFoundException ex) {
	  logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.danger,
				"accountNotFound", null);
	}

	@ExceptionHandler(IGDocumentNotFoundException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseMessage igDocumentNotFound(IGDocumentException ex) {
		logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.danger,
				"IGDocumentNotFound", null);
	}

	@ExceptionHandler(IGDocumentSaveException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public IGDocumentSaveResponse igDocumentSaveFailed(IGDocumentSaveException ex) {
		logger.debug(ex.getMessage());
		if (ex.getErrors() != null) {
			return new IGDocumentSaveResponse(ResponseMessage.Type.danger,
					"IGDocumentNotSaved", null, ex.getErrors());
		}
		return new IGDocumentSaveResponse(ResponseMessage.Type.danger,
				"IGDocumentNotSaved", null);
	}

	@ExceptionHandler(OperationNotAllowException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseMessage OperationNotAllowException(OperationNotAllowException ex) {
		logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.danger,
				"operationNotAllow", ex.getMessage());
	}  
	
	
	@ExceptionHandler(IGDocumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseMessage IGDocumentException(IGDocumentException ex) {
		logger.debug(ex.getMessage());
		return new ResponseMessage(ResponseMessage.Type.danger,
				"igDocumentIssue", ex.getMessage());
	}
	
	
	
  
  
  

}
