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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;

/**
 * @author gcr1
 *
 */
@Service
public class TableLibraryServiceImpl implements TableLibraryService {

	Logger log = LoggerFactory.getLogger(TableLibraryServiceImpl.class);

	@Autowired
	private TableLibraryRepository tableLibraryRepository;

	@Autowired
	private TableRepository tableRepository;

	@Override
	public List<TableLibrary> findAll() {
		List<TableLibrary> tableLibrary = tableLibraryRepository.findAll();
		log.debug("TableLibraryRepository.findAll tableLibrary=" + tableLibrary.size());
		return tableLibrary;
	}

	@Override
	public List<TableLibrary> findByScopes(List<SCOPE> scopes) {
		List<TableLibrary> tableLibrary = tableLibraryRepository.findByScopes(scopes);
		log.debug("TableLibraryRepository.findByScopes tableLibrary=" + tableLibrary.size());
		return tableLibrary;
	}

	@Override
	public List<String> findHl7Versions() {
		return tableLibraryRepository.findHl7Versions();
	}

	@Override
	public TableLibrary findById(String id) {
		return tableLibraryRepository.findById(id);
	}

	@Override
	public List<TableLibrary> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion. start");
		List<TableLibrary> tableLibraries = tableLibraryRepository.findScopesNVersion(scopes, hl7Version);
		log.info("DataTypeLibraryibServiceImpl.findByScopesAndVersion tableLibraries=" + tableLibraries.size());
		return tableLibraries;
	}

	@Override
	public List<TableLibrary> findByAccountId(Long accountId, String hl7Version) {
		List<TableLibrary> tableLibrary = tableLibraryRepository.findByAccountId(accountId, hl7Version);
		log.info("tableLibrary=" + tableLibrary.size());
		return tableLibrary;
	}

	@Override
	public TableLibrary save(TableLibrary library) {
		TableLibrary tableLibrary = tableLibraryRepository.save(library);
		return tableLibrary;
	}

	TableLibraryMetaData defaultMetadata() {
		TableLibraryMetaData metaData = new TableLibraryMetaData();
		metaData.setName("Master data type library");
		metaData.setOrgName("NIST");
		return metaData;
	}

	@Override
	public TableLibrary create(String name, String ext, SCOPE scope, String hl7Version, Long accountId) {
		TableLibraryMetaData metaData = defaultMetadata();
		metaData.setName(name);
		metaData.setHl7Version(hl7Version);
		metaData.setExt(ext);
		metaData.setTableLibId(UUID.randomUUID().toString());
		TableLibrary tableLibrary = new TableLibrary();
		tableLibrary.setMetaData(metaData);
		tableLibrary.setScope(scope);
		tableLibrary.setAccountId(accountId);
		//tableLibrary.setSectionDescription("Default description");
		tableLibrary.setSectionTitle("Default title");
		tableLibrary.setSectionContent("Default contents");
		tableLibrary = tableLibraryRepository.insert(tableLibrary);
		return tableLibrary;
	}

	@Override
	public void delete(TableLibrary library) {
		if (library != null) {
			Set<TableLink> links = library.getChildren();
			if (links != null && links.size() > 0) {
				Set<String> ids = new HashSet<String>();
				for (TableLink link : links) {
					ids.add(link.getId());
				}
				List<Table> tables = tableRepository.findUserTablesByIds(ids);
				List<Table> tmp = new ArrayList<Table>();

				if (tables != null) {
					for (Table dt : tables) {
						if (dt.getStatus() == null || !dt.getStatus().equals(STATUS.PUBLISHED)) {
							tmp.add(dt); 
						}
					}
				}
				
				if(tmp.size() > 0){
					tableRepository.delete(tmp);
				}
			}
			if (library.getId() != null)
				tableLibraryRepository.delete(library);
		}
	}

	@Override
	public List<Table> findAllShortTablesByIds(String libId) {
		Set<TableLink> tableLinks = tableLibraryRepository.findChildrenById(libId);
		if (tableLinks != null && !tableLinks.isEmpty()) {
			Set<String> ids = new HashSet<String>();
			for (TableLink link : tableLinks) {
				ids.add(link.getId());
			}
			return tableRepository.findShortAllByIds(ids);
		}
		return new ArrayList<Table>(0);
	}

	@Override
	public List<Table> findTablesByIds(String libId) {
		Set<TableLink> tableLinks = tableLibraryRepository.findChildrenById(libId);
		if (tableLinks != null && !tableLinks.isEmpty()) {
			Set<String> ids = new HashSet<String>();
			for (TableLink link : tableLinks) {
				ids.add(link.getId());
			}
			return tableRepository.findAllByIds(ids);
		}
		return new ArrayList<Table>(0);
	}

	class TableByLabel implements Comparator<Table> {

		@Override
		public int compare(Table thisDt, Table thatDt) {
			return thatDt.getBindingIdentifier().compareTo(thisDt.getBindingIdentifier());
		}
	}
}
