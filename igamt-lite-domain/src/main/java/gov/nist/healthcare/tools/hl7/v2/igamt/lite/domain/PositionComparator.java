package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Comparator;

public class PositionComparator implements Comparator<Section> {

  @Override
  public int compare(Section o1, Section o2) {
    // TODO Auto-generated method stub
    return o1.getSectionPosition() - o2.getSectionPosition();
  }

}
