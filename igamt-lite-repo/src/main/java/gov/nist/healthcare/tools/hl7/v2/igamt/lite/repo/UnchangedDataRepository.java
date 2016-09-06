package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UnchangedData;

public interface UnchangedDataRepository extends MongoRepository<UnchangedData,String> {


	
	
}
