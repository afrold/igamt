package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

@Document(collection = "segment-library")
public class SegmentLibrary extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Long accountId;

	private String date;

	private String ext;

	private SegmentLibraryMetaData metaData;

	private Constant.SCOPE scope;
	
	public SegmentLibrary() {
		super();
		this.id = ObjectId.get().toString();
	}

	private Set<String> children = new HashSet<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Set<String> getChildren() {
		return children;
	}

	public void setChildren(Set<String> children) {
		this.children = children;
	}

	public Constant.SCOPE getScope() {
		return scope;
	}

	public void setScope(Constant.SCOPE scope) {
		this.scope = scope;
	}

	public void addSegment(String seg) {
		children.add(seg);
	}

	public String save(String seg) {
		if (!this.children.contains(seg)) {
			children.add(seg);
		}
		return seg;
	}

	public void delete(String id) {
		String seg = findOneSegmentById(id);
		if (seg != null)
			this.children.remove(seg);
	}

	public String findOneSegmentById(String id) {
		if (this.children != null) {
			for (String seg : this.children) {
				if (seg.equals(id)) {
					return seg;
				}
			}
		}

		return null;
	}
// TODO gcr not working
	public SegmentLibrary clone() throws CloneNotSupportedException {
		SegmentLibrary clonedSegments = new SegmentLibrary();
		return clonedSegments;
	}

	public void merge(SegmentLibrary segLib) {
		segLib.getChildren().addAll(segLib.getChildren());
	}

	public SegmentLibraryMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(SegmentLibraryMetaData metaData) {
		this.metaData = metaData;
	}
}
