package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;

import java.io.Serializable;
import java.util.List;

public class IntegrationIGDocumentRequestWrapper implements Serializable {
 
		public IntegrationIGDocumentRequestWrapper() {
			super();
		}

		private static final long serialVersionUID = -8337269625916897011L;
		
		String hl7Version;
		List<String> msgIds;
		IGDocument igdocument;
		Long accountId;

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

		public IGDocument getIgdocument() {
			return igdocument;
		}

		public void setIgdocument(IGDocument igdocument) {
			this.igdocument = igdocument;
		}

		public Long getAccountId() {
			return accountId;
		}

		public void setAccountId(Long accountId) {
			this.accountId = accountId;
		}
	}