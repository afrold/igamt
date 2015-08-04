package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author Harold Affo (harold.affo@nist.gov) Feb 26, 2015
 * 
 */
@Document(collection = "table")
public class Table extends DataModel implements Serializable,
		Comparable<Table>, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 734059059225906039L;

	@Id
	private String id; //FIXME Not used in new model

	private String mappingAlternateId; //FIXME Not used in new model

	// @NotNull
	private String mappingId; //FIXME Not used in new model

	// @NotNull
	private String name; //FIXME Not used in new model

	private String version; //FIXME Not used in new model
	private String codesys; //FIXME Not used in new model
	private String oid;
	private String tableType; //FIXME Not used in new model
	private String stability;
	private String extensibility;

	private List<Code> codes = new ArrayList<Code>();
	
	//New concepts
	private String commonName = "";
	private String binding = "";
	private String bindingIdentifier = "";
	private String contentDefinition = "";
	private String rootCodeSystems = "";
	private String purpose = "";


	// @DBRef
	// private Tables tables;

	public Table() {
		super();
		this.type = Constant.TABLE;
		this.id = ObjectId.get().toString();
	}

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

	public List<Code> getCodes() {
		return codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public void addCode(Code c) {
		codes.add(c);
	}

	public boolean deleteCode(Code c) {
		return codes.remove(c);
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


	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	public String getBindingIdentifier() {
		return bindingIdentifier;
	}

	public void setBindingIdentifier(String bindingIdentifier) {
		this.bindingIdentifier = bindingIdentifier;
	}

	public String getContentDefinition() {
		return contentDefinition;
	}

	public void setContentDefinition(String contentDefinition) {
		this.contentDefinition = contentDefinition;
	}

	public String getRootCodeSystems() {
		return rootCodeSystems;
	}

	public void setRootCodeSystems(String rootCodeSystems) {
		this.rootCodeSystems = rootCodeSystems;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Code findOneCode(String id) {
		if (this.codes != null)
			for (Code m : this.codes) {
				if (id.equals(m.getId())) {
					return m;
				}
			}

		return null;
	}

	// public Tables getTables() {
	// return tables;
	// }
	//
	// public void setTables(Tables tables) {
	// this.tables = tables;
	// }

	@Override
	public String toString() {
		return "Table [id=" + id + ", mappingAlternateId=" + mappingAlternateId
				+ ", mappingId=" + mappingId + ", name=" + name + ", version="
				+ version + ", codesys=" + codesys + ", oid=" + oid + ", type="
				+ tableType + ", codes=" + codes + "]";
	}

	@Override
	public int compareTo(Table o) {
		int x = String.CASE_INSENSITIVE_ORDER.compare(this.mappingId,
				o.mappingId);
		if (x == 0) {
			x = this.mappingId.compareTo(o.mappingId);
		}
		return x;
	}

	@Override
	public Table clone() throws CloneNotSupportedException {
		Table clonedTable = new Table();
		for (Code c : this.codes) {
			clonedTable.addCode(c.clone());
		}

		clonedTable.setId(id);
		clonedTable.setCodesys(codesys);
		clonedTable.setExtensibility(extensibility);
		clonedTable.setMappingAlternateId(mappingAlternateId);
		clonedTable.setMappingId(mappingId);
		clonedTable.setName(name);
		clonedTable.setOid(oid);
		clonedTable.setStability(stability);
		clonedTable.setTableType(tableType);
		clonedTable.setVersion(version);

		clonedTable.setCommonName(commonName);
		clonedTable.setBinding(binding);
		clonedTable.setBindingIdentifier(bindingIdentifier);
		clonedTable.setContentDefinition(contentDefinition);
		clonedTable.setRootCodeSystems(rootCodeSystems);
		clonedTable.setPurpose(purpose);
		
		return clonedTable;
	}
}
