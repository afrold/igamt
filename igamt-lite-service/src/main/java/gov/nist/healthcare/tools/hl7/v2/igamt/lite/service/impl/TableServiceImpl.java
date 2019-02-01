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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipantPermission;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.TableUpdateStreamException;

/**
 * @author gcr1
 *
 */
@Service
public class TableServiceImpl implements TableService {

	Logger log = LoggerFactory.getLogger(TableServiceImpl.class);
	private static final int TOTAL_CELL = 5;
	private static final String CODE_SYSTEM = "HL70396";
	private static final String CODE_SYSTEM_VERSION = "1.0";

	@Autowired
	private TableRepository tableRepository;

	@Override
	public List<Table> findAll() {
		return tableRepository.findAll();
	}

	@Override
	public Table findById(String id) {
		if (id != null) {
			log.info("TableServiceImpl.findById=" + id);
			return tableRepository.findOne(id);
		}
		return null;
	}

	@Override
	public Table findOneShortById(String id) {
		if (id != null) {
			log.info("TableServiceImpl.findOneShortById=" + id);
			return tableRepository.findOneShortById(id);
		}
		return null;
	}

	@Override
	public List<Table> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		List<Table> tables = tableRepository.findByScopesAndVersion(scopes, hl7Version);
		log.info("TableServiceImpl.findByScopeAndVersion=" + tables.size());
		return tables;
	}

	@Override
	public Table findByScopeAndVersionAndBindingIdentifier(SCOPE scope, String hl7Version, String bindingIdentifier) {
		List<SCOPE> scopes = new ArrayList<SCOPE>();
		scopes.add(scope);

		List<Table> tables = this.findByScopesAndVersion(scopes, hl7Version);

		for (Table t : tables) {
			if (t.getBindingIdentifier().equals(bindingIdentifier))
				return t;
		}
		return null;
	}

	@Override
	public List<Table> findShared(Long accountId) {
		// TODO Auto-generated method stub
		List<Table> tables = tableRepository.findShared(accountId);
		List<Table> sharedWithAccount = new ArrayList<Table>();
		for (Table t : tables) {
			for (ShareParticipantPermission p : t.getShareParticipantIds()) {
				if (p.getAccountId() == accountId && !p.isPendingApproval()) {
					sharedWithAccount.add(t);
				}
			}
		}
		return sharedWithAccount;
	}

	@Override
	public List<Table> findPendingShared(Long accountId) {
		// TODO Auto-generated method stub
		List<Table> tables = tableRepository.findShared(accountId);
		List<Table> sharedWithAccount = new ArrayList<Table>();
		for (Table t : tables) {
			for (ShareParticipantPermission p : t.getShareParticipantIds()) {
				if (p.getAccountId() == accountId && p.isPendingApproval()) {
					sharedWithAccount.add(t);
				}
			}
		}
		return sharedWithAccount;
	}

	@Override
	public Table save(Table table) {
		log.info("TableServiceImpl.save=" + table.getBindingIdentifier());
		return tableRepository.save(table);
	}

	@Override
	public void delete(Table table) {
		log.info("TableServiceImpl.delete=" + table.getBindingIdentifier());
		tableRepository.delete(table);
	}

	@Override
	public void delete(String id) {
		log.info("TableServiceImpl.delete=" + id);
		tableRepository.delete(id);
	}

	@Override
	public void save(List<Table> tables) {
		// TODO Auto-generated method stub
		tableRepository.save(tables);
	}

	@Override
	public List<Table> findAllByIds(Set<String> ids) {
		// TODO Auto-generated method stub
		return tableRepository.findAllByIds(ids);
	}

	@Override
	public List<Table> findShortAllByIds(Set<String> ids) {
		// TODO Auto-generated method stub
		return tableRepository.findShortAllByIds(ids);
	}

	@Override
	public Table save(Table table, Date date) {
		log.info("TableServiceImpl.save=" + table.getBindingIdentifier());
		table.setDateUpdated(date);
		return tableRepository.save(table);
	}

	@Override
	public Date updateDate(String id, Date date) {
		return tableRepository.updateDate(id, date);
	}

	@Override
	public void updateStatus(String id, STATUS status) {
		tableRepository.updateStatus(id, status);
	}

	@Override
	public void delete(List<Table> tables) {
		tableRepository.delete(tables);
	}

	@Override
	public List<Table> findByScope(String scope) {
		return tableRepository.findByScope(scope);
	}

	@Override
	public List<Table> findByBindingIdentifierAndScope(String bindingIdentifier, String scope) {
		return tableRepository.findByBindingIdentifierAndScope(bindingIdentifier, scope);
	}

	@Override
	public Table findOneByScopeAndBindingIdentifier(String scope, String bindingIdentifier) {
		return tableRepository.findOneByScopeAndBindingIdentifier(scope, bindingIdentifier);
	}

	@Override
	public List<Table> findByScopeAndVersion(String scope, String hl7Version) {
		return tableRepository.findByScopeAndVersion(scope, hl7Version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#
	 * updateDescription(java.lang. String, java.lang.String)
	 */
	@Override
	public void updateDescription(String id, String description) {
		// TODO Auto-generated method stub
		tableRepository.updateDescription(id, description);
	}

	@Override
	public void updateAllDescription(String id, String description, String defPreText, String defPostText) {
		// TODO Auto-generated method stub
		tableRepository.updateAllDescription(id, description, defPreText, defPostText);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#
	 * updateCodeSystem(java.lang. String, java.util.Set)
	 */
	@Override
	public void updateCodeSystem(String id, Set<String> codesSystemtoAdd) {
		// TODO Auto-generated method stub
		tableRepository.updateCodeSystem(id, codesSystemtoAdd);
	}

	@Override
	public void updateAttributes(String id, String attributeName, Object value) {
		// TODO Auto-generated method stub
		tableRepository.updateAttributes(id, attributeName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#
	 * findShortByScope(java.lang. String)
	 */
	@Override
	public List<Table> findShortByScope(String scope) {
		// TODO Auto-generated method stub
		return tableRepository.findShortByScope(scope);
	}

	@Override
	public Table findShortById(String id) {
		// TODO Auto-generated method stub
		return tableRepository.findShortById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService#
	 * findByScopeAndVersionAndBindingIdentifier(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<Table> findByScopeAndVersionAndBindingIdentifier(String scope, String version,
			String bindingIdentifier) {
		// TODO Auto-generated method stub
		return tableRepository.findByScopeAndVersionAndBindingIdentifier(scope, version, bindingIdentifier);
	}

	@Override
	public Table findDynamicTable0396() {
		// TODO Auto-generated method stub
		return tableRepository.findDynamicTable0396();
	}

	/**
	 * Update a code values with values from a row in the excel file
	 * 
	 * @param code
	 * @param row
	 * @return
	 */
	private Code updateCode(Code code, Row row) {
		Cell cell = row.getCell(0);
		String value = null;
		if (cell != null) {
			value = cell.getStringCellValue();
			code.setValue(value);
		}

		cell = row.getCell(1);
		if (cell != null) {
			value = cell.getStringCellValue();
			code.setLabel(value);
		}

		cell = row.getCell(2);
		if (cell != null) {
			value = cell.getStringCellValue();
			code.setComments(value);
		}

		code.setCodeUsage("P");

		code.setCodeSystem(CODE_SYSTEM);
		code.setCodeSystemVersion(CODE_SYSTEM_VERSION);
		code.setType(Constant.CODE);
		code.setDateUpdated(new Date());
		return code;
	}

	@Override
	public Table updateTable(Table table, InputStream io) throws TableUpdateStreamException {
		Workbook workbook = null;
		try {
			if (io == null)
				throw new TableUpdateStreamException("Document is empty");

			if (table == null)
				throw new TableUpdateStreamException("Table is empty");

			log.info("Updating Dynamic Table");
			HashMap<String, Code> codeMap = new HashMap<String, Code>();
			if (table.getCodes() != null) {
				table.getCodes().forEach(code -> {
					codeMap.put(code.getValue(), code);
				});
			}
			workbook = WorkbookFactory.create(io);
			int numberOfSheets = workbook.getNumberOfSheets();
			if (numberOfSheets == 0)
				throw new TableUpdateStreamException("Document is empty");
			Sheet codeSheet = workbook.getSheetAt(0); // code sheet
			codeSheet.removeRow(codeSheet.getRow(0)); // skip first row
			codeSheet.forEach(row -> {
				Cell c = row.getCell(0);
				String value = c.getStringCellValue();
				Code code = null;
				Cell status = row.getCell(4);

				if (!codeMap.containsKey(value)) {
					if (status == null || !isObsolete(status.getStringCellValue())) {
						code = updateCode(new Code(), row);
						codeMap.put(code.getValue(), code);
						table.addCode(code);
					}
				} else {
					if (status == null || !isObsolete(status.getStringCellValue())) {
						code = codeMap.get(value);
						code = updateCode(code, row);
					} else {
						table.getCodes().remove(code);
					}
				}
			});
			table.setDateUpdated(new Date());
			if (table.getVersion() == null) {
				table.setVersion("1");
			} else {
				// Version should be retrieve from file
				table.setVersion(Integer.parseInt(table.getVersion()) + 1 + "");
			}

			return save(table);
		} catch (EncryptedDocumentException e) {
			throw new TableUpdateStreamException("Document is encrypted and cannot be read");
		} catch (InvalidFormatException e) {
			throw new TableUpdateStreamException("Invalid format document");
		} catch (IOException e) {
			throw new TableUpdateStreamException("Cannot read the document");
		} finally {
			if (workbook != null)
				try {
					workbook.close();
				} catch (IOException e) {
				}
		}

	}

	private boolean isObsolete(String status) {
		return status != null && status.equalsIgnoreCase("obsolete");
	}

}
