package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.springframework.beans.factory.annotation.Autowired;

public class SegmentOrGroupLink extends AbstractLink implements Cloneable, Comparable<SegmentOrGroupLink>{

	
	

	
	  public SegmentOrGroupLink() {
	    super();
	  }

	  public SegmentOrGroupLink(String id) {
	    super();
	    this.setId(id);
	   
	  }

	  
	  
	

	@Override
	public int compareTo(SegmentOrGroupLink o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
