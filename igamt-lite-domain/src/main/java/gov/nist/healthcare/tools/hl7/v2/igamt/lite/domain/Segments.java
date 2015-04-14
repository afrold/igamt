package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "segments")
public class Segments implements java.io.Serializable, Cloneable{

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

	public void delete(String id) {
		Segment d = findOne(id);
		if (d != null)
			this.children.remove(d);
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

	@Override
	public String toString() {
		return "Segments [id=" + id + "]";
	}
	
	
	@Override
	public Segments clone() throws CloneNotSupportedException {
		Segments clonedSegments = new Segments();
		clonedSegments.setChildren(new HashSet<Segment>());
		for(Segment s:this.children){
			clonedSegments.addSegment(s.clone());
		}
		
		return clonedSegments;
		
		
	}
}
