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
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.DatatypeRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;

/**
 * @author gcr1
 *
 */
@Service
public class DataTypeLibraryServiceImpl implements DatatypeLibraryService {

	Logger log = LoggerFactory.getLogger(DataTypeLibraryServiceImpl.class);

	@Autowired
	private DatatypeLibraryRepository datatypeLibraryRepository;

	@Autowired
	private DatatypeRepository datatypeRepository;
	
	private Random rand = new Random();

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
		return datatypeLibraryRepository.findOne(id);
	}

	@Override
	public List<DatatypeLibrary> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion. start");
		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryRepository.findScopesNVersion(scopes, hl7Version);
		log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion datatypeLibraries=" + datatypeLibraries.size());
		return datatypeLibraries;
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
		return datatypeLibrary;
	}

	@Override
	public void delete(DatatypeLibrary library) {
		datatypeLibraryRepository.delete(library);
	}
	
	@Override
	public void delete(String id) {
		datatypeLibraryRepository.delete(id);
	}

	@Override
	public List<Datatype> bindDatatypes(List<String> datatypeIds, String datatypeLibraryId, String datatypeLibraryExt,
			Long accountId) {
		
		DatatypeLibrary dtLib = datatypeLibraryRepository.findById(datatypeLibraryId);
		dtLib.setExt(deNull(datatypeLibraryExt));
		List<DatatypeLibrary> dtLibDups = datatypeLibraryRepository.findDups(dtLib);
		if (dtLibDups != null) {
			String ext = decorateExt(dtLib.getExt());
			dtLib.setExt(ext);
		}
		dtLib.getMetaData().setExt(dtLib.getExt());
		dtLib.setAccountId(accountId);
		
		List<Datatype> datatypes = datatypeRepository.findByIds(datatypeIds);
		for (Datatype dt : datatypes) {
			dt.setId(null);
			dt.getLibIds().add(datatypeLibraryId);
			dt.setExt(decorateExt(dtLib.getExt()));
			dt.setType(Constant.DATATYPE);
			dt.setScope(dtLib.getScope());
			dt.setHl7Version(dtLib.getMetaData().getHl7Version());
			dt.setDate(Constant.mdy.format(new Date()));
			dt.setAccountId(accountId);
			//  We save at this point in order to have an id for the link.
			datatypeRepository.save(dt);
			dtLib.addLink(dt);
		}
		datatypeLibraryRepository.save(dtLib);
		return datatypes;
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

	@Override
	public List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version,
			String name, Long accountId) {
		 return datatypeLibraryRepository.findFlavors(scope, hl7Version, name, accountId);
	}
	
	@Override
	public List<DatatypeLibrary> findLibrariesByFlavorName(SCOPE scope,
			String hl7Version, String name, Long accountId) {
		return datatypeLibraryRepository.findLibrariesByFlavorName(scope, hl7Version, name, accountId);
	}

	 
}
