package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class CompositeGroup extends SegmentOrGroup implements Cloneable {
	public CompositeGroup() {
	    super();
	    type = Constant.GROUP;
	    this.id = ObjectId.get().toString();
	  }

	  private List<SegmentOrGroup> children = new ArrayList<SegmentOrGroup>();

	  private String name;

	public List<SegmentOrGroup> getChildren() {
		return children;
	}

	public void setChildren(List<SegmentOrGroup> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	  
}
