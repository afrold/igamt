package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTLoginException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.GVTService;

@RestController
@RequestMapping("/gvt")
public class GVTController extends CommonController {

  Logger log = LoggerFactory.getLogger(IGDocumentController.class);


  @Autowired
  private GVTService gvtService;


  @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
  public boolean validCredentials(@RequestHeader("gvt-auth") String authorization)
      throws GVTLoginException {
    log.info("Logging to GVT");
    return gvtService.validCredentials(authorization);
  }



}
