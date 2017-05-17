package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/export")
public class ExportController extends CommonController{
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Autowired
	private ExportService exportService;
	
	@RequestMapping(value = "/datatype/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public Datatype getDatatypeAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		Datatype datatype = datatypeService.findById(id);
		if(datatype!=null && datatype.getScope().equals(SCOPE.HL7STANDARD)){
			return datatype;
		}
		return null;
	}
	
	@RequestMapping(value = "/datatype/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getDatatypeAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		Datatype datatype = datatypeService.findById(id);
		if(datatype!=null && datatype.getScope().equals(SCOPE.HL7STANDARD)){
			return exportService.exportDataModelAsHtml(datatype,datatype.getName());
		}
		return null;
	}
}
