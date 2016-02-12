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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ElementChange;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.HL7Version;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Messages;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SchemaVersion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class ProfileDiffImpl {

	List<ElementChange> profileInfoChanges;
	List<ElementChange> metadataChanges;
	List<ElementChange> messagesChanges;
	List<ElementChange> segrefOrGroupChanges;
	List<ElementChange> segmentsChanges;
	List<ElementChange> fieldsChanges;
	List<ElementChange> datatypesChanges;
	List<ElementChange> componentsChanges;
	List<ElementChange> tablesChanges;
	List<ElementChange> codesChanges;
	List<ElementChange> confStatementChanges;
	List<ElementChange> predicatesChanges;

	Map<String, List<ElementChange>> data; // iterable data set

	public ProfileDiffImpl() {
		super();
		this.profileInfoChanges = new ArrayList<ElementChange>();
		this.metadataChanges = new ArrayList<ElementChange>();
		this.messagesChanges = new ArrayList<ElementChange>();
		this.segrefOrGroupChanges = new ArrayList<ElementChange>();
		this.segmentsChanges = new ArrayList<ElementChange>();
		this.fieldsChanges = new ArrayList<ElementChange>();
		this.datatypesChanges = new ArrayList<ElementChange>();
		this.componentsChanges = new ArrayList<ElementChange>();
		this.tablesChanges = new ArrayList<ElementChange>();
		this.codesChanges = new ArrayList<ElementChange>();
		this.predicatesChanges = new ArrayList<ElementChange>();
		this.confStatementChanges = new ArrayList<ElementChange>();

		this.data = new HashMap<String, List<ElementChange>>();
		this.data.put("ProfileInfo", getProfileInfoChanges());
		this.data.put("MetaData", getMetadataChanges());
		this.data.put("Messages", getMessagesChanges());
		this.data.put("SegRefOrGroup", getSegrefOrGroupChanges());
		this.data.put("Segments", getSegmentsChanges());
		this.data.put("Fields", getFieldsChanges());
		this.data.put("Datatypes", getDatatypesChanges());
		this.data.put("Tables", getTablesChanges());
		this.data.put("Codes", getCodesChanges());
		this.data.put("Components", getComponentsChanges());
		this.data.put("Predicates", getPredicatesChanges());
		this.data.put("ConformanceStatements", getConfStatementChanges());
	}

	public InputStream diffToJson(Profile p1, Profile p2) {

		this.compare(p1, p2);

		try {
			// Create temporary file
			File tmpJsonFile = File.createTempFile("diffTmp", ".json");

			// Generate json file
			JsonFactory factory = new JsonFactory();
			JsonGenerator generator = factory.createGenerator(new FileWriter(
					tmpJsonFile));

			generator.writeStartObject();
			generator.writeArrayFieldStart("diff");

			for (String type : this.data.keySet()) {
				addSectionToJson(generator, type, data.get(type));
			}

			generator.writeEndArray();
			generator.writeEndObject();

			generator.close();

			return FileUtils.openInputStream(tmpJsonFile);
		} catch (IOException e) {
			return new NullInputStream(1L);
		}
	}

	public Map<String, List<ElementChange>> diff(Profile p1, Profile p2) {
		this.compare(p1, p2);
		return this.data;
	}

	public InputStream diffToPdf(Profile p1, Profile p2) {
		try {
			// Compare Profiles
			this.compare(p1, p2);

			// Serialize delta
			File tmpXmlFile = File.createTempFile("ProfileTemp", ".xml");
			String stringDiff = (new ProfileDiffSerializationImpl(p1, p2, this))
					.serializeDiffToXML();
			FileUtils.writeStringToFile(tmpXmlFile, stringDiff,
					Charset.forName("UTF-8"));

			// Apply stylesheet
			File tmpHtmlFile = File.createTempFile("DiffTemp", ".html");
			Builder builder = new Builder();
			nu.xom.Document input = builder.build(tmpXmlFile);
			nu.xom.Document stylesheet = builder.build(this.getClass()
					.getResourceAsStream("/rendering/profile2b.xsl"));
			XSLTransform transform = new XSLTransform(stylesheet);
			transform.setParameter("inlineConstraints", "false");
			Nodes output = transform.transform(input);
			nu.xom.Document result = XSLTransform.toDocument(output);
			FileUtils.writeStringToFile(tmpHtmlFile, result.toXML());

			// Convert html document to pdf
			com.itextpdf.text.Document document = new com.itextpdf.text.Document();
			File tmpPdfFile = File.createTempFile("DiffTemp", ".pdf");
			PdfWriter writer = PdfWriter.getInstance(document,
					FileUtils.openOutputStream(tmpPdfFile));
			document.open();
			XMLWorkerHelper.getInstance().parseXHtml(writer, document,
					FileUtils.openInputStream(tmpHtmlFile));
			document.close();

			return FileUtils.openInputStream(tmpPdfFile);
		} catch (IOException | DocumentException | ParsingException
				| XSLException e) {
			return new NullInputStream(1L);
		}
	}

	public InputStream diffToPdf2(Profile p1, Profile p2) {

		this.compare(p1, p2);

		Font titleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.UNDERLINE
						| Font.BOLD | Font.NORMAL, BaseColor.BLACK);
		Font eltNameFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 13, Font.UNDERLINE
						| Font.NORMAL, BaseColor.BLACK);
		Font fieldFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.ITALIC,
				BaseColor.BLACK);
		Font valueFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 11, Font.NORMAL,
				BaseColor.BLACK);

		try {
			Document deltaDocument = new Document(PageSize.A4);

			// Create temporary file
			File tmpFile = File.createTempFile("DiffTemp", ".pdf");

			PdfWriter.getInstance(deltaDocument, new FileOutputStream(tmpFile));
			deltaDocument.setPageSize(PageSize.A4);
			deltaDocument.setMargins(36f, 36f, 36f, 36f); // 72pt = 1 inch

			deltaDocument.open();

			deltaDocument.add(new Paragraph("Changes in profile information",
					titleFont));
			if (this.profileInfoChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.profileInfoChanges) {
				for (String field : ec.getChange().keySet()) {
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph("Field: " + field,
							fieldFont));
					deltaDocument.add(new Paragraph("Base value: "
							+ ec.getChange().get(field).get("basevalue"),
							valueFont));
					deltaDocument.add(new Paragraph("New value: "
							+ ec.getChange().get(field).get("newvalue"),
							valueFont));
				}
			}

			deltaDocument.add(new Paragraph("Changes in metadata", titleFont));
			if (this.metadataChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.metadataChanges) {
				for (String field : ec.getChange().keySet()) {
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph("Field: " + field,
							fieldFont));
					deltaDocument.add(new Paragraph("Base value: "
							+ ec.getChange().get(field).get("basevalue"),
							valueFont));
					deltaDocument.add(new Paragraph("New value: "
							+ ec.getChange().get(field).get("newvalue"),
							valueFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in messages", titleFont));
			if (this.messagesChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.messagesChanges) {
				if (ec.getChangeType().equals("edit")) {
					Message m = p1.getMessages().findOne(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(m.getDescription()
							+ " edited", eltNameFont));
					for (String field : ec.getChange().keySet()) {
						deltaDocument.add(Chunk.NEWLINE);
						deltaDocument.add(new Paragraph("Field: " + field,
								fieldFont));
						deltaDocument.add(new Paragraph("Base value: "
								+ ec.getChange().get(field).get("basevalue"),
								valueFont));
						deltaDocument.add(new Paragraph("New value: "
								+ ec.getChange().get(field).get("newvalue"),
								valueFont));
					}
				}
				if (ec.getChangeType().equals("del")) {
					deltaDocument.add(Chunk.NEWLINE);
					Message m = p1.getMessages().findOne(ec.getId());
					deltaDocument.add(new Paragraph(m.getId() + " deleted",
							eltNameFont));
				}
				if (ec.getChangeType().equals("add")) {
					Message m = p2.getMessages().findOne(ec.getId());
					deltaDocument.add(new Paragraph(m.getId() + " added",
							eltNameFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in segments and groups",
					titleFont));
			if (this.segrefOrGroupChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.segrefOrGroupChanges) {
				if (ec.getChangeType().equals("edit")) {
					SegmentRefOrGroup srog = p1.getMessages()
							.findOneSegmentRefOrGroup(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					if (srog instanceof SegmentRef) {
						Segment s = p1.getSegments().findOneSegmentById(
								((SegmentRef) srog).getRef());
						deltaDocument.add(new Paragraph(s.getLabel()
								+ " edited", eltNameFont));

					} else if (srog instanceof SegmentRef) {
						deltaDocument.add(new Paragraph(srog.getComment()
								+ " edited", eltNameFont));
					}
					for (String field : ec.getChange().keySet()) {
						deltaDocument.add(Chunk.NEWLINE);
						deltaDocument.add(new Paragraph("Field: " + field,
								fieldFont));
						deltaDocument.add(new Paragraph("Base value: "
								+ ec.getChange().get(field).get("basevalue"),
								valueFont));
						deltaDocument.add(new Paragraph("New value: "
								+ ec.getChange().get(field).get("newvalue"),
								valueFont));
					}
				}
				if (ec.getChangeType().equals("del")) {
					deltaDocument.add(Chunk.NEWLINE);
					SegmentRefOrGroup srog = p1.getMessages()
							.findOneSegmentRefOrGroup(ec.getId());
					deltaDocument.add(new Paragraph(srog.getId() + " deleted",
							eltNameFont));
				}
				if (ec.getChangeType().equals("add")) {
					SegmentRefOrGroup srog = p2.getMessages()
							.findOneSegmentRefOrGroup(ec.getId());
					deltaDocument.add(new Paragraph(srog.getId() + " added",
							eltNameFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in segments", titleFont));
			if (this.segmentsChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.segmentsChanges) {
				if (ec.getChangeType().equals("edit")) {
					Segment s = p1.getSegments().findOneSegmentById(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(s.getLabel() + " edited",
							eltNameFont));
					for (String field : ec.getChange().keySet()) {
						deltaDocument.add(Chunk.NEWLINE);
						deltaDocument.add(new Paragraph("Field: " + field,
								fieldFont));
						deltaDocument.add(new Paragraph("Base value: "
								+ ec.getChange().get(field).get("basevalue"),
								valueFont));
						deltaDocument.add(new Paragraph("New value: "
								+ ec.getChange().get(field).get("newvalue"),
								valueFont));
					}
				}
				if (ec.getChangeType().equals("del")) {
					deltaDocument.add(Chunk.NEWLINE);
					Segment s = p1.getSegments().findOneSegmentById(ec.getId());
					deltaDocument.add(new Paragraph(s.getLabel() + " deleted",
							eltNameFont));
				}
				if (ec.getChangeType().equals("add")) {
					Segment s = p2.getSegments().findOneSegmentById(ec.getId());
					deltaDocument.add(new Paragraph(s.getLabel() + " added",
							eltNameFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in fields", titleFont));
			if (this.fieldsChanges.isEmpty()) {
				deltaDocument.add(new Paragraph("No changes", valueFont));
			}
			for (ElementChange ec : this.fieldsChanges) {
				if (ec.getChangeType().equals("edit")) {
					Field f = p1.getSegments().findOneField(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(f.getName() + " edited",
							eltNameFont));
					for (String field : ec.getChange().keySet()) {
						deltaDocument.add(Chunk.NEWLINE);
						deltaDocument.add(new Paragraph("Field: " + field,
								fieldFont));
						deltaDocument.add(new Paragraph("Base value: "
								+ ec.getChange().get(field).get("basevalue"),
								valueFont));
						deltaDocument.add(new Paragraph("New value: "
								+ ec.getChange().get(field).get("newvalue"),
								valueFont));
					}
				}
				if (ec.getChangeType().equals("del")) {
					Field f = p1.getSegments().findOneField(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(f.getName() + " deleted",
							eltNameFont));
				}
				if (ec.getChangeType().equals("add")) {
					Field f = p2.getSegments().findOneField(ec.getId());
					deltaDocument.add(new Paragraph(f.getName() + " added",
							eltNameFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in datatypes", titleFont));
			if (this.datatypesChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.datatypesChanges) {
				if (ec.getChangeType().equals("edit")) {
					Datatype d = p1.getDatatypes().findOne(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(d.getLabel() + " edited",
							eltNameFont));
					for (String field : ec.getChange().keySet()) {
						deltaDocument.add(Chunk.NEWLINE);
						deltaDocument.add(new Paragraph("Field: " + field,
								fieldFont));
						deltaDocument.add(new Paragraph("Base value: "
								+ ec.getChange().get(field).get("basevalue"),
								valueFont));
						deltaDocument.add(new Paragraph("New value: "
								+ ec.getChange().get(field).get("newvalue"),
								valueFont));
					}
				}
				if (ec.getChangeType().equals("del")) {
					Datatype d = p1.getDatatypes().findOne(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(d.getLabel() + " deleted",
							eltNameFont));
				}
				if (ec.getChangeType().equals("add")) {
					Datatype d = p1.getDatatypes().findOne(ec.getId());
					deltaDocument.add(new Paragraph(d.getLabel() + " added",
							eltNameFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument
					.add(new Paragraph("Changes in components", titleFont));
			if (this.componentsChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.componentsChanges) {
				if (ec.getChangeType().equals("edit")) {
					Component c = p1.getDatatypes()
							.findOneComponent(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(c.getName() + " edited",
							eltNameFont));
					for (String field : ec.getChange().keySet()) {
						deltaDocument.add(Chunk.NEWLINE);
						deltaDocument.add(new Paragraph("Field: " + field,
								fieldFont));
						deltaDocument.add(new Paragraph("Base value: "
								+ ec.getChange().get(field).get("basevalue"),
								valueFont));
						deltaDocument.add(new Paragraph("New value: "
								+ ec.getChange().get(field).get("newvalue"),
								valueFont));
					}
				}
				if (ec.getChangeType().equals("del")) {
					Component c = p1.getDatatypes()
							.findOneComponent(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(c.getName() + " deleted",
							eltNameFont));
				}
				if (ec.getChangeType().equals("add")) {
					Component c = p1.getDatatypes()
							.findOneComponent(ec.getId());
					deltaDocument.add(Chunk.NEWLINE);
					deltaDocument.add(new Paragraph(c.getName() + " added",
							eltNameFont));
				}
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in tables", titleFont));
			if (this.tablesChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.tablesChanges) {
				System.out.println(ec.toString());
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph("Changes in codes", titleFont));
			if (this.codesChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.codesChanges) {
				System.out.println(ec.toString());
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument.add(new Paragraph(
					"Changes in conformance statements", titleFont));
			if (this.confStatementChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.confStatementChanges) {
				System.out.println(ec.toString());
			}

			deltaDocument.add(Chunk.NEWLINE);
			deltaDocument
					.add(new Paragraph("Changes in predicates", titleFont));
			if (this.predicatesChanges.isEmpty())
				deltaDocument.add(new Paragraph("No changes", valueFont));
			for (ElementChange ec : this.predicatesChanges) {
				System.out.println(ec.toString());
			}

			deltaDocument.close();

			return FileUtils.openInputStream(tmpFile);

		} catch (DocumentException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	private void addSectionToJson(JsonGenerator generator, String type,
			List<ElementChange> changes) throws IOException {
		generator.writeStartObject();
		generator.writeArrayFieldStart(type);
		for (ElementChange ec : changes) {
			ec.addToJson(generator);
		}
		generator.writeEndArray();
		generator.writeEndObject();
	}

	public void print(Profile p1, Profile p2) {
		this.compare(p1, p2);

		System.out.println("Diff profile info");
		if (this.profileInfoChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.profileInfoChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("Diff metadata");
		if (this.metadataChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.metadataChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("Diff messages");
		if (this.messagesChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.messagesChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff segrefs");
		if (this.segrefOrGroupChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.segrefOrGroupChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff segments");
		if (this.segmentsChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.segmentsChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Fields");
		if (this.fieldsChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.fieldsChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Datatypes");
		if (this.datatypesChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.datatypesChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Components");
		if (this.componentsChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.componentsChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Tables");
		if (this.tablesChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.tablesChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Codes");
		if (this.codesChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.codesChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Confstatements");
		if (this.confStatementChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.confStatementChanges) {
			System.out.println(ec.toString());
		}

		System.out.println("\nDiff Predicates");
		if (this.predicatesChanges.isEmpty())
			System.out.println("No changes");
		for (ElementChange ec : this.predicatesChanges) {
			System.out.println(ec.toString());
		}

	}

	private void compareProfileInfo(Profile p1, Profile p2) {
		ElementChange ec = new ElementChange(p1.getId(), p1.getType());
		if (p1.getAccountId() != null & p2.getAccountId() != null)
			if (!(p1.getAccountId().equals(p2.getAccountId()))) {
				ec.recordChange("accountId", p1.getAccountId().toString(), p2
						.getAccountId().toString());
			}
		if (!(p1.getComment().equals(p2.getComment()))) {
			ec.recordChange("comment", p1.getComment(), p2.getComment());
		}
		if (!(p1.getUsageNote().equals(p2.getUsageNote()))) {
			ec.recordChange("usageNote", p1.getUsageNote(), p2.getUsageNote());
		}
		if (ec.countChanges() != 0) {
			ec.setChangeType("edit");
			this.profileInfoChanges.add(ec);
		}
	}

	private void compareMetaData(ProfileMetaData metadata,
			ProfileMetaData metadata2) {
		ElementChange ec = new ElementChange(metadata.getName(),
				"profileMetaData");
		if (!metadata.getName().equals(metadata2.getName())) {
			ec.recordChange("name", metadata.getName(), metadata2.getName());
		}
		if (!metadata.getHl7Version().equals(metadata2.getHl7Version())) {
			ec.recordChange("hl7Version", metadata.getHl7Version(),
					metadata2.getHl7Version());
		}
		if (!metadata.getSchemaVersion().equals(metadata2.getSchemaVersion())) {
			ec.recordChange("version", metadata.getSchemaVersion(),
					metadata2.getSchemaVersion());
		}
		if (!metadata.getOrgName().equals(metadata2.getOrgName())) {
			ec.recordChange("orgName", metadata.getOrgName(),
					metadata2.getOrgName());
		}
		if (!metadata.getStatus().equals(metadata2.getStatus())) {
			ec.recordChange("status", metadata.getStatus(),
					metadata2.getStatus());
		}
		if (!metadata.getTopics().equals(metadata2.getTopics())) {
			ec.recordChange("schemaTopics", metadata.getTopics(),
					metadata2.getTopics());
		}
		if (!metadata.getSubTitle().equals(metadata2.getSubTitle())) {
			ec.recordChange("subtitle", metadata.getSubTitle(),
					metadata2.getSubTitle());
		}
		if (!metadata.getVersion().equals(metadata2.getVersion())) {
			ec.recordChange("version", metadata.getVersion(),
					metadata2.getVersion());
		}
		if (!metadata.getDate().equals(metadata2.getDate())) {
			ec.recordChange("date", metadata.getDate(), metadata2.getDate());
		}
		if (!metadata.getExt().equals(metadata2.getExt())) {
			ec.recordChange("ext", metadata.getExt(), metadata2.getExt());
		}
		for (String enc : metadata.getEncodings()) {
			if (!(metadata2.getEncodings().contains(enc))) {
				ec.recordChange("encodings", enc, "");
			}
		}
		for (String enc : metadata2.getEncodings()) {
			if (!(metadata.getEncodings().contains(enc))) {
				ec.recordChange("encodings", "", enc);
			}
		}

		if (ec.countChanges() != 0) {
			ec.setChangeType("edit");
			this.metadataChanges.add(ec);
		}

	}

	private void compareMessageDefinition(Messages mlib1, Messages mlib2) {
		for (Message m : mlib1.getChildren()) {
			ElementChange ec = new ElementChange(m.getId(), mlib1.getId());

			if (mlib2.findOne(m.getId()) == null) {
				ec.recordChange("deleted", m.getDescription(), "");
				ec.setChangeType("del");
			} else {
				Message m2 = mlib2.findOne(m.getId());

				if (!(m.getEvent().equals(m2.getEvent()))) {
					ec.recordChange("Event", m.getEvent(), m2.getEvent());
				}
				if (!(m.getDescription().equals(m2.getDescription()))) {
					ec.recordChange("Descriptiom", m.getDescription(),
							m2.getDescription());
				}
				if (!(m.getComment().equals(m2.getComment()))) {
					ec.recordChange("Comment", m.getComment(), m2.getComment());
				}
				if (!(m.getUsageNote().equals(m2.getUsageNote()))) {
					ec.recordChange("UsageNote", m.getUsageNote(),
							m2.getUsageNote());
				}
				ec.setChangeType("edit");
			}
			if (ec.countChanges() != 0)
				this.messagesChanges.add(ec);
		}
		for (Message m : mlib2.getChildren()) {
			if (mlib1.findOne(m.getId()) == null) {
				ElementChange ec = new ElementChange(m.getId(), mlib2.getId());
				ec.recordChange("added", "", m.getDescription());
				ec.setChangeType("add");
				this.messagesChanges.add(ec);
			}
		}
	}

	private void compareMessages(Messages mlib1, Messages mlib2,
			Segments slib1, Segments slib2) {
		for (Message m : mlib1.getChildren()) {
			for (SegmentRefOrGroup srog : m.getChildren()) {
				ElementChange ec = new ElementChange(srog.getId(), m.getId());
				if (mlib2.findOneSegmentRefOrGroup(srog.getId()) == null) {
					if (srog instanceof SegmentRef) {
						Segment s = slib1.findOneSegmentById(((SegmentRef) srog).getRef());
						ec.recordChange("deleted", s.getName(), "");
						ec.setChangeType("del");
					} else if (srog instanceof Group) {
						ec.recordChange("deleted", ((Group) srog).getName(), "");
						ec.setChangeType("del");
					}
				} else {
					SegmentRefOrGroup srog2 = mlib2
							.findOneSegmentRefOrGroup(srog.getId());
					if (!(srog.getMin().equals(srog2.getMin()))) {
						ec.recordChange("Min", srog.getMin().toString(), srog2
								.getMin().toString());
					}
					if (!(srog.getMax().equals(srog2.getMax()))) {
						ec.recordChange("Max", srog.getMax(), srog2.getMax());
					}
					if (!(srog.getUsage().equals(srog2.getUsage()))) {
						ec.recordChange("Usage", srog.getUsage().value(), srog
								.getUsage().value());
					}
					if (!(srog.getComment().equals(srog2.getComment()))) {
						ec.recordChange("Comment", srog.getComment(),
								srog2.getComment());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.segrefOrGroupChanges.add(ec);
			}
		}
		for (Message m : mlib2.getChildren()) {
			for (SegmentRefOrGroup srog : m.getChildren()) {
				ElementChange ec = new ElementChange(srog.getId(), m.getId());
				if (mlib1.findOneSegmentRefOrGroup(srog.getId()) == null) {
					if (srog instanceof SegmentRef) {
						Segment s = slib2.findOneSegmentById(((SegmentRef) srog).getRef());
						ec.recordChange("added", "", s.getName());
						ec.setChangeType("add");
					} else if (srog instanceof Group) {
						ec.recordChange("added", "", ((Group) srog).getName());
						ec.setChangeType("add");
					}
					this.segrefOrGroupChanges.add(ec);
				}
			}
		}
	}

	private void compareSegRef(SegmentRefOrGroup sr, Segments s1, Messages mlib2) {
		ElementChange ec = new ElementChange(sr.getId(), s1.getId());
		if (mlib2.findOneSegmentRefOrGroup(sr.getId()) == null) {
			Segment s = s1.findOneSegmentById(((SegmentRef) sr).getRef());
			ec.recordChange("deleted", s.getName(), "");
			ec.setChangeType("del");
		} else {
			SegmentRefOrGroup sr2 = mlib2.findOneSegmentRefOrGroup(sr.getId());
			if (!(sr.getMin().equals(sr2.getMin()))) {
				ec.recordChange("Min", sr.getMin().toString(), sr2.getMin()
						.toString());
			}
			if (!(sr.getMax().equals(sr2.getMax()))) {
				ec.recordChange("Max", sr.getMax(), sr2.getMax());
			}
			if (!(sr.getUsage().equals(sr2.getUsage()))) {
				ec.recordChange("Usage", sr.getUsage().value(), sr.getUsage()
						.value());
			}
			if (!(sr.getComment().equals(sr2.getComment()))) {
				ec.recordChange("Comment", sr.getComment(), sr2.getComment());
			}
			ec.setChangeType("edit");
		}
		if (ec.countChanges() != 0)
			this.segrefOrGroupChanges.add(ec);

		// TODO add added segrefs (case when srog had been added)
	}

	private void compareGroups(SegmentRefOrGroup g, Segments slib1,
			Messages mlib2) {
		ElementChange ec = new ElementChange(g.getId(), slib1.getId());
		if (mlib2.findOneSegmentRefOrGroup(g.getId()) == null) {
			ec.recordChange("deleted", ((Group) g).getName(), "");
			ec.setChangeType("del");
		} else {
			SegmentRefOrGroup sr2 = mlib2.findOneSegmentRefOrGroup(g.getId());
			if (!(g.getMin().equals(sr2.getMin()))) {
				ec.recordChange("Min", g.getMin().toString(), sr2.getMin()
						.toString());
			}
			if (!(g.getMax().equals(sr2.getMax()))) {
				ec.recordChange("Max", g.getMax().toString(), sr2.getMax()
						.toString());
			}
			if (!(g.getUsage().equals(sr2.getUsage()))) {
				ec.recordChange("Usage", g.getUsage().value(), sr2.getUsage()
						.value());
			}
			if (!(g.getComment().equals(sr2.getComment()))) {
				ec.recordChange("Comment", g.getComment(), sr2.getComment());
			}
			ec.setChangeType("edit");
		}
		if (ec.countChanges() != 0)
			this.segrefOrGroupChanges.add(ec);

		for (SegmentRefOrGroup srog : ((Group) g).getChildren()) {
			if (srog instanceof SegmentRef) {
				this.compareSegRef(srog, slib1, mlib2);
			} else {
				this.compareGroups(srog, slib1, mlib2);
			}
		}
	}

	private void compareSegments(Segments slib1, Segments slib2) {
		for (Segment s : slib1.getChildren()) {
			ElementChange ec = new ElementChange(s.getId(), slib1.getId());
			if (slib2.findOneSegmentById(s.getId()) == null) {
				ec.recordChange("deleted", s.getName(), "");
				ec.setChangeType("del");
			} else {
				Segment s2 = slib2.findOneSegmentById(s.getId());
				if (!(s.getDescription().equals(s2.getDescription()))) {
					ec.recordChange("Description", s.getDescription(),
							s2.getDescription());
				}
				if (!(s.getLabel().equals(s2.getLabel()))) {
					ec.recordChange("Label", s.getLabel(), s2.getLabel());
				}
				if (!(s.getText1().equals(s2.getText1()))) {
					ec.recordChange("Text", s.getText1(), s2.getText1());
				}
				if (!(s.getText2().equals(s2.getText2()))) {
					ec.recordChange("Comment", s.getText2(), s2.getText2());
				}
				ec.setChangeType("edit");
			}
			if (ec.countChanges() != 0)
				this.segmentsChanges.add(ec);

			for (ConformanceStatement cs : s.getConformanceStatements()) {
				ec = new ElementChange(cs.getId(), "confStat");

				if (slib2.findOneConformanceStatement(cs.getId()) == null) {
					System.out.println(cs.getDescription() + " deleted");
					ec.recordChange("deleted", cs.getDescription(), "");
					ec.setChangeType("del");
				} else {
					ConformanceStatement cs2 = slib2
							.findOneConformanceStatement(cs.getId());
					if (!cs.getAssertion().equals(cs2.getAssertion())) {
						ec.recordChange("Assertion", cs.getAssertion(),
								cs2.getAssertion());
					}
					if (!cs.getConstraintTarget().equals(
							cs2.getConstraintTarget())) {
						ec.recordChange("ConstraintTarget",
								cs.getConstraintTarget(),
								cs2.getConstraintTarget());
					}
					if (!cs.getDescription().equals(cs2.getDescription())) {
						ec.recordChange("Description", cs.getDescription(),
								cs2.getDescription());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.confStatementChanges.add(ec);

			}
			for (Predicate p : s.getPredicates()) {
				ec = new ElementChange(p.getId(), "predicate");
				if (slib2.findOnePredicate(p.getId()) == null) {
					ec.recordChange("deleted", p.getDescription(), "");
					ec.setChangeType("del");
				} else {
					Predicate p2 = slib2.findOnePredicate(p.getId());
					if (!p.getAssertion().equals(p2.getAssertion())) {
						ec.recordChange("Assertion", p.getAssertion(),
								p2.getAssertion());
					}
					if (!p.getConstraintTarget().equals(
							p2.getConstraintTarget())) {
						ec.recordChange("ConstraintTarget",
								p.getConstraintTarget(),
								p2.getConstraintTarget());
					}
					if (!p.getDescription().equals(p2.getDescription())) {
						ec.recordChange("Description", p.getDescription(),
								p2.getDescription());
					}
					if (!p.getTrueUsage().equals(p2.getTrueUsage())) {
						ec.recordChange("TrueUsage", p.getTrueUsage().value(),
								p2.getTrueUsage().value());
					}
					if (!p.getFalseUsage().equals(p2.getFalseUsage())) {
						ec.recordChange("FalseUsage",
								p.getFalseUsage().value(), p2.getFalseUsage()
										.value());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.predicatesChanges.add(ec);
			}

		}

		for (Segment s : slib2.getChildren()) {
			if (slib1.findOneSegmentById(s.getId()) == null) {
				ElementChange ec = new ElementChange(s.getId(), slib2.getId());
				ec.recordChange("added", "", s.getName());
				ec.setChangeType("add");
				this.segmentsChanges.add(ec);
			}
			for (ConformanceStatement cs : s.getConformanceStatements()) {
				if (slib1.findOneConformanceStatement(cs.getId()) == null) {
					ElementChange ec = new ElementChange(cs.getId(), "confStat");
					ec.recordChange("added", cs.getDescription(), "");
					ec.setChangeType("add");
					this.confStatementChanges.add(ec);
				}
			}
			for (Predicate p : s.getPredicates()) {
				if (slib1.findOnePredicate(p.getId()) == null) {
					ElementChange ec = new ElementChange(p.getId(), "predicate");
					ec.recordChange("added", p.getDescription(), "");
					ec.setChangeType("add");
					this.predicatesChanges.add(ec);
				}
			}
		}
	}

	private void compareFields(Segments slib1, Segments slib2) {
		for (Segment s : slib1.getChildren()) {
			for (Field f : s.getFields()) {
				ElementChange ec = new ElementChange(f.getId(), s.getId());

				if (slib2.findOneField(f.getId()) == null) {
					ec.recordChange("deleted", f.getName(), "");
					ec.setChangeType("del");
				} else {
					Field f2 = slib2.findOneField(f.getId());
					if (!f.getName().equals(f2.getName())) {
						ec.recordChange("Name", f.getName(), f2.getName());
					}
					if (!f.getUsage().equals(f2.getUsage())) {
						ec.recordChange("Usage", f.getUsage().value(), f2
								.getUsage().value());
					}
					if (!f.getDatatype().equals(f2.getDatatype())) {
						ec.recordChange("Datatype", f.getDatatype(),
								f2.getDatatype());
					}
					if (!f.getMin().equals(f2.getMin())) {
						ec.recordChange("Min", f.getMin().toString(), f2
								.getMin().toString());
					}
					if (!f.getMinLength().equals(f2.getMinLength())) {
						ec.recordChange("MinLength", f.getMinLength()
								.toString(), f2.getMinLength().toString());
					}
					if (!f.getMax().equals(f2.getMax())) {
						ec.recordChange("Max", f.getMax(), f2.getMax());
					}
					if (!f.getMaxLength().equals(f2.getMaxLength())) {
						ec.recordChange("MaxLength", f.getMaxLength(),
								f2.getMaxLength());
					}
					if (!f.getText().equals(f2.getText())) {
						ec.recordChange("Text", f.getText(), f2.getText());
					}
					if (!f.getComment().equals(f2.getComment())) {
						ec.recordChange("Comment", f.getComment(),
								f2.getComment());
					}
					if (!f.getConfLength().equals(f2.getConfLength())) {
						ec.recordChange("ConfLength", f.getConfLength(),
								f2.getConfLength());
					}
					if (f.getTable() != null & f2.getTable() != null) {
						if (!f.getTable().equals(f2.getTable())) {
							ec.recordChange("Table", f.getTable(),
									f2.getTable());
						}
					}
					if (f.getBindingLocation() != null
							& f2.getBindingLocation() != null) {
						if (!f.getBindingLocation().equals(
								f2.getBindingLocation())) {
							ec.recordChange("BindingLocation",
									f.getBindingLocation(),
									f2.getBindingLocation());
						}
					}
					if (f.getBindingStrength() != null
							& f2.getBindingStrength() != null) {
						if (!f.getBindingStrength().equals(
								f2.getBindingStrength())) {
							ec.recordChange("BindingStrength",
									f.getBindingStrength(),
									f.getBindingStrength());
						}
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.fieldsChanges.add(ec);

			}
		}
		for (Segment s : slib2.getChildren()) {
			for (Field f : s.getFields()) {
				if (slib1.findOneField(f.getId()) == null) {
					ElementChange ec = new ElementChange(f.getId(), s.getId());
					ec.recordChange("added", "", f.getName());
					ec.setChangeType("add");
					this.fieldsChanges.add(ec);
				}
			}
		}
	}

	private void compareDatatypes(Datatypes dlib1, Datatypes dlib2) {
		for (Datatype dt : dlib1.getChildren()) {
			ElementChange ec = new ElementChange(dt.getId(), dlib1.getId());
			if (dlib2.findOne(dt.getId()) == null) {
				ec.recordChange("deleted", dt.getName(), "");
				ec.setChangeType("del");
			} else {
				Datatype dt2 = dlib2.findOne(dt.getId());
				if (!(dt.getLabel().equals(dt2.getLabel()))) {
					ec.recordChange("Label", dt.getLabel(), dt2.getLabel());
				}
				if (!(dt.getName().equals(dt2.getName()))) {
					ec.recordChange("Name", dt.getName(), dt2.getName());
				}
				if (!(dt.getDescription().equals(dt2.getDescription()))) {
					ec.recordChange("Description", dt.getDescription(),
							dt2.getDescription());
				}
				if (!(dt.getComment().equals(dt2.getComment()))) {
					ec.recordChange("Comment", dt.getComment(),
							dt2.getComment());
				}
				if (!(dt.getUsageNote().equals(dt2.getUsageNote()))) {
					ec.recordChange("UsageNote", dt.getUsageNote(),
							dt2.getUsageNote());
				}
				ec.setChangeType("edit");
			}
			if (ec.countChanges() != 0)
				this.datatypesChanges.add(ec);

			for (ConformanceStatement cs : dt.getConformanceStatements()) {
				ec = new ElementChange(cs.getId(), "confStat");
				if (dlib2.findOneConformanceStatement(cs.getId()) == null) {
					ec.recordChange("deleted", cs.getDescription(), "");
					ec.setChangeType("del");
				} else {
					ConformanceStatement cs2 = dlib2
							.findOneConformanceStatement(cs.getId());
					if (!cs.getAssertion().equals(cs2.getAssertion())) {
						ec.recordChange("Assertion", cs.getAssertion(),
								cs2.getAssertion());
					}
					if (!cs.getConstraintTarget().equals(
							cs2.getConstraintTarget())) {
						ec.recordChange("ConstraintTarget",
								cs.getConstraintTarget(),
								cs2.getConstraintTarget());
					}
					if (!cs.getDescription().equals(cs2.getDescription())) {
						ec.recordChange("Description", cs.getDescription(),
								cs2.getDescription());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.confStatementChanges.add(ec);

			}
			for (Predicate p : dt.getPredicates()) {
				ec = new ElementChange(p.getId(), "predicate");
				if (dlib2.findOnePredicate(p.getId()) == null) {
					ec.recordChange("deleted", p.getDescription(), "");
					ec.setChangeType("del");
				} else {
					Predicate p2 = dlib2.findOnePredicate(p.getId());
					if (!p.getAssertion().equals(p2.getAssertion())) {
						ec.recordChange("Assertion", p.getAssertion(),
								p2.getAssertion());
					}
					if (!p.getConstraintTarget().equals(
							p2.getConstraintTarget())) {
						ec.recordChange("ConstraintTarget",
								p.getConstraintTarget(),
								p2.getConstraintTarget());
					}
					if (!p.getDescription().equals(p2.getDescription())) {
						ec.recordChange("Description", p.getDescription(),
								p2.getDescription());
					}
					if (!p.getTrueUsage().equals(p2.getTrueUsage())) {
						ec.recordChange("TrueUsage", p.getTrueUsage().value(),
								p2.getTrueUsage().value());
					}
					if (!p.getFalseUsage().equals(p2.getFalseUsage())) {
						ec.recordChange("FalseUsage",
								p.getFalseUsage().value(), p2.getFalseUsage()
										.value());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.predicatesChanges.add(ec);
			}
		}
		for (Datatype dt2 : dlib2.getChildren()) {
			if (dlib1.findOne(dt2.getId()) == null) {
				ElementChange ec = new ElementChange(dt2.getId(), dlib2.getId());
				ec.recordChange("added", "", dt2.getName());
				ec.setChangeType("add");
				this.datatypesChanges.add(ec);
			}
		}
	}

	private void compareComponents(Datatypes dlib1, Datatypes dlib2) {
		for (Datatype dt : dlib1.getChildren()) {
			for (Component c : dt.getComponents()) {
				ElementChange ec = new ElementChange(c.getId(), dt.getId());
				if (dlib2.findOneComponent(c.getId()) == null) {
					ec.recordChange("deleted", c.getName(), "");
					ec.setChangeType("del");
				} else {
					Component c2 = dlib2.findOneComponent(c.getId());

					if (!(c.getDatatype().equals(c2.getDatatype()))) {
						ec.recordChange("Datatype", c.getDatatype(),
								c2.getDatatype());
					}
					if (!(c.getName().equals(c2.getName()))) {
						ec.recordChange("Name", c.getName(), c2.getName());
					}
					if (!(c.getUsage().equals(c2.getUsage()))) {
						ec.recordChange("Usage", c.getUsage().value(), c2
								.getUsage().value());
					}
					if (!(c.getConfLength().equals(c2.getConfLength()))) {
						ec.recordChange("ConfLength", c.getConfLength(),
								c2.getConfLength());
					}
					if (!(c.getMinLength().equals(c2.getMinLength()))) {
						ec.recordChange("MinLength", c.getMinLength()
								.toString(), c2.getMinLength().toString());
					}
					if (!(c.getMaxLength().equals(c2.getMaxLength()))) {
						ec.recordChange("MaxLength", c.getMaxLength(),
								c2.getMaxLength());
					}
					if (!(c.getBindingLocation()
							.equals(c2.getBindingLocation()))) {
						ec.recordChange("BindingLocation",
								c.getBindingLocation(), c2.getBindingLocation());
					}
					if (!(c.getBindingStrength()
							.equals(c2.getBindingStrength()))) {
						ec.recordChange("BindingStrength",
								c.getBindingStrength(), c2.getBindingStrength());
					}
					if (c.getTable() != null & c2.getTable() != null) {
						if (!(c.getTable().equals(c2.getTable()))) {
							ec.recordChange("Table", c.getTable(),
									c2.getTable());
						}
					}
					if (!(c.getComment().equals(c2.getComment()))) {
						ec.recordChange("Comment", c.getComment(),
								c2.getComment());
					}
					if (!(c.getText().equals(c2.getText()))) {
						ec.recordChange("Text", c.getText(), c2.getText());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.componentsChanges.add(ec);
			}
		}
		for (Datatype dt2 : dlib2.getChildren()) {
			for (Component c : dt2.getComponents()) {
				if (dlib1.findOneComponent(c.getId()) == null) {
					ElementChange ec = new ElementChange(c.getId(), dt2.getId());
					ec.recordChange("added", "", c.getName());
					ec.setChangeType("add");
					this.componentsChanges.add(ec);
				}
			}
		}
	}

	private void compareTables(Tables tables, Tables tables2) {
		for (Table t : tables.getChildren()) {
			ElementChange ec = new ElementChange(t.getId(), tables.getId());
			if (tables2.findOneTableById(t.getId()) == null) {
				ec.recordChange("deleted", t.getName(), "");
				ec.setChangeType("del");
			} else {
				Table t2 = tables2.findOneTableById(t.getId());
				if (!(t.getName().equals(t2.getName()))) {
					ec.recordChange("Name", t.getName(), t2.getName());
				}
				ec.setChangeType("edit");
			}
			if (ec.countChanges() != 0)
				this.tablesChanges.add(ec);
		}
		for (Table t : tables2.getChildren()) {
			if (tables.findOneTableById(t.getId()) == null) {
				ElementChange ec = new ElementChange(t.getId(), tables2.getId());
				ec.recordChange("added", "", t.getName());
				ec.setChangeType("add");
				this.tablesChanges.add(ec);
			}
		}
	}

	private void compareCodes(Tables tables, Tables tables2) {
		for (Table t : tables.getChildren()) {
			for (Code c : t.getCodes()) {
				ElementChange ec = new ElementChange(c.getId(), t.getId());
				if (tables2.findOneCodeById(c.getId()) == null) {
					ec.recordChange("deleted", c.getLabel(), "");
					ec.setChangeType("del");
				} else {
					Code c2 = tables2.findOneCodeById(c.getId());
					if (!(c.getLabel().equals(c2.getLabel()))) {
						ec.recordChange("Label", c.getLabel(),
								c2.getLabel());
					}
					if (!(c.getCodeSystem().equals(c2.getCodeSystem()))) {
						ec.recordChange("CodeSys", c.getCodeSystem(),
								c2.getCodeSystem());
					}
					if (!(c.getValue().equals(c2.getValue()))) {
						ec.recordChange("Code", c.getValue(), c2.getValue());
					}
					ec.setChangeType("edit");
				}
				if (ec.countChanges() != 0)
					this.codesChanges.add(ec);
			}
		}
		for (Table t : tables2.getChildren()) {
			for (Code c : t.getCodes()) {
				if (tables.findOneCodeById(c.getId()) == null) {
					ElementChange ec = new ElementChange(c.getId(), t.getId());
					ec.recordChange("added", "", c.getLabel());
					ec.setChangeType("add");
					this.codesChanges.add(ec);
				}
			}
		}
	}

	public void compare(Profile p1, Profile p2) {

		this.clear();

		this.compareProfileInfo(p1, p2);
		this.compareMetaData(p1.getMetaData(), p2.getMetaData());
		this.compareMessageDefinition(p1.getMessages(), p2.getMessages());
		this.compareMessages(p1.getMessages(), p2.getMessages(),
				p1.getSegments(), p2.getSegments());
		this.compareSegments(p1.getSegments(), p2.getSegments()); // includes
																	// constraints
		this.compareFields(p1.getSegments(), p2.getSegments());
		this.compareDatatypes(p1.getDatatypes(), p2.getDatatypes()); // includes
																		// constraints
		this.compareComponents(p1.getDatatypes(), p2.getDatatypes());
		this.compareTables(p1.getTables(), p2.getTables());
		this.compareCodes(p1.getTables(), p2.getTables());
	}

	private void clear() {
		this.profileInfoChanges.clear();
		this.metadataChanges.clear();
		this.messagesChanges.clear();
		this.segrefOrGroupChanges.clear();
		this.segmentsChanges.clear();
		this.fieldsChanges.clear();
		this.datatypesChanges.clear();
		this.componentsChanges.clear();
		this.tablesChanges.clear();
		this.codesChanges.clear();
		this.predicatesChanges.clear();
		this.confStatementChanges.clear();
	}

	public ElementChange findOneByMessageId(String id) {
		if (this.messagesChanges != null)
			for (ElementChange s : this.messagesChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneBySegmentRefOrGroupParentId(String parentId) {
		if (this.segrefOrGroupChanges != null)
			for (ElementChange s : this.segrefOrGroupChanges) {
				if (s.getParent().equals(parentId)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneBySegmentRefOrGroupId(String id) {
		if (this.segrefOrGroupChanges != null)
			for (ElementChange s : this.segrefOrGroupChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneBySegmentId(String id) {
		if (this.segmentsChanges != null)
			for (ElementChange s : this.segmentsChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByFieldParentId(String parentId) {
		if (this.fieldsChanges != null)
			for (ElementChange s : this.fieldsChanges) {
				if (s.getParent().equals(parentId)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByCodeParentId(String parentId) {
		if (this.codesChanges != null)
			for (ElementChange s : this.codesChanges) {
				if (s.getParent().equals(parentId)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByComponentParentId(String parentId) {
		if (this.componentsChanges != null)
			for (ElementChange s : this.componentsChanges) {
				if (s.getParent().equals(parentId)) {
					return s;
				}
			}
		return null;
	}

	public List<ElementChange> findAddedSegmentRefOrGroups() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.segrefOrGroupChanges != null)
			for (ElementChange s : this.segrefOrGroupChanges) {
				if (s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedSegments() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.segmentsChanges != null)
			for (ElementChange s : this.segmentsChanges) {
				if (s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedFields() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.fieldsChanges != null)
			for (ElementChange s : this.fieldsChanges) {
				if (s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedFields() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.fieldsChanges != null)
			for (ElementChange s : this.fieldsChanges) {
				if (s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedSegments() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.segmentsChanges != null)
			for (ElementChange s : this.segmentsChanges) {
				if (s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findFieldsByParentIdAndChangeType(
			String parentId, String changeType) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		List<String> symbols = Arrays.asList(new String[] { "add", "del",
				"edit", "*" });
		if (symbols.contains(changeType)) {
			if (this.fieldsChanges != null) {
				if (changeType.equals("*")) {
					for (ElementChange s : this.fieldsChanges) {
						if (s.getParent().equals(parentId)) {
							rst.add(s);
						}
					}
				} else {
					for (ElementChange s : this.fieldsChanges) {
						if (s.getParent().equals(parentId)
								& s.getChangeType().equals(changeType)) {
							rst.add(s);
						}

					}
				}
			}
		}
		return rst;
	}

	public List<ElementChange> findComponentsByParentIdAndChangeType(
			String parentId, String changeType) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		List<String> symbols = Arrays.asList(new String[] { "add", "del",
				"edit", "*" });
		if (symbols.contains(changeType)) {
			if (this.componentsChanges != null) {
				if (changeType.equals("*")) {
					for (ElementChange s : this.componentsChanges) {
						if (s.getParent().equals(parentId)) {
							rst.add(s);
						}
					}
				} else {
					for (ElementChange s : this.componentsChanges) {
						if (s.getParent().equals(parentId)
								& s.getChangeType().equals(changeType)) {
							rst.add(s);
						}

					}
				}
			}
		}
		return rst;
	}

	public List<ElementChange> findCodesByParentIdAndChangeType(
			String parentId, String changeType) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		List<String> symbols = Arrays.asList(new String[] { "add", "del",
				"edit", "*" });
		if (symbols.contains(changeType)) {
			if (this.codesChanges != null) {
				if (changeType.equals("*")) {
					for (ElementChange s : this.codesChanges) {
						if (s.getParent().equals(parentId)) {
							rst.add(s);
						}
					}
				} else {
					for (ElementChange s : this.codesChanges) {
						if (s.getParent().equals(parentId)
								& s.getChangeType().equals(changeType)) {
							rst.add(s);
						}

					}
				}
			}
		}
		return rst;
	}

	public List<ElementChange> findAddedFieldsByParentId(String parentId) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.fieldsChanges != null)
			for (ElementChange s : this.fieldsChanges) {
				if (s.getParent().equals(parentId)
						& s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedFieldsByParentId(String parentId) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.fieldsChanges != null)
			for (ElementChange s : this.fieldsChanges) {
				if (s.getParent().equals(parentId)
						& s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedComponentsByParentId(String parentId) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.componentsChanges != null)
			for (ElementChange s : this.componentsChanges) {
				if (s.getParent().equals(parentId)
						& s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedComponentsByParentId(String parentId) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.componentsChanges != null)
			for (ElementChange s : this.componentsChanges) {
				if (s.getParent().equals(parentId)
						& s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedCodesByParentId(String parentId) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.codesChanges != null)
			for (ElementChange s : this.codesChanges) {
				if (s.getParent().equals(parentId)
						& s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedCodesByParentId(String parentId) {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.codesChanges != null)
			for (ElementChange s : this.codesChanges) {
				if (s.getParent().equals(parentId)
						& s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedDatatypes() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.datatypesChanges != null)
			for (ElementChange s : this.datatypesChanges) {
				if (s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedDatatypes() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.datatypesChanges != null)
			for (ElementChange s : this.datatypesChanges) {
				if (s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedComponents() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.componentsChanges != null)
			for (ElementChange s : this.componentsChanges) {
				if (s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findAddedTables() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.tablesChanges != null)
			for (ElementChange s : this.tablesChanges) {
				if (s.getChangeType().equals("add")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public List<ElementChange> findDeletedTables() {
		List<ElementChange> rst = new ArrayList<ElementChange>();
		if (this.tablesChanges != null)
			for (ElementChange s : this.tablesChanges) {
				if (s.getChangeType().equals("del")) {
					rst.add(s);
				}
			}
		return rst;
	}

	public ElementChange findOneByDatatypeId(String id) {
		if (this.datatypesChanges != null)
			for (ElementChange s : this.datatypesChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByComponentId(String id) {
		if (this.componentsChanges != null)
			for (ElementChange s : this.componentsChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByFieldId(String id) {
		if (this.fieldsChanges != null)
			for (ElementChange s : this.fieldsChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByTableId(String id) {
		if (this.tablesChanges != null)
			for (ElementChange s : this.tablesChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public ElementChange findOneByCodeId(String id) {
		if (this.codesChanges != null)
			for (ElementChange s : this.codesChanges) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
		return null;
	}

	public List<ElementChange> getProfileInfoChanges() {
		return profileInfoChanges;
	}

	public void setProfileInfoChanges(List<ElementChange> profileInfoChanges) {
		this.profileInfoChanges = profileInfoChanges;
	}

	public List<ElementChange> getMetadataChanges() {
		return metadataChanges;
	}

	public void setMetadataChanges(List<ElementChange> metadataChanges) {
		this.metadataChanges = metadataChanges;
	}

	public List<ElementChange> getMessagesChanges() {
		return messagesChanges;
	}

	public void setMessagesChanges(List<ElementChange> messagesChanges) {
		this.messagesChanges = messagesChanges;
	}

	public List<ElementChange> getSegrefOrGroupChanges() {
		return segrefOrGroupChanges;
	}

	public void setSegrefOrGroupChanges(List<ElementChange> segrefOrGroupChanges) {
		this.segrefOrGroupChanges = segrefOrGroupChanges;
	}

	public List<ElementChange> getSegmentsChanges() {
		return segmentsChanges;
	}

	public void setSegmentsChanges(List<ElementChange> segmentsChanges) {
		this.segmentsChanges = segmentsChanges;
	}

	public List<ElementChange> getFieldsChanges() {
		return fieldsChanges;
	}

	public void setFieldsChanges(List<ElementChange> fieldsChanges) {
		this.fieldsChanges = fieldsChanges;
	}

	public List<ElementChange> getDatatypesChanges() {
		return datatypesChanges;
	}

	public void setDatatypesChanges(List<ElementChange> datatypesChanges) {
		this.datatypesChanges = datatypesChanges;
	}

	public List<ElementChange> getPredicatesChanges() {
		return predicatesChanges;
	}

	public void setPredicatesChanges(List<ElementChange> predicatesChanges) {
		this.predicatesChanges = predicatesChanges;
	}

	public List<ElementChange> getTablesChanges() {
		return tablesChanges;
	}

	public void setTablesChanges(List<ElementChange> tablesChanges) {
		this.tablesChanges = tablesChanges;
	}

	public List<ElementChange> getCodesChanges() {
		return codesChanges;
	}

	public void setCodesChanges(List<ElementChange> codesChanges) {
		this.codesChanges = codesChanges;
	}

	public List<ElementChange> getConfStatementChanges() {
		return confStatementChanges;
	}

	public void setConfStatementChanges(List<ElementChange> confStatementChanges) {
		this.confStatementChanges = confStatementChanges;
	}

	public List<ElementChange> getComponentsChanges() {
		return componentsChanges;
	}

	public void setComponentsChanges(List<ElementChange> componentsChanges) {
		this.componentsChanges = componentsChanges;
	}

	public static void main(String[] args) throws IOException {
		try {
			IGDocumentSerialization4ExportImpl test1 = new IGDocumentSerialization4ExportImpl();

			Profile p1 = test1
					.deserializeXMLToProfile(
							new String(
									Files.readAllBytes(Paths
											.get("src//main//resources//vxu//Profile.xml"))),
							new String(
									Files.readAllBytes(Paths
											.get("src//main//resources//vxu//ValueSets_all.xml"))),
							new String(
									Files.readAllBytes(Paths
											.get("src//main//resources//vxu//Constraints.xml"))));

			System.out.println(StringUtils.repeat("& * ", 25));
			ProfileMetaData metaData = p1.getMetaData();

			DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
			Date date = new Date();
			metaData.setDate(dateFormat.format(date));
			metaData.setName("IZ_VXU");
			metaData.setOrgName("NIST");
			metaData.setSubTitle("Specifications");
			metaData.setVersion("1.0");

			metaData.setHl7Version(HL7Version.V2_7.value());
			metaData.setSchemaVersion(SchemaVersion.V1_0.value());
			metaData.setStatus("Draft");

			p1.setMetaData(metaData);

			Profile p2 = p1.clone();
			p1.setId("1");
			p2.setId("2");

			Message message = p2.getMessages().getChildren()
					.toArray(new Message[] {})[0];
			SegmentRef segmentRef = (SegmentRef) message.getChildren().get(0);
			Group group = (Group) message.getChildren().get(5);
			Segment segment = p2.getSegments().findOneSegmentById(segmentRef.getRef());
			Field field = segment.getFields().get(0);
			Datatype datatype = p2.getDatatypes().getChildren()
					.toArray(new Datatype[] {})[0];

			// Fake addition
			SegmentRef segmentRef3 = (SegmentRef) message.getChildren().get(4);
			Segment segment3 = p1.getSegments().findOneSegmentById(segmentRef3.getRef());
			p1.getSegments().delete(segment3.getId());

			segmentRef.setMin(3);
			segmentRef.setMax("94969");
			field.setComment("wawa");
			field.setName("new field name");
			field.setName("<h2>new field name</h2>");
			group.setMax("*");
			group.setComment("new group comment");
			p2.getMetaData().setName(new String("IZ_VXU_X"));
			datatype.setComment("new dt comment");
			segment.setComment("<h2>Tqqqqqqqq</h2>");
			segment.setText1("<h2>Test format!</h2><p>textAngular WYSIWYG Text Editor</p><p><b>Features:</b></p><ol><li>Two-Way-Binding</li><li style=\"color: ;\"><b>Theming</b> Options</li><li>Simple Editor Instance Creation</li></ol><p><b>Link test:</b> <a href=\"https://github.com/fraywing/textAngular\">Here</a> </p>");

			ProfileDiffImpl cmp = new ProfileDiffImpl();
			// cmp.diffToJson(p1, p2);
			cmp.print(p1, p2);
			cmp.print(p2, p1);
			InputStream inputStream = cmp.diffToPdf(p1, p2);
			File tmpFile = new File(
					"/Users/marieros/Documents/testXslt/nvo/delta.pdf");
			FileUtils.copyInputStreamToFile(inputStream, tmpFile);

			// cmp.diffToPdf(p1, p2);
			System.out.println("done");

		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
