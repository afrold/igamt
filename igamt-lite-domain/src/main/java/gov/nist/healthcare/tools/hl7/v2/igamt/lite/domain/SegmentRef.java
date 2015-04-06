package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class SegmentRef extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	public SegmentRef() {
		super();
		type = Constant.SEGMENTREF;
		this.id = ObjectId.get().toString();
	}

	@JsonIgnoreProperties({ "label", "fields", "dynamicMappings", "name",
			"description", "predicates", "conformanceStatements", "comment",
			"usageNote", "type", "text1", "text2" })
	private Segment ref;

	public Segment getRef() {
		return ref;
	}

	public void setRef(Segment ref) {
		this.ref = ref;
		// this.refId = ref != null ? ref.getId() : null;
	}

	// public String getRefId() {
	// return refId;
	// }
	//
	// public void setRefId(String refId) {
	// this.refId = refId;
	// }

	@Override
	public String toString() {
		return "SegmentRef [segment=" + ref + ", usage=" + usage + ", min="
				+ min + ", max=" + max + "]";
	}

}
