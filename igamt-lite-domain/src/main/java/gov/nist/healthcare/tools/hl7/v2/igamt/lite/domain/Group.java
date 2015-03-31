package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class Group extends SegmentRefOrGroup {

	private static final long serialVersionUID = 1L;

	public Group() {
		super();
		type = Constant.GROUP;
		this.id = ObjectId.get().toString();
	}

	private List<SegmentRefOrGroup> children = new ArrayList<SegmentRefOrGroup>();

	// @NotNull
	private String name;

	public List<SegmentRefOrGroup> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addSegmentsOrGroup(SegmentRefOrGroup e) {
		e.setPosition(this.children.size() + 1);
		this.children.add(e);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setChildren(List<SegmentRefOrGroup> children) {
		this.children = new ArrayList<SegmentRefOrGroup>();
		for (SegmentRefOrGroup child : children) {
			addSegmentsOrGroup(child);
		}
	}

	@Override
	public String toString() {
		return "Group [name=" + name + ", usage=" + usage + ", min=" + min
				+ ", max=" + max + "]";
	}

}
