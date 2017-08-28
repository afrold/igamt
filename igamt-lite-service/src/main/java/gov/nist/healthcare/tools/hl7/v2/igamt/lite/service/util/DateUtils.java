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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Harold Affo (harold.affo@nist.gov) Jun 2, 2015
 */
public class DateUtils {

  public static final String FORMAT = "yyyy/MM/dd HH:mm:ss";
  public static final String FORMAT2 = "MMM dd yyyy";


  public static String getCurrentTime() {
    DateFormat dateFormat = new SimpleDateFormat(FORMAT);
    return dateFormat.format(Calendar.getInstance().getTime());
  }

  public static String format(Date date) {
    if (date != null) {
      DateFormat dateFormat = new SimpleDateFormat(FORMAT);
      return dateFormat.format(date);
    }
    return null;
  }

  public static String formatCoverPageDate(Date date) {
    if (date != null) {
      DateFormat dateFormat = new SimpleDateFormat(FORMAT2);
      return dateFormat.format(date);
    }
    return null;
  }


  public static Date parse(String date) {
    try {
      if (date != null) {
        DateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.parse(date);
      }
    } catch (ParseException e) {
    }
    return null;
  }

  public static Date getDate(String dataString) throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat(FORMAT);
    return dateFormat.parse(dataString);
  }

  public static Date getCurrentDate() {
    return Calendar.getInstance().getTime();
  }

}
