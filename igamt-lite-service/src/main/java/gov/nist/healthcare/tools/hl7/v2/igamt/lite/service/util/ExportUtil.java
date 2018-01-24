package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.dml.CTPositiveSize2D;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;

import com.mongodb.gridfs.GridFSDBFile;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AppInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CodeUsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ExportFontConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.MetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.UsageConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;

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
public class ExportUtil {

	Logger logger = LoggerFactory.getLogger(ExportUtil.class);

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private DocxExportUtil docxExportUtil;
	
	@Autowired
	private AppInfo appInfo;

	private static final int MAX_IMAGE_WIDTH_EMU = 6000000;

	public InputStream exportAsDocxFromXml(String xmlString, String xmlPath, ExportParameters exportParameters,
			MetaData metaData, Date dateUpdated) {

		try {
			File tmpHtmlFile = doTransformToTempHtml(xmlString, xmlPath, exportParameters);

			String html = FileUtils.readFileToString(tmpHtmlFile);

			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
					.load(this.getClass().getResourceAsStream("/rendering/lri_template.dotx"));
			ObjectFactory factory = Context.getWmlObjectFactory();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String formattedDateUpdated = simpleDateFormat.format(dateUpdated);
			docxExportUtil.createCoverPageForDocx4j(wordMLPackage, factory, metaData, formattedDateUpdated);

			if (exportParameters.isIncludeTOC()) {
				docxExportUtil.createTableOfContentForDocx4j(wordMLPackage, factory);
			}

			FieldUpdater updater = new FieldUpdater(wordMLPackage);
			try {
				updater.update(true);
			} catch (Docx4JException e1) {
				e1.printStackTrace();
			}

			/*
			 * AlternativeFormatInputPart afiPart = new
			 * AlternativeFormatInputPart(new PartName("/hw.html"));
			 * afiPart.setBinaryData(html.getBytes());
			 * afiPart.setContentType(new ContentType("text/html"));
			 * Relationship altChunkRel =
			 * wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);
			 * 
			 * // .. the bit in document body CTAltChunk ac =
			 * Context.getWmlObjectFactory().createCTAltChunk();
			 * ac.setId(altChunkRel.getId());
			 * wordMLPackage.getMainDocumentPart().addObject(ac);
			 * 
			 * // .. content type
			 * wordMLPackage.getContentTypeManager().addDefaultContentType(
			 * "html", "text/html");
			 */
			docxExportUtil.replaceVersionInFooter(wordMLPackage,appInfo.getVersion());
			html = cleanHtml(IOUtils.toInputStream(html)).toString();
			XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
			wordMLPackage.getMainDocumentPart().getContent().addAll(xHTMLImporter.convert(html, null));
			List<Object> nodes = DocxExportUtil.getAllElementFromObject(wordMLPackage.getMainDocumentPart(), org.docx4j.wml.Tbl.class);
			BooleanDefaultTrue bdt = Context.getWmlObjectFactory().createBooleanDefaultTrue();
			for (Object node : nodes) {
				if (node instanceof Tbl) {
					Tbl tbl = (Tbl) node;
					if (tbl.getContent().get(0) instanceof Tr) {
						Tr tr = (Tr) tbl.getContent().get(0);
						tr.getTrPr().getCnfStyleOrDivIdOrGridBefore()
								.add(Context.getWmlObjectFactory().createCTTrPrBaseTblHeader(bdt));
					}
				}
			}
			nodes = DocxExportUtil.getAllElementFromObject(wordMLPackage.getMainDocumentPart(), org.docx4j.wml.Drawing.class);
			for (Object node : nodes) {
				if (node instanceof Drawing) {
					Drawing drawing = (Drawing) node;
					CTPositiveSize2D currentSize = ((Inline) drawing.getAnchorOrInline().get(0)).getExtent();
					if (currentSize.getCx() > MAX_IMAGE_WIDTH_EMU) {
						CTPositiveSize2D size = new CTPositiveSize2D();
						size.setCy(currentSize.getCy() * MAX_IMAGE_WIDTH_EMU / currentSize.getCx());
						size.setCx(MAX_IMAGE_WIDTH_EMU);
						((Inline) drawing.getAnchorOrInline().get(0)).setExtent(size);
					}
				}
			}
			docxExportUtil.loadTemplateForDocx4j(wordMLPackage); // Repeats the
																	// lines
																	// above but
																	// necessary;
																	// don't
																	// delete

			File tmpFile;
			tmpFile = File.createTempFile("IgDocument" + UUID.randomUUID().toString(), ".docx");

			/*
			 * XHTMLImporterImpl XHTMLImporter = new
			 * XHTMLImporterImpl(wordMLPackage);
			 * 
			 * wordMLPackage.getMainDocumentPart().getContent().addAll(
			 * XHTMLImporter.convert( html, null) );
			 */

			wordMLPackage.save(tmpFile);

			return FileUtils.openInputStream(tmpFile);

		} catch (TransformerException | IOException | Docx4JException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	public InputStream exportAsHtmlFromXsl(String xmlString, String xslPath, ExportParameters exportParameters,
			MetaData metaData){

		try {
			// generate cover picture
			if (metaData != null && metaData.getCoverPicture() != null && !metaData.getCoverPicture().isEmpty()) {
				InputStream imgis;
				byte[] bytes = null;
				String filename = docxExportUtil.parseFileName(metaData.getCoverPicture());
				String ext = FilenameUtils.getExtension(filename);
				GridFSDBFile dbFile = fileStorageService.findOneByFilename(filename);
				if (dbFile != null) {
					imgis = dbFile.getInputStream();
					bytes = IOUtils.toByteArray(imgis);
				}
				if (bytes != null && bytes.length > 0) {
					String coverImage = "data:image/" + ext + ";base64," + Base64.getEncoder().encodeToString(bytes);
					exportParameters.setImageLogo(coverImage);
				}
			}
			File tmpHtmlFile = doTransformToTempHtml(xmlString, xslPath, exportParameters);

			ByteArrayOutputStream outputStream = cleanHtml(FileUtils.openInputStream(tmpHtmlFile));
			return new ByteArrayInputStream(outputStream.toByteArray());

		} catch (TransformerException | IOException e) {
			e.printStackTrace();
			return new NullInputStream(1L);
		}
	}

	public ExportParameters setExportParameters(String documentTitle, boolean includeTOC, boolean inlineConstraints,
			String targetFormat, ExportConfig exportConfig, ExportFontConfig exportFontConfig) {
		return new ExportParameters(inlineConstraints, includeTOC, targetFormat, documentTitle, null,
				exportConfig.getMessageColumn().getColumns(), exportConfig.getCompositeProfileColumn().getColumns(),
				exportConfig.getProfileComponentColumn().getColumns(), exportConfig.getSegmentColumn().getColumns(),
				exportConfig.getDatatypeColumn().getColumns(), exportConfig.getValueSetColumn().getColumns(),exportConfig.getValueSetsMetadata(),
				exportConfig.getDatatypeMetadataConfig(), exportConfig.getSegmentMetadataConfig(), exportConfig.getMessageMetadataConfig(), exportConfig.getCompositeProfileMetadataConfig(), exportFontConfig,appInfo.getVersion());
	}

	// Private methods, alphabetically ordered

	private File doTransformToTempHtml(String xmlString, String xslPath, ExportParameters exportParameters)
			throws IOException, TransformerException {
		File tmpHtmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".html");
		// File tmpHtmlFile = new File("temp_" + UUID.randomUUID().toString() +
		// ".html");
		// Generate xml file containing profile
		File tmpXmlFile = File.createTempFile("temp" + UUID.randomUUID().toString(), ".xml");
		// File tmpXmlFile = new File("temp_" + UUID.randomUUID().toString() +
		// ".xml");
		FileUtils.writeStringToFile(tmpXmlFile, xmlString, Charset.forName("UTF-8"));
		TransformerFactory factoryTf = TransformerFactory.newInstance();
		factoryTf.setURIResolver(new XsltIncludeUriResover());
		Source xslt = new StreamSource(this.getClass().getResourceAsStream(xslPath));
		Transformer transformer;
		// Apply XSL transformation on xml file to generate html
		transformer = factoryTf.newTransformer(xslt);
		// Set the parameters
		for (Map.Entry<String, String> param : exportParameters.toMap().entrySet()) {
			if (param != null && param.getKey() != null && param.getValue() != null) {
				transformer.setParameter(param.getKey(), param.getValue());
			}
		}
		transformer.transform(new StreamSource(tmpXmlFile), new StreamResult(tmpHtmlFile));
		return tmpHtmlFile;
	}

	public ByteArrayOutputStream cleanHtml(InputStream html) {
			Properties oProps = new Properties();
			oProps.setProperty("new-blocklevel-tags", "figcaption");
			Tidy tidy = new Tidy();
			tidy.setConfigurationFromProps(oProps);
		tidy.setWraplen(Integer.MAX_VALUE);
		tidy.setDropEmptyParas(true);
		tidy.setXHTML(true);
		tidy.setShowWarnings(false); // to hide errors
		tidy.setQuiet(true); // to hide warning
		tidy.setMakeClean(true);
		tidy.setTidyMark(false);
		//tidy.setBreakBeforeBR(true);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		tidy.parseDOM(html, outputStream);
		return outputStream;
	}

	public static boolean diplayUsage(Usage usageToCompare, UsageConfig usageConfig) {
		switch (usageToCompare) {
		case R:
			return usageConfig.getR();
		case RE:
			return usageConfig.getRe();
		case C:
			return usageConfig.getC();
		case X:
			return usageConfig.getX();
		case O:
			return usageConfig.getO();
		default:
			return false;
		}
	}

	public static boolean diplayCodeUsage(String codeUsageToCompare, CodeUsageConfig codeUsageConfig) {
		switch (codeUsageToCompare.trim().toLowerCase()) {
		case "r":
			return codeUsageConfig.getR();
		case "p":
			return codeUsageConfig.getP();
		case "e":
			return codeUsageConfig.getE();
		default:
			return false;
		}
	}

	public static boolean displayUnbindedTable(ExportConfig exportConfig, Table table) {
		if ((exportConfig.isUnboundCustom() && !table.getScope().equals(Constant.SCOPE.HL7STANDARD))
				|| (exportConfig.isUnboundHL7() && table.getScope().equals(Constant.SCOPE.HL7STANDARD))) {
			return true;
		}
		return false;
	}

}
