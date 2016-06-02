package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;


public class ElementChange {

  /*
   * Id of the element that has modifications
   */
  private String id;

  /*
   * Dictionary where : key is the name of the getter of the field that has changed value is a
   * dictionary with keys "old" and "new" for the new and old values
   */
  private Map<String, Map<String, String>> change;

  /*
   * Record the parent of data corresponding to the id (Message, Datatype, Segment...
   */
  private String parent;

  /*
   * Record the parent of change : add, del, edit
   */
  private String changeType;

  public ElementChange(String id, String parent) {
    super();
    this.id = id;
    this.parent = parent;
    this.change = new HashMap<String, Map<String, String>>();
  }

  public void recordChange(String field, String baseValue, String newValue) {
    Map<String, String> values = new HashMap<String, String>();
    values.put("basevalue", baseValue);
    values.put("newvalue", newValue);
    this.change.put(field, values);
  }

  public Integer countChanges() {
    return this.change.size();
  }

  public void addToJson(JsonGenerator generator) throws IOException {
    generator.writeStartObject();
    generator.writeFieldName("id");
    generator.writeString(this.getId());

    generator.writeArrayFieldStart("changes");

    for (String field : this.getChange().keySet()) {
      generator.writeStartObject();
      generator.writeFieldName(field);
      generator.writeStartObject();
      generator.writeStringField("baseValue", this.getChange().get(field).get("basevalue"));
      generator.writeStringField("newValue", this.getChange().get(field).get("newvalue"));
      generator.writeEndObject();
      generator.writeEndObject();
    }
    generator.writeEndArray();
    generator.writeEndObject();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String type) {
    this.parent = type;
  }

  public Map<String, Map<String, String>> getChange() {
    return change;
  }

  public void setChange(Map<String, Map<String, String>> change) {
    this.change = change;
  }

  public String getChangeType() {
    return changeType;
  }

  public void setChangeType(String changeType) {
    this.changeType = changeType;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("ElementChange [id=");
    sb.append(id);
    sb.append(", parent=");
    sb.append(parent);
    sb.append(", changeType=");
    sb.append(changeType);
    sb.append(", change=");
    for (String field : change.keySet()) {
      sb.append("[");
      sb.append(field);
      sb.append(" => base value: ");
      sb.append(change.get(field).get("basevalue"));
      sb.append(", new value: ");
      sb.append(change.get(field).get("newvalue"));
      sb.append("]");
    }

    return sb.toString();
  }



}
