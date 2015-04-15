/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.general;

import java.util.Calendar;
import java.util.Random;

/**
 * @author fdevaulx
 * 
 */
public class UserUtil {

	public static final String[] AUTHORITY_LIST = { "user", "author",
			  "supervisor", "admin" };
	public static final String[] ACCOUNT_TYPE_LIST = { "author",
		  "supervisor", "admin" };

	public static String generateRandom() {
		int mon = Calendar.getInstance().get(Calendar.MONTH);
		int yea = Calendar.getInstance().get(Calendar.YEAR);
		int secs = Calendar.getInstance().get(Calendar.SECOND);
		Random rmd = new Random(System.currentTimeMillis());
		int rmdFile = rmd.nextInt(1000);
		return "" + yea + mon + rmdFile + secs;
	}
}
