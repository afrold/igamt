package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {

  @Override
  public int compare(Message o1, Message o2) {
    // TODO Auto-generated method stub
    return o1.getPosition() - o2.getPosition();
  }

}
