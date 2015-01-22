package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TableLibrary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2904036105687742572L;
	
	private long id;
	private String tableLibraryIdentifier;
	private String status;
	private String tableLibraryVersion;
	private String organizationName;
	private String name;
	private String description;
	private Set<TableDefinition> tableDefinitions;
	
	public TableLibrary(long id, String tableLibraryIdentifier, String status,
			String tableLibraryVersion, String organizationName, String name,
			String description, Set<TableDefinition> tableDefinitions) {
		super();
		this.id = id;
		this.tableLibraryIdentifier = tableLibraryIdentifier;
		this.status = status;
		this.tableLibraryVersion = tableLibraryVersion;
		this.organizationName = organizationName;
		this.name = name;
		this.description = description;
		this.tableDefinitions = tableDefinitions;
	}

	public TableLibrary() {
		super();
		this.tableDefinitions = new HashSet<TableDefinition>();
	}

	@Override
	public String toString() {
		return "TableLibrary [id=" + id + ", tableLibraryIdentifier="
				+ tableLibraryIdentifier + ", status=" + status
				+ ", tableLibraryVersion=" + tableLibraryVersion
				+ ", organizationName=" + organizationName + ", name=" + name
				+ ", description=" + description + ", tableDefinitions="
				+ tableDefinitions + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTableLibraryIdentifier() {
		return tableLibraryIdentifier;
	}

	public void setTableLibraryIdentifier(String tableLibraryIdentifier) {
		this.tableLibraryIdentifier = tableLibraryIdentifier;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTableLibraryVersion() {
		return tableLibraryVersion;
	}

	public void setTableLibraryVersion(String tableLibraryVersion) {
		this.tableLibraryVersion = tableLibraryVersion;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<TableDefinition> getTableDefinitions() {
		return tableDefinitions;
	}

	public void setTableDefinitions(Set<TableDefinition> tableDefinitions) {
		this.tableDefinitions = tableDefinitions;
	}

	
}
