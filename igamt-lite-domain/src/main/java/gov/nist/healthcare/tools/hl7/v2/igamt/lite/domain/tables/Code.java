package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

public class Code implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 410373025762745686L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;
	
	@NotNull
	@Column(nullable = false)
	protected String code;
	
	@NotNull
	@Column(nullable = false)
	protected String displayName;
	
	protected String codesys;
	protected String source;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	@Override
	public String toString() {
		return "Code [code=" + code + ", displayName=" + displayName
				+ ", codesys=" + codesys + ", source=" + source + "]";
	}
	
	
}
