package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception;

public class CoConstraintSerializationException extends SerializationException {

		private final static String label = "Co-Constraint";

		public CoConstraintSerializationException(Exception originalException, String location, String message) {
			super(originalException, location, message);
		}

    public CoConstraintSerializationException(Exception originalException, String location) {
        super(originalException, location);
    }

    @Override public String getLabel() {
				return this.label;
		}

}
