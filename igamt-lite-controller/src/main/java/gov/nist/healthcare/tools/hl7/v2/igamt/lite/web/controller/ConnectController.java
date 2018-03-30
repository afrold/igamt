package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.GVTLoginException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.ConnectService;

@RestController
@RequestMapping("/connect")
public class ConnectController extends CommonController {

  Logger log = LoggerFactory.getLogger(IGDocumentController.class);


  @Autowired
  private ConnectService gvtService;


  @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
  public boolean validCredentials(@RequestHeader("target-auth") String authorization,@RequestHeader("target-url") String url)
      throws GVTLoginException {
    log.info("Logging to " + url);
    return gvtService.validCredentials(authorization,url);
  }


  @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<?> validCredentials(@RequestHeader("target-url") String url)
      throws GVTLoginException {
    log.info("Logging to " + url);
    return gvtService.getDomains(url);
  }

  
  
  

}
