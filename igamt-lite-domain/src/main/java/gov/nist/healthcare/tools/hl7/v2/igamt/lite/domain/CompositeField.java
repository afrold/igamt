package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;

public class CompositeField extends CompositeDataElement implements java.io.Serializable, Cloneable{

	private static final long serialVersionUID = 1L;

	  protected String id;

	  public CompositeField() {
	    super();
	    type = Constant.FIELD;
	    this.id = ObjectId.get().toString();
	  }

	  private String itemNo;

	  private Integer min;

	  private String max;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	  
}
