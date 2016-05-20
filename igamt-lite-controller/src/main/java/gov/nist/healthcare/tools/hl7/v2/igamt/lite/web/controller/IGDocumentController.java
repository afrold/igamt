package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentConfiguration;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageMap;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SectionArrow;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentDeleteException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentListException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.PhinvadsWSCallService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.IGDocumentSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.IGDocumentChangeCommand;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.IntegrationIGDocumentRequestWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.OperationNotAllowException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/igdocuments")
public class IGDocumentController extends CommonController {

	Logger log = LoggerFactory.getLogger(IGDocumentController.class);

	@Autowired
	private IGDocumentService igDocumentService;

	@Autowired
	private IGDocumentExportService igDocumentExport;

	@Autowired
	private IGDocumentConfiguration igDocumentConfig;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private IGDocumentCreationService igDocumentCreation;

	@Autowired
	private DatatypeLibraryService datatypeLibraryService;

	@Autowired
	private SegmentLibraryService segmentLibraryService;

	@Autowired
	private TableLibraryService tableLibraryService;

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private TableService tableService;

	@Autowired
	private MessageService messageService;

	public IGDocumentService getIgDocumentService() {
		return igDocumentService;
	}

	public void setIGDocumentService(IGDocumentService igDocumentService) {
		this.igDocumentService = igDocumentService;
	}

	/**
	 * 
	 * @param type
	 * @return
	 * @throws UserAccountNotFoundException
	 * @throws IGDocumentException
	 */
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public List<IGDocument> getIGDocumentListByType(
			@RequestParam("type") String type)
			throws UserAccountNotFoundException, IGDocumentListException {
		try {
			if ("PRELOADED".equalsIgnoreCase(type)) {
				return preloaded();
			} else if ("USER".equalsIgnoreCase(type)) {
				return userIGDocuments();
			}
			throw new IGDocumentListException("Unknown IG document type");

		} catch (RuntimeException e) {
			throw new IGDocumentListException(e);
		} catch (Exception e) {
			throw new IGDocumentListException(e);
		}
	}

	/**
	 * Return the list of pre-loaded profiles
	 * 
	 * @return
	 */
	private List<IGDocument> preloaded() {
		log.info("Fetching all preloaded IGDocuments...");
		List<IGDocument> result = igDocumentService.findAllPreloaded();
		return result;
	}

	/**
	 * @return
	 * @throws UserAccountNotFoundException
	 */
	private List<IGDocument> userIGDocuments()
			throws UserAccountNotFoundException {
		log.info("Fetching all custom IGDocuments...");
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		if (account == null) {
			throw new UserAccountNotFoundException();
		}
		return igDocumentService.findByAccountId(account.getId());
	}

