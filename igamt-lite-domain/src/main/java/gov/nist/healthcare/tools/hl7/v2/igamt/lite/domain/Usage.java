package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum Usage {

	R, RE, O, C, X, B, W, CE;

	public String value() {
		return name();
	}

	public static Usage fromValue(String v) {
		try{
			return valueOf(v);
		}catch (IllegalArgumentException e){
			return valueOf("C"); // ????
		}
	}

}