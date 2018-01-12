package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Section;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import nu.xom.Attribute;

@Service
public class SerializationUtil {

  @Autowired
  private FileStorageService fileStorageService;

  private static final Integer IMG_MAX_WIDTH = 100;

  public String str(String value) {
    return value != null ? value : "";
  }

  public String cleanRichtext(String richtext) {
    // richtext = StringEscapeUtils.unescapeHtml4(richtext);
    richtext = richtext.replace("<br>", "<br />");
    if (richtext.contains("<pre>")) {
      richtext = richtext.replace("\n", "<br />");
    }
    richtext = richtext.replace("<p style=\"\"><br></p>", "");
    richtext = richtext.replace("<p ", "<div ");
    richtext = richtext.replace("<p>", "<div>");
    richtext = richtext.replace("</p>", "</div>");
    //richtext = richtext.replace("&reg;","&amp;reg;");
    richtext = richtext.replace("&lsquo;", "&#39;");
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
    Elements elementsPre = doc.select("pre");
    elementsPre.tagName("p").attr("class", "codeParagraph");
    for (org.jsoup.nodes.Element elementImg : doc.select("img")) {
      try {
        if (elementImg.attr("src") != null && !"".equals(elementImg.attr("src"))) {
          InputStream imgis = null;
          String ext = null;
          byte[] bytes = null;
          if (elementImg.attr("src").indexOf("name=") != -1) {
            String filename =
                elementImg.attr("src").substring(elementImg.attr("src").indexOf("name=") + 5);
            ext = FilenameUtils.getExtension(filename);
            GridFSDBFile dbFile = fileStorageService.findOneByFilename(filename);
            if (dbFile != null) {
              imgis = dbFile.getInputStream();
              /*
               * This is only used if we want to resize the image result of the bytes array String
               * style = elementImg.attr("style"); StyleSheet styleSheet = new StyleSheet();
               * AttributeSet dec = styleSheet.getDeclaration(style); Object width =
               * dec.getAttribute(CSS.Attribute.WIDTH); double widthDouble =
               * Double.parseDouble(width.toString().replace("px", "")); Object height =
               * dec.getAttribute(CSS.Attribute.HEIGHT); double heightDouble =
               * Double.parseDouble(height.toString().replace("px", ""));
               * if(widthDouble>IMG_MAX_WIDTH){ heightDouble =
               * (heightDouble*IMG_MAX_WIDTH)/widthDouble; widthDouble = IMG_MAX_WIDTH; } bytes =
               * this.scale(IOUtils.toByteArray(imgis),(int) widthDouble,(int) heightDouble);
               * elementImg.removeAttr("style");
               * elementImg.attr("width","'"+(int)widthDouble+"px'");
               * elementImg.attr("height","'"+(int)heightDouble+"px'");
               */
              bytes = IOUtils.toByteArray(imgis);
            }
          } else {
            String filename = elementImg.attr("src");
            ext = FilenameUtils.getExtension(filename);
            URL url = new URL(filename);
            bytes = IOUtils.toByteArray(url);
          }
          if (bytes != null && bytes.length > 0) {
            String imgEnc = Base64.getEncoder().encodeToString(bytes);
            String texEncImg = "data:image/" + ext + ";base64," + imgEnc;
            elementImg.attr("src", texEncImg);
          }
        }
        if (elementImg.attr("alt") == null || elementImg.attr("alt").isEmpty()) {
          elementImg.attr("alt", ".");
        }
        // String imgStyle = elementImg.attr("style");
        // elementImg.attr("style", "" + imgStyle);
        // style="width: 300px;
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
    for (org.jsoup.nodes.Element element : doc.select("td")) {
      removeEndingBrTag(element);
    }
    for (org.jsoup.nodes.Element element : doc.select("th")) {
      removeEndingBrTag(element);
    }
    for (org.jsoup.nodes.Element element : doc.select("div")) {
      element.html("<br/>" + element.html());
      removeDoubleBrTag(element);
    }
    Node bodyNode = doc.childNode(0).childNode(1);
    if (bodyNode.childNodeSize() > 0) {
      Node lastNode = bodyNode.childNode(bodyNode.childNodeSize() - 1);
      if (lastNode instanceof Element) {
        removeEndingBrTag((Element) lastNode);
      }
    }

    // Renaming strong to work as html4
    doc.select("strong").tagName("b");
    String html = doc.body().html();
    html = html.replace("<br>", "<br />");
    return "<div class=\"fr-view\">" + html + "</div>";
  }

  public void removeDoubleBrTag(Element element) {
    ArrayList<Element> toBeRemoved = new ArrayList<>();
    Element previousElement = null;
    for (Node node : element.childNodes()) {
      if (node instanceof Element) {
        if (previousElement != null) {
          if (previousElement.tag().getName().equals("br")
              && ((Element) node).tag().getName().equals("br")) {
            toBeRemoved.add((Element) node);
          }
        }
        previousElement = (Element) node;
      } else {
        previousElement = null;
      }
    }
    for (Element elementToBeRemoved : toBeRemoved) {
      elementToBeRemoved.remove();
    }
  }

  private void removeEndingBrTag(Element element) {
    if (element.childNodeSize() > 0) {
      boolean isLastElementNotBr = false;
      int i = 1;
      while (!isLastElementNotBr && element.childNodeSize() >= i) {
        Node node = element.childNodes().get(element.childNodeSize() - i);
        i++;
        if (node instanceof Element) {
          Element childElement = (Element) node;
          if (childElement.tagName().equals("br")) {
            childElement.remove();
          } else {
            isLastElementNotBr = true;
          }
        } else {
          isLastElementNotBr = true;
        }
      }
    }
  }

  private byte[] scale(byte[] fileData, int width, int height) {
    ByteArrayInputStream in = new ByteArrayInputStream(fileData);
    try {
      BufferedImage img = ImageIO.read(in);
      if (height == 0) {
        height = (width * img.getHeight()) / img.getWidth();
      }
      if (width == 0) {
        width = (height * img.getWidth()) / img.getHeight();
      }
      Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      ImageIO.write(imageBuff, "jpg", buffer);

      return buffer.toByteArray();
    } catch (IOException e) {
      return fileData;
    }
  }

  private BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight,
      boolean preserveAlpha) {
    int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
    Graphics2D g = scaledBI.createGraphics();
    if (preserveAlpha) {
      g.setComposite(AlphaComposite.Src);
    }
    g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
    g.dispose();
    return scaledBI;
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
        setSectionsPrefixes(s.getChildSections(), String.valueOf(s.getSectionPosition()), depth + 1,
            xsect);
      } else {
        xsect.addAttribute(
            new Attribute("prefix", prefix + "." + String.valueOf(s.getSectionPosition())));
        setSectionsPrefixes(s.getChildSections(),
            prefix + "." + String.valueOf(s.getSectionPosition()), depth + 1, xsect);
      }
      element.appendChild(xsect);
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

  public Boolean isShowConfLength(String hl7Version) {
    // Check if hl7Version > 2.5.1
    // if(hl7Version == null || "".equals(hl7Version)){
    // return false;
    // }
    // Integer[] comparisonVersion = {2,5,1};
    // String[] versionToCompare = hl7Version.split("\\.");
    // if(versionToCompare !=null&&versionToCompare.length>0) {
    // for (int i = 0; i < versionToCompare.length; i++) {
    // Integer comparisonValue = 0;
    // if (i < comparisonVersion.length) {
    // comparisonValue = comparisonVersion[i];
    // }
    // if(versionToCompare[i].contains("*")){
    // return true;
    // }
    // if (Integer.valueOf(versionToCompare[i]) > comparisonValue) {
    // return true;
    // } else if(Integer.valueOf(versionToCompare[i]) < comparisonValue){
    // return false;
    // }
    // }
    // }
    // temporary fix
    return true;
  }
}
