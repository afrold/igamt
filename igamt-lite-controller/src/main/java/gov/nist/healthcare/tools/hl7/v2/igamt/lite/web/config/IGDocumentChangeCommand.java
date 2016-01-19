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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 6, 2015
 */
public class IGDocumentChangeCommand {
	private String changes;

	private IGDocument igDocument;

	public String getChanges() {
		return changes;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}

	public IGDocument getIgDocument() {
		return igDocument;
	}

	public void setIgDocument(IGDocument igDocument) {
		this.igDocument = igDocument;
	}

	

}
