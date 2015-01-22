package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TableDefinition implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 734059059225906039L;


	private long id;
	private String mappingAlternateId;
	private String mappingId;
	private String name;
	private int version;
	private String codesys;
	private String oid;
	private String type;
	private Set<TableElement> tableElements;
	
	public TableDefinition(long id, String mappingAlternateId, String mappingId,
			String name, int version, String codesys, String oid, String type,
			Set<TableElement> tableElements) {
		super();
		this.id = id;
		this.mappingAlternateId = mappingAlternateId;
		this.mappingId = mappingId;
		this.name = name;
		this.version = version;
		this.codesys = codesys;
		this.oid = oid;
		this.type = type;
		this.tableElements = tableElements;
	}

	public TableDefinition() {
		super();
		this.tableElements = new HashSet<TableElement>();
	}

	@Override
	public String toString() {
		return "TableDefinition [id=" + id + ", mappingAlternateId="
				+ mappingAlternateId + ", mappingId=" + mappingId + ", name="
				+ name + ", version=" + version + ", codesys=" + codesys
				+ ", oid=" + oid + ", type=" + type + ", tableElements="
				+ tableElements + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Set<TableElement> getTableElements() {
		return tableElements;
	}

	public void setTableElements(Set<TableElement> tableElements) {
		this.tableElements = tableElements;
	}
	
	
}
