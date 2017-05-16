package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/search")
public class SearchController extends CommonController {
	Logger log = LoggerFactory.getLogger(ProfileComponentController.class);
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Autowired
	private SegmentService segmentService;

	
	@RequestMapping(value = "/datatypes", method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> getDatatypes() throws DataNotFoundException {
		return datatypeService.findByScope(SCOPE.MASTER.name());
	}
	
	@RequestMapping(value = "/segments", method = RequestMethod.GET, produces = "application/json")
	public List<Segment> getSegments() throws DataNotFoundException {
		return segmentService.findByScope(SCOPE.MASTER.name());
	}
	
}
