package gov.nist.healthcare.hl7tools.igmatlite.domain;

import java.io.Serializable;

public class TableElement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 410373025762745686L;

	private long id;
	private String code;
	private String displayName;
	private String codesys;
	private String source;
	
	public TableElement() {
		super();
	}
	
	public TableElement(long id, String code, String displayName,
			String codesys, String source) {
		super();
		this.id = id;
		this.code = code;
		this.displayName = displayName;
		this.codesys = codesys;
		this.source = source;
	}
	
	@Override
	public String toString() {
		return "TableElement [id=" + id + ", code=" + code + ", displayName="
				+ displayName + ", codesys=" + codesys + ", source=" + source
				+ "]";
	}
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
}
