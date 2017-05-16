package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	public List<Datatype> getDatatypes(@RequestParam(value="name", required=false) String name) throws DataNotFoundException {
		if(name != null && !name.isEmpty()){
			return datatypeService.findByNameAndScope(name, SCOPE.HL7STANDARD.name());
		}
		return datatypeService.findByScope(SCOPE.HL7STANDARD.name());
	}
	
	@RequestMapping(value = "/datatype", method = RequestMethod.GET, produces = "application/json")
	public Datatype getDatatype(@RequestParam(value="name") String name,@RequestParam(value="hl7Version") String hl7Version) throws DataNotFoundException {
		if(name != null && !name.isEmpty() && hl7Version != null && !hl7Version.isEmpty()){
			return datatypeService.findByNameAndVesionAndScope(name, hl7Version, SCOPE.HL7STANDARD.name());
		}
		return null;
	}
	
	@RequestMapping(value = "/segments", method = RequestMethod.GET, produces = "application/json")
	public List<Segment> getSegments(@RequestParam(value="name", required=false) String name) throws DataNotFoundException {
		if(name != null && !name.isEmpty()){
			return segmentService.findByNameAndScope(name, SCOPE.HL7STANDARD.name());
		}
		return segmentService.findByScope(SCOPE.HL7STANDARD.name());
	}
	
	@RequestMapping(value = "/segment", method = RequestMethod.GET, produces = "application/json")
	public Segment getSegment(@RequestParam(value="name") String name,@RequestParam(value="hl7Version") String hl7Version) throws DataNotFoundException {
		if(name != null && !name.isEmpty() && hl7Version != null && !hl7Version.isEmpty()){
			return segmentService.findByNameAndVersionAndScope(name, hl7Version, SCOPE.HL7STANDARD.name());
		}
		return null;
	}
	
}
