package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.CoConstraintFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypePredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessagePredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.ProfileComponentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentPredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentValueSetBindingFound;

public class ValueSetCrossReference {

  private List<MessageValueSetBindingFound> messageValueSetBindingfounds;
  private List<SegmentValueSetBindingFound> segmentValueSetBindingfounds;
  private List<DatatypeValueSetBindingFound> datatypeValueSetBindingfounds;
  private List<CoConstraintFound> coConstraintFounds;
  private List<MessageConformanceStatmentFound> messageConformanceStatmentFounds;
  private List<SegmentConformanceStatmentFound> segmentConformanceStatmentFounds;
  private List<DatatypeConformanceStatmentFound> datatypeConformanceStatmentFounds;
  private List<MessagePredicateFound> messagePredicateFounds;
  private List<SegmentPredicateFound> segmentPredicateFounds;
  private List<DatatypePredicateFound> datatypePredicateFounds;
  private List<ProfileComponentFound> profileComponentFound;
  private boolean empty;


  public List<MessageValueSetBindingFound> getMessageValueSetBindingfounds() {
    return messageValueSetBindingfounds;
  }

  public void setMessageValueSetBindingfounds(
      List<MessageValueSetBindingFound> messageValueSetBindingfounds) {
    this.messageValueSetBindingfounds = messageValueSetBindingfounds;
  }

  public List<SegmentValueSetBindingFound> getSegmentValueSetBindingfounds() {
    return segmentValueSetBindingfounds;
  }

  public void setSegmentValueSetBindingfounds(
      List<SegmentValueSetBindingFound> segmentValueSetBindingfounds) {
    this.segmentValueSetBindingfounds = segmentValueSetBindingfounds;
  }

  public List<DatatypeValueSetBindingFound> getDatatypeValueSetBindingfounds() {
    return datatypeValueSetBindingfounds;
  }

  public void setDatatypeValueSetBindingfounds(
      List<DatatypeValueSetBindingFound> datatypeValueSetBindingfounds) {
    this.datatypeValueSetBindingfounds = datatypeValueSetBindingfounds;
  }

  public List<CoConstraintFound> getCoConstraintFounds() {
    return coConstraintFounds;
  }

  public void setCoConstraintFounds(List<CoConstraintFound> coConstraintFounds) {
    this.coConstraintFounds = coConstraintFounds;
  }

  public List<MessageConformanceStatmentFound> getMessageConformanceStatmentFounds() {
    return messageConformanceStatmentFounds;
  }

  public void setMessageConformanceStatmentFounds(
      List<MessageConformanceStatmentFound> messageConformanceStatmentFounds) {
    this.messageConformanceStatmentFounds = messageConformanceStatmentFounds;
  }

  public List<SegmentConformanceStatmentFound> getSegmentConformanceStatmentFounds() {
    return segmentConformanceStatmentFounds;
  }

  public void setSegmentConformanceStatmentFounds(
      List<SegmentConformanceStatmentFound> segmentConformanceStatmentFounds) {
    this.segmentConformanceStatmentFounds = segmentConformanceStatmentFounds;
  }

  public List<DatatypeConformanceStatmentFound> getDatatypeConformanceStatmentFounds() {
    return datatypeConformanceStatmentFounds;
  }

  public void setDatatypeConformanceStatmentFounds(
      List<DatatypeConformanceStatmentFound> datatypeConformanceStatmentFounds) {
    this.datatypeConformanceStatmentFounds = datatypeConformanceStatmentFounds;
  }

  public List<MessagePredicateFound> getMessagePredicateFounds() {
    return messagePredicateFounds;
  }

  public void setMessagePredicateFounds(List<MessagePredicateFound> messagePredicateFounds) {
    this.messagePredicateFounds = messagePredicateFounds;
  }

  public List<SegmentPredicateFound> getSegmentPredicateFounds() {
    return segmentPredicateFounds;
  }

  public void setSegmentPredicateFounds(List<SegmentPredicateFound> segmentPredicateFounds) {
    this.segmentPredicateFounds = segmentPredicateFounds;
  }

  public List<DatatypePredicateFound> getDatatypePredicateFounds() {
    return datatypePredicateFounds;
  }

  public void setDatatypePredicateFounds(List<DatatypePredicateFound> datatypePredicateFounds) {
    this.datatypePredicateFounds = datatypePredicateFounds;
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
    this.empty = messageValueSetBindingfounds.isEmpty() && segmentValueSetBindingfounds.isEmpty()
        && datatypeValueSetBindingfounds.isEmpty() && coConstraintFounds.isEmpty()
        && messageConformanceStatmentFounds.isEmpty() && segmentConformanceStatmentFounds.isEmpty()
        && datatypeConformanceStatmentFounds.isEmpty() && messagePredicateFounds.isEmpty()
        && segmentPredicateFounds.isEmpty() && datatypePredicateFounds.isEmpty()
        && profileComponentFound.isEmpty();
  }
}
