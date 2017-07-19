package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.CoConstraintFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.ComponentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DynamicMappingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.FieldFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.ProfileComponentFound;

public class DatatypeCrossReference {
  private List<FieldFound> fieldFounds;
  private List<ComponentFound> componentFounds;
  private List<DynamicMappingFound> dynamicMappingFounds;
  private List<CoConstraintFound> coConstraintFounds;
  private List<ProfileComponentFound> profileComponentFound;
  private boolean empty;

  public List<FieldFound> getFieldFounds() {
    return fieldFounds;
  }

  public void setFieldFounds(List<FieldFound> fieldFounds) {
    this.fieldFounds = fieldFounds;
  }

  public List<ComponentFound> getComponentFounds() {
    return componentFounds;
  }

  public void setComponentFounds(List<ComponentFound> componentFounds) {
    this.componentFounds = componentFounds;
  }

  public List<DynamicMappingFound> getDynamicMappingFounds() {
    return dynamicMappingFounds;
  }

  public void setDynamicMappingFounds(List<DynamicMappingFound> dynamicMappingFounds) {
    this.dynamicMappingFounds = dynamicMappingFounds;
  }

  public List<CoConstraintFound> getCoConstraintFounds() {
    return coConstraintFounds;
  }

  public void setCoConstraintFounds(List<CoConstraintFound> coConstraintFounds) {
    this.coConstraintFounds = coConstraintFounds;
  }

  public List<ProfileComponentFound> getProfileComponentFound() {
    return profileComponentFound;
  }

  public void setProfileComponentFound(List<ProfileComponentFound> profileComponentFound) {
    this.profileComponentFound = profileComponentFound;
  }


  /**
   * @return the empty
   */
  public boolean isEmpty() {
    return empty;
  }

  /**
   * @param empty the empty to set
   */
  public void setEmpty() {
    this.empty = fieldFounds.isEmpty() && coConstraintFounds.isEmpty() && componentFounds.isEmpty()
        && this.dynamicMappingFounds.isEmpty() && profileComponentFound.isEmpty();
  }

}
