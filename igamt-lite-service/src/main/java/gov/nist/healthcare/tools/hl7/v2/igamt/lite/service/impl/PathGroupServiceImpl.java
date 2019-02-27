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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Comment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator.SubProfileComponentComparator;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.PathGroupService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.QueryService;
import javassist.NotFoundException;

@Service

public class PathGroupServiceImpl implements PathGroupService {

  @Autowired
  QueryService queryService;

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
          List<SubProfileComponent> newSubPcs = addMultipleFromSegmentId(subPc,
              coreMessage.getChildren(), segPath, segmentsMap,subPc.getSource().getSegmentId());
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
        List<ValueSetOrSingleCodeBinding> toRemove = new ArrayList<>();
        List<Comment> commentToRemove = new ArrayList<>();

        if (!sub.getPath().startsWith(coreMessage.getStructID())) {

        } else {
          if (sub.getPath().equals(coreMessage.getStructID())) {
            if (sub.getAttributes().getConformanceStatements() != null) {
              coreMessage.setConformanceStatements(sub.getAttributes().getConformanceStatements());
            }
          }
          if (sub.getValueSetBindings() != null) {
            for (ValueSetOrSingleCodeBinding v : sub.getValueSetBindings()) {
              for (ValueSetOrSingleCodeBinding vsb : coreMessage.getValueSetBindings()) {
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
          if (sub.getAttributes().getPredicate() != null) {
            Predicate pred = sub.getAttributes().getPredicate();
            boolean predExist = false;
            for (Predicate predicate : coreMessage.getPredicates()) {
              if (predicate.getConstraintTarget().equals(pred.getConstraintTarget())
                  && pred.getContext().getPath() == null) {
                predExist = true;
                predicate.setAssertion(pred.getAssertion());
                predicate.setConstraintClassification(pred.getConstraintClassification());
                predicate.setConstraintId(pred.getConstraintId());
                predicate.setDescription(pred.getDescription());
                predicate.setFalseUsage(pred.getFalseUsage());
                predicate.setReference(pred.getReference());
                predicate.setTrueUsage(pred.getTrueUsage());
                predicate.setContext(pred.getContext());
              }
            }
            if (!predExist) {
              if (pred.getContext().getPath() == null) {
                coreMessage.getPredicates().add(pred);
              } else {

                try {
                  DataModel dm = queryService.get(coreMessage, pred.getContext().getPath());
                  System.out.println(dm.getType());
                  if (dm instanceof Group) {
                    Group grp = (Group) dm;

                    boolean grpPredExist = false;
                    for (Predicate pr : grp.getPredicates()) {

                      if (refinePath(pr.getConstraintTarget())
                          .equals(sub.getPath().substring(sub.getPath().indexOf('.') + 1)
                              .replace((pred.getContext().getPath() + '.'), ""))) {
                        grpPredExist = true;
                        pr.setAssertion(pred.getAssertion());
                        pr.setConstraintClassification(pred.getConstraintClassification());
                        pr.setConstraintId(pred.getConstraintId());
                        pr.setDescription(pred.getDescription());
                        pr.setFalseUsage(pred.getFalseUsage());
                        pr.setReference(pred.getReference());
                        pr.setTrueUsage(pred.getTrueUsage());
                        pr.setContext(pred.getContext());
                      }
                    }
                    if (!grpPredExist) {
                      grp.addPredicate(pred);
                    }
                  }
                } catch (NotFoundException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }

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
        if (!subPc.getComments().isEmpty()) {

          // subPc.getAttributes().setComments(subPc.getComments());
        }
        add(pathGroups, subPc.getPath(), subPc.getAttributes());
      }

    }

    return pathGroups;
  }

  private String refinePath(String instancePath) {
    String[] pathArray = null;
    if (!instancePath.isEmpty()) {
      pathArray = instancePath.split("\\.");
    }
    String positionPath = new String();
    for (String s : pathArray) {
      String position = s.split("\\[")[0];
      positionPath = positionPath + '.' + position;

    }
    if (!positionPath.isEmpty()) {
      positionPath = positionPath.substring(1);

    }
    return positionPath;
  }



  private List<SubProfileComponent> addMultipleFromSegmentId(SubProfileComponent subPc,
      List<SegmentRefOrGroup> children, String path,
      Map<String, Segment> segmentsMap, String segId) {
	  
    List<SubProfileComponent> result = new ArrayList<>();
    for (SegmentRefOrGroup child : children) {
      if (child instanceof Group) {
        Group grp = (Group) child;
        String p = path + "." + grp.getPosition();
        result
            .addAll(addMultipleFromSegmentId(subPc, grp.getChildren(), p, segmentsMap,segId));
      } else if (child instanceof SegmentRef) {
        SegmentRef segRef = (SegmentRef) child;
        Segment seg = segmentsMap.get(segRef.getRef().getId());
        String name=seg.getName();
        if(name.equals("PID")){
        	System.out.println("PID");
        }
        
        if (segRef.getRef().getId().equals(segId)) {

          List<ValueSetOrSingleCodeBinding> toRemove = new ArrayList<>();
          if (subPc.getValueSetBindings() != null ) {
            for (ValueSetOrSingleCodeBinding v : subPc.getValueSetBindings()) {
              for (ValueSetOrSingleCodeBinding vsb : seg.getValueSetBindings()) {
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
          if (subPc.getAttributes().getPredicate() != null) {

            Predicate pred = subPc.getAttributes().getPredicate();
            boolean predExist = false;
            for (Predicate predicate : seg.getPredicates()) {
              if (predicate.getConstraintTarget().equals(pred.getConstraintTarget())) {
                predExist = true;
                predicate.setAssertion(pred.getAssertion());
                predicate.setConstraintClassification(pred.getConstraintClassification());
                predicate.setConstraintId(pred.getConstraintId());
                predicate.setDescription(pred.getDescription());
                predicate.setFalseUsage(pred.getFalseUsage());
                predicate.setReference(pred.getReference());
                predicate.setTrueUsage(pred.getTrueUsage());
              }
            }
            if (!predExist) {
              System.out.println(subPc.getPath());
              System.out.println(seg.getName());
              seg.getPredicates().add(pred);
            }

          }
            String p = path + "." + segRef.getPosition();
            SubProfileComponent sub = new SubProfileComponent();
            sub.setAttributes(subPc.getAttributes());
            List<Comment> comments = subPc.getComments();
            sub.setComments(comments);
            sub.setName(subPc.getName());
            sub.setPosition(subPc.getPosition());
            sub.setType(subPc.getType());
            sub.setPath(subPc.getPath().replace(seg.getLabel(), p));
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
        add(pathGroup.getChildren(), path.replaceFirst(pathGroup.getPath() + ".", ""), attributes);
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
