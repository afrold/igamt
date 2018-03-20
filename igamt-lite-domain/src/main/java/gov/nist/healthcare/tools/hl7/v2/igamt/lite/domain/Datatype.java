package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

@Document(collection = "datatype")
public class Datatype extends DataModelWithConstraints
    implements java.io.Serializable, Cloneable, Comparable<Datatype> {

  private static final long serialVersionUID = 1L;

  public Datatype() {
    super();
    this.type = Constant.DATATYPE;
  }

  @Id
  private String id;

  private String label;

  private String ext = "";

  private String purposeAndUse = "";

  private List<ValueSetOrSingleCodeBinding> valueSetBindings =
      new ArrayList<ValueSetOrSingleCodeBinding>();

  private List<Comment> comments = new ArrayList<Comment>();

  private List<SingleElementValue> singleElementValues = new ArrayList<SingleElementValue>();

  protected List<Component> components = new ArrayList<Component>();

  private String name = "";
  private Set<String> hl7versions = new HashSet<String>();

  private Set<ShareParticipantPermission> shareParticipantIds =
      new HashSet<ShareParticipantPermission>();

  public Set<String> getHl7versions() {
    return hl7versions;
  }

  public void setHl7versions(Set<String> hl7versions) {
    this.hl7versions = hl7versions;
  }

  private String description = "";

  protected String comment = "";

  protected String usageNote = "";

  protected String defPreText = "";

  protected String defPostText = "";

  @Deprecated
  protected int precisionOfDTM = 3;
  @Deprecated
  protected boolean timeZoneOfDTM = false;

  protected DTMConstraints dtmConstraints;

  public DTMConstraints getDtmConstraints() {
    if (this.name.equals("DTM") && dtmConstraints == null) {
      return new DTMConstraints().generateDefaultDTMConstraints();
    }
    return dtmConstraints;
  }

  public void setDtmConstraints(DTMConstraints dtmConstraints) {
    this.dtmConstraints = dtmConstraints;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getExt() {
    return this.ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public List<Component> getComponents() {
    return components;
  }

  public void setComponents(List<Component> components) {
    this.components = components;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPurposeAndUse() {
    return purposeAndUse;
  }

  public void setPurposeAndUse(String purposeAndUse) {
    this.purposeAndUse = purposeAndUse;
  }

  public void addComponent(Component c) {
    components.add(c);
  }

  public void addValueSetBinding(ValueSetOrSingleCodeBinding vsb) {
    valueSetBindings.add(vsb);
  }

  public void addComment(Comment comment) {
    comments.add(comment);
  }

  public void addSingleElementValue(SingleElementValue sev) {
    singleElementValues.add(sev);
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getUsageNote() {
    return usageNote;
  }

  public void setUsageNote(String usageNote) {
    this.usageNote = usageNote;
  }

  public String getDefPreText() {
    return defPreText;
  }

  public void setDefPreText(String defPreText) {
    this.defPreText = defPreText;
  }

  public String getDefPostText() {
    return defPostText;
  }

  public void setDefPostText(String defPostText) {
    this.defPostText = defPostText;
  }

  @Override
  public String toString() {
    // return "Datatype [id=" + id + ", label=" + label + ", name=" + name +
    // ", description="
    // + description + ", components=" + components + "]";
    return ReflectionToStringBuilder.toString(this);

  }

  @Override
  public Datatype clone() throws CloneNotSupportedException {
    Datatype clonedDT = new Datatype();

    clonedDT.setComment(comment);
    clonedDT.setConformanceStatements(new ArrayList<ConformanceStatement>());
    for (ConformanceStatement cs : this.conformanceStatements) {
      clonedDT.addConformanceStatement(cs.clone());
    }

    clonedDT.setPredicates(new ArrayList<Predicate>());
    for (Predicate cp : this.predicates) {
      clonedDT.addPredicate(cp.clone());
    }

    clonedDT.setComponents(new ArrayList<Component>());
    for (Component c : this.components) {
      clonedDT.addComponent(c.clone());
    }

    clonedDT.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
    for (ValueSetOrSingleCodeBinding vsb : this.valueSetBindings) {
      clonedDT.addValueSetBinding(vsb);
    }

    clonedDT.setComments(new ArrayList<Comment>());
    for (Comment c : this.comments) {
      clonedDT.addComment(c);
    }

    clonedDT.setSingleElementValues(new ArrayList<SingleElementValue>());
    for (SingleElementValue sev : this.singleElementValues) {
      clonedDT.addSingleElementValue(sev);
    }

    clonedDT.setDescription(description);
    clonedDT.setLabel(label);
    clonedDT.setExt(this.ext);
    clonedDT.setName(name);
    clonedDT.setUsageNote(usageNote);
    clonedDT.setDefPreText(defPreText);
    clonedDT.setDefPostText(defPostText);
    clonedDT.setPrecisionOfDTM(precisionOfDTM);
    clonedDT.setTimeZoneOfDTM(timeZoneOfDTM);
    clonedDT.setScope(this.scope);
    clonedDT.setStatus(this.status);
    clonedDT.setHl7versions(this.hl7versions);
    clonedDT.setStatus(this.getStatus());
    clonedDT.setPublicationVersion(this.getPublicationVersion());
    clonedDT.setHl7Version(this.hl7Version);
    clonedDT.setParentVersion(null);
    clonedDT.setHl7versions(this.hl7versions);
    clonedDT.setDateUpdated(new Date());
    return clonedDT;
  }

  public boolean isEqual(Datatype dt) {
    if (dt == null)
      return false;
    if (hl7Version == null) {
      if (dt.hl7Version != null)
        return false;
    } else if (!hl7Version.equals(dt.hl7Version))
      return false;
    if (label == null) {
      if (dt.label != null)
        return false;
    } else if (!label.equals(dt.label))
      return false;
    if (name == null) {
      if (dt.name != null)
        return false;
    } else if (!name.equals(dt.name))
      return false;
    return true;
  }

  @Override
  public int compareTo(Datatype o) {
    int x = String.CASE_INSENSITIVE_ORDER.compare(
        this.getName() != null && this.label != null ? this.getName() + this.getLabel() : "",
        o.getName() != null && o.getLabel() != null ? o.getName() + o.getLabel() : "");
    if (x == 0) {
      x = (this.getName() != null && this.getLabel() != null ? this.getName() + this.getLabel()
          : "").compareTo(
              o.getName() != null && o.getLabel() != null ? o.getName() + o.getLabel() : "");
    }
    return x;
  }

  public boolean isIdentique(Datatype d) {
    if (!this.getName().equals(d.getName())) {
      return false;
    } else if (d.getComponents().size() != this.getComponents().size()) {
      return false;
    } else {
      for (int i = 0; i < d.getComponents().size(); i++) {
        if (!this.getComponents().get(i).isIdentique(d.getComponents().get(i))) {
          return false;
        }

      }
      return true;
    }

  }

  public Component findOneComponent(String id) {
    if (this.components != null)
      for (Component m : this.components) {
        if (id.equals(m.getId())) {
          return m;
        }
      }
    return null;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31).append(id).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Datatype))
      return false;
    if (obj == this)
      return true;

    Datatype rhs = (Datatype) obj;
    return new EqualsBuilder().append(id, rhs.id).isEquals();
  }

  public String getLabel() {
    if (this.getExt() == null || this.getExt().isEmpty())
      return this.getName();
    else
      return this.getName() + "_" + this.getExt();
  }

  @Deprecated
  public int getPrecisionOfDTM() {
    return precisionOfDTM;
  }

  @Deprecated
  public void setPrecisionOfDTM(int precisionOfDTM) {
    this.precisionOfDTM = precisionOfDTM;
  }

  @Deprecated
  public boolean isTimeZoneOfDTM() {
    return timeZoneOfDTM;
  }

  @Deprecated
  public void setTimeZoneOfDTM(boolean timeZoneOfDTM) {
    this.timeZoneOfDTM = timeZoneOfDTM;
  }

  public Set<ShareParticipantPermission> getShareParticipantIds() {
    return shareParticipantIds;
  }

  public void setShareParticipantIds(Set<ShareParticipantPermission> shareParticipantIds) {
    this.shareParticipantIds = shareParticipantIds;
  }

  public List<ValueSetOrSingleCodeBinding> getValueSetBindings() {
    return valueSetBindings;
  }

  public void setValueSetBindings(List<ValueSetOrSingleCodeBinding> valueSetBindings) {
    this.valueSetBindings = valueSetBindings;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public List<SingleElementValue> getSingleElementValues() {
    return singleElementValues;
  }

  public void setSingleElementValues(List<SingleElementValue> singleElementValues) {
    this.singleElementValues = singleElementValues;
  }

  public List<ConformanceStatement> retrieveConformanceStatementsForConstant() {

    List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();

    for (SingleElementValue constant : this.singleElementValues) {
      String[] paths = constant.getLocation().split("\\.");
      String path = "";
      for (String p : paths) {
        path = path + "." + p + "[1]";
      }
      path = path.substring(1);

      String constraintId = this.getLabel() + "." + constant.location;
      String description = this.getName() + "." + constant.getLocation() + "(" + constant.getName()
          + ") SHALL contain the constant value '" + constant.getValue() + "'.";
      String assertion =
          "<Assertion><AND><Presence Path=\"" + path + "\"/><PlainText Path=\"" + path
              + "\" Text=\"" + constant.getValue() + "\" IgnoreCase=\"false\"/></AND></Assertion>";
      ConformanceStatement cs = new ConformanceStatement();
      cs.setId(ObjectId.get().toString());
      cs.setConstraintId(constraintId);
      cs.setDescription(description);
      cs.setAssertion(assertion);

      results.add(cs);

    }
    return results;
  }

  public List<ConformanceStatement> retrieveConformanceStatementsForSingleCode() {
    List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();

    for (ValueSetOrSingleCodeBinding vsoscb : this.valueSetBindings) {
      if (vsoscb instanceof SingleCodeBinding) {
        SingleCodeBinding scb = (SingleCodeBinding) vsoscb;

        String[] paths = scb.getLocation().split("\\.");
        String path = "";
        for (String p : paths) {
          path = path + "." + p + "[1]";
        }
        path = path.substring(1);

        String constraintId = this.getLabel() + "." + scb.getLocation();
        String description = this.getName() + "." + scb.getLocation()
            + " SHALL contain the constant value '" + scb.getCode().getValue()
            + "' drawn from the code system '" + scb.getCode().getCodeSystem() + "'.";
        String assertion = "";
        if (scb.isCodedElement()) {
          assertion = "<Assertion>" + "<AND>" + "<AND><Presence Path=\"" + path + ".1[1]"
              + "\"/><PlainText Path=\"" + path + ".1[1]" + "\" Text=\"" + scb.getCode().getValue()
              + "\" IgnoreCase=\"false\"/></AND>" + "<AND><Presence Path=\"" + path + ".3[1]"
              + "\"/><PlainText Path=\"" + path + ".3[1]" + "\" Text=\""
              + scb.getCode().getCodeSystem() + "\" IgnoreCase=\"false\"/></AND>" + "</AND>"
              + "</Assertion>";
        } else {
          assertion = "<Assertion><AND><Presence Path=\"" + path + "\"/><PlainText Path=\"" + path
              + "\" Text=\"" + scb.getCode().getValue()
              + "\" IgnoreCase=\"false\"/></AND></Assertion>";
        }
        ConformanceStatement cs = new ConformanceStatement();
        cs.setId(ObjectId.get().toString());
        cs.setConstraintId(constraintId);
        cs.setDescription(description);
        cs.setAssertion(assertion);

        results.add(cs);
      }
    }
    return results;
  }

  public List<ConformanceStatement> retrieveAllConformanceStatements() {
    List<ConformanceStatement> results = this.conformanceStatements;
    results.addAll(this.retrieveConformanceStatementsForSingleCode());
    results.addAll(this.retrieveConformanceStatementsForConstant());
    return results;
  }

  public Component findComponentByPosition(int position) {
    for (Component child : this.components) {
      if (child.getPosition().equals(position))
        return child;
    }
    return null;
  }

}
