package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
@Deprecated
public class DynamicMapping implements Serializable, Cloneable {

  private static final long serialVersionUID = 1L;

  public DynamicMapping() {
    super();
    this.id = ObjectId.get().toString();
  }

  private String id;

  protected List<Mapping> mappings = new ArrayList<Mapping>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Mapping> getMappings() {
    return mappings;
  }

  public void setMappings(List<Mapping> mappings) {
    this.mappings = mappings;
  }

  public void addMapping(Mapping m) {
    mappings.add(m);
  }

  @Override
  public DynamicMapping clone() throws CloneNotSupportedException {
    DynamicMapping clonedDynamicMapping = new DynamicMapping();
    clonedDynamicMapping.setId(id);
    clonedDynamicMapping.setMappings(mappings);
    for (Mapping m : this.mappings) {
      clonedDynamicMapping.addMapping(m.clone());
    }
    return clonedDynamicMapping;
  }

}
