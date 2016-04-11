/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;

/**
 * @author gcr1 12.Feb.16
 */
@ReadingConverter
public class DatatypeLibraryReadConverter extends AbstractReadConverter<DBObject, DatatypeLibrary> {

	private static final Logger log = LoggerFactory.getLogger(DatatypeLibraryReadConverter.class);

	public DatatypeLibraryReadConverter() {
		log.info("DatatypeReadConverter Read Converter Created");
	}

	@Override
	public DatatypeLibrary convert(DBObject source) {
		DatatypeLibrary dts = new DatatypeLibrary();
		return datatypes(source, dts);
	} 
	
	private DatatypeLibrary datatypes(DBObject source, DatatypeLibrary datatypes) {
		datatypes.setId(readMongoId(source));
		datatypes.setType(Constant.DATATYPES);
		datatypes.setAccountId((Long) source.get(ACCOUNT_ID));
		
		datatypes.setSectionContents((String) source.get(SECTION_COMMENTS));
		datatypes.setSectionDescription((String) source.get(SECTION_DESCRIPTION));
		datatypes.setSectionPosition((Integer) source.get(SECTION_POSITION));
		datatypes.setSectionTitle((String) source.get(SECTION_TITLE));
		BasicDBList datatypesDBObjects = (BasicDBList) source.get(CHILDREN);
		datatypes.setChildren(new HashSet<Datatype>());
		
		DatatypeReadConverter dtCnv = new DatatypeReadConverter();
		if (datatypesDBObjects != null) {
			for (Object childObj : datatypesDBObjects) {
				DBObject child = (DBObject) childObj;
				if (datatypes.findOne(readMongoId(child)) == null) {
					datatypes.addDatatype(dtCnv.convert(child));
				}
			}
		}

		return datatypes;
	}

//	private Datatype datatype(DBObject source, DatatypeLibrary datatypes,
//			BasicDBList datatypesDBObjects)
//					throws ProfileConversionException {
//		Datatype dt = new Datatype();
//		dt.setId(readMongoId(source));
//		dt.setType(((String) source.get("type")));
//		dt.setLabel((String) source.get("label"));
//		dt.setName(((String) source.get("name")));
//		dt.setDescription((String) source.get("description"));
//		dt.setComment(readString(source, "comment"));
//		dt.setUsageNote(readString(source, "usageNote"));
//		dt.setComponents(new ArrayList<Component>());
//		dt.setSectionPosition((Integer) source.get("sectionPosition"));
//		
//		BasicDBList componentObjects = (BasicDBList) source.get("components");
//		if (componentObjects != null) {
//			List<Component> components = new ArrayList<Component>();
//			for (Object compObj : componentObjects) {
//				DBObject compObject = (DBObject) compObj;
//				Component c = component(compObject, datatypesDBObjects);
//				components.add(c);
//			}
//			dt.setComponents(components);
//		}
//
//		BasicDBList confStsObjects = (BasicDBList) source
//				.get("conformanceStatements");
//		if (confStsObjects != null) {
//			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
//			for (Object confStObj : confStsObjects) {
//				DBObject confStObject = (DBObject) confStObj;
//				ConformanceStatement cs = conformanceStatement(confStObject);
//				confStatements.add(cs);
//			}
//			dt.setConformanceStatements(confStatements);
//		}
//
//		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
//		if (predDBObjects != null) {
//			List<Predicate> predicates = new ArrayList<Predicate>();
//			for (Object predObj : predDBObjects) {
//				DBObject predObject = (DBObject) predObj;
//				Predicate pred = predicate(predObject);
//				predicates.add(pred);
//			}
//			dt.setPredicates(predicates);
//		}
//
//		return dt;
//	}

//	private Reference reference(DBObject source) {
//		if (source != null) {
//			Reference reference = new Reference();
//			reference.setChapter(((String) source.get("chapter")));
//			reference.setSection(((String) source.get("section")));
//			reference.setPage((Integer) source.get("page"));
//			reference.setUrl((String) source.get("url"));
//			return reference;
//		}
//		return null;
//	}

//	private ConformanceStatement conformanceStatement(DBObject source) {
//		ConformanceStatement cs = new ConformanceStatement();
//		cs.setId(readMongoId(source));
//		cs.setConstraintId(((String) source.get("constraintId")));
//		cs.setConstraintTarget(((String) source.get("constraintTarget")));
//		cs.setDescription((String) source.get("description"));
//		cs.setAssertion(((String) source.get("assertion")));
//		cs.setReference(reference(((DBObject) source.get("reference"))));
//		return cs;
//	}

//	private Predicate predicate(DBObject source) {
//		Predicate p = new Predicate();
//		p.setId(readMongoId(source));
//		p.setConstraintId(((String) source.get("constraintId")));
//		p.setConstraintTarget(((String) source.get("constraintTarget")));
//		p.setDescription((String) source.get("description"));
//		p.setAssertion(((String) source.get("assertion")));
//		p.setReference(reference(((DBObject) source.get("reference"))));
//		p.setFalseUsage(Usage.valueOf(((String) source.get("falseUsage"))));
//		p.setTrueUsage(Usage.valueOf(((String) source.get("trueUsage"))));
//		return p;
//	}

//	private Component component(DBObject source, BasicDBList datatypesDBObjects)
//					throws ProfileConversionException {
//		Component c = new Component();
//		c.setId(readMongoId(source));
//		c.setType(((String) source.get("type")));
//		c.setName(((String) source.get("name")));
//		c.setComment(readString(source, "comment"));
//		c.setMinLength(getMinLength(source));
//		c.setMaxLength((String) source.get("maxLength"));
//		c.setConfLength(getConfLength(source));
//		c.setPosition((Integer) source.get("position"));
//		c.setTable(((String) source.get("table")));
//		c.setUsage(Usage.valueOf((String) source.get("usage")));
//		c.setBindingLocation((String) source.get("bindingLocation"));
//		c.setBindingStrength((String) source.get("bindingStrength"));
//		c.setDatatype(((String) source.get("datatype")));
//		return c;
//	}

//	private String readMongoId(DBObject source){
//		if ( source.get("_id") != null){
//			if (source.get("_id") instanceof ObjectId){
//				return ((ObjectId) source.get("_id")).toString();
//			} else {
//				return (String) source.get("_id");
//			}
//		} else if ( source.get("id") != null){
//			if (source.get("id") instanceof ObjectId){
//				return ((ObjectId) source.get("id")).toString();
//			} else {
//				return (String) source.get("id");
//			}
//		}
//		return null;
//	}

//	private Long readLong(DBObject source, String tag){
//		if ( source.get(tag) != null){
//			if (source.get(tag) instanceof Integer){
//				return Long.valueOf((Integer) source.get(tag));
//			} else if (source.get(tag) instanceof String) {
//				return Long.valueOf((String)source.get(tag));
//			} else if (source.get(tag) instanceof Long) {
//				return Long.valueOf((Long)source.get(tag));
//			}
//		}
//		return Long.valueOf(0);
//	}

//	private String readString(DBObject source, String tag){
//		if ( source.get(tag) != null){
//				return String.valueOf((String) source.get(tag));
//			}
//		return "";
//	} 
	
//	private Integer getMinLength(DBObject source){
//		return ((Integer) source.get("minLength") == -1 ? 0:((Integer) source.get("minLength")));
//	} 
	
//	private String getConfLength(DBObject source){
//		return "-1".equals((String) source.get("confLength")) ? "":(String) source.get("confLength");
//	} 
}
