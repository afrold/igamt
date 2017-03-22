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
import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.PathGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponentAttributes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.PathGroupService;

@Service

public class PathGroupServiceImpl implements PathGroupService {

  @Override
  public List<PathGroup> buildPathGroups(Message coreMessage, List<ProfileComponent> pcs) {
    List<PathGroup> pathGroups = new ArrayList<>();

    for (ProfileComponent pc : pcs) {
      List<SubProfileComponent> toRemove = new ArrayList<>();
      List<SubProfileComponent> toAdd = new ArrayList<>();
      for (SubProfileComponent subPc : pc.getChildren()) {
        if (!subPc.getPath().startsWith(coreMessage.getStructID())) {
          String[] elements = subPc.getPath().split("\\.", 2);
          String segName = elements[0];
          String segPath = coreMessage.getStructID();
          List<SubProfileComponent> newSubPcs =
              addMultipleFromSegmentName(subPc, coreMessage.getChildren(), segName, segPath);
          toRemove.add(subPc);
          toAdd.addAll(newSubPcs);
          System.out.println("MULtiple: " + subPc.getPath());
        } else {
          System.out
              .println("pc : " + pc.getName() + " Usage : " + subPc.getAttributes().getUsage());

        }

      }
      pc.getChildren().removeAll(toRemove);
      pc.getChildren().addAll(toAdd);
    }



    for (ProfileComponent pc : pcs) {
      for (SubProfileComponent subPc : pc.getChildren()) {
        add(pathGroups, subPc.getPath(), subPc.getAttributes());


      }
    }

    return pathGroups;
  }



  private List<SubProfileComponent> addMultipleFromSegmentName(SubProfileComponent subPc,
      List<SegmentRefOrGroup> children, String segLabel, String path) {
    List<SubProfileComponent> result = new ArrayList<>();
    for (SegmentRefOrGroup child : children) {
      if (child instanceof Group) {
        Group grp = (Group) child;
        String p = path + "." + grp.getPosition();
        result.addAll(addMultipleFromSegmentName(subPc, grp.getChildren(), segLabel, p));
      } else if (child instanceof SegmentRef) {
        SegmentRef segRef = (SegmentRef) child;
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
