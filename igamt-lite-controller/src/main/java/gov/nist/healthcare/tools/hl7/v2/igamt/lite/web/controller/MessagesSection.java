package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MessageConfiguration;

public class MessagesSection {

	private MessageConfiguration config;
	private String sectionContents;
	public MessageConfiguration getConfig() {
		return config;
	}
	public void setConfig(MessageConfiguration config) {
		this.config = config;
	}
	public String getSectionContents() {
		return sectionContents;
	}
	public void setSectionContents(String sectionContents) {
		this.sectionContents = sectionContents;
	}
}
