package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@javax.persistence.Table(name = "CODE")
public class Code implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410373025762745686L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Column(nullable = false, name = "CODE")
	private String code;

	@NotNull
	@Column(nullable = false, name = "LABEL")
	private String label;

	@Column(name = "CODESYS")
	private String codesys;

	@Column(name = "SOURCE")
	private String source;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	@Override
	public String toString() {
		return "Code [code=" + code + ", label=" + label + ", codesys="
				+ codesys + ", source=" + source + "]";
	}

}
