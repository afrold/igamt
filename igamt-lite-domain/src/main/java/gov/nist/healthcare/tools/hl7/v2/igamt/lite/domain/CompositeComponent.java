package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.bson.types.ObjectId;

public class CompositeComponent extends CompositeDataElement implements Cloneable{

	 private static final long serialVersionUID = 1L;

	  private String id;
	  
	  public CompositeComponent() {
	    super();
	    this.type = Constant.COMPONENT;
	    this.id = ObjectId.get().toString();
	  }

}
