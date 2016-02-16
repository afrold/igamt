package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileConversionException;

public class DatatypeReadConverter implements Converter<DBObject, Datatype> {

	@Override
	public Datatype convert(DBObject source) {
		Datatype dt = new Datatype();
		dt.setId(readMongoId(source));
		dt.setType(((String) source.get("type")));
		dt.setLabel((String) source.get("label"));
		dt.setName(((String) source.get("name")));
		dt.setDescription((String) source.get("description"));
		dt.setComment(readString(source, "comment"));
		dt.setUsageNote(readString(source, "usageNote"));
		dt.setComponents(new ArrayList<Component>());
		dt.setSectionPosition((Integer) source.get("sectionPosition"));
		
		BasicDBList componentObjects = (BasicDBList) source.get("components");
		if (componentObjects != null) {
			List<Component> components = new ArrayList<Component>();
			for (Object compObj : componentObjects) {
				DBObject compObject = (DBObject) compObj;
				Component c = component(compObject);
				components.add(c);
			}
			dt.setComponents(components);
		}

		BasicDBList confStsObjects = (BasicDBList) source
				.get("conformanceStatements");
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObj : confStsObjects) {
				DBObject confStObject = (DBObject) confStObj;
				ConformanceStatement cs = conformanceStatement(confStObject);
				confStatements.add(cs);
			}
			dt.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get("predicates");
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			dt.setPredicates(predicates);
		}
		return dt;
	} 
	
	private Reference reference(DBObject source) {
		if (source != null) {
			Reference reference = new Reference();
			reference.setChapter(((String) source.get("chapter")));
			reference.setSection(((String) source.get("section")));
			reference.setPage((Integer) source.get("page"));
			reference.setUrl((String) source.get("url"));
			return reference;
		}
		return null;
	}

	private ConformanceStatement conformanceStatement(DBObject source) {
		ConformanceStatement cs = new ConformanceStatement();
		cs.setId(readMongoId(source));
		cs.setConstraintId(((String) source.get("constraintId")));
		cs.setConstraintTarget(((String) source.get("constraintTarget")));
		cs.setDescription((String) source.get("description"));
		cs.setAssertion(((String) source.get("assertion")));
		cs.setReference(reference(((DBObject) source.get("reference"))));
		return cs;
	}

	private Predicate predicate(DBObject source) {
		Predicate p = new Predicate();
		p.setId(readMongoId(source));
		p.setConstraintId(((String) source.get("constraintId")));
		p.setConstraintTarget(((String) source.get("constraintTarget")));
		p.setDescription((String) source.get("description"));
		p.setAssertion(((String) source.get("assertion")));
		p.setReference(reference(((DBObject) source.get("reference"))));
		p.setFalseUsage(Usage.valueOf(((String) source.get("falseUsage"))));
		p.setTrueUsage(Usage.valueOf(((String) source.get("trueUsage"))));
		return p;
	}

	private Component component(DBObject source)
					throws ProfileConversionException {
		Component c = new Component();
		c.setId(readMongoId(source));
		c.setType(((String) source.get("type")));
		c.setName(((String) source.get("name")));
		c.setComment(readString(source, "comment"));
		c.setMinLength(getMinLength(source));
		c.setMaxLength((String) source.get("maxLength"));
		c.setConfLength(getConfLength(source));
		c.setPosition((Integer) source.get("position"));
		c.setTable(((String) source.get("table")));
		c.setUsage(Usage.valueOf((String) source.get("usage")));
		c.setBindingLocation((String) source.get("bindingLocation"));
		c.setBindingStrength((String) source.get("bindingStrength"));
		c.setDatatype(((String) source.get("datatype")));
		return c;
	}

	private String readMongoId(DBObject source){
		if ( source.get("_id") != null){
			if (source.get("_id") instanceof ObjectId){
				return ((ObjectId) source.get("_id")).toString();
			} else {
				return (String) source.get("_id");
			}
		} else if ( source.get("id") != null){
			if (source.get("id") instanceof ObjectId){
				return ((ObjectId) source.get("id")).toString();
			} else {
				return (String) source.get("id");
			}
		}
		return null;
	}

	private Long readLong(DBObject source, String tag){
		if ( source.get(tag) != null){
			if (source.get(tag) instanceof Integer){
				return Long.valueOf((Integer) source.get(tag));
			} else if (source.get(tag) instanceof String) {
				return Long.valueOf((String)source.get(tag));
			} else if (source.get(tag) instanceof Long) {
				return Long.valueOf((Long)source.get(tag));
			}
		}
		return Long.valueOf(0);
	}

	private String readString(DBObject source, String tag){
		if ( source.get(tag) != null){
				return String.valueOf((String) source.get(tag));
			}
		return "";
	} 
	
	private Integer getMinLength(DBObject source){
		return ((Integer) source.get("minLength") == -1 ? 0:((Integer) source.get("minLength")));
	} 
	
	private String getConfLength(DBObject source){
		return "-1".equals((String) source.get("confLength")) ? "":(String) source.get("confLength");
	} 

}
