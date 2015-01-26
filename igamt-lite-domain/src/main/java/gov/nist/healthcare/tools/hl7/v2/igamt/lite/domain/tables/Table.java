package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

public class Table implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 734059059225906039L;


	@Id
	@GenericGenerator(name = "TABLE_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.TableIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_table"))
	@GeneratedValue(generator = "TABLE_ID_GENERATOR")
	protected String id;
	
	protected String mappingAlternateId;
	
	@NotNull
	@Column(nullable = false)
	protected String mappingId;
	
	@NotNull
	@Column(nullable = false)
	protected String name;
	
	protected int version;
	protected String codesys;
	protected String oid;
	protected String type;
	
	@OneToMany(cascade = CascadeType.ALL)
	protected Set<Code> codes = new HashSet<Code>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	protected Tables tables;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Set<Code> getCodes() {
		return codes;
	}

	public void setCodes(Set<Code> codes) {
		this.codes = codes;
	}

	public Tables getTables() {
		return tables;
	}

	public void setTables(Tables tables) {
		this.tables = tables;
	}

	@Override
	public String toString() {
		return "Table [id=" + id + ", mappingAlternateId=" + mappingAlternateId
				+ ", mappingId=" + mappingId + ", name=" + name + ", version="
				+ version + ", codesys=" + codesys + ", oid=" + oid + ", type="
				+ type + ", codes=" + codes + "]";
	}
	
	
	
	
}
