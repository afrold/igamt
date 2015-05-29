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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileClone;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSaveException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ProfileServiceImpl extends PdfPageEventHelper implements
		ProfileService {

	@Autowired
	private ProfileRepository profileRepository;

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
		List<Profile> profiles = profileRepository.findPreloaded();
		return profiles;
	}

	// private void processChildren(Profile profile) {
	// List<Message> messages = messageService.findByMessagesId(profile
	// .getMessages().getId());
	// profile.getMessages().getChildren().addAll(messages);
	// }

	@Override
	public List<Profile> findByAccountId(Long accountId) {
		List<Profile> profiles = profileRepository.findByAccountId(accountId);
		// if (profiles != null && !profiles.isEmpty()) {
		// for (Profile profile : profiles) {
		// processChildren(profile);
		// }
		// }
		return profiles;
	}

	@Override
	public Profile clone(Profile p) throws CloneNotSupportedException {
		return new ProfileClone().clone(p);
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
	public InputStream exportAsZip(Profile p) throws IOException {
		if (p != null) {
			return new ProfileSerialization4ExportImpl()
					.serializeProfileToZip(p);
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
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE
					.getIndex());
			headerStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE
					.getIndex());

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
						this.addSegmentXlsx(rows, (SegmentRef) srog, 0,
								p.getSegments());
					} else if (srog instanceof Group) {
						this.addGroupXlsx(rows, (Group) srog, 0,
								p.getSegments(), p.getDatatypes());
					}
				}
				this.writeToSheet(rows, header, sheet, headerStyle);
			}

			for (Message m : p.getMessages().getChildren()) {
				rows = new ArrayList<List<String>>();
				header = Arrays.asList("Segment", "Name", "DT", "STD\nUsage",
						"Local\nUsage", "STD\nCard.", "Local\nCard.", "Len",
						"Value set", "Comment");

				for (SegmentRefOrGroup srog : m.getChildren()) {

					if (srog instanceof SegmentRef) {
						this.addSegmentXlsx2(
								p.getSegments().findOne(
										((SegmentRef) srog).getRef()), header,
								workbook, headerStyle, p.getDatatypes(),
								p.getTables());
					} else if (srog instanceof Group) {
						this.addGroupXlsx2(header, (Group) srog, workbook,
								headerStyle, p.getSegments(), p.getDatatypes(),
								p.getTables());
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

	private void writeToSheet(List<List<String>> rows, List<String> header,
			XSSFSheet sheet, XSSFCellStyle headerStyle) {
		// This data needs to be written (Object[])
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
				Cell cell = row.createCell(cellnum++);
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

	@Override
	public InputStream exportAsPdfFromXsl(Profile p, String inlineConstraints) {
		// Note: inlineConstraint can be true or false
		try {
			// Generate xml file containing profile
			File tmpXmlFile = File.createTempFile("ProfileTemp", ".xml");
			// File tmpXmlFile = new
			// File("/Users/marieros/Documents/testXslt/nvo/pp.xml");
			String stringProfile = new ProfileSerialization4ExportImpl()
					.serializeProfileToXML(p);
			FileUtils.writeStringToFile(tmpXmlFile, stringProfile,
					Charset.forName("UTF-8"));

			// Apply XSL transformation on xml file to generate html
			Source text = new StreamSource(tmpXmlFile);
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(this.getClass().getResourceAsStream(
					"/rendering/profile2a.xsl"));
			Transformer transformer;
			transformer = factory.newTransformer(xslt);
			transformer.setParameter("inlineConstraints", inlineConstraints);
			File tmpHtmlFile = File.createTempFile("ProfileTemp", ".html");
			// File tmpHtmlFile = new
			// File("/Users/marieros/Documents/testXslt/nvo/hh.html");
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

	// table to store placeholder for all chapters and sections
	private Map<String, PdfTemplate> tocPlaceholder;

	// store the chapters and sections with their title here.
	private Map<String, Integer> pageByTitle;

	@Override
	public InputStream exportAsPdf(Profile p) {

		List<String> header;
		PdfPTable table;
		float columnWidths[];
		List<List<String>> rows;

		// Create fonts and colors to be used in generated pdf
		BaseColor headerColor = WebColors.getRGBColor("#0033CC");
		BaseColor cpColor = WebColors.getRGBColor("#C0C0C0");
		Font coverH1Font = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 24, Font.UNDERLINE
						| Font.BOLD, BaseColor.BLUE);
		Font coverH2Font = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18, Font.NORMAL,
				BaseColor.BLUE);
		Font tocTitleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18, Font.BOLD,
				BaseColor.BLACK);
		Font titleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.UNDERLINE
						| Font.BOLD | Font.ITALIC, BaseColor.BLACK);
		Font headerFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL,
				BaseColor.WHITE);
		Font cellFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL,
				BaseColor.BLACK);

		try {
			/*
			 * Create cover page
			 */
			ByteArrayOutputStream coverBaos = new ByteArrayOutputStream();
			Document coverDocument = new Document(PageSize.A4);
			@SuppressWarnings("unused")
			PdfWriter coverWriter = PdfWriter.getInstance(coverDocument,
					coverBaos);
			coverDocument.open();

			Paragraph paragraph = new Paragraph(p.getMetaData().getName(),
					coverH1Font);
			paragraph.setSpacingBefore(250);
			paragraph.setAlignment(Element.ALIGN_CENTER);

			coverDocument.add(paragraph);
			paragraph = new Paragraph(p.getMetaData().getSubTitle(),
					coverH2Font);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);
			paragraph = new Paragraph(
					"HL7 v" + p.getMetaData().getHl7Version(), coverH2Font);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			paragraph.setSpacingAfter(250);
			coverDocument.add(paragraph);

			paragraph = new Paragraph();
			paragraph.add(new Chunk(p.getMetaData().getOrgName(), coverH2Font));
			paragraph.add(Chunk.NEWLINE);
			paragraph.add(new Phrase("Document Version "
					+ p.getMetaData().getVersion(), coverH2Font));
			paragraph.add(Chunk.NEWLINE);
			paragraph.add(new Phrase("Status : " + p.getMetaData().getStatus(),
					coverH2Font));
			paragraph.add(Chunk.NEWLINE);
			paragraph.add(new Chunk(p.getMetaData().getDate(), coverH2Font));
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);

			coverDocument.close();

			/*
			 * Initiate table of content
			 */
			ByteArrayOutputStream tocTmpBaos = new ByteArrayOutputStream();
			Document tocDocument = new Document(PageSize.A4);
			@SuppressWarnings("unused")
			PdfWriter tocWriter = PdfWriter
					.getInstance(tocDocument, tocTmpBaos);
			tocDocument.open();
			tocDocument.add(new Paragraph("Table of contents", tocTitleFont));
			tocDocument.add(Chunk.NEWLINE);

			tocPlaceholder = new HashMap<>();
			pageByTitle = new HashMap<>();

			/*
			 * Initiate implementation guide
			 */
			ByteArrayOutputStream igTmpBaos = new ByteArrayOutputStream();
			Document igDocument = new Document();
			PdfWriter igWriter = PdfWriter.getInstance(igDocument, igTmpBaos);
			igWriter.setPageEvent(this);

			igDocument.setPageSize(PageSize.A4);
			igDocument.setMargins(36f, 36f, 36f, 36f); // 72pt = 1 inch
			igDocument.open();

			/*
			 * Adding messages definition
			 */
			tocDocument.add(new Paragraph("Messages definition", titleFont));
			tocDocument.add(Chunk.NEWLINE);

			igDocument.add(new Paragraph("Messages definition", titleFont));
			igDocument.add(Chunk.NEWLINE);
			for (Message m : p.getMessages().getChildren()) {

				this.addTocContent(tocDocument, igWriter, m.getStructID()
						+ " - " + m.getDescription());

				igDocument.add(new Paragraph("Message definition: "));
				igDocument.add(new Paragraph(m.getStructID() + " - "
						+ m.getDescription()));
				igDocument.add(Chunk.NEWLINE);

				header = Arrays.asList("Segment", "STD\nUsage", "Local\nUsage",
						"STD\nCard.", "Local\nCard.", "Comment");
				columnWidths = new float[] { 4f, 3f, 3f, 2f, 2f, 8f };
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerColor);

				rows = new ArrayList<List<String>>();

				List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();

				for (SegmentRefOrGroup srog : segRefOrGroups) {
					if (srog instanceof SegmentRef) {
						this.addSegmentPdf1(rows, (SegmentRef) srog, 0,
								p.getSegments());
					} else if (srog instanceof Group) {
						this.addGroupPdf1(rows, (Group) srog, 0,
								p.getSegments(), p.getDatatypes());
					}
				}
				this.addCellsPdfTable(table, rows, cellFont, cpColor);
				igDocument.add(table);
			}

			/*
			 * Adding segments details
			 */
			for (Message m : p.getMessages().getChildren()) {
				igDocument.newPage();

				tocDocument.add(Chunk.NEWLINE);
				tocDocument
						.add(new Paragraph("Segments definition", titleFont));
				tocDocument.add(Chunk.NEWLINE);

				igDocument.add(new Paragraph("Segments definition", titleFont));
				igDocument.add(Chunk.NEWLINE);
				header = Arrays.asList("Seq", "Element Name", "DT",
						"STD\nUsage", "Local\nUsage", "Std\nCard.",
						"Local\nCard.", "Len", "Value\nSet", "Comment");
				columnWidths = new float[] { 2f, 3f, 2f, 1.5f, 1.5f, 1.5f,
						1.5f, 1.5f, 2f, 6f };

				for (SegmentRefOrGroup srog : m.getChildren()) {
					table = this.addHeaderPdfTable(header, columnWidths,
							headerFont, headerColor);
					rows = new ArrayList<List<String>>();
					if (srog instanceof SegmentRef) {
						this.addSegmentPdf2(igDocument, igWriter, tocDocument,
								header, columnWidths, (SegmentRef) srog,
								headerFont, headerColor, cellFont, cpColor,
								p.getSegments(), p.getDatatypes(),
								p.getTables());

					} else if (srog instanceof Group) {
						this.addGroupPdf2(igDocument, igWriter, tocDocument,
								header, columnWidths, (Group) srog, headerFont,
								headerColor, cellFont, cpColor,
								p.getSegments(), p.getDatatypes(),
								p.getTables());
					}
				}
			}

			/*
			 * Adding datatypes
			 */
			igDocument.add(new Paragraph("Datatypes", titleFont));
			igDocument.add(Chunk.NEWLINE);

			tocDocument.add(Chunk.NEWLINE);
			tocDocument.add(new Paragraph("Datatypes", titleFont));
			tocDocument.add(Chunk.NEWLINE);

			header = Arrays.asList("Seq", "Element Name", "Conf length", "DT",
					"Usage", "Len", "Table", "Comment");
			columnWidths = new float[] { 2f, 3f, 2f, 1.5f, 1.5f, 2f, 2f, 6f };

			for (Datatype d : p.getDatatypes().getChildren()) {
				if (d.getLabel().contains("_")) {

					this.addTocContent(tocDocument, igWriter, d.getLabel()
							+ " - " + d.getDescription());

					igDocument.add(new Paragraph(d.getLabel() + " - "
							+ d.getDescription() + " Datatype"));
					igDocument.add(new Paragraph(d.getComment()));

					table = this.addHeaderPdfTable(header, columnWidths,
							headerFont, headerColor);
					rows = new ArrayList<List<String>>();
					this.addComponentPdf2(rows, d, p.getDatatypes(),
							p.getTables());
					this.addCellsPdfTable(table, rows, cellFont, cpColor);
					igDocument.add(Chunk.NEWLINE);
					igDocument.add(table);
					igDocument.add(Chunk.NEWLINE);
				}
			}

			/*
			 * Adding value sets
			 */
			igDocument.add(new Paragraph("Value Sets", titleFont));
			igDocument.add(Chunk.NEWLINE);

			tocDocument.add(Chunk.NEWLINE);
			tocDocument.add(new Paragraph("Value Sets", titleFont));
			tocDocument.add(Chunk.NEWLINE);

			header = Arrays.asList("Value", "Description");

			columnWidths = new float[] { 2f, 6f };

			List<Table> tables = new ArrayList<Table>(p.getTables()
					.getChildren());
			Collections.sort(tables);

			for (Table t : tables) {

				this.addTocContent(tocDocument, igWriter, t.getMappingId()
						+ " : " + t.getName());

				igDocument.add(new Paragraph("Table " + t.getMappingId()
						+ " : " + t.getName()));

				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerColor);
				rows = new ArrayList<List<String>>();
				this.addCodesPdf2(rows, t);
				this.addCellsPdfTable(table, rows, cellFont, cpColor);
				igDocument.add(Chunk.NEWLINE);
				igDocument.add(table);
				igDocument.add(Chunk.NEWLINE);
			}

			igDocument.close();
			tocDocument.close();

			/*
			 * Second pass: Add footers
			 */
			ByteArrayOutputStream igBaos = this.addPageNumber(igTmpBaos, "ig",
					p.getMetaData().getName());
			ByteArrayOutputStream tocBaos = this.addPageNumber(tocTmpBaos,
					"toc", p.getMetaData().getName());

			/*
			 * Third pass: Merge
			 */
			List<byte[]> list = new ArrayList<byte[]>();
			list.add(coverBaos.toByteArray());
			list.add(tocBaos.toByteArray());
			list.add(igBaos.toByteArray());

			ByteArrayOutputStream igFinalBaos = new ByteArrayOutputStream();
			Document igFinalDocument = new Document();
			PdfWriter igFinalWriter = PdfWriter.getInstance(igFinalDocument,
					igFinalBaos);
			igFinalDocument.open();
			PdfContentByte cb = igFinalWriter.getDirectContent();
			for (byte[] in : list) {
				PdfReader readerf = new PdfReader(in);
				for (int i = 1; i <= readerf.getNumberOfPages(); i++) {
					igFinalDocument.newPage();
					PdfImportedPage page = igFinalWriter.getImportedPage(
							readerf, i);
					cb.addTemplate(page, 0, 0);
				}
			}
			igFinalDocument.close();

			return new ByteArrayInputStream(igFinalBaos.toByteArray());
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	public void registerChange(Map<String, List<String>> dict, String key,
			String value) {
		if (dict.containsKey(key)) {
			dict.get(key).add(value);
		} else {
			dict.put(key, new ArrayList<String>());
			dict.get(key).add(value);
		}
	}

	// public Map<String, List<String>> compareMetadata(ProfileMetaData m1,
	// ProfileMetaData m2){
	// Map<String, List<String>> rst = new HashMap<String, List<String>>();
	//
	// m1.getHl7Version()
	// m1.getIdentifier()
	// m1.getName()
	// m1.getOrgName();
	// m1.getSchemaVersion();
	// m1.getStatus();
	// m1.getTopics();
	// m1.getType();
	// m1.getDate();
	// m1.getSubTitle();
	// m1.getVersion();
	//
	//
	// return rst;
	// }
	//
	// @Override
	// public InputStream compare(Profile p, Profile p2) {
	//
	// List<String> header;
	// PdfPTable table;
	// float columnWidths[];
	// List<List<String>> rows;
	//
	//
	// try {
	// paragraph = new Paragraph(p.getMetaData().getSubTitle(),
	// coverH2Font);
	// paragraph.setAlignment(Element.ALIGN_CENTER);
	// coverDocument.add(paragraph);
	// paragraph = new Paragraph(
	// "HL7 v" + p.getMetaData().getHl7Version(), coverH2Font);
	// paragraph.setAlignment(Element.ALIGN_CENTER);
	// paragraph.setSpacingAfter(250);
	// coverDocument.add(paragraph);
	//
	// paragraph = new Paragraph();
	// paragraph.add(new Chunk(p.getMetaData().getOrgName(), coverH2Font));
	// paragraph.add(Chunk.NEWLINE);
	// paragraph.add(new Phrase("Document Version "
	// + p.getMetaData().getVersion(), coverH2Font));
	// paragraph.add(Chunk.NEWLINE);
	// paragraph.add(new Phrase("Status : " + p.getMetaData().getStatus(),
	// coverH2Font));
	// paragraph.add(Chunk.NEWLINE);
	// paragraph.add(new Chunk(p.getMetaData().getDate(), coverH2Font));
	// paragraph.setAlignment(Element.ALIGN_CENTER);
	// coverDocument.add(paragraph);
	//
	// coverDocument.close();
	//
	// /*
	// * Initiate table of content
	// */
	// ByteArrayOutputStream tocTmpBaos = new ByteArrayOutputStream();
	// Document tocDocument = new Document(PageSize.A4);
	// @SuppressWarnings("unused")
	// PdfWriter tocWriter = PdfWriter
	// .getInstance(tocDocument, tocTmpBaos);
	// tocDocument.open();
	// tocDocument.add(new Paragraph("Table of contents", tocTitleFont));
	// tocDocument.add(Chunk.NEWLINE);
	//
	// tocPlaceholder = new HashMap<>();
	// pageByTitle = new HashMap<>();
	//
	// /*
	// * Initiate implementation guide
	// */
	// ByteArrayOutputStream igTmpBaos = new ByteArrayOutputStream();
	// Document igDocument = new Document();
	// PdfWriter igWriter = PdfWriter.getInstance(igDocument, igTmpBaos);
	// igWriter.setPageEvent(this);
	//
	// igDocument.setPageSize(PageSize.A4);
	// igDocument.setMargins(36f, 36f, 36f, 36f); // 72pt = 1 inch
	// igDocument.open();
	//
	// /*
	// * Adding messages definition
	// */
	// tocDocument.add(new Paragraph("Messages definition", titleFont));
	// tocDocument.add(Chunk.NEWLINE);
	//
	// igDocument.add(new Paragraph("Messages definition", titleFont));
	// igDocument.add(Chunk.NEWLINE);
	// for (Message m : p.getMessages().getChildren()) {
	//
	// this.addTocContent(tocDocument, igWriter, m.getStructID()
	// + " - " + m.getDescription());
	//
	// igDocument.add(new Paragraph("Message definition: "));
	// igDocument.add(new Paragraph(m.getStructID() + " - "
	// + m.getDescription()));
	// igDocument.add(Chunk.NEWLINE);
	//
	// header = Arrays.asList("Segment", "STD\nUsage", "Local\nUsage",
	// "STD\nCard.", "Local\nCard.", "Comment");
	// columnWidths = new float[] { 4f, 3f, 3f, 2f, 2f, 8f };
	// table = this.addHeaderPdfTable(header, columnWidths,
	// headerFont, headerColor);
	//
	// rows = new ArrayList<List<String>>();
	//
	// List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
	//
	// for (SegmentRefOrGroup srog : segRefOrGroups) {
	// if (srog instanceof SegmentRef) {
	// this.addSegmentPdf1(rows, (SegmentRef) srog, 0,
	// p.getSegments());
	// } else if (srog instanceof Group) {
	// this.addGroupPdf1(rows, (Group) srog, 0,
	// p.getSegments(), p.getDatatypes());
	// }
	// }
	// this.addCellsPdfTable(table, rows, cellFont, cpColor);
	// igDocument.add(table);
	// }
	//
	// /*
	// * Adding segments details
	// */
	// for (Message m : p.getMessages().getChildren()) {
	// igDocument.newPage();
	//
	// tocDocument.add(Chunk.NEWLINE);
	// tocDocument
	// .add(new Paragraph("Segments definition", titleFont));
	// tocDocument.add(Chunk.NEWLINE);
	//
	// igDocument.add(new Paragraph("Segments definition", titleFont));
	// igDocument.add(Chunk.NEWLINE);
	// header = Arrays.asList("Seq", "Element Name", "DT",
	// "STD\nUsage", "Local\nUsage", "Std\nCard.",
	// "Local\nCard.", "Len", "Value\nSet", "Comment");
	// columnWidths = new float[] { 2f, 3f, 2f, 1.5f, 1.5f, 1.5f,
	// 1.5f, 1.5f, 2f, 6f };
	//
	// for (SegmentRefOrGroup srog : m.getChildren()) {
	// table = this.addHeaderPdfTable(header, columnWidths,
	// headerFont, headerColor);
	// rows = new ArrayList<List<String>>();
	// if (srog instanceof SegmentRef) {
	// this.addSegmentPdf2(igDocument, igWriter, tocDocument,
	// header, columnWidths, (SegmentRef) srog,
	// headerFont, headerColor, cellFont, cpColor,
	// p.getSegments(), p.getDatatypes(),
	// p.getTables());
	//
	// } else if (srog instanceof Group) {
	// this.addGroupPdf2(igDocument, igWriter, tocDocument,
	// header, columnWidths, (Group) srog, headerFont,
	// headerColor, cellFont, cpColor,
	// p.getSegments(), p.getDatatypes(),
	// p.getTables());
	// }
	// }
	// }
	//
	// /*
	// * Adding datatypes
	// */
	// igDocument.add(new Paragraph("Datatypes", titleFont));
	// igDocument.add(Chunk.NEWLINE);
	//
	// tocDocument.add(Chunk.NEWLINE);
	// tocDocument.add(new Paragraph("Datatypes", titleFont));
	// tocDocument.add(Chunk.NEWLINE);
	//
	// header = Arrays.asList("Seq", "Element Name", "Conf length", "DT",
	// "Usage", "Len", "Table", "Comment");
	// columnWidths = new float[] { 2f, 3f, 2f, 1.5f, 1.5f, 2f, 2f, 6f };
	//
	// for (Datatype d : p.getDatatypes().getChildren()) {
	// if (d.getLabel().contains("_")) {
	//
	// this.addTocContent(tocDocument, igWriter, d.getLabel()
	// + " - " + d.getDescription());
	//
	// igDocument.add(new Paragraph(d.getLabel() + " - "
	// + d.getDescription() + " Datatype"));
	// igDocument.add(new Paragraph(d.getComment()));
	//
	// table = this.addHeaderPdfTable(header, columnWidths,
	// headerFont, headerColor);
	// rows = new ArrayList<List<String>>();
	// this.addComponentPdf2(rows, d, p.getDatatypes(),
	// p.getTables());
	// this.addCellsPdfTable(table, rows, cellFont, cpColor);
	// igDocument.add(Chunk.NEWLINE);
	// igDocument.add(table);
	// igDocument.add(Chunk.NEWLINE);
	// }
	// }
	//
	// /*
	// * Adding value sets
	// */
	// igDocument.add(new Paragraph("Value Sets", titleFont));
	// igDocument.add(Chunk.NEWLINE);
	//
	// tocDocument.add(Chunk.NEWLINE);
	// tocDocument.add(new Paragraph("Value Sets", titleFont));
	// tocDocument.add(Chunk.NEWLINE);
	//
	// header = Arrays.asList("Value", "Description");
	//
	// columnWidths = new float[] { 2f, 6f };
	//
	// List<Table> tables = new ArrayList<Table>(p.getTables()
	// .getChildren());
	// Collections.sort(tables);
	//
	// for (Table t : tables) {
	//
	// this.addTocContent(tocDocument, igWriter, t.getMappingId()
	// + " : " + t.getName());
	//
	// igDocument.add(new Paragraph("Table " + t.getMappingId()
	// + " : " + t.getName()));
	//
	// table = this.addHeaderPdfTable(header, columnWidths,
	// headerFont, headerColor);
	// rows = new ArrayList<List<String>>();
	// this.addCodesPdf2(rows, t);
	// this.addCellsPdfTable(table, rows, cellFont, cpColor);
	// igDocument.add(Chunk.NEWLINE);
	// igDocument.add(table);
	// igDocument.add(Chunk.NEWLINE);
	// }
	//
	// igDocument.close();
	// tocDocument.close();
	//
	// /*
	// * Second pass: Add footers
	// */
	// ByteArrayOutputStream igBaos = this.addPageNumber(igTmpBaos, "ig",
	// p.getMetaData().getName());
	// ByteArrayOutputStream tocBaos = this.addPageNumber(tocTmpBaos,
	// "toc", p.getMetaData().getName());
	//
	// /*
	// * Third pass: Merge
	// */
	// List<byte[]> list = new ArrayList<byte[]>();
	// list.add(coverBaos.toByteArray());
	// list.add(tocBaos.toByteArray());
	// list.add(igBaos.toByteArray());
	//
	// ByteArrayOutputStream igFinalBaos = new ByteArrayOutputStream();
	// Document igFinalDocument = new Document();
	// PdfWriter igFinalWriter = PdfWriter.getInstance(igFinalDocument,
	// igFinalBaos);
	// igFinalDocument.open();
	// PdfContentByte cb = igFinalWriter.getDirectContent();
	// for (byte[] in : list) {
	// PdfReader readerf = new PdfReader(in);
	// for (int i = 1; i <= readerf.getNumberOfPages(); i++) {
	// igFinalDocument.newPage();
	// PdfImportedPage page = igFinalWriter.getImportedPage(
	// readerf, i);
	// cb.addTemplate(page, 0, 0);
	// }
	// }
	// igFinalDocument.close();
	//
	// return new ByteArrayInputStream(igFinalBaos.toByteArray());
	// } catch (DocumentException | IOException e) {
	// e.printStackTrace();
	// return new NullInputStream(1L);
	// }
	// }

	@Override
	public void onChapter(PdfWriter writer, Document document,
			float paragraphPosition, Paragraph title) {
		this.pageByTitle.put(title.getContent(), writer.getPageNumber());
	}

	@Override
	public void onSection(PdfWriter writer, Document document,
			float paragraphPosition, int depth, Paragraph title) {
		this.pageByTitle.put(title.getContent(), writer.getPageNumber());
	}

	private String getRoman(int number) {
		String riman[] = { "M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L",
				"XL", "X", "IX", "V", "IV", "I" };
		int arab[] = { 1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9,
				5, 4, 1 };
		StringBuilder result = new StringBuilder();
		int i = 0;
		while (number > 0 || arab.length == (i - 1)) {
			while ((number - arab[i]) >= 0) {
				number -= arab[i];
				result.append(riman[i]);
			}
			i++;
		}
		return result.toString();
	}

	private ByteArrayOutputStream addPageNumber(ByteArrayOutputStream srcBaos,
			String target, String footer) {
		ByteArrayOutputStream dstBaos = new ByteArrayOutputStream();
		PdfReader reader;
		try {
			reader = new PdfReader(srcBaos.toByteArray());
			PdfStamper stamper = new PdfStamper(reader, dstBaos);
			int n = reader.getNumberOfPages();
			for (int i = 1; i <= n; i++) {
				addPageFooter(target, footer, i, n).writeSelectedRows(0, -1,
						34, 50, stamper.getOverContent(i));
			}
			stamper.close();
			reader.close();

		} catch (IOException | DocumentException e) {
			// Returns an empty bytestream in case of error
			e.printStackTrace();
		}
		return dstBaos;
	}

	private void addTocContent(Document tocDocument, PdfWriter igWriter,
			String title_) {
		try {
			// Create TOC
			final String title = title_;
			Chunk chunk = new Chunk(title).setLocalGoto(title);
			tocDocument.add(new Paragraph(chunk));
			// Add a placeholder for the page reference
			tocDocument.add(new VerticalPositionMark() {
				@Override
				public void draw(PdfContentByte canvas, float llx, float lly,
						float urx, float ury, float y) {
					final PdfTemplate createTemplate = canvas.createTemplate(
							50, 50);
					ProfileServiceImpl.this.tocPlaceholder.put(title,
							createTemplate);
					canvas.addTemplate(createTemplate, urx - 50, y);
				}
			});

			// Create page numbers
			BaseFont baseFont;
			PdfTemplate template = this.tocPlaceholder.get(title);
			template.beginText();

			baseFont = BaseFont.createFont();
			template.setFontAndSize(baseFont, 11);
			template.setTextMatrix(
					20 - baseFont.getWidthPoint(
							String.valueOf(igWriter.getPageNumber()), 12), 0);
			template.showText(String.valueOf(igWriter.getPageNumber()));
			template.endText();

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}

	private PdfPTable addPageFooter(String target, String footer, int x, int y) {
		PdfPTable table = new PdfPTable(2);
		table.setTotalWidth(527);
		table.setLockedWidth(true);
		table.getDefaultCell().setFixedHeight(20);
		table.getDefaultCell().setBorder(Rectangle.TOP);
		table.addCell(footer);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		if (target.equalsIgnoreCase("ig")) {
			table.addCell(String.format("Page %d of %d", x, y));
		} else {
			table.addCell(getRoman(x));
		}
		return table;
	}

	private PdfPTable addHeaderPdfTable(List<String> headers,
			float[] columnWidths, Font headerFont, BaseColor headerColor) {
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

	private void addCellsPdfTable(PdfPTable table, List<List<String>> rows,
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

	private void addGroupPdf1(List<List<String>> rows, Group g, Integer depth,
			Segments segments, Datatypes datatypes) {
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
				this.addSegmentPdf1(rows, (SegmentRef) srog, depth + 1,
						segments);
			} else if (srog instanceof Group) {
				this.addGroupPdf1(rows, (Group) srog, depth + 1, segments,
						datatypes);
			}
		}

		row = Arrays.asList(indent + "]", "", "", "", "", "END " + g.getName()
				+ " GROUP");
		rows.add(row);
	}

	private void addGroupPdf2(Document igDocument, PdfWriter igWriter,
			Document tocDocument, List<String> header, float[] columnWidths,
			Group g, Font headerFont, BaseColor headerColor, Font cellFont,
			BaseColor cpColor, Segments segments, Datatypes datatypes,
			Tables tables) throws DocumentException {

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentPdf2(igDocument, igWriter, tocDocument, header,
						columnWidths, (SegmentRef) srog, headerFont,
						headerColor, cellFont, cpColor, segments, datatypes,
						tables);
			} else if (srog instanceof Group) {
				this.addGroupPdf2(igDocument, igWriter, tocDocument, header,
						columnWidths, (Group) srog, headerFont, headerColor,
						cellFont, cpColor, segments, datatypes, tables);
			}
		}
	}

	private void addGroupXlsx2(List<String> header, Group g,
			XSSFWorkbook workbook, XSSFCellStyle headerStyle,
			Segments segments, Datatypes datatypes, Tables tables)
			throws DocumentException {

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentXlsx2(
						segments.findOne(((SegmentRef) srog).getRef()), header,
						workbook, headerStyle, datatypes, tables);
			} else if (srog instanceof Group) {
				this.addGroupXlsx2(header, (Group) srog, workbook, headerStyle,
						segments, datatypes, tables);
			}
		}
	}

	private void addSegmentXlsx2(Segment s, List<String> header,
			XSSFWorkbook workbook, XSSFCellStyle headerStyle,
			Datatypes datatypes, Tables tables) {
		List<List<String>> rows = new ArrayList<List<String>>();
		XSSFSheet sheet = workbook.createSheet(s.getName());
		rows.add(header);
		this.addFieldPdf2(rows, s, Boolean.FALSE, datatypes, tables);
		this.writeToSheet(rows, header, sheet, headerStyle);
	}

	private void addGroupXlsx(List<List<String>> rows, Group g, Integer depth,
			Segments segments, Datatypes datatypes) {
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
				this.addSegmentXlsx(rows, (SegmentRef) srog, depth + 1,
						segments);
			} else if (srog instanceof Group) {
				this.addGroupXlsx(rows, (Group) srog, depth + 1, segments,
						datatypes);
			}
		}
		row = Arrays.asList(indent + "END " + g.getName() + " GROUP", "", "",
				"");
		rows.add(row);
	}

	private void addSegmentPdf1(List<List<String>> rows, SegmentRef s,
			Integer depth, Segments segments) {
		String indent = StringUtils.repeat(" ", 4 * depth);
		Segment segment = segments.findOne(s.getRef());
		List<String> row = Arrays.asList(indent + segment.getName(), s
				.getUsage().value(), "", "[" + String.valueOf(s.getMin())
				+ ".." + String.valueOf(s.getMax()) + "]", "", segment
				.getComment() == null ? "" : segment.getComment());
		rows.add(row);
	}

	private void addSegmentPdf2(Document igDocument, PdfWriter igWriter,
			Document tocDocument, List<String> header, float[] columnWidths,
			SegmentRef segRef, Font headerFont, BaseColor headerColor,
			Font cellFont, BaseColor cpColor, Segments segments,
			Datatypes datatypes, Tables tables) throws DocumentException {

		PdfPTable table = this.addHeaderPdfTable(header, columnWidths,
				headerFont, headerColor);
		ArrayList<List<String>> rows = new ArrayList<List<String>>();

		Segment s = segments.findOne(segRef.getRef());

		this.addTocContent(tocDocument, igWriter,
				s.getName() + " - " + s.getDescription());

		igDocument.add(new Paragraph(s.getName() + ": " + s.getDescription()
				+ " Segment"));
		igDocument.add(Chunk.NEWLINE);
		igDocument.add(new Paragraph(s.getText1()));
		this.addFieldPdf2(rows, s, Boolean.TRUE, datatypes, tables);
		this.addCellsPdfTable(table, rows, cellFont, cpColor);
		igDocument.add(table);
		igDocument.add(Chunk.NEWLINE);
		igDocument.add(new Paragraph(s.getText2()));
		igDocument.add(Chunk.NEWLINE);

		List<Field> fieldsList = s.getFields();
		Collections.sort(fieldsList);
		for (Field f : fieldsList) {
			if (f.getText() != null && f.getText().length() != 0) {
				Font fontbold = FontFactory.getFont("Times-Roman", 12,
						Font.BOLD);
				igDocument.add(new Paragraph(s.getName() + "-"
						+ f.getItemNo().replaceFirst("^0+(?!$)", "") + " "
						+ f.getName() + " ("
						+ datatypes.findOne(f.getDatatype()).getLabel() + ")",
						fontbold));
				igDocument.add(new Paragraph(f.getText()));
			}
		}
		igDocument.newPage();

	}

	private void addSegmentXlsx(List<List<String>> rows, SegmentRef s,
			Integer depth, Segments segments) {
		String indent = StringUtils.repeat(" ", 4 * depth);
		Segment segment = segments.findOne(s.getRef());
		List<String> row = Arrays.asList(indent + segment.getName(), s
				.getUsage().value(), "", "[" + String.valueOf(s.getMin())
				+ ".." + String.valueOf(s.getMax()) + "]", "", segment
				.getComment() == null ? "" : segment.getComment());
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

	private void addComponentPdf2(List<List<String>> rows, Datatype d,
			Datatypes datatypes, Tables tables) {
		List<String> row;
		List<Predicate> predicates = d.getPredicates();
		List<ConformanceStatement> conformanceStatements = d
				.getConformanceStatements();

		List<Component> componentsList = new ArrayList<>(d.getComponents());
		Collections.sort(componentsList);
		if (componentsList.size() == 0) {
			row = Arrays.asList("1", d.getName(), "", "", "", "", "",
					d.getComment());
			rows.add(row);
		} else {
			for (Component c : componentsList) {
				row = Arrays.asList(
						c.getPosition().toString(),
						c.getName(),
						c.getConfLength(),
						datatypes.findOne(c.getDatatype()).getLabel(),
						c.getUsage().value(),
						"[" + String.valueOf(c.getMinLength()) + ","
								+ String.valueOf(c.getMaxLength()) + "]",
						(c.getTable() == null) ? "" : tables.findOne(
								c.getTable()).getMappingId(), c.getComment());
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
						row = Arrays.asList("", constraintType,
								constraint.getDescription());
						rows.add(row);
					}
				}
			}
		}
	}

	private void addFieldPdf2(List<List<String>> rows, Segment s,
			Boolean inlineConstraints, Datatypes datatypes, Tables tables) {
		List<String> row;
		List<Predicate> predicates = s.getPredicates();
		List<ConformanceStatement> conformanceStatements = s
				.getConformanceStatements();

		List<Field> fieldsList = s.getFields();
		Collections.sort(fieldsList);
		for (Field f : fieldsList) {
			row = Arrays.asList(
					// f.getItemNo().replaceFirst("^0+(?!$)", ""),
					String.valueOf(f.getPosition()),
					f.getName(),
					datatypes.findOne(f.getDatatype()).getLabel(),
					f.getUsage().value(),
					"",
					"[" + String.valueOf(f.getMin()) + ".."
							+ String.valueOf(f.getMax()) + "]",
					"",
					"[" + String.valueOf(f.getMinLength()) + ".."
							+ String.valueOf(f.getMaxLength()) + "]",
					(f.getTable() == null) ? "" : tables.findOne(f.getTable())
							.getMappingId(), f.getComment());
			rows.add(row);

			if (inlineConstraints) {
				List<Constraint> constraints = this.findConstraints(
						fieldsList.indexOf(f) + 1, predicates,
						conformanceStatements);
				this.addConstraints(rows, constraints);
			}
		}
		if (!inlineConstraints) {
			for (Field f : fieldsList) {
				List<Constraint> constraints = this.findConstraints(
						fieldsList.indexOf(f) + 1, predicates,
						conformanceStatements);
				this.addConstraints(rows, constraints);
			}
		}
	}

	private void addConstraints(List<List<String>> rows,
			List<Constraint> constraints) {
		if (!constraints.isEmpty()) {
			List<String> row;
			for (Constraint constraint : constraints) {
				String constraintType = new String();
				if (constraint instanceof Predicate) {
					constraintType = "Condition Predicate";
				} else if (constraint instanceof ConformanceStatement) {
					constraintType = "Conformance Statement";
				}
				row = Arrays.asList("", constraintType,
						constraint.getDescription());
				rows.add(row);
			}
		}
	}

	private void addCodesPdf2(List<List<String>> rows, Table t) {
		List<String> row;
		List<Code> codes = t.getCodes();

		for (Code c : codes) {
			row = Arrays.asList(c.getCode(), c.getLabel());
			rows.add(row);
		}

	}

	public ProfileRepository getProfileRepository() {
		return profileRepository;
	}

	public void setProfileRepository(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	@Override
	public Profile apply(Profile p) throws ProfileSaveException {
		// List<ProfilePropertySaveError> errors = new ProfileChangeService()
		// .apply(newProfile, oldProfile, newValues);
		// if (errors != null && !errors.isEmpty()) {
		// throw new ProfileSaveException(errors);
		// } else {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		p.getMetaData().setDate(
				dateFormat.format(Calendar.getInstance().getTime()));
		profileRepository.save(p);
		// }
		return p;
	}

}