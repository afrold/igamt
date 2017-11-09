package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception;

public class CoConstraintSerializationException extends SerializationException {

	public CoConstraintSerializationException(Exception originalException, String location, String message) {
		super(originalException, location, message);
			this.label = "Co-Constraint";
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return null;
	}

}
