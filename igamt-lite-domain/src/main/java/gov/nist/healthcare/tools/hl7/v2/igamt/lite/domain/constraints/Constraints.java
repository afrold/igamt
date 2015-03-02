package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

public class Constraints implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	
	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="DATATYPES_ID")
 	private Context datatypes;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="SEGMENTS_ID")
	private Context segments;

	@OneToOne(optional = false,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
 	@JoinColumn(name="GROUPS_ID")
	private Context groups;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Context getDatatypes() {
		return datatypes;
	}

	public void setDatatypes(Context datatypes) {
		this.datatypes = datatypes;
	}

	public Context getSegments() {
		return segments;
	}

	public void setSegments(Context segments) {
		this.segments = segments;
	}

	public Context getGroups() {
		return groups;
	}

	public void setGroups(Context groups) {
		this.groups = groups;
	}

	
}
