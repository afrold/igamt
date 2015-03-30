package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class SegmentRef extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	public SegmentRef() {
		super();
		type = Constant.SEGMENTREF;
	}

	@DBRef
	@JsonIgnoreProperties({ "label", "fields", "name", "description",
			"predicates", "conformanceStatements", "segments", "comment",
			"dynamicMappings" })
	private Segment ref;

	public Segment getRef() {
		return ref;
	}

	public void setRef(Segment ref) {
		this.ref = ref;
	}

	@Override
	public String toString() {
		return "SegmentRef [segment=" + ref + ", usage=" + usage + ", min="
				+ min + ", max=" + max + "]";
	}

}
