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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;

/**
 * @author gcr1
 *
 */
@Service
public class SegmentServiceImpl implements SegmentService {
	
	Logger log = LoggerFactory.getLogger(SegmentServiceImpl.class);

	@Autowired
	private SegmentRepository segmentRepository;
 	
	public List<Segment> findAll() {
		return segmentRepository.findAll();
	}

	@Override
	public List<Segment> findByLibIds(String segLibId) {
		List<Segment> datatypes = segmentRepository.findByLibIds(segLibId);
		log.info("DataypeServiceImpl.findAll=" + datatypes.size());
		return datatypes;
	}
	
	@Override 
	public Segment findById(String id) {
		log.info("SegmentServiceImpl.findById=" + id);
		return segmentRepository.findOne(id);
	}
	
	@Override
	public List<Segment> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
		List<Segment> segments = segmentRepository.findByScopesAndVersion(scopes, hl7Version);
		log.info("SegmentServiceImpl.findByScopeAndVersion=" + segments.size());
		return segments;
	}
	
	@Override
	public Segment save(Segment segment) {
		log.info("SegmentServiceImpl.save=" + segment.getLabel());
		return segmentRepository.save(segment);
	}

	@Override
	public void delete(Segment segment) {
		segmentRepository.delete(segment);
	}
	
	@Override
	public void delete(String id) {
		segmentRepository.delete(id);
	}
	
	@Override
	public void save(List<Segment> segments) {
		// TODO Auto-generated method stub
		segmentRepository.save(segments);
	}
	
	@Override
	public List<Segment> findByIds(Set<String> ids) {
		log.info("DataypeServiceImpl.findByIds=" + ids);
		return segmentRepository.findByIds(ids);
	}
}
