package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

public class IGDocumentConverterFromOldToNew {
  // private static final Logger log =
  // LoggerFactory.getLogger(IGDocumentConverterFromOldToNew.class);
  //
  // public void convert() {
  //
  // MongoOperations mongoOps;
  // try {
  // mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new MongoClient(), "igamt"));
  // mongoOps.dropCollection(Table.class);
  // mongoOps.dropCollection(TableLibrary.class);
  // mongoOps.dropCollection(Datatype.class);
  // mongoOps.dropCollection(DatatypeLibrary.class);
  // mongoOps.dropCollection(Segment.class);
  // mongoOps.dropCollection(SegmentLibrary.class);
  // mongoOps.dropCollection(Message.class);
  // mongoOps.dropCollection(IGDocument.class);
  //
  // ObjectMapper mapper = new ObjectMapper();
  // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  // IGDocumentReadConverterPreLib conv = new IGDocumentReadConverterPreLib();
  //
  //
  // DBCollection coll1 = mongoOps.getCollection("igdocumentPreLibHL7");
  // DBCursor cur1 = coll1.find();
  // while (cur1.hasNext()) {
  // DBObject source = cur1.next();
  // IGDocumentPreLib appPreLib = conv.convert(source);
  // if (appPreLib.getScope().equals(IGDocumentScope.HL7STANDARD)) {
  // System.out.println("HL7 IGDOC Converting started!");
  // System.out.println(appPreLib.getId());
  // HL7STANDARD(appPreLib, mongoOps);
  // }
  // }
  //
  // DBCollection coll2 = mongoOps.getCollection("igdocumentPreLibPRELOADED");
  // DBCursor cur2 = coll2.find();
  // while (cur2.hasNext()) {
  // DBObject source = cur2.next();
  // IGDocumentPreLib appPreLib = conv.convert(source);
  // if (appPreLib.getScope().equals(IGDocumentScope.PRELOADED)) {
  // System.out.println("PRELOADED IGDOC Converting started!");
  // System.out.println(appPreLib.getId());
  // USER(appPreLib, mongoOps);
  // }
  // }
  //
  // DBCollection coll3 = mongoOps.getCollection("igdocumentPreLibUSER");
  // DBCursor cur3 = coll3.find();
  // while (cur3.hasNext()) {
  // DBObject source = cur3.next();
  // IGDocumentPreLib appPreLib = conv.convert(source);
  // if (appPreLib.getScope().equals(IGDocumentScope.USER)) {
  // System.out.println("USER IGDOC Converting started!");
  // System.out.println(appPreLib.getId());
  // USER(appPreLib, mongoOps);
  // }
  // }
  //
  // // DBCollection coll4 = mongoOps.getCollection("igdocumentPreLibCORRECTED");
  // // DBCursor cur4 = coll4.find();
  // // while (cur4.hasNext()) {
  // // DBObject source = cur4.next();
  // // IGDocumentPreLib appPreLib = conv.convert(source);
  // // if (appPreLib.getScope().equals(IGDocumentScope.USER)){
  // // System.out.println("USER IGDOC Converting started!");
  // // System.out.println(appPreLib.getId());
  // // USER(appPreLib, mongoOps);
  // // }
  // // }
  //
  // } catch (Exception e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // e.printStackTrace();
  // }
  //
  // }
  //
  // private void USER(IGDocumentPreLib appPreLib, MongoOperations mongoOps) {
  // IGDocument app = new IGDocument();
  //
  // ProfilePreLib ppl = appPreLib.getProfile();
  // Profile prof = new Profile();
  // prof.setAccountId(appPreLib.getAccountId());
  // prof.setBaseId(ppl.getBaseId());
  // prof.setChanges(ppl.getChanges());
  // prof.setComment(ppl.getComment());
  // prof.setConstraintId(ppl.getConstraintId());
  // ProfileMetaDataPreLib pmdpl = ppl.getMetaData();
  // ProfileMetaData profileMetaData = new ProfileMetaData();
  // profileMetaData.setEncodings(pmdpl.getEncodings());
  // profileMetaData.setExt(pmdpl.getExt());
  // profileMetaData.setHl7Version(pmdpl.getHl7Version());
  // profileMetaData.setOrgName("NIST");
  // profileMetaData.setSchemaVersion(pmdpl.getSchemaVersion());
  // profileMetaData.setSpecificationName(pmdpl.getSpecificationName());
  // profileMetaData.setStatus(pmdpl.getStatus());
  // profileMetaData.setSubTitle(pmdpl.getSubTitle());
  // profileMetaData.setTopics(pmdpl.getTopics());
  // profileMetaData.setType(pmdpl.getType());
  // profileMetaData.setProfileID(UUID.randomUUID().toString());
  // prof.setMetaData(profileMetaData);
  // prof.setScope(ppl.getScope());
  // prof.setSectionContents(ppl.getSectionContents());
  // prof.setSectionDescription(ppl.getSectionDescription());
  // prof.setSectionPosition(ppl.getSectionPosition());
  // prof.setSectionTitle(ppl.getSectionTitle());
  // prof.setSourceId(ppl.getSourceId());
  // prof.setType(ppl.getType());
  // prof.setUsageNote(ppl.getUsageNote());
  // prof.setScope(appPreLib.getScope());
  // app.addProfile(prof, appPreLib.getChildSections());
  // DocumentMetaDataPreLib docMetaDataPreLib = appPreLib.getMetaData();
  // DocumentMetaData metaData = new DocumentMetaData();
  // metaData.setDate(Constant.mdy.format(new Date()));
  // metaData.setExt(docMetaDataPreLib.getExt());
  // metaData.setHl7Version(appPreLib.getProfile().getMetaData().getHl7Version());
  // metaData.setOrgName(docMetaDataPreLib.getOrgName());
  // metaData.setSpecificationName(docMetaDataPreLib.getSpecificationName());
  // metaData.setStatus(docMetaDataPreLib.getStatus());
  // metaData.setSubTitle(docMetaDataPreLib.getSubTitle());
  // metaData.setTitle(docMetaDataPreLib.getTitle());
  // metaData.setTopics(docMetaDataPreLib.getTopics());
  // app.setMetaData(metaData);
  // log.info("hl7Version=" + appPreLib.getProfile().getMetaData().getHl7Version());
  // Set<Message> msgsPreLib = appPreLib.getProfile().getMessages().getChildren();
  // List<Constant.SCOPE> scopes = new ArrayList<Constant.SCOPE>();
  // scopes.add(Constant.SCOPE.HL7STANDARD);
  //
  //
  // HashMap<String, Segment> hl7SegmentMap =
  // this.findHL7SegemntsByVersion(app.getProfile().getMetaData().getHl7Version(), mongoOps);
  // HashMap<String, Datatype> hl7DatatypeMap =
  // this.findHL7DatatypesByVersion(app.getProfile().getMetaData().getHl7Version(), mongoOps);
  // HashMap<String, Table> hl7TableMap =
  // this.findHL7TablesByVersion(app.getProfile().getMetaData().getHl7Version(), mongoOps);
  // IGDocument hl7IGDocument =
  // this.findHL7IGDocumentByVersion(app.getProfile().getMetaData().getHl7Version(), mongoOps);
  // Set<Table> userTables = new HashSet<Table>();
  //
  // TableLibraryMetaData tabMetaData = new TableLibraryMetaData();
  // tabMetaData.setTableLibId(UUID.randomUUID().toString());
  // tabMetaData.setDate(Constant.mdy.format(new Date()));
  // tabMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
  // tabMetaData.setOrgName("NIST");
  // app.getProfile().getTableLibrary().setScope(Constant.SCOPE.USER);
  // app.getProfile().getTableLibrary().setMetaData(tabMetaData);
  // mongoOps.insert(app.getProfile().getTableLibrary(), "table-library");
  //
  // for (Message sm : msgsPreLib) {
  // sm.setId(null);
  // for (SegmentRefOrGroup sog : sm.getChildren()) {
  // this.updateUsageAndVisitGroupChild(sog);
  // }
  // app.getProfile().getMessages().addMessage(sm);
  // }
  //
  // Set<Segment> segs = appPreLib.getProfile().getSegments().getChildren();
  // Set<Segment> newsegs = new HashSet<Segment>();
  // SegmentLibraryMetaData segMetaData = new SegmentLibraryMetaData();
  // segMetaData.setSegmentLibId(UUID.randomUUID().toString());
  // segMetaData.setDate(Constant.mdy.format(new Date()));
  // segMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
  // segMetaData.setOrgName("NIST");
  // app.getProfile().getSegmentLibrary().setScope(Constant.SCOPE.USER);
  // app.getProfile().getSegmentLibrary().setMetaData(segMetaData);
  // mongoOps.insert(app.getProfile().getSegmentLibrary(), "segment-library");
  // for (Segment seg : segs) {
  // if (seg.getId() != null) {
  // if (seg.getLabel() == null || seg.getLabel().equals(seg.getName())) {
  // String oldSegmentId = seg.getId();
  // seg =
  // hl7SegmentMap.get(hl7IGDocument.getProfile().getSegmentLibrary()
  // .findOneByName(seg.getName()).getId());
  // String newSegmentId = seg.getId();
  // this.updateSegmentId(oldSegmentId, newSegmentId, app.getProfile());
  // seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
  // Query query = query(where("_id").is(seg.getId()));
  // Update update = update("libIds", seg.getLibIds());
  // mongoOps.updateFirst(query, update, Segment.class);
  // seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
  // app.getProfile().getSegmentLibrary()
  // .addSegment(new SegmentLink(seg.getId(), seg.getName(), ""));
  // for (Field f : seg.getFields()) {
  // if (f.getTable() != null) {
  // Table t = hl7TableMap.get(f.getTable().getId());
  // if (t == null) {
  // log.error(f.getTable() + " is missing!!!!!");
  // } else {
  // if (!t.getLibIds().contains(app.getProfile().getTableLibrary().getId())) {
  // t.getLibIds().add(app.getProfile().getTableLibrary().getId());
  // Query query2 = query(where("_id").is(t.getId()));
  // Update update2 = update("libIds", t.getLibIds());
  // mongoOps.updateFirst(query2, update2, Table.class);
  //
  // app.getProfile().getTableLibrary()
  // .addTable(new TableLink(t.getId(), t.getBindingIdentifier()));
  // }
  // }
  // }
  //
  // if (f.getDatatype() != null) {
  // Datatype dt = hl7DatatypeMap.get(f.getDatatype().getId());
  // if (!dt.getLibIds().contains(app.getProfile().getDatatypeLibrary().getId())) {
  // dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
  // Query query2 = query(where("_id").is(dt.getId()));
  // Update update2 = update("libIds", dt.getLibIds());
  // mongoOps.updateFirst(query2, update2, Datatype.class);
  //
  // app.getProfile().getDatatypeLibrary()
  // .addDatatype(new DatatypeLink(dt.getId(), dt.getName(), ""));
  // }
  // } else {
  // System.out.println("Field DT missing!!" + f.getPosition() + " of STD " + seg.getId());
  // }
  // }
  // } else {
  // String ext = seg.getLabel().replace(seg.getName() + "_", "");
  //
  // seg.setScope(Constant.SCOPE.USER);
  // seg.setStatus(STATUS.UNPUBLISHED);
  // seg.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  // seg.setExt(ext);
  //
  //
  // for (Field f : seg.getFields()) {
  // if (f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)) {
  // f.setUsage(Usage.X);
  // }
  // if (f.getTable() != null) {
  // Table t = appPreLib.getProfile().getTables().findOneTableById(f.getTable().getId());
  // if (t == null) {
  // log.error(f.getTable() + " is missing!!!!!");
  // } else {
  // if (t.getLibIds() == null) {
  // t.setLibIds(new HashSet<String>());
  // }
  // if (!t.getLibIds().contains(app.getProfile().getTableLibrary().getId())) {
  // t.setScope(Constant.SCOPE.USER);
  // t.setStatus(STATUS.UNPUBLISHED);
  // t.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  // t.getLibIds().add(app.getProfile().getTableLibrary().getId());
  //
  // String oldTableId = t.getId();
  // String newTableId = ObjectId.get().toString();
  //
  // t.setId(newTableId);
  // userTables.add(t);
  //
  // this.updateTableId(oldTableId, newTableId, appPreLib.getProfile());
  //
  // app.getProfile().getTableLibrary()
  // .addTable(new TableLink(t.getId(), t.getBindingIdentifier()));
  // }
  // }
  // }
  //
  // if (f.getDatatype() == null) {
  // System.out
  // .println("Field DT missing!!" + f.getPosition() + " of USER " + seg.getId());
  // }
  //
  // }
  //
  // String oldSegmentId = seg.getId();
  // String newSegmentId = ObjectId.get().toString();
  // seg.setId(newSegmentId);
  //
  // this.updateSegmentId(oldSegmentId, newSegmentId, app.getProfile());
  //
  // seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
  // app.getProfile().getSegmentLibrary()
  // .addSegment(new SegmentLink(seg.getId(), seg.getName(), ext));
  // newsegs.add(seg);
  // }
  // } else {
  // log.error("Null id seg=" + seg.toString());
  // }
  // }
  //
  // Set<Datatype> dts = appPreLib.getProfile().getDatatypes().getChildren();
  // Set<Datatype> newdts = new HashSet<Datatype>();
  // DatatypeLibraryMetaData dtMetaData = new DatatypeLibraryMetaData();
  // dtMetaData.setDatatypeLibId(UUID.randomUUID().toString());
  // dtMetaData.setDate(Constant.mdy.format(new Date()));
  // dtMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
  // dtMetaData.setName(Constant.DefaultDatatypeLibrary);
  // dtMetaData.setOrgName("NIST");
  // // dtMetaData.setVersion(ppl.getMetaData().getVersion());
  // app.getProfile().getDatatypeLibrary().setScope(Constant.SCOPE.USER);
  // app.getProfile().getDatatypeLibrary().setMetaData(dtMetaData);
  // mongoOps.insert(app.getProfile().getDatatypeLibrary(), "datatype-library");
  // for (Datatype dt : dts) {
  // if (dt.getId() != null) {
  // if (dt.getLabel() == null || dt.getLabel().equals(dt.getName())) {
  // String oldDatatypeId = dt.getId();
  // dt =
  // hl7DatatypeMap.get(hl7IGDocument.getProfile().getDatatypeLibrary()
  // .findOneByName(dt.getName()).getId());
  // String newDatatypeId = dt.getId();
  //
  // this.updateDatatypeId(oldDatatypeId, newDatatypeId, appPreLib.getProfile());
  //
  // dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
  // Query query = query(where("_id").is(dt.getId()));
  // Update update = update("libIds", dt.getLibIds());
  // mongoOps.updateFirst(query, update, Datatype.class);
  // app.getProfile().getDatatypeLibrary()
  // .addDatatype(new DatatypeLink(dt.getId(), dt.getName(), ""));
  // for (Component c : dt.getComponents()) {
  // if (c.getTable() != null) {
  // Table t = hl7TableMap.get(c.getTable().getId());
  // if (t == null) {
  // log.error(c.getTable() + " is missing!!!!!");
  // } else {
  // if (!t.getLibIds().contains(app.getProfile().getTableLibrary().getId())) {
  // t.getLibIds().add(app.getProfile().getTableLibrary().getId());
  // Query query2 = query(where("_id").is(t.getId()));
  // Update update2 = update("libIds", t.getLibIds());
  // mongoOps.updateFirst(query2, update2, Table.class);
  //
  // app.getProfile().getTableLibrary()
  // .addTable(new TableLink(t.getId(), t.getBindingIdentifier()));
  // }
  // }
  // }
  // if (c.getDatatype() != null) {
  // Datatype cdt = hl7DatatypeMap.get(c.getDatatype().getId());
  // if (!cdt.getLibIds().contains(app.getProfile().getDatatypeLibrary().getId())) {
  // cdt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
  // Query query2 = query(where("_id").is(cdt.getId()));
  // Update update2 = update("libIds", cdt.getLibIds());
  // mongoOps.updateFirst(query2, update2, Datatype.class);
  //
  // app.getProfile().getDatatypeLibrary()
  // .addDatatype(new DatatypeLink(cdt.getId(), cdt.getName(), ""));
  // }
  // } else {
  // System.out.println("COMPONENT DT missing!!" + c.getPosition() + " of STD "
  // + dt.getId());
  // }
  // }
  //
  // } else {
  // String ext = dt.getLabel().replace(dt.getName() + "_", "");
  // dt.setExt(ext);
  // dt.setScope(Constant.SCOPE.USER);
  // dt.setStatus(Constant.STATUS.UNPUBLISHED);
  // dt.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  //
  // for (Component c : dt.getComponents()) {
  // if (c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)) {
  // c.setUsage(Usage.X);
  // }
  //
  // if (c.getTable() != null) {
  // Table t = appPreLib.getProfile().getTables().findOneTableById(c.getTable().getId());
  // if (t == null) {
  // log.error(c.getTable() + "is missing!!!!!");
  // } else {
  // if (t.getLibIds() == null) {
  // t.setLibIds(new HashSet<String>());
  // }
  // if (!t.getLibIds().contains(app.getProfile().getTableLibrary().getId())) {
  // t.setScope(Constant.SCOPE.USER);
  // t.setStatus(STATUS.UNPUBLISHED);
  // t.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  // t.getLibIds().add(app.getProfile().getTableLibrary().getId());
  //
  // String oldTableId = t.getId();
  // String newTableId = ObjectId.get().toString();
  //
  // t.setId(newTableId);
  // this.updateTableId(oldTableId, newTableId, appPreLib.getProfile());
  //
  // userTables.add(t);
  // app.getProfile().getTableLibrary()
  // .addTable(new TableLink(t.getId(), t.getBindingIdentifier()));
  // }
  // }
  // }
  //
  // if (c.getDatatype() == null) {
  // System.out.println("Field DT missing!!" + c.getPosition() + " of USER " + dt.getId());
  // }
  // }
  // String oldDatatypeId = dt.getId();
  // String newDatatypeId = ObjectId.get().toString();
  // dt.setId(newDatatypeId);
  // this.updateDatatypeId(oldDatatypeId, newDatatypeId, appPreLib.getProfile());
  // dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
  // app.getProfile().getDatatypeLibrary()
  // .addDatatype(new DatatypeLink(dt.getId(), dt.getName(), ext));
  // newdts.add(dt);
  // }
  // } else {
  // log.error("Null id seg=" + dt.toString());
  // }
  // }
  //
  // mongoOps.save(app.getProfile().getSegmentLibrary());
  // mongoOps.save(app.getProfile().getDatatypeLibrary());
  // mongoOps.save(app.getProfile().getTableLibrary());
  //
  //
  // mongoOps.insert(app.getProfile().getMessages().getChildren(), "message");
  // mongoOps.insert(newsegs, "segment");
  // mongoOps.insert(newdts, "datatype");
  // mongoOps.insert(userTables, "table");
  // mongoOps.insert(app, "igdocument");
  //
  // }
  //
  // private void updateDatatypeId(String oldDatatypeId, String newDatatypeId,
  // ProfilePreLib profilePreLib) {
  // for (Segment s : profilePreLib.getSegments().getChildren()) {
  // for (Field f : s.getFields()) {
  // if (f.getDatatype() != null && f.getDatatype().getId().equals(oldDatatypeId))
  // f.getDatatype().setId(newDatatypeId);
  // }
  //
  // for (Mapping map : s.getDynamicMapping().getMappings()) {
  //
  // for (Case c : map.getCases()) {
  // if (c.getDatatype() != null && c.getDatatype().equals(oldDatatypeId))
  // c.setDatatype(newDatatypeId);
  // }
  //
  // }
  // }
  //
  // for (Datatype dt : profilePreLib.getDatatypes().getChildren()) {
  // for (Component c : dt.getComponents()) {
  // if (c.getDatatype() != null && c.getDatatype().getId().equals(oldDatatypeId))
  // c.getDatatype().setId(newDatatypeId);
  // }
  // }
  //
  // }
  //
  // private void updateSegmentId(String oldSegmentId, String newSegmentId, Profile profile) {
  // for (Message m : profile.getMessages().getChildren()) {
  // for (SegmentRefOrGroup sog : m.getChildren()) {
  // updateSegmentIdAndVisitChild(oldSegmentId, newSegmentId, sog);
  // }
  // }
  //
  // }
  //
  // private void updateSegmentIdAndVisitChild(String oldSegmentId, String newSegmentId,
  // SegmentRefOrGroup sog) {
  // if (sog instanceof SegmentRef) {
  // SegmentRef segmentRef = (SegmentRef) sog;
  // if (segmentRef.getRef().getId().equals(oldSegmentId))
  // segmentRef.getRef().setId(newSegmentId);
  // }
  //
  // if (sog instanceof Group) {
  // Group g = (Group) sog;
  //
  // for (SegmentRefOrGroup child : g.getChildren()) {
  // this.updateSegmentIdAndVisitChild(oldSegmentId, newSegmentId, child);
  // }
  // }
  //
  // }
  //
  // private void updateTableId(String oldTableId, String newTableId, ProfilePreLib profilePreLib) {
  // for (Segment s : profilePreLib.getSegments().getChildren()) {
  // for (Field f : s.getFields()) {
  // if (f.getTable() != null && f.getTable().getId().equals(oldTableId))
  // f.getTable().setId(newTableId);
  // }
  // }
  //
  // for (Datatype dt : profilePreLib.getDatatypes().getChildren()) {
  // for (Component c : dt.getComponents()) {
  // if (c.getTable() != null && c.getTable().getId().equals(oldTableId))
  // c.getTable().setId(newTableId);
  // }
  // }
  //
  // }
  //
  // private void HL7STANDARD(IGDocumentPreLib appPreLib, MongoOperations mongoOps) {
  // IGDocument app = new IGDocument();
  //
  // ProfilePreLib ppl = appPreLib.getProfile();
  // Profile prof = new Profile();
  // prof.setBaseId(ppl.getBaseId());
  // prof.setChanges(ppl.getChanges());
  // prof.setComment(ppl.getComment());
  // prof.setConstraintId(ppl.getConstraintId());
  // ProfileMetaDataPreLib pmdpl = ppl.getMetaData();
  // ProfileMetaData profileMetaData = new ProfileMetaData();
  // profileMetaData.setEncodings(pmdpl.getEncodings());
  // profileMetaData.setExt(pmdpl.getExt());
  // profileMetaData.setHl7Version(pmdpl.getHl7Version());
  // profileMetaData.setOrgName("NIST");
  // profileMetaData.setSchemaVersion(pmdpl.getSchemaVersion());
  // profileMetaData.setSpecificationName(pmdpl.getSpecificationName());
  // profileMetaData.setStatus(pmdpl.getStatus());
  // profileMetaData.setSubTitle(pmdpl.getSubTitle());
  // profileMetaData.setTopics(pmdpl.getTopics());
  // profileMetaData.setType(pmdpl.getType());
  //
  // prof.setMetaData(profileMetaData);
  // prof.setScope(ppl.getScope());
  // prof.setSectionContents(ppl.getSectionContents());
  // prof.setSectionDescription(ppl.getSectionDescription());
  // prof.setSectionPosition(ppl.getSectionPosition());
  // prof.setSectionTitle(ppl.getSectionTitle());
  // prof.setSourceId(ppl.getSourceId());
  // prof.setType(ppl.getType());
  // prof.setUsageNote(ppl.getUsageNote());
  // app.addProfile(prof);
  // DocumentMetaDataPreLib docMetaDataPreLib = appPreLib.getMetaData();
  // DocumentMetaData metaData = new DocumentMetaData();
  // metaData.setDate(Constant.mdy.format(new Date()));
  // metaData.setExt(docMetaDataPreLib.getExt());
  // metaData.setHl7Version(appPreLib.getProfile().getMetaData().getHl7Version());
  // metaData.setOrgName(docMetaDataPreLib.getOrgName());
  // metaData.setSpecificationName(docMetaDataPreLib.getSpecificationName());
  // metaData.setStatus(docMetaDataPreLib.getStatus());
  // metaData.setSubTitle(docMetaDataPreLib.getSubTitle());
  // metaData.setTitle(docMetaDataPreLib.getTitle());
  // metaData.setTopics(docMetaDataPreLib.getTopics());
  // app.setMetaData(metaData);
  // log.info("hl7Version=" + appPreLib.getProfile().getMetaData().getHl7Version());
  // Set<Message> msgsPreLib = appPreLib.getProfile().getMessages().getChildren();
  // for (Message sm : msgsPreLib) {
  // for (SegmentRefOrGroup sog : sm.getChildren()) {
  // this.updateUsageAndVisitGroupChild(sog);
  // }
  // app.getProfile().getMessages().addMessage(sm);
  // }
  // Set<Segment> segs = appPreLib.getProfile().getSegments().getChildren();
  // SegmentLibraryMetaData segMetaData = new SegmentLibraryMetaData();
  // segMetaData.setDate(Constant.mdy.format(new Date()));
  // segMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
  // // segMetaData.setName(ppl.getMetaData().getName());
  // segMetaData.setOrgName("NIST");
  // segMetaData.setSegmentLibId(UUID.randomUUID().toString());
  // // segMetaData.setVersion(ppl.getMetaData().getVersion());
  // app.getProfile().getSegmentLibrary().setScope(Constant.SCOPE.HL7STANDARD);
  // app.getProfile().getSegmentLibrary().setMetaData(segMetaData);
  // mongoOps.insert(app.getProfile().getSegmentLibrary(), "segment-library");
  // for (Segment seg : segs) {
  // seg.setExt(null);
  // seg.setScope(Constant.SCOPE.HL7STANDARD);
  // for (Field f : seg.getFields()) {
  // if (f.getUsage().equals(Usage.B) || f.getUsage().equals(Usage.W)) {
  // f.setUsage(Usage.X);
  // }
  // }
  //
  // seg.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  // if (seg.getId() != null) {
  // seg.getLibIds().add(app.getProfile().getSegmentLibrary().getId());
  // app.getProfile().getSegmentLibrary()
  // .addSegment(new SegmentLink(seg.getId(), seg.getName(), ""));
  // } else {
  // log.error("Null id seg=" + seg.toString());
  // }
  // }
  // mongoOps.save(app.getProfile().getSegmentLibrary());
  // Set<Datatype> dts = appPreLib.getProfile().getDatatypes().getChildren();
  // DatatypeLibraryMetaData dtMetaData = new DatatypeLibraryMetaData();
  // dtMetaData.setDate(Constant.mdy.format(new Date()));
  // dtMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
  // dtMetaData.setDatatypeLibId(UUID.randomUUID().toString());
  // dtMetaData.setName("HL7 Standard " + appPreLib.getProfile().getMetaData().getHl7Version());
  // dtMetaData.setOrgName("NIST");
  // // dtMetaData.setVersion(ppl.getMetaData().getVersion());
  // app.getProfile().getDatatypeLibrary().setScope(Constant.SCOPE.HL7STANDARD);
  // app.getProfile().getDatatypeLibrary().setMetaData(dtMetaData);
  // mongoOps.insert(app.getProfile().getDatatypeLibrary(), "datatype-library");
  // for (Datatype dt : dts) {
  // if (dt.getId() != null) {
  // for (Component c : dt.getComponents()) {
  // if (c.getUsage().equals(Usage.B) || c.getUsage().equals(Usage.W)) {
  // c.setUsage(Usage.X);
  // }
  // }
  // dt.setExt(null);
  // dt.setScope(Constant.SCOPE.HL7STANDARD);
  // dt.setStatus(Constant.STATUS.PUBLISHED);
  // dt.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  // dt.getLibIds().add(app.getProfile().getDatatypeLibrary().getId());
  // app.getProfile().getDatatypeLibrary()
  // .addDatatype(new DatatypeLink(dt.getId(), dt.getName(), ""));
  // } else {
  // log.error("Null id dt=" + dt.toString());
  // }
  // }
  // mongoOps.save(app.getProfile().getDatatypeLibrary());
  // Set<Table> tabs = appPreLib.getProfile().getTables().getChildren();
  // TableLibraryMetaData tabMetaData = new TableLibraryMetaData();
  // tabMetaData.setDate(Constant.mdy.format(new Date()));
  // tabMetaData.setHl7Version(ppl.getMetaData().getHl7Version());
  // tabMetaData.setTableLibId(UUID.randomUUID().toString());
  // // tabMetaData.setName(ppl.getMetaData().getName());
  // tabMetaData.setOrgName("NIST");
  // // tabMetaData.setVersion(ppl.getMetaData().getVersion());
  // app.getProfile().getTableLibrary().setScope(Constant.SCOPE.HL7STANDARD);
  // app.getProfile().getTableLibrary().setMetaData(tabMetaData);
  // mongoOps.insert(app.getProfile().getTableLibrary(), "table-library");
  // for (Table tab : tabs) {
  // tab.setScope(Constant.SCOPE.HL7STANDARD);
  // tab.setHl7Version(app.getProfile().getMetaData().getHl7Version());
  // if (tab.getId() != null) {
  // tab.getLibIds().add(app.getProfile().getTableLibrary().getId());
  // app.getProfile().getTableLibrary()
  // .addTable(new TableLink(tab.getId(), tab.getBindingIdentifier()));
  // } else {
  // log.error("Null id tab=" + tab.toString());
  // }
  // }
  // mongoOps.save(app.getProfile().getTableLibrary());
  // mongoOps.insert(app.getProfile().getMessages().getChildren(), "message");
  // mongoOps.insert(segs, "segment");
  // mongoOps.insert(dts, "datatype");
  // mongoOps.insert(tabs, "table");
  // mongoOps.insert(app, "igdocument");
  // }
  //
  // private HashMap<String, Segment> findHL7SegemntsByVersion(String hl7Version,
  // MongoOperations mongoOps) {
  // HashMap<String, Segment> segments = new HashMap<String, Segment>();
  //
  // DBCollection segmentsDBCollection = mongoOps.getCollection("segment");
  // DBCursor segmentCur = segmentsDBCollection.find();
  // while (segmentCur.hasNext()) {
  // DBObject source = segmentCur.next();
  // SegmentReadConverter segmentConv = new SegmentReadConverter();
  // Segment seg = segmentConv.convert(source);
  // if (seg.getScope().equals(SCOPE.HL7STANDARD) && seg.getHl7Version().equals(hl7Version)) {
  // segments.put(seg.getId(), seg);
  // }
  // }
  // return segments;
  // }
  //
  // private HashMap<String, Datatype> findHL7DatatypesByVersion(String hl7Version,
  // MongoOperations mongoOps) {
  // HashMap<String, Datatype> datatypes = new HashMap<String, Datatype>();
  //
  // DBCollection datatypesDBCollection = mongoOps.getCollection("datatype");
  // DBCursor datatypeCur = datatypesDBCollection.find();
  // while (datatypeCur.hasNext()) {
  // DBObject source = datatypeCur.next();
  // DatatypeReadConverter datatypeConv = new DatatypeReadConverter();
  // Datatype dt = datatypeConv.convert(source);
  // if (dt.getScope().equals(SCOPE.HL7STANDARD) && dt.getHl7Version().equals(hl7Version)) {
  // datatypes.put(dt.getId(), dt);
  // }
  // }
  // return datatypes;
  // }
  //
  // private HashMap<String, Table> findHL7TablesByVersion(String hl7Version, MongoOperations
  // mongoOps) {
  // HashMap<String, Table> tables = new HashMap<String, Table>();
  //
  // DBCollection tablesDBCollection = mongoOps.getCollection("table");
  // DBCursor tableCur = tablesDBCollection.find();
  // while (tableCur.hasNext()) {
  // DBObject source = tableCur.next();
  // TableReadConverter tableConv = new TableReadConverter();
  // Table t = tableConv.convert(source);
  // if (t.getScope().equals(SCOPE.HL7STANDARD) && t.getHl7Version().equals(hl7Version)) {
  // tables.put(t.getId(), t);
  // }
  // }
  // return tables;
  // }
  //
  // private IGDocument findHL7IGDocumentByVersion(String hl7Version, MongoOperations mongoOps) {
  // DBCollection igDocumentsDBCollection = mongoOps.getCollection("igdocument");
  // DBCursor igDocumentCur = igDocumentsDBCollection.find();
  // while (igDocumentCur.hasNext()) {
  // DBObject source = igDocumentCur.next();
  // IGDocumentReadConverter igDocumentConv = new IGDocumentReadConverter();
  // IGDocument igd = igDocumentConv.convert(source);
  // if (igd.getScope().equals(IGDocumentScope.HL7STANDARD)
  // && igd.getMetaData().getHl7Version().equals(hl7Version)) {
  // return igd;
  // }
  // }
  // return null;
  // }
  //
  // private void updateUsageAndVisitGroupChild(SegmentRefOrGroup sog) {
  // if (sog.getUsage().equals(Usage.B) || sog.getUsage().equals(Usage.W)) {
  // sog.setUsage(Usage.X);
  // }
  //
  // if (sog instanceof Group) {
  // Group g = (Group) sog;
  //
  // for (SegmentRefOrGroup child : g.getChildren()) {
  // this.updateUsageAndVisitGroupChild(child);
  // }
  // }
  // }
  //
  // public static void main(String[] args) throws IOException {
  // new IGDocumentConverterFromOldToNew().convert();
  // }
}
