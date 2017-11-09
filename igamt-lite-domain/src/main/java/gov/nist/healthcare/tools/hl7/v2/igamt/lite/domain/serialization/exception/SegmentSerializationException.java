package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception;


public class SegmentSerializationException extends SerializationException {
	
	public SegmentSerializationException(Exception originalException, String location) {
		super(originalException, location);
			this.label = "Segment";
	}

	@Override
	public String toJson() {
		return null;
	}

}
