package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.FileStorageUtil;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.UploadFileResponse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UploadImageFileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.HttpUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@RestController
@RequestMapping(FileStorageUtil.root)
public class FileStorageController {

	@Autowired
	FileStorageService storageService;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(value = FileStorageUtil.UPLOAD_URL, method = RequestMethod.POST, consumes = { "multipart/form-data" }, produces = "application/json")
	public UploadFileResponse upload(@RequestPart("file") MultipartFile part,
			HttpServletRequest request) throws UploadImageFileException {
		try {
			User u = userService.getCurrentUser();
			Account account = accountRepository.findByTheAccountsUsername(u
					.getUsername());
			if (account == null)
				throw new UserAccountNotFoundException();
			String mime = part.getContentType();
			String filename = part.getOriginalFilename();
			String extension = FilenameUtils.getExtension(filename);
			if (((mime.equals("text/plain")
					|| (mime.equals("application/msword"))
					|| (mime.equals("text/xml"))
					|| (mime.equals("application/x-pdf")) || (mime
						.equals("application/pdf")))
					|| (mime.equals("image/jpeg"))
					|| (mime.equals("image/gif")) || (mime.equals("image/png")))
					&& FileStorageUtil.allowedExtensions.contains(extension
							.toLowerCase())) {

				if (part.getSize() >= 1024 * 1024 * 10) {
					throw new UploadImageFileException("fileSizeTooBig");
				}
				InputStream in = part.getInputStream();
				DBObject metaData = new BasicDBObject();
				metaData.put("accountId", account.getId());
				String generatedName = UUID.randomUUID().toString() + "."
						+ extension;
				GridFSFile fsFile = storageService.store(in, generatedName,
						part.getContentType(), metaData);
				GridFSDBFile dbFile = storageService.findOne(fsFile.getId()
						.toString());
				return new UploadFileResponse(
						HttpUtil.getImagesRootUrl(request) + "/file?name="
								+ dbFile.getFilename());
			}
			throw new UploadImageFileException("fileTypeUnsupported");

		} catch (RuntimeException e) {
			throw new UploadImageFileException(e);
		} catch (Exception e) {
			throw new UploadImageFileException(e);
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/file", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> getByName(
			@RequestParam("name") String filename)
			throws UploadImageFileException {
		try {
			GridFSDBFile dbFile = storageService.findOneByFilename(filename);
			if (dbFile != null) {
				return ResponseEntity
						.ok()
						.contentLength(dbFile.getLength())
						.contentType(
								MediaType.parseMediaType(dbFile
										.getContentType()))
						.body(new InputStreamResource(dbFile.getInputStream()));
			}
		} catch (RuntimeException e) {
			throw new UploadImageFileException(e);
		} catch (Exception e) {
			throw new UploadImageFileException(e);
		}
		return null;
	}

}
