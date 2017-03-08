package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.compositeprofile.test;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class compositeProfileControllerTest {

  @Configuration
  @ComponentScan(basePackages = "gov.nist.healthcare.tools.hl7.v2.igamt.lite")
  static class ContextConfiguration {

    @Bean
    public MessageService messageService() {
      return Mockito.mock(MessageService.class);
    }


  }

  @Autowired
  private MessageService messageService;

  // @Mock
  // private CompositeProfileService compositeProfileService;

  @Before
  public void prepare() {

    Message core = new Message();
    core.setName("TEST CORE");
    Segment seg1 = new Segment();
    seg1.setId(ObjectId.get().toString());
    seg1.setName("MSH");

    //
    Field f1 = new Field();
    f1.setName("First Field");
    f1.setUsage(Usage.R);
    List<Field> fields = new ArrayList<>();
    fields.add(f1);

    seg1.setFields(fields);

    //
    SegmentRef segref1 = new SegmentRef();
    SegmentLink segLink1 = new SegmentLink();
    segLink1.setId(seg1.getId());
    segLink1.setName(seg1.getName());
    segref1.setRef(segLink1);
    segref1.setUsage(Usage.R);

    //
    List<SegmentRefOrGroup> segRefOrGrp1 = new ArrayList<>();
    segRefOrGrp1.add(segref1);
    core.setChildren(segRefOrGrp1);


    Mockito.when(messageService.findById("1")).thenReturn(core);
    // Mockito.doReturn(core).when(messageService.findById("1"));


  }


  @Test
  public void testBuildCompositeProfile() {

    Message core = messageService.findById("1");
    Assert.isTrue(core.getName().equals("TEST CORE"), "VERY GOOD");
  }
}
