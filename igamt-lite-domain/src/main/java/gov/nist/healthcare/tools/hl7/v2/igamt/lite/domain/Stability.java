package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

public enum Stability {
  Static("Static"), Dynamic("Dynamic"), Undefined("Not defined");
	public final String value;
    Stability(String v){
	  value = v;
    }
	

  public static Stability fromValue(String v) {

    return !"".equals(v) && v != null ? valueOf(v) : Stability.Dynamic;
  }
}
