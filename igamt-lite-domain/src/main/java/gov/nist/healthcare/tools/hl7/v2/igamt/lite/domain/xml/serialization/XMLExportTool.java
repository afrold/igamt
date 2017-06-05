package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.xml.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Code;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Component;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTComponent;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTMComponentDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTMConstraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DTMPredicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DocumentMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DynamicMappingItem;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Field;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Group;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.IGDocumentConfiguration;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Message;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Profile;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ProfileMetaData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Segment;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRef;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.SegmentRefOrGroup;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Table;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Usage;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.ValueSetOrSingleCodeBinding;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByName;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ByNameOrByID;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintColumnDefinition;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintIFColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.CoConstraintTHENColumnData;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.ConformanceStatement;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraint;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Constraints;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Context;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Predicate;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints.Reference;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class XMLExportTool {

  public void unZipIt(String zipFile, String outputFolder) {

    byte[] buffer = new byte[1024];

    try {

      // create output directory is not exists
      File folder = new File(outputFolder);
      if (!folder.exists()) {
        folder.mkdir();
      }

      // get the zip file content
      ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
      // get the zipped file list entry
      ZipEntry ze = zis.getNextEntry();

      while (ze != null) {

        String fileName = ze.getName();
        File newFile = new File(outputFolder + File.separator + fileName);
        // create all non exists folders
        // else you will hit FileNotFoundException for compressed folder
        new File(newFile.getParent()).mkdirs();

        FileOutputStream fos = new FileOutputStream(newFile);

        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }

        fos.close();
        ze = zis.getNextEntry();
      }

      zis.closeEntry();
      zis.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void zipIt(String zipFile, String SOURCE_FOLDER) {
    List<String> fileList = new ArrayList<String>();

    byte[] buffer = new byte[1024];

    try {

      FileOutputStream fos = new FileOutputStream(zipFile);
      ZipOutputStream zos = new ZipOutputStream(fos);

      for (String file : fileList) {
        ZipEntry ze = new ZipEntry(file);
        zos.putNextEntry(ze);

        FileInputStream in = new FileInputStream(SOURCE_FOLDER + File.separator + file);

        int len;
        while ((len = in.read(buffer)) > 0) {
          zos.write(buffer, 0, len);
        }

        in.close();
      }

      zos.closeEntry();
      // remember close it
      zos.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void generateFileList(File node, List<String> fileList, String SOURCE_FOLDER) {

    // add file only
    if (node.isFile()) {
      fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), SOURCE_FOLDER));
    }

    if (node.isDirectory()) {
      String[] subNote = node.list();
      for (String filename : subNote) {
        generateFileList(new File(node, filename), fileList, SOURCE_FOLDER);
      }
    }

  }

  /**
   * Format the file path for zip
   * 
   * @param file file path
   * @return Formatted file path
   */
  private String generateZipEntry(String file, String SOURCE_FOLDER) {
    return file.substring(SOURCE_FOLDER.length() + 1, file.length());
  }

  public InputStream exportXMLAsValidationFormatForSelectedMessages(Profile profile,
      DocumentMetaData metadata, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap)
      throws CloneNotSupportedException, IOException {
    this.normalizeProfile(profile, segmentsMap, datatypesMap);

    ByteArrayOutputStream outputStream = null;
    byte[] bytes;
    outputStream = new ByteArrayOutputStream();
    ZipOutputStream out = new ZipOutputStream(outputStream);

    String profileXMLStr =
        this.serializeProfileToDoc(profile, metadata, segmentsMap, datatypesMap, tablesMap).toXML();
    String valueSetXMLStr = this.serializeTableXML(profile, metadata, tablesMap);
    String constraintXMLStr =
        this.serializeConstraintsXML(profile, metadata, segmentsMap, datatypesMap, tablesMap);

    this.generateProfileIS(out, profileXMLStr);
    this.generateValueSetIS(out, valueSetXMLStr);
    this.generateConstraintsIS(out, constraintXMLStr);

    out.close();
    bytes = outputStream.toByteArray();
    return new ByteArrayInputStream(bytes);
  }

  public String serializeConstraintsXML(Profile profile, DocumentMetaData metadata,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) {

    Constraints predicates = findAllPredicates(profile, segmentsMap, datatypesMap, tablesMap);
    Constraints conformanceStatements =
        findAllConformanceStatement(profile, segmentsMap, datatypesMap, tablesMap);

    nu.xom.Element e = new nu.xom.Element("ConformanceContext");
    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/ConformanceContext.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    e.addAttribute(schemaDecl);

    e.addAttribute(new Attribute("UUID", profile.getId()));


    nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
    if (metadata == null) {
      elmMetaData.addAttribute(new Attribute("Name", "Constraints for " + "Profile"));
      elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
      elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
      elmMetaData.addAttribute(new Attribute("Date", ""));
    } else {
      elmMetaData.addAttribute(new Attribute("Name", !this.str(metadata.getTitle()).equals("")
          ? this.str(metadata.getTitle()) : "No Title Info"));
      elmMetaData.addAttribute(new Attribute("OrgName", !this.str(metadata.getOrgName()).equals("")
          ? this.str(metadata.getOrgName()) : "No Org Info"));
      elmMetaData.addAttribute(new Attribute("Version", !this.str(metadata.getVersion()).equals("")
          ? this.str(metadata.getVersion()) : "No Version Info"));
      elmMetaData.addAttribute(new Attribute("Date", "No Date Info"));

      if (profile.getMetaData().getSpecificationName() != null
          && !profile.getMetaData().getSpecificationName().equals(""))
        elmMetaData.addAttribute(new Attribute("SpecificationName",
            this.str(profile.getMetaData().getSpecificationName())));
      if (profile.getMetaData().getStatus() != null
          && !profile.getMetaData().getStatus().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Status", this.str(profile.getMetaData().getStatus())));
      if (profile.getMetaData().getTopics() != null
          && !profile.getMetaData().getTopics().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Topics", this.str(profile.getMetaData().getTopics())));
    }
    e.appendChild(elmMetaData);

    this.serializeMain(e, predicates, conformanceStatements);

    return e.toXML();
  }

  private Constraints findAllConformanceStatement(Profile profile, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    Constraints constraints = new Constraints();
    Context dtContext = new Context();
    Context sContext = new Context();
    Context gContext = new Context();
    Context mContext = new Context();

    Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {
      ByID byID = new ByID();
      byID.setByID(m.getId());
      byID.setConformanceStatements(m.retrieveAllConformanceStatements());
      if (byID.getConformanceStatements().size() > 0)
        byNameOrByIDs.add(byID);
    }
    mContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {

      for (SegmentRefOrGroup sog : m.getChildren()) {
        if (sog instanceof Group) {
          byNameOrByIDs = findAllConformanceStatementsForGroup((Group) sog, byNameOrByIDs);
        }
      }
    }
    gContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      ByID byID = new ByID();
      byID.setByID(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"));
      List<ConformanceStatement> segmentConformanceStatements =
          s.retrieveAllConformanceStatementsForXML(tablesMap);
      if (segmentConformanceStatements.size() > 0) {
        byID.setConformanceStatements(segmentConformanceStatements);
        byNameOrByIDs.add(byID);
      }
    }
    sContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      ByID byID = new ByID();
      byID.setByID(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"));
      byID.setConformanceStatements(d.retrieveAllConformanceStatements());
      if (d.getName().equals("DTM"))
        this.generateConstraintsForDTMFormat(byID, d);
      if (byID.getConformanceStatements().size() > 0)
        byNameOrByIDs.add(byID);
    }
    dtContext.setByNameOrByIDs(byNameOrByIDs);

    constraints.setDatatypes(dtContext);
    constraints.setSegments(sContext);
    constraints.setGroups(gContext);
    constraints.setMessages(mContext);
    return constraints;
  }

  private void generateConstraintsForDTMFormat(ByID byID, Datatype d) {
    DTMConstraints dtmConstraints = d.getDtmConstraints();
    IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
    for (DTMComponentDefinition def : dtmConstraints.getDtmComponentDefinitions()) {
      if (def.getUsage().equals(Usage.R)) {
        ConformanceStatement cs = new ConformanceStatement();
        cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
        cs.setConstraintTarget(".");
        cs.setDescription(def.getDescription() + " usage is '" + def.getUsage().toString() + "'.");

        String pattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
        String assertion = "<Assertion>" + "<Format Path=\".\" Regex=\"" + pattern + "\"/>" + "</Assertion>";
        cs.setAssertion(assertion);

        byID.getConformanceStatements().add(cs);
      } else if (def.getUsage().equals(Usage.X)) {
        ConformanceStatement cs = new ConformanceStatement();
        cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
        cs.setConstraintTarget(".");
        cs.setDescription(def.getDescription() + " usage is '" + def.getUsage().toString() + "'.");

        String pattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
        String assertion = "<Assertion>" + "<Format Path=\".\" Regex=\"" + pattern + "\"/>" + "</Assertion>";
        cs.setAssertion(assertion);

        byID.getConformanceStatements().add(cs);
      } else if (def.getUsage().equals(Usage.C)) {
        if (def.getDtmPredicate() != null) {
          DTMPredicate predicate = def.getDtmPredicate();
          if (predicate.getTrueUsage() != null && predicate.getTrueUsage().equals(Usage.R)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if(predicate.getTarget() != null){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern1 = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><AND><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern1 + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2 + "\"/>" + "</AND><Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }
          if (predicate.getTrueUsage() != null && predicate.getTrueUsage().equals(Usage.X)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if(predicate.getTarget() != null){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "True Usage is '" + predicate.getTrueUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern1 = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><AND><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern1 + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2 + "\"/>" + "</AND><Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }

          if (predicate.getFalseUsage() != null && predicate.getFalseUsage().equals(Usage.R)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if(predicate.getTarget() != null){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern1 = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT><AND><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern1 + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2 + "\"/>" + "</AND></NOT><Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }
          if (predicate.getFalseUsage() != null && predicate.getFalseUsage().equals(Usage.X)) {
            if (predicate.getVerb() != null && predicate.getVerb().equals("is valued")) {
              if(predicate.getTarget() != null){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsValuedRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not valued")) {
              ConformanceStatement cs = new ConformanceStatement();
              cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
              cs.setConstraintTarget(".");
              cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
              String ifPattern = config.getDtmCUsageIsNOTValuedRegexCodes().get(def.getPosition());
              String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
              String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
              cs.setAssertion(assertion);
              byID.getConformanceStatements().add(cs);
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern = ifPattern.replace("%", predicate.getValue());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            } else if (predicate.getVerb() != null && predicate.getVerb().equals("is not literal value")) {
              if(predicate.getValue() != null && !predicate.getValue().equals("")){
                ConformanceStatement cs = new ConformanceStatement();
                cs.setConstraintId(d.getLabel() + "_" + def.getDescription() + "_USAGE(" + def.getUsage().toString() + ")");
                cs.setConstraintTarget(".");
                cs.setDescription(def.getDescription() + " usage is 'C'." + "False Usage is '" + predicate.getFalseUsage() + "'. Predicate is '" + predicate.getPredicateDescription() + "'."); 
                String ifPattern1 = config.getDtmCUsageIsLiteralValueRegexCodes().get(def.getPosition());
                ifPattern1 = ifPattern1.replace("%", predicate.getValue());
                String ifPattern2 = config.getDtmRUsageRegexCodes().get(def.getPosition());
                String thenPattern = config.getDtmXUsageRegexCodes().get(def.getPosition());
                String assertion = "<Assertion><IMPLY><NOT><AND><NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern1 + "\"/></NOT>" + "<Format Path=\".\" Regex=\"" + ifPattern2 + "\"/>" + "</AND></NOT><Format Path=\".\" Regex=\"" + thenPattern + "\"/>" + "</IMPLY></Assertion>";
                cs.setAssertion(assertion);
                byID.getConformanceStatements().add(cs);
              }
            }
          }

        }
      }
    }


  }

  private Set<ByNameOrByID> findAllConformanceStatementsForGroup(Group g,
      Set<ByNameOrByID> byNameOrByIDs) {
    ByID byID = new ByID();
    byID.setByID(g.getId());
    if (g.getConformanceStatements().size() > 0) {
      byID.setConformanceStatements(g.getConformanceStatements());
      byNameOrByIDs.add(byID);
    }

    for (SegmentRefOrGroup sog : g.getChildren()) {
      if (sog instanceof Group) {
        byNameOrByIDs = findAllConformanceStatementsForGroup((Group) sog, byNameOrByIDs);
      }
    }

    return byNameOrByIDs;

  }

  private nu.xom.Element serializeConstaint(Constraint c, String type) {
    nu.xom.Element elmConstraint = new nu.xom.Element(type);

    if (c.getConstraintId() != null) {
      elmConstraint.addAttribute(new Attribute("ID", c.getConstraintId()));
    }

    if (c.getConstraintTarget() != null && !c.getConstraintTarget().equals(""))
      elmConstraint.addAttribute(new Attribute("Target", c.getConstraintTarget()));

    if (c instanceof Predicate) {
      Predicate pred = (Predicate) c;
      if (pred.getTrueUsage() != null)
        elmConstraint.addAttribute(new Attribute("TrueUsage", pred.getTrueUsage().value()));
      if (pred.getFalseUsage() != null)
        elmConstraint.addAttribute(new Attribute("FalseUsage", pred.getFalseUsage().value()));
    }

    if (c.getReference() != null) {
      Reference referenceObj = c.getReference();
      nu.xom.Element elmReference = new nu.xom.Element("Reference");
      if (referenceObj.getChapter() != null && !referenceObj.getChapter().equals(""))
        elmReference.addAttribute(new Attribute("Chapter", referenceObj.getChapter()));
      if (referenceObj.getSection() != null && !referenceObj.getSection().equals(""))
        elmReference.addAttribute(new Attribute("Section", referenceObj.getSection()));
      if (referenceObj.getPage() == 0)
        elmReference.addAttribute(new Attribute("Page", "" + referenceObj.getPage()));
      if (referenceObj.getUrl() != null && !referenceObj.getUrl().equals(""))
        elmReference.addAttribute(new Attribute("URL", referenceObj.getUrl()));
      elmConstraint.appendChild(elmReference);
    }
    nu.xom.Element elmDescription = new nu.xom.Element("Description");
    elmDescription.appendChild(c.getDescription());
    elmConstraint.appendChild(elmDescription);

    nu.xom.Node n = this.innerXMLHandler(c.getAssertion());
    if (n != null) {
      elmConstraint.appendChild(n);
    } else {
      return null;
    }

    return elmConstraint;
  }

  private nu.xom.Node innerXMLHandler(String xml) {
    if (xml != null) {
      Builder builder = new Builder(new NodeFactory());
      try {
        nu.xom.Document doc = builder.build(xml, null);
        return doc.getRootElement().copy();
      } catch (ValidityException e) {
        e.printStackTrace();
      } catch (ParsingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private nu.xom.Element serializeByNameOrByID(ByNameOrByID byNameOrByIDObj) {
    if (byNameOrByIDObj instanceof ByName) {
      ByName byNameObj = (ByName) byNameOrByIDObj;
      nu.xom.Element elmByName = new nu.xom.Element("ByName");
      elmByName.addAttribute(new Attribute("Name", byNameObj.getByName()));

      for (Constraint c : byNameObj.getPredicates()) {
        nu.xom.Element elmConstaint = this.serializeConstaint(c, "Predicate");
        if (elmConstaint != null)
          elmByName.appendChild(elmConstaint);
      }

      for (Constraint c : byNameObj.getConformanceStatements()) {
        nu.xom.Element elmConstaint = this.serializeConstaint(c, "Constraint");
        if (elmConstaint != null)
          elmByName.appendChild(elmConstaint);
      }

      return elmByName;
    } else if (byNameOrByIDObj instanceof ByID) {
      ByID byIDObj = (ByID) byNameOrByIDObj;
      nu.xom.Element elmByID = new nu.xom.Element("ByID");
      elmByID.addAttribute(new Attribute("ID", byIDObj.getByID()));

      for (Constraint c : byIDObj.getConformanceStatements()) {
        nu.xom.Element elmConstaint = this.serializeConstaint(c, "Constraint");
        if (elmConstaint != null)
          elmByID.appendChild(elmConstaint);
      }

      for (Constraint c : byIDObj.getPredicates()) {
        nu.xom.Element elmConstaint = this.serializeConstaint(c, "Predicate");
        if (elmConstaint != null)
          elmByID.appendChild(elmConstaint);
      }

      return elmByID;
    }

    return null;
  }

  private nu.xom.Element serializeMain(nu.xom.Element e, Constraints predicates,
      Constraints conformanceStatements) {
    nu.xom.Element predicates_Elm = new nu.xom.Element("Predicates");

    nu.xom.Element predicates_dataType_Elm = new nu.xom.Element("Datatype");
    for (ByNameOrByID byNameOrByIDObj : predicates.getDatatypes().getByNameOrByIDs()) {
      nu.xom.Element dataTypeConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (dataTypeConstaint != null)
        predicates_dataType_Elm.appendChild(dataTypeConstaint);
    }
    predicates_Elm.appendChild(predicates_dataType_Elm);

    nu.xom.Element predicates_segment_Elm = new nu.xom.Element("Segment");
    for (ByNameOrByID byNameOrByIDObj : predicates.getSegments().getByNameOrByIDs()) {
      nu.xom.Element segmentConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (segmentConstaint != null)
        predicates_segment_Elm.appendChild(segmentConstaint);
    }
    predicates_Elm.appendChild(predicates_segment_Elm);

    nu.xom.Element predicates_group_Elm = new nu.xom.Element("Group");
    for (ByNameOrByID byNameOrByIDObj : predicates.getGroups().getByNameOrByIDs()) {
      nu.xom.Element groupConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (groupConstaint != null)
        predicates_group_Elm.appendChild(groupConstaint);
    }
    predicates_Elm.appendChild(predicates_group_Elm);

    nu.xom.Element predicates_message_Elm = new nu.xom.Element("Message");
    for (ByNameOrByID byNameOrByIDObj : predicates.getMessages().getByNameOrByIDs()) {
      nu.xom.Element messageConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (messageConstaint != null)
        predicates_message_Elm.appendChild(messageConstaint);
    }
    predicates_Elm.appendChild(predicates_message_Elm);

    e.appendChild(predicates_Elm);

    nu.xom.Element constraints_Elm = new nu.xom.Element("Constraints");

    nu.xom.Element constraints_dataType_Elm = new nu.xom.Element("Datatype");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getDatatypes().getByNameOrByIDs()) {
      nu.xom.Element dataTypeConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (dataTypeConstaint != null)
        constraints_dataType_Elm.appendChild(dataTypeConstaint);
    }
    constraints_Elm.appendChild(constraints_dataType_Elm);

    nu.xom.Element constraints_segment_Elm = new nu.xom.Element("Segment");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getSegments().getByNameOrByIDs()) {
      nu.xom.Element segmentConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (segmentConstaint != null)
        constraints_segment_Elm.appendChild(segmentConstaint);
    }
    constraints_Elm.appendChild(constraints_segment_Elm);

    nu.xom.Element constraints_group_Elm = new nu.xom.Element("Group");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getGroups().getByNameOrByIDs()) {
      nu.xom.Element groupConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (groupConstaint != null)
        constraints_group_Elm.appendChild(groupConstaint);
    }
    constraints_Elm.appendChild(constraints_group_Elm);

    nu.xom.Element constraints_message_Elm = new nu.xom.Element("Message");
    for (ByNameOrByID byNameOrByIDObj : conformanceStatements.getMessages().getByNameOrByIDs()) {
      nu.xom.Element messageConstaint = this.serializeByNameOrByID(byNameOrByIDObj);
      if (messageConstaint != null)
        constraints_message_Elm.appendChild(messageConstaint);
    }
    constraints_Elm.appendChild(constraints_message_Elm);
    e.appendChild(constraints_Elm);

    return e;
  }

  private Set<ByNameOrByID> findAllPredicatesForGroup(Group g, Set<ByNameOrByID> byNameOrByIDs) {
    ByID byID = new ByID();
    byID.setByID(g.getId());
    if (g.getPredicates().size() > 0) {
      byID.setPredicates(g.getPredicates());
      byNameOrByIDs.add(byID);
    }

    for (SegmentRefOrGroup sog : g.getChildren()) {
      if (sog instanceof Group) {
        byNameOrByIDs = findAllPredicatesForGroup((Group) sog, byNameOrByIDs);
      }
    }

    return byNameOrByIDs;

  }

  private Constraints findAllPredicates(Profile profile, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap, Map<String, Table> tablesMap) {
    Constraints constraints = new Constraints();
    Context dtContext = new Context();
    Context sContext = new Context();
    Context gContext = new Context();
    Context mContext = new Context();

    Set<ByNameOrByID> byNameOrByIDs = new HashSet<ByNameOrByID>();
    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {
      ByID byID = new ByID();
      byID.setByID(m.getId());
      if (m.getPredicates().size() > 0) {
        byID.setPredicates(m.getPredicates());
        byNameOrByIDs.add(byID);
      }
    }
    mContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (Message m : profile.getMessages().getChildren()) {

      for (SegmentRefOrGroup sog : m.getChildren()) {
        if (sog instanceof Group) {
          byNameOrByIDs = findAllPredicatesForGroup((Group) sog, byNameOrByIDs);
        }
      }
    }
    gContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      ByID byID = new ByID();
      byID.setByID(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"));
      if (s.getPredicates().size() > 0) {
        byID.setPredicates(s.getPredicates());
        byNameOrByIDs.add(byID);
      }
    }

    sContext.setByNameOrByIDs(byNameOrByIDs);

    byNameOrByIDs = new HashSet<ByNameOrByID>();
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      ByID byID = new ByID();
      byID.setByID(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"));
      if (d.getPredicates().size() > 0) {
        byID.setPredicates(d.getPredicates());
        byNameOrByIDs.add(byID);
      }
    }
    dtContext.setByNameOrByIDs(byNameOrByIDs);

    constraints.setGroups(gContext);
    constraints.setDatatypes(dtContext);
    constraints.setSegments(sContext);
    constraints.setMessages(mContext);
    return constraints;
  }

  public String serializeTableXML(Profile profile, DocumentMetaData metadata,
      Map<String, Table> tablesMap) {

    nu.xom.Element elmTableLibrary = new nu.xom.Element("ValueSetLibrary");

    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/ValueSets.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    elmTableLibrary.addAttribute(schemaDecl);
    elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier", profile.getId()));

    nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
    if (metadata == null) {
      elmMetaData.addAttribute(new Attribute("Name", "Vocab for " + "Profile"));
      elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
      elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
      elmMetaData.addAttribute(new Attribute("Date", ""));
    } else {
      elmMetaData.addAttribute(new Attribute("Name", !this.str(metadata.getTitle()).equals("")
          ? this.str(metadata.getTitle()) : "No Title Info"));
      elmMetaData.addAttribute(new Attribute("OrgName", !this.str(metadata.getOrgName()).equals("")
          ? this.str(metadata.getOrgName()) : "No Org Info"));
      elmMetaData.addAttribute(new Attribute("Version", !this.str(metadata.getVersion()).equals("")
          ? this.str(metadata.getVersion()) : "No Version Info"));
      elmMetaData.addAttribute(new Attribute("Date", "No Date Info"));

      if (profile.getMetaData().getSpecificationName() != null
          && !profile.getMetaData().getSpecificationName().equals(""))
        elmMetaData.addAttribute(new Attribute("SpecificationName",
            this.str(profile.getMetaData().getSpecificationName())));
      if (profile.getMetaData().getStatus() != null
          && !profile.getMetaData().getStatus().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Status", this.str(profile.getMetaData().getStatus())));
      if (profile.getMetaData().getTopics() != null
          && !profile.getMetaData().getTopics().equals(""))
        elmMetaData
            .addAttribute(new Attribute("Topics", this.str(profile.getMetaData().getTopics())));
    }

    nu.xom.Element elmNoValidation = new nu.xom.Element("NoValidation");
    HashMap<String, nu.xom.Element> valueSetDefinitionsMap = new HashMap<String, nu.xom.Element>();

    for (String key : tablesMap.keySet()) {
      Table t = tablesMap.get(key);

      if (t != null) {
        if (t.getCodes() == null || t.getCodes().size() == 0
            || (t.getCodes().size() == 1 && t.getCodes().get(0).getValue().equals("..."))) {
          nu.xom.Element elmBindingIdentifier = new nu.xom.Element("BindingIdentifier");
          if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
            if (t.getBindingIdentifier().startsWith("0396")
                || t.getBindingIdentifier().startsWith("HL70396")) {
              elmBindingIdentifier.appendChild(this.str(t.getBindingIdentifier()));
            } else {
              elmBindingIdentifier.appendChild(this
                  .str(t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-")));
            }
          } else {
            elmBindingIdentifier.appendChild(this.str(t.getBindingIdentifier()));
          }
          elmNoValidation.appendChild(elmBindingIdentifier);
        }

        nu.xom.Element elmValueSetDefinition = new nu.xom.Element("ValueSetDefinition");
        if (t.getHl7Version() != null && !t.getHl7Version().equals("")) {
          if (t.getBindingIdentifier().startsWith("0396")
              || t.getBindingIdentifier().startsWith("HL70396")) {
            elmValueSetDefinition.addAttribute(
                new Attribute("BindingIdentifier", this.str(t.getBindingIdentifier())));
          } else {
            elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", this
                .str(t.getBindingIdentifier() + "_" + t.getHl7Version().replaceAll("\\.", "-"))));
          }

        } else {
          elmValueSetDefinition
              .addAttribute(new Attribute("BindingIdentifier", this.str(t.getBindingIdentifier())));
        }

        elmValueSetDefinition.addAttribute(new Attribute("Name", this.str(t.getName())));
        if (t.getDescription() != null && !t.getDescription().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Description", this.str(t.getDescription())));
        if (t.getVersion() != null && !t.getVersion().equals(""))
          elmValueSetDefinition.addAttribute(new Attribute("Version", this.str(t.getVersion())));
        if (t.getOid() != null && !t.getOid().equals(""))
          elmValueSetDefinition.addAttribute(new Attribute("Oid", this.str(t.getOid())));
        if (t.getStability() != null && !t.getStability().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Stability", this.str(t.getStability().value())));
        if (t.getExtensibility() != null && !t.getExtensibility().equals(""))
          elmValueSetDefinition
              .addAttribute(new Attribute("Extensibility", this.str(t.getExtensibility().value())));
        if (t.getContentDefinition() != null && !t.getContentDefinition().equals(""))
          elmValueSetDefinition.addAttribute(
              new Attribute("ContentDefinition", this.str(t.getContentDefinition().value())));

        nu.xom.Element elmValueSetDefinitions = null;
        if (t.getGroup() != null && !t.getGroup().equals("")) {
          elmValueSetDefinitions = valueSetDefinitionsMap.get(t.getGroup());
        } else {
          elmValueSetDefinitions = valueSetDefinitionsMap.get("NOGroup");
        }
        if (elmValueSetDefinitions == null) {
          elmValueSetDefinitions = new nu.xom.Element("ValueSetDefinitions");

          if (t.getGroup() != null && !t.getGroup().equals("")) {
            elmValueSetDefinitions.addAttribute(new Attribute("Group", t.getGroup()));
            elmValueSetDefinitions.addAttribute(new Attribute("Order", t.getOrder() + ""));
            valueSetDefinitionsMap.put(t.getGroup(), elmValueSetDefinitions);
          } else {
            elmValueSetDefinitions.addAttribute(new Attribute("Group", "NOGroup"));
            elmValueSetDefinitions.addAttribute(new Attribute("Order", "0"));
            valueSetDefinitionsMap.put("NOGroup", elmValueSetDefinitions);
          }

        }
        elmValueSetDefinitions.appendChild(elmValueSetDefinition);

        if (t.getCodes() != null) {
          for (Code c : t.getCodes()) {
            nu.xom.Element elmValueElement = new nu.xom.Element("ValueElement");
            elmValueElement.addAttribute(new Attribute("Value", this.str(c.getValue())));
            elmValueElement.addAttribute(new Attribute("DisplayName", this.str(c.getLabel() + "")));
            if (c.getCodeSystem() != null && !c.getCodeSystem().equals(""))
              elmValueElement
                  .addAttribute(new Attribute("CodeSystem", this.str(c.getCodeSystem())));
            if (c.getCodeSystemVersion() != null && !c.getCodeSystemVersion().equals(""))
              elmValueElement.addAttribute(
                  new Attribute("CodeSystemVersion", this.str(c.getCodeSystemVersion())));
            if (c.getCodeUsage() != null && !c.getCodeUsage().equals(""))
              elmValueElement.addAttribute(new Attribute("Usage", this.str(c.getCodeUsage())));
            if (c.getComments() != null && !c.getComments().equals(""))
              elmValueElement.addAttribute(new Attribute("Comments", this.str(c.getComments())));
            elmValueSetDefinition.appendChild(elmValueElement);
          }
        }
      }
    }

    elmTableLibrary.appendChild(elmMetaData);
    elmTableLibrary.appendChild(elmNoValidation);

    for (nu.xom.Element elmValueSetDefinitions : valueSetDefinitionsMap.values()) {
      elmTableLibrary.appendChild(elmValueSetDefinitions);
    }

    return elmTableLibrary.toXML();
  }

  public nu.xom.Document serializeProfileToDoc(Profile profile, DocumentMetaData metadata,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      Map<String, Table> tablesMap) {

    nu.xom.Element e = new nu.xom.Element("ConformanceProfile");
    this.serializeProfileMetaData(e, profile, metadata);

    nu.xom.Element ms = new nu.xom.Element("Messages");
    for (Message m : profile.getMessages().getChildren()) {
      ms.appendChild(this.serializeMessage(m, segmentsMap));
    }
    e.appendChild(ms);

    nu.xom.Element ss = new nu.xom.Element("Segments");
    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      ss.appendChild(this.serializeSegment(s, tablesMap, datatypesMap));
    }
    e.appendChild(ss);

    nu.xom.Element ds = new nu.xom.Element("Datatypes");
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      ds.appendChild(this.serializeDatatypeForValidation(d, tablesMap, datatypesMap));
    }
    e.appendChild(ds);

    nu.xom.Document doc = new nu.xom.Document(e);

    return doc;
  }

  private nu.xom.Element serializeDatatypeForValidation(Datatype d, Map<String, Table> tablesMap,
      Map<String, Datatype> datatypesMap) {
    nu.xom.Element elmDatatype = new nu.xom.Element("Datatype");
    elmDatatype.addAttribute(new Attribute("ID",
        this.str(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"))));
    elmDatatype.addAttribute(new Attribute("Name", this.str(d.getName())));
    elmDatatype.addAttribute(new Attribute("Label", this.str(d.getLabel())));
    if (d.getDescription() == null || d.getDescription().equals("")) {
      elmDatatype.addAttribute(new Attribute("Description", "NoDesc"));
    } else {
      elmDatatype.addAttribute(new Attribute("Description", this.str(d.getDescription())));
    }


    if (d.getComponents() != null) {

      Map<Integer, Component> components = new HashMap<Integer, Component>();

      for (Component c : d.getComponents()) {
        components.put(c.getPosition(), c);
      }

      for (int i = 1; i < components.size() + 1; i++) {
        Component c = components.get(i);
        Datatype componentDatatype = datatypesMap.get(c.getDatatype().getId());
        nu.xom.Element elmComponent = new nu.xom.Element("Component");
        elmComponent.addAttribute(new Attribute("Name", this.str(c.getName())));
        elmComponent.addAttribute(new Attribute("Usage", this.str(c.getUsage().toString())));
        elmComponent.addAttribute(new Attribute("Datatype", this.str(componentDatatype.getLabel()
            + "_" + componentDatatype.getHl7Version().replaceAll("\\.", "-"))));
        elmComponent.addAttribute(new Attribute("MinLength", this.str(c.getMinLength())));
        elmComponent.addAttribute(new Attribute("MaxLength", this.str(c.getMaxLength())));
        elmComponent.addAttribute(new Attribute("ConfLength", this.str(c.getConfLength())));

        List<ValueSetBinding> bindings = findBinding(d.getValueSetBindings(), c.getPosition());
        if (bindings.size() > 0) {
          String bindingString = "";
          String bindingStrength = null;
          String bindingLocation = null;

          for (ValueSetBinding binding : bindings) {
            Table table = tablesMap.get(binding.getTableId());
            bindingStrength = binding.getBindingStrength().toString();
            bindingLocation = binding.getBindingLocation();
            if (table != null && table.getBindingIdentifier() != null
                && !table.getBindingIdentifier().equals("")) {
              if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
                if (table.getBindingIdentifier().startsWith("0396")
                    || table.getBindingIdentifier().startsWith("HL70396")) {
                  bindingString = bindingString + table.getBindingIdentifier() + ":";
                } else {
                  bindingString = bindingString + table.getBindingIdentifier() + "_"
                      + table.getHl7Version().replaceAll("\\.", "-") + ":";
                }
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              }
            }
          }

          IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
          DTComponent dtComponent = new DTComponent();
          dtComponent.setDtName(componentDatatype.getName());
          dtComponent.setLocation(c.getPosition());
          if (config.getValueSetAllowedDTs().contains(componentDatatype.getName())
              || config.getValueSetAllowedComponents().contains(dtComponent)) {
            if (!bindingString.equals(""))
              elmComponent.addAttribute(
                  new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
            if (bindingStrength != null)
              elmComponent.addAttribute(new Attribute("BindingStrength", bindingStrength));

            if (componentDatatype != null && componentDatatype.getComponents() != null
                && componentDatatype.getComponents().size() > 0) {
              if (bindingLocation != null && !bindingLocation.equals("")) {
                bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
                elmComponent.addAttribute(new Attribute("BindingLocation", bindingLocation));
              } else {
                elmComponent.addAttribute(new Attribute("BindingLocation", "1"));
              }
            }
          }
        }

        if (c.isHide())
          elmComponent.addAttribute(new Attribute("Hide", "true"));

        elmDatatype.appendChild(elmComponent);
      }
    }
    return elmDatatype;
  }

  private nu.xom.Element serializeSegment(Segment s, Map<String, Table> tablesMap,
      Map<String, Datatype> datatypesMap) {
    nu.xom.Element elmSegment = new nu.xom.Element("Segment");
    elmSegment.addAttribute(
        new Attribute("ID", s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-")));
    elmSegment.addAttribute(new Attribute("Name", this.str(s.getName())));
    elmSegment.addAttribute(new Attribute("Label", this.str(s.getLabel())));
    if (s.getDescription() == null || s.getDescription().equals("")) {
      elmSegment.addAttribute(new Attribute("Description", "NoDesc"));
    } else {
      elmSegment.addAttribute(new Attribute("Description", this.str(s.getDescription())));
    }

    if (s.getName().equals("OBX") || s.getName().equals("MFA") || s.getName().equals("MFE")) {
      String targetPosition = null;
      String reference = null;
      String secondReference = null;
      String referenceTableId = null;
      HashMap<String, Datatype> dm = new HashMap<String, Datatype>();
      HashMap<String, Datatype> dm2nd = new HashMap<String, Datatype>();

      if (s.getName().equals("OBX")) {
        targetPosition = "5";
        reference = "2";
      }

      if (s.getName().equals("MFA")) {
        targetPosition = "5";
        reference = "6";
      }

      if (s.getName().equals("MFE")) {
        targetPosition = "4";
        reference = "5";
      }

      if (s.getCoConstraintsTable() != null
          && s.getCoConstraintsTable().getIfColumnDefinition() != null) {
        if (s.getCoConstraintsTable().getIfColumnDefinition().isPrimitive()) {
          secondReference = s.getCoConstraintsTable().getIfColumnDefinition().getPath();
        } else {
          secondReference = s.getCoConstraintsTable().getIfColumnDefinition().getPath() + ".1";
        }
      }

      referenceTableId = this.findValueSetID(s.getValueSetBindings(), reference);

      if (referenceTableId != null) {
        Table table = tablesMap.get(referenceTableId);
        String hl7Version = null;
        hl7Version = table.getHl7Version();
        if (hl7Version == null)
          hl7Version = s.getHl7Version();

        if (table != null) {
          for (Code c : table.getCodes()) {
            if (c.getValue() != null) {
              Datatype d =
                  this.findHL7DatatypeByNameAndVesion(datatypesMap, c.getValue(), hl7Version);
              if (d != null) {
                dm.put(c.getValue(), d);
              }
            }
          }
        }
        if (s.getDynamicMappingDefinition() != null) {
          for (DynamicMappingItem item : s.getDynamicMappingDefinition().getDynamicMappingItems()) {
            if (item.getFirstReferenceValue() != null && item.getDatatypeId() != null)
              dm.put(item.getFirstReferenceValue(), datatypesMap.get(item.getDatatypeId()));
          }
        }
      }
      if (secondReference != null) {
        for (CoConstraintColumnDefinition definition : s.getCoConstraintsTable()
            .getThenColumnDefinitionList()) {
          if (definition.isdMReference()) {
            List<CoConstraintTHENColumnData> dataList =
                s.getCoConstraintsTable().getThenMapData().get(definition.getId());

            if (dataList != null && s.getCoConstraintsTable().getIfColumnData() != null) {
              for (int i = 0; i < dataList.size(); i++) {
                CoConstraintIFColumnData ref = s.getCoConstraintsTable().getIfColumnData().get(i);
                CoConstraintTHENColumnData data = dataList.get(i);

                if (ref != null && data != null && ref.getValueData() != null
                    && ref.getValueData().getValue() != null && data.getDatatypeId() != null
                    && data.getValueData() != null && data.getValueData().getValue() != null) {
                  dm2nd.put(ref.getValueData().getValue(), datatypesMap.get(data.getDatatypeId()));
                }
              }
            }
          }
        }
      }

      if (dm.size() > 0 || dm2nd.size() > 0) {
        nu.xom.Element elmDynamicMapping = new nu.xom.Element("DynamicMapping");
        nu.xom.Element elmMapping = new nu.xom.Element("Mapping");
        elmMapping.addAttribute(new Attribute("Position", targetPosition));
        elmMapping.addAttribute(new Attribute("Reference", reference));
        if (secondReference != null)
          elmMapping.addAttribute(new Attribute("SecondReference", secondReference));

        for (String key : dm.keySet()) {
          nu.xom.Element elmCase = new nu.xom.Element("Case");
          Datatype d = dm.get(key);
          elmCase.addAttribute(new Attribute("Value", d.getName()));
          elmCase.addAttribute(new Attribute("Datatype",
              d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-")));
          elmMapping.appendChild(elmCase);
        }

        for (String key : dm2nd.keySet()) {
          nu.xom.Element elmCase = new nu.xom.Element("Case");
          Datatype d = dm2nd.get(key);
          if (d != null) {
            elmCase.addAttribute(new Attribute("Value", d.getName()));
            elmCase.addAttribute(new Attribute("SecondValue", key));
            elmCase.addAttribute(new Attribute("Datatype",
                d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-")));
            elmMapping.appendChild(elmCase);
          }

        }
        elmDynamicMapping.appendChild(elmMapping);
        elmSegment.appendChild(elmDynamicMapping);
      }
    }

    Map<Integer, Field> fields = new HashMap<Integer, Field>();

    for (Field f : s.getFields()) {
      fields.put(f.getPosition(), f);
    }

    for (int i = 1; i < fields.size() + 1; i++) {

      Field f = fields.get(i);
      Datatype d = datatypesMap.get(f.getDatatype().getId());
      nu.xom.Element elmField = new nu.xom.Element("Field");
      elmField.addAttribute(new Attribute("Name", this.str(f.getName())));
      elmField.addAttribute(new Attribute("Usage", this.str(f.getUsage().toString())));
      elmField.addAttribute(new Attribute("Datatype",
          this.str(d.getLabel() + "_" + d.getHl7Version().replaceAll("\\.", "-"))));
      elmField.addAttribute(new Attribute("MinLength", this.str(f.getMinLength())));
      elmField.addAttribute(new Attribute("MaxLength", this.str(f.getMaxLength())));
      elmField.addAttribute(new Attribute("ConfLength", this.str(f.getConfLength())));

      if (f.getConfLength() != null && !f.getConfLength().equals(""))
        elmField.addAttribute(new Attribute("ConfLength", this.str(f.getConfLength())));

      List<ValueSetBinding> bindings = findBinding(s.getValueSetBindings(), f.getPosition());
      if (bindings.size() > 0) {
        String bindingString = "";
        String bindingStrength = null;
        String bindingLocation = null;

        for (ValueSetBinding binding : bindings) {
          Table table = tablesMap.get(binding.getTableId());
          bindingStrength = binding.getBindingStrength().toString();
          bindingLocation = binding.getBindingLocation();
          if (table != null && table.getBindingIdentifier() != null
              && !table.getBindingIdentifier().equals("")) {
            if (table.getHl7Version() != null && !table.getHl7Version().equals("")) {
              if (table.getBindingIdentifier().startsWith("0396")
                  || table.getBindingIdentifier().startsWith("HL70396")) {
                bindingString = bindingString + table.getBindingIdentifier() + ":";
              } else {
                bindingString = bindingString + table.getBindingIdentifier() + "_"
                    + table.getHl7Version().replaceAll("\\.", "-") + ":";
              }
            } else {
              bindingString = bindingString + table.getBindingIdentifier() + ":";
            }
          }
        }

        IGDocumentConfiguration config = new XMLConfig().igDocumentConfig();
        if (config.getValueSetAllowedDTs().contains(d.getName())) {
          if (!bindingString.equals(""))
            elmField.addAttribute(
                new Attribute("Binding", bindingString.substring(0, bindingString.length() - 1)));
          if (bindingStrength != null)
            elmField.addAttribute(new Attribute("BindingStrength", bindingStrength));

          if (d != null && d.getComponents() != null && d.getComponents().size() > 0) {
            if (bindingLocation != null && !bindingLocation.equals("")) {
              bindingLocation = bindingLocation.replaceAll("\\s+", "").replaceAll("or", ":");
              elmField.addAttribute(new Attribute("BindingLocation", bindingLocation));
            } else {
              elmField.addAttribute(new Attribute("BindingLocation", "1"));
            }
          }
        }
      }

      if (f.isHide())
        elmField.addAttribute(new Attribute("Hide", "true"));
      elmField.addAttribute(new Attribute("Min", "" + f.getMin()));
      elmField.addAttribute(new Attribute("Max", "" + f.getMax()));
      if (f.getItemNo() != null && !f.getItemNo().equals(""))
        elmField.addAttribute(new Attribute("ItemNo", this.str(f.getItemNo())));
      elmSegment.appendChild(elmField);
    }

    return elmSegment;
  }

  private Datatype findHL7DatatypeByNameAndVesion(Map<String, Datatype> datatypesMap, String value,
      String hl7Version) {
    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      if (d.getName().equals(value) && d.getHl7Version().equals(hl7Version)
          && d.getScope().toString().equals("HL7STANDARD"))
        return d;
    }
    return null;
  }

  private String findValueSetID(List<ValueSetOrSingleCodeBinding> valueSetBindings,
      String referenceLocation) {
    for (ValueSetOrSingleCodeBinding vsb : valueSetBindings) {
      if (vsb.getLocation().equals(referenceLocation))
        return vsb.getTableId();
    }
    return null;
  }

  private List<ValueSetBinding> findBinding(List<ValueSetOrSingleCodeBinding> valueSetBindings,
      Integer position) {
    List<ValueSetBinding> result = new ArrayList<ValueSetBinding>();
    if (valueSetBindings != null && position != null) {
      for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;

          if (valueSetBinding.getLocation().equals("" + position)) {
            result.add(valueSetBinding);
          }
        }
      }
    }
    return result;
  }

  private nu.xom.Element serializeMessage(Message m, Map<String, Segment> segmentsMap) {
    nu.xom.Element elmMessage = new nu.xom.Element("Message");
    elmMessage.addAttribute(new Attribute("ID", m.getId()));
    if (m.getIdentifier() != null && !m.getIdentifier().equals(""))
      elmMessage.addAttribute(new Attribute("Identifier", this.str(m.getIdentifier())));
    if (m.getName() != null && !m.getName().equals(""))
      elmMessage.addAttribute(new Attribute("Name", this.str(m.getName())));
    elmMessage.addAttribute(new Attribute("Type", this.str(m.getMessageType())));
    elmMessage.addAttribute(new Attribute("Event", this.str(m.getEvent())));
    elmMessage.addAttribute(new Attribute("StructID", this.str(m.getStructID())));
    if (m.getDescription() != null && !m.getDescription().equals(""))
      elmMessage.addAttribute(new Attribute("Description", this.str(m.getDescription())));

    Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

    for (SegmentRefOrGroup segmentRefOrGroup : m.getChildren()) {
      segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
    }

    for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
      SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
      if (segmentRefOrGroup instanceof SegmentRef) {
        elmMessage.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segmentsMap));
      } else if (segmentRefOrGroup instanceof Group) {
        elmMessage.appendChild(serializeGroup((Group) segmentRefOrGroup, segmentsMap));
      }
    }

    return elmMessage;
  }

  private nu.xom.Element serializeGroup(Group group, Map<String, Segment> segmentsMap) {
    nu.xom.Element elmGroup = new nu.xom.Element("Group");
    elmGroup.addAttribute(new Attribute("ID", this.str(group.getId())));
    elmGroup.addAttribute(new Attribute("Name", this.str(group.getName())));
    elmGroup.addAttribute(new Attribute("Usage", this.str(group.getUsage().value())));
    elmGroup.addAttribute(new Attribute("Min", this.str(group.getMin() + "")));
    elmGroup.addAttribute(new Attribute("Max", this.str(group.getMax())));

    Map<Integer, SegmentRefOrGroup> segmentRefOrGroups = new HashMap<Integer, SegmentRefOrGroup>();

    for (SegmentRefOrGroup segmentRefOrGroup : group.getChildren()) {
      segmentRefOrGroups.put(segmentRefOrGroup.getPosition(), segmentRefOrGroup);
    }

    for (int i = 1; i < segmentRefOrGroups.size() + 1; i++) {
      SegmentRefOrGroup segmentRefOrGroup = segmentRefOrGroups.get(i);
      if (segmentRefOrGroup instanceof SegmentRef) {
        elmGroup.appendChild(serializeSegmentRef((SegmentRef) segmentRefOrGroup, segmentsMap));
      } else if (segmentRefOrGroup instanceof Group) {
        elmGroup.appendChild(serializeGroup((Group) segmentRefOrGroup, segmentsMap));
      }
    }

    return elmGroup;
  }

  private nu.xom.Element serializeSegmentRef(SegmentRef segmentRef,
      Map<String, Segment> segmentsMap) {
    Segment s = segmentsMap.get(segmentRef.getRef().getId());
    nu.xom.Element elmSegment = new nu.xom.Element("Segment");
    elmSegment.addAttribute(new Attribute("Ref",
        this.str(s.getLabel() + "_" + s.getHl7Version().replaceAll("\\.", "-"))));
    elmSegment.addAttribute(new Attribute("Usage", this.str(segmentRef.getUsage().value())));
    elmSegment.addAttribute(new Attribute("Min", this.str(segmentRef.getMin() + "")));
    elmSegment.addAttribute(new Attribute("Max", this.str(segmentRef.getMax())));
    return elmSegment;
  }

  private String str(String value) {
    return value != null ? value : "";
  }

  private void serializeProfileMetaData(nu.xom.Element e, Profile profile,
      DocumentMetaData igMetaData) {
    Attribute schemaDecl = new Attribute("noNamespaceSchemaLocation",
        "https://raw.githubusercontent.com/Jungyubw/NIST_healthcare_hl7_v2_profile_schema/master/Schema/NIST%20Validation%20Schema/Profile.xsd");
    schemaDecl.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    e.addAttribute(schemaDecl);

    e.addAttribute(new Attribute("ID", profile.getId()));
    ProfileMetaData metaData = profile.getMetaData();
    if (metaData.getType() != null && !metaData.getType().equals(""))
      e.addAttribute(new Attribute("Type", this.str(metaData.getType())));
    if (metaData.getHl7Version() != null && !metaData.getHl7Version().equals(""))
      e.addAttribute(new Attribute("HL7Version", this.str(metaData.getHl7Version())));
    if (metaData.getSchemaVersion() != null && !metaData.getSchemaVersion().equals(""))
      e.addAttribute(new Attribute("SchemaVersion", this.str(metaData.getSchemaVersion())));

    nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
    elmMetaData.addAttribute(new Attribute("Name", !this.str(igMetaData.getTitle()).equals("")
        ? this.str(igMetaData.getTitle()) : "No Title Info"));
    elmMetaData.addAttribute(new Attribute("OrgName", !this.str(igMetaData.getOrgName()).equals("")
        ? this.str(igMetaData.getOrgName()) : "No Org Info"));
    elmMetaData.addAttribute(new Attribute("Version", !this.str(igMetaData.getVersion()).equals("")
        ? this.str(igMetaData.getVersion()) : "No Version Info"));
    elmMetaData.addAttribute(new Attribute("Date", !this.str(igMetaData.getDate()).equals("")
        ? this.str(igMetaData.getDate()) : "No Date Info"));

    if (metaData.getSpecificationName() != null && !metaData.getSpecificationName().equals(""))
      elmMetaData.addAttribute(
          new Attribute("SpecificationName", this.str(metaData.getSpecificationName())));
    if (metaData.getStatus() != null && !metaData.getStatus().equals(""))
      elmMetaData.addAttribute(new Attribute("Status", this.str(metaData.getStatus())));
    if (metaData.getTopics() != null && !metaData.getTopics().equals(""))
      elmMetaData.addAttribute(new Attribute("Topics", this.str(metaData.getTopics())));

    e.appendChild(elmMetaData);
  }

  private void generateProfileIS(ZipOutputStream out, String profileXML) throws IOException {
    byte[] buf = new byte[1024];
    out.putNextEntry(new ZipEntry("Profile.xml"));
    InputStream inProfile = IOUtils.toInputStream(profileXML);
    int lenTP;
    while ((lenTP = inProfile.read(buf)) > 0) {
      out.write(buf, 0, lenTP);
    }
    out.closeEntry();
    inProfile.close();
  }

  private void generateValueSetIS(ZipOutputStream out, String valueSetXML) throws IOException {
    byte[] buf = new byte[1024];
    out.putNextEntry(new ZipEntry("ValueSets.xml"));
    InputStream inValueSet = IOUtils.toInputStream(valueSetXML);
    int lenTP;
    while ((lenTP = inValueSet.read(buf)) > 0) {
      out.write(buf, 0, lenTP);
    }
    out.closeEntry();
    inValueSet.close();
  }

  private void generateConstraintsIS(ZipOutputStream out, String constraintsXML)
      throws IOException {
    byte[] buf = new byte[1024];
    out.putNextEntry(new ZipEntry("Constraints.xml"));
    InputStream inConstraints = IOUtils.toInputStream(constraintsXML);
    int lenTP;
    while ((lenTP = inConstraints.read(buf)) > 0) {
      out.write(buf, 0, lenTP);
    }
    out.closeEntry();
    inConstraints.close();
  }

  private void normalizeProfile(Profile profile, Map<String, Segment> segmentsMap,
      Map<String, Datatype> datatypesMap) throws CloneNotSupportedException {
    Map<String, Datatype> toBeAddedDTs = new HashMap<String, Datatype>();
    Map<String, Segment> toBeAddedSegs = new HashMap<String, Segment>();

    for (String key : datatypesMap.keySet()) {
      Datatype d = datatypesMap.get(key);
      for (ValueSetOrSingleCodeBinding binding : d.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
          List<ValueSetBinding> valueSetBindings =
              findvalueSetBinding(d.getValueSetBindings(), valueSetBinding.getLocation());
          List<String> pathList =
              new LinkedList<String>(Arrays.asList(valueSetBinding.getLocation().split("\\.")));

          if (pathList.size() > 1) {
            Component c = d.findComponentByPosition(Integer.parseInt(pathList.remove(0)));

            Datatype childD = datatypesMap.get(c.getDatatype().getId());
            if (childD == null)
              childD = toBeAddedDTs.get(c.getDatatype().getId());
            Datatype copyD = childD.clone();

            int randumNum = new SecureRandom().nextInt(100000);
            copyD.setId(d.getId() + "_A" + randumNum);
            String ext = d.getExt();
            if (ext == null)
              ext = "";
            copyD.setExt(ext + "_A" + randumNum);
            toBeAddedDTs.put(copyD.getId(), copyD);
            c.getDatatype().setId(copyD.getId());

            visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
          }
        }
      }
    }

    for (String key : segmentsMap.keySet()) {
      Segment s = segmentsMap.get(key);
      for (ValueSetOrSingleCodeBinding binding : s.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
          List<ValueSetBinding> valueSetBindings =
              findvalueSetBinding(s.getValueSetBindings(), valueSetBinding.getLocation());
          List<String> pathList =
              new LinkedList<String>(Arrays.asList(valueSetBinding.getLocation().split("\\.")));

          if (pathList.size() > 1) {
            Field f = s.findFieldByPosition(Integer.parseInt(pathList.remove(0)));

            Datatype d = datatypesMap.get(f.getDatatype().getId());
            if (d == null)
              d = toBeAddedDTs.get(f.getDatatype().getId());
            Datatype copyD = d.clone();

            int randumNum = new SecureRandom().nextInt(100000);
            copyD.setId(d.getId() + "_A" + randumNum);
            String ext = d.getExt();
            if (ext == null)
              ext = "";
            copyD.setExt(ext + "_A" + randumNum);
            toBeAddedDTs.put(copyD.getId(), copyD);
            f.getDatatype().setId(copyD.getId());

            visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
          }

        }
      }

    }

    for (Message m : profile.getMessages().getChildren()) {
      for (ValueSetOrSingleCodeBinding binding : m.getValueSetBindings()) {
        if (binding instanceof ValueSetBinding) {
          ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
          List<ValueSetBinding> valueSetBindings =
              findvalueSetBinding(m.getValueSetBindings(), valueSetBinding.getLocation());
          List<String> pathList =
              new LinkedList<String>(Arrays.asList(valueSetBinding.getLocation().split("\\.")));
          SegmentRefOrGroup child = m.findChildByPosition(Integer.parseInt(pathList.remove(0)));
          visitGroupOrSegmentRef(pathList, child, segmentsMap, datatypesMap, valueSetBindings,
              toBeAddedDTs, toBeAddedSegs);
        }
      }
    }
    for (String key : toBeAddedDTs.keySet()) {
      datatypesMap.put(key, toBeAddedDTs.get(key));
    }
    for (String key : toBeAddedSegs.keySet()) {
      segmentsMap.put(key, toBeAddedSegs.get(key));
    }
  }

  private List<ValueSetBinding> findvalueSetBinding(
      List<ValueSetOrSingleCodeBinding> valueSetBindings, String location) {
    List<ValueSetBinding> resutls = new ArrayList<ValueSetBinding>();
    for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
      if (binding instanceof ValueSetBinding) {
        ValueSetBinding valueSetBinding = (ValueSetBinding) binding;
        if (valueSetBinding.getLocation().equals(location))
          resutls.add(valueSetBinding);
      }
    }
    return resutls;
  }

  private void visitDatatype(List<String> pathList, Datatype datatype,
      Map<String, Datatype> datatypesMap, List<ValueSetBinding> valueSetBindings,
      Map<String, Datatype> toBeAddedDTs) throws CloneNotSupportedException {
    if (pathList.size() == 1) {
      List<ValueSetBinding> newValueSetBindings = new ArrayList<ValueSetBinding>();
      for (ValueSetBinding binding : valueSetBindings) {
        ValueSetBinding newValueSetBinding = binding.clone();
        newValueSetBinding.setLocation(pathList.get(0));
        newValueSetBindings.add(newValueSetBinding);
      }
      List<ValueSetOrSingleCodeBinding> toBeDeleted =
          this.findToBeDeletedValueSetBindinigsByLocation(datatype.getValueSetBindings(),
              pathList.get(0));

      for (ValueSetOrSingleCodeBinding binding : toBeDeleted) {
        datatype.getValueSetBindings().remove(binding);
      }

      datatype.getValueSetBindings().addAll(newValueSetBindings);

    } else if (pathList.size() > 1) {
      Component c = datatype.findComponentByPosition(Integer.parseInt(pathList.remove(0)));

      Datatype d = datatypesMap.get(c.getDatatype().getId());
      if (d == null)
        d = toBeAddedDTs.get(c.getDatatype().getId());
      Datatype copyD = d.clone();

      int randumNum = new SecureRandom().nextInt(100000);
      copyD.setId(d.getId() + "_A" + randumNum);
      String ext = d.getExt();
      if (ext == null)
        ext = "";
      copyD.setExt(ext + "_A" + randumNum);
      toBeAddedDTs.put(copyD.getId(), copyD);
      c.getDatatype().setId(copyD.getId());
      visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
    }

  }

  private List<ValueSetOrSingleCodeBinding> findToBeDeletedValueSetBindinigsByLocation(
      List<ValueSetOrSingleCodeBinding> valueSetBindings, String location) {

    List<ValueSetOrSingleCodeBinding> toBeDeleted = new ArrayList<ValueSetOrSingleCodeBinding>();

    for (ValueSetOrSingleCodeBinding binding : valueSetBindings) {
      if (binding.getLocation().equals(location)) {
        toBeDeleted.add(binding);
      }
    }

    return toBeDeleted;
  }

  private void visitGroupOrSegmentRef(List<String> pathList, SegmentRefOrGroup srog,
      Map<String, Segment> segmentsMap, Map<String, Datatype> datatypesMap,
      List<ValueSetBinding> valueSetBindings, Map<String, Datatype> toBeAddedDTs,
      Map<String, Segment> toBeAddedSegs) throws CloneNotSupportedException {
    if (srog instanceof Group) {
      Group g = (Group) srog;
      SegmentRefOrGroup child = g.findChildByPosition(Integer.parseInt(pathList.remove(0)));
      visitGroupOrSegmentRef(pathList, child, segmentsMap, datatypesMap, valueSetBindings,
          toBeAddedDTs, toBeAddedSegs);
    } else {
      SegmentRef sr = (SegmentRef) srog;
      Segment s = segmentsMap.get(sr.getRef().getId());
      if (s == null)
        s = toBeAddedSegs.get(sr.getRef().getId());
      Segment copyS = s.clone();
      int randumNum = new SecureRandom().nextInt(100000);
      copyS.setId(s.getId() + "_A" + randumNum);
      String ext = s.getExt();
      if (ext == null)
        ext = "";
      copyS.setExt(ext + "_A" + randumNum);

      if (pathList.size() == 1) {
        List<ValueSetBinding> newValueSetBindings = new ArrayList<ValueSetBinding>();
        for (ValueSetBinding binding : valueSetBindings) {
          ValueSetBinding newValueSetBinding = binding.clone();
          newValueSetBinding.setLocation(pathList.get(0));
          newValueSetBindings.add(newValueSetBinding);
        }
        List<ValueSetOrSingleCodeBinding> toBeDeleted =
            this.findToBeDeletedValueSetBindinigsByLocation(copyS.getValueSetBindings(),
                pathList.get(0));
        for (ValueSetOrSingleCodeBinding binding : toBeDeleted) {
          copyS.getValueSetBindings().remove(binding);
        }
        copyS.getValueSetBindings().addAll(newValueSetBindings);

      } else if (pathList.size() > 1) {
        Field f = copyS.findFieldByPosition(Integer.parseInt(pathList.remove(0)));
        Datatype d = datatypesMap.get(f.getDatatype().getId());
        if (d == null)
          d = toBeAddedDTs.get(f.getDatatype().getId());
        Datatype copyD = d.clone();

        randumNum = new SecureRandom().nextInt(100000);
        copyD.setId(d.getId() + "_A" + randumNum);
        String ext2 = d.getExt();
        if (ext2 == null)
          ext2 = "";
        copyD.setExt(ext2 + "_A" + randumNum);
        toBeAddedDTs.put(copyD.getId(), copyD);
        f.getDatatype().setId(copyD.getId());
        visitDatatype(pathList, copyD, datatypesMap, valueSetBindings, toBeAddedDTs);
      }
      sr.getRef().setId(copyS.getId());
      toBeAddedSegs.put(copyS.getId(), copyS);
    }
  }
}
