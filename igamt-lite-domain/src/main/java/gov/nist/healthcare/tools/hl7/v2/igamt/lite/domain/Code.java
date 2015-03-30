package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

//@Entity
//@javax.persistence.Table(name = "CODE")
public class Code extends DataModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410373025762745686L;

	private String id;

	// @NotNull
	private String code;

	// @NotNull
	private String label;

	private String codesys;

	private String source;

	private String codeUsage;

	public Code() {
		super();
		this.type = Constant.CODE;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCodesys() {
		return codesys;
	}

	public void setCodesys(String codesys) {
		this.codesys = codesys;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCodeUsage() {
		return codeUsage;
	}

	public void setCodeUsage(String codeUsage) {
		this.codeUsage = codeUsage;
	}

	@Override
	public String toString() {
		return "Code [code=" + code + ", label=" + label + ", codesys="
				+ codesys + ", source=" + source + "]";
	}

}
