package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception;


public class SegmentSerializationException extends SerializationException {

    private String label = "Segment";

    public SegmentSerializationException(Exception originalException, String location) {
        this(originalException, location, null);
    }

    public SegmentSerializationException(Exception originalException, String location,
        String message) {
        super(originalException, location, message);
    }

    @Override public String getLabel() {
        return this.label;
    }

}
