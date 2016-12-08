package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import com.mongodb.gridfs.GridFSDBFile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.FileStorageService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class SerializationUtil {

  @Autowired
  private static FileStorageService fileStorageService;

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
