package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import javax.servlet.http.HttpServletRequest;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.*;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.ExportException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.serialization.SerializationLayout;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.io.InputStream;

@RestController @RequestMapping("/export") public class ExportController extends CommonController {

    Logger log = LoggerFactory.getLogger(ExportController.class);

    @Autowired private DatatypeService datatypeService;

    @Autowired private SegmentService segmentService;

    @Autowired private MessageService messageService;

    @Autowired private TableService tableService;

    @Autowired private IGDocumentService igDocumentService;

    @Autowired private ProfileComponentService profileComponentService;

    @Autowired private ExportFontConfigService exportFontConfigService;

    @Autowired private ExportService exportService;

    @ApiOperation(value = "Export a data type as JSON", notes = "Search a data type by ID and export it in JSON.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/datatype/{id}/json", method = RequestMethod.GET, produces = "application/json")
    public Datatype getDatatypeAsJson(@PathVariable(value = "id") String id)
        throws DataNotFoundException {
        Datatype datatype = datatypeService.findById(id);
        return datatype;
    }



    @ApiOperation(value = "Export a data type as HTML", notes = "Search a data type by ID and export it in HTML.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/datatype/{id}/html", method = RequestMethod.GET, produces = "text/html")
    public String getDatatypeAsHtml(@PathVariable(value = "id") String id,
        HttpServletRequest request) throws DataNotFoundException,SerializationException {
        Datatype datatype = datatypeService.findById(id);
        if (datatype != null) {
            String result = "";
            result = exportService
                .exportDataModelAsHtml(datatype, datatype.getName(), generateHost(request));
            return result;
        }
        return null;
    }

