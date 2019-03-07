package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;

public class MessageConfiguration {
	  private HashMap<String, String> ackBinding= new HashMap<String, String>();

	public HashMap<String, String> getAckBinding() {
		return ackBinding;
	}

	public void setAckBinding(HashMap<String, String> ackBinding) {
		this.ackBinding = ackBinding;
	}

}
