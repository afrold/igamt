/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

/**
 * @author gcr1
 *
 */
@Service
public class DataTypeLibraryDocumentServiceImpl implements DatatypeLibraryDocumentService {

	Logger log = LoggerFactory.getLogger(DataTypeLibraryServiceImpl.class);

	@Autowired
	private DatatypeLibraryDocumentRepository datatypeLibraryDocumentRepository;

	@Autowired
	private DatatypeLibraryService datatypeLibraryService;

	@Autowired
	private DatatypeRepository datatypeRepository;
	@Autowired
	private TableLibraryService tableLibraryService;

	private Random rand = new Random();

	@Override
	public List<DatatypeLibraryDocument> findAll() {
		List<DatatypeLibraryDocument> datatypeLibrary = datatypeLibraryDocumentRepository.findAll();
		log.debug("DatatypeLibraryRepository.findAll datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public List<DatatypeLibraryDocument> findByScope(SCOPE scope, Long accountId) {
		List<DatatypeLibraryDocument> datatypeLibrary = datatypeLibraryDocumentRepository.findByScope(scope, accountId);
		log.debug("DatatypeLibraryRepository.findByScope datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public List<DatatypeLibraryDocument> findByScope(SCOPE scope) {
		List<DatatypeLibraryDocument> datatypeLibrary = datatypeLibraryDocumentRepository.findByScope(scope);
		log.debug("DatatypeLibraryRepository.findByScope datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public List<String> findHl7Versions() {
		return datatypeLibraryDocumentRepository.findHl7Versions();
	}

	@Override
	public DatatypeLibraryDocument findById(String id) {
		return datatypeLibraryDocumentRepository.findOne(id);
	}

	@Override
	public List<DatatypeLibraryDocument> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion. start");
		List<DatatypeLibraryDocument> datatypeLibraries = datatypeLibraryDocumentRepository.findScopesNVersion(scopes,
				hl7Version);
		log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion datatypeLibraries=" + datatypeLibraries.size());
		return datatypeLibraries;
	}

	@Override
	public List<DatatypeLibraryDocument> findByAccountId(Long accountId, String hl7Version) {
		List<DatatypeLibraryDocument> datatypeLibrary = datatypeLibraryDocumentRepository.findByAccountId(accountId,
				hl7Version);
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public DatatypeLibraryDocument save(DatatypeLibraryDocument library) {
		DatatypeLibraryDocument datatypeLibrary = datatypeLibraryDocumentRepository.save(library);
		return datatypeLibrary;
	}

	@Override
	public DatatypeLibraryDocument saveMetaData(String libId, DatatypeLibraryMetaData datatypeLibraryMetaData) {
		log.info("DataypeServiceImpl.save=" + datatypeLibraryMetaData.getName());
		DatatypeLibraryDocument dataTypeLibrary = datatypeLibraryDocumentRepository.findOne(libId);
		dataTypeLibrary.setMetaData(datatypeLibraryMetaData);
		return datatypeLibraryDocumentRepository.save(dataTypeLibrary);
	}

	DatatypeLibraryMetaData defaultMetadata() {
		DatatypeLibraryMetaData metaData = new DatatypeLibraryMetaData();
		metaData.setName("Master data type library");
		metaData.setOrgName("NIST");
		return metaData;
	}

	@Override
	public DatatypeLibraryDocument create(String name, String ext, SCOPE scope, String hl7Version, String description,
			String orgName, Long accountId) {
		DatatypeLibraryMetaData metaData = defaultMetadata();
		metaData.setName(name);
		metaData.setHl7Version(hl7Version);
		metaData.setDescription(description);
		metaData.setOrgName(orgName);
		metaData.setDatatypeLibId(UUID.randomUUID().toString());
		metaData.setExt(ext);
		metaData.setDate(DateUtils.getCurrentTime());
		DatatypeLibraryDocument datatypeLibraryDocument = new DatatypeLibraryDocument();
		datatypeLibraryDocument.setMetaData(metaData);
		datatypeLibraryDocument.setScope(scope);
		datatypeLibraryDocument.setAccountId(accountId);
		datatypeLibraryDocument.setSectionDescription("Default description");
		datatypeLibraryDocument.setSectionTitle("Default title");
		datatypeLibraryDocument.setSectionContents("Default contents");
		datatypeLibraryDocument
				.setDatatypeLibrary(datatypeLibraryService.create(name, ext, scope, hl7Version, accountId));
		datatypeLibraryDocument.setTableLibrary(tableLibraryService.create(name, ext, scope, hl7Version, accountId));
		datatypeLibraryDocument = datatypeLibraryDocumentRepository.insert(datatypeLibraryDocument);

		return datatypeLibraryDocument;
	}

	boolean checkDup(Datatype dt, DatatypeLibrary dtLib, String ext) {
		return dtLib.getChildren().contains(new DatatypeLink(dt.getId(), dt.getName(), ext));
	}

	String decorateExt(String ext) {
		return ext + "-" + genRand();
	}

	String deNull(String ext) {
		return (ext != null && ext.trim().length() > 0) ? ext : genRand();
	}

	String genRand() {
		return Integer.toString(rand.nextInt(100));
	}

	class DatatypeByLabel implements Comparator<Datatype> {

		@Override
		public int compare(Datatype thisDt, Datatype thatDt) {
			return thatDt.getLabel().compareTo(thisDt.getLabel());
		}
	}

	// @Override
	// public List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version,
	// String name, Long accountId) {
	// return datatypeLibraryRepository.findFlavors(scope, hl7Version, name,
	// accountId);
	// }

	// @Override
	// public List<DatatypeLibrary> findLibrariesByFlavorName(SCOPE scope,
	// String hl7Version,
	// String name, Long accountId) {
	// return datatypeLibraryRepository.findByNameAndHl7VersionAndScope(name,
	// hl7Version,
	// scope.toString());
	// }

	// @Override
	// public List<Datatype> getChildren(String id) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public void delete(DatatypeLibraryDocument libraryDocument) {

		if (libraryDocument.getId() != null)
			datatypeLibraryDocumentRepository.delete(libraryDocument);

	}

	@Override
	public List<DatatypeLibraryDocument> findLibrariesByFlavorName(SCOPE scope, String hl7Version, String name,
			Long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatatypeLibrary getDatatypeLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableLibrary getTableLibrary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatatypeLibrary saveDatatypeLibrary(String libId, DatatypeLibrary datatypeLibrary) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatatypeLibrary saveTableLibrary(String libId, TableLibrary tableLibrary) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String dtLibId) {

		DatatypeLibraryDocument dtlibDoc = datatypeLibraryDocumentRepository.findById(dtLibId);
		datatypeLibraryService.delete(dtlibDoc.getDatatypeLibrary());
		tableLibraryService.delete(dtlibDoc.getTableLibrary());
		datatypeLibraryDocumentRepository.delete(dtLibId);

	}
}
