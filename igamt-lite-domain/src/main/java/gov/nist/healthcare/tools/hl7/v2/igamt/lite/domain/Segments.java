package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;

@Deprecated
public class Segments extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  private String id;

  private Set<Segment> children = new HashSet<Segment>();

  public Segments() {
    super();
    this.id = ObjectId.get().toString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<Segment> getChildren() {
    return children;
  }

  public void setChildren(Set<Segment> children) {
    this.children = children;
  }

  public void addSegment(Segment s) {
    children.add(s);
  }

  public Segment save(Segment s) {
    if (!this.children.contains(s)) {
      children.add(s);
    }
    return s;
  }

  public boolean delete(String id) {
    Segment d = findOneSegmentById(id);
    if (d != null)
      return this.children.remove(d);
    return false;
  }

  public Segment findOneSegmentById(String id) {
    if (this.children != null)
      for (Segment m : this.children) {
        if (m.getId().equals(id)) {
          return m;
        }
      }
    return null;
  }

  public Segment findOneSegmentByNameAndByHl7Version(String name, String hl7Version) {
    if (this.children != null)
      for (Segment s : this.children) {
        if (s.getName().equals(name) && s.getHl7Version().equals(hl7Version)) {
          return s;
        }
      }
    return null;
  }

  public Segment findOneSegmentByName(String name) {
    if (this.children != null)
      for (Segment s : this.children) {
        if (s.getName().equals(name)) {
          return s;
        }
      }
    return null;
  }

  public Segment findOneSegmentByLabel(String label) {
    if (this.children != null)
      for (Segment s : this.children) {
        if (s.getLabel().equals(label)) {
          return s;
        }
      }
    return null;
  }

  public Field findOneField(String id) {
    if (this.children != null) {
      for (Segment m : this.children) {
        Field c = m.findOneField(id);
        if (c != null) {
          return c;
        }
      }
    }
    return null;
  }

  public Component findOneComponent(String id, Datatypes datatypes) {
    if (this.children != null) {
      for (Segment m : this.children) {
        for (Field f : m.getFields()) {
          Component c = datatypes.findOneComponent(f.getDatatype().getId());
          if (c != null) {
            return c;
          }
        }
      }
    }
    return null;
  }

  public Predicate findOnePredicate(String predicateId) {
    for (Segment segment : this.getChildren()) {
      Predicate predicate = segment.findOnePredicate(predicateId);
      if (predicate != null) {
        return predicate;
      }
    }
    return null;
  }

  public ConformanceStatement findOneConformanceStatement(String conformanceStatementId) {
    for (Segment segment : this.getChildren()) {
      ConformanceStatement conf = segment.findOneConformanceStatement(conformanceStatementId);
      if (conf != null) {
        return conf;
      }
    }
    return null;
  }

  public boolean deletePredicate(String predicateId) {
    for (Segment segment : this.getChildren()) {
      if (segment.deletePredicate(predicateId)) {
        return true;
      }
    }
    return false;
  }

  public boolean deleteConformanceStatement(String confStatementId) {
    for (Segment segment : this.getChildren()) {
      if (segment.deleteConformanceStatement(confStatementId)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Segments [id=" + id + "]";
  }

  public Segments clone(HashMap<String, Datatype> dtRecords,
      HashMap<String, Segment> segmentRecords, HashMap<String, Table> tableRecords)
      throws CloneNotSupportedException {
    Segments clonedSegments = new Segments();
    clonedSegments.setChildren(new HashSet<Segment>());
    for (Segment s : this.children) {
      if (!segmentRecords.containsKey(s.getId())) {
        Segment clonedSegment = s.clone();
        clonedSegment.setId(s.getId());
        clonedSegments.addSegment(clonedSegment);
        segmentRecords.put(s.getId(), clonedSegment);
      } else {
        clonedSegments.addSegment(segmentRecords.get(s.getId()));
      }

    }

    return clonedSegments;

  }

  public void merge(Segments sgts) {
    for (Segment s : sgts.getChildren()) {
      if (this.findOneSegmentByNameAndByHl7Version(s.getName(), s.getHl7Version()) == null) {
        this.addSegment(s);
      } else {
        s.setId(this.findOneSegmentByNameAndByHl7Version(s.getName(), s.getHl7Version()).getId()); // FIXME
                                                                                                   // probably
                                                                                                   // useless
      }
    }
  }

  // public void setPositionsOrder(){
  // List<Segment> sortedList = new ArrayList<Segment>(this.getChildren());
  // Collections.sort(sortedList);
  // for (Segment elt: sortedList) {
  // elt.setSectionPosition(sortedList.indexOf(elt));
  // }
  // }

}
