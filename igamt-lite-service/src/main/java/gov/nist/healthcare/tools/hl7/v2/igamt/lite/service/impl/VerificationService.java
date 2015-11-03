package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerification;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementVerificationResult;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class VerificationService {


	public VerificationService() {
		super();
	}

	public ElementVerification verifyMessages(Profile p, Profile baseP, String id, String type) {
		ElementVerification evm = new ElementVerification(id, type);
		for (Message m: p.getMessages().getChildren()){
			evm.addChildrenVerification(verifyMessages(p, baseP, m.getId(), m.getType()));
		}
		return evm;
	}

	public ElementVerification verifyMessage(Profile p, Profile baseP, String id, String type) {
		ElementVerification evm = new ElementVerification(id, type);
		Message m = p.getMessages().findOne(id);

		for (SegmentRefOrGroup srog : m.getChildren()){
			evm.addChildrenVerification(verifySegmentOrGroup(p, baseP, srog.getId(), srog.getType()));
		}
		return evm;
	}

	private ElementVerification verifySegmentRef(SegmentRefOrGroup srog) {
		String result = "";
		ElementVerification evsrog = new ElementVerification(srog.getId(), srog.getType());
		ElementVerificationResult evsrogRst = new ElementVerificationResult("usage", srog.getUsage().value(), result);
		evsrog.addElementVerifications(evsrogRst);

		result = this.validateChangeCardinality(String.valueOf(srog.getMin()), srog.getMax(), srog.getUsage());
		evsrogRst = new ElementVerificationResult("min", String.valueOf(srog.getMin()), result);
		evsrog.addElementVerifications(evsrogRst);

		result = this.validateChangeCardinality(String.valueOf(srog.getMin()), srog.getMax(), srog.getUsage());
		evsrogRst = new ElementVerificationResult("max", String.valueOf(srog.getMax()), result);
		evsrog.addElementVerifications(evsrogRst);

		return evsrog;
	}

	private ElementVerification verifyGroup(SegmentRefOrGroup srog) {
		ElementVerification evsrog = verifySegmentRef(srog);
		for (SegmentRefOrGroup child : ((Group) srog).getChildren()){
			if (child instanceof SegmentRef){
				evsrog.addChildrenVerification(verifySegmentRef(child));
			} else if (child instanceof Group){
				evsrog.addChildrenVerification(verifyGroup(child));
			}
		}
		return evsrog;
	}

	public ElementVerification verifySegmentOrGroup(Profile p, Profile baseP, String id, String type) {
		SegmentRefOrGroup srog = p.getMessages().findOneSegmentRefOrGroup(id);
		ElementVerification evSrog = verifySegmentRef(srog); //verify usage, min and max for segref and group
		if (srog instanceof Group){
			evSrog.addChildrenVerification(verifyGroup(srog));
		}
		return evSrog;
	}

	public ElementVerification verifySegments(Profile p, Profile baseP, String id, String type) {
		ElementVerification evsLib = new ElementVerification(id, type);
		for (Segment s : p.getSegments().getChildren()){
			ElementVerification evs = verifySegment(p, baseP, s.getId(), s.getType());
			evs.addChildrenVerification(evsLib);
		}
		return evsLib;
	}

	public ElementVerification verifySegment(Profile p, Profile baseP, String id, String type) {
		ElementVerification evs = new ElementVerification(id, type);
		Segment s = p.getSegments().findOneSegmentById(id);
		for (Field f : s.getFields()){
			ElementVerification evf = verifyField(p, baseP, f.getId(), f.getType());
			evs.addChildrenVerification(evf);
		}
		return evs;
	}

	public InputStream verifySegment2(Profile p, Profile baseP, String id, String type) {
		String result = "";
		Segment s = p.getSegments().findOneSegmentById(id);

		try {
			//Create temporary file
			File tmpJsonFile = File.createTempFile("resultTmp", ".json");

			//Generate json file
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createGenerator(new FileWriter(tmpJsonFile));
			generator.setPrettyPrinter(new DefaultPrettyPrinter());	


			generator.writeStartObject();
			generator.writeStringField("id", id);
			generator.writeStringField("type", type);

			generator.writeArrayFieldStart("eltVerification");

			for (Field f : s.getFields()){
				generator.writeStartObject();
				generator.writeStringField("eltName", "usage");
				generator.writeStringField("eltAtt", f.getUsage().value());
				result = this.validateChangeUsage(p.getMetaData().getHl7Version(), 
						baseP.getSegments().findOneField(f.getId()).getUsage(), 
						p.getSegments().findOneField(f.getId()).getUsage());
				generator.writeStringField("result", result);
				generator.writeEndObject();

				generator.writeStartObject();
				generator.writeStringField("eltName", "minLength");
				generator.writeStringField("eltAtt", String.valueOf(f.getMinLength()));
				result = this.validateChangeLength(String.valueOf(f.getMinLength()), f.getMaxLength());
				generator.writeStringField("result", result);
				generator.writeEndObject();

				generator.writeStartObject();
				generator.writeStringField("eltName", "maxLength");
				generator.writeStringField("eltAtt", String.valueOf(f.getMaxLength()));
				result = this.validateChangeLength(String.valueOf(f.getMinLength()), f.getMaxLength());
				generator.writeStringField("result", result);
				generator.writeEndObject();

				generator.writeStartObject();
				generator.writeStringField("eltName", "min");
				generator.writeStringField("eltAtt", String.valueOf(f.getMin()));
				result = this.validateChangeCardinality(String.valueOf(f.getMin()), f.getMax(), f.getUsage());
				generator.writeStringField("result", result);
				generator.writeEndObject();

				generator.writeStartObject();
				generator.writeStringField("eltName", "max");
				generator.writeStringField("eltAtt", String.valueOf(f.getMax()));
				result = this.validateChangeCardinality(String.valueOf(f.getMin()), f.getMax(), f.getUsage());
				generator.writeStringField("result", result);
				generator.writeEndObject();

			}

			generator.writeEndArray();
			generator.writeEndObject();

			generator.close();

			return FileUtils.openInputStream(tmpJsonFile);
		} catch (IOException e) {
			return new NullInputStream(1L);
		} 
	}

	public ElementVerification verifyField(Profile p, Profile baseP, String id, String type) {
		String result = "";
		ElementVerification evc = new ElementVerification(id, type);
		Field f = p.getSegments().findOneField(id);

		ElementVerificationResult evcRst = new ElementVerificationResult("usage", f.getUsage().value(), result);
		evc.addElementVerifications(evcRst);

		result = this.validateChangeLength(String.valueOf(f.getMinLength()), f.getMaxLength());
		evcRst = new ElementVerificationResult("minLength", String.valueOf(f.getMinLength()), result);
		evc.addElementVerifications(evcRst);

		result = this.validateChangeLength(String.valueOf(f.getMinLength()), f.getMaxLength());
		evcRst = new ElementVerificationResult("maxLength", String.valueOf(f.getMaxLength()), result);
		evc.addElementVerifications(evcRst);

		result = this.validateChangeCardinality(String.valueOf(f.getMin()), f.getMax(), f.getUsage());
		evcRst = new ElementVerificationResult("min", String.valueOf(f.getMin()), result);
		evc.addElementVerifications(evcRst);

		result = this.validateChangeCardinality(String.valueOf(f.getMin()), f.getMax(), f.getUsage());
		evcRst = new ElementVerificationResult("max", String.valueOf(f.getMax()), result);
		evc.addElementVerifications(evcRst);

		return evc;
	}



	public ElementVerification verifyDatatypes(Profile p, Profile baseP, String id, String type) {
		Datatypes dtLib = p.getDatatypes();
		ElementVerification evdtLib = new ElementVerification(id, type);
		for (Datatype dt : dtLib.getChildren()){
			evdtLib.addChildrenVerification(verifyDatatype(p, baseP, dt.getId(), dt.getType()));
		}
		return evdtLib;
	}

	public ElementVerification verifyDatatype(Profile p, Profile baseP, String id, String type) {
		Datatype dt = p.getDatatypes().findOne(id); 
		ElementVerification evdt = new ElementVerification(id, type);
		for (Component c : dt.getComponents()){
			evdt.addChildrenVerification(verifyComponent(p, baseP, c.getId(), c.getType()));
		}
		return evdt;
	}

	public InputStream verifyDatatype2(Profile p, Profile baseP, String id, String type) {
		String result = "";
		Datatype dt = p.getDatatypes().findOne(id);

		try {
			//Create temporary file
			File tmpJsonFile = File.createTempFile("resultTmp", ".json");

			//Generate json file
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createGenerator(new FileWriter(tmpJsonFile));
			generator.setPrettyPrinter(new DefaultPrettyPrinter());	


			generator.writeStartObject();
			generator.writeStringField("id", id);
			generator.writeStringField("type", type);

			generator.writeArrayFieldStart("eltVerification");

			for (Component c : dt.getComponents()){
				generator.writeStartObject();
				generator.writeStringField("eltName", "usage");
				generator.writeStringField("eltAtt", c.getUsage().value());
				result = this.validateChangeUsage(p.getMetaData().getHl7Version(), 
						baseP.getDatatypes().findOneComponent(c.getId()).getUsage(), 
						p.getDatatypes().findOneComponent(c.getId()).getUsage());
				generator.writeStringField("result", result);
				generator.writeEndObject();

				generator.writeStartObject();
				generator.writeStringField("eltName", "minLength");
				generator.writeStringField("eltAtt", String.valueOf(c.getMinLength()));
				result = this.validateChangeLength(String.valueOf(c.getMinLength()), c.getMaxLength());
				generator.writeStringField("result", result);
				generator.writeEndObject();

				generator.writeStartObject();
				generator.writeStringField("eltName", "maxLength");
				generator.writeStringField("eltAtt", String.valueOf(c.getMaxLength()));
				result = this.validateChangeLength(String.valueOf(c.getMinLength()), c.getMaxLength());
				generator.writeStringField("result", result);
				generator.writeEndObject();

			}

			generator.writeEndArray();
			generator.writeEndObject();

			generator.close();

			return FileUtils.openInputStream(tmpJsonFile);
		} catch (IOException e) {
			return new NullInputStream(1L);
		} 
	}

	public ElementVerification verifyComponent(Profile p, Profile baseP, String id, String type) {
		String result = "";
		ElementVerification evc = new ElementVerification(id, type);
		Component c = p.getDatatypes().findOneComponent(id);

		ElementVerificationResult evcRst = new ElementVerificationResult("usage", c.getUsage().value(), result);
		evc.addElementVerifications(evcRst);

		result = this.validateChangeLength(String.valueOf(c.getMinLength()), c.getMaxLength());
		evcRst = new ElementVerificationResult("minLength", String.valueOf(c.getMinLength()), result);
		evc.addElementVerifications(evcRst);

		result = this.validateChangeLength(String.valueOf(c.getMinLength()), c.getMaxLength());
		evcRst = new ElementVerificationResult("maxLength", String.valueOf(c.getMaxLength()), result);
		evc.addElementVerifications(evcRst);

		Datatype dt = p.getDatatypes().findOneDatatypeByLabel(c.getDatatype());
		evc.addChildrenVerification(verifyDatatype(p, baseP, dt.getId(), dt.getType()));

		Table t = p.getTables().findOneTableById(c.getTable());
		evc.addChildrenVerification(verifyValueSet(p, baseP, t.getId(), t.getType()));

		return evc;
	}

	public ElementVerification verifyValueSetLibrary(Profile p, Profile baseP, String id, String type) {
		// Type is ValueSet (or Table)
		ElementVerification evTLib = new ElementVerification(id, type);
		for (Table t : p.getTables().getChildren()){
			evTLib.addChildrenVerification(verifyValueSet(p, baseP, t.getId(), t.getType()));
		}
		return evTLib;
	}


	public ElementVerification verifyValueSet(Profile p, Profile baseP, String id, String type) {
		// Type is ValueSet (or Table)
		String result = "";
		Table t = p.getTables().findOneTableById(id);
		ElementVerification evt = new ElementVerification(id, type);
		for (Code c : t.getCodes()){
			result = this.validateChangeUsage(p.getMetaData().getHl7Version(), 
					Usage.fromValue(baseP.getTables().findOneCodeById(id).getCodeUsage()), 
					Usage.fromValue(p.getTables().findOneCodeById(id).getCodeUsage()));
			ElementVerification evc = new ElementVerification(c.getId(), c.getType());
			ElementVerificationResult evcRst = new ElementVerificationResult("usage", c.getCodeUsage(), result);
			evc.addElementVerifications(evcRst);
			evt.addChildrenVerification(evc);
		} 
		return evt;
	}


	public InputStream verifyValueSet2(Profile p, Profile baseP, String id, String type) {
		// Type is ValueSet (or Table)
		String result = "";
		Table t = p.getTables().findOneTableById(id);

		try {
			//Create temporary file
			File tmpJsonFile = File.createTempFile("resultTmp", ".json");

			//Generate json file
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createGenerator(new FileWriter(tmpJsonFile));
			generator.setPrettyPrinter(new DefaultPrettyPrinter());	


			generator.writeStartObject();
			generator.writeStringField("id", id);
			generator.writeStringField("type", type);

			generator.writeArrayFieldStart("eltVerification");

			for (Code c : t.getCodes()){

				generator.writeStartObject();
				generator.writeStringField("eltName", "usage");
				generator.writeStringField("eltAtt", c.getCodeUsage());
				result = this.validateChangeUsage(p.getMetaData().getHl7Version(), 
						Usage.fromValue(baseP.getTables().findOneCodeById(id).getCodeUsage()), 
						Usage.fromValue(p.getTables().findOneCodeById(id).getCodeUsage()));
				generator.writeStringField("result", result);
				generator.writeEndObject();

			}

			generator.writeEndArray();
			generator.writeEndObject();

			generator.close();

			return FileUtils.openInputStream(tmpJsonFile);
		} catch (IOException e) {
			return new NullInputStream(1L);
		} 
	}


	public ElementVerification verifyUsage(Profile p, Profile baseP, String id, String type, String eltName, String eltValue){
		// Type can be SegmentRef, Group, Field, Component, Code
		// EltName is Usage
		String hl7Version = p.getMetaData().getHl7Version();
		Usage referenceUsage = Usage.R; 
		Usage currentUsage = Usage.R;

		switch(type){
		case "segmentRef":
			SegmentRefOrGroup srog = p.getMessages().findOneSegmentRefOrGroup(id);
			currentUsage = srog.getUsage();
			SegmentRefOrGroup basesrog = baseP.getMessages().findOneSegmentRefOrGroup(id);
			referenceUsage = basesrog.getUsage(); 
			break;
		case "group":
			SegmentRefOrGroup srog_ = p.getMessages().findOneSegmentRefOrGroup(id);
			currentUsage = srog_.getUsage();
			SegmentRefOrGroup basesrog_ = baseP.getMessages().findOneSegmentRefOrGroup(id);
			referenceUsage = basesrog_.getUsage(); 
			break;
		case "field":
			Field f = p.getSegments().findOneField(id);
			currentUsage = f.getUsage();
			Field basef = baseP.getSegments().findOneField(id);
			referenceUsage = basef.getUsage(); 
			break;
		case "component":
			Component c = p.getDatatypes().findOneComponent(id);
			currentUsage = c.getUsage();
			Component basec = baseP.getDatatypes().findOneComponent(id);
			referenceUsage = basec.getUsage();
			break;
		case "code":
			Code cd = p.getTables().findOneCodeById(id);
			currentUsage = Usage.fromValue(cd.getCodeUsage());
			Code basecd = baseP.getTables().findOneCodeById(id);
			referenceUsage = Usage.fromValue(basecd.getCodeUsage());
			break;
		}

		String result = this.validateChangeUsage(hl7Version, referenceUsage, currentUsage);
		ElementVerification ev = new ElementVerification(id, type);
		ElementVerificationResult evRst = new ElementVerificationResult(eltName, eltValue, result);
		ev.addElementVerifications(evRst);

		return ev;
	}

	public InputStream verifyUsage2(Profile p, Profile baseP, String id, String type, String eltName, String eltValue){
		// Type can be Field, Component, Code
		// EltName is Usage
		String hl7Version = p.getMetaData().getHl7Version();
		Usage referenceUsage = Usage.R; 
		Usage currentUsage = Usage.R;

		switch(type){
		case "segmentreforgroup":
			SegmentRefOrGroup srog = p.getMessages().findOneSegmentRefOrGroup(id);
			currentUsage = srog.getUsage();
			SegmentRefOrGroup basesrog = baseP.getMessages().findOneSegmentRefOrGroup(id);
			referenceUsage = basesrog.getUsage(); 
			break;
		case "field":
			Field f = p.getSegments().findOneField(id);
			currentUsage = f.getUsage();
			Field basef = baseP.getSegments().findOneField(id);
			referenceUsage = basef.getUsage(); 
			break;
		case "component":
			Component c = p.getDatatypes().findOneComponent(id);
			currentUsage = c.getUsage();
			Component basec = baseP.getDatatypes().findOneComponent(id);
			referenceUsage = basec.getUsage();
			break;
		case "code":
			Code cd = p.getTables().findOneCodeById(id);
			currentUsage = Usage.fromValue(cd.getCodeUsage());
			Code basecd = baseP.getTables().findOneCodeById(id);
			referenceUsage = Usage.fromValue(basecd.getCodeUsage());
			break;
		}

		String result = this.validateChangeUsage(hl7Version, referenceUsage, currentUsage);
		return this.generateOneJsonResult(id, type, eltName, eltValue, result);
	}

	public ElementVerification verifyCardinality(Profile p, String id, String type, String eltName, String eltValue){
		//Type can be Field
		//EltName can be cardMin or cardMax

		Field f = p.getSegments().findOneField(id);

		String currentMin = (String) (eltName.equalsIgnoreCase("min") ? eltValue : f.getMin());
		String currentMax = (String) (eltName.equalsIgnoreCase("max") ? eltValue : f.getMax());
		Usage currentUsage = f.getUsage();

		String result = this.validateChangeCardinality(currentMin, currentMax, currentUsage);

		ElementVerification ev = new ElementVerification(id, type);
		ElementVerificationResult evRst = new ElementVerificationResult(eltName, eltValue, result);
		ev.addElementVerifications(evRst);
		return ev;
	}

	public InputStream verifyCardinality2(Profile p, String id, String type, String eltName, String eltValue){
		//Type can be Field
		//EltName can be cardMin or cardMax

		Field f = p.getSegments().findOneField(id);

		String currentMin = (String) (eltName.equalsIgnoreCase("min") ? eltValue : f.getMin());
		String currentMax = (String) (eltName.equalsIgnoreCase("max") ? eltValue : f.getMax());
		Usage currentUsage = f.getUsage();

		String result = this.validateChangeCardinality(currentMin, currentMax, currentUsage);
		return this.generateOneJsonResult(id, type, eltName, eltValue, result);

	}

	public ElementVerification verifyLength(Profile p, String id, String type, String eltName, String eltValue){
		//type is either Field or Component
		//eltName is minLength or maxLength
		String currentMinLength = "";
		String currentMaxLength = "";
		Field f; Component c;
		if (type.equalsIgnoreCase("field")){
			f = p.getSegments().findOneField(id);
			switch(eltName){
			case "minLength":
				currentMinLength = eltValue;
				currentMaxLength = f.getMaxLength();
				break;
			case "maxLength":
				currentMinLength = String.valueOf(f.getMinLength());
				currentMaxLength = eltValue;
				break;
			}
		}

		if (type.equalsIgnoreCase("component")){
			c = p.getDatatypes().findOneComponent(id);

			switch(eltName){
			case "minLength":
				currentMinLength = eltValue;
				currentMaxLength = c.getMaxLength();
				break;
			case "maxLength":
				currentMinLength = String.valueOf(c.getMinLength());
				currentMaxLength = eltValue;
				break;
			}
		}

		String result = this.validateChangeLength(currentMinLength, currentMaxLength);
		ElementVerification ev = new ElementVerification(id, type);
		ElementVerificationResult evRst = new ElementVerificationResult(eltName, eltValue, result);
		ev.addElementVerifications(evRst);
		return ev;
	}

	public InputStream verifyLength2(Profile p, String id, String type, String eltName, String eltValue){
		//type is either Field or Component
		//eltName is minLength or maxLength
		String currentMinLength = "";
		String currentMaxLength = "";
		Field f; Component c;
		if (type.equalsIgnoreCase("field")){
			f = p.getSegments().findOneField(id);
			switch(eltName){
			case "minLength":
				currentMinLength = eltValue;
				currentMaxLength = f.getMaxLength();
				break;
			case "maxLength":
				currentMinLength = String.valueOf(f.getMinLength());
				currentMaxLength = eltValue;
				break;
			}
		}

		if (type.equalsIgnoreCase("component")){
			c = p.getDatatypes().findOneComponent(id);

			switch(eltName){
			case "minLength":
				currentMinLength = eltValue;
				currentMaxLength = c.getMaxLength();
				break;
			case "maxLength":
				currentMinLength = String.valueOf(c.getMinLength());
				currentMaxLength = eltValue;
				break;
			}
		}

		String result = this.validateChangeLength(currentMinLength, currentMaxLength);
		return this.generateOneJsonResult(id, type, eltName, eltValue, result);
	}


	public HashSet<String> duplicates (String[] values) {
		//Return hashset with duplicated values
		HashSet<String> set = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>(); 
        for (String valueElement : values)
        {
            if(!set.add(valueElement))
            {
            	if (!set.add(valueElement))
            		set2.add(valueElement);
            }
        }
        return set2;
	}

	private InputStream generateOneJsonResult(String id, String type, String eltName, String eltValue, String result){

		try {
			//Create temporary file
//			File tmpJsonFile = File.createTempFile("resultTmp", ".json"); FIXME
			File tmpJsonFile = new File("/Users/marieros/git/igamt_github4/igamt-lite-service/src/test/java/gov/nist/healthcare/tools/hl7/v2/igamt/lite/service/test/resultTmp.json");

			//Generate json file
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createGenerator(new FileWriter(tmpJsonFile));
			generator.setPrettyPrinter(new DefaultPrettyPrinter());		
			this.writeOneJsonResult(generator, id, type, eltName, eltValue, result);
			generator.close();

			return FileUtils.openInputStream(tmpJsonFile);
		} catch (IOException e) {
			return new NullInputStream(1L);
		} 

	}


	private void writeOneJsonResult(JsonGenerator generator, String id, String type, String eltName, String eltValue, String result) throws IOException{

		generator.writeStartObject(); //Start root object

		generator.writeStringField("id", id);
		generator.writeStringField("type", type);
		generator.writeStringField("eltName", eltName);
		generator.writeStringField("eltValue", eltValue);
		generator.writeStringField("result", result);

		generator.writeEndObject(); //closing root object

	}


	private String validateChangeLength(String currentMinLength, String currentMaxLength){
		String message = "";
		// Lengths have to be positive integer or star
		if (!NumberUtils.isNumber(currentMaxLength)) {
			if (!StringUtils.equalsIgnoreCase(currentMaxLength, "*")) {
				message += "Max length has to be * or a numerical value.\n";
			}
			message += "Max length has to be a numerical value.\n";
		}
		if (!NumberUtils.isNumber(currentMinLength)) {
			message += "Min length has to be a numerical value.\n";
		}

		// Length have to be positive integers
		int toBeMax = -1;
		if ("*".equalsIgnoreCase(currentMaxLength)) {
			toBeMax = Integer.MAX_VALUE;
		} else {
			toBeMax = Integer.valueOf(currentMaxLength);
		}

		int toBeMin = -1;
		if (!NumberUtils.isNumber(currentMinLength)) {
			toBeMin = Integer.valueOf(currentMinLength);
		}
		if (!(toBeMin >= 0 )){
			message += "Min length has to be positive integer.\n";
		}
		if (!(toBeMax >= 0 )){
			message += "Max length has to be positive integer.\n";
		}

		// Max length has to be greater than min length
		if (!(toBeMin <= toBeMax)){
			message += "Max length has to be greater than Min length.";
		}
		if (message.isEmpty())
			return "ok";
		return message;
	}



	private String validateChangeCardinality(String currentMin, String currentMax,
			Usage currentUsage) {
		String message = "";
		message = this.isValidCardinality(currentMin, currentMax);
		if (message.isEmpty())
			message = this.allowedChangesCardinality(Integer.valueOf(currentMin), currentMax, currentUsage);
		if (message.isEmpty())
		{
			return "ok";
		}
		return message;
	}


	private String isValidCardinality(String currentMin, String currentMax){
		//Type check
		String message = "";
		// Cardinality has to be positive integer or star
		if (!NumberUtils.isNumber(currentMax)) {
			if (!StringUtils.equalsIgnoreCase(currentMax, "*")) {
				message += "Cardinality Max has to be * or a numerical value.\n";
			}
			message += "Cardinality Max has to be a numerical value.\n";
		}
		if (!NumberUtils.isNumber(currentMin)) {
			message += "Card Min has to be a numerical value.\n";
		}

		int toBeMax = -1;
		if ("*".equalsIgnoreCase(currentMax)) {
			toBeMax = Integer.MAX_VALUE;
		} else {
			toBeMax = Integer.valueOf(currentMax);
		}
		int toBeMin = -1;
		if (!NumberUtils.isNumber(currentMin)) {
			toBeMin = Integer.valueOf(currentMin);
		}

		if (!(toBeMin >= 0 )){
			message += "Cardinality Min has to be positive integer.\n";
		}
		if (!(toBeMax >= 0 )){
			message += "Cardinality Max has to be positive integer.\n";
		}

		// Cardinality max has to be greater than cardinality min
		if (!(toBeMin <= toBeMax)){
			message += "Cardinality Max has to be greater than Cardinality Min.";
		}
		return message;
	}

	private String allowedChangesCardinality(int currentMin, String currentMax,
			Usage currentUsage){
		String message = "";
		//card vs usage
		int toBeMax = 0;
		if ("*".equalsIgnoreCase(currentMax)) {
			toBeMax = Integer.MAX_VALUE;
		} else {
			toBeMax = Integer.valueOf(currentMax);
		}

		// if X usage, then cardinality min and max have to be 0
		if (currentUsage.value().equalsIgnoreCase(Usage.X.value())) {
			if (currentMin != 0 || toBeMax != 0) {
				message += "Cardinality Min and Max must be 0 when Usage is: X.\n";
			}
		}

		// if R usage, then cardinality min has to be greater than zero 
		if (currentUsage.value().equalsIgnoreCase(Usage.R.value())) {
			if (!(currentMin > 0)) {
				message += "Cardinality Min can not be less than 1 when Usage is: R.\n";
			}
			if (!(toBeMax > 0)) {
				message += "Cardinality Max can not be less than 1 when Usage is: R.\n";
			}
		}

		// if RE usage, then cardinality min has to be greater than zero 
		if (currentUsage.value().equalsIgnoreCase(Usage.RE.value())) {
			if (!(currentMin >= 0)) {
				message += "Cardinality Min must be 0 when Usage is: RE.\n";
			}
		}

		// if C,CE,O, or C(a/b) usage, min must be zero
		// and cardinality max has to be greater than 1
		if (currentUsage.value().equalsIgnoreCase(Usage.O.value())
				|| currentUsage.value().equalsIgnoreCase(Usage.C.value())
				|| currentUsage.value().equalsIgnoreCase(Usage.CE.value())
				|| this.isConditionalUsage(currentUsage)) {

			if (!(currentMin == 0) && !(toBeMax >= 1)) {
				message += "Cardinality Min must be 0 when Usage is: "
						+ currentUsage.value() + ".\n";
			}
		}
		return message;
	}


	private String validateChangeUsage(String hl7Version, Usage referenceUsage, Usage currentUsage) {
		try {
			if (!this.validUsages(hl7Version).contains(currentUsage.value())){
				return "Invalid usage value.";
			} else if (!this.allowedChangesUsage(hl7Version, referenceUsage.value()).contains(currentUsage.value())){
				return "Selected usage of "
						+ currentUsage.value()
						+ " is non-compatible with base usage "
						+ referenceUsage.value();
			}
		}
		catch (IOException e){
			return "Rules file not found";
		}
		return "ok";
	}


	private List<String> validUsages(String version) throws IOException{
		JsonFactory factory = new JsonFactory(); 
		ObjectMapper mapper = new ObjectMapper(factory); 

		String jsonRules = IOUtils.toString(this.getClass().getResourceAsStream(
				"/validation/rulesIGL.txt"));
		ByteArrayInputStream from = new ByteArrayInputStream(jsonRules.getBytes("UTF-8")); 

		TypeReference<HashMap<String,Object>> typeRef 
		= new TypeReference<HashMap<String,Object>>() {};

		HashMap<String,Object> o = mapper.readValue(from, typeRef); 

		@SuppressWarnings("unchecked")
		HashMap<String, List<String>> hashMap = ((HashMap<String, List<String>>) o.get(version));
		return hashMap.get("usageList");

	}

	private List<String> allowedChangesUsage(String version, String from) throws IOException{
		JsonFactory factory = new JsonFactory(); 
		ObjectMapper mapper = new ObjectMapper(factory); 

		String jsonRules = IOUtils.toString(this.getClass().getResourceAsStream(
				"/validation/rulesIGL.txt"));
		ByteArrayInputStream rules = new ByteArrayInputStream(jsonRules.getBytes("UTF-8")); 

		TypeReference<HashMap<String,Object>> typeRef 
		= new TypeReference<HashMap<String,Object>>() {};

		HashMap<String,Object> o = mapper.readValue(rules, typeRef);

		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, List<String>>> hashMap = ((HashMap<String, HashMap<String, List<String>>>) o.get(version));
		return hashMap.get("usageMap").get(from);
	}

	private boolean isConditionalUsage(Usage usage) {
		return usage.value().contains("C(");
	}

	public static void main(String[] args) throws IOException {
		VerificationService ev = new VerificationService();
//		System.out.println(ev.allowedChangesUsage("2.7.1", "RE").contains("R"));
		System.out.println(ev.allowedChangesUsage("2.7.1", "RE").toString());

	}



}
