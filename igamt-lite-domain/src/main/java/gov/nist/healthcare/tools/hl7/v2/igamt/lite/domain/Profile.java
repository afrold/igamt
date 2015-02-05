package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceContext;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

public class Profile implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "PROFILE_ID_GENERATOR", strategy = "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.id.ProfileIdGenerator", parameters = @Parameter(name = "sequence", value = "seq_profile"))
	@GeneratedValue(generator = "PROFILE_ID_GENERATOR")
	protected String id;

	protected String type;

	protected String hl7Version;

	protected String schemaVersion;

	protected MetaData metaData;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	protected Encodings encodings;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	protected Segments segments;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	protected Datatypes datatypes;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	protected Messages messages;
	
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	protected ConformanceContext conformanceStatementsLibrary;
	
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	protected ConformanceContext predicatesLibrary;
	
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(unique = true)
	protected TableLibrary tableLibrary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
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
	
	public ConformanceContext getConformanceStatementsLibrary() {
		return conformanceStatementsLibrary;
	}

	public void setConformanceStatementsLibrary(
			ConformanceContext conformanceStatementsLibrary) {
		this.conformanceStatementsLibrary = conformanceStatementsLibrary;
	}

	public ConformanceContext getPredicatesLibrary() {
		return predicatesLibrary;
	}

	public void setPredicatesLibrary(ConformanceContext predicatesLibrary) {
		this.predicatesLibrary = predicatesLibrary;
	}

	public TableLibrary getTableLibrary() {
		return tableLibrary;
	}

	public void setTableLibrary(TableLibrary tableLibrary) {
		this.tableLibrary = tableLibrary;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", type=" + type + ", hl7Version="
				+ hl7Version + ", schemaVersion=" + schemaVersion
				+ ", metaData=" + metaData + ", encodings=" + encodings
				+ ", segments=" + segments + ", datatypes=" + datatypes
				+ ", messages=" + messages + "]";
	}
}
