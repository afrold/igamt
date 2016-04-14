package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Case;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Mapping;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileConversionException;

@ReadingConverter
public class SegmentReadConverter  extends AbstractReadConverter<DBObject, Segment> {

	private static final Logger log = LoggerFactory.getLogger(SegmentReadConverter.class);
	
	@Autowired
	DatatypeLibraryService datatypeLibraryService;
	
	DatatypeLibrary dtLib;
	
	public SegmentReadConverter() {
		log.info("SegmentReadConverter Read Converter Created");
	}
	
	@Override
	public Segment convert(DBObject source) {
		Segment seg = new Segment();
		seg.setId(readMongoId(source)); 
		seg.setType((String) source.get(TYPE));
		seg.setLabel((String) source.get(LABEL));
		seg.setName(((String) source.get(NAME)));
		seg.setDescription((String) source.get(DESCRIPTION));
		seg.setComment(readString(source, COMMENT));
		seg.setText1(readString(source, TEXT_1));
		seg.setText2(readString(source, TEXT_2));
		seg.setSectionPosition((Integer) source.get(SECTION_POSITION));
		seg.getLibIds().add((String) source.get(LIB_ID));
// not working		dtLib = datatypeLibraryService.findById(seg.getLibId());

		BasicDBList fieldObjects = (BasicDBList) source.get(FIELDS);
		if (fieldObjects != null) {
			List<Field> fields = new ArrayList<Field>();
			for (Object fieldObject : fieldObjects) {
				Field f = field((DBObject) fieldObject);
				fields.add(f);
			}
			seg.setFields(fields);
		}

		BasicDBList confStsObjects = (BasicDBList) source
				.get(CONFORMANCE_STATEMENTS);
		if (confStsObjects != null) {
			List<ConformanceStatement> confStatements = new ArrayList<ConformanceStatement>();
			for (Object confStObject : confStsObjects) {
				ConformanceStatement cs = conformanceStatement((DBObject) confStObject);
				confStatements.add(cs);
			}
			seg.setConformanceStatements(confStatements);
		}

		BasicDBList predDBObjects = (BasicDBList) source.get(PREDICATES);
		if (predDBObjects != null) {
			List<Predicate> predicates = new ArrayList<Predicate>();
			for (Object predObj : predDBObjects) {
				DBObject predObject = (DBObject) predObj;
				Predicate pred = predicate(predObject);
				predicates.add(pred);
			}
			seg.setPredicates(predicates);
		}

		DBObject dynamicMappingDBObject = (DBObject) source.get(DYNAMIC_MAPPING);
		if (dynamicMappingDBObject != null) {
			DynamicMapping dyn = dynamicMapping(dynamicMappingDBObject, dtLib);
			seg.setDynamicMapping(dyn);
		}

		return seg;
	}

	private DynamicMapping dynamicMapping(DBObject source, DatatypeLibrary dtLib) {
		DynamicMapping p = new DynamicMapping();
		p.setId(readMongoId(source));
		BasicDBList mappingsDBObjects = (BasicDBList) source.get(MAPPINGS);
		if (mappingsDBObjects != null) {
			List<Mapping> mappings = new ArrayList<Mapping>();
			for (Object compObj : mappingsDBObjects) {
				DBObject compObject = (DBObject) compObj;
				Mapping m = mapping(compObject, dtLib);
				mappings.add(m);
			}
			p.setMappings(mappings);
		}
		return p;
	}

	private Mapping mapping(DBObject source, DatatypeLibrary dtLib) {
		Mapping p = new Mapping();
		p.setId(readMongoId(source));
		p.setReference(((Integer) source.get(REFERENCE)));
		p.setPosition(((Integer) source.get(POSITION)));
		BasicDBList mappingsDBObjects = (BasicDBList) source.get(CASES);
		if (mappingsDBObjects != null) {
			List<Case> cases = new ArrayList<Case>();
			for (Object compObj : mappingsDBObjects) {
				DBObject compObject = (DBObject) compObj;
				Case m = toCase(compObject, dtLib);
				cases.add(m);
			}
			p.setCases(cases);
		}

		return p;
	}

	private Case toCase(DBObject source, DatatypeLibrary dtLib) {
		Case p = new Case();
		p.setId(readMongoId(source));
		p.setValue(((String) source.get(VALUE)));
		Datatype d = findDatatypeById(((String) source.get(Constant.DATATYPE)),
				dtLib);
		if (d == null) {
			throw new ProfileConversionException("Datatype "
					+ ((String) source.get(Constant.DATATYPE)) + " not found");
		}
		p.setDatatype(d.getId());
		return p;
	}

	private Datatype findDatatypeById(String id, DatatypeLibrary dtLib) {
		if (dtLib != null) {
			Datatype d = dtLib.findOne(id);
			if (d != null) {
				return d;
			}
		}

		return null;
	}

	private Field field(DBObject source) {
		Field f = new Field();
		f.setId(readMongoId(source));
		f.setType(((String) source.get(TYPE)));
		f.setName(((String) source.get(NAME)));
		f.setComment(readString(source, COMMENT));
		f.setMinLength(getMinLength(source));
		f.setMaxLength((String) source.get(MAX_LENGTH));
		f.setConfLength(getConfLength(source));
		f.setPosition((Integer) source.get(POSITION));
		f.setTable(((String) source.get(TABLE)));
		f.setUsage(Usage.valueOf((String) source.get(USAGE)));
		f.setBindingLocation((String) source.get(BINDING_LOCATION));
		f.setBindingStrength((String) source.get(BINDING_STRENGTH));
		f.setItemNo((String) source.get(ITEM_NO));
		f.setMin((Integer) source.get(MIN));
		f.setMax((String) source.get(MAX));
		f.setText(readString(source, TEXT));
		f.setDatatype(((String) source.get(Constant.DATATYPE)));
		return f;
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
		cs.setDescription((String) source.get(ASSERTION));
		cs.setAssertion(((String) source.get(FALSE_USAGE)));
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
}
