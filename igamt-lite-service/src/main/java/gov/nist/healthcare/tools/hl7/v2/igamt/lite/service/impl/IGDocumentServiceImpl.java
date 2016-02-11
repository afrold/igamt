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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.MongoException;

@Service
public class IGDocumentServiceImpl implements IGDocumentService {
	Logger log = LoggerFactory.getLogger(IGDocumentServiceImpl.class);
	@Autowired
	private IGDocumentRepository documentRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public IGDocument save(IGDocument ig) throws IGDocumentException {
		try {
			return documentRepository.save(ig);
		} catch (MongoException e) {
			throw new IGDocumentException(e);
		}
	}
	
	@Override
	@Transactional
	public void delete(String id) {
		documentRepository.delete(id);
	}

	@Override
	public IGDocument findOne(String id) {
		IGDocument ig = documentRepository.findOne(id);
		return ig;
	}
	

	@Override
	public List<IGDocument> findAll() {
		List<IGDocument> igDocuments = documentRepository.findAll();
		log.info("igDocuments=" + igDocuments.size());
		return igDocuments;
	}

	@Override
	public List<IGDocument> findAllPreloaded() {
		List<IGDocument> igDocuments = documentRepository.findPreloaded();
		log.info("igDocuments=" + igDocuments.size());
		return igDocuments;
	}
	

	@Override
	public List<IGDocument> findByAccountId(Long accountId) {
		List<IGDocument> igDocuments = documentRepository.findByAccountId(accountId);
		// if (profiles != null && !profiles.isEmpty()) {
		// for (Profile profile : profiles) {
		// processChildren(profile);
		// }
		// }
		log.debug("User IG Document found=" + igDocuments.size());
		return igDocuments;
	}

	@Override
	public IGDocument clone(IGDocument ig) throws CloneNotSupportedException {
		return new IGDocumentClone().clone(ig);
	}

	@Override
	public IGDocument apply(IGDocument ig) throws IGDocumentSaveException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		ig.getMetaData().setDate(dateFormat.format(Calendar.getInstance().getTime()));
		documentRepository.save(ig);
		return ig;
	}

	@Override
	public InputStream diffToPdf(IGDocument d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ElementVerification verifySegment(IGDocument d, String id,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ElementVerification verifyDatatype(IGDocument d, String id,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ElementVerification verifyValueSet(IGDocument p, String id,
			String type) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
