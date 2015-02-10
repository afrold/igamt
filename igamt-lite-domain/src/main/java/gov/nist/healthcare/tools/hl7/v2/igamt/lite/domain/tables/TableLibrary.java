package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Views;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.codehaus.jackson.map.annotate.JsonView;

@Entity
public class TableLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2904036105687742572L;

	@JsonView({Views.Profile.class})
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonView({Views.Profile.class})
	private String tableLibraryIdentifier;
	
	@JsonView({Views.Profile.class})
	private String status;
	
	@JsonView({Views.Profile.class})
	private String tableLibraryVersion;
	
	@JsonView({Views.Profile.class})
	private String organizationName;
	
	@JsonView({Views.Profile.class})
	private String name;
	
	@JsonView({Views.Profile.class})
	private String description;

	@JsonView({Views.Profile.class})
	@JoinColumn(unique = true)
	@OneToOne(optional = false, mappedBy = "tableLibrary", cascade = CascadeType.ALL)
	private Tables tables;

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

	public Tables getTables() {
		return tables;
	}

	public void setTables(Tables tables) {
		this.tables = tables;
	}

	@Override
	public String toString() {
		return "TableLibrary [id=" + id + ", tableLibraryIdentifier="
				+ tableLibraryIdentifier + ", status=" + status
				+ ", tableLibraryVersion=" + tableLibraryVersion
				+ ", organizationName=" + organizationName + ", name=" + name
				+ ", description=" + description + ", tables=" + tables + "]";
	}

}
