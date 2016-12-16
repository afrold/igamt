package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentOrGroup;


public interface SegmentOrGroupRepository extends MongoRepository<SegmentOrGroup, String>{

}
