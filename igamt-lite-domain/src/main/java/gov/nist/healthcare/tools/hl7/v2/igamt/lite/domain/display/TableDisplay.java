package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.display;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBindingStrength;

public class TableDisplay {

  private String id;
  private BindingType type;
  private String bindingIdentifier;
  private String bindingLocation;
  private ValueSetBindingStrength bindingStrength;
  private DisplayLevel level;
  private String levelStyle;
  private Code code;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BindingType getType() {
    return type;
  }

  public void setType(BindingType type) {
    this.type = type;
  }

  public String getBindingIdentifier() {
    return bindingIdentifier;
  }

  public void setBindingIdentifier(String bindingIdentifier) {
    this.bindingIdentifier = bindingIdentifier;
  }

  public String getBindingLocation() {
    return bindingLocation;
  }

  public void setBindingLocation(String bindingLocation) {
    this.bindingLocation = bindingLocation;
  }

  public ValueSetBindingStrength getBindingStrength() {
    return bindingStrength;
  }

  public void setBindingStrength(ValueSetBindingStrength bindingStrength) {
    this.bindingStrength = bindingStrength;
  }

  public DisplayLevel getLevel() {
    return level;
  }

  public void setLevel(DisplayLevel level) {
    this.level = level;
  }

  public String getLevelStyle() {
    return levelStyle;
  }

  public void setLevelStyle(String levelStyle) {
    this.levelStyle = levelStyle;
  }

  public Code getCode() {
    return code;
  }

  public void setCode(Code code) {
    this.code = code;
  }



}
