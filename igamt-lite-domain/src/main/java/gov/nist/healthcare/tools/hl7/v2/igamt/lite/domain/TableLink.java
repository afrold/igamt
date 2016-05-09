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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

/**
 * @author gcr1
 *
 */
public class TableLink extends AbstractLink implements Cloneable{

	private String bindingIdentifier;

	public TableLink() {
		super();
	}

	public TableLink(String id, String bindingIdentifier) {
		super();
		this.setId(id);
		this.bindingIdentifier = bindingIdentifier;
	}

	public String getBindingIdentifier() {
		return bindingIdentifier;
	}

	public void setBindingIdentifier(String bindingIdentifier) {
		this.bindingIdentifier = bindingIdentifier;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		TableLink link = null;

		if (obj == null) {
			return false;
		}

		if (obj instanceof SegmentLink) {
			link = (TableLink) obj;
		} else {
			return false;
		}

		return getId().equals(link.getId());
	}
	
	public TableLink clone(){
		TableLink clonedLink = new TableLink();
		clonedLink.setBindingIdentifier(this.getBindingIdentifier());
		clonedLink.setId(this.getId());
		return clonedLink;
	}
}
