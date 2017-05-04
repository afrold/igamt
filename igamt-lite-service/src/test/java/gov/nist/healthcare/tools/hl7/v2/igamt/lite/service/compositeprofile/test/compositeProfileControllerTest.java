package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.compositeprofile.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
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

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ApplyInfo;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponentAttributes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.CompositeProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.MessageService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;

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

    @Bean
    public ProfileComponentService profileComponentService() {
      return Mockito.mock(ProfileComponentService.class);
    }

    @Bean
    public SegmentService segmentService() {
      return Mockito.mock(SegmentService.class);
    }

    @Bean
    public DatatypeService datatypeService() {
      return Mockito.mock(DatatypeService.class);
    }


  }

  @Autowired
  private MessageService messageService;
  @Autowired
  private SegmentService segmentService;
  @Autowired
  private DatatypeService datatypeService;
  @Autowired
  private ProfileComponentService profileComponentService;

  public Message createMessage(String name, String id, List<SegmentRefOrGroup> children) {
    Message core = new Message();
    core.setName(name);
    core.setId(id);
    core.setChildren(children);
    return core;

  }

  public Segment createSegment(String name, String id, List<Field> fields) {
    Segment seg = new Segment();
    seg.setId(id);
    seg.setName(name);
    seg.setExt(null);
    seg.setFields(fields);
    return seg;
  }

  public Datatype createDatatype(String name, String id, List<Component> components) {
    Datatype dt = new Datatype();
    dt.setId(id);
    dt.setName(name);
    dt.setComponents(components);
    return dt;
  }

  public Component createComponent(String name, String id, Usage usage, Datatype dt) {
    Component c = new Component();
    c.setName(name);
    c.setId(id);
    c.setUsage(usage);
    DatatypeLink dtLink = new DatatypeLink();
    dtLink.setId(dt.getId());
    dtLink.setName(dt.getName());
    c.setDatatype(dtLink);
    return c;
  }

  public Field createField(String name, String id, Usage usage, Integer min, String max,
      String minLength, String maxLength, Datatype dt) {
    Field f = new Field();
    f.setName(name);
    f.setId(id);
    f.setUsage(usage);
    f.setMin(min);
    f.setMax(max);
    f.setMinLength(minLength);
    f.setMaxLength(maxLength);

    DatatypeLink dtLink = new DatatypeLink();
    dtLink.setId(dt.getId());
    dtLink.setName(dt.getName());
    f.setDatatype(dtLink);
    return f;
  }

  public SegmentRef createSegRef(String id, Segment seg, Usage usage, Integer min, String max) {
    SegmentRef segRef = new SegmentRef();
    segRef.setId(id);
    SegmentLink segLink = new SegmentLink();
    segLink.setExt(seg.getExt());
    segLink.setId(seg.getId());
    segLink.setName(seg.getName());
    segRef.setRef(segLink);
    segRef.setUsage(usage);
    segRef.setMin(min);
    segRef.setMax(max);
    return segRef;
  }

  public ProfileComponent createProfileComponent(String name, String id,
      List<SubProfileComponent> subPc) {
    ProfileComponent pc = new ProfileComponent();
    pc.setName(name);
    pc.setId(id);
    pc.setChildren(subPc);
    return pc;
  }

  public SubProfileComponentAttributes createPcAttributesForSegmentRef(Usage usage, Integer min,
      String max) {
    SubProfileComponentAttributes subAttr = new SubProfileComponentAttributes();
    subAttr.setUsage(usage);
    subAttr.setMin(min);
    subAttr.setMax(max);
    return subAttr;
  }

  public SubProfileComponentAttributes createPcAttributesForField(Usage usage, Integer min,
      String max, String minLength, String maxLength) {
    SubProfileComponentAttributes subAttr = new SubProfileComponentAttributes();
    subAttr.setUsage(usage);
    subAttr.setMin(min);
    subAttr.setMax(max);
    subAttr.setMinLength(minLength);
    subAttr.setMaxLength(maxLength);
    return subAttr;
  }

  public SubProfileComponent createSubProfileComponent(String itemId,
      SubProfileComponentAttributes attributes) {
    SubProfileComponent subPc = new SubProfileComponent();
    subPc.setAttributes(attributes);
    return subPc;
  }

  public ApplyInfo createApplyInfo(Integer position, ProfileComponent pc) {
    ApplyInfo a = new ApplyInfo();
    a.setPosition(position);
    a.setId(pc.getId());
    return a;
  }

  public CompositeProfileStructure createCompositeProfileStructure(String id, String name,
      String desc, String coreProfileId, List<ApplyInfo> applyInfos) {
    CompositeProfileStructure compStruct = new CompositeProfileStructure();
    compStruct.setId(id);
    compStruct.setName(name);
    compStruct.setDescription(desc);
    compStruct.setCoreProfileId(coreProfileId);
    compStruct.setProfileComponentsInfo(applyInfos);
    return compStruct;
  }

  @Before
  public void prepare() {
    List<SegmentRefOrGroup> children = new ArrayList<>();

    List<Component> components = new ArrayList<>();
    Datatype dt2 = createDatatype("dt2", "dt2", null);

    components.add(createComponent("component1", "component1", Usage.R, dt2));
    Datatype dt1 = createDatatype("dt1", "dt1", components);
    List<Datatype> dts = new ArrayList<Datatype>();
    Set<String> dtIds = new HashSet<>();
    dtIds.add(dt1.getId());
    dts.add(dt1);
    dtIds.add(dt2.getId());
    dts.add(dt2);
    List<Field> fields = new ArrayList<>();
    fields.add(createField("field1", "field1", Usage.R, 0, "0", "0", "0", dt1));
    Segment seg1 = createSegment("seg1", "seg1", fields);
    List<Segment> segs = new ArrayList<Segment>();
    Set<String> segIds = new HashSet<>();
    segIds.add(seg1.getId());
    segs.add(seg1);
    SegmentRef segref1 = createSegRef("segRef1", seg1, Usage.R, 1, "1");
    children.add(segref1);
    // CORE MESSAGE
    Message core = createMessage("core1", "core1", children);

    SubProfileComponent subPc1 =
        createSubProfileComponent("field1", createPcAttributesForField(Usage.RE, 1, "2", "1", "2"));

    SubProfileComponent subPc2 =
        createSubProfileComponent("segRef1", createPcAttributesForSegmentRef(Usage.RE, 1, "2"));
    List<SubProfileComponent> subPcs = new ArrayList<>();
    subPcs.add(subPc1);
    subPcs.add(subPc2);
    ProfileComponent profileComponent = createProfileComponent("pc1", "pc1", subPcs);

    List<ProfileComponent> pcs = new ArrayList<ProfileComponent>();
    List<String> pcIds = new ArrayList<>();


    pcIds.add(profileComponent.getId());
    pcs.add(profileComponent);



    List<ApplyInfo> applyInfos = new ArrayList<ApplyInfo>();
    ApplyInfo applyInfo = createApplyInfo(1, profileComponent);
    applyInfos.add(applyInfo);


    compositeProfileStructure =
        createCompositeProfileStructure("comp1", "comp1", "desc", core.getId(), applyInfos);


    Mockito.when(messageService.findById(core.getId())).thenReturn(core);
    Mockito.when(profileComponentService.findByIds(pcIds)).thenReturn(pcs);

    Mockito.when(segmentService.findByIds(segIds)).thenReturn(segs);
    Mockito.when(datatypeService.findByIds(dtIds)).thenReturn(dts);

    // Mockito.doReturn(core).when(messageService.findById("1"));


  }

  CompositeProfileStructure compositeProfileStructure;

  @Autowired
  CompositeProfileService compositeProfileService;


  @Test
  public void testBuildCompositeProfile() {
    CompositeProfile comp =
        compositeProfileService.buildCompositeProfile(compositeProfileStructure);
    System.out.println(comp.toString());
    Map<String, SubProfileComponentAttributes> itemsMap = new HashMap<>();
    for (ProfileComponent pc : profileComponentService
        .findByIds(compositeProfileStructure.getProfileComponentIds())) {
      for (SubProfileComponent subPc : pc.getChildren()) {
      }
    }
    for (SegmentRefOrGroup segRefOrGrp : comp.getChildren()) {
      if (itemsMap.containsKey(segRefOrGrp.getId())) {

        Assert.assertTrue("SegmentRef or Group Usage incorrect",
            segRefOrGrp.getUsage().equals(itemsMap.get(segRefOrGrp.getId()).getUsage()));
        Assert.assertTrue("SegmentRef or Group Min Card incorrect",
            segRefOrGrp.getMin().equals(itemsMap.get(segRefOrGrp.getId()).getMin()));
        Assert.assertTrue("SegmentRef or Group Max Card incorrect",
            segRefOrGrp.getMax().equals(itemsMap.get(segRefOrGrp.getId()).getMax()));
      }
    }
    for (String segId : comp.getSegmentsMap().keySet()) {
      for (Field field : comp.getSegmentsMap().get(segId).getFields()) {
        if (itemsMap.containsKey(field.getId())) {
          Assert.assertTrue("Field Usage incorrect",
              field.getUsage().equals(itemsMap.get(field.getId()).getUsage()));
          Assert.assertTrue("Field Min incorrect",
              field.getMin().equals(itemsMap.get(field.getId()).getMin()));
          Assert.assertTrue("Field Max incorrect",
              field.getMax().equals(itemsMap.get(field.getId()).getMax()));
          Assert.assertTrue("Field Min Length incorrect",
              field.getMinLength().equals(itemsMap.get(field.getId()).getMinLength()));
          Assert.assertTrue("Field Max Length incorrect",
              field.getMaxLength().equals(itemsMap.get(field.getId()).getMaxLength()));
        }
      }
    }


  }
}
