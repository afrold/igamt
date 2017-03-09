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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.CompositeProfileRepository;
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
  @Autowired
  CompositeProfileRepository compositeProfileRepository;


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
    compositeProfile.setCoreProfileId(compositeProfileStructure.getCoreProfileId());
    compositeProfile.setProfileComponentsInfo(compositeProfileStructure.getProfileComponentsInfo());
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
      String path = core.getStructID();
      if (segRefOrGrp.getType().equalsIgnoreCase(Constant.SEGMENTREF)) {
        SegmentRef segRef = (SegmentRef) segRefOrGrp;
        path = path + "." + segRef.getPosition();
        System.out.println("-------");
        System.out.println(path);
        buildSegmentRef((SegmentRef) segRefOrGrp, path, itemsMap, segmentflavorsMap,
            datatypeflavorsMap, segmentsMap, datatypesMap, tablesMap);
      } else if (segRefOrGrp.getType().equalsIgnoreCase(Constant.GROUP)) {
        Group grp = (Group) segRefOrGrp;
        path = path + "." + grp.getPosition();
        buildGroup((Group) segRefOrGrp, path, itemsMap, segmentflavorsMap, datatypeflavorsMap,
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



  private void buildGroup(Group group, String path, Map<String, List<SubProfileComponent>> itemsMap,
      Map<String, Segment> segmentflavorsMap, Map<String, Datatype> datatypeflavorsMap,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) {

    if (itemsMap.containsKey(group.getId())) {
      for (SubProfileComponent subPc : itemsMap.get(group.getId())) {
        if (subPc.getPath().equals(path)) {
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
    }
    for (SegmentRefOrGroup segRefOrGrp : group.getChildren()) {
      if (segRefOrGrp.getType().equalsIgnoreCase(Constant.SEGMENTREF)) {
        SegmentRef segRef = (SegmentRef) segRefOrGrp;
        path = path + "." + segRef.getPosition();
        buildSegmentRef((SegmentRef) segRefOrGrp, path, itemsMap, segmentflavorsMap,
            datatypeflavorsMap, segmentsMap, datatypesMap, tablesMap);
      } else if (segRefOrGrp.getType().equalsIgnoreCase(Constant.GROUP)) {
        Group grp = (Group) segRefOrGrp;
        path = path + "." + grp.getPosition();
        buildGroup((Group) segRefOrGrp, path, itemsMap, segmentflavorsMap, datatypeflavorsMap,
            segmentsMap, datatypesMap, tablesMap);
      }
    }

  }

  public void buildSegmentRef(SegmentRef segRef, String path,
      Map<String, List<SubProfileComponent>> itemsMap, Map<String, Segment> segmentflavorsMap,
      Map<String, Datatype> datatypeflavorsMap, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {

    if (itemsMap.containsKey(segRef.getId())) {
      // change values with pc Item Value
      for (SubProfileComponent subPc : itemsMap.get(segRef.getId())) {
        if (subPc.getPath().equals(path)) {
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


    }

    Segment seg = segmentsMap.get(segRef.getRef().getId());
    System.out.println("0000000");
    System.out.println(path);
    buildSegment(segRef, path, seg, itemsMap, segmentflavorsMap, datatypeflavorsMap, segmentsMap,
        datatypesMap, tablesMap);
  }


  public void buildSegment(SegmentRef segRef, String path, Segment segment,
      Map<String, List<SubProfileComponent>> itemsMap, Map<String, Segment> segmentflavorsMap,
      Map<String, Datatype> datatypeflavorsMap, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {



    for (int i = 0; i < segment.getFields().size(); i++) {

      if (itemsMap.containsKey(segment.getFields().get(i).getId())) {
        path = path + "." + segment.getFields().get(i).getPosition();

        // change values with pc item value
        // buildDatatype

        // create or update seg flavor in segmentflavorsMap
        if (segmentflavorsMap.containsKey(segment.getId())) {
          // just edit the field without creating a flavor
          // segmentflavorsMap.get(segment.getId()).getFields().get(i)

          for (SubProfileComponent subPc : itemsMap.get(segment.getFields().get(i).getId())) {

            if (subPc.getPath().equals(path)) {
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
            }

            Datatype dt = datatypesMap.get(
                segmentflavorsMap.get(segment.getId()).getFields().get(i).getDatatype().getId());

            buildDatatypeFromField(segmentflavorsMap.get(segment.getId()).getFields().get(i), path,
                dt, datatypesMap, datatypeflavorsMap, itemsMap);
          }

        } else {
          // create segment flavor, add it to flavors map and change fields
          Segment segmentFlavor = segment;
          segmentFlavor.setExt("pc");
          segmentFlavor.setId(ObjectId.get().toString());
          segmentFlavor.setScope(SCOPE.USER);
          segmentflavorsMap.put(segmentFlavor.getId(), segmentFlavor);

          for (SubProfileComponent subPc : itemsMap.get(segmentFlavor.getFields().get(i).getId())) {

            if (subPc.getPath().equals(path)) {
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
            }

            Datatype dt = datatypesMap.get(segmentflavorsMap.get(segmentFlavor.getId()).getFields()
                .get(i).getDatatype().getId());

            buildDatatypeFromField(segmentflavorsMap.get(segmentFlavor.getId()).getFields().get(i),
                path, dt, datatypesMap, datatypeflavorsMap, itemsMap);
          }

        }

      } else {
        Datatype dt = datatypesMap.get(segment.getFields().get(i).getDatatype().getId());
        // buildDatatypeFromField(segment.getFields().get(i), dt, datatypesMap, datatypeflavorsMap,
        // itemsMap);
        buildDatatypeFromHl7Field(segment.getFields().get(i), path, dt, segRef, segment,
            segmentsMap, segmentflavorsMap, datatypesMap, datatypeflavorsMap, itemsMap);
      }

    }


  }



  private void buildDatatypeFromHl7Field(Field field, String path, Datatype dt, SegmentRef segRef,
      Segment segment, Map<String, Segment> segmentsMap, Map<String, Segment> segmentflavorsMap,
      Map<String, Datatype> datatypesMap, Map<String, Datatype> datatypeflavorsMap,
      Map<String, List<SubProfileComponent>> itemsMap) {

    for (int i = 0; i < dt.getComponents().size(); i++) {

      if (itemsMap.containsKey(dt.getComponents().get(i).getId())) {
        path = path + "." + dt.getComponents().get(i).getPosition();

        if (datatypeflavorsMap.containsKey(dt.getId())) {

          for (SubProfileComponent subPc : itemsMap.get(dt.getComponents().get(i).getId())) {
            if (subPc.getPath().equals(path)) {
              if (subPc.getAttributes().getComment() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setComment(subPc.getAttributes().getComment());
              }


              if (subPc.getAttributes().getUsage() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setUsage(subPc.getAttributes().getUsage());
              }
              if (subPc.getAttributes().getConfLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setConfLength(subPc.getAttributes().getConfLength());
              }
              if (subPc.getAttributes().getMaxLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setMaxLength(subPc.getAttributes().getMaxLength());
              }
              if (subPc.getAttributes().getMinLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setMinLength(subPc.getAttributes().getMinLength());
              }
              if (segmentflavorsMap.containsKey(segment.getId())) {
                field.getDatatype().setId(dt.getId());
                field.getDatatype().setName(dt.getName());
                field.getDatatype().setExt(dt.getExt());
              } else {
                Segment segmentFlavor = segment;
                segmentFlavor.setExt("pc");
                segmentFlavor.setId(ObjectId.get().toString());
                segmentFlavor.setScope(SCOPE.USER);
                segmentflavorsMap.put(segmentFlavor.getId(), segmentFlavor);
                field.getDatatype().setId(dt.getId());
                field.getDatatype().setName(dt.getName());
                field.getDatatype().setExt(dt.getExt());
                segRef.getRef().setId(segmentFlavor.getId());
                segRef.getRef().setName(segmentFlavor.getName());
                segRef.getRef().setExt(segmentFlavor.getExt());
              }
            }

            buildDatatypeFromComponent(datatypeflavorsMap.get(dt.getId()).getComponents().get(i),
                path, datatypesMap, datatypeflavorsMap, itemsMap);
          }


        } else {
          Datatype dtFlavor = dt;
          dtFlavor.setExt("pc");
          dtFlavor.setId(ObjectId.get().toString());
          dtFlavor.setScope(SCOPE.USER);
          datatypeflavorsMap.put(dtFlavor.getId(), dtFlavor);

          for (SubProfileComponent subPc : itemsMap.get(dtFlavor.getComponents().get(i).getId())) {
            if (subPc.getPath().equals(path)) {
              if (subPc.getAttributes().getComment() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setComment(subPc.getAttributes().getComment());
              }

              if (subPc.getAttributes().getUsage() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setUsage(subPc.getAttributes().getUsage());
              }
              if (subPc.getAttributes().getConfLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setConfLength(subPc.getAttributes().getConfLength());
              }
              if (subPc.getAttributes().getMaxLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setMaxLength(subPc.getAttributes().getMaxLength());
              }
              if (subPc.getAttributes().getMinLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setMinLength(subPc.getAttributes().getMinLength());
              }
              if (segmentflavorsMap.containsKey(segment.getId())) {
                field.getDatatype().setId(dtFlavor.getId());
                field.getDatatype().setName(dtFlavor.getName());
                field.getDatatype().setExt(dtFlavor.getExt());
              } else {
                Segment segmentFlavor = segment;
                segmentFlavor.setExt("pc");
                segmentFlavor.setId(ObjectId.get().toString());
                segmentFlavor.setScope(SCOPE.USER);
                segmentflavorsMap.put(segmentFlavor.getId(), segmentFlavor);
                field.getDatatype().setId(dtFlavor.getId());
                field.getDatatype().setName(dtFlavor.getName());
                field.getDatatype().setExt(dtFlavor.getExt());
                segRef.getRef().setId(segmentFlavor.getId());
                segRef.getRef().setName(segmentFlavor.getName());
                segRef.getRef().setExt(segmentFlavor.getExt());
              }
            }

            buildDatatypeFromComponent(
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i), path, datatypesMap,
                datatypeflavorsMap, itemsMap);
          }


        }
      }
    }


  }

  private void buildDatatypeFromField(Field field, String path, Datatype dt,
      Map<String, Datatype> datatypesMap, Map<String, Datatype> datatypeflavorsMap,
      Map<String, List<SubProfileComponent>> itemsMap) {



    for (int i = 0; i < dt.getComponents().size(); i++) {

      if (itemsMap.containsKey(dt.getComponents().get(i).getId())) {
        path = path + "." + dt.getComponents().get(i).getPosition();
        if (datatypeflavorsMap.containsKey(dt.getId())) {
          // just edit the field without creating a flavor
          // segmentflavorsMap.get(segment.getId()).getFields().get(i)
          for (SubProfileComponent subPc : itemsMap.get(dt.getComponents().get(i).getId())) {
            if (subPc.getPath().equals(path)) {
              if (subPc.getAttributes().getComment() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setComment(subPc.getAttributes().getComment());
              }


              if (subPc.getAttributes().getUsage() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setUsage(subPc.getAttributes().getUsage());
              }
              if (subPc.getAttributes().getConfLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setConfLength(subPc.getAttributes().getConfLength());
              }
              if (subPc.getAttributes().getMaxLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setMaxLength(subPc.getAttributes().getMaxLength());
              }
              if (subPc.getAttributes().getMinLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setMinLength(subPc.getAttributes().getMinLength());
              }
            }

            buildDatatypeFromComponent(datatypeflavorsMap.get(dt.getId()).getComponents().get(i),
                path, datatypesMap, datatypeflavorsMap, itemsMap);
          }

        } else {
          // create segment flavor, add it to flavors map and change fields
          Datatype dtFlavor = dt;
          dtFlavor.setExt("pc");
          dtFlavor.setId(ObjectId.get().toString());
          dtFlavor.setScope(SCOPE.USER);
          datatypeflavorsMap.put(dtFlavor.getId(), dtFlavor);

          for (SubProfileComponent subPc : itemsMap.get(dtFlavor.getComponents().get(i).getId())) {
            if (subPc.getPath().equals(path)) {
              if (subPc.getAttributes().getComment() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setComment(subPc.getAttributes().getComment());
              }

              if (subPc.getAttributes().getUsage() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setUsage(subPc.getAttributes().getUsage());
              }
              if (subPc.getAttributes().getConfLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setConfLength(subPc.getAttributes().getConfLength());
              }
              if (subPc.getAttributes().getMaxLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setMaxLength(subPc.getAttributes().getMaxLength());
              }
              if (subPc.getAttributes().getMinLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setMinLength(subPc.getAttributes().getMinLength());
              }
              field.getDatatype().setId(dtFlavor.getId());
              field.getDatatype().setExt(dtFlavor.getExt());
              field.getDatatype().setName(dtFlavor.getName());
            }

            buildDatatypeFromComponent(
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i), path, datatypesMap,
                datatypeflavorsMap, itemsMap);
          }

        }
      }
    }

  }

  private void buildDatatypeFromComponent(Component component, String path,
      Map<String, Datatype> datatypesMap, Map<String, Datatype> datatypeflavorsMap,
      Map<String, List<SubProfileComponent>> itemsMap) {


    Datatype dt = datatypesMap.get(component.getDatatype().getId());
    for (int i = 0; i < dt.getComponents().size(); i++) {

      if (itemsMap.containsKey(dt.getComponents().get(i).getId())) {
        path = path + "." + dt.getComponents().get(i).getPosition();
        if (datatypeflavorsMap.containsKey(dt.getId())) {
          // just edit the field without creating a flavor
          // segmentflavorsMap.get(segment.getId()).getFields().get(i)
          for (SubProfileComponent subPc : itemsMap.get(dt.getComponents().get(i).getId())) {
            if (subPc.getPath().equals(path)) {
              if (subPc.getAttributes().getComment() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setComment(subPc.getAttributes().getComment());
              }


              if (subPc.getAttributes().getUsage() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setUsage(subPc.getAttributes().getUsage());
              }
              if (subPc.getAttributes().getConfLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setConfLength(subPc.getAttributes().getConfLength());
              }
              if (subPc.getAttributes().getMaxLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setMaxLength(subPc.getAttributes().getMaxLength());
              }
              if (subPc.getAttributes().getMinLength() != null) {
                datatypeflavorsMap.get(dt.getId()).getComponents().get(i)
                    .setMinLength(subPc.getAttributes().getMinLength());
              }
            }

            buildDatatypeFromComponent(datatypeflavorsMap.get(dt.getId()).getComponents().get(i),
                path, datatypesMap, datatypeflavorsMap, itemsMap);
          }

        } else {
          Datatype dtFlavor = dt;
          dtFlavor.setExt("pc");
          dtFlavor.setId(ObjectId.get().toString());
          dtFlavor.setScope(SCOPE.USER);
          datatypeflavorsMap.put(dtFlavor.getId(), dtFlavor);

          for (SubProfileComponent subPc : itemsMap.get(dtFlavor.getComponents().get(i).getId())) {
            if (subPc.getPath().equals(path)) {
              if (subPc.getAttributes().getComment() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setComment(subPc.getAttributes().getComment());
              }

              if (subPc.getAttributes().getUsage() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setUsage(subPc.getAttributes().getUsage());
              }
              if (subPc.getAttributes().getConfLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setConfLength(subPc.getAttributes().getConfLength());
              }
              if (subPc.getAttributes().getMaxLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setMaxLength(subPc.getAttributes().getMaxLength());
              }
              if (subPc.getAttributes().getMinLength() != null) {
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i)
                    .setMinLength(subPc.getAttributes().getMinLength());
              }
              component.getDatatype().setId(dtFlavor.getId());
              component.getDatatype().setExt(dtFlavor.getExt());
              component.getDatatype().setName(dtFlavor.getName());
            }
            buildDatatypeFromComponent(
                datatypeflavorsMap.get(dtFlavor.getId()).getComponents().get(i), path, datatypesMap,
                datatypeflavorsMap, itemsMap);


          }

        }
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
