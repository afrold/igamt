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

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ForbiddenOperationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.exception.SegmentSaveException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
  private SegmentLibraryService segmentLibraryService;

  @Autowired
  UserService userService;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  private DatatypeService datatypeService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  public Segment getSegmentById(@PathVariable("id") String id) {
    log.info("Fetching segmentById..." + id);
    Segment result = segmentService.findById(id);
    return result;
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public Segment save(@RequestBody Segment segment) throws SegmentSaveException,
      ForbiddenOperationException {
    log.debug("segment=" + segment);
    log.debug("segment.getId()=" + segment.getId());
    log.info("Saving the " + segment.getScope() + " segment.");
    segment.setDate(DateUtils.getCurrentTime());
    Segment saved = segmentService.save(segment);
    log.debug("saved.getId()=" + saved.getId());
    log.debug("saved.getScope()=" + saved.getScope());
    return segment;

  }

  @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
  public boolean delete(@PathVariable("id") String segId) {
    log.info("Deleting segment " + segId);
    segmentService.delete(segId);
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
  public Set<Datatype> collectDatatypes(@PathVariable("id") String id) {
    Segment segment = segmentService.findById(id);
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
}
