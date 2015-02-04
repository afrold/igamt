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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.MetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.MetaDataConstraintRepository;


@Repository
public class MetaDataConstraintService {
	@Autowired
	private MetaDataConstraintRepository metaDataRepository;

	public Iterable<MetaData> findAll() {
		return metaDataRepository.findAll();
	}

	public MetaData save(MetaData c) {
		return metaDataRepository.save(c);
	}

	public void delete(String id) {
		metaDataRepository.delete(id);
	}

	public MetaData findOne(String id) {
		return metaDataRepository.findOne(id);
	}

}