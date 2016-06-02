package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util.FileStorageUtil;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

  public static String getAppUrl(HttpServletRequest request) {
    String scheme = request.getScheme();
    String host = request.getHeader("Host");
    String url = scheme + "://" + host + request.getContextPath();
    return url;
  }

  public static String getImagesRootUrl(HttpServletRequest request) {
    return HttpUtil.getAppUrl(request) + "/api" + FileStorageUtil.root;
  }
}
