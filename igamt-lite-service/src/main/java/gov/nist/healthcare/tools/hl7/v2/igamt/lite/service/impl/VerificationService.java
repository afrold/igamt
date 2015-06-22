package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;


public class VerificationService {
	

	public InputStream verifySegment(Profile p, Profile baseP, String id, String type) {
		String result = "";
		Segment s = p.getSegments().findOne(id);

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



	public InputStream verifyDatatype(Profile p, Profile baseP, String id, String type) {
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

	public InputStream verifyValueSet(Profile p, Profile baseP, String id, String type) {
		// Type is ValueSet (or Table)
		String result = "";
		Table t = p.getTables().findOne(id);

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
						Usage.fromValue(baseP.getTables().findOneCode(id).getCodeUsage()), 
						Usage.fromValue(p.getTables().findOneCode(id).getCodeUsage()));
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


	public InputStream verifyUsage(Profile p, Profile baseP, String id, String type, String eltName, String eltValue){
		// Type can be Field, Component, Code
		// EltName is Usage
		String hl7Version = p.getMetaData().getHl7Version();
		Usage referenceUsage = Usage.R; 
		Usage currentUsage = Usage.R;

		switch(type){
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
			Code cd = p.getTables().findOneCode(id);
			currentUsage = Usage.fromValue(cd.getCodeUsage());
			Code basecd = baseP.getTables().findOneCode(id);
			referenceUsage = Usage.fromValue(basecd.getCodeUsage());
			break;
		}

		String result = this.validateChangeUsage(hl7Version, referenceUsage, currentUsage);
		return this.generateOneJsonResult(id, type, eltName, eltValue, result);
	}


	public InputStream verifyCardinality(Profile p, String id, String type, String eltName, String eltValue){
		//Type can be Field
		//EltName can be cardMin or cardMax

		Field f = p.getSegments().findOneField(id);

		String currentMin = (String) (eltName.equalsIgnoreCase("min") ? eltValue : f.getMin());
		String currentMax = (String) (eltName.equalsIgnoreCase("max") ? eltValue : f.getMax());
		Usage currentUsage = f.getUsage();

		String result = this.validateChangeCardinality(currentMin, currentMax, currentUsage);
		return this.generateOneJsonResult(id, type, eltName, eltValue, result);

	}

	public InputStream verifyLength(Profile p, String id, String type, String eltName, String eltValue){
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


	private InputStream generateOneJsonResult(String id, String type, String eltName, String eltValue, String result){

		try {
			//Create temporary file
			File tmpJsonFile = File.createTempFile("resultTmp", ".json");

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
		System.out.println(ev.allowedChangesUsage("2.7.1", "RE").contains("R"));



	}



}
