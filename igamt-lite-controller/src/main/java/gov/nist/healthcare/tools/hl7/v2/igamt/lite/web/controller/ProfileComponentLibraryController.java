package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;

@RestController
@RequestMapping("/profilecomponent-library")
public class ProfileComponentLibraryController extends CommonController {

  
  Logger log = LoggerFactory.getLogger(TableLibraryController.class);

  
  @Autowired
  private ProfileComponentLibraryService profileComponentLibraryService;

  @Autowired
  UserService userService;
  @Autowired
  private IGDocumentService igDocumentService;
  @Autowired
  private ProfileComponentLibraryRepository profileComponentLibraryRepository;

  @Autowired
  AccountRepository accountRepository;

  @RequestMapping(method = RequestMethod.GET)
  public List<ProfileComponentLibrary> getProfileComponentLibraries() {
    log.info("Fetching all pc libraries.");
    List<ProfileComponentLibrary> profileComponentLibraries = profileComponentLibraryService.findAll();
    return profileComponentLibraries;
  }

  @RequestMapping(value = "/{pcLibId}/profilecomponents", method = RequestMethod.GET,
      produces = "application/json")
  public List<ProfileComponent> getProfileComponentsByLibrary(@PathVariable("pcLibId") String pcLibId) {
    log.info("Fetching ProfileComponentByLibrary..." + pcLibId);
    List<ProfileComponent> result = profileComponentLibraryService.findProfileComponentsById(pcLibId);
    return result;
  }
  @RequestMapping(value = "/{pcLibId}", method = RequestMethod.GET,
      produces = "application/json")
  public ProfileComponentLibrary getProfileComponentLibrary(@PathVariable("pcLibId") String pcLibId) {
    log.info("Fetching ProfileComponentByLibrary..." + pcLibId);
    ProfileComponentLibrary result = profileComponentLibraryService.findProfileComponentLibById(pcLibId);
    return result;
  }
  
}
//  @RequestMapping(value = "{igId}/add", method = RequestMethod.POST)
//  public IGDocument saveProfileComponent(@PathVariable("igId") String igId,
//      @RequestBody ProfileComponent profileComponent, HttpServletRequest request,
//      HttpServletResponse response)
//      throws IOException, IGDocumentNotFoundException, IGDocumentException {
//    IGDocument d = igDocumentService.findOne(igId);
//    if (d == null) {
//      throw new IGDocumentNotFoundException(igId);
//    }
//    if(d.getProfile().getProfileComponentLibrary()==null){
//      ProfileComponentLibrary profileComponentLibrary=new ProfileComponentLibrary();
//      profileComponentLibrary.addProfileComponent(profileComponent);
////      profileComponentService.create(profileComponent);
////      ProfileComponentLink profileComponentLink=new ProfileComponentLink();
////      profileComponentLink.setId(profileComponent.getId());
////      profileComponentLink.setName(profileComponent.getName());
////      profileComponentLibrary.addProfileComponent(profileComponentLink);
//      profileComponentLibraryRepository.save(profileComponentLibrary);
//      d.getProfile().setProfileComponentLibrary(profileComponentLibrary);
//    } else {
////      profileComponentService.create(profileComponent);
////      ProfileComponentLink profileComponentLink=new ProfileComponentLink();
////      profileComponentLink.setId(profileComponent.getId());
////      profileComponentLink.setName(profileComponent.getName());
//      d.getProfile().getProfileComponentLibrary().addProfileComponent(profileComponent);
//      profileComponentLibraryRepository.save(d.getProfile().getProfileComponentLibrary());
//    }
//    igDocumentService.save(d);
//    return d;
//  }
//  @RequestMapping(value = "{igId}/addMult", method = RequestMethod.POST)
//  public IGDocument saveProfileComponents(@PathVariable("igId") String igId,
//      @RequestBody Set<ProfileComponent> profileComponents, HttpServletRequest request,
//      HttpServletResponse response)
//      throws IOException, IGDocumentNotFoundException, IGDocumentException {
//    IGDocument d = igDocumentService.findOne(igId);
//    if (d == null) {
//      throw new IGDocumentNotFoundException(igId);
//    }
//    if(d.getProfile().getProfileComponentLibrary()==null){
//      ProfileComponentLibrary profileComponentLibrary=new ProfileComponentLibrary();
//      profileComponentLibrary.addProfileComponents(profileComponents);;
////      profileComponentService.create(profileComponent);
////      ProfileComponentLink profileComponentLink=new ProfileComponentLink();
////      profileComponentLink.setId(profileComponent.getId());
////      profileComponentLink.setName(profileComponent.getName());
////      profileComponentLibrary.addProfileComponent(profileComponentLink);
//      profileComponentLibraryRepository.save(profileComponentLibrary);
//      d.getProfile().setProfileComponentLibrary(profileComponentLibrary);
//    } else {
////      profileComponentService.create(profileComponent);
////      ProfileComponentLink profileComponentLink=new ProfileComponentLink();
////      profileComponentLink.setId(profileComponent.getId());
////      profileComponentLink.setName(profileComponent.getName());
//      d.getProfile().getProfileComponentLibrary().addProfileComponents(profileComponents);
//      profileComponentLibraryRepository.save(d.getProfile().getProfileComponentLibrary());
//    }
//    igDocumentService.save(d);
//    return d;
//  }
//}
