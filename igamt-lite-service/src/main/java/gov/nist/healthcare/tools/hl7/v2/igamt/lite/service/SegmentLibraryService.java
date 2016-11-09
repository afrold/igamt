/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;

@Service
public interface SegmentLibraryService {

	List<SegmentLibrary> findAll();

	List<SegmentLibrary> findByScopes(List<SCOPE> scopes);

	SegmentLibrary findById(String id);

	List<SegmentLibrary> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version);

	List<SegmentLibrary> findByAccountId(Long accountId, String hl7Version);

	SegmentLibrary save(SegmentLibrary library);

	SegmentLibrary create(String name, String ext, SCOPE scope, String hl7Version, Long accountId);

	void delete(SegmentLibrary library);

	List<String> findHl7Versions();

	public List<SegmentLibrary> findLibrariesByFlavorName(SCOPE scope, String hl7Version, String name, Long accountId);

	public List<SegmentLink> findFlavors(SCOPE scope, String hl7Version, String name, Long accountId);

	List<Segment> findSegmentsById(String libId);

	SegmentLibrary save(SegmentLibrary library, Date date);

}
