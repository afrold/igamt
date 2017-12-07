package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

/**
 * @author Jungyub Woo
 *
 */
public class SingleCodeBinding extends ValueSetOrSingleCodeBinding  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -242692961686938693L;
	private Code code;
	private boolean codedElement;

	public SingleCodeBinding() {
		super();
		this.type = Constant.SINGLECODE;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

  public boolean isCodedElement() {
    return codedElement;
  }

  public void setCodedElement(boolean codedElement) {
    this.codedElement = codedElement;
  }

}
