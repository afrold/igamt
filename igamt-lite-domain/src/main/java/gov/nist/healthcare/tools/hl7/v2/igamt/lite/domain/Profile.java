package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "profile")
public class Profile extends DataModel implements java.io.Serializable,
		Cloneable {

	private static final long serialVersionUID = 1L;

	public Profile() {
		super();
		this.type = Constant.PROFILE;
		scope = ProfileScope.PRELOADED;
	}

	private ProfileScope scope;

	@Id
	private String id;

	private ProfileMetaData metaData;

	private Segments segments;

	private Datatypes datatypes;

	private Messages messages;

	private Tables tables;

	private Long accountId;

	protected String comment;

	protected String usageNote;

	private Boolean preloaded;

	private Integer version;

	private String changes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	//
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

	public Tables getTables() {
		return tables;
	}

	public void setTables(Tables tables) {
		this.tables = tables;
	}

	public ProfileScope getScope() {
		return scope;
	}

	public void setScope(ProfileScope scope) {
		this.scope = scope;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		// return "Profile [id=" + id + ", metaData=" + metaData + ", messages="
		// + messages;
		return "Profile [id=" + id + ", metaData=" + metaData;
	}

	@JsonIgnore
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

	@JsonIgnore
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

	public Predicate findOnePredicate(String predicateId) {
		Predicate predicate = this.getSegments().findOnePredicate(predicateId);
		if (predicate == null)
			predicate = this.getDatatypes().findOnePredicate(predicateId);
		return predicate;
	}

	public ConformanceStatement findOneConformanceStatement(
			String conformanceStatementId) {
		ConformanceStatement conformanceStatement = this.getSegments()
				.findOneConformanceStatement(conformanceStatementId);
		if (conformanceStatement == null)
			conformanceStatement = this.getDatatypes()
					.findOneConformanceStatement(conformanceStatementId);
		return conformanceStatement;
	}

	@Override
	public Profile clone() throws CloneNotSupportedException {
		Profile clonedProfile = new Profile();
		clonedProfile.setChanges(changes);
		clonedProfile.setComment(comment);
		clonedProfile.setDatatypes(datatypes.clone());
		clonedProfile.setMessages(messages.clone());
		clonedProfile.setMetaData(metaData.clone());
		clonedProfile.setSegments(segments.clone());
		clonedProfile.setTables(tables.clone());
		clonedProfile.setUsageNote(usageNote);
		clonedProfile.setVersion(version);
		clonedProfile.setAccountId(accountId);
		clonedProfile.setScope(scope);

		return clonedProfile;
	}

}
