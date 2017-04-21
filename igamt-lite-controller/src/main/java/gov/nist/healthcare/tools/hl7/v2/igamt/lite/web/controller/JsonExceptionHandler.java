/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgment if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

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

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentDeleteException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentListException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTExportException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTLoginException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.OperationNotAllowException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.SegmentDeleteException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.SegmentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.TableSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UploadImageFileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

/**
 * Called when an exception occurs during request processing. Transforms the exception message into
 * JSON format.
 */
@Component
public class JsonExceptionHandler implements HandlerExceptionResolver {
  private final ObjectMapper mapper = new ObjectMapper();

  static final Logger logger = LoggerFactory.getLogger(JsonExceptionHandler.class);

  @Override
  public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) {
    try {
      if (ex instanceof AccessDeniedException) {
        logger.error("ERROR: Access Denied", ex);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "accessDenied"));
      } else if (ex instanceof UserAccountNotFoundException) {
        logger.error("ERROR: User account not found", ex);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "accountNotFound", null));
      } else if (ex instanceof IGDocumentNotFoundException) {
        logger.error("ERROR: document not found", ex);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "IGDocumentNotFound"));
      } else if (ex instanceof IGDocumentSaveException) {
        logger.error("ERROR: Access Denied", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "igDocumentNotSaved"));
      } else if (ex instanceof IGDocumentDeleteException) {
        logger.error("ERROR: Access Denied", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "igDocumentNotDeleted"));
      } else if (ex instanceof OperationNotAllowException) {
        logger.error("ERROR: Access Denied", ex);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "operationNotAllow"));
      } else if (ex instanceof IGDocumentException) {
        logger.error("ERROR: Access Denied", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "igDocumentIssue"));
      } else if (ex instanceof IGDocumentListException) {
        logger.error("ERROR: Access Denied", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "igDocumentListFailed"));
      } else if (ex instanceof UploadImageFileException) {
        logger.error("ERROR: Failed to upload the image file", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, ex.getMessage()));
      } else if (ex instanceof SegmentSaveException) {
        logger.error("ERROR: Failed to save the segment", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "segmentSaveFailed"));
      } else if (ex instanceof TableSaveException) {
        logger.error("ERROR: Failed to save the table", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "tableSaveFailed"));
      } else if (ex instanceof DatatypeSaveException) {
        logger.error("ERROR: Failed to save the datatype", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "datatypeSaveFailed"));
      } else if (ex instanceof ForbiddenOperationException) {
        logger.error("ERROR: Failed to execute operation", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "forbiddenOperation"));
      } else if (ex instanceof SegmentDeleteException) {
        logger.error("ERROR: Failed to delete a segment", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, ex.getMessage()));
      } else if (ex instanceof DataNotFoundException) {
        logger.error("ERROR: Failed to retrieve a data", ex);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, ex.getMessage()));
      } else if (ex instanceof BadCredentialsException) {
        logger.error("ERROR: Failed to retrieve a data", ex);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, ex.getMessage()));
      } else if (ex instanceof GVTExportException) {
        logger.error("ERROR: Failed to export to GVT", ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, ex.getMessage()));
      } else if (ex instanceof GVTLoginException) {
        logger.error("ERROR: Failed to login to GVT", ex);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "gvtLoginFailed"));
      } else {
        logger.error("ERROR: " + ex.getMessage(), ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        mapper.writeValue(response.getWriter(),
            new ResponseMessage(ResponseMessage.Type.danger, "internalError"));
      }
    } catch (IOException e) {
      // give up
      logger.error("ERROR: GAVE UP: " + e.getMessage(), e);
    }
    return new ModelAndView();
  }

}
