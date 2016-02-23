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

	private String messageStructureId;
	
	private final String type = "structure";
	
	private Set<Event> events = new HashSet<Event>();
	
	private String description;
	
	public MessageEvents(String messageStructureId, Set<String> events, String description) {
		this.messageStructureId = messageStructureId;
		createEvents(events);
		this.description = description;
	}

	void createEvents(Set<String> events) {
		for (String event : events) {
			this.events.add(new Event(event));
		}
	}
	
	public String getMessageStructureId() {
		return messageStructureId;
	}

	public String getType() {
		return type;
	}

	public Set<Event> getEvents() {
		return events;
	}

	public String getDescription() {
		return description;
	}
	
	
	
	class Event {
		
		final String type = "event";
		
		String event;

		public Event(String event) {
			super();
			this.event = event;
		}

		public String getType() {
			return type;
		}

		public String getEvent() {
			return event;
		}
	}
}
