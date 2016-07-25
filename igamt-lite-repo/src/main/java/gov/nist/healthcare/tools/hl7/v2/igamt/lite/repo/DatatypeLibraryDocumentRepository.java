package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;

public interface DatatypeLibraryDocumentRepository extends MongoRepository<DatatypeLibraryDocument, String> ,DatatypeLibraryDocumentOperations {
	
	public List<DatatypeLibraryDocument> findAll();
	@Query(value = "{ 'children.name' : ?0, 'metaData.hl7Version':?1,'scope':?2}")
	public List<DatatypeLibraryDocument> findByNameAndHl7VersionAndScope(String name, String hl7Version, String scope);

}
