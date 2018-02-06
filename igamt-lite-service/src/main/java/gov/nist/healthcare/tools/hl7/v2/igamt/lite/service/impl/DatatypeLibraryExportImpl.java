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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.NullInputStream;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.contenttype.CTOverride;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.contenttype.ContentTypeManager;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTAltChunk;
import org.docx4j.wml.CTRel;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryExportService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import nu.xom.Attribute;

@Service
public class DatatypeLibraryExportImpl implements DatatypeLibraryExportService {
  Logger logger = LoggerFactory.getLogger(DatatypeLibraryExportImpl.class);

  @Autowired
  private DatatypeLibraryService datatypeLibraryService;

  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  @Override
  public InputStream exportAsXml(DatatypeLibrary dtl) throws IOException {

    nu.xom.Element e = new nu.xom.Element("DatatypeLibrary");
    for (DatatypeLink dl : dtl.getChildren()) {
      e.appendChild(serializeOneDatatype(dl));
    }
    nu.xom.Document doc = new nu.xom.Document(e);
    return exportAsXml(doc.toXML());
  }

  @Override
  public InputStream exportAsHtml(DatatypeLibrary dtl) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream exportAsPdf(DatatypeLibrary dtl) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream exportAsDocx(DatatypeLibrary dtl) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  private nu.xom.Element serializeOneDatatype(DatatypeLink dl) {
    Datatype d = datatypeService.findById(dl.getId());

    nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
    elmDatatype.addAttribute(new Attribute("ID", d.getId() + ""));
    elmDatatype.addAttribute(new Attribute("Name", d.getName()));
    elmDatatype.addAttribute(new Attribute("Label", d.getExt() == null || d.getExt().isEmpty()
        ? d.getName() : d.getLabel() + " - " + d.getDescription()));
    elmDatatype.addAttribute(new Attribute("Description", d.getDescription()));
    elmDatatype.addAttribute(new Attribute("Comment", d.getComment()));
    elmDatatype.addAttribute(
        new Attribute("Hl7Version", d.getHl7Version() == null ? "" : d.getHl7Version()));


    elmDatatype.addAttribute(new Attribute("id", d.getId()));
    nu.xom.Element elmText = new nu.xom.Element("Text");
    elmText.addAttribute(new Attribute("Type", "UsageNote"));
    elmText.appendChild(d.getUsageNote());
    elmDatatype.appendChild(elmText);

    if (d.getComponents() != null) {

      Map<Integer, Component> components = new HashMap<Integer, Component>();

      for (Component c : d.getComponents()) {
        components.put(c.getPosition(), c);
      }

      for (int i = 1; i < components.size() + 1; i++) {
        Component c = components.get(i);
        nu.xom.Element elmComponent = new nu.xom.Element("Component");
        elmComponent.addAttribute(new Attribute("Name", c.getName()));
        elmComponent.addAttribute(new Attribute("Usage", c.getUsage().toString()));
        if (c.getDatatype() != null && datatypeService.findById(c.getDatatype().getId()) != null
            && datatypeService.findById(c.getDatatype().getId()).getLabel() != null) {
          elmComponent.addAttribute(new Attribute("Datatype",
              datatypeService.findById(c.getDatatype().getId()).getLabel()));
        }
        elmComponent.addAttribute(new Attribute("MinLength", "" + c.getMinLength()));
        if (c.getMaxLength() != null && !c.getMaxLength().equals(""))
          elmComponent.addAttribute(new Attribute("MaxLength", c.getMaxLength()));
        if (c.getConfLength() != null && !c.getConfLength().equals(""))

          elmComponent.addAttribute(new Attribute("ConfLength", c.getConfLength()));
        if (c.getComment() != null && !c.getComment().equals(""))
          elmComponent.addAttribute(new Attribute("Comment", c.getComment()));
        elmComponent.addAttribute(new Attribute("Position", c.getPosition().toString()));
        if (c.getText() != null) {
          elmComponent.appendChild(this.serializeRichtext("Text", c.getText()));
        }


        List<Constraint> constraints =
            findConstraints(i, d.getPredicates(), d.getConformanceStatements());
        if (!constraints.isEmpty()) {
          for (Constraint constraint : constraints) {
            nu.xom.Element elmConstraint =
                serializeConstraintToElement(constraint, d.getName() + ".");
            elmComponent.appendChild(elmConstraint);
          }
        }
        elmDatatype.appendChild(elmComponent);
      }
      if (d.getComponents().size() == 0) {
        nu.xom.Element elmComponent = new nu.xom.Element("Component");
        elmComponent.addAttribute(new Attribute("Name", d.getName()));
        elmComponent.addAttribute(new Attribute("Position", "1"));
        elmDatatype.appendChild(elmComponent);
      }
    }
    return elmDatatype;
  }

