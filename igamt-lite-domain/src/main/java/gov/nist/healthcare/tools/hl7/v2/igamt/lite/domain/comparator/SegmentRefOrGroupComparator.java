package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.comparator;

import java.util.Comparator;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;

public class SegmentRefOrGroupComparator implements Comparator<SegmentRefOrGroup> {

  @Override
  public int compare(SegmentRefOrGroup f1, SegmentRefOrGroup f2) {
    return f1.getPosition() - f2.getPosition();
  }

}
