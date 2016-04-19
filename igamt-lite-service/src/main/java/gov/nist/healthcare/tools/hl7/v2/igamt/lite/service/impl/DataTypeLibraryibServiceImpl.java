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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
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
		log.info("datatypeLibrary=" + datatypeLibrary.size());
		return datatypeLibrary;
	}

	@Override
	public DatatypeLibrary findByScopeAndVersion(Constant.SCOPE scope, String hl7Version) {
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryRepository.findByScopeAndMetaData_Hl7Version(scope, hl7Version);
		log.info("datatypeLibraries=" + datatypeLibraries.size());
		DatatypeLibrary datatypeLibrary = null;
		if (datatypeLibraries.size() > 0) {
			datatypeLibrary = datatypeLibraries.get(0);
		}
		return datatypeLibrary;
	}

	@Override
	public List<DatatypeLibrary> findByAccountId(Long accountId) {
		List<DatatypeLibrary> datatypeLibrary = datatypeLibraryRepository.findByAccountId(accountId);
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
	public DatatypeLibrary create(DatatypeLibrary datatypeLibrary) {
		datatypeLibrary = datatypeLibraryRepository.insert(datatypeLibrary);
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

	@Override
	public DatatypeLibrary findById(String id) {
		return datatypeLibraryRepository.findById(id);
	}
}