  private nu.xom.Element serializeRichtext(String attribute, String richtext) {
    nu.xom.Element elmText1 = new nu.xom.Element("Text");
    elmText1.addAttribute(new Attribute("Type", attribute));
    elmText1.appendChild("<div class=\"fr-view\">" + richtext + "</div>");
    return elmText1;
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

  private nu.xom.Element serializeConstraintToElement(Constraint constraint, String locationName) {
    nu.xom.Element elmConstraint = new nu.xom.Element("Constraint");
    elmConstraint.addAttribute(new Attribute("Id",
        constraint.getConstraintId() == null ? "" : constraint.getConstraintId()));
    elmConstraint.addAttribute(new Attribute("Location", constraint.getConstraintTarget()
        .substring(0, constraint.getConstraintTarget().indexOf('['))));
    elmConstraint.addAttribute(new Attribute("LocationName", locationName));
    elmConstraint.appendChild(constraint.getDescription());
    if (constraint instanceof Predicate) {
      elmConstraint.addAttribute(new Attribute("Type", "pre"));
      elmConstraint
          .addAttribute(new Attribute("Usage", "C(" + ((Predicate) constraint).getTrueUsage() + "/"
              + ((Predicate) constraint).getFalseUsage() + ")"));
    } else if (constraint instanceof ConformanceStatement) {
      elmConstraint.addAttribute(new Attribute("Type", "cs"));
      elmConstraint.addAttribute(
          new Attribute("Classification", constraint.getConstraintClassification() == null ? ""
              : constraint.getConstraintClassification()));
    }
    return elmConstraint;
  }

  private InputStream exportAsXml(String xmlString) {
    // Note: inlineConstraint can be true or false

    try {
      // Generate xml file containing profile
      // File tmpXmlFile = File.createTempFile("temp", ".xml");
      File tmpXmlFile = new File("temp.xml");
      FileUtils.writeStringToFile(tmpXmlFile, xmlString, Charset.forName("UTF-8"));

      return FileUtils.openInputStream(tmpXmlFile);

    } catch (IOException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
  }

  private InputStream exportAsHtmlFromXsl(String xmlString, String xslPath) {
    try {
      File tmpHtmlFile = File.createTempFile("temp", ".html");

      // Generate xml file containing profile
      // File tmpXmlFile = File.createTempFile("temp", ".xml");
      File tmpXmlFile = new File("temp.xml");
      FileUtils.writeStringToFile(tmpXmlFile, xmlString, Charset.forName("UTF-8"));

      TransformerFactory factoryTf = TransformerFactory.newInstance();
      Source xslt = new StreamSource(this.getClass().getResourceAsStream(xslPath));
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

  public InputStream exportAsDocxFromXml(String xmlString, String xmlPath, Boolean includeToc) {
    // Note: inlineConstraint can be true or false
    try {
      File tmpHtmlFile = File.createTempFile("temp", ".html");

      // Generate xml file containing profile
      File tmpXmlFile = File.createTempFile("temp", ".xml");
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

      // createCoverPageForDocx4j(igdoc, wordMLPackage, factory); TODO Implement cover page

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
      // InputStream inputStream = new ByteArrayInputStream(html.getBytes());
      // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      // tidy.parseDOM(inputStream, outputStream);
      // File cleanTmpHtmlFile = File.createTempFile("IGDocTemp", ".html");
      // FileUtils.writeByteArrayToFile(cleanTmpHtmlFile, outputStream.toByteArray());
      // XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
      // wordMLPackage.getMainDocumentPart().getContent().addAll(
      // XHTMLImporter.convert(cleanTmpHtmlFile, null) );


      // addConformanceInformationForDocx4j(igdoc, wordMLPackage, factory);

      loadTemplateForDocx4j(wordMLPackage); // Repeats the lines above but necessary; don't delete

      File tmpFile;
      tmpFile = File.createTempFile("IgDocument", ".docx");
      wordMLPackage.save(tmpFile);


      return FileUtils.openInputStream(tmpFile);

    } catch (TransformerException | IOException | Docx4JException e) {
      e.printStackTrace();
      return new NullInputStream(1L);
    }
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
      // String templatePath = "/rendering/lri_template.dotx";
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

  private void addPageBreak(WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
    Br breakObj = new Br();
    breakObj.setType(STBrType.PAGE);

    P paragraph = factory.createP();
    paragraph.getContent().add(breakObj);
    wordMLPackage.getMainDocumentPart().getContent().add(paragraph);
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
    try {
      wordMLPackage.getMainDocumentPart().addAltChunk(AltChunkType.Xhtml,
          wrapRichText(htmlString).getBytes());
    } catch (Docx4JException e1) {
      e1.printStackTrace();
      wordMLPackage.getMainDocumentPart().addParagraphOfText("Error in rich text");
    }
  }

  private String tablesToString(List<TableLink> tables) {
    String res = "";
    if (tables != null && !tables.isEmpty()) {
      for (TableLink link : tables) {
        Table tbl = tableService.findById(link.getId());
        if (tbl != null) {
          res =
              "".equals(res) ? tbl.getBindingIdentifier() : res + " " + tbl.getBindingIdentifier();
        }
      }
    }
    return res;
  }

}
