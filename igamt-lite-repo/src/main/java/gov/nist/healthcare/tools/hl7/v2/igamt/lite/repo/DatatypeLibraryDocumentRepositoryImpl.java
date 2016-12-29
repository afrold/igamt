package gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.STATUS;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;

public class DatatypeLibraryDocumentRepositoryImpl implements DatatypeLibraryDocumentOperations {

	private Logger log = LoggerFactory
			.getLogger(DatatypeLibraryRepositoryImpl.class);

	@Autowired
	private MongoOperations mongo;

	@Override
	public List<DatatypeLibraryDocument> findByScope(SCOPE scope, Long accountId) {

		Criteria where = Criteria.where("scope").is(scope);


			where.andOperator(Criteria.where("accountId").is(accountId));
		

		Query qry = Query.query(where);
		List<DatatypeLibraryDocument> list = mongo.find(qry, DatatypeLibraryDocument.class);
		log.debug("DatatypeLibraryRespositoryImpl.findByScopes list.size()="
				+ list.size());
		return list;
	}
	
	@Override
	public List<DatatypeLibraryDocument> findByScope(SCOPE scope) {

		Criteria where = Criteria.where("scope").is(scope);



		Query qry = Query.query(where);
		List<DatatypeLibraryDocument> list = mongo.find(qry, DatatypeLibraryDocument.class);
		log.debug("DatatypeLibraryRespositoryImpl.findByScopes list.size()="
				+ list.size());
		return list;
	}
	
	@Override
	public List<DatatypeLibraryDocument> findScopesNVersion(List<SCOPE> scopes,
			String hl7Version) {
		log.info("DatatypeLibraryRespositoryImpl.findByScopesAndVersion="
				+ hl7Version);
		Criteria where = Criteria.where("scope").in(scopes);
		if (hl7Version != null) {
			where.andOperator(Criteria.where("metaData.hl7Version").is(
					hl7Version));
		}
		Query qry = Query.query(where);
		List<DatatypeLibraryDocument> list = mongo.find(qry, DatatypeLibraryDocument.class);
		log.info("DatatypeLibraryRespositoryImpl.findByScopesAndVersion list.size()="
				+ list.size());
		return list;
	}

	@Override
	public List<DatatypeLibraryDocument> findByAccountId(Long accountId,
			String hl7Version) {
		log.debug("DatatypeLibraryRespositoryImpl.findStandardByVersion="
				+ hl7Version);
		Criteria where = Criteria
				.where("accountId")
				.is(accountId)
				.andOperator(Criteria.where("scope").is(SCOPE.USER.name()),
							Criteria.where("metaData.hl7Version").is(hl7Version));
		Query qry = Query.query(where);
		List<DatatypeLibraryDocument> list = mongo.find(qry, DatatypeLibraryDocument.class);
		log.debug("DatatypeLibraryRespositoryImpl.findStandardByVersion list.size()="
				+ list.size());
		return list;
	}

	@Override
	public List<String> findHl7Versions() {
		Criteria where = Criteria.where("scope").is(SCOPE.HL7STANDARD);
		Query qry = Query.query(where);
		qry.fields().include("metaData.hl7Version");
		List<DatatypeLibraryDocument> dtLibs = mongo.find(qry, DatatypeLibraryDocument.class);
		List<String> versions = new ArrayList<String>();
		for (DatatypeLibraryDocument dtLib : dtLibs) {
			versions.add(dtLib.getMetaData().getHl7Version());
		}
		return versions;
	}

	@Override
	public DatatypeLibraryDocument findById(String id) {
		log.debug("DatatypeLibraryRespositoryImpl.findById=" + id);
		Criteria where = Criteria.where("id").is(id);
		Query qry = Query.query(where);
		DatatypeLibraryDocument datatypeLibrary = null;
		List<DatatypeLibraryDocument> datatypeLibraries = mongo.find(qry,
				DatatypeLibraryDocument.class);
		if (datatypeLibraries != null && datatypeLibraries.size() > 0) {
			datatypeLibrary = datatypeLibraries.get(0);
		}
		return datatypeLibrary;
	}

	@Override
	public List<DatatypeLibraryDocument> findDups(DatatypeLibrary dtl) {
		Criteria where = Criteria.where("scope").in(dtl.getScope());
		where.andOperator(Criteria.where("ext").is(dtl.getExt()));
		Query qry = Query.query(where);
		List<DatatypeLibraryDocument> datatypeLibraries = mongo.find(qry,
				DatatypeLibraryDocument.class);
		return datatypeLibraries;
	}

	@Override
	public List<DatatypeLibraryDocument> findByIds(Set<String> ids) {
		Criteria where = Criteria.where("id").in(ids);
		Query qry = Query.query(where);
		List<DatatypeLibraryDocument> libraries = mongo
				.find(qry, DatatypeLibraryDocument.class);
		return libraries;
	}

	@Override
	public List<DatatypeLibraryDocument> findLibrariesByFlavorName(SCOPE scope,
			String hl7Version, String name, Long accountId) {
		List<DatatypeLibraryDocument> libraries = null;
		Criteria libCriteria = null;
		if (scope.equals(SCOPE.HL7STANDARD) || scope.equals(SCOPE.MASTER)) {
			libCriteria = Criteria
					.where("scope")
					.is(scope)
					.andOperator(
							Criteria.where("metaData.hl7Version")
									.is(hl7Version));
			Criteria linksCriteria = Criteria.where("children").elemMatch(
					Criteria.where("name").is(name));
			BasicQuery query = new BasicQuery(libCriteria.getCriteriaObject(),
					linksCriteria.getCriteriaObject());
			libraries = mongo.find(query, DatatypeLibraryDocument.class);
			if (libraries != null) {
				Set<String> ids = new HashSet<String>();
				for (DatatypeLibraryDocument lib : libraries) {
					ids.add(lib.getId());
				}
				libraries = findByIds(ids);
			}
		}
		return libraries;
	}

	@Override
	public List<DatatypeLink> findFlavors(SCOPE scope, String hl7Version,
			String name, Long accountId) {
		List<DatatypeLink> links = null;
		if (scope.equals(SCOPE.HL7STANDARD) || scope.equals(SCOPE.MASTER)) {
			Criteria libCriteria = Criteria
					.where("scope")
					.is(scope)
					.andOperator(
							Criteria.where("metaData.hl7Version")
									.is(hl7Version))
					.andOperator(
							Criteria.where("accountId")
									.is(accountId).andOperator(Criteria.where("children").elemMatch(
					Criteria.where("name")
							.is(name)
							.andOperator(
									Criteria.where("status").is(
											STATUS.PUBLISHED))))
									);
			Query query =  Query.query(libCriteria);
			links = mongo.find(query, DatatypeLink.class);
		}
		return links;
	}


}
