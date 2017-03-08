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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
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
    IGDocument ig = iGDocumentService.findById(igId);

    if (ig.getProfile().getCompositeProfiles() == null) {
      Set<CompositeProfileStructure> cps = new HashSet<>();
      cps.add(compositeProfileStructure);
      ig.getProfile().getCompositeProfiles().setChildren(cps);
    } else {
      ig.getProfile().getCompositeProfiles().addChild(compositeProfileStructure);
    }

    compositeProfileStructureService.save(compositeProfileStructure);
    iGDocumentService.save(ig);
    return compositeProfileStructure;
  }



  @RequestMapping(value = "/build", method = RequestMethod.POST, produces = "application/json")
  public CompositeProfile buildCompositeProfile(
      @RequestBody CompositeProfileStructure compositeProfileStructure) {

    return compositeProfileService.buildCompositeProfile(compositeProfileStructure);
  }



}
