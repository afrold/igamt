/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgment if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.io.InputStream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.exception.DynTable0396Exception;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util.DynamicTable0396Util;

/**
 * @author Harold Affo
 * 
 */
@RestController
@RequestMapping("/data-management")
public class DataManagementController {

    static final Logger logger = LoggerFactory.getLogger(DataManagementController.class);
    public final String DEFAULT_PAGE_SIZE = "0";
    private static final String TABLE_0396_URL = "https://www.hl7.org/documentcenter/public/wg/vocab/Tbl0396.xls";

    @Autowired
    private TableService tableService;

    // private List<String> skippedValidationEmails = new ArrayList<String>();

    public DataManagementController() {
	// skippedValidationEmails = new ArrayList<String>();
	// skippedValidationEmails.add("haffo@nist.gov");
	// skippedValidationEmails.add("rsnelick@nist.gov");
    }

    @Inject
    AccountRepository accountRepository;

    @Autowired
    UserService userService;

    @PreAuthorize("hasRole('admin')")
    @RequestMapping(value = "/dynamic-table-0396", method = RequestMethod.GET)
    public Table getDynamicTable0396() {
	Table table = tableService.findDynamicTable0396();
	return table;
    }

    @PreAuthorize("hasRole('admin')")
    @RequestMapping(value = "/dynamic-table-0396/fetch-updates", method = RequestMethod.POST)
    public Table fetchUdpates() throws DynTable0396Exception {
	InputStream io = DynamicTable0396Util.downloadExcelFile();
	return tableService.updateDynTable0396(io);
    }

}
