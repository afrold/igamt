/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 6, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.bson.types.ObjectId;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

public class CompositeProfile extends DataModelWithConstraints
		implements java.io.Serializable, Cloneable, Comparable<CompositeProfile> {

	public CompositeProfile() {
		super();
		this.type = Constant.COMPOSITEPROFILE;
	}

	private String coreProfileId;
	private List<ApplyInfo> profileComponents;

	private String identifier;

	private String name;

	private String messageType;

	private String event;

	private String structID;

	private String description;

	private List<SegmentRefOrGroup> children = new ArrayList<SegmentRefOrGroup>();

	protected Integer position = 0;

	protected String comment = "";

	protected String usageNote = "";

	protected String defPreText = "";

	protected String defPostText = "";

	Map<String, Segment> segmentsMap;
	Map<String, Datatype> datatypesMap;
	Map<String, Table> tablesMap;

	private List<ValueSetOrSingleCodeBinding> valueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();

	private List<Comment> comments = new ArrayList<Comment>();

	private List<SingleElementValue> singleElementValues = new ArrayList<SingleElementValue>();

	public String getCoreProfileId() {
		return coreProfileId;
	}

	public void setCoreProfileId(String coreProfileId) {
		this.coreProfileId = coreProfileId;
	}

	public List<ApplyInfo> getProfileComponents() {
		return profileComponents;
	}

	public void setProfileComponents(List<ApplyInfo> profileComponents) {
		this.profileComponents = profileComponents;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getStructID() {
		return structID;
	}

	public void setStructID(String structID) {
		this.structID = structID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<SegmentRefOrGroup> getChildren() {
		return children;
	}

	public void setChildren(List<SegmentRefOrGroup> children) {
		this.children = children;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
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

	public String getDefPreText() {
		return defPreText;
	}

	public void setDefPreText(String defPreText) {
		this.defPreText = defPreText;
	}

	public String getDefPostText() {
		return defPostText;
	}

	public void setDefPostText(String defPostText) {
		this.defPostText = defPostText;
	}

	public Map<String, Segment> getSegmentsMap() {
		return segmentsMap;
	}

	public void setSegmentsMap(Map<String, Segment> segmentsMap) {
		this.segmentsMap = segmentsMap;
	}

	public Map<String, Datatype> getDatatypesMap() {
		return datatypesMap;
	}

	public void setDatatypesMap(Map<String, Datatype> datatypesMap) {
		this.datatypesMap = datatypesMap;
	}

	public Map<String, Table> getTablesMap() {
		return tablesMap;
	}

	public void setTablesMap(Map<String, Table> tablesMap) {
		this.tablesMap = tablesMap;
	}

	public List<ValueSetOrSingleCodeBinding> getValueSetBindings() {
		return valueSetBindings;
	}

	public void setValueSetBindings(List<ValueSetOrSingleCodeBinding> valueSetBindings) {
		this.valueSetBindings = valueSetBindings;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<SingleElementValue> getSingleElementValues() {
		return singleElementValues;
	}

	public void setSingleElementValues(List<SingleElementValue> singleElementValues) {
		this.singleElementValues = singleElementValues;
	}

	@Override
	public int compareTo(CompositeProfile o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public List<ConformanceStatement> retrieveConformanceStatementsForConstant() {

		List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();

		for (SingleElementValue constant : this.singleElementValues) {
			String[] paths = constant.getLocation().split("\\.");
			String path = "";
			for (String p : paths) {
				path = path + "." + p + "[1]";
			}
			path = path.substring(1);

			String constraintId = constant.location;
			String description = this.getName() + "." + constant.getLocation() + "(" + constant.getName()
					+ ") SHALL contain the constant value '" + constant.getValue() + "'.";
			String assertion = "<Assertion><PlainText Path=\"" + path + "\" Text=\"" + constant.getValue()
					+ "\" IgnoreCase=\"false\"/></Assertion>";
			ConformanceStatement cs = new ConformanceStatement();
			cs.setId(ObjectId.get().toString());
			cs.setConstraintId(constraintId);
			cs.setDescription(description);
			cs.setAssertion(assertion);

			results.add(cs);

		}
		return results;
	}

	public List<ConformanceStatement> retrieveConformanceStatementsForSingleCode() {
		List<ConformanceStatement> results = new ArrayList<ConformanceStatement>();

		for (ValueSetOrSingleCodeBinding vsoscb : this.valueSetBindings) {
			if (vsoscb instanceof SingleCodeBinding) {
				SingleCodeBinding scb = (SingleCodeBinding) vsoscb;

				String[] paths = scb.getLocation().split("\\.");
				String path = "";
				for (String p : paths) {
					path = path + "." + p + "[1]";
				}
				path = path.substring(1);

				String constraintId = scb.getLocation();
				String description = this.getName() + "." + scb.getLocation() + " SHALL contain the constant value '"
						+ scb.getCode().getValue() + "' drawn from the code system '" + scb.getCode().getCodeSystem()
						+ "'.";
				String assertion = "<Assertion><PlainText Path=\"" + path + "\" Text=\"" + scb.getCode().getValue()
						+ "\" IgnoreCase=\"false\"/></Assertion>";
				ConformanceStatement cs = new ConformanceStatement();
				cs.setId(ObjectId.get().toString());
				cs.setConstraintId(constraintId);
				cs.setDescription(description);
				cs.setAssertion(assertion);

				results.add(cs);
			}
		}
		return results;
	}

	public List<ConformanceStatement> retrieveAllConformanceStatements() {
		List<ConformanceStatement> results = this.conformanceStatements;
		results.addAll(this.retrieveConformanceStatementsForSingleCode());
		results.addAll(this.retrieveConformanceStatementsForConstant());
		return results;
	}

	public Message convertMessage() throws CloneNotSupportedException {
		Message m = new Message();

		m.setChildren(new ArrayList<SegmentRefOrGroup>());
		for (SegmentRefOrGroup srog : this.children) {
			if (srog instanceof Group) {
				Group g = (Group) srog;
				Group clone = g.clone();
				clone.setId(ObjectId.get().toString());
				m.addSegmentRefOrGroup(clone);
			} else if (srog instanceof SegmentRef) {
				SegmentRef sr = (SegmentRef) srog;
				SegmentRef clone = sr.clone();
				clone.setId(ObjectId.get().toString());
				m.addSegmentRefOrGroup(clone);
			}
		}

		m.setId(ObjectId.get().toString());
		m.setComment(comment);
		m.setDescription(description);
		m.setEvent(event);
		m.setIdentifier(identifier);
		m.setMessageType(messageType);
		m.setPosition(position);
		m.setStructID(structID);
		m.setUsageNote(usageNote);
//		m.setMessageID(messageID);
		m.setType(type);
		m.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			m.addPredicate(cp.clone());
		}

		m.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			m.addConformanceStatement(cs.clone());
		}

		m.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
		for (ValueSetOrSingleCodeBinding vsb : this.valueSetBindings) {
			m.addValueSetBinding(vsb);
		}

		m.setComments(new ArrayList<Comment>());
		for (Comment c : this.comments) {
			m.addComment(c);
		}

		m.setSingleElementValues(new ArrayList<SingleElementValue>());
		for (SingleElementValue sev : this.singleElementValues) {
			m.addSingleElementValue(sev);
		}

		return m;
	}

}
