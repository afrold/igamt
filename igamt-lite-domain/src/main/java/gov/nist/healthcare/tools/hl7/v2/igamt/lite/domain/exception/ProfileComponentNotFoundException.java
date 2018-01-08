package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.exception;

public class ProfileComponentNotFoundException extends Exception {

	public ProfileComponentNotFoundException(String profileComponentId, String label) {
        super("Profile component " + label + " not found with ID " + profileComponentId);
	}
	
}
