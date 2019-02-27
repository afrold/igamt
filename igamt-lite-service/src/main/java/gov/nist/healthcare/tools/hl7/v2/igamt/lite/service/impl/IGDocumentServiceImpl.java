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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.MongoException;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.IGDocumentRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileComponentLibraryRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileStructureService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

@Service
public class IGDocumentServiceImpl implements IGDocumentService {

  Logger log = LoggerFactory.getLogger(IGDocumentServiceImpl.class);

  @Autowired
  private IGDocumentRepository documentRepository;
  
  @Autowired
  private MessageService messageService;
  
  @Autowired
  private ProfileComponentLibraryService profileComponentLibraryService;
  
  @Autowired
  private ProfileComponentService profileComponentService;
  @Autowired
  private ProfileComponentLibraryRepository profileComponentLibraryRepository;
  @Autowired
  private CompositeProfileStructureService compositeProfileStructureService;
  @Autowired
  private DatatypeLibraryService datatypeLibraryService;

  @Autowired
  private SegmentLibraryService segmentLibraryService;

  @Autowired
  private TableLibraryService tableLibraryService;

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;
  
  
  
  
  
  @Override
  public IGDocument save(IGDocument ig) throws IGDocumentException {
    try {
      return save(ig, DateUtils.getCurrentDate());
    } catch (MongoException e) {
      throw new IGDocumentException(e);
    }
  }

  @Override
  public IGDocument save(IGDocument ig, Date date) throws IGDocumentException {
    try {
      ig.setDateUpdated(date);
      return documentRepository.save(ig);
    } catch (MongoException e) {
      throw new IGDocumentException(e);
    }
  }

