/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified. Abdelghani EL OUAKILI (NIST) Jun 6, 2017
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.service.UserService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.CompositeProfileStructure;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileComponentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLibrary;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SubProfileComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.VariesMapItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintsTable;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.DatatypeCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.MessageCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.ProfileComponentCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.SegmentCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.ValueSetCrossReference;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.CoConstraintFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.ComponentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.CompositeProfileFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypePredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DatatypeValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.DynamicMappingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.FieldFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessagePredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.MessageValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.ProfileComponentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentConformanceStatmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentPredicateFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.crossreference.found.SegmentValueSetBindingFound;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeLibraryService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportConfigService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontConfigService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ExportFontService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.IGDocumentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileComponentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.ProfileService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.DatatypeCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.MessageCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.ProfileComponentCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.SegmentCrossRefWrapper;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.service.wrappers.TableCrossRefWrapper;

/**
 * @author Abdelghani EL Ouakili (NIST)
 *
 */
@RestController
@RequestMapping("/crossRefs")

public class CrossReferencesController {
  @Autowired
  private IGDocumentService igDocumentService;
  @Autowired
  UserService userService;
  @Autowired
  ProfileService profileService;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  ExportConfigService exportConfigService;
  @Autowired
  ExportFontConfigService exportFontConfigService;
  @Autowired
  ExportFontService exportFontService;
  @Autowired
  private DatatypeService datatypeService;
  @Autowired
  private ProfileComponentService profileComponentService;
  @Autowired
  private SegmentService segmentService;
  @Autowired
  private TableService tableService;
  @Autowired
  private DatatypeLibraryService datatypeLibrayService;

  @RequestMapping(value = "/profilecomponent", method = RequestMethod.POST,
      produces = "application/json")
  public ProfileComponentCrossReference findProfileComponentReferences(
      @RequestBody ProfileComponentCrossRefWrapper wrapper) throws Exception {
    List<CompositeProfileFound> compositeProfileFounds = new ArrayList<CompositeProfileFound>();
    ProfileComponentCrossReference ret = new ProfileComponentCrossReference();
    IGDocument ig = igDocumentService.findById(wrapper.getIgDocumentId());
    Set<CompositeProfileStructure> compositeProfileStructures =
        ig.getProfile().getCompositeProfiles().getChildren();
    for (CompositeProfileStructure cps : compositeProfileStructures) {
      if (cps.getProfileComponentIds().contains(wrapper.getProfileComponentId())) {
        CompositeProfileFound cpFound = new CompositeProfileFound();
        cpFound.setId(cps.getId());
        cpFound.setName(cps.getName());
        cpFound.setDescription(cps.getDescription());
        compositeProfileFounds.add(cpFound);
      }
    }
    ret.setCompositeProfileFound(compositeProfileFounds);
    ret.setEmpty();
    return ret;
  }

  @RequestMapping(value = "/message", method = RequestMethod.POST, produces = "application/json")
  public MessageCrossReference findMessageReferences(@RequestBody MessageCrossRefWrapper wrapper)
      throws Exception {
    List<ProfileComponentFound> profileComponentFounds = new ArrayList<ProfileComponentFound>();
    List<CompositeProfileFound> compositeProfileFounds = new ArrayList<CompositeProfileFound>();
    MessageCrossReference ret = new MessageCrossReference();
    IGDocument ig = igDocumentService.findById(wrapper.getIgDocumentId());
    Set<CompositeProfileStructure> compositeProfileStructures =
        ig.getProfile().getCompositeProfiles().getChildren();
    for (CompositeProfileStructure cps : compositeProfileStructures) {
      if (wrapper.getMessageId().equals(cps.getCoreProfileId())) {
        CompositeProfileFound cpFound = new CompositeProfileFound();
        cpFound.setId(cps.getId());
        cpFound.setName(cps.getName());
        cpFound.setDescription(cps.getDescription());
        compositeProfileFounds.add(cpFound);
      }
    }
    for (ProfileComponentLink link : ig.getProfile().getProfileComponentLibrary().getChildren()) {
      ProfileComponent pc = profileComponentService.findById(link.getId());
      for (SubProfileComponent spc : pc.getChildren()) {
        if (spc.getType().equals("message")) {
          if (spc.getSource() != null && spc.getSource().getMessageId() != null) {
            if (spc.getSource().getMessageId().equals(wrapper.getMessageId())) {
              ProfileComponentFound pcf = new ProfileComponentFound();
              pcf.setDescription(pc.getDescription());
              pcf.setId(pc.getId());
              pcf.setName(pc.getName());
              pcf.setTargetPosition(spc.getPosition());
              pcf.setWhere(spc.getType());
              profileComponentFounds.add(pcf);
            }
          }
        } else {
          if (spc.getSource() != null && spc.getSource().getMessageId() != null) {
            if (spc.getSource().getMessageId().equals(wrapper.getMessageId())) {
              ProfileComponentFound pcf = new ProfileComponentFound();
              pcf.setDescription(pc.getDescription());
              pcf.setId(pc.getId());
              pcf.setName(pc.getName());
              pcf.setTargetPosition(spc.getPosition());
              pcf.setWhere(spc.getType());
              profileComponentFounds.add(pcf);
            }
          }
        }
      }
    }
    ret.setCompositeProfileFound(compositeProfileFounds);
    ret.setProfileComponentFound(profileComponentFounds);
    ret.setEmpty();
    return ret;
  }

