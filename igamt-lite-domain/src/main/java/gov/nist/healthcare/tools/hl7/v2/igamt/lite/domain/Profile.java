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
@Table(name = "PROFILE")
public class Profile extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Profile() {
		super();
		this.type = Constant.PROFILE;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;

	private ProfileMetaData metaData;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "SEGMENTS_ID")
	private Segments segments;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "DATATYTPES_ID")
	private Datatypes datatypes;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "MESSAGES_ID")
	private Messages messages;

	@Column(name = "COMMENT", columnDefinition = "TEXT")
	protected String comment;

	@Column(name = "USAGE_NOTE", columnDefinition = "TEXT")
	protected String usageNote;

	// @OneToOne(optional = false, fetch = FetchType.EAGER, cascade =
	// CascadeType.ALL)
	// @JoinColumn(name = "CONFSTATEMENTS_ID")
	@JsonIgnore
	private transient Constraints conformanceStatements;

	// @OneToOne(optional = false, fetch = FetchType.EAGER, cascade =
	// CascadeType.ALL)
	// @JoinColumn(name = "PREDICATES_ID")
	@JsonIgnore
	private transient Constraints predicates;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "TABLELIBRARY_ID")
	private TableLibrary tableLibrary;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTHOR_ID")
	private Author author;

	@JsonView({ View.Summary.class })
	@Column(name = "PRELOADED")
	private Boolean preloaded;

	@JsonView({ View.Summary.class })
	@Column(name = "VERSION")
	@Version
	// version from the db
	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProfileMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(ProfileMetaData metaData) {
		this.metaData = metaData;
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

	public void setConformanceStatements(Constraints conformanceStatements) {
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
	 * 
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUsageNote() {
		return usageNote;
	}

	public void setUsageNote(String usageNote) {
		this.usageNote = usageNote;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", metaData=" + metaData + ", messages="
				+ messages + ", author=" + author + "]";
	}
}
