package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.FileStorageUtil;

public class HttpUtil {

  public static String getAppUrl(HttpServletRequest request) {
    String scheme = request.getScheme();
    String host = request.getHeader("Host");
    if (host.contains("hl7v2.igamt.nist.gov:")) {
      host = host.substring(0, host.indexOf(":"));
    }
    String url = scheme + "://" + host + request.getContextPath();
    System.out.println(url);

    return url;
  }

  public static String getImagesRootUrl(HttpServletRequest request) {
    return HttpUtil.getAppUrl(request) + "/api" + FileStorageUtil.root;
  }

  public static String base64UrlDecode(String input) {
    Base64 decoder = new Base64(true);
    byte[] decodedBytes = decoder.decode(input);
    String result = new String(decodedBytes);
    return result;
  }


}
