/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents;

import java.util.HashSet;
import java.util.Set;

/**
 * A data transfer object used to transfer a message structure id and its related events.
 * 
 * @author gcr1
 *
 */
public class MessageEvents {

  private String id;

  private String name;

  private final String type = "message";

  private Set<Event> children = new HashSet<Event>();

  private String description;

  public MessageEvents() {
    super();
  }

  public MessageEvents(String id, String structId, Set<String> events, String description) {
    this.id = id;
    this.name = structId;
    createEvents(events,structId);
    this.description = description;
  }

  void createEvents(Set<String> events,String parentStructId) {
    for (String event : events) {
      this.children.add(new Event(id, event,parentStructId));
    }
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Set<Event> getChildren() {
    return children;
  }

  public String getDescription() {
    return description;
  }

public void setId(String id) {
	this.id = id;
}

public void setName(String name) {
	this.name = name;
}

public void setChildren(Set<Event> children) {
	this.children = children;
}

public void setDescription(String description) {
	this.description = description;
}
  
  
  
  
}
