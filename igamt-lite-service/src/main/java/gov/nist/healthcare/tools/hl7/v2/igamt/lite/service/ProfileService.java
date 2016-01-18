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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementChange;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ProfileService {
	
	public Profile save(Profile p) throws ProfileException;

	public void delete(String id);

	public Profile findOne(String id);

	public List<Profile> findAllPreloaded();
	
	public List<Profile> findAllProfiles();

	public List<Profile> findByAccountId(Long accountId);

	public Profile clone(Profile p) throws CloneNotSupportedException;

	public Profile apply(Profile p) throws ProfileSaveException;

	public InputStream diffToPdf(Profile p);

	public InputStream diffToJson(Profile p);

	public Map<String, List<ElementChange>> delta(Profile p);

	public ElementVerification verifyMessages(Profile p, String id, String type);

	public ElementVerification verifyMessage(Profile p, String id, String type);

	public ElementVerification verifySegmentRefOrGroup(Profile p, String id, String type);

	public ElementVerification verifySegments(Profile p, String id, String type);

	public ElementVerification verifySegment(Profile p, String id, String type);

	public ElementVerification verifyField(Profile p, String id, String type);

	public ElementVerification verifyDatatypes(Profile p, String id, String type);

	public ElementVerification verifyDatatype(Profile p, String id, String type);

	public ElementVerification verifyComponent(Profile p, String id, String type);

	public ElementVerification verifyValueSetLibrary(Profile p, String id, String type);

	public ElementVerification verifyValueSet(Profile p, String id, String type);

	public ElementVerification verifyUsage(Profile p, String id, String type, String eltName, String eltValue);

	public ElementVerification verifyCardinality(Profile p, String id, String type, String eltName, String eltValue);
	
	public ElementVerification verifyLength(Profile p, String id, String type, String eltName, String eltValue);
	
}
