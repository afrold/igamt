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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.BindingParametersForDatatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ShareParticipantPermission;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UnchangedDataType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VersionAndUse;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.UnchangedDataRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.VersionAndUseService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeDeleteException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DatatypeSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ScopeAndNameAndVersionWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ScopesAndVersionWrapper;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/datatypes")
public class DatatypeController extends CommonController {

  Logger log = LoggerFactory.getLogger(DatatypeController.class);

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private TableService tableService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;
  @Autowired
  UnchangedDataRepository unchangedData;
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

  @RequestMapping(value = "/findByIds", method = RequestMethod.POST, produces = "application/json")
  public List<Datatype> findByIds(@RequestBody Set<String> ids) {
    log.info("Fetching datatypeByIds..." + ids);
    List<Datatype> result = datatypeService.findByIds(ids);
    return result;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public Datatype getDatatypeById(@PathVariable("id") String id) throws DataNotFoundException {
    log.info("Fetching datatypeById..." + id);
    return findById(id);
  }

  @RequestMapping(value = "/findByScopesAndVersion", method = RequestMethod.POST,
      produces = "application/json")
  public List<Datatype> findByScopesAndVersion(
      @RequestBody ScopesAndVersionWrapper scopesAndVersion) {
    log.info("Fetching the datatype. scope=" + scopesAndVersion.getScopes() + " hl7Version="
        + scopesAndVersion.getHl7Version());
    List<Datatype> datatypes = new ArrayList<Datatype>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }

      datatypes.addAll(datatypeService.findByScopesAndVersion(scopesAndVersion.getScopes(),
          scopesAndVersion.getHl7Version()));
      if (datatypes.isEmpty()) {
        throw new NotFoundException("Datatype not found for scopesAndVersion=" + scopesAndVersion);
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return datatypes;
  }

  @RequestMapping(value = "/findByScope", method = RequestMethod.POST,
      produces = "application/json")
  public List<Datatype> findByScope(@RequestBody String scope) {

    List<Datatype> datatypes = datatypeService.findByScope(scope);

    return datatypes;
  }



  @RequestMapping(value = "/findOneStrandard", method = RequestMethod.POST,
      produces = "application/json")
  public Datatype findByNameAndVersionAndScope(
      @RequestBody ScopeAndNameAndVersionWrapper unchagedDatatype) {

    Datatype d = null;
    int max = 1;
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }

      List<Datatype> ds = datatypeService.findByNameAndVersionAndScope(unchagedDatatype.getName(),
          unchagedDatatype.getHl7Version(), "HL7STANDARD");
      if (ds == null || ds.isEmpty()) {
        throw new NotFoundException("no standard d");
      }
      d = ds.get(0);
      d.setExt(max + "");

      Datatype temp = datatypeService.findByNameAndVersionsAndScope(unchagedDatatype.getName(),
          unchagedDatatype.getVersions(), "MASTER");
      if (temp != null) {
        String tempext = temp.getExt();
        try {
          int extd = Integer.parseInt(d.getExt());
          int ext = Integer.parseInt(tempext);
          if (ext >= extd) {
            d.setExt((ext + 1 + ""));
          }
        } catch (NumberFormatException e) {

        }
      }

    } catch (Exception e) {
      log.error("", e);
    }
    return d;
  }


  @RequestMapping(value = "/getLastMaster", method = RequestMethod.POST,
      produces = "application/json")
  public Datatype getLastMaster(@RequestBody NameAndVersionWrapper wrapper) {
    List<SCOPE> scopes = new ArrayList<SCOPE>();
    Datatype d = null;
    scopes.add(SCOPE.HL7STANDARD);
    // List<Datatype> result=null;
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      List<UnchangedDataType> unchanged =
          unchangedData.findByNameAndVersions(wrapper.getName(), wrapper.getVersion());
      if (unchanged != null && !unchanged.isEmpty()) {

        UnchangedDataType model = unchanged.get(0);
        List<Datatype> ds = datatypeService.findByNameAndVersionAndScope(wrapper.getName(),
            wrapper.getVersion(), SCOPE.HL7STANDARD.toString());
        d = ds != null && !ds.isEmpty() ? ds.get(0) : null;
        if (d != null) {
          d.setId(null);
          d.setHl7versions(model.getVersions());
          String ext = this.findLastExtesionForVersions(model.getName(), model.getVersions());
          d.setExt(ext);
        }
      }

    } catch (Exception e) {
      log.error("", e);
    }
    return d;

  }

  public String findLastExtesionForVersions(String name, List<String> versions) {

    List<Datatype> datatypes =
        datatypeService.findAllByNameAndVersionsAndScope(name, versions, "MASTER");
    int extd = 1;
    if (datatypes != null && !datatypes.isEmpty()) {
      String max = datatypes.get(0).getExt();

      try {
        extd = Integer.parseInt(max);
      } catch (NumberFormatException e) {

      }
      for (Datatype d : datatypes) {
        try {
          int temp = Integer.parseInt(d.getExt());
          if (temp >= extd) {
            extd = temp + 1;
          }
        } catch (NumberFormatException e) {

        }
      }

    }
    return extd + "";

  }

  @RequestMapping(value = "/findPublished", method = RequestMethod.POST)
  public List<Datatype> findPublishedMaster(@RequestBody String version) {
    List<Datatype> published = new ArrayList<Datatype>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      List<Datatype> master = datatypeService.findByScope("MASTER");
      for (Datatype dt : master) {
        if (dt.getStatus().equals(STATUS.PUBLISHED)) {
          for (String v : dt.getHl7versions()) {
            if (v.equals(version)) {
              published.add(dt);

            }
          }


        }
      }

    } catch (Exception e) {
      log.error("", e);
    }
    return published;
  }


  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Datatype save(@RequestBody Datatype datatype)
      throws DatatypeSaveException, ForbiddenOperationException {
    if (!SCOPE.HL7STANDARD.equals(datatype.getScope())) {
      log.info("Saving the " + datatype.getScope() + " datatype.");
      datatype.setDateUpdated(DateUtils.getCurrentDate());
      Datatype saved = datatypeService.save(datatype);
      return saved;
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_SAVE_DATATYPE");
    }
  }

  @RequestMapping(value = "/publish", method = RequestMethod.POST)
  public Datatype publish(@RequestBody Datatype datatype) {
    if (!STATUS.PUBLISHED.equals(datatype.getStatus())) {
      log.debug("datatypeLibrary=" + datatype);
      log.debug("datatypeLibrary.getId()=" + datatype.getId());
      VersionAndUse versionInfo = versionAndUse.findById(datatype.getId());



      if (versionInfo == null) {
        versionInfo = new VersionAndUse();
        versionInfo.setPublicationVersion(1);
        datatype.setPublicationVersion(1);
        versionInfo.setId(datatype.getId());
      } else {
        List<VersionAndUse> ancestors = versionAndUse.findAllByIds(versionInfo.getAncestors());
        versionInfo.setPublicationDate(DateUtils.getCurrentTime());

        for (VersionAndUse ancestor : ancestors) {
          ancestor.setDeprecated(true);
          versionAndUse.save(ancestor);
        }

        versionInfo.setPublicationVersion(versionInfo.getPublicationVersion() + 1);
        datatype.setPublicationVersion(versionInfo.getPublicationVersion());


      }
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      versionInfo.setAccountId(account.getId());
      versionInfo.setPublicationDate(DateUtils.getCurrentTime());
      datatype.setPublicationDate(DateUtils.getCurrentTime());
      // if (datatype.getScope().toString().equals(SCOPE.MASTER.toString())) {
      // datatype.setName(datatype.getName() + "_" + datatype.getExt());
      // }
      versionAndUse.save(versionInfo);
      datatype.setStatus(STATUS.PUBLISHED);
      Datatype saved = datatypeService.save(datatype);
      log.debug("saved.getId()=" + saved.getId());
      log.debug("saved.getScope()=" + saved.getScope());
      return saved;
    }
    return datatype;
  }

  @RequestMapping(value = "/getMergedMaster", method = RequestMethod.POST)
  public Datatype getMergedMaster(@RequestBody Datatype datatype) throws Exception {

    // Datatype d = mergeComponent(datatype);


    Datatype d = getMergedDatatype(datatype, datatype.getScope());

    return d;
  }

  //
  // public Datatype getMergedDT(Datatype datatype) throws Exception {
  //
  //
  //
  //
  //
  // return d;
  // }
  //
  public Datatype getMergedDatatype(Datatype d, SCOPE scope) throws CloneNotSupportedException {
    List<Datatype> res =
        datatypeService.findByScopeAndVersionAndParentVersion(scope, d.getHl7Version(), d.getId());

    if (!res.isEmpty()) {

      return res.get(0);
    } else {


      Datatype toReturn = new Datatype();
      Datatype result = new Datatype();


      List<Datatype> all = datatypeService.findByNameAndVersionAndScope(d.getName(),
          d.getHl7Version(), SCOPE.HL7STANDARD.toString());
      if (!all.isEmpty()) {
        result = all.get(0);
      }

      if (result.getValueSetBindings() != null) {
        d.setValueSetBindings(result.getValueSetBindings());
      }
      for (int i = 0; i < d.getComponents().size(); i++) {
        Component temp = d.getComponents().get(i);
        if (temp.getDatatype() != null) {
          Datatype dtTemp = datatypeService.findById(temp.getDatatype().getId());
          if (dtTemp.getScope().toString().equals("INTERMASTER")) {
            System.out.println(dtTemp.getId());
            d.getComponents().get(i).getDatatype()
                .setId((result.getComponents().get(i).getDatatype().getId()));
          } else {
            Datatype dt = dtTemp.clone();
            dt.setId(dtTemp.getId());
            dt.setHl7Version(d.getHl7Version());
            // dt.setId(newId);


            Datatype toChange = getMergedDatatype(dt, d.getScope());
            d.getComponents().get(i).getDatatype().setId(toChange.getId());



          }
        }
      }
      String newId = new ObjectId().toString();
      d.setParentVersion(d.getId());
      d.setId(newId);


      result = datatypeService.save(d);
      // String newId = new ObjectId().toString();

      return result;
    }



  }



  /**
   * @param datatype
   * @throws Exception
   */
  private Datatype mergeComponent(Datatype datatype) throws Exception {

    Datatype result = null;
    List<Datatype> all = datatypeService.findByNameAndVersionAndScope(datatype.getName(),
        datatype.getHl7Version(), SCOPE.HL7STANDARD.toString());
    if (!all.isEmpty()) {
      result = all.get(0);
    } else {
      throw new Exception("cannot find datatype" + datatype.getName());
    }

    if (result.getValueSetBindings() != null) {
      datatype.setValueSetBindings(result.getValueSetBindings());
    }
    for (int i = 0; i < datatype.getComponents().size(); i++) {
      Component temp = datatype.getComponents().get(i);
      if (temp.getDatatype() != null) {
        Datatype dtTemp = datatypeService.findById(temp.getDatatype().getId());
        if (dtTemp.getScope().toString().equals("INTERMASTER")) {
          System.out.println(dtTemp.getId());
          datatype.getComponents().get(i).getDatatype()
              .setId((result.getComponents().get(i).getDatatype().getId()));
        } else {
          Datatype d = dtTemp.clone();
          String newId = new ObjectId().toString();
          d.setHl7Version(datatype.getHl7Version());
          d.setId(newId);
          d.setParentVersion(dtTemp.getId());
          if (!d.getComponents().isEmpty()) {
            mergeComponent(d);
          }
          datatypeService.save(d);
          datatype.getComponents().get(i).getDatatype().setId(newId);
        }
      }
    }
    return datatype;
  }






  @RequestMapping(value = "/updateDatatypeBinding", method = RequestMethod.POST)
  public void updateDatatypeBinding(
      @RequestBody List<BindingParametersForDatatype> bindingParametersList)
      throws DatatypeSaveException, ForbiddenOperationException, DataNotFoundException {
    for (BindingParametersForDatatype paras : bindingParametersList) {
      Datatype datatype = this.datatypeService.findById(paras.getDatatypeId());
      if (!SCOPE.HL7STANDARD.equals(datatype.getScope())) {
        datatype.setDate(DateUtils.getCurrentTime());
        Component targetComponent =
            datatype.getComponents().get(this.indexOfComponent(paras.getComponentId(), datatype));
        TableLink tableLink = paras.getTableLink();
        DatatypeLink datatypeLink = paras.getDatatypeLink();

        if (datatypeLink != null) {
          targetComponent.setDatatype(datatypeLink);
        }

        datatypeService.save(datatype);
      } else {
        throw new ForbiddenOperationException("FORBIDDEN_SAVE_SEGMENT");
      }
    }
  }

  @RequestMapping(value = "/saveDts", method = RequestMethod.POST)
  public List<Datatype> save(@RequestBody List<Datatype> datatypes) throws DatatypeSaveException {
    List<Datatype> dts = new ArrayList<Datatype>();
    for (Datatype datatype : datatypes) {
      if (!SCOPE.HL7STANDARD.equals(datatype.getScope())) {
        log.debug("datatype=" + datatype);
        log.debug("datatype.getId()=" + datatype.getId());
        log.info("Saving the " + datatype.getScope() + " datatype.");
        datatype.setDate(DateUtils.getCurrentTime());
        Datatype saved = datatypeService.save(datatype);
        log.debug("saved.getId()=" + saved.getId());
        log.debug("saved.getScope()=" + saved.getScope());
        dts.add(datatype);
      } else {
        throw new DatatypeSaveException();
      }
    }
    return dts;


  }

  @RequestMapping(value = "/saveAll", method = RequestMethod.POST)
  public void saveAll(@RequestBody List<Datatype> datatypes) throws DatatypeSaveException {
    log.info("Saving " + datatypes.size() + " datatypes.");
    Iterator<Datatype> it = datatypes.iterator();
    while (it.hasNext()) {
      Datatype c = it.next();
      if (SCOPE.HL7STANDARD.equals(c.getScope())) {
        it.remove();
      }
    }
    datatypeService.save(datatypes);
  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
  public ResponseMessage delete(@PathVariable("id") String id)
      throws DatatypeDeleteException, ForbiddenOperationException, DataNotFoundException {
    Datatype datatype = findById(id);
    if (!SCOPE.HL7STANDARD.equals(datatype.getScope())) {
      log.info("Deleting " + id);
      datatypeService.delete(datatype);
      return new ResponseMessage(ResponseMessage.Type.success, "datatypeDeletedSuccess", null);
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_DELETE_DATATYPE");
    }
  }

  @RequestMapping(value = "/{id}/datatypes", method = RequestMethod.GET,
      produces = "application/json")
  public Set<Datatype> collectDatatypes(@PathVariable("id") String id)
      throws DataNotFoundException {
    Datatype datatype = findById(id);
    Set<Datatype> datatypes = new HashSet<Datatype>();
    if (datatype != null) {
      List<Component> components = datatype.getComponents();
      for (Component c : components) {
        Datatype dt = datatypeService.findById(c.getDatatype().getId());
        datatypes.addAll(datatypeService.collectDatatypes(dt));
      }
    }
    return datatypes;
  }

  /**
   * Share multiple participants
   * 
   * @param id
   * @param participants
   * @return
   * @throws IGDocumentException
   */
  @RequestMapping(value = "/{id}/share", method = RequestMethod.POST, produces = "application/json")
  public boolean shareDatatype(@PathVariable("id") String id,
      @RequestBody HashMap<String, Object> participants) throws Exception {
    log.info("Sharing datatype with id=" + id + " with partipants=" + participants);
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
      Datatype d = this.findById(id);
      d.setAccountId(accountId);
      if (d.getAccountId() == null || !d.getAccountId().equals(account.getId())) {
        throw new Exception("You do not have the right privilege to share this Datatype");
      }
      if (participants.containsKey("participantsList")) {
        List<Integer> participantsList = (List<Integer>) participants.get("participantsList");
        for (Integer participantId : participantsList) {
          Long longId = new Long(participantId);
          if (longId != accountId) {
            d.getShareParticipantIds().add(new ShareParticipantPermission(longId));
          }

          // Find the user
          Account acc = accountRepository.findOne(longId);
          // Send confirmation email
          sendShareConfirmation(d, acc, account);
        }
      }
      datatypeService.save(d);
      return true;
    } catch (Exception e) {
      log.error("", e);
      throw new Exception("Failed to share Datatype \n" + e.getMessage());
    }
  }

  /**
   * Unshare one participant
   * 
   * @param id
   * @param participantId
   * @return
   * @throws IGDocumentException
   */
  @RequestMapping(value = "/{id}/unshare", method = RequestMethod.POST,
      produces = "application/json")
  public boolean unshareDatatype(@PathVariable("id") String id,
      @RequestBody Long shareParticipantId) throws IGDocumentException {
    log.info("Unsharing datatype with id=" + id + " with participant=" + shareParticipantId);
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null)
        throw new UserAccountNotFoundException();
      Datatype d = this.findById(id);
      // Cannot unshare owner
      if (d.getAccountId() != null && shareParticipantId != d.getAccountId()) {
        if (d.getAccountId().equals(account.getId())
            || account.getId().equals(shareParticipantId)) {
          d.getShareParticipantIds().remove(new ShareParticipantPermission(shareParticipantId));
          // Find the user
          Account acc = accountRepository.findOne(shareParticipantId);
          // Send unshare confirmation email
          sendUnshareEmail(d, acc, account);
        } else {
          throw new Exception("You do not have the right to share this datatype");
        }
      } else {
        throw new Exception("You do not have the right to share this datatype");
      }
      datatypeService.save(d);
      return true;
    } catch (Exception e) {
      log.error("", e);
      throw new IGDocumentException("Failed to unshare Datatype \n" + e.getMessage());
    }
  }

  /**
   * Find shared datatypes
   */
  @RequestMapping(value = "/findShared", method = RequestMethod.GET, produces = "application/json")
  public List<Datatype> findShared() throws Exception {
    List<Datatype> datatypes = new ArrayList<Datatype>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      datatypes = datatypeService.findShared(account.getId());

    } catch (Exception e) {
      log.error("", e);
    }
    return datatypes;
  }

  /**
   * Find pending shared datatypes
   */
  @RequestMapping(value = "/findPendingShared", method = RequestMethod.GET,
      produces = "application/json")
  public List<Datatype> findPendingShared() throws Exception {
    List<Datatype> datatypes = new ArrayList<Datatype>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }
      datatypes = datatypeService.findPendingShared(account.getId());

    } catch (Exception e) {
      log.error("", e);
    }
    return datatypes;
  }

  public Datatype findById(String id) throws DataNotFoundException {
    Datatype result = datatypeService.findById(id);
    if (result == null)
      throw new DataNotFoundException("datatypeNotFound");
    return result;
  }

  private int indexOfComponent(String id, Datatype d) throws DataNotFoundException {
    int index = 0;
    for (Component c : d.getComponents()) {
      if (id.equals(c.getId()))
        return index;
      index = index + 1;
    }
    throw new DataNotFoundException("fieldNotFound");
  }

  private void sendShareConfirmation(Datatype datatype, Account target, Account source) {

    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

    msg.setSubject("NIST IGAMT Datatype Shared with you.");
    msg.setTo(target.getEmail());
    msg.setText("Dear " + target.getUsername() + " \n\n" + source.getFullName() + "("
        + source.getUsername() + ")" + " wants to share the following data type with you: \n"
        + "\n Name: " + datatype.getName() + "_" + datatype.getExt() + "\n Description:"
        + datatype.getDescription() + "\n HL7 Version:" + datatype.getHl7Version()
        + "\n Commit Version:" + datatype.getPublicationVersion() + "\n Commit Date:"
        + datatype.getPublicationDate() + "\n"
        + "If you wish to accept or reject the request please go to IGAMT tool under the 'Shared Elements' tab"
        + "\n\n" + "P.S: If you need help, contact us at '" + ADMIN_EMAIL + "'");
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendUnshareEmail(Datatype datatype, Account target, Account source) {

    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

    msg.setSubject("NIST IGAMT Datatype unshare");
    msg.setTo(target.getEmail());
    msg.setText("Dear " + target.getUsername() + " \n\n"
        + "This is an automatic email to let you know that " + source.getFullName() + "("
        + source.getUsername() + ") has stopped sharing the following data type with you:\n"
        + "\n Name: " + datatype.getName() + "_" + datatype.getExt() + "\n Description:"
        + datatype.getDescription() + "\n HL7 Version:" + datatype.getHl7Version()
        + "\n Commit Version:" + datatype.getPublicationVersion() + "\n Commit Date:"
        + datatype.getPublicationDate() + "\n\n" + "P.S: If you need help, contact us at '"
        + ADMIN_EMAIL + "'");
    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      log.error(ex.getMessage(), ex);
    }
  }
}
