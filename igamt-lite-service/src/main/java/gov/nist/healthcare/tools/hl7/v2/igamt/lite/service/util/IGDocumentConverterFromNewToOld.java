package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatypes;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocument;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segments;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.TableLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Tables;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.DatatypeService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.SegmentService;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.TableService;
import gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain.DocumentMetaDataPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain.IGDocumentPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain.ProfileMetaDataPreLib;
import gov.nist.healthcare.tools.hl7.v2.igamt.prelib.domain.ProfilePreLib;

public class IGDocumentConverterFromNewToOld {
  @Autowired
  private DatatypeService datatypeService;

  @Autowired
  private SegmentService segmentService;

  @Autowired
  private TableService tableService;

  private static final Logger log = LoggerFactory.getLogger(IGDocumentConverterFromNewToOld.class);

  public IGDocumentPreLib convert(IGDocument doc) {
    IGDocumentPreLib preLib = new IGDocumentPreLib();
    preLib.setAccountId(doc.getAccountId());
    preLib.setChildSections(doc.getChildSections());
    preLib.setComment(doc.getComment());
    preLib.setId(doc.getId());
    preLib.setScope(doc.getScope());
    preLib.setType(doc.getType());
    preLib.setUsageNote(doc.getUsageNote());

    DocumentMetaDataPreLib metaDataPreLib = new DocumentMetaDataPreLib();
    metaDataPreLib.setDate(doc.getMetaData().getDate());
    metaDataPreLib.setExt(doc.getMetaData().getExt());
    metaDataPreLib.setIdentifier(doc.getMetaData().getIdentifier());
    metaDataPreLib.setOrgName(doc.getMetaData().getOrgName());
    metaDataPreLib.setSpecificationName(doc.getMetaData().getSpecificationName());
    metaDataPreLib.setStatus(doc.getMetaData().getStatus());
    metaDataPreLib.setSubTitle(doc.getMetaData().getSubTitle());
    metaDataPreLib.setTitle(doc.getMetaData().getTitle());
    metaDataPreLib.setTopics(doc.getMetaData().getTopics());
    metaDataPreLib.setType(doc.getType());
    metaDataPreLib.setVersion(doc.getMetaData().getVersion());
    preLib.setMetaData(metaDataPreLib);

    preLib.setProfile(convert(doc.getProfile()));
    return preLib;
  }

  public ProfilePreLib convert(Profile p) {
    ProfilePreLib ppl = new ProfilePreLib();
    ppl.setAccountId(p.getAccountId());
    ppl.setBaseId(p.getBaseId());
    ppl.setChanges(p.getChanges());
    ppl.setComment(p.getComment());
    ppl.setConstraintId(p.getConstraintId());
    ppl.setId(p.getId());
    ppl.setScope(p.getScope());
    ppl.setSectionContents(p.getSectionContents());
    ppl.setSectionDescription(p.getSectionDescription());
    ppl.setSectionPosition(p.getSectionPosition());
    ppl.setSectionTitle(p.getSectionTitle());
    ppl.setSourceId(p.getSourceId());
    ppl.setType(p.getType());
    ppl.setUsageNote(p.getUsageNote());

    ProfileMetaDataPreLib profileMetaDataPreLib = new ProfileMetaDataPreLib();
    profileMetaDataPreLib.setEncodings(p.getMetaData().getEncodings());
    profileMetaDataPreLib.setExt(p.getMetaData().getExt());
    profileMetaDataPreLib.setHl7Version(p.getMetaData().getHl7Version());
    profileMetaDataPreLib.setSchemaVersion(p.getMetaData().getSchemaVersion());
    profileMetaDataPreLib.setProfileID(null);
    profileMetaDataPreLib.setSpecificationName(p.getMetaData().getSpecificationName());
    profileMetaDataPreLib.setStatus(p.getMetaData().getStatus());
    profileMetaDataPreLib.setSubTitle(p.getMetaData().getSubTitle());
    profileMetaDataPreLib.setTopics(p.getMetaData().getTopics());
    profileMetaDataPreLib.setType(p.getMetaData().getType());
    ppl.setMetaData(profileMetaDataPreLib);
    ppl.setMessages(p.getMessages());

    Datatypes datatypes = new Datatypes();
    datatypes.setId(p.getDatatypeLibrary().getId());
    datatypes.setSectionContents(p.getDatatypeLibrary().getSectionContents());
    //datatypes.setSectionDescription(p.getDatatypeLibrary().getSectionDescription());
    //datatypes.setSectionDescription(p.getDatatypeLibrary().getSectionDescription());
    datatypes.setSectionPosition(p.getDatatypeLibrary().getSectionPosition());
    datatypes.setSectionTitle(p.getDatatypeLibrary().getSectionTitle());
    datatypes.setType(p.getDatatypeLibrary().getType());
    for (DatatypeLink link : p.getDatatypeLibrary().getChildren()) {
      Datatype dt = datatypeService.findById(link.getId());
      dt.setLabel(dt.getName() + dt.getExt());
      datatypes.addDatatype(dt);
    }
    ppl.setDatatypes(datatypes);

    Segments segments = new Segments();
    segments.setId(p.getSegmentLibrary().getId());
    segments.setSectionContents(p.getSegmentLibrary().getSectionContents());
    //segments.setSectionDescription(p.getSegmentLibrary().getSectionDescription());
    segments.setSectionPosition(p.getSegmentLibrary().getSectionPosition());
    segments.setSectionTitle(p.getSegmentLibrary().getSectionTitle());
    segments.setType(p.getSegmentLibrary().getType());
    for (SegmentLink link : p.getSegmentLibrary().getChildren()) {
      Segment seg = segmentService.findById(link.getId());
      seg.setLabel(link.getLabel());
      segments.addSegment(seg);
    }
    ppl.setSegments(segments);

    Tables tables = new Tables();
    tables.setDateCreated(p.getTableLibrary().getMetaData().getDate());
    tables.setDescription(p.getTableLibrary().getDescription());
    tables.setId(p.getTableLibrary().getId());
    tables.setName(p.getTableLibrary().getProfileName());
    tables.setOrganizationName(p.getTableLibrary().getOrganizationName());
    tables.setProfileName(p.getTableLibrary().getProfileName());
    tables.setSectionContents(p.getTableLibrary().getSectionContents());
    //tables.setSectionDescription(p.getTableLibrary().getSectionDescription());
    tables.setSectionPosition(p.getTableLibrary().getSectionPosition());
    tables.setSectionTitle(p.getTableLibrary().getSectionTitle());
    tables.setStatus(p.getTableLibrary().getStatus());
    tables.setType(p.getTableLibrary().getType());
    tables.setValueSetLibraryIdentifier(p.getTableLibrary().getValueSetLibraryIdentifier());
    tables.setValueSetLibraryVersion(p.getTableLibrary().getValueSetLibraryVersion());
    for (TableLink link : p.getTableLibrary().getChildren()) {
      Table t = tableService.findById(link.getId());
      t.setBindingIdentifier(link.getBindingIdentifier());
      tables.addTable(t);
    }
    ppl.setTables(tables);

    return ppl;
  }
}
