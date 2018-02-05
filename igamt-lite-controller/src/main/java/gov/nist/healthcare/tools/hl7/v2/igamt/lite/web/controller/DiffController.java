package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Delta;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta.DeltaElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.CompareServiceImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.DatatypeArrow;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/diff")

public class DiffController {
	@Autowired
	CompareServiceImpl compareService;
	
	  @RequestMapping(value = "/datatype", method = RequestMethod.POST, produces = "application/json")
	  public DeltaElement compareSerice(@RequestBody  DatatypeArrow arrow) {
		  return  compareService.compareDatatype( arrow.getD1(), arrow.getD2(),"");
		  
		  
	  }

}
