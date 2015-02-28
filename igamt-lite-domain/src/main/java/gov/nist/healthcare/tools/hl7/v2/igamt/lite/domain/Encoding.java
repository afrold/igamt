package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Encoding implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
 
	public Encoding() {
		super();
 	}

	private String value;

	public Encoding(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
