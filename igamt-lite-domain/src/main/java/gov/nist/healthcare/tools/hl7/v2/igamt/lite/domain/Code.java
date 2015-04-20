package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

//@Entity
//@javax.persistence.Table(name = "CODE")
public class Code extends DataModel implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410373025762745686L;

	@Id
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
		this.id = ObjectId.get().toString();
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

	@Override
	public Code clone() throws CloneNotSupportedException {
		Code clonedCode = new Code();
		clonedCode.setCodesys(codesys);
		clonedCode.setCode(code);
		clonedCode.setCodeUsage(codeUsage);
		clonedCode.setLabel(label);
		clonedCode.setSource(source);

		return clonedCode;
	}

}
