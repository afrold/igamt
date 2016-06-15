package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.TableSaveException;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tables")
public class TableController extends CommonController {
	static final Logger logger = LoggerFactory.getLogger(TableController.class);
	Logger log = LoggerFactory.getLogger(TableController.class);

	@Autowired
	private TableService tableService;

	@Autowired
	private TableLibraryService tableLibraryService;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public Table getTableById(@PathVariable("id") String id) {
		log.info("Fetching tableById..." + id);
		Table result = tableService.findById(id);
		return result;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Table save(@RequestBody Table table) throws TableSaveException,
			ForbiddenOperationException {
		log.debug("table=" + table);
		log.debug("table.getId()=" + table.getId());
		log.info("Saving the " + table.getScope() + " table.");
		table.setDate(DateUtils.getCurrentTime());
		Table saved = tableService.save(table);
		log.debug("saved.getId()=" + saved.getId());
		log.debug("saved.getScope()=" + saved.getScope());
		return table;

	}

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
	public boolean delete(@PathVariable("id") String tableId) {
		log.info("Deleting table " + tableId);
		tableService.delete(tableId);
		return true;
	}

  @RequestMapping(value = "/findAllByIds", method = RequestMethod.POST)
  public List<Table> collect(@RequestBody Set<String> tableIds) {
    return tableService.findAllByIds(tableIds);
  }


}
