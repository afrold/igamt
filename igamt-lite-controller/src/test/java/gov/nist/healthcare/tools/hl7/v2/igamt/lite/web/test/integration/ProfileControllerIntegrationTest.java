package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestWebAppConfig.class })
// @TransactionConfiguration
// @Transactional(readOnly = false)
public class ProfileControllerIntegrationTest {

	@InjectMocks
	ProfileController controller;

	@Autowired
	ProfileService profileService;

	MockMvc mockMvc;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	@Transactional
	@Rollback(false)
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
		mockMvc.perform(get("/profiles/preloaded")).andExpect(status().isOk())
				.andDo(print());

	}

	// @Test
	public void testGetProfile() throws Exception {
		Profile profile = profile();
		assertNotNull("Profile is null.", profile);
		profileService.save(profile);
		assertNotNull("Profile is not saved.", profile.getId());
		String pId = profile.getId();
		mockMvc.perform(get("/profiles/" + pId)).andExpect(status().isOk());
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

			p.setPreloaded(true);
			return p;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
