package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segments")
public class Segments implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Set<Segment> children = new HashSet<Segment>();

	/**
	 * 
	 */
	public Segments() {
		super();
		this.id = ObjectId.get().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Segment> getChildren() {
		return children;
	}

	public void setChildren(Set<Segment> children) {
		this.children = children;
	}

	/**
	 * 
	 * @param s
	 */
	public void addSegment(Segment s) {
		children.add(s);
	}

	public Segment save(Segment s) {
		if (!this.children.contains(s)) {
			children.add(s);
		}
		return s;
	}

	public boolean delete(String id) {
		Segment d = findOne(id);
		if (d != null)
			return this.children.remove(d);
		return false;
	}

	public Segment findOne(String id) {
		if (this.children != null)
			for (Segment m : this.children) {
				if (m.getId().equals(id)) {
					return m;
				}
			}

		return null;
	}

	public Field findOneField(String id) {
		if (this.children != null) {
			for (Segment m : this.children) {
				Field c = m.findOneField(id);
				if (c != null) {
					return c;
				}
			}
		}
		return null;
	}

	public Component findOneComponent(String id) {
		if (this.children != null) {
			for (Segment m : this.children) {
				for (Field f : m.getFields()) {
					Component c = f.getDatatype().findOneComponent(id);
					if (c != null) {
						return c;
					}
				}
			}
		}
		return null;
	}

	public Predicate findOnePredicate(String predicateId) {
		for (Segment segment : this.getChildren()) {
			Predicate predicate = segment.findOnePredicate(predicateId);
			if (predicate != null) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(
			String conformanceStatementId) {
		for (Segment segment : this.getChildren()) {
			ConformanceStatement conf = segment
					.findOneConformanceStatement(conformanceStatementId);
			if (conf != null) {
				return conf;
			}
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		for (Segment segment : this.getChildren()) {
			if (segment.deletePredicate(predicateId)) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteConformanceStatement(String confStatementId) {
		for (Segment segment : this.getChildren()) {
			if (segment.deleteConformanceStatement(confStatementId)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Segments [id=" + id + "]";
	}

	public Segments clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Segment> segmentRecords,
			HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		Segments clonedSegments = new Segments();
		clonedSegments.setChildren(new HashSet<Segment>());
		for (Segment s : this.children) {
			if (!segmentRecords.containsKey(s.getId())) {
				Segment clonedSegment = s.clone(dtRecords, tableRecords);
				clonedSegments.addSegment(clonedSegment);
				segmentRecords.put(s.getId(), clonedSegment);
			} else {
				clonedSegments.addSegment(segmentRecords.get(s.getId()));
			}

		}

		return clonedSegments;

	}
}
