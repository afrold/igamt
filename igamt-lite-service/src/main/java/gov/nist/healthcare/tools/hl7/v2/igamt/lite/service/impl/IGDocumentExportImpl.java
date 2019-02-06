/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

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
import java.util.*;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ConstraintSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.ProfileSerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.TableSerializationException;

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
import org.docx4j.convert.in.xhtml.ImportXHTMLProperties;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
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
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileSerialization;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.DateUtils;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.MessageExportInfo;

@Service
public class IGDocumentExportImpl implements IGDocumentExportService {
  Logger logger = LoggerFactory.getLogger(IGDocumentExportImpl.class);

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  @Autowired
  private ProfileSerialization profileSerializationService;

  @Autowired
  private IGDocumentSerialization igDocumentSerializationService;

  // @Autowired
  // private UserService userService;

  static String constraintBackground = "#EDEDED";
  static String headerBackground = "#F0F0F0";
  static String headerFontColor = "#B21A1C";
  static String tableHSeparator = "#F01D1D";
  static String tableVSeparator = "#D3D3D3";

  static String inlineConstraints = "false";

  @Override
  public InputStream exportAsValidationForSelectedMessages(IGDocument d, List<MessageExportInfo> mids)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException, ConstraintSerializationException {
    if (d != null) {
      return profileSerializationService.serializeProfileToZip(d.getProfile(), mids,
          d.getMetaData());
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsGazelleForSelectedMessages(IGDocument d, List<MessageExportInfo> mids)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException {
    if (d != null) {
      return profileSerializationService.serializeProfileGazelleToZip(d.getProfile(), mids,
          d.getMetaData());
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDisplayForSelectedMessage(IGDocument d, List<MessageExportInfo> mids)
      throws IOException, CloneNotSupportedException, TableSerializationException, ProfileSerializationException {
    if (d != null) {
      return profileSerializationService.serializeProfileDisplayToZip(d.getProfile(), mids,
          d.getMetaData());
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsValidationForSelectedCompositeProfiles(IGDocument d, String[] cids)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException, ConstraintSerializationException {
    if (d != null) {
      return profileSerializationService.serializeCompositeProfileToZip(d, cids);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsGazelleForSelectedCompositeProfiles(IGDocument d, String[] cids)
      throws IOException, CloneNotSupportedException, ProfileSerializationException, TableSerializationException {
    if (d != null) {
      return profileSerializationService.serializeCompositeProfileGazelleToZip(d, cids);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDisplayForSelectedCompositeProfiles(IGDocument d, String[] cids)
      throws IOException, CloneNotSupportedException, TableSerializationException, ProfileSerializationException {
    if (d != null) {
      return profileSerializationService.serializeCompositeProfileDisplayToZip(d, cids);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocx(IGDocument d) {
    if (d != null) {
      InputStream is = exportAsDocxIG(d);
      return is;
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocxDatatypes(IGDocument d) {
    if (d != null) {
      InputStream is = exportAsDocxFromHtmlDatatypes(d, inlineConstraints);
      return is;
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsPdf(IGDocument d) throws SerializationException {
    if (d != null) {
      return exportAsHtmlFromXsl4Pdf(d, inlineConstraints); // TODO Use
                                                            // wkhtml2pdf
                                                            // in
                                                            // exportAsHtml4Pdf
                                                            // function
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsXlsx(IGDocument d) {
    if (d != null) {
      return exportAsXslxWithApachePOI(d.getProfile());
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsXmlDisplay(IGDocument d) throws SerializationException {
    if (d != null) {
      return exportAsXml(igDocumentSerializationService.serializeIGDocumentToXML(d));
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtml(IGDocument d) throws SerializationException {
    if (d != null) {
      return exportAsHtmlFromXsl(d, inlineConstraints);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtmlDatatypes(IGDocument d) {
    if (d != null) {
      return exportAsHtmlFromXslDatatypes(d, inlineConstraints);
    } else {
      return new NullInputStream(1L);
    }
  }

  public InputStream exportAsXmlSections(IGDocument ig) {
    if (ig != null) {
      return exportAsXml(igDocumentSerializationService.serializeSectionsToXML(ig));
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtmlSections(IGDocument ig) {
    if (ig != null) {
      return exportAsHtmlFromXsl(igDocumentSerializationService.serializeSectionsToXML(ig),
          "/rendering/xml2html.xsl");
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocxSections(IGDocument ig) {
    if (ig != null) {
      return exportAsDocxFromXml(igDocumentSerializationService.serializeSectionsToXML(ig),
          "/rendering/xml2html.xsl", true);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtmlDatatypeLibraryDocument(
      DatatypeLibraryDocument datatypeLibraryDocument) {
    if (datatypeLibraryDocument != null) {
      return exportAsHtmlFromXsl(igDocumentSerializationService
          .serializeDatatypeLibraryDocumentToXML(datatypeLibraryDocument),
          "/rendering/datatypeLibraryDoc2html.xsl");
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocxDatatypeLibraryDocument(
      DatatypeLibraryDocument datatypeLibraryDocument) {
    if (datatypeLibraryDocument != null) {
      return exportAsDocxFromXml(igDocumentSerializationService
          .serializeDatatypeLibraryDocumentToXML(datatypeLibraryDocument),
          "/rendering/datatypeLibraryDoc2word.xsl", true);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsXmlTable(TableLink tl) {
    if (tl != null) {
      return exportAsXml(igDocumentSerializationService.serializeTableToXML(tl));
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtmlTable(TableLink tl) {
    if (tl != null) {
      return exportAsHtmlFromXsl(igDocumentSerializationService.serializeTableToXML(tl),
          "/rendering/xml2html.xsl");
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocxTable(TableLink tl) {
    if (tl != null) {
      return exportAsDocxFromXml(igDocumentSerializationService.serializeTableToXML(tl),
          "/rendering/xml2html.xsl", false);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsXmlDatatype(DatatypeLink dl) {
    if (dl != null) {
      return exportAsXml(igDocumentSerializationService.serializeDatatypeToXML(dl));
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtmlDatatype(DatatypeLink dl) {
    if (dl != null) {
      return exportAsHtmlFromXsl(igDocumentSerializationService.serializeDatatypeToXML(dl),
          "/rendering/xml2html.xsl");
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocxDatatype(DatatypeLink dl) {
    if (dl != null) {
      return exportAsDocxFromXml(igDocumentSerializationService.serializeDatatypeToXML(dl),
          "/rendering/xml2html.xsl", false);
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsXmlSegment(SegmentLink sl) {
    if (sl != null) {
      return exportAsXml(igDocumentSerializationService.serializeSegmentToXML(sl));
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsHtmlSegment(SegmentLink sl) {
    if (sl != null) {
      return exportAsHtmlFromXsl(igDocumentSerializationService.serializeSegmentToXML(sl),
          "/rendering/xml2html.xsl");
    } else {
      return new NullInputStream(1L);
    }
  }

  @Override
  public InputStream exportAsDocxSegment(SegmentLink sl) {
    if (sl != null) {
      return exportAsDocxFromXml(igDocumentSerializationService.serializeSegmentToXML(sl),
          "/rendering/xml2html.xsl", false);
    } else {
      return new NullInputStream(1L);
    }
  }

  public InputStream exportAsXmlMessage(Message m) {
    if (m != null) {
      return exportAsXml(igDocumentSerializationService.serializeMessageToXML(m));
    } else {
      return new NullInputStream(1L);
    }
  }

  public InputStream exportAsHtmlMessage(Message m) {
    if (m != null) {
      return exportAsHtmlFromXsl(igDocumentSerializationService.serializeMessageToXML(m),
          "/rendering/xml2html.xsl");
    } else {
      return new NullInputStream(1L);
    }
  }

  public InputStream exportAsDocxMessage(Message m) {
    if (m != null) {
      return exportAsDocxFromXml(igDocumentSerializationService.serializeMessageToXML(m),
          "/rendering/xml2html.xsl", false);
    } else {
      return new NullInputStream(1L);
    }
  }

  // Functions to collect info
  // Messages
  private void addMessage(List<List<String>> rows, Message m, Profile p) {
    List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();

    for (SegmentRefOrGroup srog : segRefOrGroups) {
      if (srog instanceof SegmentRef) {
        this.addSegmentMsgInfra(rows, (SegmentRef) srog, 0, p.getSegmentLibrary());
      } else if (srog instanceof Group) {
        this.addGroupMsgInfra(rows, (Group) srog, 0, p.getSegmentLibrary(), p.getDatatypeLibrary());
      }
    }

    List<Constraint> constraints = new ArrayList<>();
    for (ConformanceStatement cs : m.getConformanceStatements()) {
      constraints.add(cs);
    }
    for (Predicate pre : m.getPredicates()) {
      constraints.add(pre);
    }
    this.addConstraints(rows, constraints);
  }

  private void addSegmentMsgInfra(List<List<String>> rows, SegmentRef s, Integer depth,
      SegmentLibrary segments) {
    String indent = StringUtils.repeat(".", 4 * depth);
    Segment segment = segmentService.findById(s.getRef().getId());
    List<String> row = Arrays.asList(indent + segment.getName(),
        segments.findOneSegmentById(s.getRef().getId()).getLabel().equals(segment.getName()) ? ""
            : segments.findOneSegmentById(s.getRef().getId()).getLabel(),
        segment.getDescription(), s.getUsage().value(),
        "[" + String.valueOf(s.getMin()) + ".." + String.valueOf(s.getMax()) + "]",
        segment.getComment() == null ? "" : segment.getComment());
    rows.add(row);
  }

  private void addGroupMsgInfra(List<List<String>> rows, Group g, Integer depth,
      SegmentLibrary segments, DatatypeLibrary datatypes) {
    String indent = StringUtils.repeat(".", 2 * depth);

    List<String> row =
        Arrays.asList(indent + "[", "", g.getName() + " GROUP BEGIN", g.getUsage().value(),
            "[" + String.valueOf(g.getMin()) + ".." + String.valueOf(g.getMax()) + "]", "");
    rows.add(row);

    List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
    Collections.sort(segsOrGroups);
    for (SegmentRefOrGroup srog : segsOrGroups) {
      if (srog instanceof SegmentRef) {
        this.addSegmentMsgInfra(rows, (SegmentRef) srog, depth + 1, segments);
      } else if (srog instanceof Group) {
        this.addGroupMsgInfra(rows, (Group) srog, depth + 1, segments, datatypes);
      }
    }
    row = Arrays.asList(indent + "]", "", g.getName() + " GROUP END", "", "", "");
    rows.add(row);
  }

  // Segments
  private void addSegment(List<List<String>> rows, Segment s, Boolean inlineConstraints,
      DatatypeLibrary datatypes, TableLibrary tables) {
    List<String> row;
    List<Predicate> predicates = s.getPredicates();
    List<ConformanceStatement> conformanceStatements = s.getConformanceStatements();

    List<Field> fieldsList = s.getFields();
    Collections.sort(fieldsList);
    for (Field f : fieldsList) {
      row = Arrays.asList(
          // f.getItemNo().replaceFirst("^0+(?!$)", ""),
          String.valueOf(f.getPosition()), f.getName(),
          (f.getDatatype() == null || f.getDatatype().getLabel() == null ? ""
              : (datatypes.findOne(f.getDatatype()) == null ? f.getDatatype().getLabel()
                  : datatypes.findOne(f.getDatatype()).getLabel())),
          f.getUsage().value(),
          "[" + String.valueOf(f.getMin()) + ".." + String.valueOf(f.getMax()) + "]",
          "[" + String.valueOf(f.getMinLength()) + ".." + String.valueOf(f.getMaxLength()) + "]",
          (tablesToString(null)), f.getComment() == null ? "" : f.getComment());
      rows.add(row);

      if (inlineConstraints) {
        List<Constraint> constraints =
            this.findConstraints(f.getPosition(), predicates, conformanceStatements);
        this.addConstraints(rows, constraints);
      }
    }
    if (!inlineConstraints) {
      for (Field f : fieldsList) {
        List<Constraint> constraints =
            this.findConstraints(f.getPosition(), predicates, conformanceStatements);
        this.addConstraints(rows, constraints);
      }
    }
  }

  private void addDatatype(List<List<String>> rows, Datatype d, DatatypeLibrary datatypes,
      TableLibrary tables) {
    List<String> row;
    List<Predicate> predicates = d.getPredicates();
    List<ConformanceStatement> conformanceStatements = d.getConformanceStatements();

    List<Component> componentsList = new ArrayList<>(d.getComponents());
    Collections.sort(componentsList);
    if (componentsList.size() == 0) {
      row = Arrays.asList("1", d.getName(), "", "", "", "", "", d.getComment());
      rows.add(row);
    } else {
      for (Component c : componentsList) {
        row = Arrays.asList(c.getPosition().toString(), c.getName(), c.getConfLength(),
            (c.getDatatype() == null
                || c.getDatatype().getLabel() == null || datatypes.findOne(c.getDatatype()) == null
                    ? "" : datatypes.findOne(c.getDatatype()).getLabel()),
            c.getUsage().value(),
            "[" + String.valueOf(c.getMinLength()) + ".." + String.valueOf(c.getMaxLength()) + "]",
            (tablesToString(null)), c.getComment());
        rows.add(row);
        List<Constraint> constraints =
            this.findConstraints(c.getPosition(), predicates, conformanceStatements);
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

  private void addValueSet(List<List<String>> rows, Table t) {
    List<String> row = new ArrayList<String>();
    List<Code> codes = t.getCodes();

    Collections.sort(codes);
    for (Code c : codes) {
      row = Arrays.asList(c.getValue(), c.getCodeSystem(), c.getCodeUsage(), c.getLabel());
      rows.add(row);
    }
  }

  private List<Constraint> findConstraints(Integer target, List<Predicate> predicates,
      List<ConformanceStatement> conformanceStatements) {
    List<Constraint> constraints = new ArrayList<>();
    for (Predicate pre : predicates) {
      if (target == Integer.parseInt(
          pre.getConstraintTarget().substring(0, pre.getConstraintTarget().indexOf('[')))) {
        constraints.add(pre);
      }
    }
    for (ConformanceStatement conformanceStatement : conformanceStatements) {
      if (target == Integer.parseInt(conformanceStatement.getConstraintTarget().substring(0,
          conformanceStatement.getConstraintTarget().indexOf('[')))) {
        constraints.add(conformanceStatement);
      }
    }
    return constraints;
  }

  private void addConstraints(List<List<String>> rows, List<Constraint> constraints) {
    if (!constraints.isEmpty()) {
      List<String> row;
      for (Constraint constraint : constraints) {
        String constraintType = new String();
        if (constraint instanceof Predicate) {
          constraintType = "Condition Predicate";
          row = Arrays.asList("", constraintType,
              "Usage : C(" + ((Predicate) constraint).getTrueUsage() + "/"
                  + ((Predicate) constraint).getFalseUsage() + ") \n Predicate: "
                  + constraint.getDescription());
          rows.add(row);
        } else if (constraint instanceof ConformanceStatement) {
          constraintType = "Conformance Statement";
          row = Arrays.asList("", constraintType, constraint.getDescription());
          rows.add(row);
        }

      }
    }
  }

  private void addCsMessage(List<List<String>> rows, Message m) {
    List<String> row;
    for (ConformanceStatement cs : m.getConformanceStatements()) {
      row = Arrays.asList(cs.getConstraintTarget(), cs.getDescription());
      rows.add(row);
    }
  }

  private void addPreMessage(List<List<String>> rows, Message m) {
    List<String> row;
    for (Predicate pre : m.getPredicates()) {
      row = Arrays.asList(pre.getConstraintTarget(),
          "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
      rows.add(row);
    }
  }

  private void addCsGroup(List<List<String>> rows, Message m) {
    List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
    List<Constraint> constraints = new ArrayList<>();
    List<String> row;

    for (SegmentRefOrGroup srog : segRefOrGroups) {
      if (srog instanceof Group) {
        for (ConformanceStatement cs : ((Group) srog).getConformanceStatements()) {
          row = Arrays.asList(cs.getConstraintTarget(), cs.getDescription());
          rows.add(row);
        }
        this.addCsGroupList(rows, (Group) srog);
      }
    }
    this.addConstraints(rows, constraints);
  }

  private void addCsGroupList(List<List<String>> rows, Group group) {
    List<String> row;
    for (SegmentRefOrGroup srog : group.getChildren()) {
      if (srog instanceof Group) {
        for (ConformanceStatement cs : ((Group) srog).getConformanceStatements()) {
          row = Arrays.asList(cs.getConstraintTarget(), cs.getDescription());
          rows.add(row);
        }
        this.addCsGroupList(rows, (Group) srog);
      }
    }
  }

  private void addPreGroup(List<List<String>> rows, Message m) {
    List<SegmentRefOrGroup> segRefOrGroups = m.getChildren();
    List<String> row;

    for (SegmentRefOrGroup srog : segRefOrGroups) {
      if (srog instanceof Group) {
        for (Predicate pre : ((Group) srog).getPredicates()) {
          row = Arrays.asList(pre.getConstraintTarget(),
              "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
          rows.add(row);
        }
        this.addPreGroupList(rows, (Group) srog);
      }
    }

  }

  private void addPreGroupList(List<List<String>> rows, Group group) {
    List<String> row;
    for (SegmentRefOrGroup srog : group.getChildren()) {
      if (srog instanceof Group) {
        for (Predicate pre : ((Group) srog).getPredicates()) {
          row = Arrays.asList(pre.getConstraintTarget(),
              "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
          rows.add(row);
        }
        this.addPreGroupList(rows, (Group) srog);
      }
    }
  }

  private void addCsSegment(List<List<String>> rows, Segment s) {
    List<String> row;
    for (ConformanceStatement cs : s.getConformanceStatements()) {
      row = Arrays.asList(cs.getConstraintTarget(), cs.getDescription());
      rows.add(row);
    }
  }

  private void addPreSegment(List<List<String>> rows, Segment s) {
    List<String> row;
    for (Predicate pre : s.getPredicates()) {
      row = Arrays.asList(pre.getConstraintTarget(),
          "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
      rows.add(row);
    }
  }

  private void addCsDatatype(List<List<String>> rows, Datatype dt) {
    List<String> row;
    for (ConformanceStatement cs : dt.getConformanceStatements()) {
      row = Arrays.asList(cs.getConstraintTarget(), cs.getDescription());
      rows.add(row);
    }
  }

  private void addPreDatatype(List<List<String>> rows, Datatype dt) {
    List<String> row;
    for (Predicate pre : dt.getPredicates()) {
      row = Arrays.asList(pre.getConstraintTarget(),
          "C(" + pre.getTrueUsage() + "/" + pre.getFalseUsage() + ")", pre.getDescription());
      rows.add(row);
    }
  }

  private InputStream exportAsXslxWithApachePOI(Profile p) {
    logger.debug("Export profile id: " + p.getId() + " as xslx");
    try {
      File tmpXlsxFile = File.createTempFile("ProfileTmp" + UUID.randomUUID().toString(), ".xslx");

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
      headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
      headerStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());

      // Adding list of elements
      sheet = workbook.createSheet("Message list");
      header = Arrays.asList("Messages");
      rows = new ArrayList<List<String>>();
      rows.add(header);
      row = new ArrayList<String>();

      List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
      Collections.sort(messagesList);
      for (Message m : messagesList) {
        row = Arrays.asList(m.getName() + " - " + m.getDescription());
        rows.add(row);
      }
      this.writeToSheet(rows, header, sheet, headerStyle);

      sheet = workbook.createSheet("Segment list");
      header = Arrays.asList("Segments");
      rows = new ArrayList<List<String>>();
      rows.add(header);
      row = new ArrayList<String>();
      List<SegmentLink> segmentList =
          new ArrayList<SegmentLink>(p.getSegmentLibrary().getChildren());
      // Collections.sort(segmentList);
      for (SegmentLink sl : segmentList) {
        Segment s = segmentService.findById(sl.getId());
        row = Arrays.asList(sl.getLabel() + " - " + s.getDescription());
        rows.add(row);
      }
      this.writeToSheet(rows, header, sheet, headerStyle);

      sheet = workbook.createSheet("Datatype list");
      header = Arrays.asList("Datatypes");
      rows = new ArrayList<List<String>>();
      rows.add(header);
      row = new ArrayList<String>();
      List<DatatypeLink> datatypeList =
          new ArrayList<DatatypeLink>(p.getDatatypeLibrary().getChildren());
      Collections.sort(datatypeList);
      for (DatatypeLink dl : datatypeList) {
        Datatype dt = datatypeService.findById(dl.getLabel());
        row = Arrays.asList(dl.getLabel() + " - " + dt.getDescription());
        rows.add(row);
      }
      this.writeToSheet(rows, header, sheet, headerStyle);

      sheet = workbook.createSheet("Value set list");
      header = Arrays.asList("Value sets");
      rows = new ArrayList<List<String>>();
      rows.add(header);
      row = new ArrayList<String>();
      List<TableLink> tableList = new ArrayList<TableLink>(p.getTableLibrary().getChildren());
      Collections.sort(tableList);
      for (TableLink tl : tableList) {
        Table t = tableService.findById(tl.getId());
        row = Arrays.asList(tl.getBindingIdentifier() + " - " + t.getName());
        rows.add(row);
      }
      this.writeToSheet(rows, header, sheet, headerStyle);

      // Adding messages
      for (Message m : messagesList) {
        sheetName = "MSG_" + m.getMessageType() + "_" + m.getStructID();
        if (sheetNames.contains(sheetName)) {
          logger.debug(sheetName + " already added!!");
        } else {
          sheetNames.add(sheetName);
          sheet = workbook.createSheet(sheetName); // Sheet name must
                                                   // be unique

          header = Arrays.asList("SEGMENT", "CDC Usage", "Local Usage", "CDC Cardinality",
              "Local Cardinality", "Comments");
          rows = new ArrayList<List<String>>();
          rows.add(header);

          for (SegmentRefOrGroup srog : m.getChildren()) {
            if (srog instanceof SegmentRef) {
              this.addSegmentInfoXlsx(rows, (SegmentRef) srog, 0, p.getSegmentLibrary());
            } else if (srog instanceof Group) {
              this.addGroupInfoXlsx(rows, (Group) srog, 0, p.getSegmentLibrary(),
                  p.getDatatypeLibrary());
            }
          }
          this.writeToSheet(rows, header, sheet, headerStyle);
        }
      }

      // Adding segments
      for (SegmentLink sl : segmentList) {
        Segment s = segmentService.findById(sl.getId());
        rows = new ArrayList<List<String>>();
        header =
            Arrays.asList("Segment", "Name", "DT", "Usage", "Card.", "Len", "Value set", "Comment");
        sheetName = "SGT_" + sl.getLabel();
        if (sheetNames.contains(sheetName)) {
          logger.debug(sheetName + " already added!!");
        } else {
          sheetNames.add(sheetName);
          sheet = workbook.createSheet(sheetName);
          rows.add(header);
          this.addSegment(rows, s, Boolean.FALSE, p.getDatatypeLibrary(), p.getTableLibrary());
          this.writeToSheet(rows, header, sheet, headerStyle);
        }
      }

      // Adding datatypes
      for (DatatypeLink dl : datatypeList) {
        Datatype dt = datatypeService.findById(dl.getId());
        rows = new ArrayList<List<String>>();
        header = Arrays.asList("Component", "Name", "Len.", "DT", "Usage", "Card.", "Value set",
            "Comment");
        sheetName = "DT_" + dl.getLabel();
        if (sheetNames.contains(sheetName)) {
          logger.debug(sheetName + " already added!!");
        } else {
          sheetNames.add(sheetName);
          sheet = workbook.createSheet(sheetName);
          rows.add(header);
          this.addDatatype(rows, dt, p.getDatatypeLibrary(), p.getTableLibrary());
          this.writeToSheet(rows, header, sheet, headerStyle);
        }
      }

      // Adding value sets
      for (TableLink tl : tableList) {
        Table t = tableService.findById(tl.getId());
        sheetName = "VS_" + tl.getBindingIdentifier();
        if (sheetNames.contains(sheetName)) {
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
      SegmentLibrary segments, DatatypeLibrary datatypes) {
    String indent = StringUtils.repeat(" ", 4 * depth);

    List<String> row =
        Arrays.asList(indent + "BEGIN " + g.getName() + " GROUP", g.getUsage().value(), "",
            "[" + String.valueOf(g.getMin()) + ".." + String.valueOf(g.getMax()) + "]", "", "");
    rows.add(row);
    List<SegmentRefOrGroup> segsOrGroups = g.getChildren();
    Collections.sort(segsOrGroups);
    for (SegmentRefOrGroup srog : segsOrGroups) {
      if (srog instanceof SegmentRef) {
        this.addSegmentInfoXlsx(rows, (SegmentRef) srog, depth + 1, segments);
      } else if (srog instanceof Group) {
        this.addGroupInfoXlsx(rows, (Group) srog, depth + 1, segments, datatypes);
      }
    }
    row = Arrays.asList(indent + "END " + g.getName() + " GROUP", "", "", "");
    rows.add(row);
  }

  private void addSegmentInfoXlsx(List<List<String>> rows, SegmentRef s, Integer depth,
      SegmentLibrary segments) {
    String indent = StringUtils.repeat(" ", 4 * depth);
    Segment segment = segmentService.findById(s.getRef().getId());
    List<String> row = Arrays.asList(indent + segment.getName(), s.getUsage().value(), "",
        "[" + String.valueOf(s.getMin()) + ".." + String.valueOf(s.getMax()) + "]", "",
        segment.getComment() == null ? "" : segment.getComment());
    rows.add(row);
  }

  private void writeToSheet(List<List<String>> rows, List<String> header, XSSFSheet sheet,
      XSSFCellStyle headerStyle) {
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

  public InputStream exportAsHtmlFromXsl(IGDocument igdoc, String inlineConstraints)
      throws SerializationException {
    // Note: inlineConstraint can be true or false

    try {
      File tmpHtmlFile = File.createTempFile("IGDocTemp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("IGDocTemp" + UUID.randomUUID().toString(), ".xml");
      // File tmpXmlFile = new File("IGDocTemp"+
      // UUID.randomUUID().toString()+".xml");
      String stringIgDoc = igDocumentSerializationService.serializeIGDocumentToXML(igdoc);
      FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc, Charset.forName("UTF-8"));

      TransformerFactory factory = TransformerFactory.newInstance();
      Source xslt =
          new StreamSource(this.getClass().getResourceAsStream("/rendering/igdoc2htmlWithTOC.xsl"));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factory.newTransformer(xslt);
      transformer.setParameter("inlineConstraints", inlineConstraints);
      transformer.setParameter("includeTOC", "true");

      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));
      return FileUtils.openInputStream(tmpHtmlFile);

    } catch (TransformerException | IOException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }

    // // Apply XSL transformation on xml file to generate html
    // File tmpHtmlFile = File.createTempFile("IGDocTemp", ".html");
    // // File tmpHtmlFile = new File("ProfileTemp.html");
    // Builder builder = new Builder();
    // // nu.xom.Document input = builder.build(tmpXmlFile);
    // nu.xom.Document input = new ProfileSerialization4ExportImpl()
    // .serializeIGDocumentToDoc(igdoc);
    // nu.xom.Document stylesheet = builder.build(this.getClass()
    // .getResourceAsStream("/rendering/igdocument.xsl"));
    // XSLTransform transform = new XSLTransform(stylesheet);
    // transform.setParameter("inlineConstraints", inlineConstraints);
    // Nodes output = transform.transform(input);
    // nu.xom.Document result = XSLTransform.toDocument(output);
    //
    // Tidy tidy = new Tidy();
    // tidy.setWraplen(Integer.MAX_VALUE);
    // tidy.setXHTML(true);
    // tidy.setShowWarnings(false); //to hide errors
    // tidy.setQuiet(true); //to hide warning
    // ByteArrayInputStream inputStream = new
    // ByteArrayInputStream(result.toXML().getBytes("UTF-8"));
    // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // tidy.parseDOM(inputStream, outputStream);
    // FileUtils.writeStringToFile(tmpHtmlFile,
    // outputStream.toString("UTF-8"));
    //
    // return FileUtils.openInputStream(tmpHtmlFile);
    // } catch (IOException | ParsingException
    // | XSLException e) {
    // return new NullInputStream(1L);
    // }
  }

  public InputStream exportAsHtmlFromXsl4Pdf(IGDocument igdoc, String inlineConstraints)
      throws SerializationException {
    // Note: inlineConstraint can be true or false

    try {
      File tmpHtmlFile = File.createTempFile("IGDocTemp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("IGDocTemp" + UUID.randomUUID().toString(), ".xml");
      // File tmpXmlFile = new File("IGDocTemp +
      // UUID.randomUUID().toString().xml");
      String stringIgDoc = igDocumentSerializationService.serializeIGDocumentToXML(igdoc);
      FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc, Charset.forName("UTF-8"));

      TransformerFactory factory = TransformerFactory.newInstance();
      Source xslt =
          new StreamSource(this.getClass().getResourceAsStream("/rendering/igdoc2htmlNoTOC.xsl"));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factory.newTransformer(xslt);
      transformer.setParameter("inlineConstraints", inlineConstraints);
      transformer.setParameter("includeTOC", "true");

      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

      // TODO Add here call to wkhtml2pdf

      return FileUtils.openInputStream(tmpHtmlFile);

    } catch (TransformerException | IOException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }

  }

  public InputStream exportAsHtmlFromXslDatatypes(IGDocument igdoc, String inlineConstraints) {
    // Note: inlineConstraint can be true or false

    try {
      File tmpHtmlFile = File.createTempFile("DTTemp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("DTTemp" + UUID.randomUUID().toString(), ".xml");
      String stringIgDoc = igDocumentSerializationService.serializeDatatypesToXML(igdoc);
      FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc, Charset.forName("UTF-8"));

      TransformerFactory factoryTf = TransformerFactory.newInstance();
      Source xslt =
          new StreamSource(this.getClass().getResourceAsStream("/rendering/igdoc2htmlWithTOC.xsl"));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factoryTf.newTransformer(xslt);
      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));
      return FileUtils.openInputStream(tmpHtmlFile);

    } catch (TransformerException | IOException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  public void registerChange(Map<String, List<String>> dict, String key, String value) {
    if (dict.containsKey(key)) {
      dict.get(key).add(value);
    } else {
      dict.put(key, new ArrayList<String>());
      dict.get(key).add(value);
    }
  }

  public InputStream exportAsDocxFromHtml(IGDocument igdoc, String inlineConstraints)
      throws SerializationException {
    // Note: inlineConstraint can be true or false

    try {
      File tmpHtmlFile = File.createTempFile("IGDocTemp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("IGDocTemp" + UUID.randomUUID().toString(), ".xml");
      String stringIgDoc = igDocumentSerializationService.serializeIGDocumentToXML(igdoc);
      FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc, Charset.forName("UTF-8"));
      File testxml = new File("tst.xml");
      FileUtils.writeStringToFile(testxml, stringIgDoc, Charset.forName("UTF-8"));

      TransformerFactory factoryTf = TransformerFactory.newInstance();
      Source xslt =
          new StreamSource(this.getClass().getResourceAsStream("/rendering/igdoc2htmlNoTOC.xsl"));
      // new
      // StreamSource(this.getClass().getResourceAsStream("/rendering/igdoc2htmlWithTOC.xsl"));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factoryTf.newTransformer(xslt);
      // transformer.setParameter("inlineConstraints", inlineConstraints);
      // transformer.setParameter("includeTOC", "false");
      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

      String html = FileUtils.readFileToString(tmpHtmlFile);
      File testhml = new File("tst.html");
      transformer.setOutputProperty(OutputKeys.METHOD, "html");
      FileUtils.writeStringToFile(testhml, html, Charset.forName("UTF-8"));

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
          .load(this.getClass().getResourceAsStream("/rendering/lri_template.dotx"));

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
      ac.setId(altChunkRel.getId());
      wordMLPackage.getMainDocumentPart().addObject(ac);

      // .. content type
      // wordMLPackage.getContentTypeManager().addDefaultContentType("html",
      // "text/html");

      // Tidy tidy = new Tidy();
      // tidy.setWraplen(Integer.MAX_VALUE);
      // tidy.setXHTML(true);
      // tidy.setShowWarnings(false); //to hide errors
      // tidy.setQuiet(true); //to hide warning
      // tidy.setMakeClean(true);
      // tidy.setErrfile("err.ig.txt");
      // InputStream inputStream = new
      // ByteArrayInputStream(html.getBytes());
      // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      // tidy.parseDOM(inputStream, outputStream);
      // File cleanTmpHtmlFile = File.createTempFile("IGDocTemp",
      // ".html");
      // FileUtils.writeByteArrayToFile(cleanTmpHtmlFile,
      // outputStream.toByteArray());
      //
      // String htmlSouped =
      // inlineCss(FileUtils.readFileToString(cleanTmpHtmlFile));
      //
      // XHTMLImporterImpl XHTMLImporter = new
      // XHTMLImporterImpl(wordMLPackage);
      // wordMLPackage.getMainDocumentPart().getContent().addAll(
      // XHTMLImporter.convert(htmlSouped, null) );
      //// XHTMLImporter.convert(cleanTmpHtmlFile, null) );

      // addConformanceInformationForDocx4j(igdoc, wordMLPackage,
      // factory);

      loadTemplateForDocx4j(wordMLPackage); // Repeats the lines above but
                                            // necessary; don't delete

      File tmpFile;
      tmpFile = File.createTempFile("IgDocument" + UUID.randomUUID().toString(), ".docx");
      wordMLPackage.save(tmpFile);

      return FileUtils.openInputStream(tmpFile);

    } catch (TransformerException | IOException | Docx4JException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  public InputStream exportAsDocxIG(IGDocument igdoc) {
    // Note: inlineConstraint can be true or false

    try {
      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
          .load(this.getClass().getResourceAsStream("/rendering/lri_template.dotx"));

      ObjectFactory factory = Context.getWmlObjectFactory();

      createCoverPageForDocx4j(igdoc, wordMLPackage, factory);

      addPageBreak(wordMLPackage, factory);

      createTableOfContentForDocx4j(wordMLPackage, factory);

      // FieldUpdater updater = new FieldUpdater(wordMLPackage);
      // try {
      // updater.update(true);
      // } catch (Docx4JException e1) {
      // e1.printStackTrace();
      // }

      addPageBreak(wordMLPackage, factory);

      // Add sections
      try {
        this.addXhtmlChunk(this.exportAsHtmlSections(igdoc), wordMLPackage);
      } catch (Exception e1) {
        logger.warn("Error adding sections");
        e1.printStackTrace();
        addErrorMessageInDocx("Could not add sections. ",
            e1.getLocalizedMessage() + IOUtils.toString(this.exportAsHtmlSections(igdoc)),
            wordMLPackage, factory);
      }

      addPageBreak(wordMLPackage, factory);

      // addContents4Docx(
      // (Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section>)
      // igdoc.getChildSections(),
      // "",
      // 1, wordMLPackage);

      Profile profile = igdoc.getProfile();

      if (profile.getSectionTitle() != null) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1",
            profile.getSectionTitle());
      } else {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", "");
      }

      // Including information regarding messages
      if (profile.getMessages().getSectionTitle() != null) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
            profile.getMessages().getSectionTitle());
      } else {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
      }

      // Add messages infrastructure
      List<Message> msgList = new ArrayList<>(profile.getMessages().getChildren());
      Collections.sort(msgList);

      for (Message m : msgList) {
        this.addHtmlChunk(m.getId(), this.exportAsHtmlMessage(m), wordMLPackage, factory);
      }

      // => sgts
      if (profile.getSegmentLibrary().getSectionTitle() != null) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
            profile.getSegmentLibrary().getSectionTitle());
      } else {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
      }
      List<SegmentLink> sgtList =
          new ArrayList<SegmentLink>(profile.getSegmentLibrary().getChildren());
      Collections.sort(sgtList);
      for (SegmentLink link : sgtList) {
        this.addHtmlChunk(link.getId(), this.exportAsHtmlSegment(link), wordMLPackage, factory);
      }

      // => dts
      if (profile.getDatatypeLibrary().getSectionTitle() != null) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
            profile.getDatatypeLibrary().getSectionTitle());
      } else {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
      }
      List<DatatypeLink> dtList =
          new ArrayList<DatatypeLink>(profile.getDatatypeLibrary().getChildren());
      Collections.sort(dtList);
      for (DatatypeLink link : dtList) {
        this.addHtmlChunk(link.getId(), this.exportAsHtmlDatatype(link), wordMLPackage, factory);
      }

      // => tbls
      if (profile.getTableLibrary().getSectionTitle() != null) {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
            profile.getTableLibrary().getSectionTitle());
      } else {
        wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
      }
      List<TableLink> tables = new ArrayList<TableLink>(profile.getTableLibrary().getTables());
      Collections.sort(tables);
      for (TableLink link : tables) {
        this.addHtmlChunk(link.getId(), this.exportAsHtmlTable(link), wordMLPackage, factory);
      }

      // addConformanceInformationForDocx4j(igdoc, wordMLPackage,
      // factory);

      loadTemplateForDocx4j(wordMLPackage); // Repeats the lines above but
                                            // necessary; don't delete

      File tmpFile;
      tmpFile = File.createTempFile("IgDocument" + UUID.randomUUID().toString(), ".docx");
      wordMLPackage.save(tmpFile);
      wordMLPackage = WordprocessingMLPackage.load(tmpFile);
      FieldUpdater updater = new FieldUpdater(wordMLPackage);
      updater = new FieldUpdater(wordMLPackage);
      try {
        updater.update(true);
      } catch (Docx4JException e1) {
        e1.printStackTrace();
      }
      wordMLPackage.save(tmpFile);

      return FileUtils.openInputStream(tmpFile);

    } catch (IOException | Docx4JException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  private void addXhtmlChunk(InputStream inputStream, WordprocessingMLPackage wordMLPackage)
      throws Docx4JException {
    Tidy tidy = new Tidy();
    tidy.setWraplen(Integer.MAX_VALUE);
    tidy.setXHTML(true);
    tidy.setShowWarnings(false); // to hide errors
    tidy.setQuiet(true); // to hide warning
    tidy.setMakeClean(true);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    tidy.parseDOM(inputStream, outputStream);

    XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
    ImportXHTMLProperties.getProperty("docx4j-ImportXHTML.Element.Heading.MapToStyle", true);

    wordMLPackage.getMainDocumentPart().getContent()
        .addAll(XHTMLImporter.convert(IOUtils.toInputStream(outputStream.toString()), null));
  }

  private void addHtmlChunk(String id, InputStream inputStream,
      WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Tidy tidy = new Tidy();
    tidy.setWraplen(Integer.MAX_VALUE);
    tidy.setXHTML(true);
    tidy.setShowWarnings(false); // to hide errors
    tidy.setQuiet(true); // to hide warning
    tidy.setMakeClean(true);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    tidy.parseDOM(inputStream, outputStream);
    try {
      wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Html,
          outputStream.toByteArray());
    } catch (Docx4JException e) {
      e.printStackTrace();
      try {
        addErrorMessageInDocx("Docx4jException with object id" + id,
            e.getLocalizedMessage() + IOUtils.toString(inputStream), wordMLPackage, factory);
      } catch (IOException e1) {
        e1.printStackTrace();
        addErrorMessageInDocx("OIException with object id" + id, e.getLocalizedMessage(),
            wordMLPackage, factory);
      }
    }
  }

  public String inlineCss(String html) {
    final String style = "style";
    org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html);
    Elements els = doc.select(style);// to get all the style elements
    for (org.jsoup.nodes.Element e : els) {
      String styleRules = e.getAllElements().get(0).data().replaceAll("\n", "").trim();
      String delims = "{}";
      StringTokenizer st = new StringTokenizer(styleRules, delims);
      while (st.countTokens() > 1) {
        String selector = st.nextToken(), properties = st.nextToken();
        if (!selector.contains(":")) { // skip a:hover rules, etc.
          Elements selectedElements = doc.select(selector);
          for (org.jsoup.nodes.Element selElem : selectedElements) {
            String oldProperties = selElem.attr(style);
            selElem.attr(style, oldProperties.length() > 0
                ? concatenateProperties(oldProperties, properties) : properties);
          }
        }
      }
      e.remove();
    }
    return doc.toString();
  }

  private String concatenateProperties(String oldProp, String newProp) {
    oldProp = oldProp.trim();
    if (!oldProp.endsWith(";"))
      oldProp += ";";
    return oldProp + newProp.replaceAll("\\s{2,}", " ");
  }

  public InputStream exportAsDocxFromHtmlDatatypes(IGDocument igdoc, String inlineConstraints) {
    // Note: inlineConstraint can be true or false

    try {
      File tmpHtmlFile = File.createTempFile("DTTemp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("DTTemp" + UUID.randomUUID().toString(), ".xml");
      String stringIgDoc = igDocumentSerializationService.serializeDatatypesToXML(igdoc);
      FileUtils.writeStringToFile(tmpXmlFile, stringIgDoc, Charset.forName("UTF-8"));

      TransformerFactory factoryTf = TransformerFactory.newInstance();
      Source xslt = new StreamSource(
          this.getClass().getResourceAsStream("/rendering/igdoc2htmlWithoutTOC.xsl"));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factoryTf.newTransformer(xslt);
      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

      String html = FileUtils.readFileToString(tmpHtmlFile);

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
          .load(this.getClass().getResourceAsStream("/rendering/lri_template.dotx"));

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
      ac.setId(altChunkRel.getId());
      wordMLPackage.getMainDocumentPart().addObject(ac);

      // .. content type
      wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");

      addConformanceInformationForDocx4j(igdoc, wordMLPackage, factory);

      loadTemplateForDocx4j(wordMLPackage); // Repeats the lines above but
                                            // necessary; don't delete

      File tmpFile;
      tmpFile = File.createTempFile("DTDocument" + UUID.randomUUID().toString(), ".docx");
      wordMLPackage.save(tmpFile);

      return FileUtils.openInputStream(tmpFile);

    } catch (TransformerException | IOException | Docx4JException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  @SuppressWarnings("resource")
  public static void mergePOI(InputStream src1, InputStream src2, OutputStream dest)
      throws Exception {
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
    String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
    String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
    String sufix = srcString.substring(srcString.lastIndexOf("<"));
    String addPart =
        appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
    CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);
    src.set(makeBody);
  }

  private static long chunk = 0;
  private static final String CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

  public void mergeDocx4J(InputStream s1, InputStream s2, OutputStream os) throws Exception {
    WordprocessingMLPackage target = WordprocessingMLPackage.load(s1);
    insertDocx(target.getMainDocumentPart(), IOUtils.toByteArray(s2));
    org.docx4j.openpackaging.io3.Save saver = new org.docx4j.openpackaging.io3.Save(target);
    FileOutputStream out = new FileOutputStream("mergeddocx4.docx");
    saver.save(out);
  }

  private static void insertDocx(MainDocumentPart main, byte[] bytes) throws Exception {
    AlternativeFormatInputPart afiPart =
        new AlternativeFormatInputPart(new PartName("/part" + (chunk++) + ".docx"));
    afiPart.setContentType(new ContentType(CONTENT_TYPE));
    afiPart.setBinaryData(bytes);
    Relationship altChunkRel = main.addTargetPart(afiPart);

    CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
    chunk.setId(altChunkRel.getId());

    main.addObject(chunk);
  }

  private void createCoverPageForDocx4j(IGDocument igdoc, WordprocessingMLPackage wordMLPackage,
      ObjectFactory factory) {
    Profile p = igdoc.getProfile();

    try {
      byte[] imageInByte = Base64.getDecoder().decode(
          "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");

      addImageToPackage(wordMLPackage, imageInByte);
    } catch (Exception e) {
      logger.warn("Unable to add cover page image");
      e.printStackTrace();
      addErrorMessageInDocx("Unable to add image", e.getLocalizedMessage(), wordMLPackage, factory);
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title",
        igdoc.getMetaData().getTitle());
    addLineBreak(wordMLPackage, factory);
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle",
        "Subtitle " + igdoc.getMetaData().getSubTitle());
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        DateUtils.formatCoverPageDate(igdoc.getDateUpdated()));
    addLineBreak(wordMLPackage, factory);
    addLineBreak(wordMLPackage, factory);
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        "HL7 Version " + p.getMetaData().getHl7Version());
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        "Document Version " + igdoc.getMetaData().getVersion());
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        p.getMetaData().getOrgName());

    addPageBreak(wordMLPackage, factory);
  }

  private void createTableOfContentForDocx4j(WordprocessingMLPackage wordMLPackage,
      ObjectFactory factory) {
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
    r.getContent().add(factory.createRInstrText(txt));
    paragraphForTOC.getContent().add(r1);

    FldChar fldcharend = factory.createFldChar();
    fldcharend.setFldCharType(STFldCharType.END);
    R r2 = factory.createR();
    r2.getContent().add(getWrappedFldChar(fldcharend));
    paragraphForTOC.getContent().add(r2);

    wordMLPackage.getMainDocumentPart().getContent().add(paragraphForTOC);
    addPageBreak(wordMLPackage, factory);
  }

  private void loadTemplateForDocx4j(WordprocessingMLPackage wordMLPackage) {
    try {
      // Replace dotx content type with docx
      ContentTypeManager ctm = wordMLPackage.getContentTypeManager();

      // Get <Override PartName="/word/document.xml"
      // ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml"/>
      CTOverride override;
      override = ctm.getOverrideContentType().get(new URI("/word/document.xml"));
      override.setContentType(
          org.docx4j.openpackaging.contenttype.ContentTypes.WORDPROCESSINGML_DOCUMENT);

      // Create settings part, and init content
      DocumentSettingsPart dsp = new DocumentSettingsPart();
      CTSettings settings = Context.getWmlObjectFactory().createCTSettings();
      dsp.setJaxbElement(settings);
      wordMLPackage.getMainDocumentPart().addTargetPart(dsp);

      // Create external rel
      RelationshipsPart rp = RelationshipsPart.createRelationshipsPartForPart(dsp);
      org.docx4j.relationships.Relationship rel =
          new org.docx4j.relationships.ObjectFactory().createRelationship();
      rel.setType(Namespaces.ATTACHED_TEMPLATE);
      URL templateData = getClass().getResource("/rendering/lri_template.dotx");
      rel.setTarget(templateData.getPath());
      rel.setTargetMode("External");
      rp.addRelationship(rel); // addRelationship sets the rel's @Id

      settings.setAttachedTemplate((CTRel) XmlUtils.unmarshalString(
          "<w:attachedTemplate xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\""
              + rel.getId() + "\"/>",
          Context.jc, CTRel.class));

    } catch (URISyntaxException | JAXBException | Docx4JException e1) {
      e1.printStackTrace();
    }
  }

  private void addConformanceInformationForDocx4j(IGDocument igdoc,
      WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Profile p = igdoc.getProfile();
    List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
    Collections.sort(messagesList);
    List<DatatypeLink> datatypeList =
        new ArrayList<DatatypeLink>(p.getDatatypeLibrary().getChildren());
    List<SegmentLink> segmentsList =
        new ArrayList<SegmentLink>(p.getSegmentLibrary().getChildren());

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
        "Conformance information");
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
        "Conditional predicates");
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreMessage(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreGroup(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
    for (SegmentLink sl : segmentsList) {
      Segment s = segmentService.findById(sl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreSegment(rows, s);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(sl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
    for (DatatypeLink dl : datatypeList) {
      Datatype dt = datatypeService.findById(dl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreDatatype(rows, dt);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(dl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
        "Conformance statements");

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsMessage(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsGroup(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
    for (SegmentLink sl : segmentsList) {
      Segment s = segmentService.findById(sl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsSegment(rows, s);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(sl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
    for (DatatypeLink dl : datatypeList) {
      Datatype dt = datatypeService.findById(dl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsDatatype(rows, dt);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(dl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

  }

  @SuppressWarnings("unused")
  private InputStream exportAsDocxWithDocx4J(IGDocument igdoc) {
    Profile p = igdoc.getProfile();
    WordprocessingMLPackage wordMLPackage;
    try {
      wordMLPackage = WordprocessingMLPackage
          .load(this.getClass().getResourceAsStream("/rendering/lri_template.dotx"));
    } catch (Docx4JException e1) {
      e1.printStackTrace();
      return new NullInputStream(1L);
    }

    // Replace dotx content type with docx
    ContentTypeManager ctm = wordMLPackage.getContentTypeManager();

    // Get <Override PartName="/word/document.xml"
    // ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml"/>
    CTOverride override;
    try {
      override = ctm.getOverrideContentType().get(new URI("/word/document.xml"));

      override.setContentType(
          org.docx4j.openpackaging.contenttype.ContentTypes.WORDPROCESSINGML_DOCUMENT);

      // Create settings part, and init content
      DocumentSettingsPart dsp = new DocumentSettingsPart();
      CTSettings settings = Context.getWmlObjectFactory().createCTSettings();
      dsp.setJaxbElement(settings);
      wordMLPackage.getMainDocumentPart().addTargetPart(dsp);

      // Create external rel
      RelationshipsPart rp = RelationshipsPart.createRelationshipsPartForPart(dsp);
      org.docx4j.relationships.Relationship rel =
          new org.docx4j.relationships.ObjectFactory().createRelationship();
      rel.setType(Namespaces.ATTACHED_TEMPLATE);
      String templatePath = "/rendering/lri_template.dotx";
      rel.setTarget(templatePath);
      rel.setTargetMode("External");
      rp.addRelationship(rel); // addRelationship sets the rel's @Id

      settings.setAttachedTemplate((CTRel) XmlUtils.unmarshalString(
          "<w:attachedTemplate xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" r:id=\""
              + rel.getId() + "\"/>",
          Context.jc, CTRel.class));

    } catch (URISyntaxException | InvalidFormatException | JAXBException e1) {
      e1.printStackTrace();
    } // note this assumption

    ObjectFactory factory = Context.getWmlObjectFactory();

    BufferedImage image = null;
    try {
      URL url = new URL("http://hit-2015.nist.gov/docs/hl7Logo.png");
      image = ImageIO.read(url);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      baos.flush();
      byte[] imageInByte = baos.toByteArray();
      baos.close();

      addImageToPackage(wordMLPackage, imageInByte);
    } catch (Exception e) {
      logger.warn("Unable to add image");
      e.printStackTrace();
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title",
        igdoc.getMetaData().getTitle());
    addLineBreak(wordMLPackage, factory);
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle",
        "Subtitle " + igdoc.getMetaData().getSubTitle());
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        DateUtils.format(igdoc.getDateUpdated()));
    addLineBreak(wordMLPackage, factory);
    addLineBreak(wordMLPackage, factory);
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        "HL7 Version " + p.getMetaData().getHl7Version());
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        "Document Version " + igdoc.getMetaData().getVersion());
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
        p.getMetaData().getOrgName());

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
    r.getContent().add(factory.createRInstrText(txt));
    paragraphForTOC.getContent().add(r1);

    FldChar fldcharend = factory.createFldChar();
    fldcharend.setFldCharType(STFldCharType.END);
    R r2 = factory.createR();
    r2.getContent().add(getWrappedFldChar(fldcharend));
    paragraphForTOC.getContent().add(r2);

    // To uncomment
    wordMLPackage.getMainDocumentPart().getContent().add(paragraphForTOC);
    addPageBreak(wordMLPackage, factory);

    traverseIGDocument4Docx(igdoc, wordMLPackage);

    addPageBreak(wordMLPackage, factory);
    if (p.getSectionTitle() != null) {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", p.getSectionTitle());
    } else {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading1", "");
    }

    // Including information regarding messages
    if (p.getMessages().getSectionTitle() != null) {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
          p.getMessages().getSectionTitle());
    } else {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
    }

    p.getMessages().setPositionsOrder();
    List<Message> messagesList = new ArrayList<Message>(p.getMessages().getChildren());
    Collections.sort(messagesList);
    for (Message m : messagesList) {
      // String messageInfo = m.getMessageType() + "^"
      // + m.getEvent() + "^" + m.getStructID() + " - " +
      // m.getDescription();
      // wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
      // messageInfo);
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", m.getName());

      addRichTextToDocx(wordMLPackage, m.getComment());

      List<String> header = Arrays.asList("Segment", "Flavor", "Element Name", "Usage", "Card.",
          "Description/Comments");
      List<Integer> widths = Arrays.asList(1200, 1000, 2000, 800, 900, 3000);

      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addMessage(rows, m, p);
      wordMLPackage.getMainDocumentPart().addObject(
          IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));

      addRichTextToDocx(wordMLPackage, m.getUsageNote());
      addPageBreak(wordMLPackage, factory);
    }

    // Including information regarding segments
    List<SegmentLink> segmentsList =
        new ArrayList<SegmentLink>(p.getSegmentLibrary().getChildren());
    if (p.getSegmentLibrary().getSectionTitle() != null) {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
          p.getSegmentLibrary().getSectionTitle());
    } else {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
    }

    for (SegmentLink sl : segmentsList) {
      Segment s = segmentService.findById(sl.getId());
      String segmentInfo = sl.getLabel() + " - " + s.getDescription();
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", segmentInfo);

      // Add segment details

      // wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
      // "pre-definition");
      // addRichTextToDocx(wordMLPackage, s.getText1());

      List<String> header = Arrays.asList("Seq", "Element Name", "DT", "Usage", "Card.", "Length",
          "Value Set", "Description/Comments");
      List<Integer> widths = Arrays.asList(600, 2000, 900, 800, 800, 1000, 1000, 3000);
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      this.addSegment(rows, s, Boolean.TRUE, p.getDatatypeLibrary(), p.getTableLibrary());
      wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl
          .createTableDocxWithConstraints(header, widths, rows, wordMLPackage, factory));
      // wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
      // "post-definition");
      //
      // addRichTextToDocx(wordMLPackage, s.getText2());

      // Add field texts
      List<Field> fieldsList = s.getFields();
      Collections.sort(fieldsList);
      for (Field f : fieldsList) {
        if (f.getText() != null && f.getText().length() != 0) {
          wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
              s.getName() + "-" + f.getItemNo().replaceFirst("^0+(?!$)", "") + " " + f.getName()
                  + " (" + p.getDatatypeLibrary().findOne(f.getDatatype()).getLabel() + ")");
          wordMLPackage.getMainDocumentPart().addParagraphOfText(f.getText());
        }
      }
      addPageBreak(wordMLPackage, factory);
    }

    // Including information regarding data types
    List<DatatypeLink> datatypeList =
        new ArrayList<DatatypeLink>(p.getDatatypeLibrary().getChildren());
    if (p.getDatatypeLibrary().getSectionTitle() != null) {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
          p.getDatatypeLibrary().getSectionTitle());
    } else {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
    }

    for (DatatypeLink dl : datatypeList) {
      Datatype d = datatypeService.findById(dl.getId());
      String dtInfo = dl.getLabel() + " - " + d.getDescription();
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", dtInfo);

      wordMLPackage.getMainDocumentPart().addParagraphOfText(d.getComment());

      List<String> header = Arrays.asList("Seq", "Element Name", "Conf\nlength", "DT", "Usage",
          "Length", "Value\nSet", "Comment");
      List<Integer> widths = Arrays.asList(600, 2000, 900, 750, 900, 900, 1000, 3000);
      List<List<String>> rows = new ArrayList<List<String>>();
      this.addDatatype(rows, d, p.getDatatypeLibrary(), p.getTableLibrary());
      wordMLPackage.getMainDocumentPart().addObject(IGDocumentExportImpl
          .createTableDocxWithConstraints(header, widths, rows, wordMLPackage, factory));
    }
    addPageBreak(wordMLPackage, factory);

    // Including information regarding value sets
    List<TableLink> tables = new ArrayList<TableLink>(p.getTableLibrary().getChildren());
    Collections.sort(tables);
    if (p.getTableLibrary().getSectionTitle() != null) {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
          p.getTableLibrary().getSectionTitle());
    } else {
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2", "");
    }

    for (TableLink tl : tables) {
      Table t = tableService.findById(tl.getId());
      String valuesetInfo = tl.getBindingIdentifier() + " - " + t.getDescription();
      wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3", valuesetInfo);

      StringBuilder sb;
      sb = new StringBuilder();
      sb.append("\nOid: ");
      sb.append(t.getOid() == null || t.getOid().isEmpty() ? "UNSPECIFIED" : t.getOid());
      wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());
      // sb = new StringBuilder();
      // sb.append("\nStability: ");
      // sb.append(t.getStability()==null ? "Static":t.getStability());
      // wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());
      // sb = new StringBuilder();
      // sb.append("\nExtensibility: ");
      // sb.append(t.getExtensibility() == null ?
      // "Closed":t.getExtensibility());
      // wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());
      // sb = new StringBuilder();
      // sb.append("\nContent definition: ");
      // sb.append(t.getContentDefinition() == null ?
      // "Extensional":t.getContentDefinition());
      // wordMLPackage.getMainDocumentPart().addParagraphOfText(sb.toString());

      List<String> header = Arrays.asList("Value", "Code system", "Usage", "Description");
      List<Integer> widths = Arrays.asList(1500, 1500, 1000, 5000);
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      this.addValueSet(rows, t);
      wordMLPackage.getMainDocumentPart().addObject(
          IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));

    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading2",
        "Conformance information");
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
        "Conditional predicates");
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreMessage(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreGroup(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
    for (SegmentLink sl : segmentsList) {
      Segment s = segmentService.findById(sl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreSegment(rows, s);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(sl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
    for (DatatypeLink dl : datatypeList) {
      Datatype dt = datatypeService.findById(dl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addPreDatatype(rows, dt);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(dl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Usage", "Description");
        List<Integer> widths = Arrays.asList(1500, 1500, 6000);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }
    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading3",
        "Conformance statements");

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Message Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsMessage(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Group Level");
    for (Message m : messagesList) {
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsGroup(rows, m);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(m.getName(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Segment Level");
    for (SegmentLink sl : segmentsList) {
      Segment s = segmentService.findById(sl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsSegment(rows, s);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(s.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
      }
    }

    wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Heading4", "Datatype Level");
    for (DatatypeLink dl : datatypeList) {
      Datatype dt = datatypeService.findById(dl.getId());
      ArrayList<List<String>> rows = new ArrayList<List<String>>();
      addCsDatatype(rows, dt);
      if (!rows.isEmpty()) {
        addLineBreak(wordMLPackage, factory);
        addParagraph(dl.getLabel(), wordMLPackage, factory);
        addLineBreak(wordMLPackage, factory);
        List<String> header = Arrays.asList("Location", "Description");
        List<Integer> widths = Arrays.asList(1500, 7500);
        wordMLPackage.getMainDocumentPart().addObject(
            IGDocumentExportImpl.createTableDocx(header, widths, rows, wordMLPackage, factory));
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
      tmpFile = File.createTempFile("Profile" + UUID.randomUUID().toString(), ".docx");
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

  private static Tbl createTableDocx(List<String> header, List<Integer> widths,
      List<List<String>> rows, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Tbl table = factory.createTbl();
    Tr tableRow;

    tableRow = factory.createTr();
    Integer width = null;

    if (widths != null && header != null && header.size() == widths.size()) {
      for (String cell : header) {
        width = widths.get(header.indexOf(cell));
        addTableCell(tableRow, cell, width, headerBackground, wordMLPackage, factory);
      }
      table.getContent().add(tableRow);
      for (List<String> row : rows) {
        tableRow = factory.createTr();
        for (String cell : row) {
          width = widths.get(row.indexOf(cell));
          addTableCell(tableRow, cell, width, null, wordMLPackage, factory);
        }
        table.getContent().add(tableRow);
      }
      addBorders(table);
    }
    return table;
  }

  private static Tbl createTableDocxWithConstraints(List<String> header, List<Integer> widths,
      List<List<String>> rows, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Tbl table = factory.createTbl();
    Tr tableRow;

    tableRow = factory.createTr();
    Integer width = null;
    if (widths != null && header != null && header.size() == widths.size()) {
      for (String cell : header) {
        width = widths.get(header.indexOf(cell));
        addTableCell(tableRow, cell, width, headerBackground, wordMLPackage, factory);
      }
      table.getContent().add(tableRow);

      if (!rows.isEmpty()) {
        int nbOfColumns = rows.get(0).size();
        for (List<String> row : rows) {
          tableRow = factory.createTr();
          if (row.size() == nbOfColumns) {
            // case "normal" row
            for (String cell : row) {
              addTableCell(tableRow, cell, null, null, wordMLPackage, factory);
            }
          } else {
            // case "constraints" row
            tableRow.getContent()
                .add(createTableCell(row.get(0), null, null, wordMLPackage, factory));
            tableRow.getContent().add(
                createTableCell(row.get(1), null, constraintBackground, wordMLPackage, factory));
            tableRow.getContent().add(createTableCellGspan(row.get(2), nbOfColumns - 2,
                constraintBackground, wordMLPackage, factory));
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
    // STTblLayoutType stTblLayoutType = STTblLayoutType.AUTOFIT;
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

  private static void addTableCell(Tr tableRow, String content, Integer width,
      String backgroundColor, WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Tc tableCell = factory.createTc();
    TcPr tcpr = factory.createTcPr();
    tableCell.setTcPr(tcpr);
    if (width != null) {
      setCellWidth(tcpr, width, factory);
    }
    if (backgroundColor != null) {
      setCellShading(tcpr, backgroundColor, factory);
    }
    setCellNoWrap(tcpr, true);
    tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(content));
    tableRow.getContent().add(tableCell);
  }

  private static Tc createTableCell(String content, Integer width, String backgroundColor,
      WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Tc tableCell = factory.createTc();
    TcPr tcpr = factory.createTcPr();
    tableCell.setTcPr(tcpr);
    tableCell.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(content));
    if (width != null) {
      setCellWidth(tcpr, width, factory);
    }
    if (backgroundColor != null) {
      setCellShading(tcpr, backgroundColor, factory);
    }
    return tableCell;
  }

  private static Tc createTableCellGspan(String content, int gridspan, String backgroundColor,
      WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Tc tc = factory.createTc();
    TcPr tcpr = factory.createTcPr();
    tc.setTcPr(tcpr);
    org.docx4j.wml.CTVerticalJc valign = factory.createCTVerticalJc();
    valign.setVal(STVerticalJc.TOP);
    tcpr.setVAlign(valign);
    if (backgroundColor != null) {
      setCellShading(tcpr, backgroundColor, factory);
    }
    GridSpan gspan = factory.createTcPrInnerGridSpan();
    gspan.setVal(new BigInteger("" + gridspan));
    tcpr.setGridSpan(gspan);
    tc.getContent().add(wordMLPackage.getMainDocumentPart().createParagraphOfText(content));
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

  private static void setCellShading(TcPr tcpr, String color, ObjectFactory factory) {
    CTShd shading = factory.createCTShd();
    tcpr.setShd(shading);
    // shading.setColor("Red");
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

  private void addParagraph(String content, WordprocessingMLPackage wordMLPackage,
      ObjectFactory factory) {
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
    // addItalicStyle(runProperties);
    // addUnderlineStyle(runProperties);
    // setFontFamily(runProperties, "Arial");
    // setFontSize(runProperties, "14");
    run.setRPr(runProperties);
    wordMLPackage.getMainDocumentPart().addObject(paragraph);

  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static JAXBElement getWrappedFldChar(FldChar fldchar) {
    return new JAXBElement(new QName(Namespaces.NS_WORD12, "fldChar"), FldChar.class, fldchar);
  }

  private String wrapRichText(String htmlString) {
    // Adds html tags so that string can be decoded in docx export
    Tidy tidy = new Tidy();
    tidy.setWraplen(Integer.MAX_VALUE);
    tidy.setXHTML(true);
    tidy.setShowWarnings(false); // to hide errors
    tidy.setQuiet(true); // to hide warning
    InputStream inputStream = new ByteArrayInputStream(htmlString.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    tidy.parseDOM(inputStream, outputStream);

    StringBuilder rst = new StringBuilder("<html><head></head><body></body>");
    return rst.insert(25, outputStream.toString()).toString();
  }

  private void addRichTextToDocx(WordprocessingMLPackage wordMLPackage, String htmlString) {
    // this.addHtmlChunk(IOUtils.toInputStream(htmlString), wordMLPackage);

    // StringBuilder rst = new
    // StringBuilder("<html><head></head><body></body>");
    // this.addXhtmlChunk(IOUtils.toInputStream(rst.insert(25,
    // htmlString.toString()).toString()),
    // wordMLPackage);

    try {
      wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Xhtml,
          wrapRichText(htmlString).getBytes());
    } catch (Docx4JException e1) {
      e1.printStackTrace();
      wordMLPackage.getMainDocumentPart().addParagraphOfText("Error in rich text");
    }
  }

  public void traverseIGDocument4Docx(IGDocument d, WordprocessingMLPackage wordMLPackage) {

    addContents4Docx(d.getChildSections(), "", 1, wordMLPackage);

  }

  private void addContents4Docx(
      Set<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sect, String prefix,
      Integer depth, WordprocessingMLPackage wordMLPackage) {
    SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortedSections =
        sortSections(sect);
    for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section s : sortedSections) {
      if (s.getSectionTitle() != null) {
        wordMLPackage.getMainDocumentPart()
            .addStyledParagraphOfText("Heading" + String.valueOf(depth), s.getSectionTitle());
      } else {
        wordMLPackage.getMainDocumentPart()
            .addStyledParagraphOfText("Heading" + String.valueOf(depth), "");
      }
      if (s.getSectionContents() != null) {
        addRichTextToDocx(wordMLPackage, s.getSectionContents());
      }
      addContents4Docx(s.getChildSections(), String.valueOf(s.getSectionPosition() + 1), depth + 1,
          wordMLPackage);
    }

  }

  private SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortSections(
      Set<Section> s) {
    SortedSet<Section> sortedSet = new TreeSet<Section>();
    Iterator<Section> setIt = s.iterator();
    while (setIt.hasNext()) {
      sortedSet.add(setIt.next());
    }
    return sortedSet;
  }

  private static void addImageToPackage(WordprocessingMLPackage wordMLPackage, byte[] bytes)
      throws Exception {
    BinaryPartAbstractImage imagePart =
        BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

    int docPrId = 1;
    int cNvPrId = 2;
    Inline inline =
        imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);

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

  private InputStream exportAsXml(String xmlString) {
    // Note: inlineConstraint can be true or false
    try {
      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".xml");
      // File tmpXmlFile = new File("temp" +
      // UUID.randomUUID().toString()+".xml");
      FileUtils.writeStringToFile(tmpXmlFile, xmlString, Charset.forName("UTF-8"));

      return FileUtils.openInputStream(tmpXmlFile);

    } catch (IOException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  private InputStream exportAsHtmlFromXsl(String xmlString, String xslPath) {
    // Note: inlineConstraint can be true or false

    try {
      File tmpHtmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".xml");
      // File tmpXmlFile = new File("temp +
      // UUID.randomUUID().toString().xml");
      FileUtils.writeStringToFile(tmpXmlFile, xmlString, Charset.forName("UTF-8"));

      TransformerFactory factoryTf = TransformerFactory.newInstance();
      Source xslt = new StreamSource(this.getClass().getResourceAsStream(xslPath));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factoryTf.newTransformer(xslt);
      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

      Tidy tidy = new Tidy();
      tidy.setWraplen(Integer.MAX_VALUE);
      tidy.setXHTML(true);
      tidy.setShowWarnings(false); // to hide errors
      tidy.setQuiet(true); // to hide warning
      tidy.setMakeClean(true);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      tidy.parseDOM(FileUtils.openInputStream(tmpHtmlFile), outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());

    } catch (TransformerException | IOException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  public InputStream exportAsDocxFromXml(String xmlString, String xmlPath, Boolean includeToc) {
    // Note: inlineConstraint can be true or false
    try {
      File tmpHtmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".xml");
      FileUtils.writeStringToFile(tmpXmlFile, xmlString, Charset.forName("UTF-8"));

      TransformerFactory factoryTf = TransformerFactory.newInstance();
      Source xslt = new StreamSource(this.getClass().getResourceAsStream(xmlPath));
      Transformer transformer;

      // Apply XSL transformation on xml file to generate html
      transformer = factoryTf.newTransformer(xslt);
      transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));

      String html = FileUtils.readFileToString(tmpHtmlFile);

      WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
          .load(this.getClass().getResourceAsStream("/rendering/lri_template.dotx"));

      ObjectFactory factory = Context.getWmlObjectFactory();

      // createCoverPageForDocx4j(igdoc, wordMLPackage, factory); TODO
      // Implement cover page

      if (includeToc) {
        createTableOfContentForDocx4j(wordMLPackage, factory);
      }

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
      ac.setId(altChunkRel.getId());
      wordMLPackage.getMainDocumentPart().addObject(ac);

      // .. content type
      wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");

      // Tidy tidy = new Tidy();
      // tidy.setWraplen(Integer.MAX_VALUE);
      // tidy.setXHTML(true);
      // tidy.setShowWarnings(false); //to hide errors
      // tidy.setQuiet(true); //to hide warning
      // InputStream inputStream = new
      // ByteArrayInputStream(html.getBytes());
      // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      // tidy.parseDOM(inputStream, outputStream);
      // File cleanTmpHtmlFile = File.createTempFile("IGDocTemp",
      // ".html");
      // FileUtils.writeByteArrayToFile(cleanTmpHtmlFile,
      // outputStream.toByteArray());
      // XHTMLImporterImpl XHTMLImporter = new
      // XHTMLImporterImpl(wordMLPackage);
      // wordMLPackage.getMainDocumentPart().getContent().addAll(
      // XHTMLImporter.convert(cleanTmpHtmlFile, null) );

      // addConformanceInformationForDocx4j(igdoc, wordMLPackage,
      // factory);

      loadTemplateForDocx4j(wordMLPackage); // Repeats the lines above but
                                            // necessary; don't delete

      File tmpFile;
      tmpFile = File.createTempFile("IgDocument" + UUID.randomUUID().toString(), ".docx");
      wordMLPackage.save(tmpFile);

      return FileUtils.openInputStream(tmpFile);

    } catch (TransformerException | IOException | Docx4JException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  private String tablesToString(List<TableLink> tables) {
    String res = "";
    if (tables != null && !tables.isEmpty()) {
      for (TableLink link : tables) {
        Table tbl = tableService.findById(link.getId());
        if (tbl != null) {
          res = res.isEmpty() ? tbl.getBindingIdentifier() : res + " " + tbl.getBindingIdentifier();
        }
      }
    }
    return res;
  }

  private void addErrorMessageInDocx(String customMessage, String exceptionMessage,
      WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    P paragraph = factory.createP();
    R run = factory.createR();

    Text text = factory.createText();
    text.setValue(customMessage + ": " + exceptionMessage);
    run.getContent().add(text);

    paragraph.getContent().add(run);
    setHorizontalAlignment(paragraph, JcEnumeration.LEFT);

    RPr runProperties = factory.createRPr();
    addBoldStyle(runProperties);
    setFontColor(runProperties, "red");
    // addItalicStyle(runProperties);
    // addUnderlineStyle(runProperties);
    // setFontFamily(runProperties, "Arial");
    // setFontSize(runProperties, "14");
    run.setRPr(runProperties);
    wordMLPackage.getMainDocumentPart().addObject(paragraph);
  }
}

// class SectionComparator implements
// Comparator<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section>{
//
// @Override
// public int compare(Section o1, Section o2) {
// return ((Section) o1).getSectionPosition() - ((Section)
// o2).getSectionPosition();
// }
//
// }
