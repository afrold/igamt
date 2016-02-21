package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentScope;
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
import org.springframework.test.web.servlet.MvcResult;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestWebAppConfig.class })
public class ProfileControllerIntegrationTest {

//	@InjectMocks
	@Autowired
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
//		Profile profile = profile();
//		assertNotNull("Profile is null.", profile);
//		profileService.save(profile);
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
			p.getMetaData().setProfileID("Test IG_VXU_V04");
			p.getMetaData().setOrgName("Test NIST");
			p.getMetaData().setSubTitle("Test NIST");
			p.getMetaData().setVersion("1.0");
			p.getMetaData().setDate("April 16th 2015");
			p.setScope(IGDocumentScope.PRELOADED);

			Profile clone = p.clone();
			return clone;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

//	@Test
	public void testApplyMessageChanges() throws Exception {

		Profile profile = profile();
		assertNotNull("Profile is null.", profile);
		profileService.save(profile);

		String jsonChanges = "{\"value\": \n                       {\n  \"datatype\": {\n    \"add\": [\n      {\n        \"type\": \"datatype\",\n        \"id\": -5446415,\n        \"label\": \"HD_IZ_2\",\n        \"components\": [\n          {\n            \"type\": \"component\",\n            \"name\": \"Namespace ID\",\n            \"usage\": \"C\",\n            \"minLength\": 1,\n            \"maxLength\": \"20\",\n            \"confLength\": \"\",\n            \"table\": {\n              \"id\": \"5530963d30040340c9cb4905\"\n            },\n            \"bindingStrength\": \"\",\n            \"bindingLocation\": \"\",\n            \"datatype\": {\n              \"id\": \"5530963d30040340c9cb5179\"\n            },\n            \"position\": 1,\n            \"comment\": null,\n            \"id\": -2193717,\n            \"path\": \"MSH.3.1\"\n          },\n          {\n            \"type\": \"component\",\n            \"name\": \"Universal ID\",\n            \"usage\": \"C\",\n            \"minLength\": 1,\n            \"maxLength\": \"199\",\n            \"confLength\": \"\",\n            \"table\": null,\n            \"bindingStrength\": \"\",\n            \"bindingLocation\": \"\",\n            \"datatype\": {\n              \"id\": \"5530963d30040340c9cb517b\"\n            },\n            \"position\": 2,\n            \"comment\": null,\n            \"id\": -752404,\n            \"path\": \"MSH.3.2\"\n          },\n          {\n            \"type\": \"component\",\n            \"name\": \"Universal ID Type\",\n            \"usage\": \"C\",\n            \"minLength\": 1,\n            \"maxLength\": \"6\",\n            \"confLength\": \"\",\n            \"table\": {\n              \"id\": \"5530963d30040340c9cb48b0\"\n            },\n            \"bindingStrength\": \"\",\n            \"bindingLocation\": \"\",\n            \"datatype\": {\n              \"id\": \"5530963d30040340c9cb5180\"\n            },\n            \"position\": 3,\n            \"comment\": null,\n            \"id\": -6958197,\n            \"path\": \"MSH.3.3\"\n          }\n        ],\n        \"name\": \"HD\",\n        \"description\": \"Hierarchic Designator\",\n        \"predicates\": [\n          {\n            \"id\": -3546464,\n            \"constraintId\": \"[HD_IZ]1[1]\",\n            \"constraintTarget\": \"1[1]\",\n            \"reference\": null,\n            \"description\": \"If HD.2 (Universal ID) is not valued.\",\n            \"assertion\": \"<Condition>\\n                        <NOT>\\n                            <Presence Path=\\\"2[1]\\\"/>\\n                        </NOT>\\n                    </Condition>\",\n            \"trueUsage\": \"R\",\n            \"falseUsage\": \"O\"\n          },\n          {\n            \"id\": -9133599,\n            \"constraintId\": \"[HD_IZ]2[1]\",\n            \"constraintTarget\": \"2[1]\",\n            \"reference\": null,\n            \"description\": \"If HD.1 (Namespace ID) is not valued.\",\n            \"assertion\": \"<Condition>\\n                        <NOT>\\n                            <Presence Path=\\\"1[1]\\\"/>\\n                        </NOT>\\n                    </Condition>\",\n            \"trueUsage\": \"R\",\n            \"falseUsage\": \"O\"\n          },\n          {\n            \"id\": -9986875,\n            \"constraintId\": \"[HD_IZ]3[1]\",\n            \"constraintTarget\": \"3[1]\",\n            \"reference\": null,\n            \"description\": \"If HD.2 (Universal ID) is valued.\",\n            \"assertion\": \"<Condition>\\n                        <Presence Path=\\\"2[1]\\\"/>\\n                    </Condition>\",\n            \"trueUsage\": \"R\",\n            \"falseUsage\": \"X\"\n          }\n        ],\n        \"conformanceStatements\": [\n          {\n            \"id\": -738102,\n            \"constraintId\": \"IZ-5\",\n            \"constraintTarget\": \"2[1]\",\n            \"reference\": null,\n            \"description\": \"The value of HD.2 (Universal ID) SHALL be formatted with ISO-compliant OID.\",\n            \"assertion\": \"<Assertion>\\n                        <Format Path=\\\"2[1]\\\" Regex=\\\"[0-2](\\\\.(0|[1-9][0-9]*))*\\\"/>\\n                    </Assertion>\"\n          },\n          {\n            \"id\": -9156520,\n            \"constraintId\": \"IZ-6\",\n            \"constraintTarget\": \"3[1]\",\n            \"reference\": null,\n            \"description\": \"The value of HD.3 (Universal ID Type) SHALL be 'ISO'.\",\n            \"assertion\": \"<Assertion>\\n                        <PlainText IgnoreCase=\\\"false\\\" Path=\\\"3[1]\\\" Text=\\\"ISO\\\"/>\\n                    </Assertion>\"\n          }\n        ],\n        \"comment\": null,\n        \"usageNote\": null\n      }\n    ]\n  },\n  \"field\": {\n    \"edit\": [\n      {\n        \"id\": \"5530963d30040340c9cb5315\",\n        \"datatype\": {\n          \"id\": -5446415\n        }\n      }\n    ]\n  }\n}\n}";
		MvcResult result = mockMvc
				.perform(
						post("/profiles/553101bfd728d1a1382f9c2c/save")
								.content("{value:''}"))
				.andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();

		System.out.println("DONE");
	}
}
