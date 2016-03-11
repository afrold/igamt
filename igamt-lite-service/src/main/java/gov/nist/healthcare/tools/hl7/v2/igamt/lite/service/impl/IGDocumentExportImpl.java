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
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nu.xom.Builder;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.contenttype.CTOverride;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypeManager;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTAltChunk;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTRel;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.CTShd;
import org.docx4j.wml.CTTblLayoutType;
import org.docx4j.wml.Color;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STTblLayoutType;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.GridSpan;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
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

@Service
public class IGDocumentExportImpl extends PdfPageEventHelper implements IGDocumentExportService{
	Logger logger = LoggerFactory.getLogger( IGDocumentExportImpl.class ); 

	//	@Autowired
	//	private UserService userService;

	static String constraintBackground = "#EDEDED";
	static String headerBackground = "#F0F0F0";
	static String headerFontColor = "#B21A1C";
	static String tableHSeparator = "#F01D1D";
	static String tableVSeparator = "#D3D3D3";


	@Override
	public InputStream exportAsXml(IGDocument d) {
		if (d != null) {
			return IOUtils.toInputStream(new ProfileSerializationImpl()
			.serializeProfileToXML(d.getProfile()));
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsZip(IGDocument d) throws IOException {
		if (d != null) {
			return new ProfileSerializationImpl().serializeProfileToZip(d.getProfile());
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsZip(DatatypeLibrary d) throws IOException {
		if (d != null) {
			return new ProfileSerializationImpl().serializeDatatypeToZip(d);
		} else {
			return new NullInputStream(1L);
		}
	}
	
	@Override
	public InputStream exportAsValidationForSelectedMessages(IGDocument d, String[] mids) throws IOException, CloneNotSupportedException {
		if (d != null) {
			return new ProfileSerializationImpl().serializeProfileToZip(d.getProfile(), mids);
		} else {
			return new NullInputStream(1L);
		}
	}

	@Override
	public InputStream exportAsGazelleForSelectedMessages(IGDocument d, String[] mids) throws IOException, CloneNotSupportedException {
		if (d != null) {
			return new ProfileSerializationImpl().serializeProfileGazelleToZip(d.getProfile(), mids);
		} else {
			return new NullInputStream(1L);
		}
	}
	
	@Override
	public InputStream exportAsDisplayForSelectedMessage(IGDocument d, String mid) throws IOException, CloneNotSupportedException {
		if (d != null) {
			return new ProfileSerializationImpl().serializeProfileDisplayToZip(d.getProfile(), mid);
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsDocx(IGDocument d) {
		if (d != null) {
			//			InputStream is = exportAsDocxWithDocx4J(d);
			InputStream is = exportAsDocxFromHtml(d, "true"); 
			return is;
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsDocxDatatypes(IGDocument d) {
		if (d != null) {
			InputStream is = exportAsDocxFromHtmlDatatypes(d, "true"); 
			return is;
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsPdf(IGDocument d) {
		if (d != null) {
			return exportAsPdfWithIText(d);
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsXlsx(IGDocument d) {
		if (d != null) {
			return exportAsXslxWithApachePOI(d.getProfile());
		} else {
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsHtml(IGDocument d) {
		if (d != null) {
			return exportAsHtmlFromXsl(d, "true");
		} else {
			return new NullInputStream(1L);
		}
	}


	//	Functions to collect info
	//Messages
	private void addMessage(List<List<String>> rows, Message m, Profile p){
		List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();

		for (SegmentRefOrGroup srog : segRefOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentMsgInfra(rows, (SegmentRef) srog, 0,
						p.getSegments());
			} else if (srog instanceof Group) {
				this.addGroupMsgInfra(rows, (Group) srog, 0,
						p.getSegments(), p.getDatatypes());
			}
		}

		List<Constraint> constraints = new ArrayList<>();
		for (ConformanceStatement cs: m.getConformanceStatements()){
			constraints.add(cs);
		}
		for (Predicate pre: m.getPredicates()){
			constraints.add(pre);
		}
		this.addConstraints(rows, constraints);
	}

	private void addSegmentMsgInfra(List<List<String>> rows, SegmentRef s,
			Integer depth, Segments segments) {
		String indent = StringUtils.repeat(".", 4 * depth);
		Segment segment = segments.findOneSegmentById(s.getRef());
		List<String> row = Arrays.asList(indent + segment.getName(), 
				segment.getLabel().equals(segment.getName()) ? "" : segment.getLabel(),
						segment.getDescription(),
						s.getUsage().value(), 
						"[" + String.valueOf(s.getMin())
						+ ".." + String.valueOf(s.getMax()) + "]", 
						segment.getComment() == null ? "" : segment.getComment());
		rows.add(row);
	}

	private void addGroupMsgInfra(List<List<String>> rows, Group g, Integer depth,
			Segments segments, Datatypes datatypes) {
		String indent = StringUtils.repeat(".", 2 * depth);

		List<String> row = Arrays.asList(
				indent + "[", "",
				g.getName() + " GROUP BEGIN",
				g.getUsage().value(),
				"[" + String.valueOf(g.getMin()) + ".."
						+ String.valueOf(g.getMax()) + "]", 
				"");
		rows.add(row);

		List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
		Collections.sort(segsOrGroups);
		for (SegmentRefOrGroup srog : segsOrGroups) {
			if (srog instanceof SegmentRef) {
				this.addSegmentMsgInfra(rows, (SegmentRef) srog, depth + 1,
						segments);
			} else if (srog instanceof Group) {
				this.addGroupMsgInfra(rows, (Group) srog, depth + 1, segments,
						datatypes);
			}
		}
		row = Arrays.asList(indent + "]",
				"",
				g.getName() + " GROUP END",
				"", 
				"", 
				"");
		rows.add(row);
	}

	//Segments
	private void addSegment(List<List<String>> rows, Segment s,
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
					(f.getDatatype() == null || f.getDatatype().isEmpty() ?
							"" : (datatypes.findOne(f.getDatatype()) == null ? 
									f.getDatatype() : datatypes.findOne(f.getDatatype()).getLabel())),
									f.getUsage().value(),
									"[" + String.valueOf(f.getMin()) + ".."
											+ String.valueOf(f.getMax()) + "]",
											"[" + String.valueOf(f.getMinLength()) + ".."
													+ String.valueOf(f.getMaxLength()) + "]",
													(f.getTable() == null || f.getTable().isEmpty() ? 
															"" : (tables.findOneTableById(f.getTable()) == null ? f.getTable() : tables.findOneTableById(f.getTable()).getBindingIdentifier())),
															f.getComment() == null ? "" : f.getComment());
			rows.add(row);

			if (inlineConstraints) {
				List<Constraint> constraints = this.findConstraints(
						f.getPosition(), predicates,
						conformanceStatements);
				this.addConstraints(rows, constraints);
			}
		}
		if (!inlineConstraints) {
			for (Field f : fieldsList) {
				List<Constraint> constraints = this.findConstraints(
						f.getPosition(), predicates,
						conformanceStatements);
				this.addConstraints(rows, constraints);
			}
		}
	}

	private void addDatatype(List<List<String>> rows, Datatype d,
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
						(c.getDatatype() == null || c.getDatatype().isEmpty() ?
								"" : (datatypes.findOne(c.getDatatype()) == null ? 
										c.getDatatype() : datatypes.findOne(c.getDatatype()).getLabel())),
										c.getUsage().value(),
										"[" + String.valueOf(c.getMinLength()) + ".."
												+ String.valueOf(c.getMaxLength()) + "]",
												(c.getTable() == null || c.getTable().isEmpty() ? 
														"" : (tables.findOneTableById(c.getTable()) == null ? c.getTable() : tables.findOneTableById(c.getTable()).getBindingIdentifier())),
														c.getComment());
				rows.add(row);
				List<Constraint> constraints = this.findConstraints(
						c.getPosition(), predicates,
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

	private void addValueSet(List<List<String>> rows, Table t) {
		List<String> row = new ArrayList<String>();
		List<Code> codes = t.getCodes();

		Collections.sort(codes);
		for (Code c : codes) {
			row = Arrays.asList(c.getValue(), c.getCodeSystem(), c.getCodeUsage(), c.getLabel());
			rows.add(row);
		}
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


	private void addConstraints(List<List<String>> rows,
			List<Constraint> constraints) {
		if (!constraints.isEmpty()) {
			List<String> row;
			for (Constraint constraint : constraints) {
				String constraintType = new String();
				if (constraint instanceof Predicate) {
					constraintType = "Condition Predicate";
					row = Arrays.asList("", constraintType,
							"Usage : C(" + ((Predicate)constraint).getTrueUsage() + "/" + ((Predicate)constraint).getFalseUsage() + ") \n Predicate: " + constraint.getDescription());
					rows.add(row);
				} else if (constraint instanceof ConformanceStatement) {
					constraintType = "Conformance Statement";
					row = Arrays.asList("", constraintType, constraint.getDescription());
					rows.add(row);
				}

			}
		}
	}

	private void addCsMessage(List<List<String>> rows, Message m){
		List<String> row;
		for (ConformanceStatement cs: m.getConformanceStatements()){
			row = Arrays.asList(
					cs.getConstraintTarget(), cs.getDescription());
			rows.add(row);
		}
	}

	private void addPreMessage(List<List<String>> rows, Message m){	
		List<String> row;
		for (Predicate pre: m.getPredicates()){
			row = Arrays.asList(
					pre.getConstraintTarget(), "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
			rows.add(row);
		}
	}

	private void addCsGroup(List<List<String>> rows, Message m){
		List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
		List<Constraint> constraints = new ArrayList<>();
		List<String> row;

		for (SegmentRefOrGroup srog : segRefOrGroups) {
			if (srog instanceof Group) {
				for (ConformanceStatement cs: ((Group) srog).getConformanceStatements()){
					row = Arrays.asList(
							cs.getConstraintTarget(), cs.getDescription());
					rows.add(row);
				}
				this.addCsGroupList(rows, (Group) srog);
			}
		}
		this.addConstraints(rows, constraints);
	}

	private void addCsGroupList(List<List<String>> rows, Group group){
		List<String> row;
		for (SegmentRefOrGroup srog : group.getChildren()) {
			if (srog instanceof Group) {
				for (ConformanceStatement cs: ((Group) srog).getConformanceStatements()){
					row = Arrays.asList(
							cs.getConstraintTarget(), cs.getDescription());
					rows.add(row);
				}
				this.addCsGroupList(rows, (Group) srog);
			}
		}
	}

	private void addPreGroup(List<List<String>> rows, Message m){
		List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
		List<String> row;

		for (SegmentRefOrGroup srog : segRefOrGroups) {
			if (srog instanceof Group) {
				for (Predicate pre: ((Group) srog).getPredicates()){
					row = Arrays.asList(
							pre.getConstraintTarget(), "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
					rows.add(row);
				}
				this.addPreGroupList(rows, (Group) srog);
			}
		}

	}

	private void addPreGroupList(List<List<String>> rows, Group group){
		List<String> row;
		for (SegmentRefOrGroup srog : group.getChildren()) {
			if (srog instanceof Group) {
				for (Predicate pre: ((Group) srog).getPredicates()){
					row = Arrays.asList(
							pre.getConstraintTarget(), "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
					rows.add(row);
				}
				this.addPreGroupList(rows, (Group) srog);
			}
		}
	}

	private void addCsSegment(List<List<String>> rows, Segment s){
		List<String> row;
		for (ConformanceStatement cs: s.getConformanceStatements()){
			row = Arrays.asList(
					cs.getConstraintTarget(), cs.getDescription());
			rows.add(row);
		}
	}

	private void addPreSegment(List<List<String>> rows, Segment s){		
		List<String> row;
		for (Predicate pre: s.getPredicates()){
			row = Arrays.asList(
					pre.getConstraintTarget(), "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
			rows.add(row);
		}
	}


	private void addCsDatatype(List<List<String>> rows, Datatype dt){
		List<String> row;
		for (ConformanceStatement cs: dt.getConformanceStatements()){
			row = Arrays.asList(
					cs.getConstraintTarget(), cs.getDescription());
			rows.add(row);
		}
	}

	private void addPreDatatype(List<List<String>> rows, Datatype dt){		
		List<String> row;
		for (Predicate pre: dt.getPredicates()){
			row = Arrays.asList(
					pre.getConstraintTarget(), "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
			rows.add(row);
		}
	}

	private InputStream exportAsXslxWithApachePOI(Profile p) {
		logger.debug("Export profile id: " + p.getId() + " as xslx");
		try {
			File tmpXlsxFile = File.createTempFile("ProfileTmp", ".xslx");

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet;
			XSSFCellStyle headerStyle;
			List<List<String>> rows;
			List<String> row;
			List<String> header;

			List<String> sheetNames = new ArrayList<String>();
			String sheetName;

			headerStyle = workbook.createCellStyle();
			headerStyle.setFillPattern(XSSFCellStyle.BORDER_THICK);
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE
					.getIndex());
			headerStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE
					.getIndex());

			//Adding list of elements
			sheet = workbook.createSheet("Message list");
			header = Arrays.asList("Messages");
			rows = new ArrayList<List<String>>();
			rows.add(header);
			row = new ArrayList<String>();

			List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
			Collections.sort(messagesList);
			for (Message m : messagesList) {
				row = Arrays.asList(m.getName() +" - "+  m.getDescription());
				rows.add(row);
			}
			this.writeToSheet(rows, header, sheet, headerStyle);

			sheet = workbook.createSheet("Segment list");
			header = Arrays.asList("Segments");
			rows = new ArrayList<List<String>>();
			rows.add(header);
			row = new ArrayList<String>();
			List<Segment> segmentList = new ArrayList<Segment>(p.getSegments().getChildren());
			Collections.sort(segmentList);
			for (Segment s: segmentList) {
				row = Arrays.asList(s.getLabel() + " - " +  s.getDescription());
				rows.add(row);
			}
			this.writeToSheet(rows, header, sheet, headerStyle);

			sheet = workbook.createSheet("Datatype list");
			header = Arrays.asList("Datatypes");
			rows = new ArrayList<List<String>>();
			rows.add(header);
			row = new ArrayList<String>();
			List<Datatype> datatypeList = new ArrayList<Datatype>(p.getDatatypes().getChildren());
			Collections.sort(datatypeList);
			for (Datatype dt: datatypeList) {
				row = Arrays.asList(dt.getLabel() +" - "+ dt.getDescription());
				rows.add(row);
			}
			this.writeToSheet(rows, header, sheet, headerStyle);

			sheet = workbook.createSheet("Value set list");
			header = Arrays.asList("Value sets");
			rows = new ArrayList<List<String>>();
			rows.add(header);
			row = new ArrayList<String>();
			List<Table> tableList = new ArrayList<Table>(p.getTables().getChildren());
			Collections.sort(tableList);
			for (Table t: tableList) {
				row = Arrays.asList(t.getBindingIdentifier()+" - "+ t.getName());
				rows.add(row);
			}
			this.writeToSheet(rows, header, sheet, headerStyle);

			// Adding messages
			for (Message m : messagesList) {
				sheetName = "MSG_"+m.getMessageType()+ "_"+ m.getStructID();
				if (sheetNames.contains(sheetName)){
					logger.debug(sheetName + " already added!!");
				} else {
					sheetNames.add(sheetName);
					sheet = workbook
							.createSheet(sheetName); //Sheet name must be unique

					header = Arrays.asList("SEGMENT", "CDC Usage", "Local Usage",
							"CDC Cardinality", "Local Cardinality", "Comments");
					rows = new ArrayList<List<String>>();
					rows.add(header);

					for (SegmentRefOrGroup srog : m.getChildren()) {
						if (srog instanceof SegmentRef) {
							this.addSegmentInfoXlsx(rows, (SegmentRef) srog, 0,
									p.getSegments());
						} else if (srog instanceof Group) {
							this.addGroupInfoXlsx(rows, (Group) srog, 0,
									p.getSegments(), p.getDatatypes());
						}
					}
					this.writeToSheet(rows, header, sheet, headerStyle);
				}
			}

			// Adding segments 
			for (Segment s: segmentList) {
				rows = new ArrayList<List<String>>();
				header = Arrays.asList("Segment", "Name", "DT", "Usage",
						"Card.", "Len",
						"Value set", "Comment");
				sheetName = "SGT_"+s.getLabel();
				if (sheetNames.contains(sheetName)){
					logger.debug(sheetName + " already added!!");
				} else {
					sheetNames.add(sheetName);
					sheet = workbook.createSheet(sheetName);
					rows.add(header);
					this.addSegment(rows, s, Boolean.FALSE, p.getDatatypes(), p.getTables());
					this.writeToSheet(rows, header, sheet, headerStyle);
				}
			}

			// Adding datatypes
			for (Datatype dt : datatypeList){
				rows = new ArrayList<List<String>>();
				header = Arrays.asList("Component", "Name", "Len.", "DT", "Usage",
						"Card.", "Value set", "Comment");
				sheetName = "DT_"+dt.getLabel();
				if (sheetNames.contains(sheetName)){
					logger.debug(sheetName + " already added!!");
				} else {
					sheetNames.add(sheetName);
					sheet = workbook.createSheet(sheetName);
					rows.add(header);
					this.addDatatype(rows, dt, p.getDatatypes(), p.getTables());
					this.writeToSheet(rows, header, sheet, headerStyle);
				}
			}

			// Adding value sets
			for (Table t: tableList){
				sheetName = "VS_"+t.getBindingIdentifier();
				if (sheetNames.contains(sheetName)){
					logger.debug(sheetName + " already added!!");
				} else {
					sheetNames.add(sheetName);
					sheet = workbook.createSheet(sheetName);

					header = Arrays.asList("Value", "CodeSystem", "Usage", "Label");
					rows = new ArrayList<List<String>>();
					this.addValueSet(rows, t);
					rows.add(0, header);
					this.writeToSheet(rows, header, sheet, headerStyle);
				}
			}

			FileOutputStream out = new FileOutputStream(tmpXlsxFile);
			workbook.write(out);
			workbook.close();
			out.close();

			return FileUtils.openInputStream(tmpXlsxFile);

		} catch (Exception e) {
			logger.debug("Error creating workbook");
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}
	private void addGroupInfoXlsx(List<List<String>> rows, Group g, Integer depth,
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
				this.addSegmentInfoXlsx(rows, (SegmentRef) srog, depth + 1,
						segments);
			} else if (srog instanceof Group) {
				this.addGroupInfoXlsx(rows, (Group) srog, depth + 1, segments,
						datatypes);
			}
		}
		row = Arrays.asList(indent + "END " + g.getName() + " GROUP", "", "",
				"");
		rows.add(row);
	}

	private void addSegmentInfoXlsx(List<List<String>> rows, SegmentRef s,
			Integer depth, Segments segments) {
		String indent = StringUtils.repeat(" ", 4 * depth);
		Segment segment = segments.findOneSegmentById(s.getRef());
		List<String> row = Arrays.asList(indent + segment.getName(), s
				.getUsage().value(), "", "[" + String.valueOf(s.getMin())
				+ ".." + String.valueOf(s.getMax()) + "]", "", segment
				.getComment() == null ? "" : segment.getComment());
		rows.add(row);
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

	public InputStream exportAsHtmlFromXsl(IGDocument igdoc, String inlineConstraints) {
		// Note: inlineConstraint can be true or false

		try {
			File tmpHtmlFile = File.createTempFile("IGDocTemp", ".html");

			// Generate xml file containing profile
			File tmpXmlFile = File.createTempFile("IGDocTemp", ".xml");
			String stringIgDoc = new IGDocumentSerialization4ExportImpl()
			.serializeIGDocumentToXML(igdoc);
			FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc,
					Charset.forName("UTF-8"));

			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(this.getClass()
					.getResourceAsStream("/rendering/igdoc2htmlWithTOC.xsl"));
			Transformer transformer;

			// Apply XSL transformation on xml file to generate html
			transformer = factory.newTransformer(xslt);
			transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));
			return FileUtils.openInputStream(tmpHtmlFile);

		} catch (TransformerException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}


		//			// Apply XSL transformation on xml file to generate html
		//			File tmpHtmlFile = File.createTempFile("IGDocTemp", ".html");
		//			//			File tmpHtmlFile = new File("ProfileTemp.html");
		//			Builder builder = new Builder();
		//			//			nu.xom.Document input = builder.build(tmpXmlFile);
		//			nu.xom.Document input = new ProfileSerialization4ExportImpl()
		//			.serializeIGDocumentToDoc(igdoc);
		//			nu.xom.Document stylesheet = builder.build(this.getClass()
		//					.getResourceAsStream("/rendering/igdocument.xsl"));
		//			XSLTransform transform = new XSLTransform(stylesheet);
		//			transform.setParameter("inlineConstraints", inlineConstraints);
		//			Nodes output = transform.transform(input);
		//			nu.xom.Document result = XSLTransform.toDocument(output);
		//
		//			Tidy tidy = new Tidy();
		//			tidy.setWraplen(Integer.MAX_VALUE);
		//			tidy.setXHTML(true);
		//			tidy.setShowWarnings(false); //to hide errors
		//			tidy.setQuiet(true); //to hide warning
		//			ByteArrayInputStream inputStream = new ByteArrayInputStream(result.toXML().getBytes("UTF-8"));
		//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		//			tidy.parseDOM(inputStream, outputStream);
		//			FileUtils.writeStringToFile(tmpHtmlFile, outputStream.toString("UTF-8"));
		//
		//			return FileUtils.openInputStream(tmpHtmlFile);
		//		} catch (IOException | ParsingException
		//				| XSLException e) {
		//			return new NullInputStream(1L);
		//		}
	}


	public InputStream exportAsPdfFromXsl(IGDocument d, String inlineConstraints) {
		// Note: inlineConstraint can be true or false

		Profile p = d.getProfile();
		p.getMessages().setPositionsOrder();
		p.getSegments().setPositionsOrder();
		p.getDatatypes().setPositionsOrder();
		p.getTables().setPositionsOrder();


		try {
			// Generate xml file containing profile
			//			File tmpXmlFile = File.createTempFile("ProfileTemp", ".xml");
			File tmpXmlFile = new File("IGDocTemp.xml");
			String stringProfile = new IGDocumentSerialization4ExportImpl()
			.serializeProfileToXML(d.getProfile());
			FileUtils.writeStringToFile(tmpXmlFile, stringProfile,
					Charset.forName("UTF-8"));

			// Apply XSL transformation on xml file to generate html
			File tmpHtmlFile = File.createTempFile("ProfileTemp", ".html");
			//			File tmpHtmlFile = new File("ProfileTemp.html");
			Builder builder = new Builder();
			//			nu.xom.Document input = builder.build(tmpXmlFile);

			nu.xom.Document input = new IGDocumentSerialization4ExportImpl()
			.serializeIGDocumentToDoc(d);
			nu.xom.Document stylesheet = builder.build(this.getClass()
					.getResourceAsStream("/rendering/igdocument.xsl"));
			XSLTransform transform = new XSLTransform(stylesheet);
			transform.setParameter("inlineConstraints", inlineConstraints);
			Nodes output = transform.transform(input);
			nu.xom.Document result = XSLTransform.toDocument(output);

			Tidy tidy = new Tidy();
			tidy.setWraplen(Integer.MAX_VALUE);
			tidy.setXHTML(true);
			tidy.setShowWarnings(false); //to hide errors
			tidy.setQuiet(true); //to hide warning
			ByteArrayInputStream inputStream = new ByteArrayInputStream(result.toXML().getBytes("UTF-8"));
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			tidy.parseDOM(inputStream, outputStream);
			FileUtils.writeStringToFile(tmpHtmlFile, outputStream.toString("UTF-8"));

			// Convert html document to pdf
			Document document = new Document();
			File tmpPdfFile = File.createTempFile("Profile", ".pdf");
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

	// table to store placeholder for all chapters and sections
	private Map<String, PdfTemplate> tocPlaceholder;

	// store the chapters and sections with their title here.
	private Map<String, Integer> pageByTitle;


	private InputStream exportAsPdfWithIText(IGDocument igdoc) {
		Profile p = igdoc.getProfile();

		List<String> header;
		PdfPTable table;
		float columnWidths[];
		List<List<String>> rows;

		// Create fonts and colors to be used in generated pdf
		BaseColor headerBackgroundColor = new BaseColor(java.awt.Color.decode(headerBackground).getRGB());//WebColors.getRGBColor(headerBackground);
		BaseColor headerFontItxtColor = new BaseColor(java.awt.Color.decode(headerFontColor).getRGB());//WebColors.getRGBColor(headerFontColor);
		BaseColor cpColor = new BaseColor(java.awt.Color.decode(constraintBackground).getRGB());//WebColors.getRGBColor(constraintBackground);
		Font coverH1Font = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18, Font.BOLD,
				BaseColor.BLACK);
		Font coverH2Font = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD,
				BaseColor.BLACK);
		Font coverFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.BOLD,
				BaseColor.BLACK);
		Font tocTitleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16, Font.BOLD,
				BaseColor.BLACK);
		Font titleFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.BOLD, 
				BaseColor.BLACK);
		Font headerFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL,
				headerFontItxtColor);
		Font cellFont = FontFactory.getFont("/rendering/Arial Narrow.ttf",
				BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL,
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

			Image img1 = Image.getInstance(this.getClass().getResource("/rendering/hl7Logo.tiff"));
			img1.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(img1);

			Paragraph paragraph = new Paragraph(igdoc.getMetaData().getTitle(),
					coverH1Font);
			paragraph.setSpacingBefore(55);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);

			paragraph = new Paragraph(igdoc.getMetaData().getSubTitle(),
					coverH2Font);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);

			paragraph = new Paragraph(igdoc.getMetaData().getDate(),
					coverH2Font);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			paragraph.setSpacingAfter(20);
			coverDocument.add(paragraph);

			paragraph = new Paragraph(
					"HL7 version: " + p.getMetaData().getHl7Version(), coverFont);
			paragraph.add(Chunk.NEWLINE);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);

			paragraph = new Paragraph("Document version: "
					+ igdoc.getMetaData().getVersion(), coverFont);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);

			paragraph = new Paragraph(p.getMetaData().getOrgName(), coverFont);
			paragraph.setAlignment(Element.ALIGN_CENTER);
			coverDocument.add(paragraph);

			//			paragraph = new Paragraph("Principal author: " + userService.getAccountById(p.getAccountId()).getFullName(), coverH2Font);
			//			paragraph.setAlignment(Element.ALIGN_CENTER);
			//			coverDocument.add(paragraph);

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
			igDocument.setMargins(36f, 36f, 46f, 46f); // 72pt = 1 inch
			igDocument.open();

			Paragraph title = null;
			com.itextpdf.text.Section section = null;

			/*
			 * Adding sections and subsections content
			 */
			traverseIGDocument4Pdf(igdoc, tocDocument, igDocument, titleFont, igWriter); 

			/*
			 * Adding Messaging infrastructure
			 */
			int positionMessageInfrastructure = igdoc.getChildSections().size();
			tocDocument.add(Chunk.NEWLINE);
			tocDocument.add(new Paragraph(String.valueOf(positionMessageInfrastructure + 1) + " " + p.getSectionTitle(), titleFont));
			tocDocument.add(Chunk.NEWLINE);

			Chapter chapterMsgInfra = new Chapter(new Paragraph(p.getSectionTitle(), titleFont), positionMessageInfrastructure + 1);

			/*
			 * Adding conformance profiles(messages) definitions
			 */

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*1) + String.valueOf(positionMessageInfrastructure+1) + "." + String.valueOf(p.getMessages().getSectionPosition()+1) + p.getMessages().getSectionTitle(), p.getMessages().getId());
			Chunk cp = new Chunk(p.getMessages().getSectionTitle(), titleFont).setLocalDestination(p.getMessages().getId());
			Paragraph titleCp = new Paragraph(cp);
			//			Paragraph titleCp = new Paragraph(p.getMessages().getSectionTitle(), titleFont);

			com.itextpdf.text.Section sectionCp = chapterMsgInfra.addSection(titleCp);

			p.getMessages().setPositionsOrder();
			List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
			Collections.sort(messagesList);

			for (Message m : messagesList) {

				this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*2) + String.valueOf(positionMessageInfrastructure+1) + "." +  String.valueOf(p.getMessages().getSectionPosition()+1)+ "." + String.valueOf(m.getSectionPosition()+1) + " " + m.getName(), m.getId());
				title = new Paragraph(m.getName(), titleFont);
				section = sectionCp.addSection(title);
				section.setIndentationLeft(30);
				//				section.setTriggerNewPage(true); TODO Check if required by users

				section.add(Chunk.NEWLINE);
				section.add(richTextToParagraph(m.getComment()));
				section.add(Chunk.NEWLINE);

				header = Arrays.asList("Segment", "Flavor", "Element name", "Usage", "Card.", "Description/Comments");
				columnWidths = new float[] { 12f, 10f, 20f, 8f, 9f, 30f };
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);

				rows = new ArrayList<List<String>>();
				this.addMessage(rows, m, p);
				this.addCellsPdfTable(table, rows, cellFont, cpColor);

				section.add(table);

				section.add(Chunk.NEWLINE);
				section.add(richTextToParagraph(m.getUsageNote()));
				section.add(Chunk.NEWLINE);
			}

			/*
			 * Adding segments details
			 */
			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*1) + String.valueOf(positionMessageInfrastructure+1) + "." + String.valueOf(p.getSegments().getSectionPosition()+1) + " " + p.getSegments().getSectionTitle(), p.getSegments().getId());
			Paragraph titleSgt = new Paragraph(p.getSegments().getSectionTitle(), titleFont);
			com.itextpdf.text.Section sectionSgt = chapterMsgInfra.addSection(titleSgt);

