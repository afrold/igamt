package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tables")
public class Tables extends DataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2904036105687742572L;

	@Id
	private String id;

	private String tableLibraryIdentifier;

	private String status;

	private String tableLibraryVersion;

	private String organizationName;

	private String name;

	private String description;

	private Set<Table> children = new HashSet<Table>();

	public Tables() {
		super();
		this.type = Constant.TABLELIBRARY;
		this.id = ObjectId.get().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public void setChildren(Set<Table> children) {
		this.children = children;
	}

	public void addTable(Table t) {
		children.add(t);
	}

	public Table findOne(String id) {
		if (this.children != null)
			for (Table m : this.children) {
				if (m.getId().equals(id)) {
					return m;
				}
			}

		return null;
	}

	public Code findOneCode(String id) {
		if (this.children != null) {
			for (Table m : this.children) {
				Code c = m.findOneCode(id);
				if (c != null) {
					return c;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Tables [id=" + id + ", tableLibraryIdentifier="
				+ tableLibraryIdentifier + ", status=" + status
				+ ", tableLibraryVersion=" + tableLibraryVersion
				+ ", organizationName=" + organizationName + ", name=" + name
				+ ", description=" + description + "]";
	}
}
