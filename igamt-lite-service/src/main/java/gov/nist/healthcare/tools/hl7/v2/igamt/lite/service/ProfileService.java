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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;

import java.io.InputStream;
import java.util.List;

public interface ProfileService {

	public Profile save(Profile p) throws ProfileException;

	public void delete(String id);

	public Profile findOne(String id);

	public List<Profile> findAllPreloaded();

	public List<Profile> findByAccountId(Long accountId);

	public Profile clone(Profile p) throws CloneNotSupportedException;

	public Profile apply(Profile newProfile, Profile oldProfile,
			String newValues) throws ProfileSaveException;

	public InputStream exportAsPdfFromXsl(Profile p, String inlineConstraints);

	public InputStream exportAsPdf(Profile p);

	public InputStream exportAsXml(Profile p);

	public InputStream exportAsXlsx(Profile p);

	public InputStream diffToPdf(Profile p);

	public InputStream diffToJson(Profile p);
}