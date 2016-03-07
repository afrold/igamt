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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.AppInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.HttpUtil;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harold Affo (NIST)
 * 
 */
@RestController
@RequestMapping("/appInfo")
public class AppInfoController {

	@Autowired
	private AppInfo appInfo;

	@RequestMapping(method = RequestMethod.GET)
	public AppInfo info(HttpServletRequest request) {
		if (appInfo.getUploadedImagesUrl() == null) {
			appInfo.setUploadedImagesUrl(HttpUtil.getImagesRootUrl(request));
		}
		return appInfo;
	}
}
