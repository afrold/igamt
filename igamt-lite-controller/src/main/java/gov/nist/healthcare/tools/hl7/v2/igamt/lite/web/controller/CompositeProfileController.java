/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 6, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ApplyInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;


@RestController
@RequestMapping("/composite-profile")
public class CompositeProfileController {

  static final Logger logger = LoggerFactory.getLogger(MessageController.class);
  Logger log = LoggerFactory.getLogger(MessageController.class);
  @Autowired
  MessageService messageService;
  @Autowired
  IGDocumentService iGDocumentService;

  @Autowired
  ProfileComponentService profileComponentService;
  @Autowired
  CompositeProfileStructureService compositeProfileStructureService;
  @Autowired
  SegmentService segmentService;
  @Autowired
  DatatypeService datatypeService;
  @Autowired
  CompositeProfileService compositeProfileService;

  @RequestMapping(value = "/create/{igId}", method = RequestMethod.POST,
      produces = "application/json")
  public CompositeProfileStructure createCompositeProfile(@PathVariable String igId,
      @RequestBody CompositeProfileStructure compositeProfileStructure) throws IGDocumentException {
    compositeProfileStructure.setDateUpdated(new Date());
    IGDocument ig = iGDocumentService.findById(igId);

    if (ig.getProfile().getCompositeProfiles() == null) {
      Set<CompositeProfileStructure> cps = new HashSet<>();
      cps.add(compositeProfileStructure);
      ig.getProfile().getCompositeProfiles().setChildren(cps);
    } else {
      ig.getProfile().getCompositeProfiles().addChild(compositeProfileStructure);
    }
    List<ProfileComponent> pcs =
        profileComponentService.findByIds(compositeProfileStructure.getProfileComponentIds());
    for (ProfileComponent pc : pcs) {
      pc.addCompositeProfileStructure(compositeProfileStructure.getId());
    }
    profileComponentService.saveAll(pcs);
    Message core = messageService.findById(compositeProfileStructure.getCoreProfileId());
    core.addCompositeProfileStructure(compositeProfileStructure.getId());
    messageService.save(core);

    compositeProfileStructureService.save(compositeProfileStructure);
    iGDocumentService.save(ig);
    return compositeProfileStructure;
  }


  @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json")
  public CompositeProfileStructure saveCompositeProfile(
      @RequestBody CompositeProfileStructure compositeProfileStructure) throws IGDocumentException {
    compositeProfileStructure.setDateUpdated(new Date());

    compositeProfileStructureService.save(compositeProfileStructure);
    return compositeProfileStructure;
  }

  @RequestMapping(value = "/removePc/{pcId}", method = RequestMethod.POST,
      produces = "application/json")
  public CompositeProfileStructure removeProfileComponents(@PathVariable String pcId,
      @RequestBody CompositeProfileStructure compositeProfileStructure) throws IGDocumentException {
    compositeProfileStructure.setDateUpdated(new Date());
    ProfileComponent pc = profileComponentService.findById(pcId);
    String toRemove = "";
    for (String s : pc.getCompositeProfileStructureList()) {
      if (s.equals(compositeProfileStructure.getId())) {
        toRemove = s;
      }
    }
    pc.getCompositeProfileStructureList().remove(toRemove);
    profileComponentService.save(pc);

    compositeProfileStructureService.save(compositeProfileStructure);
    return compositeProfileStructure;
  }

  @RequestMapping(value = "/addPcs/{cpId}", method = RequestMethod.POST,
      produces = "application/json")
  public CompositeProfileStructure addPcs(@PathVariable String cpId,
      @RequestBody List<ApplyInfo> pcs) {
    CompositeProfileStructure cp = compositeProfileStructureService.findById(cpId);
    cp.setDateUpdated(new Date());
    List<String> profileComponentsIds = new ArrayList<>();
    for (ApplyInfo info : pcs) {
      info.setPcDate(new Date());
      cp.addProfileComponent(info);
      profileComponentsIds.add(info.getId());
    }
    List<ProfileComponent> profileComponents =
        profileComponentService.findByIds(profileComponentsIds);

    for (ProfileComponent pc : profileComponents) {
      pc.addCompositeProfileStructure(cp.getId());
    }
    profileComponentService.saveAll(profileComponents);
    compositeProfileStructureService.save(cp);
    return cp;

  }



  @RequestMapping(value = "/build", method = RequestMethod.POST, produces = "application/json")
  public CompositeProfile buildCompositeProfile(
      @RequestBody CompositeProfileStructure compositeProfileStructure) {

    return compositeProfileService.buildCompositeProfile(compositeProfileStructure);
  }

  @RequestMapping(value = "delete/{id}/{igId}", method = RequestMethod.GET,
      produces = "application/json")
  public void deleteCompositeProfileById(@PathVariable("id") String id,
      @PathVariable("igId") String igId) throws IGDocumentException {


    CompositeProfileStructure cpToDelete = compositeProfileStructureService.findById(id);
    IGDocument ig = iGDocumentService.findById(igId);
    ig.getProfile().getCompositeProfiles().removeChild(cpToDelete.getId());
    Message msg = messageService.findById(cpToDelete.getCoreProfileId());
    msg.removeCompositeProfileStructure(id);
    List<ProfileComponent> pcs =
        profileComponentService.findByIds(cpToDelete.getProfileComponentIds());
    for (ProfileComponent pc : pcs) {
      pc.removeCompositeProfileStructure(id);
    }
    iGDocumentService.save(ig);
    profileComponentService.saveAll(pcs);
    messageService.save(msg);
    compositeProfileStructureService.delete(id);

  }



}
