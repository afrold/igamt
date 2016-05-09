package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.DocumentMetaDataPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.IGDocumentPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.IGDocumentReadConverterPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.ProfileMetaDataPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.prelib.ProfilePreLib;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class IGDocumentConverterFromOldToNew{
	private static final Logger log = LoggerFactory.getLogger(IGDocumentConverterFromOldToNew.class);

	public void convert() {

		MongoOperations mongoOps;
		try {
			mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igl"));
			mongoOps.dropCollection(Table.class);
			mongoOps.dropCollection(TableLibrary.class);
			mongoOps.dropCollection(Datatype.class);
			mongoOps.dropCollection(DatatypeLibrary.class);
			mongoOps.dropCollection(Segment.class);
			mongoOps.dropCollection(SegmentLibrary.class);
			mongoOps.dropCollection(Message.class);
			mongoOps.dropCollection(IGDocument.class);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			IGDocumentReadConverterPreLib conv = new IGDocumentReadConverterPreLib();
			DBCollection coll1 = mongoOps.getCollection("igdocumentPreLib");
			DBCursor cur1 = coll1.find();
			while (cur1.hasNext()) {
				DBObject source = cur1.next();
				IGDocumentPreLib appPreLib = conv.convert(source);
				if(appPreLib.getScope().equals(IGDocumentScope.HL7STANDARD)){
					HL7STANDARD(appPreLib, mongoOps);
				}
			}
			
			DBCollection coll2 = mongoOps.getCollection("igdocumentPreLib");
			DBCursor cur2 = coll2.find();
			while (cur2.hasNext()) {
				DBObject source = cur2.next();
				IGDocumentPreLib appPreLib = conv.convert(source);
				if (appPreLib.getScope().equals(IGDocumentScope.PRELOADED)){
					PRELOADED(appPreLib, mongoOps);
				}
			}
			
			DBCollection coll3 = mongoOps.getCollection("igdocumentPreLib");
			DBCursor cur3 = coll3.find();
			while (cur3.hasNext()) {
				DBObject source = cur3.next();
				IGDocumentPreLib appPreLib = conv.convert(source);
				if (appPreLib.getScope().equals(IGDocumentScope.USER)){
					USER(appPreLib, mongoOps);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void USER(IGDocumentPreLib appPreLib, MongoOperations mongoOps){
		IGDocument app = new IGDocument();

		ProfilePreLib ppl = appPreLib.getProfile();
		Profile prof = new Profile();
		prof.setAccountId(appPreLib.getAccountId());
		prof.setBaseId(ppl.getBaseId());
		prof.setChanges(ppl.getChanges());
		prof.setComment(ppl.getComment());
		prof.setConstraintId(ppl.getConstraintId());
		ProfileMetaDataPreLib pmdpl = ppl.getMetaData();
		ProfileMetaData profileMetaData = new ProfileMetaData();
		profileMetaData.setEncodings(pmdpl.getEncodings());
		profileMetaData.setExt(pmdpl.getExt());
		profileMetaData.setHl7Version(pmdpl.getHl7Version());
		profileMetaData.setOrgName("NIST");
		profileMetaData.setSchemaVersion(pmdpl.getSchemaVersion());
		profileMetaData.setSpecificationName(pmdpl.getSpecificationName());
		profileMetaData.setStatus(pmdpl.getStatus());
		profileMetaData.setSubTitle(pmdpl.getSubTitle());
		profileMetaData.setTopics(pmdpl.getTopics());
		profileMetaData.setType(pmdpl.getType());
		prof.setMetaData(profileMetaData);
		prof.setScope(ppl.getScope());
		prof.setSectionContents(ppl.getSectionContents());
		prof.setSectionDescription(ppl.getSectionDescription());
		prof.setSectionPosition(ppl.getSectionPosition());
		prof.setSectionTitle(ppl.getSectionTitle());
		prof.setSourceId(ppl.getSourceId());
		prof.setType(ppl.getType());
		prof.setUsageNote(ppl.getUsageNote());
		prof.setScope(IGDocumentScope.USER);
		app.addProfile(prof, appPreLib.getChildSections());
		DocumentMetaDataPreLib docMetaDataPreLib = appPreLib.getMetaData();
		DocumentMetaData metaData = new DocumentMetaData();
		metaData.setDate(Constant.mdy.format(new Date()));
		metaData.setExt(docMetaDataPreLib.getExt());
		metaData.setHl7Version(appPreLib.getProfile().getMetaData().getHl7Version());
		metaData.setOrgName(docMetaDataPreLib.getOrgName());
		metaData.setSpecificationName(docMetaDataPreLib.getSpecificationName());
		metaData.setStatus(docMetaDataPreLib.getStatus());
		metaData.setSubTitle(docMetaDataPreLib.getSubTitle());
		metaData.setTitle(docMetaDataPreLib.getTitle());
		metaData.setTopics(docMetaDataPreLib.getTopics());
		app.setMetaData(metaData);
		log.info("hl7Version=" + appPreLib.getProfile().getMetaData().getHl7Version());
		Set<Message> msgsPreLib = appPreLib.getProfile().getMessages().getChildren();
		System.out.println("hl7Version=" + app.getProfile().getMetaData().getHl7Version());
		List<Constant.SCOPE> scopes = new ArrayList<Constant.SCOPE>();
		scopes.add(Constant.SCOPE.HL7STANDARD);
		
//		System.out.println(segmentLibraryService);
//		List<SegmentLibrary> segmentLibraries = segmentLibraryService.findByScopesAndVersion(scopes, app.getProfile().getMetaData().getHl7Version());
//		SegmentLibrary segmentLibrary = segmentLibraries.get(0);
//		List<DatatypeLibrary> datatypeLibraries = datatypeLibraryService.findByScopesAndVersion(scopes, app.getProfile().getMetaData().getHl7Version());
//		DatatypeLibrary datatypeLibrary = datatypeLibraries.get(0);
		
		for (Message sm : msgsPreLib) {
			sm.setId(null);
			
			for(SegmentRefOrGroup sog :sm.getChildren()){
				if(sog.getUsage().equals(Usage.B) || sog.getUsage().equals(Usage.W)){
					sog.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			System.out.println("msgs=" + app.getProfile().getMessages().getChildren().size() + " msg name="
					+ sm.getName() + " children=" + sm.getChildren().size());
			app.getProfile().getMessages().addMessage(sm);
		}
		Set<Segment> segs = appPreLib.getProfile().getSegments().getChildren();
		SegmentLibraryMetaData segMetaData = new SegmentLibraryMetaData();
		segMetaData.setDate(Constant.mdy.format(new Date()));
		segMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// segMetaData.setName(ppl.getMetaData().getName());
		segMetaData.setOrgName("NIST");
		// segMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getSegmentLibrary().setScope(Constant.SCOPE.USER);
		app.getProfile().getSegmentLibrary().setMetaData(segMetaData);
		mongoOps.insert(app.getProfile().getSegmentLibrary(), "segment-library");
		for (Segment seg : segs) {
			seg.setScope(Constant.SCOPE.USER);
			seg.setHl7Version(app.getProfile().getMetaData().getHl7Version());
			
			
			for(Field f :seg.getFields()){
				if(f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)){
					f.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			
			if (seg.getId() != null) {
//				if(seg.getLabel() == null || seg.getLabel().equals(seg.getName())){
//					SegmentLink sl = segmentLibrary.findOneByName(seg.getName());
//					seg = segmentService.findById(sl.getId());
//					seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
//					app.getProfile().getSegmentLibrary().addSegment(sl);
//				}else {
					seg.setId(ObjectId.get().toString());
					seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
					app.getProfile().getSegmentLibrary().addSegment(new SegmentLink(seg.getId(), seg.getName(), seg.getLabel().replace(seg.getName() + "_", "")));
//				}
			} else {
				log.error("Null id seg=" + seg.toString());
			}
		}
		mongoOps.save(app.getProfile().getSegmentLibrary());
		Set<Datatype> dts = appPreLib.getProfile().getDatatypes().getChildren();
		DatatypeLibraryMetaData dtMetaData = new DatatypeLibraryMetaData();
		dtMetaData.setDate(Constant.mdy.format(new Date()));
		dtMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// dtMetaData.setName(ppl.getMetaData().getName());
		dtMetaData.setOrgName("NIST");
		// dtMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getDatatypeLibrary().setScope(Constant.SCOPE.USER);
		app.getProfile().getDatatypeLibrary().setMetaData(dtMetaData);
		mongoOps.insert(app.getProfile().getDatatypeLibrary(), "datatype-library");
		for (Datatype dt : dts) {
			for(Component c : dt.getComponents()){
				if(c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)){
					c.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			if (dt.getId() != null) {
//				if(dt.getLabel() == null || dt.getLabel().equals(dt.getName())){
//					DatatypeLink dl = datatypeLibrary.findOneByName(dt.getName());
//					dt = datatypeService.findById(dt.getId());
//					dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
//					app.getProfile().getDatatypeLibrary().addDatatype(dl);
//				}else {
					dt.setId(ObjectId.get().toString());
					dt.setScope(Constant.SCOPE.USER);
					dt.setStatus(Constant.STATUS.UNPUBLISHED);
					dt.setHl7Version(app.getProfile().getMetaData().getHl7Version());
					dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
					app.getProfile().getDatatypeLibrary().addDatatype(new DatatypeLink(dt.getId(), dt.getName(), dt.getLabel().replace(dt.getName() + "_", "")));
//				}
			} else {
				log.error("Null id seg=" + dt.toString());
			}
		}
		mongoOps.save(app.getProfile().getDatatypeLibrary());
		Set<Table> tabs = appPreLib.getProfile().getTables().getChildren();
		TableLibraryMetaData tabMetaData = new TableLibraryMetaData();
		tabMetaData.setDate(Constant.mdy.format(new Date()));
		tabMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// tabMetaData.setName(ppl.getMetaData().getName());
		tabMetaData.setOrgName("NIST");
		// tabMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getTableLibrary().setScope(Constant.SCOPE.USER);
		app.getProfile().getTableLibrary().setMetaData(tabMetaData);
		mongoOps.insert(app.getProfile().getTableLibrary(), "table-library");
		for (Table tab : tabs) {
			tab.setScope(Constant.SCOPE.USER);
			tab.setHl7Version(app.getProfile().getMetaData().getHl7Version());
			if (tab.getId() != null) {
				tab.setId(ObjectId.get().toString());
				tab.getLibIds().add(app.getProfile().getTableLibrary().getId());
				app.getProfile().getTableLibrary()
						.addTable(new TableLink(tab.getId(), tab.getBindingIdentifier()));
			} else {
				log.error("Null id tab=" + tab.toString());
			}
		}
		
		
		
		mongoOps.save(app.getProfile().getTableLibrary());
		mongoOps.insert(app.getProfile().getMessages().getChildren(), "message");
		mongoOps.insert(segs, "segment");
		mongoOps.insert(dts, "datatype");
		mongoOps.insert(tabs, "table");
		mongoOps.insert(app, "igdocument");
		
	}
	
	private void PRELOADED(IGDocumentPreLib appPreLib, MongoOperations mongoOps){
		IGDocument app = new IGDocument();

		ProfilePreLib ppl = appPreLib.getProfile();
		Profile prof = new Profile();
		prof.setBaseId(ppl.getBaseId());
		prof.setChanges(ppl.getChanges());
		prof.setComment(ppl.getComment());
		prof.setConstraintId(ppl.getConstraintId());
		ProfileMetaDataPreLib pmdpl = ppl.getMetaData();
		ProfileMetaData profileMetaData = new ProfileMetaData();
		profileMetaData.setEncodings(pmdpl.getEncodings());
		profileMetaData.setExt(pmdpl.getExt());
		profileMetaData.setHl7Version(pmdpl.getHl7Version());
		profileMetaData.setOrgName("NIST");
		profileMetaData.setSchemaVersion(pmdpl.getSchemaVersion());
		profileMetaData.setSpecificationName(pmdpl.getSpecificationName());
		profileMetaData.setStatus(pmdpl.getStatus());
		profileMetaData.setSubTitle(pmdpl.getSubTitle());
		profileMetaData.setTopics(pmdpl.getTopics());
		profileMetaData.setType(pmdpl.getType());
		prof.setMetaData(profileMetaData);
		prof.setScope(ppl.getScope());
		prof.setSectionContents(ppl.getSectionContents());
		prof.setSectionDescription(ppl.getSectionDescription());
		prof.setSectionPosition(ppl.getSectionPosition());
		prof.setSectionTitle(ppl.getSectionTitle());
		prof.setSourceId(ppl.getSourceId());
		prof.setType(ppl.getType());
		prof.setUsageNote(ppl.getUsageNote());
		app.addProfile(prof);
		DocumentMetaDataPreLib docMetaDataPreLib = appPreLib.getMetaData();
		DocumentMetaData metaData = new DocumentMetaData();
		metaData.setDate(Constant.mdy.format(new Date()));
		metaData.setExt(docMetaDataPreLib.getExt());
		metaData.setHl7Version(appPreLib.getProfile().getMetaData().getHl7Version());
		metaData.setOrgName(docMetaDataPreLib.getOrgName());
		metaData.setSpecificationName(docMetaDataPreLib.getSpecificationName());
		metaData.setStatus(docMetaDataPreLib.getStatus());
		metaData.setSubTitle(docMetaDataPreLib.getSubTitle());
		metaData.setTitle(docMetaDataPreLib.getTitle());
		metaData.setTopics(docMetaDataPreLib.getTopics());
		app.setMetaData(metaData);
		log.info("hl7Version=" + appPreLib.getProfile().getMetaData().getHl7Version());
		Set<Message> msgsPreLib = appPreLib.getProfile().getMessages().getChildren();
		System.out.println("hl7Version=" + app.getProfile().getMetaData().getHl7Version());
		for (Message sm : msgsPreLib) {
			System.out.println("msgs=" + app.getProfile().getMessages().getChildren().size() + " msg name="
					+ sm.getName() + " children=" + sm.getChildren().size());
			
			for(SegmentRefOrGroup sog :sm.getChildren()){
				if(sog.getUsage().equals(Usage.B) || sog.getUsage().equals(Usage.W)){
					sog.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			app.getProfile().getMessages().addMessage(sm);
		}
		Set<Segment> segs = appPreLib.getProfile().getSegments().getChildren();
		SegmentLibraryMetaData segMetaData = new SegmentLibraryMetaData();
		segMetaData.setDate(Constant.mdy.format(new Date()));
		segMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// segMetaData.setName(ppl.getMetaData().getName());
		segMetaData.setOrgName("NIST");
		// segMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getSegmentLibrary().setScope(Constant.SCOPE.USER);
		app.getProfile().getSegmentLibrary().setMetaData(segMetaData);
		mongoOps.insert(app.getProfile().getSegmentLibrary(), "segment-library");
		for (Segment seg : segs) {
			seg.setScope(Constant.SCOPE.USER);
			
			for(Field f :seg.getFields()){
				if(f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)){
					f.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			seg.setHl7Version(app.getProfile().getMetaData().getHl7Version());
			if (seg.getId() != null) {
				seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
				app.getProfile().getSegmentLibrary().addSegment(new SegmentLink(seg.getId(), seg.getName(), seg.getLabel().replace(seg.getName() + "_", "")));
			} else {
				log.error("Null id seg=" + seg.toString());
			}
		}
		mongoOps.save(app.getProfile().getSegmentLibrary());
		Set<Datatype> dts = appPreLib.getProfile().getDatatypes().getChildren();
		DatatypeLibraryMetaData dtMetaData = new DatatypeLibraryMetaData();
		dtMetaData.setDate(Constant.mdy.format(new Date()));
		dtMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// dtMetaData.setName(ppl.getMetaData().getName());
		dtMetaData.setOrgName("NIST");
		// dtMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getDatatypeLibrary().setScope(Constant.SCOPE.USER);
		app.getProfile().getDatatypeLibrary().setMetaData(dtMetaData);
		mongoOps.insert(app.getProfile().getDatatypeLibrary(), "datatype-library");
		for (Datatype dt : dts) {
			if (dt.getId() != null) {
				for(Component c : dt.getComponents()){
					if(c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)){
						c.setUsage(Usage.X); System.out.println("Find");
					}
				}
				dt.setScope(Constant.SCOPE.USER);
				dt.setStatus(Constant.STATUS.UNPUBLISHED);
				dt.setHl7Version(app.getProfile().getMetaData().getHl7Version());
				dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
				app.getProfile().getDatatypeLibrary()
						.addDatatype(new DatatypeLink(dt.getId(), dt.getName(), dt.getLabel().replace(dt.getName() + "_", "")));
			} else {
				log.error("Null id dt=" + dt.toString());
			}
		}
		mongoOps.save(app.getProfile().getDatatypeLibrary());
		Set<Table> tabs = appPreLib.getProfile().getTables().getChildren();
		TableLibraryMetaData tabMetaData = new TableLibraryMetaData();
		tabMetaData.setDate(Constant.mdy.format(new Date()));
		tabMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// tabMetaData.setName(ppl.getMetaData().getName());
		tabMetaData.setOrgName("NIST");
		// tabMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getTableLibrary().setScope(Constant.SCOPE.USER);
		app.getProfile().getTableLibrary().setMetaData(tabMetaData);
		mongoOps.insert(app.getProfile().getTableLibrary(), "table-library");
		for (Table tab : tabs) {
			tab.setScope(Constant.SCOPE.USER);
			tab.setHl7Version(app.getProfile().getMetaData().getHl7Version());
			if (tab.getId() != null) {
				tab.getLibIds().add(app.getProfile().getTableLibrary().getId());
				app.getProfile().getTableLibrary()
						.addTable(new TableLink(tab.getId(), tab.getBindingIdentifier()));
			} else {
				log.error("Null id tab=" + tab.toString());
			}
		}
		
		
		
		mongoOps.save(app.getProfile().getTableLibrary());
		mongoOps.insert(app.getProfile().getMessages().getChildren(), "message");
		mongoOps.insert(segs, "segment");
		mongoOps.insert(dts, "datatype");
		mongoOps.insert(tabs, "table");
		mongoOps.insert(app, "igdocument");
		
	}
	
	
	private void HL7STANDARD(IGDocumentPreLib appPreLib, MongoOperations mongoOps){
		IGDocument app = new IGDocument();

		ProfilePreLib ppl = appPreLib.getProfile();
		Profile prof = new Profile();
		prof.setBaseId(ppl.getBaseId());
		prof.setChanges(ppl.getChanges());
		prof.setComment(ppl.getComment());
		prof.setConstraintId(ppl.getConstraintId());
		ProfileMetaDataPreLib pmdpl = ppl.getMetaData();
		ProfileMetaData profileMetaData = new ProfileMetaData();
		profileMetaData.setEncodings(pmdpl.getEncodings());
		profileMetaData.setExt(pmdpl.getExt());
		profileMetaData.setHl7Version(pmdpl.getHl7Version());
		profileMetaData.setOrgName("NIST");
		profileMetaData.setSchemaVersion(pmdpl.getSchemaVersion());
		profileMetaData.setSpecificationName(pmdpl.getSpecificationName());
		profileMetaData.setStatus(pmdpl.getStatus());
		profileMetaData.setSubTitle(pmdpl.getSubTitle());
		profileMetaData.setTopics(pmdpl.getTopics());
		profileMetaData.setType(pmdpl.getType());

		prof.setMetaData(profileMetaData);
		prof.setScope(ppl.getScope());
		prof.setSectionContents(ppl.getSectionContents());
		prof.setSectionDescription(ppl.getSectionDescription());
		prof.setSectionPosition(ppl.getSectionPosition());
		prof.setSectionTitle(ppl.getSectionTitle());
		prof.setSourceId(ppl.getSourceId());
		prof.setType(ppl.getType());
		prof.setUsageNote(ppl.getUsageNote());
		app.addProfile(prof);
		DocumentMetaDataPreLib docMetaDataPreLib = appPreLib.getMetaData();
		DocumentMetaData metaData = new DocumentMetaData();
		metaData.setDate(Constant.mdy.format(new Date()));
		metaData.setExt(docMetaDataPreLib.getExt());
		metaData.setHl7Version(appPreLib.getProfile().getMetaData().getHl7Version());
		metaData.setOrgName(docMetaDataPreLib.getOrgName());
		metaData.setSpecificationName(docMetaDataPreLib.getSpecificationName());
		metaData.setStatus(docMetaDataPreLib.getStatus());
		metaData.setSubTitle(docMetaDataPreLib.getSubTitle());
		metaData.setTitle(docMetaDataPreLib.getTitle());
		metaData.setTopics(docMetaDataPreLib.getTopics());
		app.setMetaData(metaData);
		log.info("hl7Version=" + appPreLib.getProfile().getMetaData().getHl7Version());
		Set<Message> msgsPreLib = appPreLib.getProfile().getMessages().getChildren();
		System.out.println("hl7Version=" + app.getProfile().getMetaData().getHl7Version());
		for (Message sm : msgsPreLib) {
			System.out.println("msgs=" + app.getProfile().getMessages().getChildren().size() + " msg name="
					+ sm.getName() + " children=" + sm.getChildren().size());
			
			for(SegmentRefOrGroup sog :sm.getChildren()){
				if(sog.getUsage().equals(Usage.B) || sog.getUsage().equals(Usage.W)){
					sog.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			app.getProfile().getMessages().addMessage(sm);
		}
		Set<Segment> segs = appPreLib.getProfile().getSegments().getChildren();
		SegmentLibraryMetaData segMetaData = new SegmentLibraryMetaData();
		segMetaData.setDate(Constant.mdy.format(new Date()));
		segMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// segMetaData.setName(ppl.getMetaData().getName());
		segMetaData.setOrgName("NIST");
		// segMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getSegmentLibrary().setScope(Constant.SCOPE.HL7STANDARD);
		app.getProfile().getSegmentLibrary().setMetaData(segMetaData);
		mongoOps.insert(app.getProfile().getSegmentLibrary(), "segment-library");
		for (Segment seg : segs) {
			seg.setScope(Constant.SCOPE.HL7STANDARD);
			for(Field f :seg.getFields()){
				if(f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)){
					f.setUsage(Usage.X); System.out.println("Find");
				}
			}
			
			seg.setHl7Version(app.getProfile().getMetaData().getHl7Version());
			if (seg.getId() != null) {
				seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
				app.getProfile().getSegmentLibrary().addSegment(new SegmentLink(seg.getId(), seg.getName(), seg.getLabel().replace(seg.getName() + "_", "")));
			} else {
				log.error("Null id seg=" + seg.toString());
			}
		}
		mongoOps.save(app.getProfile().getSegmentLibrary());
		Set<Datatype> dts = appPreLib.getProfile().getDatatypes().getChildren();
		DatatypeLibraryMetaData dtMetaData = new DatatypeLibraryMetaData();
		dtMetaData.setDate(Constant.mdy.format(new Date()));
		dtMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// dtMetaData.setName(ppl.getMetaData().getName());
		dtMetaData.setOrgName("NIST");
		// dtMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getDatatypeLibrary().setScope(Constant.SCOPE.HL7STANDARD);
		app.getProfile().getDatatypeLibrary().setMetaData(dtMetaData);
		mongoOps.insert(app.getProfile().getDatatypeLibrary(), "datatype-library");
		for (Datatype dt : dts) {
			if (dt.getId() != null) {
				for(Component c : dt.getComponents()){
					if(c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)){
						c.setUsage(Usage.X); System.out.println("Find");
					}
				}
				dt.setScope(Constant.SCOPE.HL7STANDARD);
				dt.setStatus(Constant.STATUS.PUBLISHED);
				dt.setHl7Version(app.getProfile().getMetaData().getHl7Version());
				dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
				app.getProfile().getDatatypeLibrary()
						.addDatatype(new DatatypeLink(dt.getId(), dt.getName(), dt.getLabel().replace(dt.getName() + "_", "")));
			} else {
				log.error("Null id dt=" + dt.toString());
			}
		}
		mongoOps.save(app.getProfile().getDatatypeLibrary());
		Set<Table> tabs = appPreLib.getProfile().getTables().getChildren();
		TableLibraryMetaData tabMetaData = new TableLibraryMetaData();
		tabMetaData.setDate(Constant.mdy.format(new Date()));
		tabMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
		// tabMetaData.setName(ppl.getMetaData().getName());
		tabMetaData.setOrgName("NIST");
		// tabMetaData.setVersion(ppl.getMetaData().getVersion());
		app.getProfile().getTableLibrary().setScope(Constant.SCOPE.HL7STANDARD);
		app.getProfile().getTableLibrary().setMetaData(tabMetaData);
		mongoOps.insert(app.getProfile().getTableLibrary(), "table-library");
		for (Table tab : tabs) {
			tab.setScope(Constant.SCOPE.HL7STANDARD);
			tab.setHl7Version(app.getProfile().getMetaData().getHl7Version());
			if (tab.getId() != null) {
				tab.getLibIds().add(app.getProfile().getTableLibrary().getId());
				app.getProfile().getTableLibrary()
						.addTable(new TableLink(tab.getId(), tab.getBindingIdentifier()));
			} else {
				log.error("Null id tab=" + tab.toString());
			}
		}
		mongoOps.save(app.getProfile().getTableLibrary());
		mongoOps.insert(app.getProfile().getMessages().getChildren(), "message");
		mongoOps.insert(segs, "segment");
		mongoOps.insert(dts, "datatype");
		mongoOps.insert(tabs, "table");
		mongoOps.insert(app, "igdocument");
	}
}
