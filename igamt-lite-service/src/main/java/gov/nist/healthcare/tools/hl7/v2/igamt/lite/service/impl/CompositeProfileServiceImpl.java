/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 7, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ApplyInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.PathGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator.ApplyInfoComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.CompositeProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FlavorService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.PathGroupService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.QueryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import javassist.NotFoundException;

@Service
public class CompositeProfileServiceImpl implements CompositeProfileService {

  @Autowired
  SegmentService segmentService;
  @Autowired
  DatatypeService datatypeService;
  @Autowired
  MessageService messageService;
  @Autowired
  ProfileComponentService profileComponentService;
  @Autowired
  CompositeProfileRepository compositeProfileRepository;
  @Autowired
  QueryService queryService;
  @Autowired
  PathGroupService pathGroupService;
  @Autowired
  FlavorService flavorService;

  public void createSegAndDtMaps(Message core, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap) {

    Set<String> segIds = new HashSet<String>();
    Set<String> dtIds = new HashSet<String>();
    findSegmentsId(core.getChildren(), segIds);
    List<Segment> segments = segmentService.findByIds(segIds);
    findDtsIdFromSegs(segments, dtIds);
    List<Datatype> interDatatypes = datatypeService.findByIds(dtIds);
    findDtsIdFromDts(interDatatypes, dtIds);
    List<Datatype> datatypes = datatypeService.findByIds(dtIds);
    for (Segment seg : segments) {
      segmentsMap.put(seg.getId(), seg);
    }
    for (Datatype dt : datatypes) {
      datatypesMap.put(dt.getId(), dt);
    }

  }

  @Override
  public CompositeProfile buildCompositeProfile(
      CompositeProfileStructure compositeProfileStructure) {
    Message coreMessage = messageService.findById(compositeProfileStructure.getCoreProfileId());
    Map<String, Segment> segmentsMap = new HashMap<>();
    Map<String, Datatype> datatypesMap = new HashMap<>();
    createSegAndDtMaps(coreMessage, segmentsMap, datatypesMap);
    queryService.setSegmentsMap(segmentsMap);
    queryService.setDatatypesMap(datatypesMap);
    List<ProfileComponent> pcs = new ArrayList<>();
    ApplyInfoComparator Comp = new ApplyInfoComparator();
    Collections.sort(compositeProfileStructure.getProfileComponentsInfo(), Comp);
    
    for (ApplyInfo pc : compositeProfileStructure.getProfileComponentsInfo()) {
      ProfileComponent pco = profileComponentService.findById(pc.getId());
      for(SubProfileComponent spc : pco.getChildren()){
        if(spc.getAttributes().getDatatype() != null) {
          Datatype dt = this.datatypeService.findById(spc.getAttributes().getDatatype().getId());
          if(dt != null)  {
            datatypesMap.put(dt.getId(), dt);
            addChildDt(datatypesMap, dt);
            
          }
        }
      }
      pcs.add(pco);
    }

    List<PathGroup> pathGroups = pathGroupService.buildPathGroups(coreMessage, pcs, segmentsMap);

    if (!pathGroups.isEmpty()) {
      pathGroups = pathGroups.get(0).getChildren();
    }


    CompositeProfile compositeProfile = new CompositeProfile();
    compositeProfile.setStructID(coreMessage.getStructID());
    compositeProfile.setEvent(coreMessage.getEvent());
    compositeProfile.setMessageType(coreMessage.getMessageType());
    compositeProfile.setIdentifier(coreMessage.getIdentifier());
    compositeProfile.setChildren(coreMessage.getChildren());
    compositeProfile.setValueSetBindings(coreMessage.getValueSetBindings());
    compositeProfile.setSingleElementValues(coreMessage.getSingleElementValues());
    compositeProfile.setPredicates(coreMessage.getPredicates());
    compositeProfile.setComments(coreMessage.getComments());
    compositeProfile.setConformanceStatements(coreMessage.getConformanceStatements());
    browse(compositeProfileStructure.getExt(), compositeProfile, pathGroups);
    compositeProfile.setSegmentsMap(queryService.getSegmentsMap());
    compositeProfile.setDatatypesMap(queryService.getDatatypesMap());
    compositeProfile.setName(compositeProfileStructure.getName());
    compositeProfile.setDescription(compositeProfileStructure.getDescription());
    compositeProfile.setComment(compositeProfileStructure.getComment());
    compositeProfile.setDefPreText(compositeProfileStructure.getDefPreText());
    compositeProfile.setDefPostText(compositeProfileStructure.getDefPostText());
    compositeProfile.setCoreProfileId(compositeProfileStructure.getCoreProfileId());
    compositeProfile.setProfileComponents(compositeProfileStructure.getProfileComponentsInfo());
    compositeProfile.setDateUpdated(compositeProfileStructure.getDateUpdated()); 
    return compositeProfile;
  }



  /**
   * @param datatypesMap
   * @param dt
   */
  private void addChildDt(Map<String, Datatype> datatypesMap, Datatype dt) {
    for(Component c: dt.getComponents()){
      if(c.getDatatype() != null){
        Datatype childDt = this.datatypeService.findById(c.getDatatype().getId());
        if(childDt != null) datatypesMap.put(childDt.getId(), childDt);
        if(childDt.getComponents() != null && childDt.getComponents().size() > 0) addChildDt(datatypesMap, childDt);
      }
    }
    
  }

  private void browse(String ext, DataModel dataModel, List<PathGroup> pathGroups) {
    for (PathGroup pathGroup : pathGroups) {
      try {
        DataModel dm = queryService.get(dataModel, pathGroup.getPath());

        DataModel context =
            flavorService.createFlavor(ext, dm, pathGroup.getAttributes(), pathGroup.getChildren());
        browse(ext, context, pathGroup.getChildren());
      } catch (NotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

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


  @Override
  public CompositeProfileStructure getCompositeProfileStructureById(String id) {

    return compositeProfileRepository.findOne(id);
  }



}
