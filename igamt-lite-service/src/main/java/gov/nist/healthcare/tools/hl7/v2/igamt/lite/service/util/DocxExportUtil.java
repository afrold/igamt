package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.mongodb.gridfs.GridFSDBFile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibraryMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.CTOverride;
import org.docx4j.openpackaging.contenttype.ContentTypeManager;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTRel;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;

/**
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of their
 * official duties. Pursuant to title 17 Section 105 of the United States Code
 * this software is not subject to copyright protection and is in the public
 * domain. This is an experimental system. NIST assumes no responsibility
 * whatsoever for its use by other parties, and makes no guarantees, expressed
 * or implied, about its quality, reliability, or any other characteristic. We
 * would appreciate acknowledgement if the software is used. This software can
 * be redistributed and/or modified freely provided that any derivative works
 * bear some notice that they are derived from it, and any modified versions
 * bear some notice that they have been modified.
 * <p>
 * Created by Maxence Lefort on 11/01/16.
 */
@Service
public class DocxExportUtil {

	Logger logger = LoggerFactory.getLogger(DocxExportUtil.class);

	@Autowired
	private FileStorageService fileStorageService;

	public void createCoverPageForDocx4j(WordprocessingMLPackage wordMLPackage, ObjectFactory factory,
			MetaData metaData, String dateUpdated) {

		BufferedImage image = null;
		try {
			if (metaData.getCoverPicture() != null && !metaData.getCoverPicture().isEmpty()) {
				InputStream imgis;
				byte[] bytes = null;
				String filename = parseFileName(metaData.getCoverPicture());
				GridFSDBFile dbFile = fileStorageService.findOneByFilename(filename);
				if (dbFile != null) {
					imgis = dbFile.getInputStream();
					bytes = IOUtils.toByteArray(imgis);
				}
				if (bytes != null && bytes.length > 0) {
						addImageToPackage(wordMLPackage, bytes);
				}
			}
		} catch (Exception e) {
			logger.warn("Unable to add image");
			e.printStackTrace();
		}

		if ( null!= metaData) {
				if(metaData instanceof DocumentMetaData) {
						DocumentMetaData documentMetaData = (DocumentMetaData) metaData;
						if (null != documentMetaData.getTitle()) {
								wordMLPackage.getMainDocumentPart()
										.addStyledParagraphOfText("Title", documentMetaData.getTitle());
								addLineBreak(wordMLPackage, factory);
						}
						if (null != documentMetaData.getSubTitle() && !documentMetaData.getSubTitle().isEmpty()) {
								wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Subtitle",
										"Subtitle " + documentMetaData.getSubTitle());
						}
				} else {
						if(null!=metaData.getName() & !"".equals(metaData.getName())){
								wordMLPackage.getMainDocumentPart()
										.addStyledParagraphOfText("Title", metaData.getName());
								addLineBreak(wordMLPackage, factory);
						}
						if(null!=metaData.getDescription() & !"".equals(metaData.getDescription())){
								wordMLPackage.getMainDocumentPart()
										.addStyledParagraphOfText("Style1", "Description: "+metaData.getDescription());
								addLineBreak(wordMLPackage, factory);
						}
				}
				if(null!=dateUpdated && !dateUpdated.isEmpty()) {
					wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", dateUpdated);
				}
				addLineBreak(wordMLPackage, factory);
				addLineBreak(wordMLPackage, factory);
				if (null != metaData.getHl7Version() && !"".equals(metaData.getHl7Version())) {
					wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1",
							"HL7 Version " + metaData.getHl7Version());
				}
				if (null != metaData.getOrgName() && !"".equals(metaData.getOrgName())) {
					wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Style1", metaData.getOrgName());
				}
		}
		addPageBreak(wordMLPackage, factory);
	}

		protected String parseFileName(String coverPicture) {
				if(coverPicture.contains("name=")){
						return coverPicture.substring(coverPicture.indexOf("name=")+"name=".length());
				}
				return "";

		}

		public void createTableOfContentForDocx4j(WordprocessingMLPackage wordMLPackage, ObjectFactory factory) {
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
		txt.setValue("TOC \\o \"1-5\" \\h \\z \\u \\h");
		r.getContent().add(factory.createRInstrText(txt));
		paragraphForTOC.getContent().add(r1);

		FldChar fldcharend = factory.createFldChar();
		fldcharend.setFldCharType(STFldCharType.END);
		R r2 = factory.createR();
		r2.getContent().add(getWrappedFldChar(fldcharend));
		paragraphForTOC.getContent().add(r2);

		wordMLPackage.getMainDocumentPart().getContent().add(paragraphForTOC);
	}

	public void loadTemplateForDocx4j(WordprocessingMLPackage wordMLPackage) {
		try {
			// Replace dotx content type with docx
			ContentTypeManager ctm = wordMLPackage.getContentTypeManager();

			// Get <Override PartName="/word/document.xml"
			// ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml"/>
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
			org.docx4j.relationships.Relationship rel = new org.docx4j.relationships.ObjectFactory()
					.createRelationship();
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

	// Private methods, alphabetically ordered

	private static void addImageToPackage(WordprocessingMLPackage wordMLPackage, byte[] bytes) throws Exception {
		BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
		int docPrId = 1;
		int cNvPrId = 2;
		Inline inline = imagePart.createImageInline("Filename hint", "Alternative text", docPrId, cNvPrId, false);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JAXBElement getWrappedFldChar(FldChar fldchar) {
		return new JAXBElement(new QName(Namespaces.NS_WORD12, "fldChar"), FldChar.class, fldchar);
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
}
