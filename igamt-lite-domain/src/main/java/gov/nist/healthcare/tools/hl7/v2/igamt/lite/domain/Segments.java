package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

 
 
@Entity
@Table(name="SEGMENTS")
public class Segments implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

 	@Id
 	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

 	@OneToMany(mappedBy = "segments",fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy(value="position")
	private final Set<Segment> segments = new HashSet<Segment>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Segment> getSegments() {
		return segments;
	}

	public void addSegment(Segment s) {
		if (s.getSegments() != null) {
			throw new IllegalArgumentException(
					"This segment already belong to a different segment library");
		}
		s.setPosition(segments.size() +1);
		segments.add(s);
		s.setSegments(this);
	}

	@Override
	public String toString() {
		return "Segments [id=" + id + ", segments=" + segments + "]";
	}

}
