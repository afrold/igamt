package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.unit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileCreationService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.converters.ProfileReadConverter;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.ProfileController;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.wrappers.IntegrationProfileRequestWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.integration.ProfileControllerIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProfileControllerTest {

	@Autowired
	ProfileController controller;

	@Autowired
	ProfileService mockProfileService;

	@Autowired
	ProfileCreationService profileCreation;

	MockMvc mockMvc;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setupClass() {
		try {
			Properties p = new Properties();
			InputStream log4jFile = ProfileControllerTest.class
					.getResourceAsStream("/igl-test-log4j.properties");
			p.load(log4jFile);
			PropertyConfigurator.configure(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setProfileService(mockProfileService);
		mockMvc = standaloneSetup(controller).build();
	}
	
	@Test
	public void testGetAllPreloaded() throws Exception {
//		List<Profile> preloaded = findAllPreloaded();
//		when(mockProfileService.findAllPreloaded()).thenReturn(preloaded);
		MvcResult mvcResult = mockMvc.perform(get("/profiles")).andExpect(status().isOk()).andReturn();
		String content = mvcResult.getResponse().getContentAsString();
	}
	
//	@Test
	public void testCreateIG() throws Exception {

			MongoClient mongoClient = new MongoClient();
			ObjectMapper mapper = new ObjectMapper();
			DB db = mongoClient.getDB("igl");

			Profile profile = null;
			DBObject qry = new BasicDBObject();
			qry.put( "metaData.hl7Version", "2.7" );
			DBCollection coll = db.getCollection("profile");
			DBCursor cur = coll.find(qry);
			if (cur.hasNext()) {
				DBObject obj = cur.next();
				profile = new ProfileReadConverter().convert(obj);
			}
			IntegrationProfileRequestWrapper iprw = new IntegrationProfileRequestWrapper();
			List<String> msgs = new ArrayList<String>();
			Message[] mm = profile.getMessages().getChildren().toArray(new Message[0]);
			msgs.add(mm[2].getId());
			msgs.add(mm[5].getId());
			msgs.add(mm[9].getId());
			iprw.setHl7Version(profile.getMetaData().getHl7Version());
			iprw.setMsgIds(msgs);
			iprw.setAccountId(45L);
			iprw.setProfile(profile);
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, iprw);
			String json = writer.toString();

		MvcResult mvcResult = mockMvc.perform((RequestBuilder)post("/profiles/hl7/createIntegrationProfile")
		.contentType(MediaType.APPLICATION_JSON)
		.content(json))
		.andExpect(status().isOk())
		.andReturn();
		Object obj = mvcResult.getAsyncResult();
	}

	private List<Profile> findAllPreloaded() {
		List<Profile> profiles = new ArrayList<Profile>();
		Profile p = profile();
		profiles.add(p);
		p = profile();
		profiles.add(p);
		return profiles;
	}

	private Profile profile() {
		String xmlContentsProfile;
		try {
			xmlContentsProfile = IOUtils
					.toString(ProfileControllerIntegrationTest.class
							.getResourceAsStream("/vxu/Profile.xml"));
			String xmlValueSet = IOUtils
					.toString(ProfileControllerIntegrationTest.class
							.getResourceAsStream("/vxu/ValueSets_all.xml"));
			String xmlConstraints = IOUtils
					.toString(ProfileControllerIntegrationTest.class
							.getResourceAsStream("/vxu/Constraints.xml"));
			Profile p = new ProfileSerializationImpl().deserializeXMLToProfile(
					xmlContentsProfile, xmlValueSet, xmlConstraints);

			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
