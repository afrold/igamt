package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

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

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.TableSaveException;

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
  public Table getTableById(@PathVariable("id") String id) throws DataNotFoundException {
    log.info("Fetching tableById..." + id);
    return findById(id);
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Table save(@RequestBody Table table)
      throws TableSaveException, ForbiddenOperationException {
    if (SCOPE.USER.equals(table.getScope())) {
      log.debug("table=" + table);
      log.debug("table.getId()=" + table.getId());
      log.info("Saving the " + table.getScope() + " table.");
      table.setDate(DateUtils.getCurrentTime());
      Table saved = tableService.save(table);
      log.debug("saved.getId()=" + saved.getId());
      log.debug("saved.getScope()=" + saved.getScope());
      return table;
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_SAVE_TABLE");
    }
  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
  public boolean delete(@PathVariable("id") String tableId)
      throws ForbiddenOperationException, DataNotFoundException {
    Table table = findById(tableId);
    if (SCOPE.USER.equals(table.getScope())) {
      log.info("Deleting table " + tableId);
      tableService.delete(tableId);
      return true;
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_DELETE_TABLE");
    }
  }

  @RequestMapping(value = "/findAllByIds", method = RequestMethod.POST)
  public List<Table> findAllByIds(@RequestBody Set<String> tableIds) {
    return tableService.findAllByIds(tableIds);
  }

  @RequestMapping(value = "/findShortAllByIds", method = RequestMethod.POST)
  public List<Table> findShortAllByIds(@RequestBody Set<String> tableIds) {
    return tableService.findShortAllByIds(tableIds);
  }


  public Table findById(String id) throws DataNotFoundException {
    Table result = tableService.findById(id);
    if (result == null)
      throw new DataNotFoundException("tableNotFound");
    return result;
  }


}
