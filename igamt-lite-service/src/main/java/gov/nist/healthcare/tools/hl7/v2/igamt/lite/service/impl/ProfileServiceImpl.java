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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration.Main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.mongodb.MongoException;

@Service
public class ProfileServiceImpl extends PdfPageEventHelper implements ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private MessageService messageService;

	@Autowired
	private SegmentService segmentService;

	@Autowired
	private DatatypeService datatypeService;

	@Autowired
	private TableService tableService;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Profile save(Profile p) throws ProfileException {
		try {
			return profileRepository.save(p);
		} catch (MongoException e) {
			throw new ProfileException(e);
		}
	}

	// public Set<Datatype> findPrimitiveDatatypes(Datatypes datatypes) {
	// Set<Datatype> primitives = new HashSet<Datatype>();
	// for (Datatype datatype : datatypes.getChildren()) {
	// findPrimitiveDatatypes(datatype, primitives);
	// }
	// return primitives;
	// }
	//
	// public Set<Datatype> findPrimitiveDatatypes(Datatype datatype,
	// Set<Datatype> result) {
	// if (datatype.getComponents() == null
	// || datatype.getComponents().isEmpty()) {
	// result.add(datatype);
	// } else {
	// for (Component component : datatype.getComponents()) {
	// findPrimitiveDatatypes(component.getDatatype(), result);
	// }
	// }
	// return result;
	// }

	@Override
	@Transactional
	public void delete(String id) {
		profileRepository.delete(id);
	}

	@Override
	public Profile findOne(String id) {
		Profile profile = profileRepository.findOne(id);
		return profile;
	}

	// public Profile setDatatypeReferences(Profile profile) {
	// for (Segment s : profile.getSegments().getChildren()) {
	// setDatatypeReferences(s, profile.getDatatypes());
	// }
	// for (Datatype d : profile.getDatatypes().getChildren()) {
	// setDatatypeReferences(d, profile.getDatatypes());
	// }
	// return profile;
	// }
	//
	// private void setDatatypeReferences(Segment segment, Datatypes datatypes)
	// {
	// for (Field f : segment.getFields()) {
	// f.setDatatype(datatypes.find(f.getDatatypeLabel()));
	// }
	// }
	//
	// private void setDatatypeReferences(Datatype datatype, Datatypes
	// datatypes) {
	// if (datatype != null && datatype.getComponents() != null) {
	// for (Component c : datatype.getComponents()) {
	// c.setDatatype(datatypes.find(c.getDatatypeLabel()));
	// }
	// }
	// }

	@Override
	public List<Profile> findAllPreloaded() {
		List<Profile> profiles = profileRepository.findByPreloaded(true);
		return profiles;
	}

	// private void processChildren(Profile profile) {
	// List<Message> messages = messageService.findByMessagesId(profile
	// .getMessages().getId());
	// profile.getMessages().getChildren().addAll(messages);
	// }

	@Override
	public List<Profile> findAllCustom() {
		List<Profile> profiles = profileRepository.findByPreloaded(false);
		// if (profiles != null && !profiles.isEmpty()) {
		// for (Profile profile : profiles) {
		// processChildren(profile);
		// }
		// }
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
	public List<String> apply(String jsonChanges, Profile p) {
		List<String> errorList = new ArrayList<String>();
		try {
			String id;
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
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();
				if (p == null) {
					errorList.add("profile ID not found: " + node.getKey());
				} else {
					// FIXME
					// Now: all changes are saved;
					// Todo: "unsave" changes that could not be saved
					// FIXME 2
					// changes is initialized with a dummy value {"0":0}

					if (p.getChanges() == null) {
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
				}
			}

			// message
			nodes = rootNode.path("message").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				Message m = p.getMessages().findOne(id);
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
					// messageService.save(m);
				}
			}

			// segment
			nodes = rootNode.path("segment").getFields();

			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				Segment s = p.getSegments().findOne(id);

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
					// segmentService.save(s);
				}
			}

			// segmentRef
			nodes = rootNode.path("segmentRef").getFields();

			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				SegmentRef sr = (SegmentRef) p.getMessages()
						.findOneSegmentRefOrGroup(id);

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
					// segmentRefService.save(sr);
				}
			}

			// group
			nodes = rootNode.path("group").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				// Group has a String id; node.getKey() is used directly
				individualChanges = node.getValue();
				id = String.valueOf(node.getKey());
				Group g = (Group) p.getMessages().findOneSegmentRefOrGroup(id);
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
					// groupService.save(g);
				}
			}

			// component
			nodes = rootNode.path("component").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				Component c = p.getDatatypes().findOneComponent(id);
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
					// componentService.save(c);
				}
			}

			// field
			nodes = rootNode.path("field").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				Field f1 = p.getSegments().findOneField(id);
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
				// fieldService.save(f1);
			}

			// table
			nodes = rootNode.path("table").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				Table t = p.getTables().findOne(id);
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
				// tableService.save(t);
			}

			// code
			nodes = rootNode.path("code").getFields();
			while (nodes.hasNext()) {
				node = nodes.next();
				id = String.valueOf(node.getKey());
				individualChanges = node.getValue();

				Code c1 = p.getTables().findOneCode(id);
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
					// codeService.save(c1);
				}
			}

			profileRepository.save(p);

		} catch (Exception e) {

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
	public InputStream exportAsXml(Profile p) {
		if (p != null) {
			return IOUtils.toInputStream(new ProfileSerializationImpl()
			.serializeProfileToXML(p));
		} else {
			return new NullInputStream(1L);
		}
	}

	@Override
	public InputStream exportAsXlsx(Profile p) {
		try {
			File tmpXlsxFile = File.createTempFile("ProfileTmp", ".xslx");

			// Blank workbook
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet;
			XSSFCellStyle headerStyle;
			List<List<String>> rows;
			List<String> header;

			headerStyle = workbook.createCellStyle();
			headerStyle.setFillPattern(XSSFCellStyle.BORDER_THICK);
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			headerStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());

			for (Message m : p.getMessages().getChildren()) {
				// Create a blank sheet
				sheet = workbook
						.createSheet(m.getStructID() + " Segment Usage");

				rows = new ArrayList<List<String>>();

				header = Arrays.asList("SEGMENT", "CDC Usage", "Local Usage",
						"CDC Cardinality", "Local Cardinality", "Comments");
				rows.add(header);

				for (SegmentRefOrGroup srog : m.getChildren()) {
					if (srog instanceof SegmentRef) {
						this.addSegmentXlsx(rows, (SegmentRef) srog, 0);
					} else if (srog instanceof Group) {
						this.addGroupXlsx(rows, (Group) srog, 0);
					}
				}
				this.writeToSheet(rows, header, sheet, headerStyle);
			}

			for (Message m : p.getMessages().getChildren()) {
				rows = new ArrayList<List<String>>();
				header = Arrays.asList("Segment", "Name", "DT", "STD\nUsage", "Local\nUsage",
						"STD\nCard.", "Local\nCard.", "Len", "Value set", "Comment");

				for (SegmentRefOrGroup srog : m.getChildren()) {

					if (srog instanceof SegmentRef) {
						this.addSegmentXlsx2(((SegmentRef) srog).getRef(), header, workbook, headerStyle);
					} else if (srog instanceof Group) {
						this.addGroupXlsx2(header, (Group) srog, workbook, headerStyle);
					}
				}
			}

			FileOutputStream out = new FileOutputStream(tmpXlsxFile);
			workbook.write(out);
			workbook.close();
			out.close();

			return FileUtils.openInputStream(tmpXlsxFile);

		} catch (Exception e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	private void writeToSheet(List<List<String>> rows, List<String> header, XSSFSheet sheet, XSSFCellStyle headerStyle){
		//This data needs to be written (Object[])
		Map<String, Object[]> data = new TreeMap<String, Object[]>();
		for (List<String> row : rows) {
			Object[] tmp = new Object[header.size()];
			for (String elt : row) {
				tmp[row.indexOf(elt)] = elt;
			}
			data.put(String.format("%06d", rows.indexOf(row)), tmp);
		}

		// Iterate over data and write to sheet
		Set<String> keyset = data.keySet();
		keyset = new TreeSet<String>(keyset);

		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = (Cell) row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
				if (rownum == 1)
					cell.setCellStyle(headerStyle);
			}
		}
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(8);
	}

	public InputStream exportAsPdfFromXSL(Profile p) {
		try {
			// Generate xml file containing profile
			File tmpXmlFile = File.createTempFile("ProfileTemp", ".xml");
			String stringProfile = new ProfileSerializationImpl()
			.serializeProfileToXML(p);
			FileUtils.writeStringToFile(tmpXmlFile, stringProfile,
					Charset.forName("UTF-8"));

			// Apply XSL transformation on xml file to generate html
			Source text = new StreamSource(tmpXmlFile);
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(this.getClass().getResourceAsStream(
					"/rendering/profile.xsl"));
			Transformer transformer;
			transformer = factory.newTransformer(xslt);
			File tmpHtmlFile = File.createTempFile("ProfileTemp", ".html");
			transformer.transform(text, new StreamResult(tmpHtmlFile));

			// Convert html document to pdf
			Document document = new Document();
			File tmpPdfFile = File.createTempFile("ProfileTemp", ".pdf");
			PdfWriter writer = PdfWriter.getInstance(document,
					FileUtils.openOutputStream(tmpPdfFile));
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,
					FileUtils.openInputStream(tmpHtmlFile));
			document.close();
			return FileUtils.openInputStream(tmpPdfFile);
		} catch (IOException | TransformerException | DocumentException e) {
			return new NullInputStream(1L);
		}
	}

	private Document                 document;
	private PdfWriter                writer;
	private PdfWriter writer1;
	private Font                     chapterFont    = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.NORMAL);

	// table to store placeholder for all chapters and sections
	private Map<String, PdfTemplate> tocPlaceholder;

	// store the chapters and sections with their title here.
	private Map<String, Integer>     pageByTitle;

	@Override
	public void onChapter(final PdfWriter writer, final Document document, final float paragraphPosition, final Paragraph title)
	{
		this.pageByTitle.put(title.getContent(), writer.getPageNumber());
	}

	@Override
	public void onSection(final PdfWriter writer, final Document document, final float paragraphPosition, final int depth, final Paragraph title)
	{
		this.pageByTitle.put(title.getContent(), writer.getPageNumber());
	}

	//@Override
	public void onEndPage(PdfWriter writer, Document document) {
		//        PdfPTable table = new PdfPTable(2);
		//        try {
		//            table.setWidths(new int[]{24, 24});
		//            table.setTotalWidth(527);
		//            table.setLockedWidth(true);
		//            table.getDefaultCell().setFixedHeight(20);
		//            table.getDefaultCell().setBorder(Rectangle.BOTTOM);
		//            table.addCell("footer");
		//            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		//            table.addCell(String.format("Page %d", writer.getPageNumber()));
		//            table.writeSelectedRows(0, -1, 34, 50, writer.getDirectContent());
		//        }
		//        catch(DocumentException de) {
		//            throw new ExceptionConverter(de);
		//        }    	
	}

	@Override
	public InputStream exportAsPdf(Profile p) {

		try {
			//235124512461436461436346314713713473463
			//TODO
			File tmpTOCFile = File.createTempFile("ProfileTOCTmp", ".pdf");
			this.document = new Document(PageSize.A4);
			this.writer = PdfWriter.getInstance(this.document, FileUtils.openOutputStream(tmpTOCFile));
			//this.writer.setPageEvent(this);
			this.document.open();

			tocPlaceholder = new HashMap<>();
			pageByTitle    = new HashMap<>();
			//235124512461436461436346314713713473463


			// Create fonts and colors to be used in generated pdf
			BaseColor headerColor = WebColors.getRGBColor("#0033CC");
			BaseColor cpColor = WebColors.getRGBColor("#C0C0C0");
			Font titleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.UNDERLINE
					| Font.BOLD, BaseColor.RED);
			Font headerFont = FontFactory.getFont(
					"/rendering/Arial Narrow.ttf", BaseFont.IDENTITY_H,
					BaseFont.EMBEDDED, 11, Font.NORMAL, BaseColor.WHITE);
			Font cellFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
					BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL,
					BaseColor.BLACK);

			List<String> header;
			PdfPTable table;
			float columnWidths[];
			List<List<String>> rows;

			//TODO 
			//
			//			File tmpPdfFile = File.createTempFile("ProfileTmp", ".pdf");			
			//			Document document1 = new Document();
			//			writer1 = PdfWriter.getInstance(document1,
			//					FileUtils.openOutputStream(tmpPdfFile));
			//			this.writer1.setPageEvent(this);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document document1 = new Document();
			writer1 = PdfWriter.getInstance(document1, baos);
			this.writer1.setPageEvent(this);


			document1.setPageSize(PageSize.A4);
			document1.setMargins(36f, 36f, 36f, 36f); // 72pt = 1 inch
			document1.open();

