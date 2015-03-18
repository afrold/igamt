package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.TableLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.TableService;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tables")
public class TableController extends CommonController {
	static final Logger logger = LoggerFactory.getLogger(TableController.class);

	@Autowired
	private TableLibraryRepository tableLibraryRepository;

	@Autowired
	private TableLibraryService tableLibraryService;

	@Autowired
	private TableService tableService;

	// CRUD R for TableLib
	@RequestMapping(value = "/tableLibrary/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Tables tableLibrary(@PathVariable("id") Long id) {
		return tableLibraryService.findOne(id);
	}

	// CRUD R for Table
	@RequestMapping(value = "/table/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Table table(@PathVariable("id") Long id) {
		return tableService.findOne(id);
	}
	/*	
		
	// CRUD C for Table, User cannot create tableLib
	@RequestMapping(value = "/table/create/{tableLibraryId}", method = RequestMethod.POST)
	public Tables createTable(@RequestBody Long tableLibraryId) {
		Table t = new Table();
		Tables tl = tableLibraryService.findOne(tableLibraryId);
		tl.addTable(t);
		return tableLibraryService.save(tl);
	}

	// CRUD C for Code
	@RequestMapping(value = "/table/create/{tableId}", method = RequestMethod.POST)
	public Table addCode(@RequestBody Long tableId) {
		Code code = new Code();
		Table table = tableService.findOne(tableId);
		table.getCodes().add(code);
		return tableService.save(table);
	}

	// Update for TableLib
	@RequestMapping(value = "/tableLibrary/update", method = RequestMethod.PUT)
	public Tables update(@RequestBody @Valid Tables tableLibrary) {
		return tableLibraryService.save(tableLibrary);
	}

	// Update for Table
	@RequestMapping(value = "/table/update", method = RequestMethod.PUT)
	public Table update(@RequestBody @Valid Table table) {
		return tableService.save(table);
	}

	// Update for Code
	@RequestMapping(value = "/table/updatecode/{tableId}", method = RequestMethod.PUT)
	public Code update(final Long tableId, @RequestBody @Valid Code code) {
		Table table = tableService.findOne(tableId);
		for (Code c : table.getCodes()) {
			if (code.getId() == c.getId()) {
				table.getCodes().remove(c);
				table.getCodes().add(code);
				return code;
			}
		}
		return null;
	}

	// CRUD D for Table, User cannot delete tableLib
	// FIXME If the table is binding to a tablelib, it should not be deleted
	@RequestMapping(value = "/table/delete/{tableId}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> delete(final Long tableId) {
		tableService.delete(tableId);
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);

	}

	// Delete Code for Table
	@RequestMapping(value = "/table/delete/{tableId}/{codeId}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> delete(final Long tableId, final Long codeId) {
		Table table = tableService.findOne(tableId);
		for (Code c : table.getCodes()) {
			if (codeId == c.getId()) {
				table.getCodes().remove(c);
				return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
			}
		}
		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK);
	}
*/
	// GET,SET
	public TableLibraryRepository getTableLibraryRepository() {
		return tableLibraryRepository;
	}

	public void setTableLibraryRepository(
			TableLibraryRepository tableLibraryRepository) {
		this.tableLibraryRepository = tableLibraryRepository;
	}

	public TableLibraryService getTableLibraryService() {
		return tableLibraryService;
	}

	public void setTableLibraryService(TableLibraryService tableLibraryService) {
		this.tableLibraryService = tableLibraryService;
	}

	public TableService getTableService() {
		return tableService;
	}

	public void setTableService(TableService tableService) {
		this.tableService = tableService;
	}

}
