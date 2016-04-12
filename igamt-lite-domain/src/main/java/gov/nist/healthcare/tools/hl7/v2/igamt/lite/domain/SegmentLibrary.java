package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	public SegmentLibrary() {
		super();
	}

	@DBRef
	private Set<Segment> children = new HashSet<Segment>();

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

	public Set<Segment> getChildren() {
		return children;
	}

	public void setChildren(Set<Segment> children) {
		this.children = children;
	}

	public void addSegment(Segment seg) {
		seg.setLibId(ext);
		children.add(seg);
	}

	public Segment save(Segment seg) {
		if (!this.children.contains(seg)) {
			children.add(seg);
		}
		return seg;
	}

	public void delete(String id) {
		Segment seg = findOne(id);
		if (seg != null)
			this.children.remove(seg);
	}

	public Segment findOne(String id) {
		if (this.children != null) {
			for (Segment seg : this.children) {
				if (seg.getId().equals(id)) {
					return seg;
				}
			}
		}

		return null;
	}

	public Segment findOneByNameAndByLabel(String name, String label) {
		if (this.children != null) {
			for (Segment seg : this.children) {
				if (seg.getName().equals(name) 
						&& seg.getLabel().equals(label)) {
					return seg;
				}
			}
		}
		return null;
	}

	public Field findOneField(String id, Segment seg) {
		if (seg.getFields() != null) {
			for (Field fld : seg.getFields()) {
				if (fld.getId().equals(id)) {
					return fld;
				}
			}
		}
		return null;
	}

	public Segment findOneSegmentByLabel(String label) {
		if (this.children != null)
			for (Segment seg : this.children) {
				if (seg.getLabel().equals(label)) {
					return seg;
				}
			}
		return null;
	}
	
	public Segment findOneSegmentByBase(String baseName){
		if (this.children != null)
			for (Segment seg : this.children){
				if(seg.getName().equals(baseName)) {
					return seg;
				}
			}
		return null;
	}

	public Predicate findOnePredicate(String predicateId) {
		for (Segment seg : this.getChildren()) {
			Predicate predicate = seg.findOnePredicate(predicateId);
			if (predicate != null) {
				return predicate;
			}
		}
		return null;
	}

	public ConformanceStatement findOneConformanceStatement(
			String conformanceStatementId) {
		for (Segment seg : this.getChildren()) {
			ConformanceStatement conf = seg
					.findOneConformanceStatement(conformanceStatementId);
			if (conf != null) {
				return conf;
			}
		}
		return null;
	}

	public boolean deletePredicate(String predicateId) {
		for (Segment seg : this.getChildren()) {
			if (seg.deletePredicate(predicateId)) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteConformanceStatement(String confStatementId) {
		for (Segment seg : this.getChildren()) {
			if (seg.deleteConformanceStatement(confStatementId)) {
				return true;
			}
		}
		return false;
	}

	public SegmentLibrary clone(HashMap<String, Segment> segRecords, HashMap<String, Datatype> dtRecords,
			HashMap<String, Table> tabRecords)
			throws CloneNotSupportedException {
		SegmentLibrary clonedSegments = new SegmentLibrary();
		clonedSegments.setChildren(new HashSet<Segment>());
		for (Segment seg : this.children) {
			if (segRecords.containsKey(seg.getId())) {
				clonedSegments.addSegment(segRecords.get(seg.getId()));
			} else {
				Segment clone = seg.clone(dtRecords, tabRecords);
				clone.setId(seg.getId());
				segRecords.put(seg.getId(), clone);
				clonedSegments.addSegment(clone);
			}
		}

		return clonedSegments;
	}
	
	public void merge(SegmentLibrary segLib){
		for (Segment seg : segLib.getChildren()){
			if (this.findOneByNameAndByLabel(seg.getName(), seg.getLabel()) == null){
				this.addSegment(seg);
			} else {
				seg.setId(this.findOneByNameAndByLabel(seg.getName(), seg.getLabel()).getId()); //FIXME Probably useless...
			}
		}
		
	}
	
	public void setPositionsOrder(){
		List<Segment> sortedList = new ArrayList<Segment>(this.getChildren());
		Collections.sort(sortedList);
		for (Segment seg: sortedList) {
			seg.setSectionPosition(sortedList.indexOf(seg));
		}
	}
	
	@JsonIgnore
	public Constraints getConformanceStatements() {
		//TODO Only byID constraints are considered; might want to consider byName
		Constraints constraints = new Constraints();
		Context dtContext = new Context();

		Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
		byNameOrByIDs = new HashSet<ByNameOrByID>();
		for (Segment seg : this.getChildren()) {
			ByID byID = new ByID();
			byID.setByID(seg.getLabel());
			if (seg.getConformanceStatements().size() > 0) {
				byID.setConformanceStatements(seg.getConformanceStatements());
				byNameOrByIDs.add(byID);
			}
		}
		dtContext.setByNameOrByIDs(byNameOrByIDs);

		constraints.setSegments(dtContext);
		return constraints;
	}

	public SegmentLibraryMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(SegmentLibraryMetaData metaData) {
		this.metaData = metaData;
	}
}
