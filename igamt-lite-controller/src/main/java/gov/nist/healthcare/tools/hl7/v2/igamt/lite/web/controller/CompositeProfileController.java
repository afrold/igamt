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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
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
    Message coreMessage = messageService.findById(compositeProfileStructure.getCoreProfileId());
    List<String> pcIds = new ArrayList<String>();
    for (ApplyInfo pc : compositeProfileStructure.getProfileComponentsInfo()) {
      pcIds.add(pc.getId());
    }
    List<ProfileComponent> pcs = profileComponentService.findByIds(pcIds);



    CompositeProfile compositeProfile = build(coreMessage, pcs);
    return compositeProfile;
  }

  public void findSegmentsId(List<SegmentRefOrGroup> children, Set<String> segmentsId) {
    for (SegmentRefOrGroup segRefOrGrp : children) {
      if (segRefOrGrp.getType().equalsIgnoreCase(Constant.SEGMENTREF)) {
        SegmentRef seg = (SegmentRef) segRefOrGrp;
        segmentsId.add(seg.getRef().getId());
      } else if (segRefOrGrp.getType().equalsIgnoreCase(Constant.GROUP)) {
        Group grp = (Group) segRefOrGrp;
        findSegmentsId(grp.getChildren(), segmentsId);
      }
    }

  }

  public void findDtsIdFromSegs(List<Segment> segments, Set<String> datatypesId) {
    for (Segment seg : segments) {
      for (Field field : seg.getFields()) {
        datatypesId.add(field.getDatatype().getId());

      }
    }
  }

  public void findDtsIdFromDts(List<Datatype> datatypes, Set<String> datatypesId) {
    for (Datatype dt : datatypes) {
      for (Component comp : dt.getComponents()) {
        datatypesId.add(comp.getDatatype().getId());

      }
    }
  }


  public CompositeProfile build(Message core, List<ProfileComponent> pcs) {
    Set<String> segIds = null;
    Set<String> dtIds = null;
    findSegmentsId(core.getChildren(), segIds);
    List<Segment> segments = segmentService.findByIds(segIds);
    findDtsIdFromSegs(segments, dtIds);
    List<Datatype> interDatatypes = datatypeService.findByIds(dtIds);
    findDtsIdFromDts(interDatatypes, dtIds);
    List<Datatype> datatypes = datatypeService.findByIds(dtIds);

    Map<String, Segment> segmentsMap = null;
    Map<String, Segment> segmentflavorsMap = null;
    Map<String, Datatype> datatypesMap = null;

    for (Segment seg : segments) {
      segmentsMap.put(seg.getId(), seg);
    }
    for (Datatype dt : datatypes) {
      datatypesMap.put(dt.getId(), dt);
    }

    Map<String, Table> tablesMap = null;
    Map<String, List<Object>> itemsMap = null;
    CompositeProfile result;
    for (ProfileComponent pc : pcs) {
      for (SubProfileComponent subPc : pc.getChildren()) {
        if (itemsMap.containsKey(subPc.getItemId())) {
          itemsMap.get(subPc.getItemId()).add(subPc);
        } else {
          List<Object> sub = null;
          sub.add(subPc);
          itemsMap.put(subPc.getItemId(), sub);
        }
      }
    }
    for (SegmentRefOrGroup segRefOrGrp : core.getChildren()) {
      if (segRefOrGrp.getType().equalsIgnoreCase(Constant.SEGMENTREF)) {
        buildSegmentRef((SegmentRef) segRefOrGrp, itemsMap, segmentflavorsMap, segmentsMap,
            datatypesMap, tablesMap);
      }
    }


    return null;

  }

  public void buildSegmentRef(SegmentRef segRef, Map<String, List<Object>> itemsMap,
      Map<String, Segment> segmentflavorsMap, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    if (itemsMap.containsKey(segRef.getId())) {
      // change values with pc Item Value
      // in subPc replace object with a class that containes all attributes(usage,oldUsage,....)


    }

    Segment seg = segmentsMap.get(segRef.getRef().getId());
    buildSegment(segRef, seg, itemsMap, segmentflavorsMap, segmentsMap, datatypesMap, tablesMap);
  }


  public void buildSegment(SegmentRef segRef, Segment segment, Map<String, List<Object>> itemsMap,
      Map<String, Segment> segmentflavorsMap, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    for (Field field : segment.getFields()) {
      if (itemsMap.containsKey(field.getId())) {
        // change values with pc item value
        // buildDatatype
        // create or update seg flavor in segmentflavorsMap
        if (segmentflavorsMap.containsKey(segment.getId())) {
          // just edit the field without creating a flavor
        }
      }
    }


  }



}
