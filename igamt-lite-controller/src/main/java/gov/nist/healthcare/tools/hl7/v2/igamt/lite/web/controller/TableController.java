package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.TableCSVGenerator;
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
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public Table getTableById(@PathVariable("id") String id) throws DataNotFoundException {
    log.info("Fetching tableById..." + id);
    
    Table table = findById(id);
    int codeSize = table.getCodes().size();
    if(codeSize > Constant.CODESIZELIMIT) {
    	List<Code> codes = new ArrayList<Code>();
    	Code c = new Code();
        c.setValue("Too Many Codes");
        c.setLabel("Here are " + codeSize + " codes. All codes have been omitted by the perforamance issue");
        c.setComments("Current Limit of Code size is " + Constant.CODESIZELIMIT);
        c.setCodeSystem("NA");
        c.setType(Constant.CODE);
        codes.add(c);
        table.setCodes(codes);
    }
    return table;
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Table save(@RequestBody Table table)
      throws TableSaveException, ForbiddenOperationException {
    if (SCOPE.USER.equals(table.getScope())||(SCOPE.MASTER.equals(table.getScope())&&table.getStatus().equals(STATUS.UNPUBLISHED))) {
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

  @RequestMapping(value = "/exportCSV/{id}", method = RequestMethod.POST,
	      produces = "text/xml", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
	  public void exportCSV(@PathVariable("id") String tableId, HttpServletRequest request, HttpServletResponse response) throws DataNotFoundException, IOException {
	  log.info("Export table " + tableId);
	  Table table = findById(tableId);
      
      InputStream content = IOUtils.toInputStream(new TableCSVGenerator().generate(table), "UTF-8");
      response.setContentType("text/xml");
      response.setHeader("Content-disposition", "attachment;filename=" + table.getBindingIdentifier() + "-" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");
      FileCopyUtils.copy(content, response.getOutputStream());
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
