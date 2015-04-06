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

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConstraintType;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.tables.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileNotFoundException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.clone.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.CodeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.FieldService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.GroupService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentRefService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.hibernate.mapping.Collection;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private MessageService messageService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private SegmentRefService segmentRefService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private ComponentService componentService;

	@Autowired
	private FieldService fieldService;

	@Autowired
	private TableService tableService;

	@Autowired
	private CodeService codeService;

	@Override
	@Transactional
	public Profile save(Profile p) {
		return profileRepository.save(p);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		profileRepository.delete(id);
	}

	@Override
	public Profile findOne(Long id) {
		Profile profile = profileRepository.findOne(id);
		setDatatypeReferences(profile);
		return profile;
	}

	public Profile setDatatypeReferences(Profile profile) {
		for (Segment s : profile.getSegments().getChildren()) {
			setDatatypeReferences(s, profile.getDatatypes());
		}
		for (Datatype d : profile.getDatatypes().getChildren()) {
			setDatatypeReferences(d, profile.getDatatypes());
		}
		return profile;
	}

	private void setDatatypeReferences(Segment segment, Datatypes datatypes) {
		for (Field f : segment.getFields()) {
			f.setDatatype(datatypes.find(f.getDatatypeLabel()));
		}
	}

	private void setDatatypeReferences(Datatype datatype, Datatypes datatypes) {
		if (datatype != null && datatype.getComponents() != null) {
			for (Component c : datatype.getComponents()) {
				c.setDatatype(datatypes.find(c.getDatatypeLabel()));
			}
		}
	}

	@Override
	public List<Profile> findAllPreloaded() {
		List<Profile> profiles = profileRepository.findAllPreloaded();
		for (Profile profile : profiles) {
			setDatatypeReferences(profile);
		}
		return profiles;

	}

	@Override
	public List<Profile> findAllCustom() {
		List<Profile> profiles = profileRepository.findAllCustom();
		for (Profile profile : profiles) {
			setDatatypeReferences(profile);
		}
		return profiles;
	}

	@Override
	public Profile clone(Profile p) {
		return new ProfileClone().clone(p);
	}

	/*
	 * { "component": { "59": { "usage": "C" }, "303": { "maxLength": "27" } } }
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> apply(String jsonChanges)
			throws ProfileNotFoundException {
		List<String> errorList = new ArrayList<String>();

		try {
			Long id;
			Iterator<Entry<String, JsonNode>> nodes;
			Entry<String, JsonNode> node;
			JsonNode individualChanges;
			Entry<String, JsonNode> newValue;
			Iterator<Entry<String, JsonNode>> newValues;

			JsonFactory f = new JsonFactory();
			JsonParser jp = f.createJsonParser(jsonChanges);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(jp);

			// profile
			nodes = rootNode.path("profile").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();
				Profile p = findOne(id);
				if (p == null) {
					errorList.add("profile ID not found: " + node.getKey());
				} else {
					//FIXME
					//Now: all changes are saved; 
					//Todo: "unsave" changes that could not be saved
					//FIXME 2
					//changes is initialized with a dummy value {"0":0}

					if (p.getChanges() == null){
						p.setChanges(new String("{\"0\":0}"));
					}
					JsonNode currentNode = mapper.readTree(f.createJsonParser(p
							.getChanges()));
					JsonNode newNode = mapper.readTree(f
							.createJsonParser(jsonChanges));
					JsonNode updated = merge(currentNode, newNode);
					p.setChanges(updated.toString());

					BeanWrapper metadata = new BeanWrapperImpl(p.getMetaData());
					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							metadata.setPropertyValue(newValue.getKey(),
									newValue.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String(
									"profile property not set: "
											+ newValue.getKey()
											+ newValue.getValue()
											.getTextValue()));
						}
					}
					profileRepository.save(p);
				}
			}

			// message
			nodes = rootNode.path("message").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Message m = messageService.findOne(id);
				if (m == null) {
					errorList.add("message ID not found: " + node.getKey());
				} else {
					BeanWrapper message = new BeanWrapperImpl(m);
					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							message.setPropertyValue(newValue.getKey(),
									newValue.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String(
									"message property not set: "
											+ newValue.getKey()
											+ newValue.getValue()
											.getTextValue()));
						}
					}
					messageService.save(m);
				}
			}

			// segment
			nodes = rootNode.path("segment").getFields();

			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Segment s = segmentService.findOne(id);

				if (s == null) {
					errorList.add("segment ID not found: " + node.getKey());
				} else {
					BeanWrapper segment = new BeanWrapperImpl(s);

					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							segment.setPropertyValue(newValue.getKey(),
									newValue.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String(
									"Segment property not set: "
											+ newValue.getKey()
											+ newValue.getValue()
											.getTextValue()));
						}
					}
					segmentService.save(s);
				}
			}

			// segmentRef
			nodes = rootNode.path("segmentRef").getFields();

			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				SegmentRef sr = segmentRefService.findOne(id);

				if (sr == null) {
					errorList.add("SegmentRef ID not found: " + node.getKey());
				} else {
					BeanWrapper segmentRef = new BeanWrapperImpl(sr);

					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							segmentRef.setPropertyValue(newValue.getKey(),
									newValue.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String(
									"SegmentRef property not set: "
											+ newValue.getKey()
											+ newValue.getValue()
											.getTextValue()));
						}
					}
					segmentRefService.save(sr);
				}
			}

			// group
			nodes = rootNode.path("group").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				// Group has a String id; node.getKey() is used directly
				individualChanges = node.getValue();
				id = Long.valueOf(node.getKey());
				Group g = groupService.findOne(id);
				if (g == null) {
					errorList.add("Group ID not found: " + node.getKey());
				} else {
					BeanWrapper group = new BeanWrapperImpl(g);
					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							group.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String("group property not set: "
									+ newValue.getKey()
									+ newValue.getValue().getTextValue()));
						}
					}
					groupService.save(g);
				}
			}

			// component
			nodes = rootNode.path("component").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Component c = componentService.findOne(id);
				if (c == null) {
					errorList.add("Component ID not found: " + node.getKey());
				} else {
					BeanWrapper component = new BeanWrapperImpl(c);
					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							component.setPropertyValue(newValue.getKey(),
									newValue.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String(
									"Component property not set: "
											+ newValue.getKey()
											+ newValue.getValue()
											.getTextValue()));
						}
					}
					componentService.save(c);
				}
			}

			// field
			nodes = rootNode.path("field").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Field f1 = fieldService.findOne(id);
				BeanWrapper field = new BeanWrapperImpl(f1);

				newValues = individualChanges.getFields();
				while (newValues.hasNext()) {
					newValue = newValues.next();
					try {
						field.setPropertyValue(newValue.getKey(), newValue
								.getValue().getTextValue());
					} catch (NotWritablePropertyException e) {
						errorList.add(new String("profile property not set: "
								+ newValue.getKey()
								+ newValue.getValue().getTextValue()));
					}
				}
				fieldService.save(f1);
			}

			// table
			nodes = rootNode.path("table").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Table t = tableService.findOne(id);
				BeanWrapper code = new BeanWrapperImpl(t);
				newValues = individualChanges.getFields();
				while (newValues.hasNext()) {
					newValue = newValues.next();
					try {
						code.setPropertyValue(newValue.getKey(), newValue
								.getValue().getTextValue());
					} catch (NotWritablePropertyException e) {
						errorList.add(new String("table property not set: "
								+ newValue.getKey()
								+ newValue.getValue().getTextValue()));
					}
				}
				tableService.save(t);
			}

			// code
			nodes = rootNode.path("code").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = Long.valueOf(node.getKey());
				individualChanges = node.getValue();

				Code c1 = codeService.findOne(id);
				if (c1 == null) {
					errorList.add("Code ID not found: " + node.getKey());
				} else {
					BeanWrapper code = new BeanWrapperImpl(c1);
					newValues = individualChanges.getFields();
					while (newValues.hasNext()) {
						newValue = newValues.next();
						try {
							code.setPropertyValue(newValue.getKey(), newValue
									.getValue().getTextValue());
						} catch (NotWritablePropertyException e) {
							errorList.add(new String("code property not set: "
									+ newValue.getKey()
									+ newValue.getValue().getTextValue()));
						}
					}
					codeService.save(c1);
				}
			}
		} catch (IOException e) {

		}
		return errorList;
	}

	public JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
		Iterator<String> fieldNames = updateNode.getFieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			// if field exists and is an embedded object
			if (jsonNode != null && jsonNode.isObject()) {
				merge(jsonNode, updateNode.get(fieldName));
			} else {
				if (mainNode instanceof ObjectNode) {
					// Overwrite field
					JsonNode value = updateNode.get(fieldName);
					((ObjectNode) mainNode).put(fieldName, value);
				}
			}
		}
		return mainNode;
	}


	@Override
	public InputStream exportAsXml(Long targetId) {
		Profile p = clone(findOne(targetId));
		if (p != null) {
			return IOUtils.toInputStream(new ProfileSerializationImpl()
			.serializeProfileToXML(p));
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsXlsx(Long targetId){
		List<String> header;
		PdfPTable table;
		List <String> cells;

		try {
			//Look for the profile
			Profile p = findOne(targetId);
			//File tmpxslxFile = File.createTempFile("ProfileTmp", ".xslx");
			File tmpxslxFile = new File("/Users/marieros/Documents/testXslt/profile.xlsx");

			//Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();

			for (Message m : p.getMessages().getChildren()){
				//Create a blank sheet
				XSSFSheet sheet;
				sheet = workbook.createSheet(m.getStructID() + " Segment Usage");

				int headerSize = 8;

				//This data needs to be written (Object[])
				Map<String, Object[]> data = new TreeMap<String, Object[]>();

				List<List<String>> rows = new ArrayList<List<String>>();

				rows.add(Arrays.asList("SEGMENT", "CDC Usage", "Local Usage", 
						"Local Usage Constraint", "CDC Cardinality", "Local Cardinality", 
						"Local Cardinality Constraint", "Local Comments"));


				for (SegmentRefOrGroup srog : m.getSegmentRefOrGroups()) {
					if (srog instanceof SegmentRef) {
						this.addSegmentXlsx(rows, (SegmentRef) srog, 0);
					} else if (srog instanceof Group) {
						this.addGroupXlsx(rows, (Group) srog, 0);	
					} 
				}
				for (List<String> row: rows){

					Object[] tmp = new Object[headerSize];
					for (String elt : row){
						tmp[row.indexOf(elt)] = elt;
					}
					//TODO Remove print
					//System.out.println(String.valueOf(rows.indexOf(row)) + " " + row.toString());
					//data.put(String.valueOf(rows.indexOf(row)), tmp);
					data.put(String.format("%06d", rows.indexOf(row)), tmp);
				}

				//Iterate over data and write to sheet
				Set<String> keyset = data.keySet();
				keyset = new TreeSet<String>(keyset);

				int rownum = 0;
				for (String key : keyset)
				{
					Row row = sheet.createRow(rownum++);
					Object [] objArr = data.get(key);
					int cellnum = 0;
					for (Object obj : objArr)
					{
						Cell cell = row.createCell(cellnum++);
						if(obj instanceof String)
							cell.setCellValue((String)obj);
						else if(obj instanceof Integer)
							cell.setCellValue((Integer)obj);
					}
				}
				FileOutputStream out = new FileOutputStream(tmpxslxFile);
				workbook.write(out);
				workbook.close();
				out.close();
			}
			return new NullInputStream(1L);
		}catch (Exception e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}


	public InputStream exportAsPdfFromXSL(Long targetId){
		try {
			//Look for the profile
			Profile p = findOne(targetId);

			//Generate xml file containing profile
			File tmpXmlFile = File.createTempFile("ProfileTemp", ".xml");
			String stringProfile = new ProfileSerializationImpl().serializeProfileToXML(p);
			FileUtils.writeStringToFile(tmpXmlFile, stringProfile, Charset.forName("UTF-8"));

			//Apply XSL transformation on xml file to generate html
			Source text = new StreamSource(tmpXmlFile);
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource( this.getClass().getResourceAsStream("/rendering/profile.xsl"));
			Transformer transformer;
			transformer = factory.newTransformer(xslt);
			File tmpHtmlFile = File.createTempFile("ProfileTemp", ".html");
			transformer.transform(text, new StreamResult(tmpHtmlFile));

			//Convert html document to pdf
			Document document = new Document();
			File tmpPdfFile = File.createTempFile("ProfileTemp", ".pdf");
			PdfWriter writer = PdfWriter.getInstance(document, FileUtils.openOutputStream(tmpPdfFile));
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, 
					FileUtils.openInputStream(tmpHtmlFile));
			document.close();
			return FileUtils.openInputStream(tmpPdfFile);
		}
		catch(IOException | TransformerException | DocumentException e){
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsPdf(Long targetId){

		try {
			//Look for the profile
			Profile p = findOne(targetId);

			//Create fonts and colors to be used in generated pdf
			BaseColor headerColor = WebColors.getRGBColor("#0033CC");
			BaseColor cpColor = WebColors.getRGBColor("#C0C0C0");
			Font titleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.UNDERLINE | Font.BOLD, BaseColor.RED);
			Font headerFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL, BaseColor.WHITE);
			Font cellFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL, BaseColor.BLACK);

			List<String> header;
			PdfPTable table;
			float columnWidths[];
			List<List<String>> rows;				

			//File tmpPdfFile = File.createTempFile("ProfileTmp", ".pdf");
			File tmpPdfFile = new File("/Users/marieros/Documents/testXslt/profile.pdf");

			Document document1 = new Document();
			PdfWriter writer1 = PdfWriter.getInstance(document1, FileUtils.openOutputStream(tmpPdfFile));

			document1.setPageSize(PageSize.A4);
			document1.setMargins(15f, 15f, 36f, 36f);
			//72pt = 1 inch

			document1.open();

			document1.add(new Paragraph("Messages definition", titleFont));
			document1.add( Chunk.NEWLINE );

			for (Message m : p.getMessages().getChildren()){
				document1.add(new Paragraph("Message definition: "));
				document1.add(new Paragraph(m.getStructID() + " - " + m.getDescription()));
				document1.add( Chunk.NEWLINE );

				header = Arrays.asList("Segment" ,"STD\nUsage", "Local\nUsage", "STD\nCard.", "Local\nCard." , "Comment");
				columnWidths = new float[] {4f, 3f, 3f, 2f, 2f, 8f};
				table = this.createHeader(header, columnWidths, headerFont, headerColor);

				rows = new ArrayList<List<String>>();
				
				List<SegmentRefOrGroup> segRefOrGroups = new ArrayList<>(m.getSegmentRefOrGroups());
				Collections.sort(segRefOrGroups);

				for (SegmentRefOrGroup srog : segRefOrGroups) {
					if (srog instanceof SegmentRef) {
						this.addSegmentPdf(rows, (SegmentRef) srog, 0);
					} else if (srog instanceof Group) {
						this.addGroupPdf1(rows, (Group) srog, 0);
					} 
				}
				this.addCells(table, rows, cellFont, cpColor);
				document1.add(table);

			}

			for (Message m : p.getMessages().getChildren()){
				document1.newPage();

				document1.add(new Paragraph("Segments definition", titleFont));
				document1.add( Chunk.NEWLINE );

				header = Arrays.asList("Seq" , "Element Name" ,"DT" , "STD\nUsage", "Local\nUsage", "Std\nCard.", "Local\nCard." , 
						"Len" , "Value\nSet", "Comment");
				columnWidths = new float[] {2f, 3f, 2f, 1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2f, 6f};

				for (SegmentRefOrGroup srog : m.getSegmentRefOrGroups()) {
					table = this.createHeader(header, columnWidths, headerFont, headerColor);
					rows = new ArrayList<List<String>>();
					if (srog instanceof SegmentRef) {
						Segment s = ((SegmentRef) srog).getRef();
						document1.add(new Paragraph(s.getDescription() + " (" + s.getName() + ")"));
						document1.add( Chunk.NEWLINE );
						this.addFieldPdf2(rows, s);
						this.addCells(table, rows, cellFont, cpColor);
						document1.add(table);
						document1.newPage();

					} else if (srog instanceof Group) {
						this.addGroupPdf2(document1, header, columnWidths, (Group) srog, headerFont, headerColor, cellFont, cpColor);
					} 
				}
			}


			document1.add(new Paragraph("Datatypes", titleFont));
			document1.add( Chunk.NEWLINE );

			header = Arrays.asList("Seq" , "Element Name" ,"Conf length" , "DT", "Usage", 
					"Len" , "Table", "Comment");
			columnWidths = new float[] {2f, 3f, 2f, 1.5f, 1.5f, 2f, 2f, 6f};


			for (Datatype d : p.getDatatypes().getChildren()) {
				document1.add(new Paragraph(d.getName() + " - " + d.getDescription()));
				document1.add(new Paragraph(d.getComment()));

				table = this.createHeader(header, columnWidths, headerFont, headerColor);
				rows = new ArrayList<List<String>>();
				this.addComponentPdf2(rows, d);
				this.addCells(table, rows, cellFont, cpColor);
				document1.add(Chunk.NEWLINE);
				document1.add(table);
				document1.add(Chunk.NEXTPAGE);
			}

			document1.close();
			return FileUtils.openInputStream(tmpPdfFile);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}


	private PdfPTable createHeader(List<String> headers, float[] columnWidths, Font headerFont, BaseColor headerColor){
		PdfPTable table = new PdfPTable(headers.size());
		PdfPCell c1;
		for (String cellName : headers){
			c1 = new PdfPCell(new Phrase(cellName, headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			c1.setBackgroundColor(headerColor);
			table.addCell(c1);
		}
		try {
			if (columnWidths.length != 0){
				table.setWidths(columnWidths);
			}
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		return table;
	}


	private void addCells(PdfPTable table, List<List<String>> rows, Font cellFont, BaseColor cpColor){
		for (List<String> cells: rows){
			if (cells.size() != 3){
				for (String cell: cells){
					table.addCell(new Phrase(cell, cellFont));
				}
			} else {
				PdfPCell cell;
				cell = new PdfPCell(new Phrase(cells.get(0), cellFont));
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(cells.get(1), cellFont));
				cell.setColspan(2);
				cell.setBackgroundColor(cpColor);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(cells.get(2), cellFont));
				cell.setColspan(7);
				table.addCell(cell);
			}
		}
	}

	private void addGroupPdf1(List<List<String>> rows, Group g, Integer depth){
		String indent = StringUtils.repeat(" ", 2 * depth);

		List <String> row = Arrays.asList(
				indent + "[", g.getUsage().value(), "", 
				"[" + String.valueOf(g.getMin()) + ".." + String.valueOf(g.getMax()) + "]",
				"", "BEGIN " + g.getName() + " GROUP");
		rows.add(row);

		List<SegmentRefOrGroup> segsOrGroups = new ArrayList<>(g.getSegmentsOrGroups());
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentPdf(rows, (SegmentRef) srog, depth + 1);
			} else if (srog instanceof Group) {
				this.addGroupPdf1(rows, (Group) srog, depth + 1);	
			}
		}

		row = Arrays.asList(indent + "]", "", "", "", "", "END " + g.getName() + " GROUP");
		rows.add(row);		
	}


	private void addGroupPdf2(Document document, List<String> header, float[] columnWidths, Group g, Font headerFont, 
			BaseColor headerColor, Font cellFont, BaseColor cpColor) throws DocumentException{
		PdfPTable table;
		List<List<String>> rows;

		List<SegmentRefOrGroup> segsOrGroups = new ArrayList<>(g.getSegmentsOrGroups());
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				table = this.createHeader(header, columnWidths, headerFont, headerColor);
				rows = new ArrayList<List<String>>();
				this.addFieldPdf2(rows, ((SegmentRef) srog).getRef());
				this.addCells(table, rows, cellFont, cpColor);
				document.add(table);
				document.newPage();
			} else if (srog instanceof Group) {
				this.addGroupPdf2(document, header, columnWidths, (Group) srog, headerFont, 
						headerColor, cellFont, cpColor);	
			}
		}
	}


	private void addGroupXlsx(List<List<String>> rows, Group g, Integer depth){
		String indent = StringUtils.repeat(" ", 4 * depth);

		List <String> row = Arrays.asList(
				indent + "BEGIN " + g.getName() + " GROUP", "", g.getUsage().value(), "",
				"", "[" + String.valueOf(g.getMin()) + ".." + String.valueOf(g.getMax()) + "]",
				"", "");
		rows.add(row);
		List<SegmentRefOrGroup> segsOrGroups = new ArrayList<>(g.getSegmentsOrGroups());
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentXlsx(rows, (SegmentRef) srog, depth + 1);
			} else if (srog instanceof Group) {
				this.addGroupXlsx(rows, (Group) srog, depth + 1);						
			}
		}
		row = Arrays.asList(
				indent + "END " + g.getName() + " GROUP", "", "", "", "", "");
		rows.add(row);
	}

	private void addSegmentPdf(List<List<String>> rows, SegmentRef s, Integer depth){
		String indent = StringUtils.repeat(" ", 4 * depth);

		List <String> row = Arrays.asList(indent + s.getRef().getName(), s.getUsage().value(), 
				"", "[" + String.valueOf(s.getMin()) + ".." + String.valueOf(s.getMax()) + "]", 
				"", (String)(s.getRef().getComment() == null ? "" : s.getRef().getComment()));
		rows.add(row);
	}


	private void addSegmentXlsx(List<List<String>> rows, SegmentRef s, Integer depth){
		String indent = StringUtils.repeat(" ", 4 * depth);

		List <String> row = Arrays.asList(indent + s.getRef().getName(), "", s.getUsage().value(), 
				"", "", "[" + String.valueOf(s.getMin()) + ".." + String.valueOf(s.getMax()) + "]", 
				"", (String)(s.getRef().getComment() == null ? "" : s.getRef().getComment()));
		rows.add(row);
	}


	private List<Constraint> findConstraints(Integer target, Set<Predicate> predicates, Set<ConformanceStatement> conformanceStatements){
		List<Constraint> constraints = new ArrayList<>();
		for (Predicate pre: predicates){
			if (target == Integer.parseInt(pre.getConstraintTarget().substring(0, pre.getConstraintTarget().indexOf('[')))){
				constraints.add(pre);
			}
		}
		for (ConformanceStatement conformanceStatement: conformanceStatements){
			if (target == Integer.parseInt(
					conformanceStatement.getConstraintTarget().substring(
							0, conformanceStatement.getConstraintTarget().indexOf('[')))){
				constraints.add(conformanceStatement);
			}
		}
		return constraints;
	}

	private void addComponentPdf2(List<List<String>> rows, Datatype d){
		List <String> row;
		//System.out.println(StringUtils.repeat("%",25));

		Set<Predicate> predicates = d.getPredicates();
		Set<ConformanceStatement> conformanceStatements = d.getConformanceStatements();
		
		List<Component> componentsList = new ArrayList<>(d.getComponents());
		Collections.sort(componentsList);
		for (Component c : componentsList){
			row = Arrays.asList(c.getPosition().toString(), c.getName(), c.getConfLength(), 
					c.getDatatypeLabel(), c.getUsage().value(),
					"[" + String.valueOf(c.getMinLength()) + "," +String.valueOf(c.getMaxLength()) + "]",
					(String)((c.getTable() == null) ? "" : c.getTable().getMappingId()), c.getComment() );
			rows.add(row);

			List<Constraint> constraints = this.findConstraints(componentsList.indexOf(c) + 1, predicates, conformanceStatements);
			if (!constraints.isEmpty()){

				for (Constraint constraint : constraints){
					String constraintType = new String();
					if (constraint instanceof Predicate)	{
						constraintType = "Condition Predicate";
					} else if (constraint instanceof ConformanceStatement)	{
						constraintType =  "Conformance Statement";
					}
					row = Arrays.asList(constraint.getConstraintId(), constraintType, constraint.getDescription());
					rows.add(row);
				}
			}
		}
	}

	private void addFieldPdf2(List<List<String>> rows, Segment s){
		//System.out.println(StringUtils.repeat("%",25));
		//System.out.println("Segment id: " + s.getId().toString());
		List <String> row;
		Set<Predicate> predicates = s.getPredicates();
		Set<ConformanceStatement> conformanceStatements = s.getConformanceStatements();
		
		List<Field> fieldsList = new ArrayList<>(s.getFields());
		Collections.sort(fieldsList);
		for (Field f : fieldsList){
			row = Arrays.asList(f.getItemNo().replaceFirst("^0+(?!$)", ""), f.getName(), 
					f.getDatatypeLabel(), f.getUsage().value(), "", "[" + String.valueOf(f.getMin()) + ".." +String.valueOf(f.getMax()) + "]",
					"", "[" + String.valueOf(f.getMinLength()) + "," +String.valueOf(f.getMaxLength()) + "]",
					(String)((f.getTable() == null) ? "" : f.getTable().getMappingId()), f.getComment() );
			rows.add(row);

			List<Constraint> constraints = this.findConstraints(fieldsList.indexOf(f) + 1, predicates, conformanceStatements);
			if (!constraints.isEmpty()){

				for (Constraint constraint : constraints){
					String constraintType = new String();
					if (constraint instanceof Predicate)	{
						constraintType = "Condition Predicate";
					} else if (constraint instanceof ConformanceStatement)	{
						constraintType =  "Conformance Statement";
					}
					row = Arrays.asList(constraint.getConstraintId(), constraintType, constraint.getDescription());
					rows.add(row);
				}
			}
		}
	}

}