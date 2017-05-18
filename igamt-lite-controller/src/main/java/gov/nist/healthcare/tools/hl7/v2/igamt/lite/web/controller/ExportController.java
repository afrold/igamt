package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
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

@RestController
@RequestMapping("/export")
public class ExportController extends CommonController{
	
	Logger log = LoggerFactory.getLogger(ExportController.class);
	
	@Autowired
	private DatatypeService datatypeService;
	
	@Autowired
	private SegmentService segmentService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private TableService tableService;
	
	@Autowired
	private IGDocumentService igDocumentService;
	
	@Autowired
	private ProfileComponentService profileComponentService;

	@Autowired
	private ExportFontConfigService exportFontConfigService;
	
	@Autowired
	private ExportService exportService;
	
	@RequestMapping(value = "/datatype/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public Datatype getDatatypeAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		Datatype datatype = datatypeService.findById(id);
		return datatype;
	}
	
	@RequestMapping(value = "/datatype/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getDatatypeAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		Datatype datatype = datatypeService.findById(id);
		if(datatype!=null && datatype.getScope().equals(SCOPE.HL7STANDARD)){
			return exportService.exportDataModelAsHtml(datatype,datatype.getName());
		}
		return null;
	}
	
	@RequestMapping(value = "/valueSet/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public Table getValueSetAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		Table table = tableService.findById(id);
		return table;
	}
	
	@RequestMapping(value = "/valueSet/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getValueSetAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		Table table = tableService.findById(id);
		if(table!=null && (table.getScope().equals(SCOPE.HL7STANDARD) || table.getScope().equals(SCOPE.PHINVADS))){
			return exportService.exportDataModelAsHtml(table,table.getName());
		}
		return null;
	}
	
	@RequestMapping(value = "/segment/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public Segment getSegmentAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		Segment segment = segmentService.findById(id);
		return segment;
	}
	
	@RequestMapping(value = "/segment/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getSegmentAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		Segment segment = segmentService.findById(id);
		if(segment!=null && segment.getScope().equals(SCOPE.HL7STANDARD)){
			return exportService.exportDataModelAsHtml(segment,segment.getName());
		}
		return null;
	}
	
	@RequestMapping(value = "/message/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public Message getMessageAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		Message message = messageService.findById(id);
		return message;
	}
	
	@RequestMapping(value = "/message/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getMessageAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		Message message = messageService.findById(id);
		return exportService.exportDataModelAsHtml(message,message.getName());
	}
	
	@RequestMapping(value = "/igDocument/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public IGDocument getIgDocumentAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		IGDocument igDocument = igDocumentService.findById(id);
		if(igDocument!=null){
			return igDocument;
		}
		return null;
	}
	
	@RequestMapping(value = "/igDocument/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getIgDocumentAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		IGDocument igDocument = igDocumentService.findById(id);
		if(igDocument!=null){
			try {
				return IOUtils.toString(exportService.exportIGDocumentAsHtml(igDocument, SerializationLayout.IGDOCUMENT, ExportConfig.getBasicExportConfig("table"), exportFontConfigService.getDefaultExportFontConfig()));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/profileComponent/{id}/json", method = RequestMethod.GET, produces = "application/json")
	public ProfileComponent getProfileComponentAsJson(@PathVariable(value="id") String id) throws DataNotFoundException {
		ProfileComponent profileComponent = profileComponentService.findById(id);
		return profileComponent;
	}
	
	@RequestMapping(value = "/profileComponent/{id}/html", method = RequestMethod.GET, produces = "text/html")
	public String getProfileComponentAsHtml(@PathVariable(value="id") String id) throws DataNotFoundException {
		ProfileComponent profileComponent = profileComponentService.findById(id);
		if(profileComponent!=null){
			return exportService.exportDataModelAsHtml(profileComponent,profileComponent.getName());
		}
		return null;
	}
}
