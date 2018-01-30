package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.delta;

public class Delta {
	String _old; 
	String _new;
	public String getOld() {
		return _old;
	}
	public void setOld(String _old) {
		this._old = _old;
	}
	public String getNew() {
		return _new;
	}
	public void setNew(String _new) {
		this._new = _new;
	}
	public Delta(String _old, String _new) {
		super();
		this._old = _old;
		this._new = _new;
	}

}
