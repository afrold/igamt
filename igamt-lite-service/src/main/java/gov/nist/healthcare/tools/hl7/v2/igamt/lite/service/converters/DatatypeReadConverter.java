package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileConversionException;

@ReadingConverter
public class DatatypeReadConverter  extends AbstractReadConverter<DBObject, Datatype> {

	private static final Logger log = LoggerFactory.getLogger(DatatypeReadConverter.class);
	
	public DatatypeReadConverter() {
		log.info("DatatypeReadConverter Created...");
	}
	
	@Override
	public Datatype convert(DBObject source) {
		log.info("Datatype.convert==>");
		Datatype dt = new Datatype();
		dt.setId(readMongoId(source));
		dt.setType((String) source.get(TYPE));
		dt.setLabel((String) source.get(LABEL));
		dt.setName(((String) source.get(NAME)));
		dt.setDescription((String) source.get(DESCRIPTION));
		dt.setComment(readString(source, COMMENT));
		dt.setUsageNote(readString(source, USAGE_NOTE));
		dt.setComponents(new ArrayList<Component>());
		dt.setSectionPosition((Integer) source.get(SECTION_POSITION));
		
		BasicDBList componentObjects = (BasicDBList) source.get(COMPONENTS);
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
				.get(CONFORMANCE_STATEMENTS);
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObj : confStsObjects) {
				DBObject confStObject = (DBObject) confStObj;
				ConformanceStatement cs = conformanceStatement(confStObject);
				confStatements.add(cs);
			}
			dt.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get(PREDICATES);
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			dt.setPredicates(predicates);
		}
		log.info("<==convert");

		return dt;
	} 
	
	private Reference reference(DBObject source) {
		if (source != null) {
			Reference reference = new Reference();
			reference.setChapter(((String) source.get(CHAPTER)));
			reference.setSection(((String) source.get(SECTION)));
			reference.setPage((Integer) source.get(PAGE));
			reference.setUrl((String) source.get(URL));
			return reference;
		}
		return null;
	}

	private ConformanceStatement conformanceStatement(DBObject source) {
		ConformanceStatement cs = new ConformanceStatement();
		cs.setId(readMongoId(source));
		cs.setConstraintId(((String) source.get(CONSTRAINT_ID)));
		cs.setConstraintTarget(((String) source.get(CONSTRAINT_TARGET)));
		cs.setDescription((String) source.get(DESCRIPTION));
		cs.setAssertion(((String) source.get(ASSERTION)));
		cs.setReference(reference(((DBObject) source.get(REFERENCE))));
		return cs;
	}

	private Predicate predicate(DBObject source) {
		Predicate p = new Predicate();
		p.setId(readMongoId(source));
		p.setConstraintId(((String) source.get(CONSTRAINT_ID)));
		p.setConstraintTarget(((String) source.get(CONSTRAINT_TARGET)));
		p.setDescription((String) source.get(DESCRIPTION));
		p.setAssertion(((String) source.get(ASSERTION)));
		p.setReference(reference(((DBObject) source.get(REFERENCE))));
		p.setFalseUsage(source.get(FALSE_USAGE) != null ? Usage.valueOf(((String) source.get(FALSE_USAGE))):null);
		p.setTrueUsage(source.get(TRUE_USAGE) != null ?Usage.valueOf(((String) source.get(TRUE_USAGE))):null);
		return p;
	}

	private Component component(DBObject source)
					throws ProfileConversionException {
		Component c = new Component();
		c.setId(readMongoId(source));
		c.setType(((String) source.get(TYPE)));
		c.setName(((String) source.get(NAME)));
		c.setComment(readString(source, COMMENT));
		c.setMinLength(getMinLength(source));
		c.setMaxLength((String) source.get(MAX_LENGTH));
		c.setConfLength(getConfLength(source));
		c.setPosition((Integer) source.get(POSITION));
		c.setTable(((String) source.get(Constant.TABLE)));
		c.setUsage(Usage.valueOf((String) source.get(USAGE)));
		c.setBindingLocation((String) source.get(BINDING_LOCATION));
		c.setBindingStrength((String) source.get(BINDING_STRENGTH));
		c.setDatatype(((String) source.get(Constant.DATATYPE)));
		return c;
	}
}