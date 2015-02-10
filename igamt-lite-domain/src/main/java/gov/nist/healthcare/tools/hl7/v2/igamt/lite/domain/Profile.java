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

@Entity
public class Profile implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String type;

	private String hl7Version;

	private String schemaVersion;

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
        return clonedProfile;
    }


}
