package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceContext;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonView;

 
@Entity
public class Profile implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@JsonView({View.Summary.class })
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonView({View.Summary.class})
 	private String type;

	@JsonView({View.Summary.class})
	private String hl7Version;

	@JsonView({View.Summary.class})
	private String schemaVersion;

	@JsonView({View.Summary.class})
	private ProfileMetaData metaData;

	private Encodings encodings;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Segments segments;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Datatypes datatypes;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private Messages messages;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private ConformanceContext conformanceStatements;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private ConformanceContext predicates;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	private TableLibrary tableLibrary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(unique = true)
	private Author author;

	@JsonView({View.Summary.class})
 	private Boolean preloaded; 
 	
 	@JsonView({View.Summary.class})
 	@Version // version from the db
  	private Integer version;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public ProfileMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(ProfileMetaData metaData) {
		this.metaData = metaData;
	}

	public Encodings getEncodings() {
		return encodings;
	}

	public void setEncodings(Encodings encodings) {
		this.encodings = encodings;
	}

	public Segments getSegments() {
		return segments;
	}

	public void setSegments(Segments segments) {
		this.segments = segments;
		this.segments.setProfile(this);
	}

	public Datatypes getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Datatypes datatypes) {
		this.datatypes = datatypes;
		this.datatypes.setProfile(this);
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
		this.messages.setProfile(this);
	}

	public ConformanceContext getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(
			ConformanceContext conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	public ConformanceContext getPredicates() {
		return predicates;
	}

	public void setPredicates(ConformanceContext predicates) {
		this.predicates = predicates;
	}

	public TableLibrary getTableLibrary() {
		return tableLibrary;
	}

	public void setTableLibrary(TableLibrary tableLibrary) {
		this.tableLibrary = tableLibrary;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}
	

	public Integer getVersion() {
		return version;
	}

	/**
	 * Do not set the version. Hibernate set the version automatically
	 * @param version
	 * @return
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	public Boolean getPreloaded() {
		return preloaded;
	}

	public void setPreloaded(Boolean preloaded) {
		this.preloaded = preloaded;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", type=" + type + ", hl7Version="
				+ hl7Version + ", schemaVersion=" + schemaVersion
				+ ", metaData=" + metaData + ", encodings=" + encodings
				+ ", segments=" + segments + ", datatypes=" + datatypes
				+ ", messages=" + messages + ", conformanceStatements="
				+ conformanceStatements + ", predicates=" + predicates
				+ ", tableLibrary=" + tableLibrary + ", author=" + author + "]";
	}
	
	@Override
    public Profile clone() throws CloneNotSupportedException {
		Profile clonedProfile = (Profile) super.clone();
		clonedProfile.setId(null);
		clonedProfile.setAuthor(this.author.clone());
		clonedProfile.setConformanceStatements(this.conformanceStatements.clone());
		clonedProfile.setDatatypes(this.datatypes.clone());
		clonedProfile.getDatatypes().setProfile(this);		
//		clonedProfile.setEncodings(this.encodings.clone());
		clonedProfile.setHl7Version(this.hl7Version);
//		clonedProfile.setMessages(this.messages.clone());
//		clonedProfile.setMetaData(this.metaData.clone());
		clonedProfile.setPredicates(this.predicates.clone());
		clonedProfile.setSchemaVersion(schemaVersion);
//		clonedProfile.setSegments(segments.clone());
//		clonedProfile.setTableLibrary(tableLibrary.clone());
		clonedProfile.setType(type);
        return clonedProfile;
    }


}
