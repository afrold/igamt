package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ProfileChangeServiceTest.class, ProfileCloneTest.class,
		ProfileExportTest.class, ProfileLoadStandardTest.class, ProfileLoadCorrectnessTest.class,
		ProfileValidationTest.class, ProfileVerificationTest.class, SerializationTest.class })

public class AllTests {

}
