package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

@Document(collection = "message")
public class Message extends DataModelWithConstraints implements java.io.Serializable, Cloneable, Comparable<Message> {

	private static final long serialVersionUID = 1L;

	public Message() {
		super();
		this.type = Constant.MESSAGE;
	}

	@Id
	private String id;

	private String identifier; // Message/@Identifier

	private String messageID;

	private String name; // Message/@Name

	private String messageType; // Message/@Type

	private String event; // Message/@Event

	private String structID; // Message/@StructID

	private String description; // Message/@Description
	private List<String> compositeProfileStructureList;

	private List<ValueSetOrSingleCodeBinding> valueSetBindings = new ArrayList<ValueSetOrSingleCodeBinding>();

	private List<Comment> comments = new ArrayList<Comment>();

	private List<SingleElementValue> singleElementValues = new ArrayList<SingleElementValue>();

	public List<String> getCompositeProfileStructureList() {
		return compositeProfileStructureList;
	}

	public void setCompositeProfileStructureList(List<String> compositeProfileStructureList) {
		this.compositeProfileStructureList = compositeProfileStructureList;
	}

	public void addCompositeProfileStructure(String id) {
		if (this.compositeProfileStructureList == null) {
			this.compositeProfileStructureList = new ArrayList<>();
		}
		this.compositeProfileStructureList.add(id);
	}

	public void removeCompositeProfileStructure(String id) {
		String toRemove = "";
		for (String s : this.compositeProfileStructureList) {
			if (s.equals(id)) {
				toRemove = s;

			}
		}
		this.compositeProfileStructureList.remove(toRemove);
	}

	private List<SegmentRefOrGroup> children = new ArrayList<SegmentRefOrGroup>();

	protected Integer position = 0;

	protected String comment = "";

	protected String usageNote = "";

	protected String defPreText = "";

	protected String defPostText = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	//
	// public Messages getMessages() {
	// return messages;
	// }
	//
	// public void setMessages(Messages messages) {
	// this.messages = messages;
	// }

