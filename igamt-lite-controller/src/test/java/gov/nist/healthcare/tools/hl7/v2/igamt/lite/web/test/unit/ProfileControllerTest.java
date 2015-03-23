package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.unit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.HL7Version;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SchemaVersion;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.repo.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.xml.ProfileSerializationImpl;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller.ProfileController;

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
		mockMvc.perform(get("/profiles/preloaded")).andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	public void testGetProfile() throws Exception {
		Profile custom = findOneFull();
		when(mockProfileService.findOne(new Long(3))).thenReturn(custom);
		mockMvc.perform(get("/profiles/3")).andExpect(status().isOk())
				.andDo(print());
	}

	private List<Profile> findAllPreloaded() {
		List<Profile> profiles = new ArrayList<Profile>();
		Profile p = new Profile();
		ProfileMetaData metaData = new ProfileMetaData();
		p.setMetaData(metaData);
		p.setId(new Long(1));
		metaData.setHl7Version(HL7Version.V2_0.value());
		metaData.setSchemaVersion(SchemaVersion.V1_0.value());
		ProfileMetaData m = new ProfileMetaData();
		m.setName("P1");
		m.setOrgName("NIST");
		m.setStatus("Completed");
		p.setMetaData(m);
		profiles.add(p);

		p = new Profile();
		metaData = new ProfileMetaData();
		p.setMetaData(metaData);
		p.setId(new Long(2));
		metaData.setHl7Version(HL7Version.V2_0.value());
		metaData.setSchemaVersion(SchemaVersion.V1_0.value());
		m = new ProfileMetaData();
		m.setName("P2");
		m.setOrgName("NIST");
		m.setStatus("Draft");
		p.setMetaData(m);
		profiles.add(p);
		return profiles;
	}

	private Profile findOneFull() {
		String xmlContentsProfile;
		try {
			xmlContentsProfile = IOUtils.toString(ProfileController.class
					.getResourceAsStream("/Profile.xml"));
			String xmlValueSet = IOUtils.toString(ProfileController.class
					.getResourceAsStream("/ValueSets_all.xml"));
			String xmlConstraints = IOUtils.toString(ProfileController.class
					.getResourceAsStream("/Constraints.xml"));
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