	@RequestMapping(value = "/{id}/clone", method = RequestMethod.POST)
	public IGDocument clone(@PathVariable("id") String id)
			throws IGDocumentNotFoundException, UserAccountNotFoundException,
			IGDocumentException {
		try {
			log.info("Clone IGDocument with id=" + id);

			HashMap<String, String> segmentIdChangeMap = new HashMap<String, String>();
			HashMap<String, String> datatypeIdChangeMap = new HashMap<String, String>();
			HashMap<String, String> tableIdChangeMap = new HashMap<String, String>();

			User u = userService.getCurrentUser();
			Account account = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (account == null)
				throw new UserAccountNotFoundException();
			IGDocument igDocument = this.findIGDocument(id);

			for (Message m : igDocument.getProfile().getMessages()
					.getChildren()) {
				m.setId(null);
				messageService.save(m);
			}

			DatatypeLibrary datatypeLibrary = igDocument.getProfile()
					.getDatatypeLibrary();
			SegmentLibrary segmentLibrary = igDocument.getProfile()
					.getSegmentLibrary();
			TableLibrary tableLibrary = igDocument.getProfile()
					.getTableLibrary();

			DatatypeLibrary clonedDatatypeLibrary = datatypeLibrary.clone();
			SegmentLibrary clonedSegmentLibrary = segmentLibrary.clone();
			TableLibrary clonedTableLibrary = tableLibrary.clone();

			clonedDatatypeLibrary.setChildren(new HashSet<DatatypeLink>());
			clonedSegmentLibrary.setChildren(new HashSet<SegmentLink>());
			clonedTableLibrary.setChildren(new HashSet<TableLink>());

			datatypeLibraryService.save(clonedDatatypeLibrary);
			segmentLibraryService.save(clonedSegmentLibrary);
			tableLibraryService.save(clonedTableLibrary);

			List<Datatype> datatypes = datatypeService
					.findByFullDTsLibIds(datatypeLibrary.getId());
			if (datatypes != null) {
				for (int i = 0; i < datatypes.size(); i++) {
					String oldDatatypeId = null;
					Datatype d = datatypes.get(i);
					DatatypeLink dl = datatypeLibrary.findOne(d.getId())
							.clone();
					if (d.getScope().equals(SCOPE.USER)) {
						oldDatatypeId = d.getId();
						d.setId(null);
						d.setLibId(new HashSet<String>());
					}

					d.getLibIds().add(clonedDatatypeLibrary.getId());
					datatypeService.save(d);
					dl.setId(d.getId());
					clonedDatatypeLibrary.addDatatype(dl);
					if (oldDatatypeId != null) {
						datatypeIdChangeMap.put(oldDatatypeId, d.getId());
					}
				}
			}

			List<Segment> segments = segmentService.findByLibIds(segmentLibrary
					.getId());
			if (segments != null) {
				for (int i = 0; i < segments.size(); i++) {
					String oldSegmentId = null;
					Segment s = segments.get(i);
					SegmentLink sl = segmentLibrary.findOne(s.getId());
					if (s.getScope().equals(SCOPE.USER)) {
						oldSegmentId = s.getId();
						s.setId(null);
						s.setLibId(new HashSet<String>());
					}
					s.getLibIds().add(clonedSegmentLibrary.getId());
					segmentService.save(s);
					sl.setId(s.getId());
					clonedSegmentLibrary.addSegment(sl);
					if (oldSegmentId != null) {
						segmentIdChangeMap.put(oldSegmentId, s.getId());
					}
				}

			}

			List<Table> tables = tableService
					.findByLibIds(tableLibrary.getId());
			if (tables != null) {
				for (int i = 0; i < tables.size(); i++) {
					String oldTableId = null;
					Table t = tables.get(i);
					TableLink tl = tableLibrary.findOneTableById(t.getId());
					if (t.getScope().equals(SCOPE.USER)) {
						oldTableId = t.getId();
						t.setId(null);
						t.setLibIds(new HashSet<String>());
					}
					t.getLibIds().add(clonedTableLibrary.getId());
					tableService.save(t);
					tl.setId(t.getId());
					clonedTableLibrary.addTable(tl);
					if (oldTableId != null) {
						tableIdChangeMap.put(oldTableId, t.getId());
					}
				}
			}

			datatypeLibraryService.save(clonedDatatypeLibrary);
			segmentLibraryService.save(clonedSegmentLibrary);
			tableLibraryService.save(clonedTableLibrary);

			igDocument.getProfile().setMetaData(igDocument.getProfile().getMetaData().clone());
			igDocument.getProfile().setDatatypeLibrary(clonedDatatypeLibrary);
			igDocument.getProfile().setSegmentLibrary(clonedSegmentLibrary);
			igDocument.getProfile().setTableLibrary(clonedTableLibrary);

			updateModifiedId(tableIdChangeMap, datatypeIdChangeMap,
					segmentIdChangeMap, igDocument.getProfile());

			igDocument.setId(null);
			igDocument.setScope(IGDocumentScope.USER);
			igDocument.setAccountId(account.getId());
			igDocument.getMetaData().setDate(Constant.mdy.format(new Date()));
			igDocumentService.save(igDocument);
			return igDocument;
		} catch (UserAccountNotFoundException e) {
			throw new IGDocumentException(e);
		} catch (CloneNotSupportedException e) {
			throw new IGDocumentException(e);
		}
	}

