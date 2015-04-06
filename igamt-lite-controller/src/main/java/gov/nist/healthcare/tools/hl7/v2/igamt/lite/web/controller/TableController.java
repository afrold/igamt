package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tables")
public class TableController extends CommonController {
	static final Logger logger = LoggerFactory.getLogger(TableController.class);

	// // CRUD R for TableLib
	// @RequestMapping(value = "/tableLibrary/{id}", method = RequestMethod.GET)
	// @ResponseBody
	// public Tables tableLibrary(@PathVariable("id") String id) {
	// return tablesService.findOne(id);
	// }
	//
	// // CRUD R for Table
	// @RequestMapping(value = "/table/{id}", method = RequestMethod.GET)
	// @ResponseBody
	// public Table table(@PathVariable("id") String id) {
	// return tableService.findOne(id);
	// }
	//
	// // CRUD C for Code
	// @RequestMapping(value = "/table/{targetId}/addCode", method =
	// RequestMethod.POST)
	// public Table addCode(@PathVariable("targetId") String targetId) {
	// Code code = new Code();
	// Table table = tableService.findOne(targetId);
	// table.getCodes().add(code);
	// return tableService.save(table);
	// }
	//
	// // Update for Table
	// @RequestMapping(value = "/table/update", method = RequestMethod.PUT)
	// public Table update(@RequestBody Table table) {
	// return tableService.save(table);
	// }
	//
	// // Update for Code
	// @RequestMapping(value = "/table/{tableId}/update", method =
	// RequestMethod.PUT)
	// public Code update(final String tableId, @RequestBody Code code) {
	// Table table = tableService.findOne(tableId);
	// for (Code c : table.getCodes()) {
	// if (code.getId() == c.getId()) {
	// table.getCodes().remove(c);
	// table.getCodes().add(code);
	// return code;
	// }
	// }
	// return null;
	// }

	/*
	 * 
	 * // CRUD C for Table, User cannot create tableLib
	 * 
	 * @RequestMapping(value = "/table/create/{tableLibraryId}", method =
	 * RequestMethod.POST) public Tables createTable(@RequestBody Long
	 * tableLibraryId) { Table t = new Table(); Tables tl =
	 * tablesService.findOne(tableLibraryId); tl.addTable(t); return
	 * tablesService.save(tl); }
	 * 
	 * 
	 * 
	 * // Update for TableLib
	 * 
	 * @RequestMapping(value = "/tableLibrary/update", method =
	 * RequestMethod.PUT) public Tables update(@RequestBody @Valid Tables
	 * tableLibrary) { return tablesService.save(tableLibrary); }
	 * 
	 * 
	 * 
	 * // Update for Code
	 * 
	 * @RequestMapping(value = "/table/updatecode/{tableId}", method =
	 * RequestMethod.PUT) public Code update(final Long tableId, @RequestBody
	 * 
	 * @Valid Code code) { Table table = tableService.findOne(tableId); for
	 * (Code c : table.getCodes()) { if (code.getId() == c.getId()) {
	 * table.getCodes().remove(c); table.getCodes().add(code); return code; } }
	 * return null; }
	 * 
	 * // CRUD D for Table, User cannot delete tableLib // FIXME If the table is
	 * binding to a tablelib, it should not be deleted
	 * 
	 * @RequestMapping(value = "/table/delete/{tableId}", method =
	 * RequestMethod.DELETE) public ResponseEntity<Boolean> delete(final Long
	 * tableId) { tableService.delete(tableId); return new
	 * ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	 * 
	 * }
	 * 
	 * // Delete Code for Table
	 * 
	 * @RequestMapping(value = "/table/delete/{tableId}/{codeId}", method =
	 * RequestMethod.DELETE) public ResponseEntity<Boolean> delete(final Long
	 * tableId, final Long codeId) { Table table =
	 * tableService.findOne(tableId); for (Code c : table.getCodes()) { if
	 * (codeId == c.getId()) { table.getCodes().remove(c); return new
	 * ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK); } } return new
	 * ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.OK); }
	 */
	// GET,SET
	// public TablesRepository getTableLibraryRepository() {
	// return tableLibraryRepository;
	// }
	//
	// public void setTableLibraryRepository(
	// TablesRepository tableLibraryRepository) {
	// this.tableLibraryRepository = tableLibraryRepository;
	// }
	//
	// public TableService getTableService() {
	// return tableService;
	// }
	//
	// public void setTableService(TableService tableService) {
	// this.tableService = tableService;
	// }

}
