package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segments")
public class Segments implements java.io.Serializable {

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
		// if (s.getSegments() != null) {
		// throw new IllegalArgumentException(
		// "This segment already belong to a different segment library");
		// }
		children.add(s);
		// s.setSegments(this);
	}

	@Override
	public String toString() {
		return "Segments [id=" + id + "]";
	}
}
