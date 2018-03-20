package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;

public class Component extends DataElement implements Cloneable {

  private static final long serialVersionUID = 1L;

  private String id;

  public Component() {
    super();
    this.type = Constant.COMPONENT;
    this.id = ObjectId.get().toString();
  }

  @Override
  public String toString() {
    return "Component [position=" + position + ", id=" + id + ", datatype=" + datatype + ", name="
        + name + ", usage=" + usage + ", minLength=" + minLength + ", maxLength=" + maxLength
        + ", confLength=" + confLength + "]";
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public int compareTo(DataElement o) {
    return this.getPosition() - o.getPosition();
  }

  @Override
  public Component clone() throws CloneNotSupportedException {
    Component clonedObj = new Component();
    clonedObj.setId(ObjectId.get().toString());
    clonedObj.setConfLength(confLength);
    clonedObj.setDatatype(datatype);
    clonedObj.setMaxLength(maxLength);
    clonedObj.setMinLength(minLength);
    clonedObj.setName(name);
    clonedObj.setPosition(position);
    clonedObj.setUsage(usage);
    return clonedObj;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31).append(id).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Component))
      return false;
    if (obj == this)
      return true;

    Component rhs = (Component) obj;
    return new EqualsBuilder().append(id, rhs.id).isEquals();
  }

  public boolean isIdentique(Component c) {
    if (!c.getName().toLowerCase().equals(this.getName().toLowerCase())) {
      return false;
    }
    if (!c.getUsage().toString().equalsIgnoreCase(this.getUsage().toString())) {
      return false;
    }
//    if (!c.getMaxLength().toLowerCase().equals(this.getMaxLength().toLowerCase())) {
//      return false;
//    }
//    if (!c.getMinLength().toLowerCase().equals(this.getMinLength().toLowerCase())) {
//      return false;
//    }
//    
     if (!c.getConfLength().toString().equalsIgnoreCase(this.getConfLength().toString())) {
      return false;
    }
    // if (c.getMinLength().toString().trim()
    // .equalsIgnoreCase(this.getMinLength().toString().trim())) {
    // return false;
    //
    // }
    return true;

  }
}
