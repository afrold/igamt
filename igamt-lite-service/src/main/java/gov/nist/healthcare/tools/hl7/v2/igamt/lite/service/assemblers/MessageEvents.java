/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.assemblers;

import java.util.HashSet;
import java.util.Set;

/**
 * A data transfer object used to transfer a message structure id 
 * and its related events.   
 * 
 * @author gcr1
 *
 */
public class MessageEvents {

	private final String id;

	private final String name;
	
	private final String type = "message";
	
	private Set<Event> children = new HashSet<Event>();
	
	private final String description;
	
	public MessageEvents(String id, String name, Set<String> events, String description) {
		this.id = id;
		this.name = name;
		createEvents(events);
		this.description = description;
	}

	void createEvents(Set<String> events) {
		for (String event : events) {
			this.children.add(new Event(id, event));
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

	public class Event {
		
		final String id;
		final String name;
		final String type = "event";

		public Event(String id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public String getName() {
			return name;
		}
	}
}
