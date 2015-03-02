package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="GROUPE") // GROUP is a keyword
public class Group extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	public Group() {
		super();
		type = Constant.GROUP;
	}
	
	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@OrderBy(value="position")
	private Set<SegmentRefOrGroup> segmentsOrGroups = new HashSet<SegmentRefOrGroup>();

	@NotNull
	@Column(nullable = false, name="GROUP_NAME")
	private String name;

	public Set<SegmentRefOrGroup> getSegmentsOrGroups() {
		return segmentsOrGroups;
	}

	public void setSegmentsOrGroups(Set<SegmentRefOrGroup> segmentsOrGroups) {
		this.segmentsOrGroups = segmentsOrGroups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addChild(SegmentRefOrGroup e) { 
		e.setPosition(segmentsOrGroups.size()+1);
		segmentsOrGroups.add(e);
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", segmentsOrGroups=" + segmentsOrGroups
				+ ", name=" + name + ", usage=" + usage + ", min=" + min
				+ ", max=" + max + "]";
	}
}
