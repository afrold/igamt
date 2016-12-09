package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl;

import com.mongodb.gridfs.GridFSDBFile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableElement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableMetadata;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.SerializableStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SerializationService;
import nu.xom.Document;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URL;

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
 * <p>
 * Created by Maxence Lefort on 12/7/16.
 */
public class SerializationServiceImpl implements SerializationService {

    @Autowired
    FileStorageService fileStorageService;

    @Override public Document serializeIGDocument(IGDocument igDocument) {
        SerializableStructure serializableStructure = new SerializableStructure();
        SerializableMetadata serializableMetadata = new SerializableMetadata(igDocument.getMetaData(),igDocument.getProfile().getMetaData(),igDocument.getDateUpdated());
        serializableStructure.addSerializableElement(serializableMetadata);
        return null;
    }

    @Override public Document serializeDatatypeLibrary(IGDocument igDocument) {
        return null;
    }

    @Override public Document serializeElement(SerializableElement element) {
        SerializableStructure serializableStructure = new SerializableStructure();
        serializableStructure.addSerializableElement(element);
        return serializableStructure.serializeStructure();
    }

    private String cleanRichtext(String richtext) {
        org.jsoup.nodes.Document doc = Jsoup.parse(richtext);
        Elements elements1 = doc.select("h1");
        elements1.tagName("p").attr("style",
            "display: block;font-size: 16.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
        // elements1.after("<hr />");
        Elements elements2 = doc.select("h2");
        elements2.tagName("p").attr("style",
            "display: block;font-size: 14.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
        Elements elements3 = doc.select("h3");
        elements3.tagName("p").attr("style",
            "display: block;font-size: 12.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");
        Elements elements4 = doc.select("h4");
        elements4.tagName("p").attr("style",
            "display: block;font-size: 10.0pt;margin-left: 0;margin-right: 0;font-weight: bold;");

        for (org.jsoup.nodes.Element elementImg : doc.select("img")) {
            try {
                if (elementImg.attr("src") != null && !"".equals(elementImg.attr("src"))) {
                    InputStream imgis = null;
                    String ext = null;
                    byte[] bytes = null;
                    if (elementImg.attr("src").indexOf("name=") != -1) {
                        String filename = elementImg.attr("src").substring(elementImg.attr("src").indexOf("name=") + 5);
                        ext = FilenameUtils.getExtension(filename);
                        GridFSDBFile dbFile = fileStorageService.findOneByFilename(filename);
                        if (dbFile != null) {
                            imgis = dbFile.getInputStream();
                            bytes = IOUtils.toByteArray(imgis);
                        }
                    } else {
                        String filename = elementImg.attr("src");
                        ext = FilenameUtils.getExtension(filename);
                        URL url = new URL(filename);
                        bytes = IOUtils.toByteArray(url);
                    }
                    if (bytes != null && bytes.length > 0) {
                        String imgEnc = Base64.encodeBase64String(bytes);
                        String texEncImg = "data:image/" + ext + ";base64," + imgEnc;
                        elementImg.attr("src", texEncImg);
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace(); // If error, we leave the original document
                // as is.
            } catch (Exception e) {
                e.printStackTrace(); // If error, we leave the original document
                // as is.
            }
        }
        return "<div class=\"fr-view\">" + doc.body().html() + "</div>";
    }
}
