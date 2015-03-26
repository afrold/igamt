package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segmentRefs")
public class SegmentRef extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	public SegmentRef() {
		super();
		type = Constant.SEGMENTREF;
	}

	@JsonIgnoreProperties({ "type", "fields", "label", "dynamicMappings",
			"name", "description", "predicates", "conformanceStatements",
			"segments" })
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REF")
	private Segment ref;

	public Segment getRef() {
		return ref;
	}

	public void setRef(Segment ref) {
		this.ref = ref;
	}

	@Override
	public String toString() {
		return "SegmentRef [id=" + id + ", segment=" + ref + ", usage=" + usage
				+ ", min=" + min + ", max=" + max + "]";
	}

}
