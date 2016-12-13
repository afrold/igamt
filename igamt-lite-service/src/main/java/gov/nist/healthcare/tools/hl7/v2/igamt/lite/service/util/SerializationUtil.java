package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import com.mongodb.gridfs.GridFSDBFile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import nu.xom.Attribute;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class SerializationUtil {

    private static FileStorageService fileStorageService;

    @Autowired
    public void setFileStorageService(FileStorageService fileStorageService){
        this.fileStorageService = fileStorageService;
    }

    public static String str(String value) {
        return value != null ? value : "";
    }

    public static String cleanRichtext(String richtext) {
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
                        String filename = elementImg.attr("src")
                            .substring(elementImg.attr("src").indexOf("name=") + 5);
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

    public static void setSectionsPrefixes(Set<Section> sections, String prefix, Integer depth,
        nu.xom.Element element) {
        SortedSet<Section> sortedSections = sortSections(sections);
        for (gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section s : sortedSections) {
            nu.xom.Element xsect = new nu.xom.Element("Section");
            xsect.addAttribute(new Attribute("id", s.getId()));
            xsect.addAttribute(new Attribute("position", String.valueOf(s.getSectionPosition())));
            xsect.addAttribute(new Attribute("h", String.valueOf(depth)));
            if (s.getSectionTitle() != null)
                xsect.addAttribute(new Attribute("title", s.getSectionTitle()));

            if (s.getSectionContents() != null && !s.getSectionContents().isEmpty()) {
                nu.xom.Element sectCont = new nu.xom.Element("SectionContent");
                sectCont.appendChild(cleanRichtext(s.getSectionContents()));
                xsect.appendChild(sectCont);
            }

            if (depth == 1) {
                xsect.addAttribute(new Attribute("prefix", String.valueOf(s.getSectionPosition())));
                setSectionsPrefixes(s.getChildSections(), String.valueOf(s.getSectionPosition()),
                    depth + 1, xsect);
            } else {
                xsect.addAttribute(new Attribute("prefix", prefix + "." + String.valueOf(s.getSectionPosition())));
                setSectionsPrefixes(s.getChildSections(),
                    prefix + "." + String.valueOf(s.getSectionPosition()), depth + 1, xsect);
            }
            element.appendChild(xsect);
        }
    }

    private static SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortSections(Set<Section> s) {
        SortedSet<Section> sortedSet = new TreeSet<Section>();
        Iterator<Section> setIt = s.iterator();
        while (setIt.hasNext()) {
            sortedSet.add(setIt.next());
        }
        return sortedSet;
    }

}
