package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;

@RestController
@RequestMapping("/search")
public class SearchController extends CommonController {
	Logger log = LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Autowired
	private SegmentService segmentService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private TableService tableService;
	
	@RequestMapping(value = "/datatypes", method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> getDatatypes(@RequestParam(value="name", required=false) String name,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
		if((name != null && !name.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())){
			if(name != null && !name.isEmpty()){
				if(hl7Version != null && !hl7Version.isEmpty()){
					Datatype datatype = datatypeService.findOneByNameAndVersionAndScope(name, hl7Version, SCOPE.HL7STANDARD.name());
					ArrayList<Datatype> result = new ArrayList<>();
					result.add(datatype);
					return result;
				}else {
					return datatypeService.findByNameAndScope(name, SCOPE.HL7STANDARD.name());
				}
			} else {
				return datatypeService.findByScopeAndVersion(SCOPE.HL7STANDARD.name(), hl7Version);
			}
		}
		return datatypeService.findByScope(SCOPE.HL7STANDARD.name());
	}
	
	@RequestMapping(value = "/segments", method = RequestMethod.GET, produces = "application/json")
	public List<Segment> getSegments(@RequestParam(value="name", required=false) String name,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
		if((name != null && !name.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())){
			if(name != null && !name.isEmpty()){
				if(hl7Version != null && !hl7Version.isEmpty()){
					Segment segment = segmentService.findByNameAndVersionAndScope(name, hl7Version, SCOPE.HL7STANDARD.name());
					ArrayList<Segment> result = new ArrayList<>();
					result.add(segment);
					return result;
				}else {
					return segmentService.findByNameAndScope(name, SCOPE.HL7STANDARD.name());
				}
			} else {
				return segmentService.findByScopeAndVersion(SCOPE.HL7STANDARD.name(), hl7Version);
			}
		}
		return segmentService.findByScope(SCOPE.HL7STANDARD.name());
	}
	
	@RequestMapping(value = "/messages", method = RequestMethod.GET, produces = "application/json")
	public List<Message> getMessages(@RequestParam(value="name", required=false) String name,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
		if((name != null && !name.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())){
			if(name != null && !name.isEmpty()){
				if(hl7Version != null && !hl7Version.isEmpty()){
					Message message = messageService.findByNameAndVersionAndScope(name, hl7Version, SCOPE.HL7STANDARD.name());
					ArrayList<Message> result = new ArrayList<>();
					result.add(message);
					return result;
				}else {
					return messageService.findByNameAndScope(name, SCOPE.HL7STANDARD.name());
				}
			} else {
				return messageService.findByScopeAndVersion(SCOPE.HL7STANDARD.name(), hl7Version);
			}
			
		}
		return messageService.findByScope(SCOPE.HL7STANDARD.name());
	}
	
	@RequestMapping(value = "/valueSets", method = RequestMethod.GET, produces = "application/json")
	public List<Table> getValueSets(@RequestParam(value="scope", required=false) String scope, @RequestParam(value="bindingIdentifier", required=false) String bindingIdentifier,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
	  //By default, the scope is HL7
	  if(scope == null){
	    scope = SCOPE.HL7STANDARD.name();
	  }
	  if(scope.equals(SCOPE.HL7STANDARD.name()) || scope.equals(SCOPE.PHINVADS.name())){
			if((bindingIdentifier != null && !bindingIdentifier.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())){
				if(bindingIdentifier != null && !bindingIdentifier.isEmpty()){
					if(hl7Version != null && !hl7Version.isEmpty()){
						Table table = tableService.findByScopeAndVersionAndBindingIdentifier(SCOPE.HL7STANDARD,bindingIdentifier, hl7Version);
						ArrayList<Table> result = new ArrayList<>();
						result.add(table);
						return result;
					}else {
						return tableService.findByBindingIdentifierAndScope(bindingIdentifier, scope);
					}
				} else {
					if(scope.equals(SCOPE.HL7STANDARD.name()) && hl7Version != null && !hl7Version.isEmpty()){
						return tableService.findByScopeAndVersion(scope, hl7Version);
					}
				}
			}
			return tableService.findByScope(scope);
		}
		return null;
	}
	
}
