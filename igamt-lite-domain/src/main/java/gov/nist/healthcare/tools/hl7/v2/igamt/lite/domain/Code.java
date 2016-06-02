package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;

public class Code extends DataModel implements Serializable, Cloneable, Comparable<Code> {

  private static final long serialVersionUID = 410373025762745686L;

  private String id;

  private String value;

  private String label;

  private String codeSystem;

  private String codeSystemVersion;

  private String codeUsage;

  private String comments;

  public Code() {
    super();
    this.type = Constant.CODE;
    this.id = ObjectId.get().toString();
  }

  public String getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getCodeSystem() {
    return codeSystem;
  }

  public void setCodeSystem(String codeSystem) {
    this.codeSystem = codeSystem;
  }

  public String getCodeSystemVersion() {
    return codeSystemVersion;
  }

  public void setCodeSystemVersion(String codeSystemVersion) {
    this.codeSystemVersion = codeSystemVersion;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCodeUsage() {
    return codeUsage;
  }

  public void setCodeUsage(String codeUsage) {
    this.codeUsage = codeUsage;
  }

  @Override
  public String toString() {
    return "Code [id=" + id + ", value=" + value + ", label=" + label + ", codeSystem="
        + codeSystem + ", codeSystemVersion=" + codeSystemVersion + ", codeUsage=" + codeUsage
        + ", comments=" + comments + "]";
  }

  @Override
  public Code clone() throws CloneNotSupportedException {
    Code clonedCode = new Code();
    clonedCode.setId(id);
    clonedCode.setType(type);
    clonedCode.setValue(value);
    clonedCode.setLabel(label);
    clonedCode.setComments(comments);
    clonedCode.setCodeUsage(codeUsage);
    clonedCode.setCodeSystemVersion(codeSystemVersion);
    clonedCode.setCodeSystem(codeSystem);

    return clonedCode;
  }

  public int compareTo(Code o) {
    int x =
        String.CASE_INSENSITIVE_ORDER.compare(
            this.getValue() != null && this.getLabel() != null ? this.getValue() + this.getLabel()
                : "",
            o.getValue() != null && this.getLabel() != null ? o.getValue() + this.getLabel() : "");
    if (x == 0) {
      x =
          (this.getValue() != null && this.getLabel() != null ? this.getValue() + this.getLabel()
              : "").compareTo(o.getValue() != null && this.getLabel() != null ? o.getValue()
              + o.getLabel() : "");
    }
    return x;
  }

}
