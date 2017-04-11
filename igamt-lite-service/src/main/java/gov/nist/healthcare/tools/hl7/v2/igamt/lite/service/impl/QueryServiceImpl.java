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

import java.util.Map;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.QueryService;
import javassist.NotFoundException;

@Service
public class QueryServiceImpl implements QueryService {

  private Map<String, Segment> segmentsMap;
  private Map<String, Datatype> datatypesMap;


  @Override
  public DataModel get(DataModel context, String path) throws NotFoundException {
    String post = "";
    if (path.isEmpty()) {
      return context;
    } else {
      String[] elements = path.split("\\.", 2);
      String pre = elements[0];
      if (elements.length > 1) {
        post = elements[1];
      }
      if (context instanceof Group) {
        Group grp = (Group) context;
        DataModel dm = getFromGroup(grp, pre);
        return get(dm, post);
      } else if (context instanceof SegmentRef) {

        SegmentRef segReg = (SegmentRef) context;
        DataModel dm = getFromSegmentRef(segReg, pre);
        return get(dm, post);
      } else if (context instanceof Field) {

        Field f = (Field) context;
        DataModel dm = getFromField(f, pre);
        return get(dm, post);
      } else if (context instanceof Component) {

        Component c = (Component) context;
        DataModel dm = getFromComponent(c, pre);
        return get(dm, post);
      } else if (context instanceof CompositeProfile) {
        Group grp = new Group();
        CompositeProfile cp = (CompositeProfile) context;
        grp.setName(cp.getStructID());
        grp.setChildren(cp.getChildren());
        DataModel dm = getFromGroup(grp, pre);
        return get(dm, post);
      } else if (context instanceof Message) {
        Group grp = new Group();
        Message cp = (Message) context;
        grp.setName(cp.getStructID());
        grp.setChildren(cp.getChildren());
        DataModel dm = getFromGroup(grp, pre);
        return get(dm, post);
      }

    }
    throw new NotFoundException(path);

  }



  private DataModel getFromComponent(Component component, String path) throws NotFoundException {
    Integer pos = checkPath(path);
    Datatype datatype = datatypesMap.get(component.getDatatype().getId());
    if (pos != null) {
      for (Component c : datatype.getComponents()) {
        if (c.getPosition().equals(pos)) {
          return c;
        }
      }
    }
    throw new NotFoundException(path);

  }



  private DataModel getFromField(Field field, String path) throws NotFoundException {
    Integer pos = checkPath(path);
    Datatype datatype = datatypesMap.get(field.getDatatype().getId());
    if (pos != null) {
      for (Component c : datatype.getComponents()) {
        if (c.getPosition().equals(pos)) {
          return c;
        }
      }
    }
    throw new NotFoundException(path);

  }

  private DataModel getFromSegmentRef(SegmentRef segmentRef, String path) throws NotFoundException {

    Integer pos = checkPath(path);
    Segment segment = segmentsMap.get(segmentRef.getRef().getId());
    if (pos != null) {
      for (Field f : segment.getFields()) {
        if (f.getPosition().equals(pos)) {
          return f;
        }
      }
    }
    throw new NotFoundException(path);
  }

  private DataModel getFromGroup(Group group, String path) throws NotFoundException {

    Integer pos = checkPath(path);
    if (pos != null) {
      for (SegmentRefOrGroup segRefOrGrp : group.getChildren()) {
        if (segRefOrGrp.getPosition().equals(pos)) {
          return segRefOrGrp;
        }
      }
    } else {
      for (SegmentRefOrGroup segRefOrGrp : group.getChildren()) {
        if (segRefOrGrp instanceof SegmentRef) {
          SegmentRef segRef = (SegmentRef) segRefOrGrp;
          if (segRef.getRef().getLabel().equals(path)) {
            return segRef;
          }
        } else {
          Group grp = (Group) segRefOrGrp;
          if (grp.getName().equals(path)) {
            return grp;
          }
        }

      }
    }
    throw new NotFoundException(path);

  }

  private Integer checkPath(String path) {
    try {
      Integer pos = Integer.parseInt(path);
      return pos;
    } catch (Exception e) {
      return null;
    }


  }

  @Override
  public Map<String, Segment> getSegmentsMap() {
    return segmentsMap;
  }

  @Override
  public void setSegmentsMap(Map<String, Segment> segmentsMap) {
    this.segmentsMap = segmentsMap;
  }

  @Override
  public Map<String, Datatype> getDatatypesMap() {
    return datatypesMap;
  }

  @Override
  public void setDatatypesMap(Map<String, Datatype> datatypesMap) {
    this.datatypesMap = datatypesMap;
  }



}
