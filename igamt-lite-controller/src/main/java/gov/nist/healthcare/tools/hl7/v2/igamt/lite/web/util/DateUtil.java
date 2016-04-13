package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public String getCurrentDate() {
		return format(Calendar.getInstance().getTime());
	}

	public String format(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(date);
	}
}
