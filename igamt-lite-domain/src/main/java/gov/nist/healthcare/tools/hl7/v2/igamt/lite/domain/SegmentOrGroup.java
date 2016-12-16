package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@Document(collection = "segment-group")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = CompositeGroup.class, name = Constant.GROUP),
    @JsonSubTypes.Type(value = CompositeSegmentRef.class, name = Constant.SEGMENTREF)})

public abstract class SegmentOrGroup extends DataModelWithConstraints implements
java.io.Serializable, Comparable<SegmentOrGroup>{

	
	
	private static final long serialVersionUID = 1L;
	public SegmentOrGroup() {
	    super();
	  }
	@Id
	  protected String id;
	  protected Usage usage; 
	  protected Integer min;
	  protected String max;
	  protected Integer position = 0;
	  protected String comment = "";
	  
	  
	  
	   
	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public Usage getUsage() {
		return usage;
	}




	public void setUsage(Usage usage) {
		this.usage = usage;
	}




	public Integer getMin() {
		return min;
	}




	public void setMin(Integer min) {
		this.min = min;
	}




	public String getMax() {
		return max;
	}




	public void setMax(String max) {
		this.max = max;
	}




	public Integer getPosition() {
		return position;
	}




	public void setPosition(Integer position) {
		this.position = position;
	}




	public String getComment() {
		return comment;
	}




	public void setComment(String comment) {
		this.comment = comment;
	}




	@Override
	public int compareTo(SegmentOrGroup o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
