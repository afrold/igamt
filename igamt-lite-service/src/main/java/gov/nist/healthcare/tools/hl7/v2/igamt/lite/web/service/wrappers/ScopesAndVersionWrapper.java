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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers;

import java.util.List;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;

/**
 * @author gcr1
 *
 */
public class ScopesAndVersionWrapper {

	private static final long serialVersionUID = -8337269625916897011L;

	public ScopesAndVersionWrapper() {
		super();
	}

	private List<SCOPE> scopes;

	private String hl7Version;

	public List<SCOPE> getScopes() {
		return scopes;
	}

	public void setScopes(List<SCOPE> scopes) {
		this.scopes = scopes;
	}

	public String getHl7Version() {
		return hl7Version;
	}

	public void setHl7Version(String hl7Version) {
		this.hl7Version = hl7Version;
	}

	@Override
	public String toString() {
		return scopes.toString() + " " + hl7Version;
	}
}
