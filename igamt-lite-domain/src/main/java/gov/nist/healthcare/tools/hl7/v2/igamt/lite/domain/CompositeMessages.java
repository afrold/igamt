package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class CompositeMessages extends TextbasedSectionModel implements java.io.Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	  private String id;

	  /**
		 * 
		 */
	  public CompositeMessages() {
	    super();
	    this.id = ObjectId.get().toString();
	  }

	  @DBRef
	  private Set<CompositeMessage> children = new HashSet<CompositeMessage>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<CompositeMessage> getChildren() {
		return children;
	}

	public void setChildren(Set<CompositeMessage> children) {
		this.children = children;
	}
	public void addChild(CompositeMessage child){
		this.children.add(child);
	}
	  
}