	private void updateModifiedId(HashMap<String, String> tableIdChangeMap,
			HashMap<String, String> datatypeIdChangeMap,
			HashMap<String, String> segmentIdChangeMap, Profile profile) {
		for (SegmentLink sl : profile.getSegmentLibrary().getChildren()) {
			Segment s = segmentService.findById(sl.getId());
			for (Field f : s.getFields()) {
				if (f.getDatatype() != null && f.getDatatype().getId() != null && datatypeIdChangeMap.containsKey(f.getDatatype().getId()))
					f.getDatatype().setId(datatypeIdChangeMap.get(f.getDatatype().getId()));
				if (f.getTable() != null && f.getTable().getId() != null && tableIdChangeMap.containsKey(f.getTable().getId()))
					f.getTable().setId(tableIdChangeMap.get(f.getTable().getId()));
			}
			
			for(Mapping map:s.getDynamicMapping().getMappings()){
				for(Case c : map.getCases()){
					if(c.getDatatype() != null && datatypeIdChangeMap.containsKey(c.getDatatype())) c.setDatatype(datatypeIdChangeMap.get(c.getDatatype()));
				}
			}
			segmentService.save(s);
		}

		for (DatatypeLink dl : profile.getDatatypeLibrary().getChildren()) {
			Datatype d = datatypeService.findById(dl.getId());
			for (Component c : d.getComponents()) {
				if (c.getDatatype() != null && c.getDatatype().getId() != null && datatypeIdChangeMap.containsKey(c.getDatatype().getId()))
					c.getDatatype().setId(datatypeIdChangeMap.get(c.getDatatype().getId()));
				if (c.getTable() != null && c.getTable().getId() != null && tableIdChangeMap.containsKey(c.getTable().getId()))
					c.getTable().setId(tableIdChangeMap.get(c.getTable().getId()));
			}
			datatypeService.save(d);
		}

		for (Message m : profile.getMessages().getChildren()) {
			for (SegmentRefOrGroup sog : m.getChildren()) {
				this.udateModifiedSegmentIdAndVisitChild(segmentIdChangeMap,
						sog);
			}
			messageService.save(m);
		}
	}

	private void udateModifiedSegmentIdAndVisitChild(
			HashMap<String, String> segmentIdChangeMap, SegmentRefOrGroup sog) {
		if (sog instanceof SegmentRef) {
			SegmentRef segmentRef = (SegmentRef) sog;
			if (segmentIdChangeMap.containsKey(segmentRef.getRef().getId()))
				segmentRef.getRef().setId(segmentIdChangeMap.get(segmentRef.getRef().getId()));
		}

		if (sog instanceof Group) {
			Group g = (Group) sog;

			for (SegmentRefOrGroup child : g.getChildren()) {
				this.udateModifiedSegmentIdAndVisitChild(segmentIdChangeMap,
						child);
			}
		}

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public IGDocument get(@PathVariable("id") String id)
			throws IGDocumentNotFoundException {
		try {
			log.info("Fetching profile with id=" + id);
			User u = userService.getCurrentUser();
			Account account = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (account == null)
				throw new UserAccountNotFoundException();
			IGDocument d = findIGDocument(id);
			return d;
		} catch (RuntimeException e) {
			throw new IGDocumentNotFoundException(e);
		} catch (Exception e) {
			throw new IGDocumentNotFoundException(e);
		}
	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
	public ResponseMessage delete(@PathVariable("id") String id)
			throws IGDocumentDeleteException {
		try {
			User u = userService.getCurrentUser();
			Account account = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (account == null)
				throw new UserAccountNotFoundException();
			log.info("Delete IGDocument with id=" + id);
			IGDocument d = findIGDocument(id);
			if (d.getAccountId() == account.getId()) {
				igDocumentService.delete(id);
				return new ResponseMessage(ResponseMessage.Type.success,
						"igDocumentDeletedSuccess", null);
			} else {
				throw new OperationNotAllowException("delete");
			}
		} catch (RuntimeException e) {
			throw new IGDocumentDeleteException(e);
		} catch (Exception e) {
			throw new IGDocumentDeleteException(e);
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public IGDocumentSaveResponse save(
			@RequestBody IGDocumentChangeCommand command)
			throws IGDocumentSaveException {
		try {
			User u = userService.getCurrentUser();
			Account account = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (account == null)
				throw new UserAccountNotFoundException();
			log.info("Applying changes to IGDocument="
					+ command.getIgDocument().getId() + " for account="
					+ command.getIgDocument().getAccountId());
			IGDocument saved = igDocumentService.apply(command.getIgDocument());
			return new IGDocumentSaveResponse(saved.getMetaData().getDate(),
					saved.getMetaData().getVersion());
		} catch (RuntimeException e) {
			throw new IGDocumentSaveException(e);
		} catch (Exception e) {
			throw new IGDocumentSaveException(e);
		}
	}

	@RequestMapping(value = "/{id}/export/xml", method = RequestMethod.POST, produces = "text/xml", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void export(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as xml file IGDcoument with id=" + id);
		IGDocument d = this.findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsXml(d);
		response.setContentType("text/xml");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".xml");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/html", method = RequestMethod.POST, produces = "text/html", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportHtml(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as html file IGDcoument with id=" + id);
		IGDocument d = this.findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsHtml(d);
		response.setContentType("text/html");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".html");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/zip", method = RequestMethod.POST, produces = "application/zip", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportZip(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as xml file profile with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsZip(d);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".zip");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/Validation/{mIds}", method = RequestMethod.POST, produces = "application/zip")
	public void exportValidationXMLByMessages(@PathVariable("id") String id,
			@PathVariable("mIds") String[] messageIds,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException,
			CloneNotSupportedException {
		log.info("Exporting as xml file profile with id=" + id
				+ " for selected messages=" + Arrays.toString(messageIds));
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsValidationForSelectedMessages(d,
				messageIds);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".zip");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/Gazelle/{mIds}", method = RequestMethod.POST, produces = "application/zip", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportGazelleXMLByMessages(@PathVariable("id") String id,
			@PathVariable("mIds") String[] messageIds,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException,
			CloneNotSupportedException {
		log.info("Exporting as xml file profile with id=" + id
				+ " for selected messages=" + messageIds);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsGazelleForSelectedMessages(d,
				messageIds);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".zip");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/Display/{mIds}", method = RequestMethod.POST, produces = "application/zip", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportDisplayXMLByMessages(@PathVariable("id") String id,
			@PathVariable("mIds") String[] messageIds,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException,
			CloneNotSupportedException {
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsDisplayForSelectedMessage(d,
				messageIds);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".zip");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/pdf", method = RequestMethod.POST, produces = "application/pdf", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportPdfFromXsl(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as pdf file profile with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsPdf(d);
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".pdf");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/docx", method = RequestMethod.POST, produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportDocx(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as docx file profile with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsDocx(d);
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".docx");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/delta/pdf", method = RequestMethod.POST, produces = "application/pdf", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void deltaPdf(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting delta as pdf file IGDocument with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;

		// TODO need to implement igDocumentService.diffToPdf
		content = igDocumentService.diffToPdf(d);
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-Delta-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".pdf");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	private IGDocument findIGDocument(String documentId)
			throws IGDocumentNotFoundException {
		IGDocument d = igDocumentService.findOne(documentId);
		if (d == null) {
			throw new IGDocumentNotFoundException(documentId);
		}
		return d;
	}

