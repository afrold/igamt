package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.TableLibrary;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Version;

@Document(collection = "profiles")
public class Profile extends DataModel implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Profile() {
		super();
		this.type = Constant.PROFILE;
	}

	@Id
	private Long id;

	private ProfileMetaData metaData;

	private Segments segments;

	private Datatypes datatypes;

	private Messages messages;

	protected String comment;

	protected String usageNote;

	private TableLibrary tableLibrary;

	private Boolean preloaded;

	@Version
	private Integer version;

	private String changes;

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

	public TableLibrary getTableLibrary() {
		return tableLibrary;
	}

	public void setTableLibrary(TableLibrary tableLibrary) {
		this.tableLibrary = tableLibrary;
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

	public String getChanges() {
		return changes;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}

	@Override
	public String toString() {
		return "Profile [id=" + id + ", metaData=" + metaData + ", messages="
				+ messages;
	}

	public Constraints getConformanceStatements() {
		Constraints constraints = new Constraints();
		Context dtContext = new Context();
		Context sContext = new Context();
		Context gContext = new Context();

		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Segment s : this.getSegments().getChildren()) {
			ByID byID = new ByID();
			byID.setByID("" + s.getId());
			if (s.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(s.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		sContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Datatype d : this.getDatatypes().getChildren()) {
			ByID byID = new ByID();
			byID.setByID("" + d.getId());
			if (d.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(d.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDs);

		constraints.setDatatypes(dtContext);
		constraints.setSegments(sContext);
		constraints.setGroups(gContext);
		return constraints;
	}

	public Constraints getPredicates() {
		Constraints constraints = new Constraints();
		Context dtContext = new Context();
		Context sContext = new Context();
		Context gContext = new Context();

		Set<ByNameOrByID> byNameOrByIDsSEG = new HashSet<ByNameOrByID>();
		for (Segment s : this.getSegments().getChildren()) {
			ByID byID = new ByID();
			byID.setByID("" + s.getId());
			if (s.getPredicates().size() > 0) {
				byID.setPredicates(s.getPredicates());
				byNameOrByIDsSEG.add(byID);
			}
		}
		sContext.setByNameOrByIDs(byNameOrByIDsSEG);

		Set<ByNameOrByID> byNameOrByIDsDT = new HashSet<ByNameOrByID>();
		for (Datatype d : this.getDatatypes().getChildren()) {
			ByID byID = new ByID();
			byID.setByID("" + d.getId());
			if (d.getPredicates().size() > 0) {
				byID.setPredicates(d.getPredicates());
				byNameOrByIDsDT.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDsDT);

		constraints.setDatatypes(dtContext);
		constraints.setSegments(sContext);
		constraints.setGroups(gContext);
		return constraints;
	}
}