//			/*
//			 * Adding messages definition
//			 */
//			document1.add(new Paragraph("Cover Page", titleFont));
//			document1.add(new Paragraph("Profile title", titleFont));
//			document1.add(new Paragraph("Profile identifier", titleFont));
//			document1.add(Chunk.NEWLINE);
//
//			document1.newPage();
//


			/*
			 * Adding messages definition
			 */
			document1.add(new Paragraph("Messages definition", titleFont));
			document1.add(Chunk.NEWLINE);
			for (Message m : p.getMessages().getChildren()) {
				document1.add(new Paragraph("Message definition: "));
				document1.add(new Paragraph(m.getStructID() + " - "
						+ m.getDescription()));
				document1.add(Chunk.NEWLINE);

				header = Arrays.asList("Segment", "STD\nUsage", "Local\nUsage",
						"STD\nCard.", "Local\nCard.", "Comment");
				columnWidths = new float[] { 4f, 3f, 3f, 2f, 2f, 8f };
				table = this.createHeader(header, columnWidths, headerFont,
						headerColor);

				rows = new ArrayList<List<String>>();

				List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();

				for (SegmentRefOrGroup srog : segRefOrGroups) {
					if (srog instanceof SegmentRef) {
						this.addSegmentPdf1(rows, (SegmentRef) srog, 0);
					} else if (srog instanceof Group) {
						this.addGroupPdf1(rows, (Group) srog, 0);
					}
				}
				this.addCells(table, rows, cellFont, cpColor);
				document1.add(table);
			}

			/*
			 * Adding segments details
			 */
			for (Message m : p.getMessages().getChildren()) {
				document1.newPage();

				document1.add(new Paragraph("Segments definition", titleFont));
				document1.add(Chunk.NEWLINE);				

				header = Arrays.asList("Seq", "Element Name", "DT",
						"STD\nUsage", "Local\nUsage", "Std\nCard.",
						"Local\nCard.", "Len", "Value\nSet", "Comment");
				columnWidths = new float[] { 2f, 3f, 2f, 1.5f, 1.5f, 1.5f,
						1.5f, 1.5f, 2f, 6f };

				for (SegmentRefOrGroup srog : m.getChildren()) {
					table = this.createHeader(header, columnWidths, headerFont,
							headerColor);
					rows = new ArrayList<List<String>>();
					if (srog instanceof SegmentRef) {
						this.addSegmentPdf2(document1, header,
								columnWidths, (SegmentRef) srog, headerFont,
								headerColor, cellFont, cpColor);

					} else if (srog instanceof Group) {
						this.addGroupPdf2(document1, header, columnWidths,
								(Group) srog, headerFont, headerColor,
								cellFont, cpColor);
					}
				}
			}

			/*
			 * Adding datatypes
			 */
			document1.add(new Paragraph("Datatypes", titleFont));
			document1.add(Chunk.NEWLINE);

			header = Arrays.asList("Seq", "Element Name", "Conf length", "DT",
					"Usage", "Len", "Table", "Comment");
			columnWidths = new float[] { 2f, 3f, 2f, 1.5f, 1.5f, 2f, 2f, 6f };

			for (Datatype d : p.getDatatypes().getChildren()) {
				if (d.getLabel().contains("_")){
					document1.add(new Paragraph(d.getLabel() + " - "
							+ d.getDescription() + " Datatype"));
					document1.add(new Paragraph(d.getComment()));

					table = this.createHeader(header, columnWidths, headerFont,
							headerColor);
					rows = new ArrayList<List<String>>();
					this.addComponentPdf2(rows, d);
					this.addCells(table, rows, cellFont, cpColor);
					document1.add(Chunk.NEWLINE);
					document1.add(table);
					document1.add(Chunk.NEWLINE);
				}
			}

			/*
			 * Adding value sets
			 */
			document1.add(new Paragraph("Value Sets", titleFont));
			document1.add(Chunk.NEWLINE);

			header = Arrays.asList("Value", "Description");

			columnWidths = new float[] {2f, 6f };

			List<Table> tables = new ArrayList<Table>(p.getTables().getChildren());
			Collections.sort(tables);

			for (Table t: tables) {
				document1.add(new Paragraph("Table " + t.getMappingId()
						+ " : "+ t.getName()));

				table = this.createHeader(header, columnWidths, headerFont,
						headerColor);
				rows = new ArrayList<List<String>>();
				this.addCodesPdf2(rows, t);
				this.addCells(table, rows, cellFont, cpColor);
				document1.add(Chunk.NEWLINE);
				document1.add(table);
				document1.add(Chunk.NEWLINE);
			}

			document1.close();
			//23451251235125124512425214231523523	
			//TODO
			this.document.close();
			//23451251235125124512425214231523523	


			// SECOND PASS, ADD THE FOOTER
			File tmpPdfFile = File.createTempFile("ProfileTmp", ".pdf");

			// Create a reader
			PdfReader reader = new PdfReader(baos.toByteArray());
			// Create a stamper
			PdfStamper stamper
			= new PdfStamper(reader, FileUtils.openOutputStream(tmpPdfFile));
			// Loop over the pages and add a header to each page
			int n = reader.getNumberOfPages();
			for (int i = 1; i <= n; i++) {
				getFooterTable("pname: "+p.getMetaData().getName(), i, n).writeSelectedRows(
						0, -1, 34, 50, stamper.getOverContent(i));
			}
			stamper.close();
			reader.close();
			