    @ApiOperation(value = "Export a data type as HTML", notes = "Search a data type by name (required) and HL7 version (required) and export it in HTML.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")
    })
    @RequestMapping(value = "/datatype", method = RequestMethod.GET, produces = "application/json")
    public ExportableDataModel getDatatypeWithParams(@RequestParam(value="name", required=true) String name,@RequestParam(value="hl7Version", required=true) String hl7Version, HttpServletRequest request)
        throws DataNotFoundException, SerializationException {
        if ((name != null && !name.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())) {
            if (name != null && !name.isEmpty()) {
                if (hl7Version != null && !hl7Version.isEmpty()) {
                    Datatype datatype = datatypeService
                        .findOneByNameAndVersionAndScope(name, hl7Version,
                            Constant.SCOPE.HL7STANDARD.name());
                    if(datatype != null){
	                    String html = exportService
	                        .exportDataModelAsHtml(datatype, datatype.getName(), generateHost(request));
	                    ExportableDataModel exportableDataModel = new ExportableDataModel(html,datatype);
	                    return exportableDataModel;
                    }
                }
            }
        }
        return null;
    }

    @ApiOperation(value = "Export a value set as JSON", notes = "Search a value set by ID and export it in JSON.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/valueSet/{id}/json", method = RequestMethod.GET, produces = "application/json")
    public Table getValueSetAsJson(@PathVariable(value = "id") String id)
        throws DataNotFoundException {
        Table table = tableService.findById(id);
        return table;
    }

    @ApiOperation(value = "Export a value set as HTML", notes = "Search a value set by ID and export it in HTML.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/valueSet/{id}/html", method = RequestMethod.GET, produces = "text/html")
    public String getValueSetAsHtml(@PathVariable(value = "id") String id,
        HttpServletRequest request) throws DataNotFoundException, SerializationException {
        Table table = tableService.findById(id);
        if (table != null) {
            return exportService
                .exportDataModelAsHtml(table, table.getName(), generateHost(request));
        }
        return null;
    }

    @ApiOperation(value = "Export a value set as HTML", notes = "Search a value set by scope (required, HL7STANDARD or PHINVADS), binding identifier (required) and HL7 version (required when scope is HL7STANDARD) and export it in HTML.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")
    })
    @RequestMapping(value = "/valueSet", method = RequestMethod.GET, produces = "application/json")
    public ExportableDataModel getValueSetWithParams(@RequestParam("scope") String scope, @RequestParam(value="bindingIdentifier", required=true) String bindingIdentifier,@RequestParam(value="hl7Version", required=false) String hl7Version, HttpServletRequest request)
        throws DataNotFoundException, SerializationException {
        if(scope.equals(Constant.SCOPE.HL7STANDARD.name()) || scope.equals(Constant.SCOPE.PHINVADS.name())){
            if((bindingIdentifier != null && !bindingIdentifier.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())){
                if(bindingIdentifier != null && !bindingIdentifier.isEmpty()){
                	Table table = null;
                	if(scope.equals(Constant.SCOPE.HL7STANDARD.name())){
	                	if(hl7Version != null && !hl7Version.isEmpty()){
	                        table = tableService.findByScopeAndVersionAndBindingIdentifier(Constant.SCOPE.HL7STANDARD, hl7Version, bindingIdentifier);
	                    }
                    } else if (scope.equals(Constant.SCOPE.PHINVADS.name())){
                    	table = tableService.findOneByScopeAndBindingIdentifier(scope, bindingIdentifier);
                    }
                	if(table != null){
                        String html = exportService
                            .exportDataModelAsHtml(table, table.getName(), generateHost(request));
                        return new ExportableDataModel(html,table);
                    }
                }
            }
        }
        return null;
    }

    @ApiOperation(value = "Export a segment as JSON", notes = "Search a segment by ID and export it in JSON.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/segment/{id}/json", method = RequestMethod.GET, produces = "application/json")
    public Segment getSegmentAsJson(@PathVariable(value = "id") String id)
        throws DataNotFoundException {
        Segment segment = segmentService.findById(id);
        return segment;
    }

    @ApiOperation(value = "Export a segment as HTML", notes = "Search a segment by ID and export it in HTML.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/segment/{id}/html", method = RequestMethod.GET, produces = "text/html")
    public String getSegmentAsHtml(@PathVariable(value = "id") String id,
        HttpServletRequest request) throws DataNotFoundException, SerializationException {
        Segment segment = segmentService.findById(id);
        if (segment != null) {
            return exportService
                .exportDataModelAsHtml(segment, segment.getName(), generateHost(request));
        }
        return null;
    }

    @ApiOperation(value = "Export a segment as HTML", notes = "Search by name (required) and HL7 version (not required). Send back a list of segments.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")
    })
    @RequestMapping(value = "/segment", method = RequestMethod.GET, produces = "application/json")
    public ExportableDataModel getSegmentWithParams(@RequestParam(value="name", required=true) String name,@RequestParam(value="hl7Version", required=true) String hl7Version, HttpServletRequest request)
        throws DataNotFoundException, SerializationException {
        if((name != null && !name.isEmpty()) || (hl7Version != null && !hl7Version.isEmpty())){
            if(name != null && !name.isEmpty()){
                if(hl7Version != null && !hl7Version.isEmpty()){
                    Segment segment = segmentService.findByNameAndVersionAndScope(name, hl7Version, Constant.SCOPE.HL7STANDARD.name());
                    if(segment != null){
	                    String html = exportService
	                        .exportDataModelAsHtml(segment, segment.getName(), generateHost(request));
	                    return new ExportableDataModel(html,segment);
                    }
                }
            }
        }
        return null;
    }

    @ApiOperation(value = "Export a message as JSON", notes = "Search a message by ID and export it in JSON.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/message/{id}/json", method = RequestMethod.GET, produces = "application/json")
    public Message getMessageAsJson(@PathVariable(value = "id") String id)
        throws DataNotFoundException {
        Message message = messageService.findById(id);
        return message;
    }

    @ApiOperation(value = "Export a message as HTML", notes = "Search a message by ID and export it in HTML.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/message/{id}/html", method = RequestMethod.GET, produces = "text/html")
    public String getMessageAsHtml(@PathVariable(value = "id") String id,
        HttpServletRequest request) throws DataNotFoundException, SerializationException {
        Message message = messageService.findById(id);
        if (message != null) {
            return exportService
                .exportDataModelAsHtml(message, message.getName(), generateHost(request));
        }
        return null;
    }

    @ApiOperation(value = "Export a message as HTML", notes = "Search a message by message type (required), event (required) and HL7 version (required) and export it in HTML.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")
    })
    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = "application/json")
    public ExportableDataModel getMessageWithParams(@RequestParam(value="messageType", required=true) String messageType,@RequestParam(value="event", required=false) String event,@RequestParam(value="hl7Version", required=true) String hl7Version, HttpServletRequest request)
        throws DataNotFoundException, SerializationException {
        if(messageType != null && !messageType.isEmpty() && hl7Version != null && !hl7Version.isEmpty()){
          
          if(messageType.equals("ACK") || (event !=null && !event.isEmpty())){
            if(messageType.equals("ACK")){
              event="";
            }
            Message message = messageService.findByMessageTypeAndEventAndVersionAndScope(
                messageType, event, hl7Version, Constant.SCOPE.HL7STANDARD.name());
            if(message != null){
	            String html = exportService
	                .exportDataModelAsHtml(message, message.getName(), generateHost(request));
	            return new ExportableDataModel(html,message);
            }
          }
          
        }
        return null;
    }

    @ApiOperation(value = "Export an IG Document as JSON", notes = "Search an IG Document by ID and export it in JSON.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/igDocument/{id}/json", method = RequestMethod.GET, produces = "application/json")
    public IGDocument getIgDocumentAsJson(@PathVariable(value = "id") String id)
        throws DataNotFoundException {
        IGDocument igDocument = igDocumentService.findById(id);
        return igDocument;
    }

    @ApiOperation(value = "Export an IG Document as HTML", notes = "Search an IG Document by ID and export it in HTML.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/igDocument/{id}/html", method = RequestMethod.GET, produces = "text/html")
    public String getIgDocumentAsHtml(@PathVariable(value = "id") String id)
        throws ExportException, SerializationException {
        InputStream resultInputStream = null;
        String result = "";
        IGDocument igDocument = igDocumentService.findById(id);
        if (igDocument != null) {
            ExportFontConfig exportFontConfig;
            try {
                exportFontConfig = exportFontConfigService.getDefaultExportFontConfig();
            } catch (Exception e){
                throw new ExportException("Unable to load font export configuration",e);
            }
            resultInputStream = exportService
                .exportIGDocumentAsHtml(igDocument, SerializationLayout.IGDOCUMENT,
                    ExportConfig.getBasicExportConfig(true),exportFontConfig
                    );
            if(resultInputStream != null){
                try {
                    result = IOUtils.toString(resultInputStream);
                } catch (IOException e) {

                }
            }
        }
        return result;
    }

    @ApiOperation(value = "Export a profile component as JSON", notes = "Search a profile component by ID and export it in JSON.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/profileComponent/{id}/json", method = RequestMethod.GET, produces = "application/json")
    public ProfileComponent getProfileComponentAsJson(@PathVariable(value = "id") String id)
        throws DataNotFoundException {
        ProfileComponent profileComponent = profileComponentService.findById(id);
        return profileComponent;
    }

    @ApiOperation(value = "Export a profile component as HTML", notes = "Search a profile component by ID and export it in HTML.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad request")})
    @RequestMapping(value = "/profileComponent/{id}/html", method = RequestMethod.GET, produces = "text/html")
    public String getProfileComponentAsHtml(@PathVariable(value = "id") String id,
        HttpServletRequest request) throws DataNotFoundException, SerializationException {
        ProfileComponent profileComponent = profileComponentService.findById(id);
        if (profileComponent != null) {
            return exportService.exportDataModelAsHtml(profileComponent, profileComponent.getName(),
                generateHost(request));
        }
        return null;
    }

    private String generateHost(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        String servletPath = request.getServletPath();
        String host = requestUrl.substring(0, requestUrl.indexOf(servletPath));
        return host;
    }
}
