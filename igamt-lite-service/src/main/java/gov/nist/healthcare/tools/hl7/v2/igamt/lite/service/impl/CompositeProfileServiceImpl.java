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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ApplyInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;

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


  @Override
  public CompositeProfile buildCompositeProfile(
      CompositeProfileStructure compositeProfileStructure) {
    Message coreMessage = messageService.findById(compositeProfileStructure.getCoreProfileId());
    List<String> pcIds = new ArrayList<String>();
    for (ApplyInfo pc : compositeProfileStructure.getProfileComponentsInfo()) {
      pcIds.add(pc.getId());
    }
    List<ProfileComponent> pcs = profileComponentService.findByIds(pcIds);



    CompositeProfile compositeProfile = build(coreMessage, pcs);
    compositeProfile.setName(compositeProfileStructure.getName());
    compositeProfile.setDescription(compositeProfileStructure.getDescription());
    return compositeProfile;
  }

  @Override
  public CompositeProfile build(Message core, List<ProfileComponent> pcs) {
    Set<String> segIds = new HashSet<String>();
    Set<String> dtIds = new HashSet<String>();
    findSegmentsId(core.getChildren(), segIds);
    List<Segment> segments = segmentService.findByIds(segIds);
    findDtsIdFromSegs(segments, dtIds);
    List<Datatype> interDatatypes = datatypeService.findByIds(dtIds);
    findDtsIdFromDts(interDatatypes, dtIds);
    List<Datatype> datatypes = datatypeService.findByIds(dtIds);

    Map<String, Segment> segmentsMap = new HashMap<>();
    Map<String, Segment> segmentflavorsMap = new HashMap<>();
    Map<String, Datatype> datatypesMap = new HashMap<>();
    Map<String, Datatype> datatypeflavorsMap = new HashMap<>();

    for (Segment seg : segments) {
      segmentsMap.put(seg.getId(), seg);
    }
    for (Datatype dt : datatypes) {
      datatypesMap.put(dt.getId(), dt);
    }

    Map<String, Table> tablesMap = new HashMap<>();
    Map<String, List<SubProfileComponent>> itemsMap = new HashMap<>();

    // should add the order of pcs to be applied
    for (ProfileComponent pc : pcs) {
      for (SubProfileComponent subPc : pc.getChildren()) {
        if (itemsMap.containsKey(subPc.getItemId())) {
          itemsMap.get(subPc.getItemId()).add(subPc);
        } else {
          List<SubProfileComponent> sub = new ArrayList<>();
          sub.add(subPc);

          itemsMap.put(subPc.getItemId(), sub);
        }
      }
    }

    for (SegmentRefOrGroup segRefOrGrp : core.getChildren()) {
      if (segRefOrGrp.getType().equalsIgnoreCase(Constant.SEGMENTREF)) {
        buildSegmentRef((SegmentRef) segRefOrGrp, itemsMap, segmentflavorsMap, datatypeflavorsMap,
            segmentsMap, datatypesMap, tablesMap);
      } else if (segRefOrGrp.getType().equalsIgnoreCase(Constant.GROUP)) {
        buildGroup((Group) segRefOrGrp, itemsMap, segmentflavorsMap, datatypeflavorsMap,
            segmentsMap, datatypesMap, tablesMap);
      }
    }
    segmentsMap.putAll(segmentflavorsMap);
    datatypesMap.putAll(datatypeflavorsMap);
    CompositeProfile result = new CompositeProfile();

    result.setChildren(core.getChildren());
    result.setDatatypesMap(datatypesMap);
    result.setSegmentsMap(segmentsMap);


    return result;

  }



  private void buildGroup(Group group, Map<String, List<SubProfileComponent>> itemsMap,
      Map<String, Segment> segmentflavorsMap, Map<String, Datatype> datatypeflavorsMap,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) {

    if (itemsMap.containsKey(group.getId())) {
      for (SubProfileComponent subPc : itemsMap.get(group.getId())) {
        if (subPc.getAttributes().getComment() != null) {
          group.setComment(subPc.getAttributes().getComment());
        }
        if (subPc.getAttributes().getMax() != null) {
          group.setMax(subPc.getAttributes().getMax());
        }
        if (subPc.getAttributes().getMin() != null) {
          group.setMin(subPc.getAttributes().getMin());
        }
        if (subPc.getAttributes().getUsage() != null) {
          group.setUsage(subPc.getAttributes().getUsage());
        }
      }
    }
    for (SegmentRefOrGroup segRefOrGrp : group.getChildren()) {
      if (segRefOrGrp.getType().equalsIgnoreCase(Constant.SEGMENTREF)) {
        buildSegmentRef((SegmentRef) segRefOrGrp, itemsMap, segmentflavorsMap, datatypeflavorsMap,
            segmentsMap, datatypesMap, tablesMap);
      } else if (segRefOrGrp.getType().equalsIgnoreCase(Constant.GROUP)) {
        buildGroup((Group) segRefOrGrp, itemsMap, segmentflavorsMap, datatypeflavorsMap,
            segmentsMap, datatypesMap, tablesMap);
      }
    }

  }

  public void buildSegmentRef(SegmentRef segRef, Map<String, List<SubProfileComponent>> itemsMap,
      Map<String, Segment> segmentflavorsMap, Map<String, Datatype> datatypeflavorsMap,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) {

    if (itemsMap.containsKey(segRef.getId())) {
      // change values with pc Item Value
      for (SubProfileComponent subPc : itemsMap.get(segRef.getId())) {
        if (subPc.getAttributes().getComment() != null) {
          segRef.setComment(subPc.getAttributes().getComment());
        }
        if (subPc.getAttributes().getMax() != null) {
          segRef.setMax(subPc.getAttributes().getMax());
        }
        if (subPc.getAttributes().getMin() != null) {
          segRef.setMin(subPc.getAttributes().getMin());
        }
        if (subPc.getAttributes().getUsage() != null) {
          segRef.setUsage(subPc.getAttributes().getUsage());
        }
      }


    }

    Segment seg = segmentsMap.get(segRef.getRef().getId());
    buildSegment(segRef, seg, itemsMap, segmentflavorsMap, datatypeflavorsMap, segmentsMap,
        datatypesMap, tablesMap);
  }


  public void buildSegment(SegmentRef segRef, Segment segment,
      Map<String, List<SubProfileComponent>> itemsMap, Map<String, Segment> segmentflavorsMap,
      Map<String, Datatype> datatypeflavorsMap, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    for (int i = 0; i < segment.getFields().size(); i++) {
      if (itemsMap.containsKey(segment.getFields().get(i).getId())) {
        // change values with pc item value
        // buildDatatype

        // create or update seg flavor in segmentflavorsMap
        if (segmentflavorsMap.containsKey(segment.getId())) {
          // just edit the field without creating a flavor
          // segmentflavorsMap.get(segment.getId()).getFields().get(i)
          for (SubProfileComponent subPc : itemsMap.get(segment.getFields().get(i).getId())) {
            if (subPc.getAttributes().getComment() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setComment(subPc.getAttributes().getComment());
            }
            if (subPc.getAttributes().getMax() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setMax(subPc.getAttributes().getMax());
            }
            if (subPc.getAttributes().getMin() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setMin(subPc.getAttributes().getMin());
            }
            if (subPc.getAttributes().getUsage() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setUsage(subPc.getAttributes().getUsage());
            }
            if (subPc.getAttributes().getConfLength() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setConfLength(subPc.getAttributes().getConfLength());
            }
            if (subPc.getAttributes().getMaxLength() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setMaxLength(subPc.getAttributes().getMaxLength());
            }
            if (subPc.getAttributes().getMinLength() != null) {
              segmentflavorsMap.get(segment.getId()).getFields().get(i)
                  .setMinLength(subPc.getAttributes().getMinLength());
            }
            buildDatatypeFromField(segmentflavorsMap.get(segment.getId()).getFields().get(i),
                datatypesMap, datatypeflavorsMap, itemsMap);
          }

        } else {
          // create segment flavor, add it to flavors map and change fields
          Segment segmentFlavor = segment;
          segmentFlavor.setExt("pc");
          segmentFlavor.setId(ObjectId.get().toString());
          segmentFlavor.setScope(SCOPE.USER);
          segmentflavorsMap.put(segmentFlavor.getId(), segmentFlavor);

          for (SubProfileComponent subPc : itemsMap.get(segmentFlavor.getFields().get(i).getId())) {

            if (subPc.getAttributes().getComment() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setComment(subPc.getAttributes().getComment());
            }
            if (subPc.getAttributes().getMax() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setMax(subPc.getAttributes().getMax());
            }
            if (subPc.getAttributes().getMin() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setMin(subPc.getAttributes().getMin());
            }
            if (subPc.getAttributes().getUsage() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setUsage(subPc.getAttributes().getUsage());
            }
            if (subPc.getAttributes().getConfLength() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setConfLength(subPc.getAttributes().getConfLength());
            }
            if (subPc.getAttributes().getMaxLength() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setMaxLength(subPc.getAttributes().getMaxLength());
            }
            if (subPc.getAttributes().getMinLength() != null) {
              segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i)
                  .setMinLength(subPc.getAttributes().getMinLength());
            }
            segRef.getRef().setId(segmentFlavor.getId());
            segRef.getRef().setExt(segmentFlavor.getExt());
            segRef.getRef().setName(segmentFlavor.getName());
            buildDatatypeFromField(segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i),
                datatypesMap, datatypeflavorsMap, itemsMap);
          }

        }
      }
    }


  }



  private void buildDatatypeFromField(Field field, Map<String, Datatype> datatypesMap,
      Map<String, Datatype> datatypeflavorsMap, Map<String, List<SubProfileComponent>> itemsMap) {

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


}