//
//			// THIRD PASS
//			//this.insertTOC(tmpTOCFile.getPath(), tmpPdfFile.getPath());
//			
//			String reader_ = tmpTOCFile.getPath(); 
//			String stationery_ = tmpPdfFile.getPath();
//			
//	        List<InputStream> list = new ArrayList<InputStream>();
//	        list.add(FileUtils.openInputStream(tmpTOCFile));
//	        list.add(FileUtils.openInputStream(tmpPdfFile));
//
//
//			File tmpFinalPdfFile = File.createTempFile("ProfileTmp", ".pdf");
//	        Document documentf = new Document();
//	        PdfWriter writer = PdfWriter.getInstance(documentf, FileUtils.openOutputStream(tmpFinalPdfFile));
//	        documentf.open();
//	        PdfContentByte cb = writer.getDirectContent();
//	        for (InputStream in : list) {
//	            PdfReader readerf = new PdfReader(in);
//	            for (int i = 1; i <= readerf.getNumberOfPages(); i++) {
//	                documentf.newPage();
//	                //import the page from source pdf
//	                PdfImportedPage page = writer.getImportedPage(readerf, i);
//	                //add the page to the destination pdf
//	                cb.addTemplate(page, 0, 0);
//	            }
//	        }
//	        document.close();
	        
	        
			
