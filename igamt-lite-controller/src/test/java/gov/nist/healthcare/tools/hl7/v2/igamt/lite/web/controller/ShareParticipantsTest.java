package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class ShareParticipantsTest {
	
	@InjectMocks
	ShareParticipantsController ctrl;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() throws Exception {
		// Process mock annotations
        MockitoAnnotations.initMocks(this);
 
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(ctrl).build();
	}

	@Test
	public void testParticipantList() throws Exception {
		this.mockMvc.perform(get("/api/usernames"));
		assertTrue(true);
	}

}
