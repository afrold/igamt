package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

@Deprecated
public class Datatypes extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  private String id;

  /**
	 * 
	 */
  public Datatypes() {
    super();
    this.id = ObjectId.get().toString();
  }

  private Set<Datatype> children = new HashSet<Datatype>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<Datatype> getChildren() {
    return children;
  }

  public void setChildren(Set<Datatype> children) {
    this.children = children;
  }

  public void addDatatype(Datatype d) {
    children.add(d);
  }

  // public Datatype find(String label) {
  // Iterator<Datatype> it = this.children.iterator();
  // while (it.hasNext()) {
  // Datatype tmp = it.next();
  // if (tmp.getLabel().equals(label)) {
  // return tmp;
  // }
  // }
  // return null;
  // }

  public Datatype save(Datatype d) {
    if (!this.children.contains(d)) {
      children.add(d);
    }
    return d;
  }

  public void delete(String id) {
    Datatype d = findOne(id);
    if (d != null)
      this.children.remove(d);
  }

  public Datatype findOne(String id) {
    if (this.children != null) {
      for (Datatype m : this.children) {
        if (m.getId().equals(id)) {
          return m;
        }
      }
    }

    return null;
  }

  public Datatype findOneByNameAndByLabelAndByVersion(String name, String version) {
    if (this.children != null) {
      for (Datatype dt : this.children) {
        if (dt.getName().equals(name) && dt.getHl7Version().equals(version)) {
          return dt;
        }
      }
    }
    return null;
  }

  public Component findOneComponent(String id) {
    if (this.children != null)
      for (Datatype m : this.children) {
        Component c = findOneComponent(id, m);
        if (c != null) {
          return c;
        }
      }

    return null;
  }

  public Component findOneComponent(String id, Datatype datatype) {
    if (datatype.getComponents() != null) {
      for (Component c : datatype.getComponents()) {
        if (c.getId().equals(id)) {
          return c;
        } else {
          Component r = findOneComponent(id, this.findOne(c.getDatatype().getId()));
          if (r != null) {
            return r;
          }
        }
      }
    }
    return null;
  }

  public Datatype findOneDatatypeByBase(String baseName) {
    if (this.children != null)
      for (Datatype d : this.children) {
        if (d.getName().equals(baseName)) {
          return d;
        }
      }
    return null;
  }

  public Predicate findOnePredicate(String predicateId) {
    for (Datatype datatype : this.getChildren()) {
      Predicate predicate = datatype.findOnePredicate(predicateId);
      if (predicate != null) {
        return predicate;
      }
    }
    return null;
  }

  public ConformanceStatement findOneConformanceStatement(String conformanceStatementId) {
    for (Datatype datatype : this.getChildren()) {
      ConformanceStatement conf = datatype.findOneConformanceStatement(conformanceStatementId);
      if (conf != null) {
        return conf;
      }
    }
    return null;
  }

  public boolean deletePredicate(String predicateId) {
    for (Datatype datatype : this.getChildren()) {
      if (datatype.deletePredicate(predicateId)) {
        return true;
      }
    }
    return false;
  }

  public boolean deleteConformanceStatement(String confStatementId) {
    for (Datatype datatype : this.getChildren()) {
      if (datatype.deleteConformanceStatement(confStatementId)) {
        return true;
      }
    }
    return false;
  }

  public Datatypes clone(HashMap<String, Datatype> dtRecords, HashMap<String, Table> tableRecords)
      throws CloneNotSupportedException {
    Datatypes clonedDatatypes = new Datatypes();
    clonedDatatypes.setChildren(new HashSet<Datatype>());
    for (Datatype dt : this.children) {
      if (dtRecords.containsKey(dt.getId())) {
        clonedDatatypes.addDatatype(dtRecords.get(dt.getId()));
      } else {
        Datatype clone = dt.clone();
        clone.setId(dt.getId());
        dtRecords.put(dt.getId(), clone);
        clonedDatatypes.addDatatype(clone);
      }
    }

    return clonedDatatypes;
  }

  public void merge(Datatypes dts) {
    for (Datatype dt : dts.getChildren()) {
      if (this.findOneByNameAndByLabelAndByVersion(dt.getName(), dt.getHl7Version()) == null) {
        this.addDatatype(dt);
      } else {
        dt.setId(this.findOneByNameAndByLabelAndByVersion(dt.getName(), dt.getHl7Version()).getId()); // FIXME
                                                                                                      // Probably
                                                                                                      // useless...
      }
    }

  }

  // public void setPositionsOrder(){
  // List<Datatype> sortedList = new ArrayList<Datatype>(this.getChildren());
  // Collections.sort(sortedList);
  // for (Datatype elt: sortedList) {
  // elt.setSectionPosition(sortedList.indexOf(elt));
  // }
  // }

}
