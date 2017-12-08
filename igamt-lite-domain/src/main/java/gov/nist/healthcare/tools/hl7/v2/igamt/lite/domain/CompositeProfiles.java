/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Ismail Mellouli (NIST) Mar 6, 2017
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class CompositeProfiles extends Library
    implements java.io.Serializable, Cloneable {


  private static final long serialVersionUID = 1L;

  private String id;
  /**
     * 
     */
  public CompositeProfiles() {
    super();
    this.setId(ObjectId.get().toString());
    type=Constant.COMPOSITEPROFILES;
    sectionPosition=3;
  }

  @DBRef
  private Set<CompositeProfileStructure> children = new HashSet<CompositeProfileStructure>();


  public Set<CompositeProfileStructure> getChildren() {
    return children;
  }

  public void setChildren(Set<CompositeProfileStructure> children) {
    this.children = children;
  }

  public void addChild(CompositeProfileStructure child) {
    this.children.add(child);
  }

  public void removeChild(String id) {
    CompositeProfileStructure toRemove = new CompositeProfileStructure();
    for (CompositeProfileStructure cps : this.children) {
      if (cps.getId().equals(id)) {
        toRemove = cps;
      }
    }
    this.children.remove(toRemove);
  }

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

}
