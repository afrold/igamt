package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeMessage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.CompositeMessageRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.SegmentOrGroupRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeMessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;

@Service
public class CompositeMessageServiceImpl implements CompositeMessageService {

  Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

  @Autowired
  private CompositeMessageRepository compositeMessageRepository;

  @Autowired
  private SegmentOrGroupRepository segmentOrGroupRepository;

  @Override
  public CompositeMessage findById(String id) {
    log.info("CompositeMessageServiceImpl.findById=" + id);
    return compositeMessageRepository.findOne(id);
  }

  @Override
  public CompositeMessage save(CompositeMessage compositeMessage) {
    compositeMessage.setDateUpdated(DateUtils.getCurrentDate());
    return compositeMessageRepository.save(compositeMessage);
  }

  @Override
  public SegmentOrGroup saveSegOrGrp(SegmentOrGroup segmentOrGroup) {
    segmentOrGroup.setDateUpdated(DateUtils.getCurrentDate());
    return segmentOrGroupRepository.save(segmentOrGroup);

  }

  @Override
  public SegmentOrGroup getSegOrGrp(String id) {

    return segmentOrGroupRepository.findOne(id);

  }

  @Override
  public void deleteSegOrGrp(String id) {
    segmentOrGroupRepository.delete(id);
  }

  @Override
  public void delete(String id) {
    compositeMessageRepository.delete(id);
  }

}
