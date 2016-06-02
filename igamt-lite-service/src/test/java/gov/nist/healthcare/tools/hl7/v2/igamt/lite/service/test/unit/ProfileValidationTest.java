package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.unit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.repo.ProfileRepository;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileValidationException;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileValidationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceContextUnit.class})
public class ProfileValidationTest {
  Logger logger = LoggerFactory.getLogger(ProfileValidationTest.class);

  @Autowired
  private Environment env;

  @Resource
  ApplicationContext ctx;

  @Autowired
  ProfileRepository profileRepository;

  @Autowired
  ProfileValidationService profileValidation;

  String referenceProfile = "561c7ffbef869a3dfafccc4a";


  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    try {
      Properties p = new Properties();
      InputStream log4jFile =
          ProfileVerificationTest.class.getResourceAsStream("/igl-test-log4j.properties");
      p.load(log4jFile);
      PropertyConfigurator.configure(p);

    } catch (IOException e) {
      e.printStackTrace();
    }

    MongoClient mongo = (MongoClient) ctx.getBean("mongo");
    DB db = mongo.getDB(env.getProperty("mongo.dbname"));
    DBCollection collection = db.getCollection("profile");

    if (profileRepository.findOne(referenceProfile) == null) {

      String profileJson =
          IOUtils.toString(this.getClass().getClassLoader()
              .getResource("profileUserTest/profile_test.json"));
      DBObject dbObject = (DBObject) JSON.parse(profileJson);
      collection.save(dbObject);
    }

  }

  @After
  public void tearDown() throws Exception {
    // profileRepository.delete(profileRepository.findOne(referenceProfile));
  }

  @Test
  public void testValidateProfile() {

    int pIndex = ThreadLocalRandom.current().nextInt(0, (int) profileRepository.count());
    Profile p = profileRepository.findAll().get(pIndex);

    p = profileRepository.findOne(referenceProfile);

    try {
      profileValidation.validate(p);
      fail();
    } catch (ProfileValidationException e) {
      assertTrue(e.getMessage().contains("invalid"));
    }
  }
}
