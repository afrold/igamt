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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.BindingParametersForSegment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.DataNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.NotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.SegmentSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.UserAccountNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ScopesAndVersionWrapper;

/**
 * @author Harold Affo (harold.affo@nist.gov) Mar 17, 2015
 */

@RestController
@RequestMapping("/segments")
public class SegmentController extends CommonController {

  Logger log = LoggerFactory.getLogger(SegmentController.class);

  @Autowired
  private SegmentService segmentService;
  
  @Autowired
  private TableService tableService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  private DatatypeService datatypeService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public Segment getSegmentById(@PathVariable("id") String id) throws DataNotFoundException {
    log.info("Fetching segmentById..." + id);
    return findById(id);
  }

  @RequestMapping(value = "/findByScopesAndVersion", method = RequestMethod.POST,
      produces = "application/json")
  public List<Segment> findByScopesAndVersion(@RequestBody ScopesAndVersionWrapper scopesAndVersion) {
    log.info("Fetching the segment. scope=" + scopesAndVersion.getScopes() + " hl7Version="
        + scopesAndVersion.getHl7Version());
    List<Segment> semgents = new ArrayList<Segment>();
    try {
      User u = userService.getCurrentUser();
      Account account = accountRepository.findByTheAccountsUsername(u.getUsername());
      if (account == null) {
        throw new UserAccountNotFoundException();
      }

      semgents.addAll(segmentService.findByScopesAndVersion(scopesAndVersion.getScopes(),
          scopesAndVersion.getHl7Version()));
      if (semgents.isEmpty()) {
        throw new NotFoundException("Segment not found for scopesAndVersion=" + scopesAndVersion);
      }
    } catch (Exception e) {
      log.error("", e);
    }
    return semgents;
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Segment save(@RequestBody Segment segment) throws SegmentSaveException,
      ForbiddenOperationException {
    if (!SCOPE.HL7STANDARD.equals(segment.getScope())) {
      log.debug("segment=" + segment);
      log.debug("segment.getId()=" + segment.getId());
      log.info("Saving the " + segment.getScope() + " segment.");
       Segment saved = segmentService.save(segment);
      log.debug("saved.getId()=" + saved.getId());
      log.debug("saved.getScope()=" + saved.getScope());
      return segment;
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_SAVE_SEGMENT");
    }

  }
  
//  @RequestMapping(value = "/updateTableBinding", method = RequestMethod.POST)
//  public void updateTableBinding(@RequestBody List<BindingParametersForSegment> bindingParametersList) throws SegmentSaveException, ForbiddenOperationException, DataNotFoundException {
//	  for(BindingParametersForSegment paras : bindingParametersList){
//		  Segment segment = this.segmentService.findById(paras.getSegmentId());
//		  if (!SCOPE.HL7STANDARD.equals(segment.getScope())) {
// 			  Field targetField = segment.getFields().get(this.indexOfField(paras.getFieldId(), segment));
//			  TableLink tableLink = paras.getTableLink();
//			  if(tableLink != null && tableLink.getBindingIdentifier() != null && !tableLink.getBindingIdentifier().equals("")) {
//				  tableLink.setBindingIdentifier(tableService.findById(tableLink.getId()).getBindingIdentifier());
//				  targetField.getTables().add(paras.getTableLink());
//			  }
//			  if(paras.getKey() != null){
//				  this.deleteTable(targetField, paras.getKey());  
//			  }
//			  segmentService.save(segment);
//		  } else {
//			  throw new ForbiddenOperationException("FORBIDDEN_SAVE_SEGMENT");  
//		  }
//	  }
//  }
  @RequestMapping(value = "/updateDatatypeBinding", method = RequestMethod.POST)
  public void updateDatatypeBinding(@RequestBody List<BindingParametersForSegment> bindingParametersList) throws SegmentSaveException, ForbiddenOperationException, DataNotFoundException {
	  for(BindingParametersForSegment paras : bindingParametersList){
		  Segment segment = this.segmentService.findById(paras.getSegmentId());
		  if (!SCOPE.HL7STANDARD.equals(segment.getScope())) {
 			  Field targetField = segment.getFields().get(this.indexOfField(paras.getFieldId(), segment));
			  DatatypeLink datatypeLink=paras.getDatatypeLink();
			  if(datatypeLink != null) {
				  targetField.setDatatype(datatypeLink);
			  }
			  segmentService.save(segment);
		  } else {
			  throw new ForbiddenOperationException("FORBIDDEN_SAVE_SEGMENT");  
		  }
	  }
  }
  private void deleteDatatype(Field targetField, String key) throws DataNotFoundException {
	  DatatypeLink found = null;
	  if(targetField.getDatatype().getId().equals(key)) found = targetField.getDatatype();
	  
	  if(found != null){
		  targetField.setDatatype(null);;
	  }else {
		  throw new DataNotFoundException("datatypeLinkNotFound");
	  }
}
  

@RequestMapping(value = "/saveSegs", method = RequestMethod.POST)
  public List<Segment> save(@RequestBody List<Segment> segments) throws SegmentSaveException,
      ForbiddenOperationException {
	  List<Segment> segs=new ArrayList<Segment>();
	  for(Segment seg:segments){
		  if (!SCOPE.HL7STANDARD.equals(seg.getScope())) {
		      log.debug("segment=" + seg);
		      log.debug("segment.getId()=" + seg.getId());
		      log.info("Saving the " + seg.getScope() + " segment.");
 		      Segment saved = segmentService.save(seg);
		      log.debug("saved.getId()=" + saved.getId());
		      log.debug("saved.getScope()=" + saved.getScope());
		      segs.add(seg);
		    } else {
		      throw new ForbiddenOperationException("FORBIDDEN_SAVE_SEGMENT");
		    }
	  }
	  return segs;
    

  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
  public boolean delete(@PathVariable("id") String segId) throws ForbiddenOperationException,
      DataNotFoundException {
    Segment segment = findById(segId);
    if (!SCOPE.HL7STANDARD.equals(segment.getScope())) {
      log.info("Deleting segment " + segId);
      segmentService.delete(segId);
    } else {
      throw new ForbiddenOperationException("FORBIDDEN_DELETE_SEGMENT");
    }
    return true;
  }

  @RequestMapping(value = "/findByIds", method = RequestMethod.POST, produces = "application/json")
  public List<Segment> findByIds(@RequestBody Set<String> ids) {
    log.info("Fetching datatypeByIds..." + ids);
    List<Segment> result = segmentService.findByIds(ids);
    return result;
  }

  @RequestMapping(value = "/{id}/datatypes", method = RequestMethod.GET,
      produces = "application/json")
  public Set<Datatype> collectDatatypes(@PathVariable("id") String id) throws DataNotFoundException {
    Segment segment = findById(id);
    Set<Datatype> datatypes = new HashSet<Datatype>();
    if (segment != null) {
      List<Field> fields = segment.getFields();
      for (Field f : fields) {
        Datatype dt = datatypeService.findById(f.getDatatype().getId());
        datatypes.addAll(datatypeService.collectDatatypes(dt));
      }
    }
    return datatypes;
  }

  public Segment findById(String id) throws DataNotFoundException {
    Segment result = segmentService.findById(id);
    if (result == null)
      throw new DataNotFoundException("segmentNotFound");
    return result;
  }
  
  private int indexOfField(String id, Segment s) throws DataNotFoundException {
	  int index = 0;
	  for(Field f:s.getFields()){
		  if(id.equals(f.getId())) return index;
		  index = index + 1;
	  }
	  throw new DataNotFoundException("fieldNotFound");
  }
  
}
