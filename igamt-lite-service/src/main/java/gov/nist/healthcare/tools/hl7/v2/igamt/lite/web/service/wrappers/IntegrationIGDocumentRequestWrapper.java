package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers;

import java.io.Serializable;
import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.messageevents.MessageEvents;

public class IntegrationIGDocumentRequestWrapper implements Serializable {

	public IntegrationIGDocumentRequestWrapper() {
		super();
	}

	private static final long serialVersionUID = -8337269625916897011L;

	String hl7Version;
	List<MessageEvents> msgEvts;
	DocumentMetaData metaData;
	IGDocument igdocument;
	
	Long accountId;

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	public List<MessageEvents> getMsgEvts() {
		return msgEvts;
	}

	public void setMsgEvts(List<MessageEvents> msgEvts) {
		this.msgEvts = msgEvts;
	}

	public DocumentMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(DocumentMetaData metaData) {
		this.metaData = metaData;
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