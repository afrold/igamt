package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

 
@Entity
@Table(name="PROFILE")
public class Profile implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@JsonView({View.Summary.class })
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	@JsonView({View.Summary.class})
	@Column(name="TYPE")
 	private String type;

	@JsonView({View.Summary.class})
	@Column(name="HL7VERSION")
	private String hl7Version;

	@JsonView({View.Summary.class})
	@Column(name="SCHEMAVERSION")
	private String schemaVersion;

	@JsonView({View.Summary.class})
	private ProfileMetaData metaData;

	private Encodings encodings;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="SEGMENTS_ID")
	private Segments segments;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="DATATYTPES_ID")
	private Datatypes datatypes;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="MESSAGES_ID")
	private Messages messages;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="CONFSTATEMENTS_ID")
	private Constraints conformanceStatements;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="PREDICATES_ID")
	private Constraints predicates;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="TABLELIBRARY_ID")
	private TableLibrary tableLibrary;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="AUTHOR_ID")
	private Author author;

	@JsonView({View.Summary.class})
	@Column(name="PRELOADED")
 	private Boolean preloaded; 
 	
 	@JsonView({View.Summary.class})
 	@Column(name="VERSION")
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
 	}

	public Datatypes getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Datatypes datatypes) {
		this.datatypes = datatypes;
 	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
 	}

	public Constraints getConformanceStatements() {
		return conformanceStatements;
	}

	public void setConformanceStatements(
			Constraints conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	public Constraints getPredicates() {
		return predicates;
	}

	public void setPredicates(Constraints predicates) {
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


}
