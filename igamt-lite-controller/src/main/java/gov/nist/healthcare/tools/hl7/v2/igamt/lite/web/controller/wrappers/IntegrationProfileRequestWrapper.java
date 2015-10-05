package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers;

import java.io.Serializable;
import java.util.List;

public class IntegrationProfileRequestWrapper implements Serializable {
 
		public IntegrationProfileRequestWrapper() {
			super();
		}

		private static final long serialVersionUID = -8337269625916897011L;
		
		String hl7Version;
		List<String> msgIds;

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
	}