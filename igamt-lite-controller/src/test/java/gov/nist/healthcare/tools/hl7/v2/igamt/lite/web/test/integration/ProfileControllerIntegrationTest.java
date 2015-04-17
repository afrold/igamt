package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileScope;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.ProfileController;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.TestWebAppConfig;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestWebAppConfig.class })
public class ProfileControllerIntegrationTest {

	@InjectMocks
	ProfileController controller;

	@Autowired
	ProfileService profileService;

	MockMvc mockMvc;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setProfileService(profileService);
		mockMvc = standaloneSetup(controller).build();

	}

	@Test
	public void testGetAllPreloaded() throws Exception {
		Profile profile = profile();
		assertNotNull("Profile is null.", profile);
		profileService.save(profile);
		mockMvc.perform(get("/profiles")).andExpect(status().isOk());

	}

	// @Test
	// public void testGetProfile() throws Exception {
	// Profile profile = profile();
	// assertNotNull("Profile is null.", profile);
	// profileService.save(profile);
	// assertNotNull("Profile is not saved.", profile.getId());
	// String pId = profile.getId();
	// mockMvc.perform(get("/profiles/" + pId)).andExpect(status().isOk());
	// }

	private Profile profile() throws CloneNotSupportedException {
		String xmlContentsProfile;
		try {
			xmlContentsProfile = IOUtils
					.toString(ProfileControllerIntegrationTest.class
							.getResourceAsStream("/vxu2/Profile.xml"));
			String xmlValueSet = IOUtils
					.toString(ProfileControllerIntegrationTest.class
							.getResourceAsStream("/vxu2/ValueSets_all.xml"));
			String xmlConstraints = IOUtils
					.toString(ProfileControllerIntegrationTest.class
							.getResourceAsStream("/vxu2/Constraints.xml"));
			Profile p = new ProfileSerializationImpl().deserializeXMLToProfile(
					xmlContentsProfile, xmlValueSet, xmlConstraints);
			p.getMetaData().setName("Test VXU V04 Implementation Guide");
			p.getMetaData().setIdentifier("Test IG_VXU_V04");
			p.getMetaData().setOrgName("Test NIST");
			p.getMetaData().setSubTitle("Test NIST");
			p.getMetaData().setVersion("1.0");
			p.getMetaData().setDate("April 16th 2015");
			p.setScope(ProfileScope.PRELOADED);

			Profile clone = p.clone();
			return clone;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
