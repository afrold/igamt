/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 14, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Comment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.PathGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SingleElementValue;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponentAttributes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponentComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.PathGroupService;

@Service

public class PathGroupServiceImpl implements PathGroupService {

  @Override
  public List<PathGroup> buildPathGroups(Message coreMessage, List<ProfileComponent> pcs,
      Map<String, Segment> segmentsMap) {
    List<PathGroup> pathGroups = new ArrayList<>();

    for (ProfileComponent pc : pcs) {
      List<SubProfileComponent> toRemove = new ArrayList<>();
      List<SubProfileComponent> toAdd = new ArrayList<>();
      for (SubProfileComponent subPc : pc.getChildren()) {
        if (!subPc.getPath().startsWith(coreMessage.getStructID())) {
          String[] elements = subPc.getPath().split("\\.", 2);
          String segName = elements[0];
          String segPath = coreMessage.getStructID();
          List<SubProfileComponent> newSubPcs = addMultipleFromSegmentName(subPc,
              coreMessage.getChildren(), segName, segPath, segmentsMap);
          toRemove.add(subPc);
          toAdd.addAll(newSubPcs);
        } else {


        }

      }
      pc.getChildren().removeAll(toRemove);
      pc.getChildren().addAll(toAdd);
    }
    for (ProfileComponent p : pcs) {
      for (SubProfileComponent sub : p.getChildren()) {
        List<ValueSetBinding> toRemove = new ArrayList<>();
        List<Comment> commentToRemove = new ArrayList<>();

        if (!sub.getPath().startsWith(coreMessage.getStructID())) {

        } else {
          if (sub.getValueSetBindings() != null) {
            for (ValueSetBinding v : sub.getValueSetBindings()) {
              for (ValueSetBinding vsb : coreMessage.getValueSetBindings()) {
                if (v.getLocation().equals(vsb.getLocation())) {
                  toRemove.add(vsb);
                }
              }
            }
            coreMessage.getValueSetBindings().removeAll(toRemove);
            coreMessage.getValueSetBindings().addAll(sub.getValueSetBindings());


          }
          if (sub.getComments() != null) {
            for (Comment v : sub.getComments()) {
              for (Comment vsb : coreMessage.getComments()) {
                if (v.getLocation().equals(vsb.getLocation())) {
                  commentToRemove.add(vsb);
                }
              }
            }
            coreMessage.getComments().removeAll(commentToRemove);
            coreMessage.getComments().addAll(sub.getComments());


          }


          if (sub.getSingleElementValues() != null
              && sub.getSingleElementValues().getName() != null) {
            boolean sevExist = false;
            for (SingleElementValue sev : coreMessage.getSingleElementValues()) {
              if (sev.getLocation().equals(sub.getSingleElementValues().getLocation())) {
                sevExist = true;
                sev.setValue(sub.getSingleElementValues().getValue());

              }

            }
            if (!sevExist) {
              coreMessage.getSingleElementValues().add(sub.getSingleElementValues());
            }
          }

        }
      }
    }



    for (ProfileComponent pc : pcs) {
      // order subPcs
      SubProfileComponentComparator Comp = new SubProfileComponentComparator();
      Collections.sort(pc.getChildren(), Comp);
      for (SubProfileComponent subPc : pc.getChildren()) {
        add(pathGroups, subPc.getPath(), subPc.getAttributes());


      }
    }

    return pathGroups;
  }



  private List<SubProfileComponent> addMultipleFromSegmentName(SubProfileComponent subPc,
      List<SegmentRefOrGroup> children, String segLabel, String path,
      Map<String, Segment> segmentsMap) {
    List<SubProfileComponent> result = new ArrayList<>();
    for (SegmentRefOrGroup child : children) {
      if (child instanceof Group) {
        Group grp = (Group) child;
        String p = path + "." + grp.getPosition();
        result
            .addAll(addMultipleFromSegmentName(subPc, grp.getChildren(), segLabel, p, segmentsMap));
      } else if (child instanceof SegmentRef) {
        SegmentRef segRef = (SegmentRef) child;
        Segment seg = segmentsMap.get(segRef.getRef().getId());
        List<ValueSetBinding> toRemove = new ArrayList<>();
        if (subPc.getValueSetBindings() != null) {
          for (ValueSetBinding v : subPc.getValueSetBindings()) {
            for (ValueSetBinding vsb : seg.getValueSetBindings()) {
              if (v.getLocation().equals(vsb.getLocation())) {
                toRemove.add(vsb);
              }
            }
          }
          seg.getValueSetBindings().removeAll(toRemove);
          seg.getValueSetBindings().addAll(subPc.getValueSetBindings());


        }
        List<Comment> commentToRemove = new ArrayList<>();
        if (subPc.getComments() != null) {
          for (Comment v : subPc.getComments()) {
            for (Comment vsb : seg.getComments()) {
              if (v.getLocation().equals(vsb.getLocation())) {
                commentToRemove.add(vsb);
              }
            }
          }
          seg.getComments().removeAll(commentToRemove);
          seg.getComments().addAll(subPc.getComments());


        }
        if (subPc.getSingleElementValues() != null
            && subPc.getSingleElementValues().getLocation() != null) {
          boolean sevExist = false;
          for (SingleElementValue sev : seg.getSingleElementValues()) {
            if (sev.getLocation().equals(subPc.getSingleElementValues().getLocation())) {
              sevExist = true;
              sev.setValue(subPc.getSingleElementValues().getValue());
            }
          }
          if (!sevExist) {
            seg.getSingleElementValues().add(subPc.getSingleElementValues());
          }
        }
        String p = path + "." + segRef.getPosition();
        if (segRef.getRef().getLabel().equals(segLabel)) {
          SubProfileComponent sub = new SubProfileComponent();
          sub.setAttributes(subPc.getAttributes());
          sub.setName(subPc.getName());
          sub.setPosition(subPc.getPosition());
          sub.setType(subPc.getType());
          sub.setPath(subPc.getPath().replace(segLabel, p));
          result.add(sub);
        }
      }
    }
    return result;
  }



  private void add(List<PathGroup> pathGroups, String path,
      SubProfileComponentAttributes attributes) {
    for (PathGroup pathGroup : pathGroups) {
      if (path.startsWith(pathGroup.getPath() + ".")) {
        add(pathGroup.getChildren(), path.replace(pathGroup.getPath() + ".", ""), attributes);
        return;
      } else if (path.equals(pathGroup.getPath())) {
        pathGroup.add(attributes);
        return;
      }
    }
    PathGroup grp = new PathGroup();
    createHierarchy(grp, path, attributes);
    pathGroups.add(grp);
  }

  private void createHierarchy(PathGroup cursor, String path,
      SubProfileComponentAttributes attributes) {
    if (path.contains(".") && !path.endsWith(".")) {

      String[] elements = path.split("\\.", 2);
      String pre = elements[0];
      String post = elements[1];
      cursor.setPath(pre);
      List<PathGroup> list = new ArrayList<>();
      PathGroup pg = new PathGroup();
      list.add(pg);
      cursor.setChildren(list);
      createHierarchy(pg, post, attributes);
    } else {
      cursor.setPath(path);
      cursor.add(attributes);
      cursor.setChildren(new ArrayList<PathGroup>());
    }
  }



}