			p.getSegments().setPositionsOrder();
			List<Segment> segmentsList = new ArrayList<Segment>(p.getSegments().getChildren());
			Collections.sort(segmentsList);

			for (Segment s: segmentsList){
				String segmentInfo = s.getLabel() + " - " + s.getDescription();
				this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*2) + String.valueOf(positionMessageInfrastructure+1) + "." + String.valueOf(p.getSegments().getSectionPosition()+1) + "." + String.valueOf(s.getSectionPosition()+1) + " " + segmentInfo, s.getId());
				com.itextpdf.text.Section section1 = sectionSgt.addSection(new Paragraph(segmentInfo, titleFont));

				section1.add(Chunk.NEWLINE);
				section1.add(richTextToParagraph(s.getText1()));
				section1.add(Chunk.NEWLINE);

				header = Arrays.asList("Seq", "Element Name", "DT",
						"Usage", "Card.", "Length", "Value\nSet", "Description/Comments");
				columnWidths = new float[] { 6f, 20f, 9f, 7.5f, 9f, 9f, 10f, 30f };
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);

				rows = new ArrayList<List<String>>();
				this.addSegment(rows, s, Boolean.TRUE, p.getDatatypes(), p.getTables());
				this.addCellsPdfTable(table, rows, cellFont, cpColor);
				section1.add(table);

				section1.add(Chunk.NEWLINE);
				section1.add(richTextToParagraph(s.getText2()));
				section1.add(Chunk.NEWLINE);

				List<Field> fieldsList = s.getFields();
				Collections.sort(fieldsList);
				for (Field f : fieldsList) {
					if (f.getText() != null && f.getText().length() != 0) {
						Font fontbold = FontFactory.getFont("Arial", 12,
								Font.BOLD);
						section1.add(new Paragraph(s.getName() + "-"
								+ f.getItemNo().replaceFirst("^0+(?!$)", "") + " "
								+ f.getName() + " ("
								+ p.getDatatypes().findOne(f.getDatatype()).getLabel() + ")",
								fontbold));
						section1.add(new Paragraph(f.getText()));
					}
				}
				section1.add(Chunk.NEWLINE);
			}


			/*
			 * Adding datatypes info
			 */
			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*1) + String.valueOf(positionMessageInfrastructure+1) + "." + String.valueOf(p.getDatatypes().getSectionPosition()+1) + " " + p.getDatatypes().getSectionTitle(), p.getDatatypes().getId());
			Paragraph titleDts = new Paragraph(p.getDatatypes().getSectionTitle(), titleFont);
			com.itextpdf.text.Section sectionDts = chapterMsgInfra.addSection(titleDts);

			header = Arrays.asList("Seq", "Element Name", "Conf\nlength", "DT",
					"Usage", "Length", "Value\nSet", "Comment");
			columnWidths = new float[] {6f, 20f, 9f, 7.5f, 9f, 9f, 10f, 30f};

			p.getDatatypes().setPositionsOrder();
			List<Datatype> datatypeList = new ArrayList<Datatype>(p.getDatatypes().getChildren());
			Collections.sort(datatypeList);
			for (Datatype d: datatypeList) {

				this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*2) + String.valueOf(positionMessageInfrastructure+1) + "." + String.valueOf(p.getDatatypes().getSectionPosition()+1) + "." + String.valueOf(d.getSectionPosition()+1) + " " + (d.getLabel() != null ?  d.getLabel()+ " - " + d.getDescription() : d.getName()
						+ " - " + d.getDescription()), d.getId());
				com.itextpdf.text.Section section1 = sectionDts.addSection(new Paragraph( d.getLabel() != null ?  d.getLabel() + " - "
						+ d.getDescription() : d.getName() + " - " + d.getDescription()));
				

				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addDatatype(rows, d, p.getDatatypes(),
						p.getTables());
				this.addCellsPdfTable(table, rows, cellFont, cpColor);
				section1.add(Chunk.NEWLINE);
				section1.add(table);
				section1.add(Chunk.NEWLINE);
				section1.add(richTextToParagraph(d.getUsageNote()));
				section1.add(Chunk.NEWLINE);
				section1.add(new Paragraph(d.getComment()));				
				section1.add(Chunk.NEWLINE);
			}

			/*
			 * Adding value sets info
			 */
			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*1) + String.valueOf(positionMessageInfrastructure+1) + "." + String.valueOf(p.getTables().getSectionPosition()+1) + " " + p.getTables().getSectionTitle(), p.getTables().getId());
			Paragraph titleVsd = new Paragraph(p.getTables().getSectionTitle(), titleFont);
			com.itextpdf.text.Section sectionVsd = chapterMsgInfra.addSection(titleVsd);

			header = Arrays.asList("Value", "Code system", "Usage", "Description");

			columnWidths = new float[] { 15f, 15f, 10f, 50f };

			p.getTables().setPositionsOrder();
			List<Table> tables = new ArrayList<Table>(p.getTables()
					.getChildren());
			Collections.sort(tables);

			for (Table t : tables) {

				this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*2) + String.valueOf(positionMessageInfrastructure+1) + "." +  String.valueOf(p.getTables().getSectionPosition()+1) + "." + String.valueOf(t.getSectionPosition()+1) + " " + t.getBindingIdentifier()
						+ " - " + t.getDescription(), t.getId());
				com.itextpdf.text.Section section1 = sectionVsd.addSection(new Paragraph(t.getBindingIdentifier() + " - " + t.getDescription()));

				StringBuilder sb = new StringBuilder();
				sb.append("\nOid: ");
				sb.append(t.getOid()==null||t.getOid().isEmpty() ? "UNSPECIFIED" : t.getOid());
				//				sb.append("\nStability: ");
				//				sb.append(t.getStability()==null ? "Static":t.getStability().value());
				//				sb.append("\nExtensibility: ");
				//				sb.append(t.getExtensibility() == null ? "Closed":t.getExtensibility().value());
				//				sb.append("\nContent: ");
				//				sb.append(t.getContentDefinition() == null ? "Extensional":t.getContentDefinition().value());

				//				igDocument.add(new Chunk(sb.toString(), cellFont));
				section1.add(new Chunk(sb.toString(), cellFont));

				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addValueSet(rows, t);
				this.addCellsPdfTable(table, rows, cellFont, cpColor);
				section1.add(Chunk.NEWLINE);
				section1.add(table);
				section1.add(Chunk.NEWLINE);


			}

			/*
			 * Adding conformance information
			 */
			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*1) + String.valueOf(positionMessageInfrastructure+1) + ".5" + " Conformance information", "conformanceinformation");
			Paragraph titleCfi = new Paragraph("Conformance information", titleFont);
			com.itextpdf.text.Section sectionCfi = chapterMsgInfra.addSection(titleCfi);

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*2) + String.valueOf(positionMessageInfrastructure+1) + ".5.1 Conditional predicates", "conditionalpredicates");
			com.itextpdf.text.Section section1 = sectionCfi.addSection(new Paragraph("Conditional predicates"));

			header = Arrays.asList("Location", "Usage", "Description");
			columnWidths = new float[] { 15f, 15f, 75f };

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.1 Message level", "cpmessagelevel");
			com.itextpdf.text.Section sectionTmp = section1.addSection(new Paragraph("Message level"));

			int j = 1;
			for (Message m: messagesList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addPreMessage(rows, m);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.1."+ String.valueOf(j) + " " + m.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.1.1."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(m.getName()));
					this.addAllCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.2 Group level", "cpgrouplevel");
			sectionTmp = section1.addSection(new Paragraph("Group level"));

			j = 1;
			for (Message m: messagesList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addPreGroup(rows, m);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.2."+ String.valueOf(j) + " " + m.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.1.2."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(m.getName()));
					this.addAllCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.3 Segment level", "cpsegmentlevel");
			sectionTmp = section1.addSection(new Paragraph("Segment level"));

			j = 1;
			for (Segment s: segmentsList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addPreSegment(rows, s);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.3."+ String.valueOf(j) + " " + s.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.1.3."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(s.getName()));
					this.addAllCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.4 Datatype level", "cpdatatypelevel");
			sectionTmp = section1.addSection(new Paragraph("Datatype level"));

			j = 1;
			for (Datatype dt: datatypeList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addPreDatatype(rows, dt);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.1.4."+ String.valueOf(j) + " " + dt.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.1.4."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(dt.getLabel()));
					this.addAllCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*2) + String.valueOf(positionMessageInfrastructure+1) + ".5.2 Conformance statements", "conformancestatements");
			section1 = sectionCfi.addSection(new Paragraph("Conformance statements"));

			header = Arrays.asList("Location", "Description");
			columnWidths = new float[] { 15f, 75f };

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.1 Message level", "csconformancestatement");
			sectionTmp = section1.addSection(new Paragraph("Message level"));

			j = 1;
			for (Message m: messagesList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addCsMessage(rows, m);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.1."+ String.valueOf(j) + " " + m.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.2.1."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(m.getName()));
					this.addCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.2 Group level", "csgrouplevel");
			sectionTmp = section1.addSection(new Paragraph("Group level"));

			j = 1;
			for (Message m: messagesList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addCsGroup(rows, m);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.2."+ String.valueOf(j) + " " + m.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.2.2."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(m.getName()));
					this.addCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.3 Segment level", "cssegmentlevel");
			sectionTmp = section1.addSection(new Paragraph("Segment level"));

			j = 1;
			for (Segment s: segmentsList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addCsSegment(rows, s);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.3."+ String.valueOf(j) + " " + s.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.2.3."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(s.getName()));
					this.addCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*3) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.4 Datatype level", "csdatatypelevel");
			sectionTmp = section1.addSection(new Paragraph("Datatype level"));

			j = 1;
			for (Datatype dt: datatypeList){
				table = this.addHeaderPdfTable(header, columnWidths,
						headerFont, headerBackgroundColor);
				rows = new ArrayList<List<String>>();
				this.addCsDatatype(rows, dt);
				if (!rows.isEmpty()){
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*4) + String.valueOf(positionMessageInfrastructure+1) + ".5.2.4."+ String.valueOf(j) + " " + dt.getName(), String.valueOf(positionMessageInfrastructure+1) + ".5.2.4."+ String.valueOf(j));
					j += 1;
					sectionTmp.add(new Paragraph(dt.getLabel()));
					this.addCellsPdfTable(table, rows, cellFont, cpColor);
					sectionTmp.add(Chunk.NEWLINE);
					sectionTmp.add(table);
					sectionTmp.add(Chunk.NEWLINE);
				}
			}

			igDocument.add(chapterMsgInfra);
			igDocument.add(Chunk.NEWLINE);

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
			String title_, String idTarget) {
		try {
			// Create TOC
			final String title = title_;
			Chunk chunk = new Chunk(title).setLocalGoto(idTarget);
			tocDocument.add(new Paragraph(chunk));
			// Add a placeholder for the page reference
			tocDocument.add(new VerticalPositionMark() {
				@Override
				public void draw(PdfContentByte canvas, float llx, float lly,
						float urx, float ury, float y) {
					final PdfTemplate createTemplate = canvas.createTemplate(
							60, 60);
					IGDocumentExportImpl.this.tocPlaceholder.put(title,
							createTemplate);
					canvas.addTemplate(createTemplate, urx - 60, y);
				}
			});

			// Create page numbers
			BaseFont baseFont;
			PdfTemplate template = this.tocPlaceholder.get(title);
			template.beginText();

			baseFont = BaseFont.createFont();
			template.setFontAndSize(baseFont, 11);
			template.setTextMatrix(
					30 - baseFont.getWidthPoint(
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
			setCellBorders(c1);
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
					PdfPCell tcell;
					tcell = new PdfPCell(new Phrase(cell, cellFont));
					setCellBorders(tcell);
					table.addCell(tcell);
				}
			} else {
				PdfPCell cell;
				cell = new PdfPCell(new Phrase(cells.get(0), cellFont));
				setCellBorders(cell);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(cells.get(1), cellFont));
				cell.setColspan(2);
				cell.setBackgroundColor(cpColor);
				setCellBorders(cell);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(cells.get(2), cellFont));
				cell.setColspan(7);
				setCellBorders(cell);
				table.addCell(cell);
			}
		}
	}

	private void addAllCellsPdfTable(PdfPTable table, List<List<String>> rows,
			Font cellFont, BaseColor cpColor) {
		for (List<String> cells : rows) {
			for (String cell : cells) {
				PdfPCell tcell;
				tcell = new PdfPCell(new Phrase(cell, cellFont));
				setCellBorders(tcell);
				table.addCell(tcell);
			}
		}
	}
	private void setCellBorders(PdfPCell tcell){
		tcell.setBorderColorRight(new BaseColor(java.awt.Color.decode(tableVSeparator).getRGB()));
		tcell.setBorderColorLeft(new BaseColor(java.awt.Color.decode(tableVSeparator).getRGB()));
		tcell.setBorderColorTop(new BaseColor(java.awt.Color.decode(tableHSeparator).getRGB()));
		tcell.setBorderColorBottom(new BaseColor(java.awt.Color.decode(tableHSeparator).getRGB()));
	}

	private Paragraph richTextToParagraph(String htmlString){
		List<Element> p = new ArrayList<Element>();
		try {
			Tidy tidy = new Tidy();
			tidy.setWraplen(Integer.MAX_VALUE);
			tidy.setXHTML(true);
			tidy.setShowWarnings(false); //to hide errors
			tidy.setQuiet(true); //to hide warning
			InputStream inputStream = new ByteArrayInputStream(htmlString.getBytes());
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			tidy.parseDOM(inputStream, outputStream);

			p = XMLWorkerHelper.parseToElementList(outputStream.toString(), null);

			Paragraph paragraph=new Paragraph();
			for (int k = 0; k < p.size(); ++k){
				paragraph.add((Element) p.get(k));
			}
			return paragraph;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream exportAsDocxFromHtml(IGDocument igdoc, String inlineConstraints) {
		// Note: inlineConstraint can be true or false

		try {
			File tmpHtmlFile = File.createTempFile("IGDocTemp", ".html");

			// Generate xml file containing profile
			File tmpXmlFile = File.createTempFile("IGDocTemp", ".xml");
			String stringIgDoc = new IGDocumentSerialization4ExportImpl()
			.serializeIGDocumentToXML(igdoc);
			FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc,
					Charset.forName("UTF-8"));

			TransformerFactory factoryTf = TransformerFactory.newInstance();
			Source xslt = new StreamSource(this.getClass()
					.getResourceAsStream("/rendering/igdocument2.xsl"));
			Transformer transformer;

			// Apply XSL transformation on xml file to generate html
			transformer = factoryTf.newTransformer(xslt);
			transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

			String html =  FileUtils.readFileToString(tmpHtmlFile);

			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(this.getClass()
					.getResourceAsStream("/rendering/lri_template.dotx"));

			ObjectFactory factory = Context.getWmlObjectFactory();   

			createCoverPageForDocx4j(igdoc, wordMLPackage, factory);

			createTableOfContentForDocx4j(wordMLPackage, factory);

			FieldUpdater updater = new FieldUpdater(wordMLPackage);
			try {
				updater.update(true);
			} catch (Docx4JException e1) {
				e1.printStackTrace();
			}

			AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
			afiPart.setBinaryData(html.getBytes());
			afiPart.setContentType(new ContentType("text/html"));
			Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);

			// .. the bit in document body
			CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
			ac.setId(altChunkRel.getId() );
			wordMLPackage.getMainDocumentPart().addObject(ac);

			// .. content type
			wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");

			addConformanceInformationForDocx4j(igdoc, wordMLPackage, factory);

			loadTemplateForDocx4j(wordMLPackage); //Repeats the lines above but necessary; don't delete

			File tmpFile;
			tmpFile = File.createTempFile("IgDocument", ".docx");
			wordMLPackage.save(tmpFile);


			return FileUtils.openInputStream(tmpFile);

		} catch (TransformerException | IOException | Docx4JException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsDocxFromHtmlDatatypes(IGDocument igdoc, String inlineConstraints) {
		// Note: inlineConstraint can be true or false

		try {
			File tmpHtmlFile = File.createTempFile("DTTemp", ".html");

			// Generate xml file containing profile
			File tmpXmlFile = File.createTempFile("DTTemp", ".xml");
			String stringIgDoc = new IGDocumentSerialization4ExportImpl()
			.serializeDatatypesToXML(igdoc);
			FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc,
					Charset.forName("UTF-8"));

			TransformerFactory factoryTf = TransformerFactory.newInstance();
			Source xslt = new StreamSource(this.getClass()
					.getResourceAsStream("/rendering/igdocument2.xsl"));
			Transformer transformer;

			// Apply XSL transformation on xml file to generate html
			transformer = factoryTf.newTransformer(xslt);
			transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

			String html =  FileUtils.readFileToString(tmpHtmlFile);

			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(this.getClass()
					.getResourceAsStream("/rendering/lri_template.dotx"));

			ObjectFactory factory = Context.getWmlObjectFactory();   

			createCoverPageForDocx4j(igdoc, wordMLPackage, factory);

			createTableOfContentForDocx4j(wordMLPackage, factory);

			FieldUpdater updater = new FieldUpdater(wordMLPackage);
			try {
				updater.update(true);
			} catch (Docx4JException e1) {
				e1.printStackTrace();
			}

			AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
			afiPart.setBinaryData(html.getBytes());
			afiPart.setContentType(new ContentType("text/html"));
			Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);

			// .. the bit in document body
			CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
			ac.setId(altChunkRel.getId() );
			wordMLPackage.getMainDocumentPart().addObject(ac);

			// .. content type
			wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");

			addConformanceInformationForDocx4j(igdoc, wordMLPackage, factory);

			loadTemplateForDocx4j(wordMLPackage); //Repeats the lines above but necessary; don't delete

			File tmpFile;
			tmpFile = File.createTempFile("DTDocument", ".docx");
			wordMLPackage.save(tmpFile);


			return FileUtils.openInputStream(tmpFile);

		} catch (TransformerException | IOException | Docx4JException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}


	@SuppressWarnings("resource")
	public static void mergePOI(InputStream src1, InputStream src2, OutputStream dest) throws Exception {
		OPCPackage src1Package = OPCPackage.open(src1);
		OPCPackage src2Package = OPCPackage.open(src2);
		XWPFDocument src1Document = new XWPFDocument(src1Package);        
		CTBody src1Body = src1Document.getDocument().getBody();
		XWPFDocument src2Document = new XWPFDocument(src2Package);
		CTBody src2Body = src2Document.getDocument().getBody();        
		appendBody(src1Body, src2Body);
		src1Document.write(dest);
	}

	private static void appendBody(CTBody src, CTBody append) throws Exception {
		XmlOptions optionsOuter = new XmlOptions();
		optionsOuter.setSaveOuter();
		String appendString = append.xmlText(optionsOuter);
		String srcString = src.xmlText();
		String prefix = srcString.substring(0,srcString.indexOf(">")+1);
		String mainPart = srcString.substring(srcString.indexOf(">")+1,srcString.lastIndexOf("<"));
		String sufix = srcString.substring( srcString.lastIndexOf("<") );
		String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
		CTBody makeBody = CTBody.Factory.parse(prefix+mainPart+addPart+sufix);
		src.set(makeBody);
	}

	private static long chunk = 0;
	private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

	public void mergeDocx4J(InputStream s1, InputStream s2, OutputStream os) throws Exception {
		WordprocessingMLPackage target = WordprocessingMLPackage.load(s1);
		insertDocx(target.getMainDocumentPart(), IOUtils.toByteArray(s2));
		 org.docx4j.openpackaging.io3.Save saver = new  org.docx4j.openpackaging.io3.Save(target);
		FileOutputStream out = new FileOutputStream("mergeddocx4.docx");
		saver.save(out);
	}

	private static void insertDocx(MainDocumentPart main, byte[] bytes) throws Exception {
		AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + (chunk++) + ".docx"));
		afiPart.setContentType(new ContentType(CONTENT_TYPE));
		afiPart.setBinaryData(bytes);
		Relationship altChunkRel = main.addTargetPart(afiPart);

		CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
		chunk.setId(altChunkRel.getId());

		main.addObject(chunk);
	}


	private void createCoverPageForDocx4j(IGDocument igdoc, WordprocessingMLPackage wordMLPackage, ObjectFactory factory){
		Profile p = igdoc.getProfile();

		BufferedImage image = null;
		try {
			URL url = new URL("http://hit-2015.nist.gov/docs/hl7Logo.png");
			image = ImageIO.read(url);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();

			addImageToPackage(wordMLPackage, imageInByte);
		} catch (Exception e) {
			logger.warn("Unable to add image");
			e.printStackTrace();
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", igdoc.getMetaData().getTitle());
		addLineBreak(wordMLPackage, factory);
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle", "Subtitle " + igdoc.getMetaData().getSubTitle());
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", igdoc.getMetaData().getDate());
		addLineBreak(wordMLPackage, factory);
		addLineBreak(wordMLPackage, factory);
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", "HL7 Version " + p.getMetaData().getHl7Version());
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", "Document Version "
				+ igdoc.getMetaData().getVersion());
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", p.getMetaData().getOrgName());

		addPageBreak(wordMLPackage, factory);	
	}

	private void createTableOfContentForDocx4j(WordprocessingMLPackage wordMLPackage, ObjectFactory factory){
		P paragraphForTOC = factory.createP();		       
		R r = factory.createR();

		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);
		fldchar.setDirty(true);
		r.getContent().add(getWrappedFldChar(fldchar));
		paragraphForTOC.getContent().add(r);

		R r1 = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("TOC \\o \"1-3\" \\h \\z \\u \\h");
		r.getContent().add(factory.createRInstrText(txt) );
		paragraphForTOC.getContent().add(r1);

		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R r2 = factory.createR();
		r2.getContent().add(getWrappedFldChar(fldcharend));
		paragraphForTOC.getContent().add(r2);

		wordMLPackage.getMainDocumentPart().getContent().add(paragraphForTOC);
		addPageBreak(wordMLPackage, factory);
	}

	private void loadTemplateForDocx4j(WordprocessingMLPackage wordMLPackage){
		try {
			// Replace dotx content type with docx
			ContentTypeManager ctm = wordMLPackage.getContentTypeManager();

			// Get <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml"/>
			CTOverride override;
			override = ctm.getOverrideContentType().get(new URI("/word/document.xml"));
			override.setContentType(org.docx4j.openpackaging.contenttype.ContentTypes.WORDPROCESSINGML_DOCUMENT);

			// Create settings part, and init content
			DocumentSettingsPart dsp = new DocumentSettingsPart();
			CTSettings settings = Context.getWmlObjectFactory().createCTSettings();
			dsp.setJaxbElement(settings);
			wordMLPackage.getMainDocumentPart().addTargetPart(dsp);

			// Create external rel
			RelationshipsPart rp = RelationshipsPart.createRelationshipsPartForPart(dsp); 		
			org.docx4j.relationships.Relationship rel = new org.docx4j.relationships.ObjectFactory().createRelationship();
			rel.setType( Namespaces.ATTACHED_TEMPLATE  );
			//			String templatePath = "/rendering/lri_template.dotx";
			URL templateData = getClass().getResource("/rendering/lri_template.dotx");
			rel.setTarget(templateData.getPath());
			rel.setTargetMode("External");  		
			rp.addRelationship(rel); // addRelationship sets the rel's @Id

			settings.setAttachedTemplate(
					(CTRel)XmlUtils.unmarshalString("<w:attachedTemplate xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + rel.getId() + "\"/>", Context.jc, CTRel.class)
					);

		} catch (URISyntaxException | JAXBException | Docx4JException e1) {
			e1.printStackTrace();
		} 
	}

	private void addConformanceInformationForDocx4j(IGDocument igdoc, WordprocessingMLPackage wordMLPackage, ObjectFactory factory){
		Profile p = igdoc.getProfile();
		List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
		Collections.sort(messagesList);
		List<Datatype> datatypeList = new ArrayList<Datatype>(p.getDatatypes().getChildren());
		Collections.sort(datatypeList);
		List<Segment> segmentsList = new ArrayList<Segment>(p.getSegments().getChildren());
		Collections.sort(segmentsList);

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "Conformance information");
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", "Conditional predicates");
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreMessage(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreGroup(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
		for (Segment s: segmentsList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreSegment(rows, s);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(s.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
		for (Datatype dt:datatypeList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreDatatype(rows, dt);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(dt.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", "Conformance statements");

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsMessage(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsGroup(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
		for (Segment s: segmentsList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsSegment(rows, s);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(s.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
		for (Datatype dt:datatypeList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsDatatype(rows, dt);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(dt.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

	}

	@SuppressWarnings("unused")
	private InputStream exportAsDocxWithDocx4J(IGDocument igdoc) {
		Profile p = igdoc.getProfile();
		WordprocessingMLPackage wordMLPackage;
		try {
			wordMLPackage = WordprocessingMLPackage.load(this.getClass()
					.getResourceAsStream("/rendering/lri_template.dotx"));
		} catch (Docx4JException e1) {
			e1.printStackTrace();
			return new NullInputStream(1L);
		}

		// Replace dotx content type with docx
		ContentTypeManager ctm = wordMLPackage.getContentTypeManager();

		// Get <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml"/>
		CTOverride override;
		try {
			override = ctm.getOverrideContentType().get(new URI("/word/document.xml"));

			override.setContentType(org.docx4j.openpackaging.contenttype.ContentTypes.WORDPROCESSINGML_DOCUMENT);

			// Create settings part, and init content
			DocumentSettingsPart dsp = new DocumentSettingsPart();
			CTSettings settings = Context.getWmlObjectFactory().createCTSettings();
			dsp.setJaxbElement(settings);
			wordMLPackage.getMainDocumentPart().addTargetPart(dsp);

			// Create external rel
			RelationshipsPart rp = RelationshipsPart.createRelationshipsPartForPart(dsp); 		
			org.docx4j.relationships.Relationship rel = new org.docx4j.relationships.ObjectFactory().createRelationship();
			rel.setType( Namespaces.ATTACHED_TEMPLATE  );
			String templatePath = "/rendering/lri_template.dotx";
			rel.setTarget(templatePath);
			rel.setTargetMode("External");  		
			rp.addRelationship(rel); // addRelationship sets the rel's @Id

			settings.setAttachedTemplate(
					(CTRel)XmlUtils.unmarshalString("<w:attachedTemplate xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\"" + rel.getId() + "\"/>", Context.jc, CTRel.class)
					);

		} catch (URISyntaxException | InvalidFormatException | JAXBException e1) {
			e1.printStackTrace();
		} // note this assumption


		ObjectFactory factory = Context.getWmlObjectFactory();   

		BufferedImage image = null;
		try {
			URL url = new URL("http://hit-2015.nist.gov/docs/hl7Logo.png");
			image = ImageIO.read(url);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();

			addImageToPackage(wordMLPackage, imageInByte);
		} catch (Exception e) {
			logger.warn("Unable to add image");
			e.printStackTrace();
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", igdoc.getMetaData().getTitle());
		addLineBreak(wordMLPackage, factory);
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle", "Subtitle " + igdoc.getMetaData().getSubTitle());
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", igdoc.getMetaData().getDate());
		addLineBreak(wordMLPackage, factory);
		addLineBreak(wordMLPackage, factory);
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", "HL7 Version " + p.getMetaData().getHl7Version());
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", "Document Version "
				+ igdoc.getMetaData().getVersion());
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", p.getMetaData().getOrgName());

		addPageBreak(wordMLPackage, factory);

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", "TABLE OF CONTENTS");
		P paragraphForTOC = factory.createP();		       
		R r = factory.createR();

		FldChar fldchar = factory.createFldChar();
		fldchar.setFldCharType(STFldCharType.BEGIN);
		fldchar.setDirty(true);
		r.getContent().add(getWrappedFldChar(fldchar));
		paragraphForTOC.getContent().add(r);

		R r1 = factory.createR();
		Text txt = new Text();
		txt.setSpace("preserve");
		txt.setValue("TOC \\o \"1-3\" \\h \\z \\u \\h");
		r.getContent().add(factory.createRInstrText(txt) );
		paragraphForTOC.getContent().add(r1);

		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R r2 = factory.createR();
		r2.getContent().add(getWrappedFldChar(fldcharend));
		paragraphForTOC.getContent().add(r2);

		//To uncomment
		wordMLPackage.getMainDocumentPart().getContent().add(paragraphForTOC);
		addPageBreak(wordMLPackage, factory);

		traverseIGDocument4Docx(igdoc, wordMLPackage); 

		addPageBreak(wordMLPackage, factory);
		if (p.getSectionTitle() != null){
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", p.getSectionTitle());
		} else { 
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", "");
		}

		// Including information regarding messages
		if (p.getMessages().getSectionTitle() != null) {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", p.getMessages().getSectionTitle());
		} else {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
		}

		p.getMessages().setPositionsOrder();
		List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
		Collections.sort(messagesList);
		for (Message m : messagesList) {
			//			String messageInfo = m.getMessageType() + "^"
			//					+ m.getEvent() + "^" + m.getStructID() + " - " + m.getDescription();
			//			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
			//					messageInfo);
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
					m.getName());

			addRichTextToDocx(wordMLPackage, m.getComment());

			List<String> header = Arrays.asList("Segment", "Flavor", "Element Name", "Usage",
					"Card.", "Description/Comments");
			List<Integer> widths = Arrays.asList(1200, 1000, 2000, 800, 900, 3000);

			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addMessage(rows, m, p);
			wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));

			addRichTextToDocx(wordMLPackage, m.getUsageNote());
			addPageBreak(wordMLPackage, factory);
		}

		// Including information regarding segments 
		p.getSegments().setPositionsOrder();
		List<Segment> segmentsList = new ArrayList<Segment>(p.getSegments().getChildren());
		Collections.sort(segmentsList);
		if (p.getSegments().getSectionTitle() != null) {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", p.getSegments().getSectionTitle());
		} else {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
		}

		for (Segment s: segmentsList){
			String segmentInfo = s.getLabel() + " - " + s.getDescription();
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
					segmentInfo);

			// Add segment details
			addRichTextToDocx(wordMLPackage, s.getText1());

			List<String> header = Arrays.asList("Seq", "Element Name", "DT",
					"Usage", "Card.", "Length", "Value Set", "Description/Comments");
			List<Integer> widths = Arrays.asList(600, 2000, 900, 800, 800, 1000, 1000, 3000);
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			this.addSegment(rows, s, Boolean.TRUE, p.getDatatypes(), p.getTables());
			wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocxWithConstraints(header, widths, rows, wordMLPackage, factory));

			addRichTextToDocx(wordMLPackage, s.getText2());

			// Add field texts
			List<Field> fieldsList = s.getFields();
			Collections.sort(fieldsList);
			for (Field f : fieldsList) {
				if (f.getText() != null && f.getText().length() != 0) {
					wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", s.getName() + "-"
							+ f.getItemNo().replaceFirst("^0+(?!$)", "") + " "
							+ f.getName() + " ("
							+ p.getDatatypes().findOne(f.getDatatype()).getLabel() + ")");
					wordMLPackage.getMainDocumentPart().addParagraphOfText(f.getText());
				}
			}
			addPageBreak(wordMLPackage, factory);
		}

		// Including information regarding data types
		p.getDatatypes().setPositionsOrder();
		List<Datatype> datatypeList = new ArrayList<Datatype>(p.getDatatypes().getChildren());
		Collections.sort(datatypeList);
		if (p.getDatatypes().getSectionTitle() != null) {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", p.getDatatypes().getSectionTitle());
		} else {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
		}

		for (Datatype d: datatypeList){
			String dtInfo = d.getLabel() + " - " + d.getDescription();
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
					dtInfo);

			wordMLPackage.getMainDocumentPart().addParagraphOfText(d.getComment());

			List<String> header = Arrays.asList("Seq", "Element Name", "Conf\nlength", "DT",
					"Usage", "Length", "Value\nSet", "Comment");
			List<Integer> widths = Arrays.asList(600, 2000, 900, 750, 900, 900, 1000, 3000);
			List<List<String>> rows = new ArrayList<List<String>>();
			this.addDatatype(rows, d, p.getDatatypes(), p.getTables());
			wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocxWithConstraints(header, widths, rows, wordMLPackage, factory));
		}
		addPageBreak(wordMLPackage, factory);

		// Including information regarding value sets 
		p.getTables().setPositionsOrder();
		List<Table> tables = new ArrayList<Table>(p.getTables().getChildren());
		Collections.sort(tables);
		if (p.getTables().getSectionTitle() != null) {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", p.getTables().getSectionTitle());
		} else {
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
		}

		for (Table t : tables) {
			String valuesetInfo = t.getBindingIdentifier()
					+ " - " + t.getDescription();
			wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
					valuesetInfo);

			StringBuilder sb;
			sb = new StringBuilder();
			sb.append("\nOid: ");
			sb.append(t.getOid()==null||t.getOid().isEmpty() ? "UNSPECIFIED":t.getOid());
			wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());
			//				sb = new StringBuilder();
			//				sb.append("\nStability: ");
			//				sb.append(t.getStability()==null ? "Static":t.getStability());
			//				wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());
			//				sb = new StringBuilder();
			//				sb.append("\nExtensibility: ");
			//				sb.append(t.getExtensibility() == null ? "Closed":t.getExtensibility());
			//				wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());
			//				sb = new StringBuilder();
			//				sb.append("\nContent definition: ");
			//				sb.append(t.getContentDefinition() == null ? "Extensional":t.getContentDefinition());
			//				wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());

			List<String> header = Arrays.asList("Value", "Code system", "Usage", "Description");
			List<Integer> widths = Arrays.asList(1500, 1500, 1000, 5000);
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			this.addValueSet(rows, t);
			wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));

		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "Conformance information");
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", "Conditional predicates");
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreMessage(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreGroup(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
		for (Segment s: segmentsList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreSegment(rows, s);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(s.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
		for (Datatype dt:datatypeList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addPreDatatype(rows, dt);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(dt.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Usage", "Description");
				List<Integer> widths = Arrays.asList(1500, 1500, 6000);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}
		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", "Conformance statements");

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsMessage(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
		for (Message m : messagesList) { 
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsGroup(rows, m);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(m.getName(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
		for (Segment s: segmentsList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsSegment(rows, s);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(s.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
		for (Datatype dt:datatypeList){
			ArrayList<List<String>> rows = new ArrayList<List<String>>();
			addCsDatatype(rows, dt);
			if (!rows.isEmpty()){
				addLineBreak(wordMLPackage, factory);
				addParagraph(dt.getLabel(), wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				List<String> header = Arrays.asList("Location", "Description");
				List<Integer> widths = Arrays.asList(1500, 7500);
				wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
			}
		}

		FieldUpdater updater = new FieldUpdater(wordMLPackage);
		try {
			updater.update(true);
		} catch (Docx4JException e1) {
			e1.printStackTrace();
		}

		File tmpFile;
		try {
			tmpFile = File.createTempFile("Profile", ".docx");
			wordMLPackage.save(tmpFile);

			return FileUtils.openInputStream(tmpFile);
		} catch (IOException | Docx4JException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		} catch (Exception e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	private static Tbl createTableDocx(List<String> header, List<Integer> widths, List<List<String>> rows, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Tbl table = factory.createTbl();
		Tr tableRow;

		tableRow = factory.createTr();
		Integer width = null;

		if (widths != null && header != null && header.size() == widths.size()) {
			for (String cell: header){				
				width = widths.get(header.indexOf(cell));
				addTableCell(tableRow, cell, width, headerBackground, wordMLPackage, factory);
			}
			table.getContent().add(tableRow);
			for (List<String> row: rows){
				tableRow = factory.createTr();
				for (String cell: row){
					width = widths.get(row.indexOf(cell));
					addTableCell(tableRow, cell, width, null, wordMLPackage, factory);
				} 
				table.getContent().add(tableRow);
			}
			addBorders(table);
		}
		return table;
	}

	private static Tbl createTableDocxWithConstraints(List<String> header, List<Integer> widths, List<List<String>> rows, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Tbl table = factory.createTbl();
		Tr tableRow;

		tableRow = factory.createTr();
		Integer width = null;
		if (widths != null && header != null && header.size() == widths.size()) {
			for (String cell: header){				
				width = widths.get(header.indexOf(cell));
				addTableCell(tableRow, cell, width, headerBackground, wordMLPackage, factory);
			}
			table.getContent().add(tableRow);

			if (!rows.isEmpty()){ 
				int nbOfColumns = rows.get(0).size(); 
				for (List<String> row: rows){
					tableRow = factory.createTr();
					if (row.size() == nbOfColumns){ 
						//case "normal" row
						for (String cell: row){
							addTableCell(tableRow, cell, null, null, wordMLPackage, factory);
						}
					} else { 
						//case "constraints" row
						tableRow.getContent().add(createTableCell(row.get(0), null, null, wordMLPackage, factory));
						tableRow.getContent().add(createTableCell(row.get(1), null, constraintBackground, wordMLPackage, factory));
						tableRow.getContent().add(createTableCellGspan(row.get(2), nbOfColumns - 2, constraintBackground, wordMLPackage, factory));
					}
					table.getContent().add(tableRow);
				}
			}
			addBorders(table);
		}
		return table;
	}

	private static void addBorders(Tbl table) {
		TblPr tableProps = new TblPr();
		CTTblLayoutType tblLayoutType = new CTTblLayoutType();
		tableProps.setTblLayout(tblLayoutType);
		table.setTblPr(tableProps);
		//		STTblLayoutType stTblLayoutType = STTblLayoutType.AUTOFIT;
		STTblLayoutType stTblLayoutType = STTblLayoutType.FIXED;
		tblLayoutType.setType(stTblLayoutType);

		CTBorder border1 = new CTBorder();
		border1.setColor(tableVSeparator);
		border1.setSz(new BigInteger("4"));
		border1.setSpace(new BigInteger("0"));
		border1.setVal(STBorder.SINGLE);
		CTBorder border2 = new CTBorder();
		border2.setColor(tableHSeparator);
		border2.setSz(new BigInteger("4"));
		border2.setSpace(new BigInteger("0"));
		border2.setVal(STBorder.SINGLE);

		TblBorders borders = new TblBorders();
		borders.setBottom(border1);
		borders.setLeft(border1);
		borders.setRight(border1);
		borders.setTop(border1);
		borders.setInsideH(border2);
		borders.setInsideV(border1);
		table.getTblPr().setTblBorders(borders);
	}

	private static void addTableCell(Tr tableRow, String content, Integer width, String backgroundColor, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Tc tableCell = factory.createTc();
		TcPr tcpr = factory.createTcPr();
		tableCell.setTcPr(tcpr);
		if (width != null){
			setCellWidth(tcpr, width, factory);
		}
		if (backgroundColor != null){
			setCellShading(tcpr, backgroundColor, factory);
		}
		setCellNoWrap(tcpr, true);
		tableCell.getContent().add(
				wordMLPackage.getMainDocumentPart().
				createParagraphOfText(content));
		tableRow.getContent().add(tableCell);
	}

	private static Tc createTableCell(String content, Integer width, String backgroundColor, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Tc tableCell = factory.createTc();
		TcPr tcpr = factory.createTcPr();
		tableCell.setTcPr(tcpr);
		tableCell.getContent().add(wordMLPackage.getMainDocumentPart().
				createParagraphOfText(content)
				);
		if (width != null){
			setCellWidth(tcpr, width, factory);
		}
		if (backgroundColor != null){
			setCellShading(tcpr, backgroundColor, factory);
		}
		return tableCell;
	}

	private static Tc createTableCellGspan(String content, int gridspan, String backgroundColor, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Tc tc = factory.createTc();
		TcPr tcpr = factory.createTcPr();
		tc.setTcPr(tcpr);
		org.docx4j.wml.CTVerticalJc valign = factory.createCTVerticalJc();
		valign.setVal(STVerticalJc.TOP);
		tcpr.setVAlign(valign);
		if (backgroundColor != null){
			setCellShading(tcpr, backgroundColor, factory);
		}
		GridSpan gspan = factory.createTcPrInnerGridSpan();
		gspan.setVal(new BigInteger("" + gridspan));
		tcpr.setGridSpan(gspan);
		tc.getContent().add(wordMLPackage.getMainDocumentPart().
				createParagraphOfText(content));
		return tc;
	}

	private static void setCellWidth(TcPr tableCellProperties, int width, ObjectFactory factory) {
		TblWidth tableWidth = new TblWidth();
		tableWidth.setW(BigInteger.valueOf(width));
		tableCellProperties.setTcW(tableWidth);
	}

	private static void setCellNoWrap(TcPr tableCellProperties, Boolean value) {
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(value);
		tableCellProperties.setNoWrap(b);
	}

	private static void setCellShading(TcPr tcpr, String color, ObjectFactory factory){
		CTShd shading=factory.createCTShd();
		tcpr.setShd(shading);
		//		shading.setColor("Red");
		shading.setVal(org.docx4j.wml.STShd.CLEAR);
		shading.setFill(color); 
	}


	@SuppressWarnings("unused")
	private void setFontSize(RPr runProperties, String fontSize) {
		if (fontSize != null && !fontSize.isEmpty()) {
			HpsMeasure size = new HpsMeasure();
			size.setVal(new BigInteger(fontSize));
			runProperties.setSz(size);
			runProperties.setSzCs(size);
		}
	}

	@SuppressWarnings("unused")
	private void setFontFamily(RPr runProperties, String fontFamily) {
		if (fontFamily != null) {
			RFonts rf = runProperties.getRFonts();
			if (rf == null) {
				rf = new RFonts();
				runProperties.setRFonts(rf);
			}
			rf.setAscii(fontFamily);
		}
	}

	private void setFontColor(RPr runProperties, String color) {
		if (color != null) {
			Color c = new Color();
			c.setVal(color);
			runProperties.setColor(c);
		} 
	}

	private static void setHorizontalAlignment(P paragraph, JcEnumeration hAlign) {
		if (hAlign != null) {
			PPr pprop = new PPr();
			Jc align = new Jc();
			align.setVal(hAlign);
			pprop.setJc(align);
			paragraph.setPPr(pprop);
		}
	}

	private void addBoldStyle(RPr runProperties) {
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(true);
		runProperties.setB(b);
	}

	@SuppressWarnings("unused")
	private void addItalicStyle(RPr runProperties) {
		BooleanDefaultTrue b = new BooleanDefaultTrue();
		b.setVal(true);
		runProperties.setI(b);
	}

	@SuppressWarnings("unused")
	private void addUnderlineStyle(RPr runProperties) {
		U val = new U();
		val.setVal(UnderlineEnumeration.SINGLE);
		runProperties.setU(val);
	}

	private void addLineBreak(WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Br breakObj = new Br();
		breakObj.setType(STBrType.TEXT_WRAPPING);

		P paragraph = factory.createP();
		paragraph.getContent().add(breakObj);
		wordMLPackage.getMainDocumentPart().getContent().add(paragraph);
	}

	private void addPageBreak(WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
		Br breakObj = new Br();
		breakObj.setType(STBrType.PAGE);

		P paragraph = factory.createP();
		paragraph.getContent().add(breakObj);
		wordMLPackage.getMainDocumentPart().getContent().add(paragraph);
	}

	private void addParagraph(String content, WordprocessingMLPackage wordMLPackage, ObjectFactory factory){
		P paragraph = factory.createP();
		R run = factory.createR();

		Text text = factory.createText();
		text.setValue(content);
		run.getContent().add(text);

		paragraph.getContent().add(run);
		setHorizontalAlignment(paragraph, JcEnumeration.LEFT);

		RPr runProperties = factory.createRPr();
		addBoldStyle(runProperties);
		setFontColor(runProperties, headerFontColor);                
		//		addItalicStyle(runProperties);
		//		addUnderlineStyle(runProperties);
		//		setFontFamily(runProperties, "Arial");
		//		setFontSize(runProperties, "14");                
		run.setRPr(runProperties);
		wordMLPackage.getMainDocumentPart().addObject(paragraph);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JAXBElement getWrappedFldChar(FldChar fldchar) {
		return new JAXBElement( new QName(Namespaces.NS_WORD12, "fldChar"), 
				FldChar.class, fldchar);
	}

	private String wrapRichText(String htmlString){
		//Adds html tags so that string can be decoded in docx export
		Tidy tidy = new Tidy();
		tidy.setWraplen(Integer.MAX_VALUE);
		tidy.setXHTML(true);
		tidy.setShowWarnings(false); //to hide errors
		tidy.setQuiet(true); //to hide warning
		InputStream inputStream = new ByteArrayInputStream(htmlString.getBytes());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		tidy.parseDOM(inputStream, outputStream);

		StringBuilder rst = new StringBuilder("<html><head></head><body></body>");
		return rst.insert(25, outputStream.toString()).toString();
	}

	private void addRichTextToDocx(WordprocessingMLPackage wordMLPackage, String htmlString){
		try {
			wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Xhtml,
					wrapRichText(htmlString).getBytes());
		} catch (Docx4JException e1) {
			e1.printStackTrace();
			wordMLPackage.getMainDocumentPart().addParagraphOfText("Error in rich text");
		}
	}

	public void traverseIGDocument4Docx(IGDocument d, WordprocessingMLPackage wordMLPackage){

		addContents4Docx((Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section>) d.getChildSections(), "", 1, wordMLPackage);

	}

	@SuppressWarnings("unchecked")
	private void addContents4Docx(Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sect, String prefix, Integer depth, WordprocessingMLPackage wordMLPackage){
		SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortedSections = sortSections(sect);
		for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section s: sortedSections){
			if (s.getSectionTitle() != null) {
				wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading"+String.valueOf(depth), s.getSectionTitle());
			} else {
				wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading"+String.valueOf(depth), "");
			}
			if (s.getSectionContents() != null){
				addRichTextToDocx(wordMLPackage, s.getSectionContents());
			}
			addContents4Docx((Set<Section>)s.getChildSections(), String.valueOf(s.getSectionPosition()+1), depth + 1, wordMLPackage);
		}

	}

	public void traverseIGDocument4Pdf(IGDocument d, Document tocDocument, Document igDocument, Font titleFont, PdfWriter igWriter){

		addContents4Pdf((Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section>) d.getChildSections(), "", 1, tocDocument, igDocument, null, titleFont, igWriter);

	}

	@SuppressWarnings("unchecked")
	private void addContents4Pdf(Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sect, String prefix, Integer depth, Document tocDocument, Document igDocument, com.itextpdf.text.Section chapt, Font titleFont, PdfWriter igWriter){
		SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortedSections = sortSections(sect);

		for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section s: sortedSections){
			if (depth == 1){
				try {
					tocDocument.add(Chunk.NEWLINE);
					if (s.getSectionTitle() != null) {
						Chunk link = new Chunk(String.valueOf(s.getSectionPosition()+1) + " " + s.getSectionTitle(), titleFont).setLocalGoto(s.getId());
						tocDocument.add(new Paragraph(link));
					} else {
						Chunk link = new Chunk(String.valueOf(s.getSectionPosition()+1) + " ", titleFont).setLocalGoto(s.getId());
						tocDocument.add(new Paragraph(link));
					}

					//					tocDocument.add(new Paragraph(String.valueOf(s.getSectionPosition()+1) + " " + s.getSectionTitle(), titleFont));
					tocDocument.add(Chunk.NEWLINE);

					Chunk target;
					if (s.getSectionTitle() != null) {
						target = new Chunk(s.getSectionTitle(), titleFont).setLocalDestination(s.getId());
					} else {
						target = new Chunk(" ", titleFont).setLocalDestination(s.getId());
					}
					//					Paragraph par = new Paragraph(s.getSectionTitle(), titleFont);
					Chapter chapter = new Chapter(new Paragraph(target), s.getSectionPosition()+1);
					chapter.add(Chunk.NEWLINE);
					if (s.getSectionContents() != null){
						chapter.add(richTextToParagraph(s.getSectionContents()));
					}
					chapter.add(Chunk.NEWLINE);

					addContents4Pdf((Set<Section>)s.getChildSections(), String.valueOf(s.getSectionPosition()+1), depth + 1, tocDocument, igDocument, chapter, titleFont, igWriter);

					igDocument.add(chapter); //Note: leave call after addContents4Pdf 
				} catch (DocumentException e) {
					e.printStackTrace();
				}
			} else {
				if (s.getSectionTitle() != null) {
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*depth) + prefix + "." + String.valueOf(s.getSectionPosition()) + " " + s.getSectionTitle(), s.getId());
				} else {
					this.addTocContent(tocDocument, igWriter, StringUtils.repeat(" ", 4*depth) + prefix + "." + String.valueOf(s.getSectionPosition()) + " ", s.getId());

				}
				//				Paragraph title = new Paragraph(s.getSectionTitle(), titleFont);
				Chunk target;
				if (s.getSectionTitle() != null) {
					target = new Chunk(s.getSectionTitle(), titleFont).setLocalDestination(s.getId());
				} else {
					target = new Chunk(" ", titleFont).setLocalDestination(s.getId());
				}

				com.itextpdf.text.Section section = chapt.addSection(new Paragraph(target));

				section.add(Chunk.NEWLINE);
				if (s.getSectionContents() != null){
					section.add(richTextToParagraph(s.getSectionContents()));
				}
				section.add(Chunk.NEWLINE);

				addContents4Pdf((Set<Section>)s.getChildSections(), prefix + "." + String.valueOf(s.getSectionPosition()), depth + 1, tocDocument, igDocument, section, titleFont, igWriter);
			}
		}
	}

	private SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortSections(Set<Section> s){
		SortedSet<Section> sortedSet = new TreeSet<Section>();
		Iterator<Section> setIt = s.iterator();
		while (setIt.hasNext()) {
			sortedSet.add((Section) setIt.next());
		}
		return sortedSet;
	}


	private static void addImageToPackage(WordprocessingMLPackage wordMLPackage,
			byte[] bytes) throws Exception {
		BinaryPartAbstractImage imagePart =
				BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

		int docPrId = 1;
		int cNvPrId = 2;
		Inline inline = imagePart.createImageInline("Filename hint",
				"Alternative text", docPrId, cNvPrId, false);

		P paragraph = addInlineImageToParagraph(inline);
		setHorizontalAlignment(paragraph, JcEnumeration.CENTER);

		wordMLPackage.getMainDocumentPart().addObject(paragraph);
	}

	private static P addInlineImageToParagraph(Inline inline) {
		// Now add the in-line image to a paragraph
		ObjectFactory factory = new ObjectFactory();
		P paragraph = factory.createP();
		R run = factory.createR();
		paragraph.getContent().add(run);
		Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		return paragraph;
	}
}

//class SectionComparator implements Comparator<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section>{
//
//	@Override
//	public int compare(Section o1, Section o2) {
//		return  ((Section) o1).getSectionPosition() - ((Section) o2).getSectionPosition();
//	}
//
//}