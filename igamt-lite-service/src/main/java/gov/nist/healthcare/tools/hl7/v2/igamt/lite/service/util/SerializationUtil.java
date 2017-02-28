package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import com.mongodb.gridfs.GridFSDBFile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import nu.xom.Attribute;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SerializationUtil {

  @Autowired
  private FileStorageService fileStorageService;

  public String str(String value) {
    return value != null ? value : "";
  }

  public String cleanRichtext(String richtext) {
    //richtext = StringEscapeUtils.unescapeHtml4(richtext);
    richtext = richtext.replace("<br>", "<br></br>");
    richtext = richtext.replace("<p style=\"\"><br></p>", "<p></p>");
    richtext = richtext.replaceAll("[^\\p{Print}]", "?");
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
        if (elementImg.attr("alt") == null || elementImg.attr("alt").isEmpty()){
          elementImg.attr("alt", ".");
      }
      String imgStyle = elementImg.attr("style");
      elementImg.attr("style", imgStyle.replace("px;", ";"));
//    style="width: 300px;
      } catch (RuntimeException e) {
        e.printStackTrace(); // If error, we leave the original document
        // as is.
      } catch (Exception e) {
        e.printStackTrace(); // If error, we leave the original document
        // as is.
      }
    }
    for (org.jsoup.nodes.Element elementTbl : doc.select("table")) {
      if (elementTbl.attr("summary") == null || elementTbl.attr("summary").isEmpty()) {
          elementTbl.attr("summary", ".");
      }
  }

    //Renaming strong to work as html4 
    doc.select("strong").tagName("b");

    return "<div class=\"fr-view\">" + doc.body().html() + "</div>";
  }

  public void setSectionsPrefixes(Set<Section> sections, String prefix, Integer depth,
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

  private SortedSet<gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section> sortSections(Set<Section> s) {
    SortedSet<Section> sortedSet = new TreeSet<Section>();
    Iterator<Section> setIt = s.iterator();
    while (setIt.hasNext()) {
      sortedSet.add(setIt.next());
    }
    return sortedSet;
  }

  public Boolean isShowConfLength(String hl7Version) {
    //Check if hl7Version > 2.5.1
    if(hl7Version == null || "".equals(hl7Version)){
      return false;
    }
    Integer[] comparisonVersion = {2,5,1};
    String[] versionToCompare = hl7Version.split("\\.");
    if(versionToCompare !=null&&versionToCompare.length>0) {
      for (int i = 0; i < versionToCompare.length; i++) {
        Integer comparisonValue = 0;
        if (i < comparisonVersion.length) {
          comparisonValue = comparisonVersion[i];
        }
        if (Integer.valueOf(versionToCompare[i]) > comparisonValue) {
          return true;
        } else if(Integer.valueOf(versionToCompare[i]) < comparisonValue){
          return false;
        }
      }
    }
    return false;
  }
}
