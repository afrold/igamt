package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentConfiguration;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageByListCommand;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentDeleteException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentListException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.IGDocumentSaveResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.IGDocumentChangeCommand;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.IntegrationIGDocumentRequestWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.OperationNotAllowException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
			User u = userService.getCurrentUser();
			Account account = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (account == null)
				throw new UserAccountNotFoundException();
			IGDocument d = this.findIGDocument(id);
			d.setId(null);
			d.setScope(IGDocumentScope.USER);
			d.setAccountId(account.getId());
			// d.setBaseId(d.getBaseId() != null ? d.getBaseId() : id);
			// d.setSourceId(id);
			d.getMetaData().setDate(DateUtils.getCurrentTime());
			igDocumentService.save(d);
			return d;
		} catch (UserAccountNotFoundException e) {
			throw new IGDocumentException(e);
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

	@RequestMapping(value = "/{id}/export/xml", method = RequestMethod.POST, produces = "text/xml")
	public void export(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as xml file IGDcoument with id=" + id);
		IGDocument d = this.findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsXml(d);
		response.setContentType("text/xml");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".xml");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/html", method = RequestMethod.POST, produces = "text/html")
	public void exportHtml(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as html file IGDcoument with id=" + id);
		IGDocument d = this.findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsHtml(d);
		response.setContentType("text/html");
		response.setHeader("Content-disposition", "attachment;filename=" +  d.getMetaData().getTitle() + "-" +  new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".html");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/zip", method = RequestMethod.POST, produces = "application/zip")
	public void exportZip(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as xml file profile with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsZip(d);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".zip");
		FileCopyUtils.copy(content, response.getOutputStream());
	}
	
	@RequestMapping(value = "/{id}/export/zip/{mIds}", method = RequestMethod.POST, produces = "application/zip")
	public void exportZipByMessage(@PathVariable("id") String id,
			@PathVariable("mIds") String[] messageIds,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException, CloneNotSupportedException {
		log.info("Exporting as xml file profile with id=" + id + " for selected messages=" + messageIds);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsZipForSelectedMessages(d, messageIds);
		response.setContentType("application/zip");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".zip");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/pdf", method = RequestMethod.POST, produces = "application/pdf")
	public void exportPdfFromXsl(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as pdf file profile with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsPdf(d);
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".pdf");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/export/docx", method = RequestMethod.POST, produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	public void exportDocx(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as docx file profile with id=" + id);
		IGDocument d = findIGDocument(id);
		InputStream content = null;
		content = igDocumentExport.exportAsDocx(d);
		response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-"
				+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".docx");
		FileCopyUtils.copy(content, response.getOutputStream());
	}

	@RequestMapping(value = "/{id}/delta/pdf", method = RequestMethod.POST, produces = "application/pdf")
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

	@RequestMapping(value = "/{id}/export/xslx", method = RequestMethod.POST, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public void exportXlsx(@PathVariable("id") String id,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, IGDocumentNotFoundException {
		log.info("Exporting as spreadsheet profile with id=" + id);
		InputStream content = null;
		IGDocument d = findIGDocument(id);
		content = igDocumentExport.exportAsXlsx(d);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-disposition", "attachment;filename="
				+ d.getMetaData().getTitle() + "-"
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
		log.info("Fetching all HL7 versions");
		List<String> result = igDocumentCreation.findHl7Versions();
		return result;
	}

	// TODO Change to query as is but with $nin a list of messages that can be
	// empty.
	// @RequestMapping(value = "/hl7/messageListByVersion/{hl7Version:.*}",
	// method = RequestMethod.POST, produces = "application/json")
	// public List<String[]> getMessageListByVersion(@PathVariable("hl7Version")
	// String hl7Version, MessageByListCommand command) {
	@RequestMapping(value = "/messageListByVersion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public List<String[]> getMessageListByVersion(
			@RequestBody MessageByListCommand command) {
		log.info("Fetching messages of version hl7Version="
				+ command.getHl7Version() + " command="
				+ command.getMessageIds() + " size="
				+ command.getMessageIds().size());
		List<String[]> messages = igDocumentCreation.summary(
				command.getHl7Version(), command.getMessageIds());
		log.debug("messages=" + messages.size());
		return messages;
	}

	@RequestMapping(value = "/createIntegrationProfile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public IGDocument createIG(
			@RequestBody IntegrationIGDocumentRequestWrapper idrw)
			throws IGDocumentException {
		log.info("Creation of profile.");
		IGDocument igDocument = igDocumentCreation.createIntegratedIGDocument(
				idrw.getMsgIds(), idrw.getHl7Version(), idrw.getAccountId());
		igDocumentService.save(igDocument);
		assert (igDocument.getId() != null);
		assert (igDocument.getAccountId() != null);
		return igDocument;
	}

	@RequestMapping(value = "/updateIntegrationProfile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public IGDocument updateIG(
			@RequestBody IntegrationIGDocumentRequestWrapper idrw)
			throws IGDocumentException {
		log.info("Update profile with additional messages.");
		IGDocument igDocument = igDocumentCreation.updateIntegratedIGDocument(
				idrw.getMsgIds(), idrw.getIgdocument());
		igDocumentService.save(igDocument);
		return igDocument;
	}
}