  @RequestMapping(value = "/segment", method = RequestMethod.POST, produces = "application/json")
  public SegmentCrossReference findSegmentReferences(@RequestBody SegmentCrossRefWrapper wrapper)
      throws Exception {
    List<MessageFound> messageFounds = new ArrayList<MessageFound>();
    List<ProfileComponentFound> profileComponentFounds = new ArrayList<ProfileComponentFound>();
    SegmentCrossReference ret = new SegmentCrossReference();
    IGDocument ig = igDocumentService.findById(wrapper.getIgDocumentId());
    Set<Message> messages = ig.getProfile().getMessages().getChildren();
    for (Message m : messages) {
      findSegmentInsideMessage(wrapper.getSegmentId(), m, messageFounds);
    }
    for (ProfileComponentLink link : ig.getProfile().getProfileComponentLibrary().getChildren()) {
      ProfileComponent pc = profileComponentService.findById(link.getId());
      for (SubProfileComponent spc : pc.getChildren()) {

        if (spc.getType().equals("segment") || spc.getType().equals("segmentRef")) {
          if (spc.getAttributes().getOldRef().getId().equals(wrapper.getSegmentId())) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("oldRef of " + spc.getType());
            profileComponentFounds.add(pcf);
          } else if (spc.getAttributes().getRef().getId().equals(wrapper.getSegmentId())) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("newRef of " + spc.getType());
            profileComponentFounds.add(pcf);
          }
        } else {
          if (spc.getSource() != null && spc.getSource().getSegmentId() != null) {
            if (spc.getSource().getSegmentId().equals(wrapper.getSegmentId())) {
              ProfileComponentFound pcf = new ProfileComponentFound();
              pcf.setDescription(pc.getDescription());
              pcf.setId(pc.getId());
              pcf.setName(pc.getName());
              pcf.setTargetPosition(spc.getPosition());
              pcf.setWhere("From " + spc.getFrom() + ", Type: " + spc.getType());
              profileComponentFounds.add(pcf);
            }
          }
        }
      }
    }
    ret.setMessageFounds(messageFounds);
    ret.setProfileComponentFound(profileComponentFounds);
    ret.setEmpty();
    return ret;
  }

  /**
   * @param segmentId
   * @param m
   * @param founds
   */
  private void findSegmentInsideMessage(String segmentId, Message m, List<MessageFound> founds) {
    // TODO Auto-generated method stub
    for (int i = 0; i < m.getChildren().size(); i++) {
      findReferenceInsideGroup(segmentId, m.getChildren().get(i), i, founds, null, null, m);
    }
  }


  /**
   * @param segmentId
   * @param segmentRefOrGroup
   * @param i
   * @param founds
   */
  private void findReferenceInsideGroup(String segmentId, SegmentRefOrGroup segmentRefOrGroup,
      int i, List<MessageFound> founds, String path, String positionPath, Message m) {

    if (segmentRefOrGroup instanceof SegmentRef) {
      Segment s = segmentService.findById(((SegmentRef) segmentRefOrGroup).getRef().getId());
      if (s.getId().equals(segmentId)) {
        MessageFound found = new MessageFound();
        if (path == null) {
          found.setPath(s.getLabel());
          found.setPositionPath("" + segmentRefOrGroup.getPosition());
        } else {
          found.setPath(path + "." + s.getLabel());
          found.setPositionPath(positionPath + "." + segmentRefOrGroup.getPosition());
        }
        found.setDescription(m.getDescription());
        found.setIdentifier(m.getIdentifier());
        found.setName(m.getName());
        found.setId(m.getId());
        founds.add(found);
      }

    } else if (segmentRefOrGroup instanceof Group) {
      List<SegmentRefOrGroup> children = ((Group) segmentRefOrGroup).getChildren();
      for (int j = 0; j < children.size(); j++) {
        if (path == null) {
          findReferenceInsideGroup(segmentId, children.get(j), j, founds,
              ((Group) segmentRefOrGroup).getName(), "" + segmentRefOrGroup.getPosition(), m);
        } else {
          findReferenceInsideGroup(segmentId, children.get(j), j, founds,
              path + "." + ((Group) segmentRefOrGroup).getName(),
              positionPath + "." + segmentRefOrGroup.getPosition(), m);
        }
      }
    }
  }

  @RequestMapping(value = "/datatype", method = RequestMethod.POST, produces = "application/json")
  public DatatypeCrossReference findDatatypeCrossReference(
      @RequestBody DatatypeCrossRefWrapper wrapper) throws Exception {
    List<FieldFound> fieldFounds = new ArrayList<FieldFound>();
    List<ComponentFound> componentFounds = new ArrayList<ComponentFound>();
    List<DynamicMappingFound> dynamicMappingFounds = new ArrayList<DynamicMappingFound>();;
    List<CoConstraintFound> coConstraintFounds = new ArrayList<CoConstraintFound>();
    List<ProfileComponentFound> profileComponentFounds = new ArrayList<ProfileComponentFound>();

    IGDocument ig = igDocumentService.findById(wrapper.getIgDocumentId());
    Set<String> segmentIds = new HashSet<String>();
    SegmentLibrary lib = ig.getProfile().getSegmentLibrary();
    for (SegmentLink link : lib.getChildren()) {
      segmentIds.add(link.getId());
    }
    List<Segment> allSegments = segmentService.findByIds(segmentIds);

    for (Segment s : allSegments) {
      for (Field f : s.getFields()) {
        if (f.getDatatype().getId().equals(wrapper.getDatatypeId())) {
          FieldFound found = new FieldFound();
          found.setName(f.getName());
          found.setId(f.getId());
          found.setPosition(f.getPosition());
          found.setUsage(f.getUsage());
          SegmentFound seg = new SegmentFound();
          seg.setDescription(s.getDescription());
          seg.setId(s.getId());
          seg.setExt(s.getExt());
          seg.setLabel(s.getLabel());
          seg.setName(s.getName());
          found.setSegmentFound(seg);
          fieldFounds.add(found);
        }
      }

      if (s.getDynamicMappingDefinition() != null
          && !s.getDynamicMappingDefinition().getDynamicMappingItems().isEmpty()) {
        DynamicMappingDefinition df = s.getDynamicMappingDefinition();
        
        for (DynamicMappingItem item : df.getDynamicMappingItems()) {
          if (item.getDatatypeId()!=null&&item.getDatatypeId().equals(wrapper.getDatatypeId())) {
            DynamicMappingFound found = new DynamicMappingFound();
            SegmentFound segFound = new SegmentFound();
            segFound.setDescription(s.getDescription());
            segFound.setExt(s.getExt());
            segFound.setId(s.getId());
            segFound.setLabel(s.getLabel());
            segFound.setName(s.getName());
            found.setDynamicMappingItem(item);
            found.setSegmentFound(segFound);
            VariesMapItem mapStructure = df.getMappingStructure();
            found.setMappingStructure(mapStructure);
            dynamicMappingFounds.add(found);
          }
        }
      }
      if (s.getCoConstraintsTable() != null) {
        CoConstraintsTable coconstraints = s.getCoConstraintsTable();
        if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
            && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
          CoConstraintFound found = new CoConstraintFound();
          for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
            if (coconstraints.getThenMapData().get(thn.getId()) != null
                && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
              for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {
                if (coconstraints.getThenMapData().get(thn.getId()).get(i) != null
                    && coconstraints.getThenMapData().get(thn.getId()).get(i)
                        .getDatatypeId() != null
                    && coconstraints.getThenMapData().get(thn.getId()).get(i).getDatatypeId()
                        .equals(wrapper.getDatatypeId())) {
                  SegmentFound segFound = new SegmentFound();
                  segFound.setDescription(s.getDescription());
                  segFound.setExt(s.getExt());
                  segFound.setId(s.getId());
                  segFound.setLabel(s.getLabel());
                  segFound.setName(s.getName());
                  found.setSegmentFound(segFound);
                  found.setIfDefinition(coconstraints.getIfColumnDefinition());
                  found.setThenDefinition(thn);
                  if (!coconstraints.getIfColumnData().isEmpty())
                    found.setIfData(coconstraints.getIfColumnData().get(0));
                  found.setThenData(coconstraints.getThenMapData().get(thn.getId()).get(i));
                  coConstraintFounds.add(found);
                }
              }
            }
          }
        }
      }
    }
    Set<String> datatypeIds = new HashSet<String>();
    for (DatatypeLink link : ig.getProfile().getDatatypeLibrary().getChildren()) {
      datatypeIds.add(link.getId());
    }
    List<Datatype> allDatatypes = datatypeService.findByIds(datatypeIds);
    for (Datatype d : allDatatypes) {
      for (Component c : d.getComponents()) {
        if (c.getDatatype().getId().equals(wrapper.getDatatypeId())) {
          ComponentFound found = new ComponentFound();
          found.setName(c.getName());
          found.setId(c.getId());
          found.setPosition(c.getPosition());
          found.setUsage(c.getUsage());
          DatatypeFound dt = new DatatypeFound();
          dt.setDescription(dt.getDescription());
          dt.setId(d.getId());
          dt.setExt(d.getExt());
          dt.setLabel(d.getLabel());
          dt.setName(d.getName());
          found.setDatatypeFound(dt);
          componentFounds.add(found);
        }
      }
    }
    for (ProfileComponentLink link : ig.getProfile().getProfileComponentLibrary().getChildren()) {
      ProfileComponent pc = profileComponentService.findById(link.getId());
      for (SubProfileComponent spc : pc.getChildren()) {
        if (spc.getSource() != null
            && (spc.getSource().getFieldDt() != null || spc.getSource().getComponentDt() != null)) {
          if (spc.getSource().getFieldDt() != null
              && spc.getSource().getFieldDt().equals(wrapper.getDatatypeId())
              || spc.getSource().getComponentDt() != null
                  && spc.getSource().getComponentDt().equals(wrapper.getDatatypeId())) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("From " + spc.getFrom() + ", Type: " + spc.getType());
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getOldDatatype() != null
            && spc.getAttributes().getOldDatatype().getId().equals(wrapper.getDatatypeId())) {
          ProfileComponentFound pcf = new ProfileComponentFound();
          pcf.setDescription(pc.getDescription());
          pcf.setId(pc.getId());
          pcf.setName(pc.getName());
          pcf.setTargetPosition(spc.getPosition());
          pcf.setWhere(
              "From " + spc.getFrom() + ", Type: " + spc.getType() + " is using as old DT.");
          profileComponentFounds.add(pcf);
        }

        if (spc.getAttributes().getDatatype() != null
            && spc.getAttributes().getDatatype().getId().equals(wrapper.getDatatypeId())) {
          ProfileComponentFound pcf = new ProfileComponentFound();
          pcf.setDescription(pc.getDescription());
          pcf.setId(pc.getId());
          pcf.setName(pc.getName());
          pcf.setTargetPosition(spc.getPosition());
          pcf.setWhere(
              "From " + spc.getFrom() + ", Type: " + spc.getType() + " is using as new DT.");
          profileComponentFounds.add(pcf);
        }

        if (spc.getAttributes().getDynamicMappingDefinition() != null && !spc.getAttributes()
            .getDynamicMappingDefinition().getDynamicMappingItems().isEmpty()) {
          DynamicMappingDefinition df = spc.getAttributes().getDynamicMappingDefinition();
          boolean isFound = false;
          for (DynamicMappingItem item : df.getDynamicMappingItems()) {
            if (item.getDatatypeId().equals(wrapper.getDatatypeId())) {
              isFound = true;
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent new Dynamic Mapping is using this datatype.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getOldDynamicMappingDefinition() != null && !spc.getAttributes()
            .getOldDynamicMappingDefinition().getDynamicMappingItems().isEmpty()) {
          DynamicMappingDefinition df = spc.getAttributes().getOldDynamicMappingDefinition();
          boolean isFound = false;
          for (DynamicMappingItem item : df.getDynamicMappingItems()) {
            if (item.getDatatypeId().equals(wrapper.getDatatypeId())) {
              isFound = true;
            }
          }

          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent old Dynamic Mapping is using this datatype.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getCoConstraintsTable() != null) {
          CoConstraintsTable coconstraints = spc.getAttributes().getCoConstraintsTable();
          boolean isFound = false;
          if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
              && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
            for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
              if (coconstraints.getThenMapData().get(thn.getId()) != null
                  && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
                for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {
                  if (coconstraints.getThenMapData().get(thn.getId()).get(i) != null
                      && coconstraints.getThenMapData().get(thn.getId()).get(i)
                          .getDatatypeId() != null
                      && coconstraints.getThenMapData().get(thn.getId()).get(i).getDatatypeId()
                          .equals(wrapper.getDatatypeId())) {
                    isFound = true;
                  }
                }
              }
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent new CoConstraintTable is using this datatype.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getOldCoConstraintsTable() != null) {
          CoConstraintsTable coconstraints = spc.getAttributes().getOldCoConstraintsTable();
          boolean isFound = false;
          if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
              && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
            for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
              if (coconstraints.getThenMapData().get(thn.getId()) != null
                  && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
                for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {
                  if (coconstraints.getThenMapData().get(thn.getId()).get(i) != null
                      && coconstraints.getThenMapData().get(thn.getId()).get(i)
                          .getDatatypeId() != null
                      && coconstraints.getThenMapData().get(thn.getId()).get(i).getDatatypeId()
                          .equals(wrapper.getDatatypeId())) {
                    isFound = true;
                  }
                }
              }
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent old CoConstraintTable is using this datatype.");
            profileComponentFounds.add(pcf);
          }
        }
      }
    }

    DatatypeCrossReference ret = new DatatypeCrossReference();
    ret.setCoConstraintFounds(coConstraintFounds);
    ret.setComponentFounds(componentFounds);
    ret.setDynamicMappingFounds(dynamicMappingFounds);
    ret.setFieldFounds(fieldFounds);
    ret.setProfileComponentFound(profileComponentFounds);
    ret.setEmpty();
    return ret;
  }

  @RequestMapping(value = "/datatypeInLibrary", method = RequestMethod.POST,
      produces = "application/json")
  public DatatypeCrossReference findDatatypeCrossReferenceInLibrary(
      @RequestBody DatatypeCrossRefWrapper wrapper) throws Exception {
    List<FieldFound> fieldFounds = new ArrayList<FieldFound>();
    List<ComponentFound> componentFounds = new ArrayList<ComponentFound>();
    List<DynamicMappingFound> dynamicMappingFounds = new ArrayList<DynamicMappingFound>();;
    List<CoConstraintFound> coConstraintFounds = new ArrayList<CoConstraintFound>();
    List<ProfileComponentFound> profileComponentFounds = new ArrayList<ProfileComponentFound>();
    DatatypeLibrary dtl = datatypeLibrayService.findById(wrapper.getIgDocumentId());


    Set<String> datatypeIds = new HashSet<String>();
    for (DatatypeLink link : dtl.getChildren()) {
      datatypeIds.add(link.getId());
    }
    List<Datatype> allDatatypes = datatypeService.findByIds(datatypeIds);
    for (Datatype d : allDatatypes) {
      for (Component c : d.getComponents()) {
        if (c.getDatatype().getId().equals(wrapper.getDatatypeId())) {
          ComponentFound found = new ComponentFound();
          found.setName(c.getName());
          found.setId(c.getId());
          found.setPosition(c.getPosition());
          found.setUsage(c.getUsage());
          DatatypeFound dt = new DatatypeFound();
          dt.setDescription(dt.getDescription());
          dt.setId(d.getId());
          dt.setExt(d.getExt());
          dt.setLabel(d.getLabel());
          dt.setName(d.getName());
          found.setDatatypeFound(dt);
          componentFounds.add(found);
        }
      }
    }


    DatatypeCrossReference ret = new DatatypeCrossReference();
    ret.setCoConstraintFounds(coConstraintFounds);
    ret.setComponentFounds(componentFounds);
    ret.setDynamicMappingFounds(dynamicMappingFounds);
    ret.setFieldFounds(fieldFounds);
    ret.setProfileComponentFound(profileComponentFounds);
    ret.setEmpty();
    return ret;
  }

  @RequestMapping(value = "/table", method = RequestMethod.POST, produces = "application/json")
  public ValueSetCrossReference findValueSetsCrossReference(
      @RequestBody TableCrossRefWrapper wrapper) throws Exception {
    List<MessageValueSetBindingFound> messageValueSetBindingfounds =
        new ArrayList<MessageValueSetBindingFound>();
    List<SegmentValueSetBindingFound> segmentValueSetBindingfounds =
        new ArrayList<SegmentValueSetBindingFound>();
    List<DatatypeValueSetBindingFound> datatypeValueSetBindingfounds =
        new ArrayList<DatatypeValueSetBindingFound>();
    List<CoConstraintFound> coConstraintFounds = new ArrayList<CoConstraintFound>();
    List<MessageConformanceStatmentFound> messageConformanceStatmentFounds =
        new ArrayList<MessageConformanceStatmentFound>();
    List<SegmentConformanceStatmentFound> segmentConformanceStatmentFounds =
        new ArrayList<SegmentConformanceStatmentFound>();
    List<DatatypeConformanceStatmentFound> datatypeConformanceStatmentFounds =
        new ArrayList<DatatypeConformanceStatmentFound>();
    List<MessagePredicateFound> messagePredicateFounds = new ArrayList<MessagePredicateFound>();
    List<SegmentPredicateFound> segmentPredicateFounds = new ArrayList<SegmentPredicateFound>();
    List<DatatypePredicateFound> datatypePredicateFounds = new ArrayList<DatatypePredicateFound>();
    List<ProfileComponentFound> profileComponentFounds = new ArrayList<ProfileComponentFound>();
    IGDocument ig = igDocumentService.findById(wrapper.getIgDocumentId());
    Set<Message> messages = ig.getProfile().getMessages().getChildren();
    for (Message m : messages) {
      for (ValueSetOrSingleCodeBinding vs : m.getValueSetBindings()) {
        if (vs.getTableId() != null && vs.getTableId().equals(wrapper.getTableId())) {
          if (vs instanceof ValueSetBinding) {
            ValueSetBinding binding = (ValueSetBinding) vs;
            MessageValueSetBindingFound messageRef = new MessageValueSetBindingFound();
            messageRef.setBinding(binding);
            MessageFound found = new MessageFound();
            found.setDescription(m.getDescription());
            found.setIdentifier(m.getIdentifier());
            found.setName(m.getName());
            found.setId(m.getId());
            messageRef.setMessageFound(found);
            messageValueSetBindingfounds.add(messageRef);
          }
        }
      }
      if (m.getPredicates() != null && !m.getPredicates().isEmpty()) {
        for (Predicate p : m.getPredicates()) {
          if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
            MessagePredicateFound confFound = new MessagePredicateFound();
            MessageFound found = new MessageFound();
            found.setDescription(m.getDescription());
            found.setIdentifier(m.getIdentifier());
            found.setName(m.getName());
            found.setId(m.getId());
            confFound.setMessageFound(found);
            confFound.setPredicate(p);
            messagePredicateFounds.add(confFound);

          }

        }
      }
      if (m.getConformanceStatements() != null && !m.getConformanceStatements().isEmpty()) {
        for (ConformanceStatement p : m.getConformanceStatements()) {

          if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
            MessageConformanceStatmentFound confFound = new MessageConformanceStatmentFound();
            MessageFound found = new MessageFound();
            found.setDescription(m.getDescription());
            found.setIdentifier(m.getIdentifier());
            found.setName(m.getName());
            found.setId(m.getId());
            confFound.setConformanceStatement(p);
            confFound.setMessageFound(found);
            messageConformanceStatmentFounds.add(confFound);

          }

        }
      }
    }

    SegmentLibrary lib = ig.getProfile().getSegmentLibrary();
    Set<String> segmentIds = new HashSet<String>();

    for (SegmentLink link : lib.getChildren()) {
      segmentIds.add(link.getId());

    }

    List<Segment> allSegments = segmentService.findByIds(segmentIds);
    Set<String> datatypeIds = new HashSet<String>();
    for (DatatypeLink link : ig.getProfile().getDatatypeLibrary().getChildren()) {
      datatypeIds.add(link.getId());

    }
    List<Datatype> allDatatypes = datatypeService.findByIds(datatypeIds);



    for (Segment s : allSegments) {

      for (ValueSetOrSingleCodeBinding vs : s.getValueSetBindings()) {
        if (vs.getTableId() != null && vs.getTableId().equals(wrapper.getTableId())) {
          if (vs instanceof ValueSetBinding) {
            ValueSetBinding binding = (ValueSetBinding) vs;
            SegmentValueSetBindingFound segmentRef = new SegmentValueSetBindingFound();
            segmentRef.setBinding(binding);
            SegmentFound segFound = new SegmentFound();
            segFound.setDescription(s.getDescription());
            segFound.setExt(s.getExt());
            segFound.setId(s.getId());
            segFound.setLabel(s.getLabel());
            segFound.setName(s.getName());
            segmentRef.setSegmentFound(segFound);
            segmentValueSetBindingfounds.add(segmentRef);

          }

        }
      }
      if (s.getCoConstraintsTable() != null) {
        CoConstraintsTable coconstraints = s.getCoConstraintsTable();
        if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
            && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
          CoConstraintFound found = new CoConstraintFound();
          for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
            if (coconstraints.getThenMapData().get(thn.getId()) != null
                && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
              for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {

                if (coconstraints.getThenMapData().get(thn.getId()).get(i) != null
                    && coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets() != null
                    && !coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets()
                        .isEmpty()) {
                  for (int j = 0; j < coconstraints.getThenMapData().get(thn.getId()).get(i)
                      .getValueSets().size(); j++) {
                    String tableId = coconstraints.getThenMapData().get(thn.getId()).get(i)
                        .getValueSets().get(j).getTableId();
                    if (tableId != null && tableId.equals(wrapper.getTableId())) {
                      SegmentFound segFound = new SegmentFound();
                      segFound.setDescription(s.getDescription());
                      segFound.setExt(s.getExt());
                      segFound.setId(s.getId());
                      segFound.setLabel(s.getLabel());
                      segFound.setName(s.getName());
                      found.setSegmentFound(segFound);
                      found.setIfDefinition(coconstraints.getIfColumnDefinition());
                      found.setThenDefinition(thn);
                      if (!coconstraints.getIfColumnData().isEmpty())
                        found.setIfData(coconstraints.getIfColumnData().get(0));
                      found.setThenData(coconstraints.getThenMapData().get(thn.getId()).get(i));
                      coConstraintFounds.add(found);
                    }
                  }
                }
              }
            }
          }
        }
      }

      if (s.getPredicates() != null && !s.getPredicates().isEmpty()) {
        for (Predicate p : s.getPredicates()) {
          if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
            SegmentPredicateFound confFound = new SegmentPredicateFound();
            SegmentFound segFound = new SegmentFound();
            segFound.setDescription(s.getDescription());
            segFound.setExt(s.getExt());
            segFound.setId(s.getId());
            segFound.setLabel(s.getLabel());
            segFound.setName(s.getName());
            confFound.setSegmenteFound(segFound);
            confFound.setPredicate(p);
            segmentPredicateFounds.add(confFound);

          }

        }
      }

      if (s.getConformanceStatements() != null && !s.getConformanceStatements().isEmpty()) {
        for (ConformanceStatement p : s.getConformanceStatements()) {
          if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
            SegmentConformanceStatmentFound confFound = new SegmentConformanceStatmentFound();
            SegmentFound segFound = new SegmentFound();
            segFound.setDescription(s.getDescription());
            segFound.setExt(s.getExt());
            segFound.setId(s.getId());
            segFound.setLabel(s.getLabel());
            segFound.setName(s.getName());
            confFound.setSegmenteFound(segFound);
            confFound.setConformanceStatement(p);
            segmentConformanceStatmentFounds.add(confFound);

          }

        }
      }



    }
    for (Datatype d : allDatatypes) {
      for (ValueSetOrSingleCodeBinding vs : d.getValueSetBindings()) {
        if (vs.getTableId() != null && vs.getTableId().equals(wrapper.getTableId())) {
          if (vs instanceof ValueSetBinding) {
            ValueSetBinding binding = (ValueSetBinding) vs;
            DatatypeValueSetBindingFound datatypeFound = new DatatypeValueSetBindingFound();
            datatypeFound.setBinding(binding);
            DatatypeFound dt = new DatatypeFound();
            dt.setDescription(dt.getDescription());
            dt.setId(d.getId());
            dt.setExt(d.getExt());
            dt.setLabel(d.getLabel());
            dt.setName(d.getName());
            datatypeFound.setDatatypeFound(dt);
            datatypeValueSetBindingfounds.add(datatypeFound);

          }

        }
      }

      if (d.getPredicates() != null && !d.getPredicates().isEmpty()) {
        for (Predicate p : d.getPredicates()) {

          if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
            DatatypePredicateFound confFound = new DatatypePredicateFound();
            DatatypeFound dt = new DatatypeFound();
            dt.setDescription(dt.getDescription());
            dt.setId(d.getId());
            dt.setExt(d.getExt());
            dt.setLabel(d.getLabel());
            dt.setName(d.getName());
            confFound.setDatatypeFound(dt);
            confFound.setPredicate(p);
            datatypePredicateFounds.add(confFound);

          }

        }
      }

      if (d.getConformanceStatements() != null && !d.getConformanceStatements().isEmpty()) {
        for (ConformanceStatement p : d.getConformanceStatements()) {
          if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
            DatatypeConformanceStatmentFound confFound = new DatatypeConformanceStatmentFound();
            DatatypeFound dt = new DatatypeFound();
            dt.setDescription(dt.getDescription());
            dt.setId(d.getId());
            dt.setExt(d.getExt());
            dt.setLabel(d.getLabel());
            dt.setName(d.getName());
            confFound.setDatatypeFound(dt);
            confFound.setConformanceStatement(p);
            datatypeConformanceStatmentFounds.add(confFound);
          }
        }
      }
    }

    for (ProfileComponentLink link : ig.getProfile().getProfileComponentLibrary().getChildren()) {
      ProfileComponent pc = profileComponentService.findById(link.getId());
      for (SubProfileComponent spc : pc.getChildren()) {
        if (spc.getOldValueSetBindings() != null) {
          boolean isFound = false;
          for (ValueSetOrSingleCodeBinding vs : spc.getOldValueSetBindings()) {
            if (vs.getTableId() != null && vs.getTableId().equals(wrapper.getTableId())) {
              if (vs instanceof ValueSetBinding) {
                isFound = true;
              }
            }
          }

          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent Old ValueSetBinding is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getValueSetBindings() != null) {
          boolean isFound = false;
          for (ValueSetOrSingleCodeBinding vs : spc.getValueSetBindings()) {
            if (vs.getTableId() != null && vs.getTableId().equals(wrapper.getTableId())) {
              if (vs instanceof ValueSetBinding) {
                isFound = true;
              }
            }
          }

          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent New ValueSetBinding is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getOldConformanceStatements() != null) {
          boolean isFound = false;
          for (ConformanceStatement p : spc.getAttributes().getOldConformanceStatements()) {
            if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
              isFound = true;
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent Old Conformnce Statement is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getConformanceStatements() != null) {
          boolean isFound = false;
          for (ConformanceStatement p : spc.getAttributes().getConformanceStatements()) {
            if (p.getAssertion() != null && p.getAssertion().contains(wrapper.getAssertionId())) {
              isFound = true;
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent New Conformnce Statement is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getOldPredicate() != null) {
          if (spc.getOldPredicate().getAssertion() != null
              && spc.getOldPredicate().getAssertion().contains(wrapper.getAssertionId())) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent Old Predicate is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getPredicate() != null) {
          if (spc.getAttributes().getPredicate().getAssertion() != null && spc.getAttributes()
              .getPredicate().getAssertion().contains(wrapper.getAssertionId())) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent New Predicate is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getCoConstraintsTable() != null) {
          CoConstraintsTable coconstraints = spc.getAttributes().getCoConstraintsTable();
          boolean isFound = false;
          if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
              && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
            for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
              if (coconstraints.getThenMapData().get(thn.getId()) != null
                  && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
                for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {
                  if (coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets() != null
                      && !coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets()
                          .isEmpty()) {
                    for (int j = 0; j < coconstraints.getThenMapData().get(thn.getId()).get(i)
                        .getValueSets().size(); j++) {
                      String tableId = coconstraints.getThenMapData().get(thn.getId()).get(i)
                          .getValueSets().get(j).getTableId();
                      if (tableId != null && tableId.equals(wrapper.getTableId())) {
                        isFound = true;
                      }
                    }
                  }
                }
              }
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent new CoConstraintTable is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }

        if (spc.getAttributes().getOldCoConstraintsTable() != null) {
          CoConstraintsTable coconstraints = spc.getAttributes().getOldCoConstraintsTable();
          boolean isFound = false;
          if (coconstraints != null && coconstraints.getThenColumnDefinitionList() != null
              && !coconstraints.getThenColumnDefinitionList().isEmpty()) {
            for (CoConstraintColumnDefinition thn : coconstraints.getThenColumnDefinitionList()) {
              if (coconstraints.getThenMapData().get(thn.getId()) != null
                  && !coconstraints.getThenMapData().get(thn.getId()).isEmpty()) {
                for (int i = 0; i < coconstraints.getThenMapData().get(thn.getId()).size(); i++) {
                  if (coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets() != null
                      && !coconstraints.getThenMapData().get(thn.getId()).get(i).getValueSets()
                          .isEmpty()) {
                    for (int j = 0; j < coconstraints.getThenMapData().get(thn.getId()).get(i)
                        .getValueSets().size(); j++) {
                      String tableId = coconstraints.getThenMapData().get(thn.getId()).get(i)
                          .getValueSets().get(j).getTableId();
                      if (tableId != null && tableId.equals(wrapper.getTableId())) {
                        isFound = true;
                      }
                    }
                  }
                }
              }
            }
          }
          if (isFound) {
            ProfileComponentFound pcf = new ProfileComponentFound();
            pcf.setDescription(pc.getDescription());
            pcf.setId(pc.getId());
            pcf.setName(pc.getName());
            pcf.setTargetPosition(spc.getPosition());
            pcf.setWhere("This ProfileComponent Old CoConstraintTable is using this Table.");
            profileComponentFounds.add(pcf);
          }
        }
      }
    }



    ValueSetCrossReference ret = new ValueSetCrossReference();
    ret.setCoConstraintFounds(coConstraintFounds);
    ret.setMessageValueSetBindingfounds(messageValueSetBindingfounds);
    ret.setSegmentValueSetBindingfounds(segmentValueSetBindingfounds);
    ret.setDatatypeValueSetBindingfounds(datatypeValueSetBindingfounds);
    ret.setDatatypeConformanceStatmentFounds(datatypeConformanceStatmentFounds);
    ret.setDatatypePredicateFounds(datatypePredicateFounds);
    ret.setSegmentConformanceStatmentFounds(segmentConformanceStatmentFounds);
    ret.setSegmentPredicateFounds(segmentPredicateFounds);
    ret.setMessageConformanceStatmentFounds(messageConformanceStatmentFounds);
    ret.setMessagePredicateFounds(messagePredicateFounds);
    ret.setProfileComponentFound(profileComponentFounds);
    ret.setEmpty();
    return ret;
  }
}
