/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Aug 28, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.DatatypeCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.SegmentCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.ValueSetCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.CoConstraintFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypePredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessagePredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentPredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ReplaceService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@Service
public class ReplaceServiceImpl implements ReplaceService {
  private DatatypeService datatypeService;
  @Autowired
  private ProfileComponentService profileComponentService;
  @Autowired
  private SegmentService segmentService;
  @Autowired
  private TableService tableService;
  @Autowired
  private MessageService messageService;
  HashMap<String, Message> visitedMessages = new HashMap<String, Message>();
  HashMap<String, Segment> visitedSegments = new HashMap<String, Segment>();
  HashMap<String, Datatype> visitedDatatypes = new HashMap<String, Datatype>();

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ReplaceService#replace(gov.nist.healthcare.
   * tools.hl7.v2.igamt.lite.domain.crossreference.ValueSetCrossReference,
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table,
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table)
   */
  @Override
  public void replace(ValueSetCrossReference refs, Table source, Table dest) {

    // TODO Auto-generated method stub
    List<MessageValueSetBindingFound> messageValueSetBindingfounds =
        refs.getMessageValueSetBindingfounds();
    if (!messageValueSetBindingfounds.isEmpty()) {
      for (MessageValueSetBindingFound msgF : messageValueSetBindingfounds) {
        if (!visitedMessages.containsKey(msgF.getMessageFound().getId())) {
          Message toModify = getMessage(msgF.getMessageFound().getId());
          for (ValueSetOrSingleCodeBinding vs : toModify.getValueSetBindings()) {
            if (vs.getTableId() != null && vs.getTableId().equals(source.getId())) {
              vs.setTableId(dest.getId());

            }
          }
          visitedMessages.put(msgF.getMessageFound().getId(), toModify);
        }
      }
    }
    List<SegmentValueSetBindingFound> segmentValueSetBindingfounds =
        refs.getSegmentValueSetBindingfounds();
    if (!segmentValueSetBindingfounds.isEmpty()) {

      for (SegmentValueSetBindingFound segF : segmentValueSetBindingfounds) {
        if (!visitedSegments.containsKey(segF.getSegmentFound().getId())) {
          Segment toModify = getSegment(segF.getSegmentFound().getId());
          for (ValueSetOrSingleCodeBinding vs : toModify.getValueSetBindings()) {
            if (vs.getTableId() != null && vs.getTableId().equals(source.getId())) {
              vs.setTableId(dest.getId());

            }
          }
          visitedSegments.put(segF.getSegmentFound().getId(), toModify);
        }
      }
    }
    List<DatatypeValueSetBindingFound> datatypeValueSetBindingfounds =
        refs.getDatatypeValueSetBindingfounds();
    if (!datatypeValueSetBindingfounds.isEmpty()) {
      for (DatatypeValueSetBindingFound dtF : datatypeValueSetBindingfounds) {
        if (!visitedDatatypes.containsKey(dtF.getDatatypeFound().getId())) {
          Datatype toModify = getDatatype(dtF.getDatatypeFound().getId());
          for (ValueSetOrSingleCodeBinding vs : toModify.getValueSetBindings()) {
            if (vs.getTableId() != null && vs.getTableId().equals(source.getId())) {
              vs.setTableId(dest.getId());
            }
          }
          visitedDatatypes.put(dtF.getDatatypeFound().getId(), toModify);
        }
      }
    }

    List<CoConstraintFound> coConstraintFounds = refs.getCoConstraintFounds();
    for (CoConstraintFound cocnst : coConstraintFounds) {
      Segment s = null;
      String segmentId = cocnst.getSegmentFound().getId();
      if (visitedSegments.containsKey(segmentId)) {
        s = visitedSegments.get(segmentId);
      } else {
        s = segmentService.findById(segmentId);

      }
      if (s.getCoConstraintsTable() != null) {
        CoConstraintsTable coconstraints = s.getCoConstraintsTable();
        if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
            && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
          CoConstraintFound found = new CoConstraintFound();
          for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
            if (coconstraints.getThenMapData().get(thn.getId()) != null
                && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
              for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {

                if (coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets() != null
                    && !coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets()
                        .isEmpty()) {
                  for (int j = 0; j < coconstraints.getThenMapData().get(thn.getId()).get(i)
                      .getValueSets().size(); j++) {
                    String tableId = coconstraints.getThenMapData().get(thn.getId()).get(i)
                        .getValueSets().get(j).getTableId();
                    if (tableId != null && tableId.equals(source.getId())) {
                      coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets().get(j)
                          .setTableId(dest.getId());
                    }
                  }
                }
              }
            }
          }
        }
        if (!visitedSegments.containsKey(segmentId)) {
          visitedSegments.put(segmentId, s);
        }
      }
    }
    List<MessageConformanceStatmentFound> messageConformanceStatmentFounds =
        refs.getMessageConformanceStatmentFounds();

    for (MessageConformanceStatmentFound conf : messageConformanceStatmentFounds) {
      Message m = getMessage(conf.getMessageFound().getId());
      for (ConformanceStatement p : m.getConformanceStatements()) {

        if (p.getAssertion() != null && !p.getAssertion().isEmpty()) {
          p.getAssertion().replaceAll(getAssertionId(source), getAssertionId(dest));
        }
      }

    }
    List<MessagePredicateFound> messagePredicateFounds = refs.getMessagePredicateFounds();

    for (MessagePredicateFound conf : messagePredicateFounds) {
      Message m = getMessage(conf.getMessageFound().getId());
      for (Predicate p : m.getPredicates()) {

        if (p.getAssertion() != null && !p.getAssertion().isEmpty()) {
          p.getAssertion().replaceAll(getAssertionId(source), getAssertionId(dest));
        }
      }

    }

    List<SegmentConformanceStatmentFound> segmentConformanceStatmentFounds =
        refs.getSegmentConformanceStatmentFounds();

    for (SegmentConformanceStatmentFound conf : segmentConformanceStatmentFounds) {
      Segment s = getSegment(conf.getSegmenteFound().getId());
      for (ConformanceStatement p : s.getConformanceStatements()) {

        if (p.getAssertion() != null && !p.getAssertion().isEmpty()) {
          p.getAssertion().replaceAll(getAssertionId(source), getAssertionId(dest));
        }
      }
    }
    List<SegmentPredicateFound> segmentPredicateFounds = refs.getSegmentPredicateFounds();
    for (SegmentPredicateFound conf : segmentPredicateFounds) {
      Segment s = getSegment(conf.getSegmenteFound().getId());
      for (Predicate p : s.getPredicates()) {

        if (p.getAssertion() != null && !p.getAssertion().isEmpty()) {
          p.getAssertion().replaceAll(getAssertionId(source), getAssertionId(dest));
        }
      }

    }
    List<DatatypeConformanceStatmentFound> datatypeConformanceStatmentFounds =
        refs.getDatatypeConformanceStatmentFounds();

    for (DatatypeConformanceStatmentFound conf : datatypeConformanceStatmentFounds) {
      Datatype d = getDatatype(conf.getDatatypeFound().getId());
      for (ConformanceStatement p : d.getConformanceStatements()) {

        if (p.getAssertion() != null && !p.getAssertion().isEmpty()) {
          p.getAssertion().replaceAll(getAssertionId(source), getAssertionId(dest));
        }
      }

    }
    List<DatatypePredicateFound> datatypePredicateFounds = refs.getDatatypePredicateFounds();

    for (DatatypePredicateFound conf : datatypePredicateFounds) {
      Datatype s = getDatatype(conf.getDatatypeFound().getId());
      for (Predicate p : s.getPredicates()) {

        if (p.getAssertion() != null && !p.getAssertion().isEmpty()) {
          p.getAssertion().replaceAll(getAssertionId(source), getAssertionId(dest));
        }
      }

    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ReplaceService#replace(gov.nist.healthcare.
   * tools.hl7.v2.igamt.lite.domain.crossreference.SegmentCrossReference,
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment,
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment)
   */
  @Override
  public void replace(SegmentCrossReference refs, Segment source, Segment dest) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ReplaceService#replace(gov.nist.healthcare.
   * tools.hl7.v2.igamt.lite.domain.crossreference.DatatypeCrossReference,
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype,
   * gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype)
   */
  @Override
  public void replace(DatatypeCrossReference refs, Datatype source, Datatype dest) {
    // TODO Auto-generated method stub

  }

  private Message getMessage(String id) {
    if (this.visitedMessages.containsKey(id)) {
      return this.visitedMessages.get(id);
    } else {
      Message m = messageService.findById(id);
      return m;

    }
  }

  private Segment getSegment(String id) {
    if (this.visitedSegments.containsKey(id)) {
      return this.visitedSegments.get(id);
    } else {
      Segment s = segmentService.findById(id);
      return s;

    }
  }

  private Datatype getDatatype(String id) {
    if (this.visitedDatatypes.containsKey(id)) {
      return this.visitedDatatypes.get(id);
    } else {
      Datatype d = datatypeService.findById(id);
      return d;

    }
  }

  private String getAssertionId(Table table) {
    if (table.getHl7Version() != null && !table.getHl7Version().isEmpty()) {
      return table.getBindingIdentifier() + "_" + table.getHl7Version().replace('.', '-');
    } else {
      return table.getBindingIdentifier();
    }
  }

  private void saveChanges() {
    for (Message m : this.visitedMessages.values()) {
      messageService.save(m);
    }
    for (Segment s : this.visitedSegments.values()) {
      segmentService.save(s);
    }
    for (Datatype s : this.visitedDatatypes.values()) {
      datatypeService.save(s);
    }
  }


}
