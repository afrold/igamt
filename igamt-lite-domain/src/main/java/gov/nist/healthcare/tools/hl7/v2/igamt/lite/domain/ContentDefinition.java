package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;


public enum ContentDefinition {
	Extensional, Intensional;

	public String value() {
		return name();
	}

	public static ContentDefinition fromValue(String v) {
		return valueOf(v);
	}
}
