package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

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
import javax.persistence.OneToMany;

@Entity
@javax.persistence.Table(name = "TABLES")
public class Tables implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2904036105687742572L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "IDENTIFIER")
	private String tableLibraryIdentifier;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "TABLESVERSION")
	private String tableLibraryVersion;

	@Column(name = "ORGNAME")
	private String organizationName;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany(mappedBy = "tables", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private final Set<Table> children = new HashSet<Table>();

	public void addTable(Table t) {
		if (t.getTables() != null) {
			throw new IllegalArgumentException("The Table " + t.getName()
					+ " already belong to a different Table library");
		}
		children.add(t);
		t.setTables(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Set<Table> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "TableLibrary [id=" + id + ", tableLibraryIdentifier="
				+ tableLibraryIdentifier + ", status=" + status
				+ ", tableLibraryVersion=" + tableLibraryVersion
				+ ", organizationName=" + organizationName + ", name=" + name
				+ ", description=" + description + "]";
	}
}