//			reader = new PdfReader(reader_);
//			PdfReader stationery = new PdfReader(stationery_);
//			stamper = new PdfStamper(stationery, FileUtils.openOutputStream(tmpPdfFile));
//			
//			PdfImportedPage page = stamper.getImportedPage(stationery, 1);
//			
//			int i = 0;
//			// Add the content of the ColumnText object 
//			//while(true) {
//				// Add a new page
//				stamper.insertPage(++i, reader.getPageSize(1));
//				// Add the stationary to the new page
//				stamper.getUnderContent(i).addTemplate(page, 0, 0);
//			//}
//			// Close the stamper
//			stamper.close();
//			//reader.close();
//			stationery.close();
//


			return FileUtils.openInputStream(tmpPdfFile);
			
			
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	
	public PdfPTable getFooterTable(String footer, int x, int y) {
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(527);
		table.setLockedWidth(true);
		table.getDefaultCell().setFixedHeight(20);
		table.getDefaultCell().setBorder(Rectangle.TOP);
		table.addCell(footer);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(String.format("Page %d of %d", x, y));
		return table;
	}

	private PdfPTable createHeader(List<String> headers, float[] columnWidths,
			Font headerFont, BaseColor headerColor) {
		PdfPTable table = new PdfPTable(headers.size());
		PdfPCell c1;

		table.setTotalWidth(PageSize.A4.getWidth() - 72);
		table.setLockedWidth(true);

		for (String cellName : headers) {
			c1 = new PdfPCell(new Phrase(cellName, headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_LEFT);
			c1.setBackgroundColor(headerColor);
			table.addCell(c1);
		}
		try {
			if (columnWidths.length != 0) {
				table.setWidths(columnWidths);
			}
		} catch (DocumentException e) {

			e.printStackTrace();
		}
		return table;
	}

	private void addCells(PdfPTable table, List<List<String>> rows,
			Font cellFont, BaseColor cpColor) {
		for (List<String> cells : rows) {
			if (cells.size() != 3) {
				for (String cell : cells) {
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

	private void addGroupPdf1(List<List<String>> rows, Group g, Integer depth) {
		String indent = StringUtils.repeat(" ", 2 * depth);

		List<String> row = Arrays.asList(
				indent + "[",
				g.getUsage().value(),
				"",
				"[" + String.valueOf(g.getMin()) + ".."
						+ String.valueOf(g.getMax()) + "]", "",
						"BEGIN " + g.getName() + " GROUP");
		rows.add(row);

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentPdf1(rows, (SegmentRef) srog, depth + 1);
			} else if (srog instanceof Group) {
				this.addGroupPdf1(rows, (Group) srog, depth + 1);
			}
		}

		row = Arrays.asList(indent + "]", "", "", "", "", "END " + g.getName()
				+ " GROUP");
		rows.add(row);
	}

	private void addGroupPdf2(Document document, List<String> header,
			float[] columnWidths, Group g, Font headerFont,
			BaseColor headerColor, Font cellFont, BaseColor cpColor)
					throws DocumentException {

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentPdf2(document, header,
						columnWidths, (SegmentRef) srog, headerFont,
						headerColor, cellFont, cpColor);
			} else if (srog instanceof Group) {
				this.addGroupPdf2(document, header, columnWidths, (Group) srog,
						headerFont, headerColor, cellFont, cpColor);
			}
		}
	}

	private void addGroupXlsx2(List<String> header, Group g, XSSFWorkbook workbook, XSSFCellStyle headerStyle)
			throws DocumentException {

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentXlsx2(((SegmentRef) srog).getRef(), header, workbook, headerStyle);
			} else if (srog instanceof Group) {
				this.addGroupXlsx2(header, (Group) srog, workbook, headerStyle);
			}
		}
	}

	private void addSegmentXlsx2(Segment s, List<String> header, XSSFWorkbook workbook, XSSFCellStyle headerStyle){
		List<List<String>> rows = new ArrayList<List<String>>();
		XSSFSheet sheet = workbook.createSheet(s.getName());
		rows.add(header);
		this.addFieldPdf2(rows, s, Boolean.FALSE);
		this.writeToSheet(rows, header, sheet, headerStyle);
	}


	private void addGroupXlsx(List<List<String>> rows, Group g, Integer depth) {
		String indent = StringUtils.repeat(" ", 4 * depth);

		List<String> row = Arrays.asList(
				indent + "BEGIN " + g.getName() + " GROUP",
				g.getUsage().value(),
				"",
				"[" + String.valueOf(g.getMin()) + ".."
						+ String.valueOf(g.getMax()) + "]", "", "");
		rows.add(row);
		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentXlsx(rows, (SegmentRef) srog, depth + 1);
			} else if (srog instanceof Group) {
				this.addGroupXlsx(rows, (Group) srog, depth + 1);
			}
		}
		row = Arrays.asList(indent + "END " + g.getName() + " GROUP", 
				"", "", "");
		rows.add(row);
	}

	private void addSegmentPdf1(List<List<String>> rows, SegmentRef s,
			Integer depth) {
		String indent = StringUtils.repeat(" ", 4 * depth);

		List<String> row = Arrays.asList(indent + s.getRef().getName(), s
				.getUsage().value(), "", "[" + String.valueOf(s.getMin())
				+ ".." + String.valueOf(s.getMax()) + "]", "", s.getRef()
				.getComment() == null ? "" : s.getRef().getComment());
		rows.add(row);
	}

	private void addSegmentPdf2(Document document, List<String> header,
			float[] columnWidths, SegmentRef segRef, Font headerFont,
			BaseColor headerColor, Font cellFont, BaseColor cpColor) throws DocumentException{

		PdfPTable table = this.createHeader(header, columnWidths, headerFont,
				headerColor);
		ArrayList<List<String>> rows = new ArrayList<List<String>>();

		Segment s = segRef.getRef();




		//23451251235125124512425214231523523	
		//TODO
		//Create TOC
		final String title = s.getName();
		Chunk chunk = new Chunk(title).setLocalGoto(title);
		this.document.add(new Paragraph(chunk));

		// Add a placeholder for the page reference
		this.document.add(new VerticalPositionMark() {
			@Override
			public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx, final float ury, final float y)
			{
				final PdfTemplate createTemplate = canvas.createTemplate(50, 50);
				ProfileServiceImpl.this.tocPlaceholder.put(title, createTemplate);

				canvas.addTemplate(createTemplate, urx - 50, y);
			}
		});

		//TODO
		//Create page numbers
		PdfTemplate template = this.tocPlaceholder.get(title);
		template.beginText();
		//template.setFontAndSize(this.baseFont, 12);
		//template.setTextMatrix(50 - this.baseFont.getWidthPoint(String.valueOf(this.writer.getPageNumber()), 12), 0);
		BaseFont baseFont;
		try {
			baseFont = BaseFont.createFont();
			template.setFontAndSize(baseFont, 12);
			//			template.setTextMatrix(50 - baseFont.getWidthPoint(String.valueOf(this.writer.getPageNumber()), 12), 0);
			//			template.showText(String.valueOf(this.writer.getPageNumber()));
			template.setTextMatrix(50 - baseFont.getWidthPoint(String.valueOf(this.writer1.getPageNumber()), 12), 0);
			template.showText(String.valueOf(this.writer1.getPageNumber()));
			template.endText();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//23451251235125124512425214231523523		





		document.add(new Paragraph(s.getName() + ": " +
				s.getDescription() + " Segment"));
		document.add(Chunk.NEWLINE);
		document.add(new Paragraph(s.getText1()));
		this.addFieldPdf2(rows, s, Boolean.TRUE);
		this.addCells(table, rows, cellFont, cpColor);
		document.add(table);
		document.add(Chunk.NEWLINE);
		document.add(new Paragraph(s.getText2()));
		document.add(Chunk.NEWLINE);

		List<Field> fieldsList = s.getFields();
		Collections.sort(fieldsList);
		for (Field f : fieldsList) {
			if (f.getText() != null && f.getText().length() != 0){
				Font fontbold = FontFactory.getFont("Times-Roman", 12, Font.BOLD);
				document.add(new Paragraph(s.getName() + "-" +
						f.getItemNo().replaceFirst("^0+(?!$)", "") + " " + f.getName() +
						" (" + f.getDatatype().getLabel() + ")", fontbold));				
				document.add(new Paragraph(f.getText()));
			}
		}
		//		//TODO REMOVE FOLLOWING AFTER DEMO!!
		//		for (Field f : fieldsList) {
		//			Font fontbold = FontFactory.getFont("Times-Roman", 12, Font.BOLD);
		//			document.add(new Paragraph(s.getName() + "-" +
		//					f.getItemNo().replaceFirst("^0+(?!$)", "") + " " + f.getName() +
		//					" (" + f.getDatatype().getLabel() + ")", fontbold));
		//			document.add(new Paragraph("wfnwenfwnvw"));
		//		}
		document.newPage();

	}

	private void addSegmentXlsx(List<List<String>> rows, SegmentRef s,
			Integer depth) {
		String indent = StringUtils.repeat(" ", 4 * depth);

		List<String> row = Arrays.asList(indent + s.getRef().getName(), s
				.getUsage().value(), "", "[" + String.valueOf(s.getMin())
				+ ".." + String.valueOf(s.getMax()) + "]", "", s.getRef()
				.getComment() == null ? "" : s.getRef().getComment());
		rows.add(row);
	}

	private List<Constraint> findConstraints(Integer target,
			List<Predicate> predicates,
			List<ConformanceStatement> conformanceStatements) {
		List<Constraint> constraints = new ArrayList<>();
		for (Predicate pre : predicates) {
			if (target == Integer.parseInt(pre.getConstraintTarget().substring(
					0, pre.getConstraintTarget().indexOf('[')))) {
				constraints.add(pre);
			}
		}
		for (ConformanceStatement conformanceStatement : conformanceStatements) {
			if (target == Integer.parseInt(conformanceStatement
					.getConstraintTarget().substring(
							0,
							conformanceStatement.getConstraintTarget().indexOf(
									'[')))) {
				constraints.add(conformanceStatement);
			}
		}
		return constraints;
	}

	private void addComponentPdf2(List<List<String>> rows, Datatype d) {
		List<String> row;
		// System.out.println(StringUtils.repeat("%",25));

		List<Predicate> predicates = d.getPredicates();
		List<ConformanceStatement> conformanceStatements = d
				.getConformanceStatements();

		List<Component> componentsList = new ArrayList<>(d.getComponents());
		Collections.sort(componentsList);
		if (componentsList.size() == 0){
			row = Arrays.asList("1", d.getName(), "", "", "", "", "",
					d.getComment());
			rows.add(row);
		} else {
			for (Component c : componentsList) {
				row = Arrays.asList(c.getPosition().toString(), c.getName(), c
						.getConfLength(), c.getDatatype().getLabel(), c.getUsage()
						.value(), "[" + String.valueOf(c.getMinLength()) + ","
								+ String.valueOf(c.getMaxLength()) + "]",
								(c.getTable() == null) ? "" : c.getTable().getMappingId(),
										c.getComment());
				rows.add(row);

				List<Constraint> constraints = this.findConstraints(
						componentsList.indexOf(c) + 1, predicates,
						conformanceStatements);
				if (!constraints.isEmpty()) {

					for (Constraint constraint : constraints) {
						String constraintType = new String();
						if (constraint instanceof Predicate) {
							constraintType = "Condition Predicate";
						} else if (constraint instanceof ConformanceStatement) {
							constraintType = "Conformance Statement";
						}
						row = Arrays.asList("", constraintType, constraint.getDescription());
						rows.add(row);
					}
				}
			}
		}
	}

	private void addFieldPdf2(List<List<String>> rows, Segment s, Boolean inlineConstraints) {
		List<String> row;
		List<Predicate> predicates = s.getPredicates();
		List<ConformanceStatement> conformanceStatements = s
				.getConformanceStatements();

		List<Field> fieldsList = s.getFields();
		Collections.sort(fieldsList);
		for (Field f : fieldsList) {
			row = Arrays.asList(
					//f.getItemNo().replaceFirst("^0+(?!$)", ""),
					String.valueOf(f.getPosition()),
					f.getName(),
					f.getDatatype().getLabel(),
					f.getUsage().value(),
					"",
					"[" + String.valueOf(f.getMin()) + ".."
							+ String.valueOf(f.getMax()) + "]",
							"",
							"[" + String.valueOf(f.getMinLength()) + ".."
									+ String.valueOf(f.getMaxLength()) + "]",
									(String) ((f.getTable() == null) ? "" : f.getTable()
											.getMappingId()), f.getComment());
			rows.add(row);

			if (inlineConstraints){
				List<Constraint> constraints = this.findConstraints(
						fieldsList.indexOf(f) + 1, predicates, conformanceStatements);
				this.addConstraints(rows, constraints);
			}
		}
		if (!inlineConstraints){
			for (Field f : fieldsList) {
				List<Constraint> constraints = this.findConstraints(
						fieldsList.indexOf(f) + 1, predicates, conformanceStatements);
				this.addConstraints(rows, constraints);
			}	
		}
	}

	private void addConstraints(List<List<String>> rows, List<Constraint> constraints ) {
		if (!constraints.isEmpty()) {
			List<String> row;
			for (Constraint constraint : constraints) {
				String constraintType = new String();
				if (constraint instanceof Predicate) {
					constraintType = "Condition Predicate";
				} else if (constraint instanceof ConformanceStatement) {
					constraintType = "Conformance Statement";
				}
				row = Arrays.asList("", 
						constraintType, constraint.getDescription());
				rows.add(row);
			}
		}			
	}

	private void addCodesPdf2(List<List<String>> rows, Table t) {
		List <String> row;
		List <Code> codes = t.getCodes();

		for (Code c: codes){
			row = Arrays.asList(c.getCode(), c.getLabel());
			rows.add(row);
		}

	}
}