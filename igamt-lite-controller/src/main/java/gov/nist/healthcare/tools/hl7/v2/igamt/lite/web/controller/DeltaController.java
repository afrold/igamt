package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DeltaService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/profileComponent")
public class DeltaController {

	Logger log = LoggerFactory.getLogger(DeltaController.class);
	
	  @Autowired
	  private DeltaService deltaService;
	  
	  
	  @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = "application/json")
	  public Delta getDeltaById(@PathVariable("id") String id) throws DataNotFoundException {
	    log.info("Fetching Profile component ById..." + id);
	    return deltaService.findById(id);
	  }
	  
	  
	  @RequestMapping(value = "/save", method = RequestMethod.POST)
	  public Delta save(@RequestBody Delta Delta) {

	      Delta saved = deltaService.save(Delta);
	      return saved;
	  }
	  
	  @RequestMapping(value = "/findAll", method = RequestMethod.POST)
	  public List<Delta> findAll() {

	      List<Delta> res = deltaService.findAll();
	      return res;
	  }
	  
}
