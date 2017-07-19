package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.CompositeProfileFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.ProfileComponentFound;

public class MessageCrossReference {

  private List<ProfileComponentFound> profileComponentFound;
  private List<CompositeProfileFound> compositeProfileFound;
  private boolean empty;


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
    this.empty = compositeProfileFound.isEmpty() && profileComponentFound.isEmpty();
  }

  public List<ProfileComponentFound> getProfileComponentFound() {
    return profileComponentFound;
  }

  public void setProfileComponentFound(List<ProfileComponentFound> profileComponentFound) {
    this.profileComponentFound = profileComponentFound;
  }

  public List<CompositeProfileFound> getCompositeProfileFound() {
    return compositeProfileFound;
  }

  public void setCompositeProfileFound(List<CompositeProfileFound> compositeProfileFound) {
    this.compositeProfileFound = compositeProfileFound;
  }
}
