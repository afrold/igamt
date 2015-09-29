package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.unit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.impl.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.ProfileController;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.integration.ProfileControllerIntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

public class ProfileControllerTest {

	@InjectMocks
	ProfileController controller;

	@Mock
	ProfileService mockProfileService;

	MockMvc mockMvc;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setProfileService(mockProfileService);
		mockMvc = standaloneSetup(controller).build();
	}

	@Test
	public void testGetAllPreloaded() throws Exception {
		List<Profile> preloaded = findAllPreloaded();
		when(mockProfileService.findAllPreloaded()).thenReturn(preloaded);
		mockMvc.perform(get("/profiles")).andExpect(status().isOk());
	}
	
	// @Test
	// public void testGetProfile() throws Exception {
	// Profile custom = findOneFull();
	// when(mockProfileService.findOne("3")).thenReturn(custom);
	// mockMvc.perform(get("/profiles/3")).andExpect(status().isOk())
	// .andDo(print());
	// }

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