	@RequestMapping(value = "/{id}/export/xslx", method = RequestMethod.POST, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	public void exportXlsx(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as spreadsheet profile with id=" + id);
		InputStream content = null;
		IGDocument d = findIGDocument(id);
		content = igDocumentExport.exportAsXlsx(d);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-disposition", "attachment;filename="
				+ escapeSpace(d.getMetaData().getTitle()) + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".xlsx");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public IGDocumentConfiguration config() {
		return this.igDocumentConfig;
	}

	@RequestMapping(value = "/{id}/verify/segment/{sId}", method = RequestMethod.POST, produces = "application/json")
	public ElementVerification verifySegment(@PathVariable("id") String id,
			@PathVariable("sId") String sId, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			IGDocumentNotFoundException {
		log.info("Verifying segment " + sId + " from profile " + id);
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		return igDocumentService.verifySegment(d, sId, "segment");
	}

	@RequestMapping(value = "/{id}/verify/datatype/{dtId}", method = RequestMethod.POST, produces = "application/json")
	public ElementVerification verifyDatatype(@PathVariable("id") String id,
			@PathVariable("dtId") String dtId, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			IGDocumentNotFoundException {
		log.info("Verifying datatype " + dtId + " from profile " + id);
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		return igDocumentService.verifyDatatype(d, dtId, "datatype");
	}

	@RequestMapping(value = "/{id}/verify/valueset/{vsId}", method = RequestMethod.POST, produces = "application/json")
	public ElementVerification verifyValueSet(@PathVariable("id") String id,
			@PathVariable("vsId") String vsId, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			ProfileNotFoundException {
		log.info("Verifying segment " + vsId + " from profile " + id);
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new ProfileNotFoundException(id);
		}
		return igDocumentService.verifyValueSet(d, vsId, "valueset");
	}

	@RequestMapping(value = "/findVersions", method = RequestMethod.GET, produces = "application/json")
	public List<String> findHl7Versions() {
		log.info("Fetching all HL7 versions.");
		List<String> result = igDocumentCreation.findHl7Versions();
		return result;
	}

	@RequestMapping(value = "/{hl7Version}/tables", method = RequestMethod.GET, produces = "application/json")
	public Set<Table> findHl7Tables(
			@PathVariable("hl7Version") String hl7Version) {
		log.info("Fetching all Tables for " + hl7Version);

		Set<Table> result = new HashSet<Table>();

		List<IGDocument> igDocuments = igDocumentCreation
				.findIGDocumentsByHl7Versions();
		for (IGDocument igd : igDocuments) {
			if (igd.getProfile().getMetaData().getHl7Version()
					.equals(hl7Version)) {
				for (TableLink link : igd.getProfile().getTableLibrary()
						.getChildren()) {
					Table t = tableService.findById(link.getId());
					t.setBindingIdentifier(link.getBindingIdentifier());
					result.add(t);
				}
			}
		}
		return result;
	}

	@RequestMapping(value = "/{searchText}/PHINVADS/tables", method = RequestMethod.GET, produces = "application/json")
	public Set<Table> findPHINVADSTables(
			@PathVariable("searchText") String searchText)
			throws MalformedURLException {
		log.info("Fetching all Tables for " + searchText);
		return new PhinvadsWSCallService().generateTableList(searchText);
	}

	// TODO Change to query as is but with $nin a list of messages that can be
	// empty.
	// @RequestMapping(value = "/hl7/messageListByVersion/{hl7Version:.*}",
	// method = RequestMethod.POST, produces = "application/json")
	// public List<String[]> getMessageListByVersion(@PathVariable("hl7Version")
	// String hl7Version, MessageByListCommand command) {
	@RequestMapping(value = "/messageListByVersion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public List<MessageEvents> getMessageListByVersion(
			@RequestBody String hl7Version) throws IGDocumentNotFoundException {
		log.info("Fetching messages of version hl7Version=" + hl7Version);
		List<MessageEvents> messages = igDocumentCreation.summary(hl7Version);
		if (messages.isEmpty()) {
			throw new IGDocumentNotFoundException(hl7Version);
		}
		return messages;
	}

	@RequestMapping(value = "/createIntegrationProfile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public IGDocument createIG(
			@RequestBody IntegrationIGDocumentRequestWrapper idrw)
			throws IGDocumentException {
		log.info("Creation of IGDocument.");
		log.debug("idrw.getMsgEvts()=" + idrw.getMsgEvts());
		log.debug("idrw.getAccountId()=" + idrw.getAccountId());
		User u = userService.getCurrentUser();
		Account account = accountRepository.findByTheAccountsUsername(u
				.getUsername());
		IGDocument igDocument = igDocumentCreation.createIntegratedIGDocument(
				idrw.getMsgEvts(), idrw.getHl7Version(), account.getId());

		assert (igDocument.getId() != null);
		assert (igDocument.getAccountId() != null);
		return igDocument;
	}

	@RequestMapping(value = "/updateIntegrationProfile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public IGDocument updateIG(
			@RequestBody IntegrationIGDocumentRequestWrapper idrw)
			throws IGDocumentException {
		log.info("Update profile with additional messages.");
		log.debug("getMsgEvts()" + idrw.getMsgEvts());
		log.debug("getIgdocument()" + idrw.getIgdocument());
		IGDocument igDocument = igDocumentCreation.updateIntegratedIGDocument(
				idrw.getMsgEvts(), idrw.getIgdocument());
		return igDocument;
	}

	@RequestMapping(value = "/{id}/deleteMessage", method = RequestMethod.POST, produces = "application/json")
	public boolean deleteMessage(@PathVariable("id") String id,
			@RequestParam("messageId") String messageId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException,
			IGDocumentException {
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		Set<Message> messages = d.getProfile().getMessages().getChildren();
		Message found = null;
		for (Message message : messages) {
			if (message.getId().equals(messageId)) {
				found = message;
				break;
			}
		}
		if (found != null) {
			messages.remove(found);
			igDocumentService.save(d);
		}
		return true;
	}

	@RequestMapping(value = "/{id}/metadata/save", method = RequestMethod.POST)
	public boolean saveIgDocMetadata(@PathVariable("id") String id,
			@RequestBody DocumentMetaData metaData, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		d.setMetaData(metaData);
		igDocumentService.save(d);
		return true;
	}

	@RequestMapping(value = "/{id}/profile/metadata/save", method = RequestMethod.POST)
	public boolean saveProfileMetadata(@PathVariable("id") String id,
			@RequestBody ProfileMetaData metaData, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		d.getProfile().setMetaData(metaData);
		igDocumentService.save(d);
		return true;
	}

	@RequestMapping(value = "/{id}/section/save", method = RequestMethod.POST)
	public boolean saveSection(@PathVariable("id") String id,
			@RequestBody Section section, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		
		Set<Section> newChildSection = d.getChildSections();
		newChildSection.add(section);
		d.setChildSections(newChildSection);
		igDocumentService.save(d);
		return true;
	}
	
	@RequestMapping(value = "/{id}/section/update", method = RequestMethod.POST)
	public boolean updateSection(@PathVariable("id") String id,
			@RequestBody Section section, HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		String idSect= section.getId();
        Section s = findSection(d, idSect);
		if (s == null)
		throw new IGDocumentException("Unknown Section");
		
		s.merge(section);
		igDocumentService.save(d);
		return true;
	}
	
	

	@RequestMapping(value = "/{id}/section/{sectionId}/delete", method = RequestMethod.POST)
	public boolean updateSection(@PathVariable("id") String id,
			@PathVariable("sectionId") String sectionId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException,
			IGDocumentException {
		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		Section sect = findSection(d, sectionId);
		if (sect == null)
			throw new IGDocumentException("Unknown Section");
		if (d.getChildSections().contains(sect)) {
			d.getChildSections().remove(sect);
		} else {
			Section parent = findSectionParent(d, sectionId);
			if (parent == null)
				throw new IGDocumentException("Unknown Section");
			parent.getChildSections().remove(sect);
		}
		igDocumentService.save(d);
		return true;
	}

	private Section findSectionParent(IGDocument document, String sectionId) {
		for (Section section : document.getChildSections()) {
			Section s = findSectionParent(section, sectionId);
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	private Section findSection(IGDocument document, String sectionId) {
		for (Section section : document.getChildSections()) {
			Section s = findSection(section, sectionId);
			if (s != null) {
				return s;
			}
		}
		return null;
	}

	private Section findSection(Section parent, String sectionId) {
		if (parent.getId().equals(sectionId))
			return parent;
		if (parent.getChildSections() != null)
			for (Section child : parent.getChildSections()) {
			
					Section section = (Section) child;
					Section f = findSection(section, sectionId);
					if (f != null) {
						return f;
					}
				}
			
		return null;
	}

	private Section findSectionParent(Section parent, String sectionId) {
		if (parent.getChildSections() != null)
			for (Object child : parent.getChildSections()) {
				if (child instanceof Section) {
					Section section = (Section) child;
					if (section.getId().equals(sectionId)) {
						return parent;
					}
					Section f = findSectionParent(section, sectionId);
					if (f != null) {
						return section;
					}
				}
			}
		return null;
	}

	private String escapeSpace(String str) {
		return str.replaceAll(" ", "-");
	}

	@RequestMapping(value = "{id}/dropped", method = RequestMethod.POST)
	public String updateAfterDrop(@PathVariable("id") String id,
			@RequestBody SectionArrow arrow) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		System.out.println(id);

		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		Section s = findSection(d, arrow.getSource().getId());
		if (s == null)
			throw new IGDocumentException("Unknown Section");

		Section target = findSection(d, arrow.getDest().getId());
		if (target == null)
			throw new IGDocumentException("Unknown Section dest");

		System.out.println(s);
		s.merge(arrow.getSource());
		target.merge(arrow.getDest());

		igDocumentService.save(d);

		return null;
	}

	@RequestMapping(value = "/{id}/updateChildSections", method = RequestMethod.POST)
	public String updateChildSections(@PathVariable("id") String id,
			@RequestBody Set<Section> childSections) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		System.out.println(id);

		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}

		d.setChildSections(childSections);
		igDocumentService.save(d);

		return null;
	}
	
	@RequestMapping(value = "/{id}/reorderMessages", method = RequestMethod.POST)
	public String reorderMessages(@PathVariable("id") String id,
			@RequestBody Set<MessageMap> messagesMap) throws IOException,
			IGDocumentNotFoundException, IGDocumentException {
		System.out.println(id);

		IGDocument d = igDocumentService.findOne(id);
		if (d == null) {
			throw new IGDocumentNotFoundException(id);
		}
		for (Message message: d.getProfile().getMessages().getChildren() ){
			for(MessageMap map : messagesMap){
				if(message.getId().equals(map.getId())){
					message.setPosition(map.getPosition());
				}
			}
			
		}
		messageService.save(d.getProfile().getMessages().getChildren());

		return null;
	}

}
	
	
	
	

