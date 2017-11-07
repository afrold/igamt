package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SourceType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipantPermission;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.VersionAndUseService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.TableCSVGenerator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.TableSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

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
  @Autowired
  VersionAndUseService versionAndUse;

  @Value("${server.email}")
  private String SERVER_EMAIL;

  @Value("${admin.email}")
  private String ADMIN_EMAIL;

  @Autowired
  private MailSender mailSender;

  @Autowired
  private SimpleMailMessage templateMessage;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public Table getTableById(@PathVariable("id") String id) throws DataNotFoundException {
    log.info("Fetching tableById..." + id);

    Table table = findById(id);
    // int codeSize = table.getCodes().size();
    // if(codeSize > Constant.CODESIZELIMIT) {
    // List<Code> codes = new ArrayList<Code>();
    // Code c = new Code();
    // c.setValue("Too Many Codes");
    // c.setLabel("Here are " + codeSize + " codes. All codes have been omitted by the perforamance
    // issue");
    // c.setComments("Current Limit of Code size is " + Constant.CODESIZELIMIT);
    // c.setCodeSystem("NA");
    // c.setType(Constant.CODE);
    // codes.add(c);
    // table.setCodes(codes);
    // }
    return table;
  }

  @RequestMapping(value = "/{libId}/{id}", method = RequestMethod.GET,
      produces = "application/json")
  public Table getInLibary(@PathVariable("libId") String libId, @PathVariable("id") String id)
      throws DataNotFoundException {
    TableLibrary lib = tableLibraryService.findById(libId);
    Table table = null;
    if (lib.getCodePresence().containsKey(id)) {
      if (!lib.getCodePresence().get(id).equals(false)) {
        table = tableService.findById(id);
      } else {
        table = tableService.findShortById(id);
      }
    } else {
      table = tableService.findById(id);
    }
    return table;
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Table save(@RequestBody Table table)
      throws TableSaveException, ForbiddenOperationException {
    if (SCOPE.USER.equals(table.getScope()) || (SCOPE.MASTER.equals(table.getScope())
        && table.getStatus().equals(STATUS.UNPUBLISHED))) {
      log.debug("table=" + table);
      log.debug("table.getId()=" + table.getId());
      log.info("Saving the " + table.getScope() + " table.");
      if (table.getSourceType().equals(SourceType.INTERNAL)) {
        table.setNumberOfCodes(table.getCodes().size());
      }
      Table saved = tableService.save(table);
      log.debug("saved.getId()=" + saved.getId());
      log.debug("saved.getScope()=" + saved.getScope());
      return table;
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_SAVE_TABLE");
    }
  }

  @RequestMapping(value = "/exportCSV/{id}", method = RequestMethod.POST, produces = "text/xml",
      consumes = "application/x-www-form-urlencoded; charset=UTF-8")
  public void exportCSV(@PathVariable("id") String tableId, HttpServletRequest request,
      HttpServletResponse response) throws DataNotFoundException, IOException {
    log.info("Export table " + tableId);
    Table table = findById(tableId);

    InputStream content = IOUtils.toInputStream(new TableCSVGenerator().generate(table), "UTF-8");
    response.setContentType("text/xml");
    response.setHeader("Content-disposition", "attachment;filename=" + table.getBindingIdentifier()
        + "-" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv");
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

  @RequestMapping(value = "/findShortById", method = RequestMethod.POST)
  public Table findShortById(@RequestBody String id) {
    return tableService.findShortById(id);
  }

  @RequestMapping(value = "/findShortAllByIds", method = RequestMethod.POST)
  public List<Table> findShortAllByIds(@RequestBody Set<String> tableIds) {
    return tableService.findShortAllByIds(tableIds);
  }


  @RequestMapping(value = "/findShortByScope", method = RequestMethod.POST)
  public List<Table> findShortByScope(@RequestBody String scope) {
    return tableService.findShortByScope(scope);
  }

  public Table findById(String id) throws DataNotFoundException {
    Table result = tableService.findById(id);
    if (result == null)
      throw new DataNotFoundException("tableNotFound");
    return result;
  }

  @RequestMapping(value = "/publish", method = RequestMethod.POST)
  public Table publish(@RequestBody Table table) {
    log.debug("datatypeLibrary=" + table);
    log.debug("datatypeLibrary.getId()=" + table.getId());
    VersionAndUse versionInfo = versionAndUse.findById(table.getId());


    if (versionInfo == null) {
      versionInfo = new VersionAndUse();
      versionInfo.setPublicationVersion(1);
      table.setPublicationVersion(1);
      versionInfo.setId(table.getId());
    } else {
      List<VersionAndUse> ancestors = versionAndUse.findAllByIds(versionInfo.getAncestors());
      versionInfo.setPublicationDate(DateUtils.getCurrentTime());

      for (VersionAndUse ancestor : ancestors) {
        ancestor.setDeprecated(true);
        versionAndUse.save(ancestor);
      }
      versionInfo.setPublicationVersion(versionInfo.getPublicationVersion() + 1);
      table.setPublicationVersion(versionInfo.getPublicationVersion() + 1);


    }
    versionInfo.setPublicationDate(DateUtils.getCurrentTime());
    table.setPublicationDate(DateUtils.getCurrentTime());
    versionAndUse.save(versionInfo);
    table.setStatus(STATUS.PUBLISHED);
    Table saved = tableService.save(table);
    log.debug("saved.getId()=" + saved.getId());
    log.debug("saved.getScope()=" + saved.getScope());
    return saved;


  }

  /**
   * Share multiple participants
   * 
   * @param id
   * @param participants
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/share", method = RequestMethod.POST, produces = "application/json")
  public boolean shareDatatype(@PathVariable("id") String id,
      @RequestBody HashMap<String, Object> participants) throws Exception {
    log.info("Sharing table with id=" + id + " with partipants=" + participants);
    Long accountId = null;
    // Get account ID
    if (participants.containsKey("accountId")) {
      accountId = new Long((int) participants.get("accountId"));
    }
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null)
        throw new UserAccountNotFoundException();
      Table t = this.findById(id);
      t.setAccountId(accountId);
      if (t.getAccountId() == null || !t.getAccountId().equals(account.getId())) {
        throw new Exception("You do not have the right privilege to share this Table");
      }
      if (participants.containsKey("participantsList")) {
        List<Integer> participantsList = (List<Integer>) participants.get("participantsList");
        for (Integer participantId : participantsList) {
          Long longId = new Long(participantId);
          if (longId != accountId) {
            t.getShareParticipantIds().add(new ShareParticipantPermission(longId));
          }

          // Find the user
          Account acc = accountRepository.findOne(accountId);
          // Send confirmation email
          sendShareConfirmation(t, acc, account);
        }
      }
      tableService.save(t);
      return true;
    } catch (Exception e) {
      log.error("", e);
      throw new Exception("Failed to share Table \n" + e.getMessage());
    }
  }

  /**
   * Unshare one participant
   * 
   * @param id
   * @param participantId
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/{id}/unshare", method = RequestMethod.POST,
      produces = "application/json")
  public boolean unshareIgDocument(@PathVariable("id") String id,
      @RequestBody Long shareParticipantId) throws Exception {
    log.info("Unsharing table with id=" + id + " with participant=" + shareParticipantId);
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null)
        throw new UserAccountNotFoundException();
      Table t = this.findById(id);
      // Cannot unshare owner
      if (t.getAccountId() != null && shareParticipantId != t.getAccountId()) {
        if (t.getAccountId().equals(account.getId())
            || account.getId().equals(shareParticipantId)) {
          t.getShareParticipantIds().remove(new ShareParticipantPermission(shareParticipantId));
          // Find the user
          Account acc = accountRepository.findOne(shareParticipantId);
          // Send unshare confirmation email
          sendUnshareEmail(t, acc, account);
        } else {
          throw new Exception("You do not have the right to share this table");
        }
      } else {
        throw new Exception("You do not have the right to share this table");
      }
      tableService.save(t);
      return true;
    } catch (Exception e) {
      log.error("", e);
      throw new Exception("Failed to unshare Table \n" + e.getMessage());
    }
  }

  /**
   * Find shared datatypes
   */
  @RequestMapping(value = "/findShared", method = RequestMethod.GET, produces = "application/json")
  public List<Table> findShared() throws Exception {
    List<Table> tables = new ArrayList<Table>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      tables = tableService.findShared(account.getId());

    } catch (Exception e) {
      log.error("", e);
    }
    return tables;
  }

  /**
   * Find pending shared datatypes
   */
  @RequestMapping(value = "/findPendingShared", method = RequestMethod.GET,
      produces = "application/json")
  public List<Table> findPendingShared() throws Exception {
    List<Table> tables = new ArrayList<Table>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      tables = tableService.findPendingShared(account.getId());

    } catch (Exception e) {
      log.error("", e);
    }
    return tables;
  }

  private void sendShareConfirmation(Table table, Account target, Account source) {

    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

    msg.setSubject("NIST IGAMT Value Set Shared with you.");
    msg.setTo(target.getEmail());
    msg.setText("Dear " + target.getUsername() + " \n\n" + source.getFullName() + "("
        + source.getUsername() + ")" + " wants to share the following value set with you: \n"
        + "\n Name: " + table.getName() + "\n Binding Identifier:" + table.getBindingIdentifier()
        + "\n Description:" + table.getDescription() + "\n HL7 Version:" + table.getHl7Version()
        + "\n Commit Version:" + table.getPublicationVersion() + "\n Commit Date:"
        + table.getPublicationDate() + "\n"
        + "If you wish to accept or reject the request please go to IGAMT tool under the 'Shared Elements' tab"
        + "\n\n" + "P.S: If you need help, contact us at '" + ADMIN_EMAIL + "'");
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendUnshareEmail(Table table, Account target, Account source) {

    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

    msg.setSubject("NIST IGAMT Value Set unshare");
    msg.setTo(target.getEmail());
    msg.setText("Dear " + target.getUsername() + " \n\n"
        + "This is an automatic email to let you know that " + source.getFullName() + "("
        + source.getUsername() + ") has stopped sharing the following value set with you:\n"
        + "\n Name: " + table.getName() + "\n Binding Identifier:" + table.getBindingIdentifier()
        + "\n Description:" + table.getDescription() + "\n HL7 Version:" + table.getHl7Version()
        + "\n Commit Version:" + table.getPublicationVersion() + "\n Commit Date:"
        + table.getPublicationDate() + "\n\n" + "P.S: If you need help, contact us at '"
        + ADMIN_EMAIL + "'");
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  @RequestMapping(value = "/searchForDelta", method = RequestMethod.POST,
      produces = "application/json")
  public List<Table> getValueSets(@RequestBody ValueSetDeltaSearchWrapper wrapper)
      throws DataNotFoundException {
    List<Table> ret = new ArrayList<Table>();
    if (wrapper.getVersion() != null && !wrapper.getVersion().equals("NV")) {
      ret = tableService.findByScopeAndVersionAndBindingIdentifier(wrapper.getScope(),
          wrapper.getVersion(), wrapper.getBindingIdentifier());
    } else {
      ret = tableService.findByBindingIdentifierAndScope(wrapper.getBindingIdentifier(),
          wrapper.getScope());
    }
    return ret;
  }
}
