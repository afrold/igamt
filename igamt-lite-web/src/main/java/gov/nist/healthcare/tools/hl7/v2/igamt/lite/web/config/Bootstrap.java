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

package gov.nist.healthcare.tools.hl7.v2.iz.tool.web.config;

import gov.nist.healthcare.tools.core.models.ConnectivityTestContext;
import gov.nist.healthcare.tools.core.models.ContextFreeTestContext;
import gov.nist.healthcare.tools.core.models.EnvelopeTestContext;
import gov.nist.healthcare.tools.core.models.Message;
import gov.nist.healthcare.tools.core.models.Profile;
import gov.nist.healthcare.tools.core.models.SutType;
import gov.nist.healthcare.tools.core.models.TestCase;
import gov.nist.healthcare.tools.core.models.TestPlan;
import gov.nist.healthcare.tools.core.models.TestStory;
import gov.nist.healthcare.tools.core.repo.ConnectivityTestContextRepository;
import gov.nist.healthcare.tools.core.repo.ConnectivityTransactionRepository;
import gov.nist.healthcare.tools.core.repo.ContextFreeTestContextRepository;
import gov.nist.healthcare.tools.core.repo.EnvelopeTestContextRepository;
import gov.nist.healthcare.tools.core.repo.TestCaseRepository;
import gov.nist.healthcare.tools.core.repo.TestPlanRepository;
import gov.nist.healthcare.tools.core.repo.UserRepository;
import gov.nist.healthcare.tools.hl7.v2.iz.tool.domain.IZTestType;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Bootstrap implements InitializingBean {

	private final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

	 
}
