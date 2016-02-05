package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import java.io.Serializable;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;

public class IntegrationProfileRequestWrapper implements Serializable {
 
		public IntegrationProfileRequestWrapper() {
			super();
		}

		private static final long serialVersionUID = -8337269625916897011L;
		
		String hl7Version;
		List<String> msgIds;
		IGDocument igdocument;
		Long accountId;

		public Long getAccountId() {
			return accountId;
		}

		public void setAccountId(Long accountId) {
			this.accountId = accountId;
		}

		public String getHl7Version() {
			return hl7Version;
		}

		public void setHl7Version(String hl7Version) {
			this.hl7Version = hl7Version;
		}

		public List<String> getMsgIds() {
			return msgIds;
		}

		public void setMsgIds(List<String> msgIds) {
			this.msgIds = msgIds;
		}

		public IGDocument getIGDocument() {
			return igdocument;
		}

		public void setIGDocument(IGDocument igdocument) {
			this.igdocument = igdocument;
		}	
	}