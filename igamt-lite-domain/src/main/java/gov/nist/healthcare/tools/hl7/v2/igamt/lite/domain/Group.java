package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;

public class Group extends SegmentRefOrGroup implements Cloneable {

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

	public void setChildren(List<SegmentRefOrGroup> children) {
		this.children = new ArrayList<SegmentRefOrGroup>();
		for (SegmentRefOrGroup child : children) {
			addSegmentsOrGroup(child);
		}
	}

	public SegmentRefOrGroup findOneSegmentRefOrGroup(String id) {
		if (this.getId().equals(id)) {
			return this;
		}
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

	@Override
	public String toString() {
		return "Group [name=" + name + ", usage=" + usage + ", min=" + min
				+ ", max=" + max + "]";
	}

	public Group clone(HashMap<String, Datatype> dtRecords,
			HashMap<String, Segment> segmentRecords,
			HashMap<String, Table> tableRecords)
			throws CloneNotSupportedException {
		Group clonedGroup = new Group();

		clonedGroup.setChildren(new ArrayList<SegmentRefOrGroup>());
		for (SegmentRefOrGroup srog : this.children) {
			if (srog instanceof Group) {
				Group g = (Group) srog;
				clonedGroup.addSegmentsOrGroup(g.clone(dtRecords,
						segmentRecords, tableRecords));
			} else if (srog instanceof SegmentRef) {
				SegmentRef sr = (SegmentRef) srog;
				clonedGroup.addSegmentsOrGroup(sr.clone(dtRecords,
						segmentRecords, tableRecords));
			}
		}

		clonedGroup.setComment(name);
		clonedGroup.setMax(max);
		clonedGroup.setMin(min);
		clonedGroup.setName(name);
		clonedGroup.setPosition(position);
		clonedGroup.setUsage(usage);

		return clonedGroup;
	}

}