  @Override
  public List<IGDocument> save(Collection<IGDocument> igs) throws IGDocumentException {
    try {
      return documentRepository.save(igs);
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
    log.info("IGDocumentServiceImpl.findOne=" + id);
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
  public List<IGDocument> findAllUser() {
    List<IGDocument> igDocuments = documentRepository.findUser();
    log.info("igDocuments=" + igDocuments.size());
    return igDocuments;
  }

  @Override
  @Deprecated
  /** Use findByAccountIdAndAScope **/
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
  public List<IGDocument> findByScopesAndVersion(List<SCOPE> scopes, String hl7Version) {
    List<IGDocument> igDocuments = documentRepository.findByScopesAndVersion(scopes, hl7Version);
    log.info("IGDocumentServiceImpl.findByScopeAndVersion=" + igDocuments.size());
    return igDocuments;
  }

  @Override
  public List<IGDocument> findByScopeAndVersions(IGDocumentScope scope, List<String> hl7Versions) {
    List<IGDocument> igDocuments = documentRepository.findByScopeAndVersions(scope, hl7Versions);
    log.info("IGDocumentServiceImpl.findByScopeAndVersions=" + igDocuments.size());
    return igDocuments;
  }

  @Override

  public List<IGDocument> findByScopeAndVersion(IGDocumentScope scope, String hl7Version) {
    List<IGDocument> igDocuments = documentRepository.findByScopeAndVersion(scope, hl7Version);
    log.info("IGDocumentServiceImpl.findByScopeAndVersions=" + igDocuments.size());
    return igDocuments;
  }

  @Override
  public List<IGDocument> findByScopeAndVersionsInIg(IGDocumentScope scope,
      List<String> hl7Versions) {
    List<IGDocument> igDocuments =
        documentRepository.findByScopeAndVersionsInIg(scope, hl7Versions);
    log.info("IGDocumentServiceImpl.findByScopeAndVersionsInIg=" + igDocuments.size());
    return igDocuments;
  }

  @Override
  public List<IGDocument> findByAccountIdAndScopesAndVersion(Long accountId, List<SCOPE> scopes,
      String hl7Version) {
    List<IGDocument> igDocuments =
        documentRepository.findByAccountIdAndScopesAndVersion(accountId, scopes, hl7Version);
    log.info("IGDocumentServiceImpl.findByScopeAndVersion=" + igDocuments.size());
    return igDocuments;
  }

  @Override
  public IGDocument findById(String id) {
    log.info("IGDocumentServiceImpl.findById=" + id);
    IGDocument igDocument;
    igDocument = documentRepository.findOne(id);
    return igDocument;
  }

  @Override
  public IGDocument clone(IGDocument ig) throws CloneNotSupportedException {
    return new IGDocumentClone().clone(ig);
  }

  @Override
  public IGDocument apply(IGDocument ig) throws IGDocumentException {
    save(ig);
    return ig;
  }

  @Override
  public InputStream diffToPdf(IGDocument d) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ElementVerification verifySegment(IGDocument d, String id, String type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ElementVerification verifyDatatype(IGDocument d, String id, String type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ElementVerification verifyValueSet(IGDocument p, String id, String type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IGDocument> findAllByScope(IGDocumentScope scope) {
    List<IGDocument> igDocuments = documentRepository.findAllByScope(scope);
    log.info("igDocuments=" + igDocuments.size());
    return igDocuments;
  }

  @Override
  public List<IGDocument> findSharedIgDocuments(Long participantId) {
    List<IGDocument> igDocuments = documentRepository.findByParticipantId(participantId);
    log.info("igDocuments=" + igDocuments.size());

    return igDocuments;
  }

  @Override
  public Date updateDate(String id, Date date) throws IGDocumentException {
    return documentRepository.updateDate(id, date);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService#updatePosition(java.lang.
   * String, int)
   */
  @Override
  public int updatePosition(String id, int position) {
    // TODO Auto-generated method stub
    return documentRepository.updatePosition(id, position);
  }

  @Override
  public List<IGDocument> findByAccountIdAndScope(Long accountId, IGDocumentScope scope) {
    // TODO Auto-generated method stub
    return documentRepository.findByAccountIdAndScope(accountId, scope);
  }

@Override
public void makePreloaded(String id) {
	// TODO Auto-generated method stub
	IGDocument ig =documentRepository.findOne(id);	
	if(ig !=null){
	ig.setScope(IGDocumentScope.PRELOADED);
	Profile p= ig.getProfile();
	
	for(Message child:p.getMessages().getChildren()){
		if(child.getScope().equals(SCOPE.USER)){
			child.setScope(SCOPE.PRELOADED);
			child.setStatus(STATUS.PUBLISHED);
			messageService.save(child);	
		}
	}
	
	for (CompositeProfileStructure child : p.getCompositeProfiles().getChildren()){
		if(child.getScope().equals(SCOPE.USER)){
			child.setScope(SCOPE.PRELOADED);
			child.setStatus(STATUS.PUBLISHED);
			compositeProfileStructureService.save(child);
		}
	}
	ProfileComponentLibrary lib=profileComponentLibraryService.findProfileComponentLibById(p.getProfileComponentLibrary().getId());
	for(ProfileComponentLink l:lib.getChildren()){
		ProfileComponent child =	profileComponentService.findById(l.getId());

		if(child != null && child.getScope().equals(SCOPE.USER)){
			child.setScope(SCOPE.PRELOADED);
			child.setStatus(STATUS.PUBLISHED);
			profileComponentService.save(child);	
		}
	
		
	}

	SegmentLibrary seglib= segmentLibraryService.findById(p.getSegmentLibrary().getId());
	for(SegmentLink l:seglib.getChildren()){
		
		Segment child =	segmentService.findById(l.getId());
		if(child != null && child.getScope().equals(SCOPE.USER)){

		child.setScope(SCOPE.PRELOADED);
		child.setStatus(STATUS.PUBLISHED);
		segmentService.save(child);
		}
		
	}
	
	DatatypeLibrary dtLib= datatypeLibraryService.findById(p.getDatatypeLibrary().getId());
	for(DatatypeLink l:dtLib.getChildren()){
		
		Datatype child =datatypeService.findById(l.getId());
		if(child != null && child.getScope().equals(SCOPE.USER)){

		child.setScope(SCOPE.PRELOADED);
		child.setStatus(STATUS.PUBLISHED);
		datatypeService.save(child);
		}
		
	}
	
	TableLibrary tblLib= tableLibraryService.findById(p.getTableLibrary().getId());
	for(TableLink l:tblLib.getChildren()){
		
		Table child =tableService.findById(l.getId());
		if(child != null && child.getScope().equals(SCOPE.USER)){
		child.setScope(SCOPE.PRELOADED);
		child.setStatus(STATUS.PUBLISHED);
		tableService.save(child);
		}
	}
	
	 try {
		save(ig, DateUtils.getCurrentDate());
	} catch (IGDocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	}
}


}
