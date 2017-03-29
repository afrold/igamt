package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;


@RestController
@RequestMapping("/profile-components")
public class ProfileComponentController extends CommonController {
  Logger log = LoggerFactory.getLogger(ProfileComponentController.class);

  @Autowired
  private ProfileComponentService profileComponentService;
  @Autowired
  UserService userService;
  @Autowired
  private IGDocumentService igDocumentService;
  @Autowired
  private MessageService messageService;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  private ProfileComponentLibraryService profileComponentLibraryService;
  @Autowired
  private ProfileComponentRepository profileComponentRepository;
  @Autowired
  CompositeProfileStructureService compositeProfileStructureService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public ProfileComponent getProfileComponentById(@PathVariable("id") String id)
      throws DataNotFoundException {
    log.info("Fetching ProfileComponentById..." + id);
    return profileComponentService.findById(id);
  }

  @RequestMapping(value = "/save/{pcLibid}", method = RequestMethod.POST)
  public ProfileComponent saveProfileComponent(@PathVariable("pcLibid") String pcLibid,
      @RequestBody ProfileComponent profileComponent, HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, IGDocumentNotFoundException, IGDocumentException {
    profileComponent.setDateUpdated(new Date());
    ProfileComponentLibrary pcLib =
        profileComponentLibraryService.findProfileComponentLibById(pcLibid);
    // pcLib.findOne(profileComponent.getId());
    for (ProfileComponentLink pcLink : pcLib.getChildren()) {
      if (pcLink.getId().equals(profileComponent.getId())) {
        pcLink.setName(profileComponent.getName());
        pcLink.setComment(profileComponent.getComment());
        pcLink.setDescription(profileComponent.getDescription());
      }
    }
    profileComponentLibraryService.save(pcLib);
    profileComponentService.save(profileComponent);
    return profileComponent;


  }

  @RequestMapping(value = "/saveAll", method = RequestMethod.POST)
  public List<ProfileComponent> saveProfileComponents(
      @RequestBody List<ProfileComponent> profileComponents, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    // profileComponent.setDataUpdated(new Date());

    for (ProfileComponent pc : profileComponents) {
      profileComponentService.save(pc);
    }

    return profileComponents;


  }

  @RequestMapping(value = "/delete/{pcLibid}", method = RequestMethod.POST)
  public ProfileComponentLibrary deleteProfileComponent(@PathVariable("pcLibid") String pcLibid,
      @RequestBody ProfileComponent profileComponent, HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, IGDocumentNotFoundException, IGDocumentException {
    ProfileComponentLibrary pcLib =
        profileComponentLibraryService.findProfileComponentLibById(pcLibid);
    ProfileComponentLink toDelete = new ProfileComponentLink();
    for (ProfileComponentLink pcLink : pcLib.getChildren()) {
      if (pcLink.getId().equals(profileComponent.getId())) {
        // pcLink.setName(profileComponent.getName());
        toDelete = pcLink;

      }
    }
    if (toDelete != null) {
      pcLib.getChildren().remove(toDelete);
    }
    profileComponentService.delete(toDelete.getId());
    profileComponentLibraryService.save(pcLib);
    return pcLib;


  }

  // @RequestMapping(value = "/create", method = RequestMethod.POST,
  // consumes = "application/json", produces = "application/json")
  // public ProfileComponent createPC(@RequestBody ProfileComponent pc) throws
  // UserAccountNotFoundException{
  // User u = userService.getCurrentUser();
  // Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
  // if (account == null)
  // throw new UserAccountNotFoundException();
  // log.info("Creation of Profile Component.");
  // log.debug("pc.getName()=" + pc.getName());
  // log.debug("pc.getAttributes()=" + pc.getAttributes());
  // profileComponentService.create(pc);
  // return pc;
  // }
  @RequestMapping(value = "{pcId}/addMult", method = RequestMethod.POST)
  public ProfileComponent addProfileComponents(@PathVariable("pcId") String pcId,
      @RequestBody Set<SubProfileComponent> subprofileComponents, HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    ProfileComponent pc = profileComponentService.findById(pcId);
    if (pc == null) {
      throw new IOException();
    }
    pc.addChildren(subprofileComponents);
    profileComponentRepository.save(pc);
    return pc;

  }

}
