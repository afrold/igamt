package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class Profile extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	public Profile() {
		super();
		this.type = Constant.PROFILE;
		scope = IGDocumentScope.PRELOADED;
		this.id = ObjectId.get().toString();
	}

	private IGDocumentScope scope;

	private String id;

	private ProfileMetaData metaData;

	@JsonIgnoreProperties(value= {"accountId", "date"})
	@DBRef
	private SegmentLibrary segmentLibrary;
	
	@DBRef
	private DatatypeLibrary datatypeLibrary;

	private Messages messages;

	@DBRef
	private TableLibrary tableLibrary;

	private Long accountId;

	protected String comment = "";

	protected String usageNote = "";

	private String changes = "";

	private String baseId = null; // baseId is the original version of the
									// profile that was cloned

	private String constraintId;

	private String sourceId;

	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

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

	public SegmentLibrary getSegmentLibrary() {
		return segmentLibrary;
	}

	public void setSegmentLibrary(SegmentLibrary segmentLibrary) {
		this.segmentLibrary = segmentLibrary;
	}

	public DatatypeLibrary getDatatypeLibrary() {
		return datatypeLibrary;
	}

	public void setDatatypeLibrary(DatatypeLibrary datatypeLibrary) {
		this.datatypeLibrary = datatypeLibrary;
	}

	public TableLibrary getTableLibrary() {
		return tableLibrary;
	}

	public void setTableLibrary(TableLibrary tableLibrary) {
		this.tableLibrary = tableLibrary;
	}

	public Messages getMessages() {
		return messages;
	}

	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	public String getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(String constraintId) {
		this.constraintId = constraintId;
	}

	/**
	 * Do not set the version. Hibernate set the version automatically
	 * 
	 * @param version
	 * @return
	 */
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

	public IGDocumentScope getScope() {
		return scope;
	}

	public void setScope(IGDocumentScope scope) {
		this.scope = scope;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public String toString() {
		// return "Profile [id=" + id + ", metaData=" + metaData + ", messages="
		// + messages;
		return "Profile [id=" + id + ", metaData=" + metaData;
	}

	@JsonIgnore
	public Constraints getConformanceStatements() {
		// TODO Only byID constraints are considered; might want to consider
		// byName
		Constraints constraints = new Constraints();
		Context dtContext = new Context();
		Context sContext = new Context();
		Context gContext = new Context();
		Context mContext = new Context();

		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Message m : this.getMessages().getChildren()) {
			ByID byID = new ByID();
			byID.setByID(m.getMessageID());
			if (m.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(m.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		mContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Message m : this.getMessages().getChildren()) {
			for (SegmentRefOrGroup srog : m.getChildren()) {
				if (srog instanceof Group) {
					Group g = (Group) srog;
					ByID byID = new ByID();
					byID.setByID("" + g.getName());
					if (g.getConformanceStatements().size() > 0) {
						byID.setConformanceStatements(g.getConformanceStatements());
						byNameOrByIDs.add(byID);
					}
				}
			}
		}
		gContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Segment s : this.getSegmentLibrary().getChildren()) {
			ByID byID = new ByID();
			byID.setByID(s.getLabel());
			if (s.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(s.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		sContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Datatype d : this.getDatatypeLibrary().getChildren()) {
			ByID byID = new ByID();
			byID.setByID(d.getLabel());
			if (d.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(d.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDs);

		constraints.setDatatypes(dtContext);
		constraints.setSegments(sContext);
		constraints.setGroups(gContext);
		constraints.setMessages(mContext);
		return constraints;
	}

	@JsonIgnore
	public Constraints getPredicates() {
		// TODO Only byID constraints are considered; might want to consider
		// byName
		Constraints constraints = new Constraints();
		Context dtContext = new Context();
		Context sContext = new Context();
		Context gContext = new Context();
		Context mContext = new Context();

		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Message m : this.getMessages().getChildren()) {
			ByID byID = new ByID();
			byID.setByID(m.getMessageID());
			if (m.getPredicates().size() > 0) {
				byID.setPredicates(m.getPredicates());
				byNameOrByIDs.add(byID);
			}
		}
		mContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Message m : this.getMessages().getChildren()) {
			for (SegmentRefOrGroup srog : m.getChildren()) {
				if (srog instanceof Group) {
					Group g = (Group) srog;
					ByID byID = new ByID();
					byID.setByID(g.getName());
					if (g.getPredicates().size() > 0) {
						byID.setPredicates(g.getPredicates());
						byNameOrByIDs.add(byID);
					}
				}
			}
		}
		gContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Segment s : this.getSegmentLibrary().getChildren()) {
			ByID byID = new ByID();
			byID.setByID(s.getLabel());
			if (s.getPredicates().size() > 0) {
				byID.setPredicates(s.getPredicates());
				byNameOrByIDs.add(byID);
			}
		}
		sContext.setByNameOrByIDs(byNameOrByIDs);

		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Datatype d : this.getDatatypeLibrary().getChildren()) {
			ByID byID = new ByID();
			byID.setByID(d.getLabel());
			if (d.getPredicates().size() > 0) {
				byID.setPredicates(d.getPredicates());
				byNameOrByIDs.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDs);

		constraints.setDatatypes(dtContext);
		constraints.setSegments(sContext);
		constraints.setGroups(gContext);
		constraints.setMessages(mContext);
		return constraints;
	}

	public Predicate findOnePredicate(String predicateId) {
		for (Message m : this.messages.getChildren()) {
			if (m.findOnePredicate(predicateId) != null) {
				return m.findOnePredicate(predicateId);
			}
			for (SegmentRefOrGroup srog : m.getChildren()) {
				// TODO Check depth of search
				if (srog instanceof Group) {
					if (((Group) srog).findOnePredicate(predicateId) != null) {
						return ((Group) srog).findOnePredicate(predicateId);
					}
				}
			}
		}
		if (this.getSegmentLibrary().findOnePredicate(predicateId) != null) {
			return this.getSegmentLibrary().findOnePredicate(predicateId);
		} else if (this.getDatatypeLibrary().findOnePredicate(predicateId) != null) {
			return this.getDatatypeLibrary().findOnePredicate(predicateId);
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(String conformanceStatementId) {
		for (Message m : this.messages.getChildren()) {
			if (m.findOneConformanceStatement(conformanceStatementId) != null) {
				return m.findOneConformanceStatement(conformanceStatementId);
			}
			for (SegmentRefOrGroup srog : m.getChildren()) {
				// TODO Check depth of search
				if (srog instanceof Group) {
					if (((Group) srog).findOneConformanceStatement(conformanceStatementId) != null) {
						return ((Group) srog).findOneConformanceStatement(conformanceStatementId);
					}
				}
			}
		}
		if (this.getSegmentLibrary().findOneConformanceStatement(conformanceStatementId) != null) {
			return this.getSegmentLibrary().findOneConformanceStatement(conformanceStatementId);
		} else if (this.getDatatypeLibrary().findOneConformanceStatement(conformanceStatementId) != null) {
			return this.getDatatypeLibrary().findOneConformanceStatement(conformanceStatementId);
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		for (Message m : this.messages.getChildren()) {
			if (m.deletePredicate(predicateId)) {
				return true;
			}
			for (SegmentRefOrGroup srog : m.getChildren()) {
				// TODO Check depth of search
				if (srog instanceof Group) {
					if (((Group) srog).deletePredicate(predicateId)) {
						return true;
					}
				}
			}
		}
		if (this.getSegmentLibrary().deletePredicate(predicateId)) {
			return true;
		}

		if (this.getDatatypeLibrary().deletePredicate(predicateId)) {
			return true;
		}

		return false;
	}

	public boolean deleteConformanceStatement(String confStatementId) {
		for (Message m : this.messages.getChildren()) {
			if (m.deleteConformanceStatement(confStatementId)) {
				return true;
			}
			for (SegmentRefOrGroup srog : m.getChildren()) {
				// TODO Check depth of search
				if (srog instanceof Group) {
					if (((Group) srog).deleteConformanceStatement(confStatementId)) {
						return true;
					}
				}
			}
		}
		if (this.getSegmentLibrary().deleteConformanceStatement(confStatementId)) {
			return true;
		}

		if (this.getDatatypeLibrary().deleteConformanceStatement(confStatementId)) {
			return true;
		}

		return false;
	}

	@Override
	public Profile clone() throws CloneNotSupportedException {
		Profile clonedProfile = new Profile();
		HashMap<String, Datatype> dtRecords = new HashMap<String, Datatype>();
		HashMap<String, Segment> segRecords = new HashMap<String, Segment>();
		HashMap<String, Table> tabRecords = new HashMap<String, Table>();

		clonedProfile.setChanges(changes);
		clonedProfile.setComment(comment);
		clonedProfile.setDatatypeLibrary(datatypeLibrary.clone(dtRecords, tabRecords));
		clonedProfile.setSegmentLibrary(segmentLibrary.clone(segRecords, dtRecords, tabRecords));
		clonedProfile.setTableLibrary(tableLibrary.clone(tabRecords));

		clonedProfile.setMessages(messages.clone(dtRecords, segRecords, tabRecords));
		clonedProfile.setMetaData(metaData.clone());
		clonedProfile.setUsageNote(usageNote);
		clonedProfile.setAccountId(accountId);
		clonedProfile.setScope(scope);
		clonedProfile.setBaseId(baseId != null ? baseId : id);
		clonedProfile.setSourceId(id);
		clonedProfile.setConstraintId(constraintId);

		return clonedProfile;
	}

	public void merge(Profile p) {
		// Note: merge is used for creation of new profiles do we don't consider
		// constraints and annotations
		// in each profile, there is one message library with one message
		this.tableLibrary.merge(p.getTableLibrary());
		this.datatypeLibrary.merge(p.getDatatypeLibrary());
		this.segmentLibrary.merge(p.getSegmentLibrary());

		for (Message m : p.getMessages().getChildren()) {
			this.messages.addMessage(m);
		}
	}
}
