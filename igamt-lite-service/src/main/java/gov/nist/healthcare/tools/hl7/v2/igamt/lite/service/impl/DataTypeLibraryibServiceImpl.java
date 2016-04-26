/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;

/**
 * @author gcr1
 *
 */
@Service
public class DataTypeLibraryibServiceImpl implements DatatypeLibraryService {

	Logger log = LoggerFactory.getLogger(DataTypeLibraryibServiceImpl.class);

	@Autowired
	private DatatypeLibraryRepository datatypeLibraryRepository;
	
	@Override
	public List<DatatypeLibrary> findAll() {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findAll();
		log.debug("DatatypeLibraryRepository.findAll datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}
	
	@Override
	public List<DatatypeLibrary> findByScopes(List<SCOPE> scopes) {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findByScopes(scopes);
		log.debug("DatatypeLibraryRepository.findByScopes datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}
	
	@Override
	public List<String> findHl7Versions() {
		return datatypeLibraryRepository.findHl7Versions();
	}
	
	@Override
	public DatatypeLibrary findById(String id) {
		return datatypeLibraryRepository.findById(id);
	}

	@Override
	public DatatypeLibrary findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryRepository.findByScopesAndMetaData_Hl7Version(scopes, hl7Version);
		log.info("DataTypeLibraryibServiceImpl.findByScopeAndVersion datatypeLibraries=" + datatypeLibraries.size());
		DatatypeLibrary datatypeLibrary = null;
		if (datatypeLibraries.size() > 0) {
			datatypeLibrary = datatypeLibraries.get(0);
		}
		return datatypeLibrary;
	}

	@Override
	public List<DatatypeLibrary> findByAccountId(Long accountId, String hl7Version) {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findByAccountId(accountId, hl7Version);
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public DatatypeLibrary save(DatatypeLibrary library) {
		DatatypeLibrary datatypeLibrary = datatypeLibraryRepository.save(library);
		return datatypeLibrary;
	}

	DatatypeLibraryMetaData defaultMetadata() {
		DatatypeLibraryMetaData metaData = new DatatypeLibraryMetaData();
		metaData.setName("Master data type library");
		metaData.setOrgName("NIST");
		metaData.setDate(Constant.mdy.format(new Date()));
		return metaData;
	}

	@Override
	public DatatypeLibrary create(String name, String ext, SCOPE scope, String hl7Version, Long accountId) {
		DatatypeLibraryMetaData metaData = defaultMetadata();
		metaData.setName(name);
		metaData.setHl7Version(hl7Version);
		metaData.setExt(ext);
		DatatypeLibrary datatypeLibrary = new DatatypeLibrary();
		datatypeLibrary.setMetaData(metaData);
		datatypeLibrary.setScope(scope);
		datatypeLibrary.setAccountId(accountId);
		datatypeLibrary.setDate(Constant.mdy.format(new Date()));
		datatypeLibrary.setSectionDescription("Default description");
		datatypeLibrary.setSectionTitle("Default title");
		datatypeLibrary.setSectionContents("Default contents");
		datatypeLibrary = datatypeLibraryRepository.insert(datatypeLibrary);
		datatypeLibrary.getMetaData().setDatatypLibId(datatypeLibrary.getId());
		return datatypeLibrary;
	}

	@Override
	public void delete(DatatypeLibrary library) {
		datatypeLibraryRepository.delete(library);
	}
	
	class DatatypeByLabel implements Comparator<Datatype> {

		@Override
		public int compare(Datatype thisDt, Datatype thatDt) {
			return thatDt.getLabel().compareTo(thisDt.getLabel());
		}
	}
}
