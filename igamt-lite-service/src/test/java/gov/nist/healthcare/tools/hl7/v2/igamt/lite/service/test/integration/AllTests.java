package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit.ProfileChangeServiceTest;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit.ProfileCloneTest;

@RunWith(Suite.class)
@SuiteClasses({DatatypeServiceTest.class})
public class AllTests {

}
