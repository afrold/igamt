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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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

	public InputStream exportAsPdf(Long targetId){

		try {
			//Look for the profile
			Profile p = findOne(targetId);

			/*
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
			System.out.println( "HTML Created!" );

			//Convert html document to pdf
			Document document = new Document();
			File tmpPdfFile = File.createTempFile("ProfileTemp", ".pdf");
			PdfWriter writer = PdfWriter.getInstance(document, FileUtils.openOutputStream(tmpPdfFile));
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, 
					FileUtils.openInputStream(tmpHtmlFile));
			document.close();
			System.out.println( "PDF Created!" );
			 */

			//Create fonts and colors to be used in generated pdf
			BaseColor headerColor = WebColors.getRGBColor("#0033CC");
			Font titleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.UNDERLINE | Font.BOLD, BaseColor.RED);
			Font headerFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL, BaseColor.WHITE);
			Font cellFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL, BaseColor.BLACK);

			File tmpPdfFile = File.createTempFile("ProfileTmp", ".pdf");
			Document document1 = new Document();
			PdfWriter writer1 = PdfWriter.getInstance(document1, FileUtils.openOutputStream(tmpPdfFile));
			document1.open();

			document1.add(new Paragraph("Message definition", titleFont));
			document1.add( Chunk.NEWLINE );

			List<String> header;
			PdfPTable table;
			List <String> cells;

			for (Message m : p.getMessages().getChildren()){
				document1.add(new Paragraph("Message definition: "));
				document1.add(new Paragraph(m.getStructID() + " - " + m.getDescription()));
				document1.add( Chunk.NEWLINE );

				header = Arrays.asList("Segment" ,"Name",  "Card.", "Local Card." , "Usage", 
						"Local Usage" , "Comment");
				table = this.createHeader(header, headerFont, headerColor);
				float[] columnWidths = {3f, 4f, 1.5f, 1.5f, 2f, 2f, 8f};
				table.setWidths(columnWidths);

				cells = new ArrayList<String>();
				for (SegmentRefOrGroup srog : m.getSegmentRefOrGroups()) {
					if (srog instanceof SegmentRef) {
						this.addSegment(cells, (SegmentRef) srog, 0);
					} else if (srog instanceof Group) {
						this.addGroup(cells, (Group) srog, 0);	
					} 
				}
				this.addCells(table, cells, cellFont);
				document1.add(table);


				/*
				document1.add(new Paragraph("Segments"));
				document1.add( Chunk.NEWLINE );

				header = Arrays.asList("SEQ" , "Field" , "Usage", "Cardi" , 
						"Length" , "ValueSet", "Conf Statement" , "Predicate" , "Comment");
				table = this.createHeader(header, headerFont, headerColor);
				for (Segment s : p.getSegments().getChildren()) {
					document1.add(new Paragraph(s.getName() + " - " + s.getDescription()));

					cells = new ArrayList<String>();
					for (Field f : s.getFields()) {
						cells.add(f.getItemNo());
						cells.add(f.getName());
						cells.add(f.getUsage().value());
						cells.add("[" + String.valueOf(f.getMin()) + ".." +String.valueOf(f.getMax()) + "]");
						cells.add("[" + String.valueOf(f.getMinLength()) + "," +String.valueOf(f.getMaxLength()) + "]");
						if (f.getTable() != null){
							cells.add(f.getTable().getMappingId());
						} else {
							cells.add("");
						}
						cells.add("");
						cells.add("");
						cells.add(f.getComment());
					}
					this.addCells(table, cells, cellFont);
					document1.add(table);
				}

				document1.add(new Paragraph("Datatypes"));
				document1.add( Chunk.NEWLINE );


				for (Datatype d : p.getDatatypes().getChildren()) {
					document1.add(new Paragraph(d.getName() + " - " + d.getDescription()));
					document1.add(new Paragraph(d.getComment()));

					header = Arrays.asList("Name", "Usage", "Length" , "Conf length", "Datatype" , 
							"ValueSet", "Conf Statement" , "Predicate" , "Comment");
					table = this.createHeader(header, headerFont, headerColor);

					cells = new ArrayList<String>();
					for (Component c: d.getComponents()){
						//FIXME
						//Add recursive calls on Components
						cells.add(c.getName());
						cells.add(c.getUsage().value());

						cells.add("[" + String.valueOf(c.getMinLength()) + ".." +String.valueOf(c.getMaxLength()) + "]");
						cells.add(c.getConfLength());
						cells.add(c.getDatatypeLabel());


						if (c.getTable() != null){
							cells.add(c.getTable().getMappingId());
						} else {
							cells.add("");
						}
						cells.add("");
						cells.add("");
						cells.add(c.getComment());
					}
					this.addCells(table, cells, cellFont);
					document1.add(table);
				}
				 */

			}

			document1.close();
			return FileUtils.openInputStream(tmpPdfFile);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	private void addCells(PdfPTable table, List<String> cells, Font cellFont){
		for (String cell: cells){
			table.addCell(new Phrase(cell, cellFont));
		}
	}

	private PdfPTable createHeader(List<String> headers, Font headerFont, BaseColor headerColor){
		PdfPTable table = new PdfPTable(headers.size());
		PdfPCell c1;
		for (String cellName : headers){
			c1 = new PdfPCell(new Phrase(cellName, headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			c1.setBackgroundColor(headerColor);
			table.addCell(c1);
		}
		return table;
	}

	private void addGroup(List<String> cells, Group g, Integer depth){
		String indent = StringUtils.repeat(" ", 2 * depth);
		cells.add(indent + "[");
		cells.add("[" + String.valueOf(g.getMin()) + ".." + String.valueOf(g.getMax()) + "]");
		cells.add("");
		cells.add(g.getUsage().value());
		cells.add("");
		cells.add("BEGIN " + g.getName() + " GROUP");

		for (SegmentRefOrGroup srog : g.getSegmentsOrGroups()) {
			if (srog instanceof SegmentRef) {
				this.addSegment(cells, (SegmentRef) srog, depth + 1);
			} else if (srog instanceof Group) {
				this.addGroup(cells, (Group) srog, depth + 1);						
			}
		}
		cells.add(indent + "]");
		cells.add("");
		cells.add("");
		cells.add("");
		cells.add("");
		cells.add("END " + g.getName() + " GROUP");
	}

	private void addSegment(List<String> cells, SegmentRef s, Integer depth){
		String indent = StringUtils.repeat(" ", 2 * depth);
		cells.add(indent + s.getRef().getName());
		cells.add("[" + String.valueOf(s.getMin()) + ".." + String.valueOf(s.getMax()) + "]");
		cells.add("");
		cells.add(s.getUsage().value());
		cells.add("");
		cells.add(s.getRef().getComment());

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

}