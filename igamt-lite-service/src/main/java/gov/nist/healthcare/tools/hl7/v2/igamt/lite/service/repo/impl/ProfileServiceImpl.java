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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileSummary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Override
	@Transactional
	public Profile save(Profile p) {
		return profileRepository.save(p);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		profileRepository.delete(id);
	}

	@Override
	public Profile findOne(Long id) {
		return profileRepository.findOne(id);
	}

	@Override
	public Iterable<ProfileSummary> findAllPreloadedSummaries() {
		return profileRepository.findAllPreloadedSummaries();
	}

	@Override
	public Iterable<ProfileSummary> findAllSummariesByUser(Long userId) {
		return profileRepository.findAllSummariesByUserId(userId);
	}

}