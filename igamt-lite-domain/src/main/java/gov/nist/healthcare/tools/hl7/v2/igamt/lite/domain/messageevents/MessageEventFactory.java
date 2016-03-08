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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;

/**
 * @author gcr1
 *
 */
public class MessageEventFactory {
	
	private static Logger log = LoggerFactory.getLogger(MessageEventFactory.class);

	private Tables tables;
	
	private Table tab0354;
	
	public MessageEventFactory(IGDocument igdocument) {
		tables = igdocument.getProfile().getTables();
	}
	
	public List<MessageEvents> createMessageEvents(Messages msgs) {
		
		List<MessageEvents> list = new ArrayList<MessageEvents>();
		for(Message msg : msgs.getChildren()) {
			String id = msg.getId();
			String structID = msg.getStructID();
			Set<String> events = findEvents(structID);
			String description = msg.getDescription();
			list.add(new MessageEvents(id, structID, events, description));
		}
		return list;
	}

	Set<String> findEvents(String structID) {
		Set<String> events = new HashSet<String>();
		String structID1 = fixUnderscore(structID);
		Code code = get0354Table().findOneCodeByValue(structID1);
		if (code != null) {
			String label = code.getLabel();
			String[] ss = label.split(",");
			if (ss[0].equalsIgnoreCase("Varies")) {
				ss[0] = structID1;
			}
			Collections.addAll(events, ss);
		} else {
			log.error("No code found for structID=" + structID1);
		}
		return events;
	}

	String fixUnderscore (String structID) {
		if (structID.endsWith("_")) {
			int pos = structID.length();
			return structID.substring(0, pos -1);
		} else {
			return structID;
		}
	}
	
	Table get0354Table() {
		if (tab0354 == null) {
			for (Table tab : tables.getChildren()) {
				if ("0354".equals(tab.getBindingIdentifier())) {
					tab0354 = tab;
					break;
				}
			}
		}
		return tab0354;
	}
}