	public List<SegmentRefOrGroup> getChildren() {
		return children;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public List<SingleElementValue> getSingleElementValues() {
		return singleElementValues;
	}

	public void setSingleElementValues(List<SingleElementValue> singleElementValues) {
		this.singleElementValues = singleElementValues;
	}

	public void addSegmentRefOrGroup(SegmentRefOrGroup e) {
		e.setPosition(children.size() + 1);
		this.children.add(e);
	}

	public void addValueSetBinding(ValueSetOrSingleCodeBinding vsb) {
		valueSetBindings.add(vsb);
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public void addSingleElementValue(SingleElementValue sev) {
		singleElementValues.add(sev);
	}

	public void setChildren(List<SegmentRefOrGroup> children) {
		this.children = new ArrayList<SegmentRefOrGroup>();
		for (SegmentRefOrGroup child : children) {
			addSegmentRefOrGroup(child);
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	public void addPredicate(Predicate p) {
		predicates.add(p);
	}

	@Override
	public void addConformanceStatement(ConformanceStatement cs) {
		conformanceStatements.add(cs);
	}

	@Override
	public List<Predicate> getPredicates() {
		return predicates;
	}

	@Override
	public List<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}

	@Override
	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	@Override
	public void setConformanceStatements(List<ConformanceStatement> conformanceStatements) {
		this.conformanceStatements = conformanceStatements;
	}

	public Boolean deleteSegmentRefOrGroup(String id) {
		if (this.getChildren() != null) {
			for (int i = 0; i < this.getChildren().size(); i++) {
				SegmentRefOrGroup m = this.getChildren().get(i);
				if (m.getId().equals(id)) {
					return this.getChildren().remove(m);
				} else if (m instanceof Group) {
					Group gr = (Group) m;
					Boolean result = gr.deleteSegmentRefOrGroup(id);
					if (result) {
						return result;
					}
				}
			}
		}
		return false;
	}

	public SegmentRefOrGroup findOneSegmentRefOrGroup(String id) {
		if (this.getChildren() != null) {
			for (SegmentRefOrGroup m : this.getChildren()) {
				if (m instanceof SegmentRef) {
					if (m.getId().equals(id)) {
						return m;
					}
				} else if (m instanceof Group) {
					Group gr = (Group) m;
					SegmentRefOrGroup tmp = gr.findOneSegmentRefOrGroup(id);
					if (tmp != null) {
						return tmp;
					}
				}
			}
		}
		return null;
	}

	@Override
	public Predicate findOnePredicate(String predicateId) {
		for (Predicate predicate : this.getPredicates()) {
			if (predicate.getId().equals(predicateId)) {
				return predicate;
			}
		}
		return null;
	}

	@Override
	public ConformanceStatement findOneConformanceStatement(String confId) {
		for (ConformanceStatement conf : this.getConformanceStatements()) {
			if (conf.getId().equals(confId)) {
				return conf;
			}
		}
		return null;
	}

	@Override
	public boolean deletePredicate(String predicateId) {
		Predicate p = findOnePredicate(predicateId);
		return p != null && this.getPredicates().remove(p);
	}

	@Override
	public boolean deleteConformanceStatement(String cId) {
		ConformanceStatement c = findOneConformanceStatement(cId);
		return c != null && this.getConformanceStatements().remove(c);
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", identifier=" + identifier + ", messageID=" + messageID + ", name=" + name
				+ ", messageType=" + messageType + ", event=" + event + ", structID=" + structID + ", description="
				+ description + ", children=" + children + ", position=" + position + ", comment=" + comment
				+ ", usageNote=" + usageNote + ", predicates=" + predicates + ", conformanceStatements="
				+ conformanceStatements + "]";
	}

	@Override
	public Message clone() throws CloneNotSupportedException {
		Message clonedMessage = new Message();

		clonedMessage.setChildren(new ArrayList<SegmentRefOrGroup>());
		for (SegmentRefOrGroup srog : this.children) {
			if (srog instanceof Group) {
				Group g = (Group) srog;
				Group clone = g.clone();
				clone.setId(g.getId());
				clonedMessage.addSegmentRefOrGroup(clone);
			} else if (srog instanceof SegmentRef) {
				SegmentRef sr = (SegmentRef) srog;
				SegmentRef clone = sr.clone();
				clone.setId(sr.getId());
				clonedMessage.addSegmentRefOrGroup(clone);
			}
		}

		clonedMessage.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedMessage.addPredicate(cp.clone());
		}

		clonedMessage.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedMessage.addConformanceStatement(cs.clone());
		}

		clonedMessage.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
		for (ValueSetOrSingleCodeBinding vsb : this.valueSetBindings) {
			clonedMessage.addValueSetBinding(vsb);
		}

		clonedMessage.setComments(new ArrayList<Comment>());
		for (Comment c : this.comments) {
			clonedMessage.addComment(c);
		}

		clonedMessage.setSingleElementValues(new ArrayList<SingleElementValue>());
		for (SingleElementValue sev : this.singleElementValues) {
			clonedMessage.addSingleElementValue(sev);
		}

		clonedMessage.setId(ObjectId.get().toString());
		clonedMessage.setComment(comment);
		clonedMessage.setDescription(description);
		clonedMessage.setEvent(event);
		clonedMessage.setIdentifier(identifier);
		clonedMessage.setMessageType(messageType);
		clonedMessage.setPosition(position);
		clonedMessage.setStructID(structID);
		clonedMessage.setUsageNote(usageNote);
		clonedMessage.setMessageID(messageID);
		clonedMessage.setType(type);
		clonedMessage.setHl7Version(hl7Version);
		clonedMessage.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedMessage.addConformanceStatement(cs.clone());
		}
		clonedMessage.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedMessage.addPredicate(cp.clone());
		}
		return clonedMessage;
	}

	public Message clone(HashMap<String, Datatype> dtRecords, HashMap<String, Segment> segmentRecords,
			HashMap<String, Table> tableRecords) throws CloneNotSupportedException {
		Message clonedMessage = new Message();

		clonedMessage.setChildren(new ArrayList<SegmentRefOrGroup>());
		for (SegmentRefOrGroup srog : this.children) {
			if (srog instanceof Group) {
				Group g = (Group) srog;
				Group clone = g.clone();
				clone.setId(g.getId());
				clonedMessage.addSegmentRefOrGroup(clone);
			} else if (srog instanceof SegmentRef) {
				SegmentRef sr = (SegmentRef) srog;
				SegmentRef clone = sr.clone();
				clone.setId(sr.getId());
				clonedMessage.addSegmentRefOrGroup(clone);
			}
		}

		clonedMessage.setId(ObjectId.get().toString());
		clonedMessage.setComment(comment);
		clonedMessage.setDescription(description);
		clonedMessage.setEvent(event);
		clonedMessage.setIdentifier(identifier);
		clonedMessage.setMessageType(messageType);
		clonedMessage.setPosition(position);
		clonedMessage.setStructID(structID);
		clonedMessage.setUsageNote(usageNote);
		clonedMessage.setMessageID(messageID);
		clonedMessage.setType(type);
		clonedMessage.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedMessage.addPredicate(cp.clone());
		}

		clonedMessage.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedMessage.addConformanceStatement(cs.clone());
		}

		clonedMessage.setValueSetBindings(new ArrayList<ValueSetOrSingleCodeBinding>());
		for (ValueSetOrSingleCodeBinding vsb : this.valueSetBindings) {
			clonedMessage.addValueSetBinding(vsb);
		}

		clonedMessage.setComments(new ArrayList<Comment>());
		for (Comment c : this.comments) {
			clonedMessage.addComment(c);
		}

		clonedMessage.setSingleElementValues(new ArrayList<SingleElementValue>());
		for (SingleElementValue sev : this.singleElementValues) {
			clonedMessage.addSingleElementValue(sev);
		}

		return clonedMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Message o) {

		return this.position - o.getPosition();
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
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

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<ValueSetOrSingleCodeBinding> getValueSetBindings() {
		return valueSetBindings;
	}

	public void setValueSetBindings(List<ValueSetOrSingleCodeBinding> valueSetBindings) {
		this.valueSetBindings = valueSetBindings;
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
			String description = this.getName() + "." + constant.getLocation() + "(" + constant.getName() + ") SHALL contain the constant value '" + constant.getValue() + "'.";
			String assertion = "<Assertion><AND><Presence Path=\"" + path + "\"/><PlainText Path=\"" + path + "\" Text=\"" + constant.getValue() + "\" IgnoreCase=\"false\"/></AND></Assertion>";
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
				String assertion = "";
				if(scb.isCodedElement()){
		          assertion = "<Assertion>" 
		                              + "<AND>" 
		                              + "<AND><Presence Path=\"" + path + ".1[1]" + "\"/><PlainText Path=\"" + path + ".1[1]" + "\" Text=\"" + scb.getCode().getValue() + "\" IgnoreCase=\"false\"/></AND>" 
		                              + "<AND><Presence Path=\"" + path + ".3[1]" + "\"/><PlainText Path=\"" + path + ".3[1]" + "\" Text=\"" + scb.getCode().getCodeSystem() + "\" IgnoreCase=\"false\"/></AND>" 
		                              + "</AND>"
		                              + "</Assertion>";
		        }else {
		          assertion = "<Assertion><AND><Presence Path=\"" + path + "\"/><PlainText Path=\"" + path + "\" Text=\"" + scb.getCode().getValue() + "\" IgnoreCase=\"false\"/></AND></Assertion>";          
		        }
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
	
	public SegmentRefOrGroup findChildByPosition(Integer position){
		for(SegmentRefOrGroup child:this.children){
			if(child.getPosition().equals(position)) return child;
		}
		return null;
	}

}
