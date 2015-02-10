package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.codehaus.jackson.map.annotate.JsonView;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Segments implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@JsonView({Views.Profile.class})
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonView({Views.Profile.class})
	@OneToMany(mappedBy = "segments", cascade = CascadeType.ALL)
	private final Set<Segment> segments = new HashSet<Segment>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Profile profile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Segment> getSegments() {
		return segments;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public void addSegment(Segment s) {
		if (s.getSegments() != null) {
			throw new IllegalArgumentException(
					"This segment already belong to a different segment library");
		}
		segments.add(s);
		s.setSegments(this);
	}

	@Override
	public String toString() {
		return "Segments [id=" + id + ", segments=" + segments + "]";
	}

}
