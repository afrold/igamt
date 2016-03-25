package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bson.types.ObjectId;

public class Message extends SectionModel implements java.io.Serializable,
Cloneable, Comparable<Message> {

	private static final long serialVersionUID = 1L;

	public Message() {
		super();
		this.type = Constant.MESSAGE;
		this.id = ObjectId.get().toString();
	}

	private String id;

	private String identifier;																//Message/@Identifier
	
	private String messageID;

	private String name;																	//Message/@Name
	
	private String messageType;																//Message/@Type

	private String event;																	//Message/@Event

	private String structID;																//Message/@StructID

	private String description;																//Message/@Description

	private List<SegmentRefOrGroup> children = new ArrayList<SegmentRefOrGroup>();

	// @DBRef
	// private Messages messages;

	// @NotNull
	protected Integer position = 0;

	protected String comment = "";

	protected String usageNote = "";

	protected List<Predicate> predicates = new ArrayList<Predicate>();

	protected List<ConformanceStatement> conformanceStatements = new ArrayList<ConformanceStatement>();

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

	public void addSegmentRefOrGroup(SegmentRefOrGroup e) {
		e.setPosition(children.size() + 1);
		this.children.add(e);
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

	public void addPredicate(Predicate p) {
		predicates.add(p);
	}

	public void addConformanceStatement(ConformanceStatement cs) {
		conformanceStatements.add(cs);
	}

	public List<Predicate> getPredicates() {
		return predicates;
	}

	public List<ConformanceStatement> getConformanceStatements() {
		return conformanceStatements;
	}

	public void setPredicates(List<Predicate> predicates) {
		this.predicates = predicates;
	}

	public void setConformanceStatements(
			List<ConformanceStatement> conformanceStatements) {
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

	public Predicate findOnePredicate(String predicateId) {
		for (Predicate predicate : this.getPredicates()) {
			if (predicate.getId().equals(predicateId)) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(String confId) {
		for (ConformanceStatement conf : this.getConformanceStatements()) {
			if (conf.getId().equals(confId)) {
				return conf;
			}
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		Predicate p = findOnePredicate(predicateId);
		return p != null && this.getPredicates().remove(p);
	}

	public boolean deleteConformanceStatement(String cId) {
		ConformanceStatement c = findOneConformanceStatement(cId);
		return c != null && this.getConformanceStatements().remove(c);
	}


	@Override
	public String toString() {
		return "Message [id=" + id + ", identifier=" + identifier
				+ ", messageID=" + messageID + ", name=" + name
				+ ", messageType=" + messageType + ", event=" + event
				+ ", structID=" + structID + ", description=" + description
				+ ", children=" + children + ", position=" + position
				+ ", comment=" + comment + ", usageNote=" + usageNote
				+ ", predicates=" + predicates + ", conformanceStatements="
				+ conformanceStatements + "]";
	}
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
		clonedMessage
		.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedMessage.addConformanceStatement(cs.clone());
		}
		clonedMessage.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedMessage.addPredicate(cp.clone());
		}
		return clonedMessage;
	}
	
	public Message clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Segment> segmentRecords,
			HashMap<String, Table> tableRecords)
					throws CloneNotSupportedException {
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
		clonedMessage
		.setConformanceStatements(new ArrayList<ConformanceStatement>());
		for (ConformanceStatement cs : this.conformanceStatements) {
			clonedMessage.addConformanceStatement(cs.clone());
		}
		clonedMessage.setPredicates(new ArrayList<Predicate>());
		for (Predicate cp : this.predicates) {
			clonedMessage.addPredicate(cp.clone());
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
		int x = String.CASE_INSENSITIVE_ORDER.compare(this.getMessageType() != null && this.getEvent() != null ? this.getMessageType() + this.getEvent() : "",
				o.getMessageType() != null && this.getEvent() != null ? o.getMessageType() + this.getEvent() : "");
		if (x == 0) {
			x = (this.getMessageType() != null  && this.getEvent() != null ? this.getMessageType() + this.getEvent() : "").compareTo(o.getMessageType() != null && this.getEvent() != null ? o.getMessageType()+o.getEvent(): "");
		}
		return x;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
}
