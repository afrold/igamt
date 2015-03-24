package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DataModel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 26, 2015
 * 
 */
@Entity
@javax.persistence.Table(name = "IGTABLE")
public class Table extends DataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 734059059225906039L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "MAPPING_ALTERNATE_ID")
	private String mappingAlternateId;

	@NotNull
	@Column(nullable = false, name = "MAPPING_ID")
	private String mappingId;

	@NotNull
	@Column(nullable = false, name = "NAME")
	private String name;

	@Column(name = "VERSION")
	private String version;
	@Column(name = "CODESYS")
	private String codesys;
	@Column(name = "OID")
	private String oid;
	@Column(name = "TABLETYPE")
	private String tableType;
	@Column(name = "STABILITY")
	private String stability;
	@Column(name = "EXTENSIBILITY")
	private String extensibility;
	

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "TABLE_CODE", joinColumns = @JoinColumn(name = "IGTABLE"), inverseJoinColumns = @JoinColumn(name = "CODE"))
	private final Set<Code> codes = new HashSet<Code>();
	
	
	public Table() {
		super();
		this.type = Constant.TABLE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMappingAlternateId() {
		return mappingAlternateId;
	}

	public void setMappingAlternateId(String mappingAlternateId) {
		this.mappingAlternateId = mappingAlternateId;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCodesys() {
		return codesys;
	}

	public void setCodesys(String codesys) {
		this.codesys = codesys;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public Set<Code> getCodes() {
		return codes;
	}

	public void addCode(Code c) {
		codes.add(c);
	}

	public String getStability() {
		return stability;
	}

	public void setStability(String stability) {
		this.stability = stability;
	}

	public String getExtensibility() {
		return extensibility;
	}

	public void setExtensibility(String extensibility) {
		this.extensibility = extensibility;
	}

	@Override
	public String toString() {
		return "Table [id=" + id + ", mappingAlternateId=" + mappingAlternateId
				+ ", mappingId=" + mappingId + ", name=" + name + ", version="
				+ version + ", codesys=" + codesys + ", oid=" + oid + ", type="
				+ tableType + ", codes=" + codes + "]";
	}

}
