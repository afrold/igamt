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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
	
	@ApiOperation(value = "Search data types", notes = "Search by name (required) and HL7 version (not required). Send back a list of data types.")
	@ApiResponses({
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 400, message = "Bad request")
    })
	@RequestMapping(value = "/datatypes", method = RequestMethod.GET, produces = "application/json")
	public List<Datatype> getDatatypes(@RequestParam(value="name", required=true) String name,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
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
		return null;
	}
	
	@ApiOperation(value = "Search segments", notes = "Search by name (required) and HL7 version (not required). Send back a list of segments.")
    @ApiResponses({
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 400, message = "Bad request")
    })
	@RequestMapping(value = "/segments", method = RequestMethod.GET, produces = "application/json")
	public List<Segment> getSegments(@RequestParam(value="name", required=true) String name,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
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
			}
		}
		return null;
	}
	
	@ApiOperation(value = "Search messages", notes = "Search by message type (required), event (required) and HL7 version (required). Send back a list of messages.")
    @ApiResponses({
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 400, message = "Bad request")
    })
	@RequestMapping(value = "/messages", method = RequestMethod.GET, produces = "application/json")
	public List<Message> getMessage(@RequestParam(value="messageType", required=true) String messageType,@RequestParam(value="event", required=true) String event,@RequestParam(value="hl7Version", required=true) String hl7Version) throws DataNotFoundException {
		if(messageType != null && !messageType.isEmpty() && event !=null && !event.isEmpty() && hl7Version != null && !hl7Version.isEmpty()){
				List<Message> messages = messageService.findAllByMessageTypeAndEventAndVersionAndScope(
						messageType, event, hl7Version, SCOPE.HL7STANDARD.name());
		    return messages;
		}
		return null;
	}
	
    @ApiOperation(value = "Search value sets", notes = "Search by scope (required, HL7STANDARD or PHINVADS), binding identifier (required) and HL7 version when scope is HL7STANDARD (not required). Send back a list of value sets.")
    @ApiResponses({
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 400, message = "Bad request")
    })
	@RequestMapping(value = "/valueSets", method = RequestMethod.GET, produces = "application/json")
	public List<Table> getValueSets(@RequestParam("scope") String scope, @RequestParam(value="bindingIdentifier", required=true) String bindingIdentifier,@RequestParam(value="hl7Version", required=false) String hl7Version) throws DataNotFoundException {
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
				}
			}
			return tableService.findByScope(scope);
		}
		return null;
	}
	
}
