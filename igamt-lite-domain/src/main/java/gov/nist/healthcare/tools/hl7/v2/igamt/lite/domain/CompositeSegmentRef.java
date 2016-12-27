package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;

public class CompositeSegmentRef extends SegmentOrGroup implements Cloneable{

	 private static final long serialVersionUID = 1L;

	  public CompositeSegmentRef() {
	    super();
	    type = Constant.SEGMENTREF;
	    this.id = ObjectId.get().toString();
	  }

	  // @JsonIgnoreProperties({ "label", "fields", "dynamicMappings", "name",
	  // "description", "predicates", "conformanceStatements", "comment",
	  // "usageNote", "type", "text1", "text2" })
	  private CompositeSegment ref;

	  public CompositeSegment getRef() {
	    return ref;
	  }

	  public void setRef(CompositeSegment ref) {
	    this.ref = ref;
	    // this.refId = ref != null ? ref.getId() : null;
	  }
}
