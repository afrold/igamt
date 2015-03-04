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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentRefRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentRefService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegmentRefServiceImpl implements SegmentRefService {
	@Autowired
	private SegmentRefRepository segmentRefRepository;

	@Override
	public Iterable<SegmentRef> findAll() {
		return segmentRefRepository.findAll();
	}

	@Override
	public SegmentRef save(SegmentRef c) {
		return segmentRefRepository.save(c);
	}

	@Override
	public void delete(Long id) {
		segmentRefRepository.delete(id);
	}

	@Override
	public SegmentRef findOne(Long id) {
		return segmentRefRepository.findOne(id);
	}

}