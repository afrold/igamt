package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import java.util.HashMap;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Constant.SCOPE;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.Datatype;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.DatatypeLink;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Attribute;
import nu.xom.Element;

public class SerializableDatatypeLibrarySummary extends SerializableSection {

  private HashMap<DatatypeLink, Datatype> datatypeLinkDatatypeMap;
  private SCOPE datatypeLibraryScope;

  public SerializableDatatypeLibrarySummary(String id, String prefix, String position,
      String headerLevel, String title, HashMap<DatatypeLink, Datatype> datatypeLinkDatatypeMap,
      SCOPE datatypeLibraryScope) {
    super(id, prefix, position, headerLevel, title);
    this.datatypeLinkDatatypeMap = datatypeLinkDatatypeMap;
    this.datatypeLibraryScope = datatypeLibraryScope;
  }

  @Override
  public Element serializeElement() throws SerializationException {
    Element datatypeLibrarySummaryElement = new Element("DatatypeLibrarySummary");
    for (DatatypeLink datatypeLink : datatypeLinkDatatypeMap.keySet()) {
      if (datatypeLink != null) {
        Datatype datatype = datatypeLinkDatatypeMap.get(datatypeLink);
        if (datatype != null && datatype.getScope().equals(datatypeLibraryScope)) {
          Element datatypeLibrarySummaryItemElement = new Element("DatatypeLibrarySummaryItem");
          if (datatype.getLabel() != null) {
            datatypeLibrarySummaryItemElement
                .addAttribute(new Attribute("label", datatype.getLabel()));
          }
          if (datatype.getDescription() != null) {
            datatypeLibrarySummaryItemElement
                .addAttribute(new Attribute("description", datatype.getDescription()));
          }
          if (datatype.getHl7versions() != null && !datatype.getHl7versions().isEmpty()) {
            datatypeLibrarySummaryItemElement.addAttribute(new Attribute("compatibilityVersions",
                String.join(",", datatype.getHl7versions())));
          }
          if (datatype.getStatus() != null) {
            datatypeLibrarySummaryItemElement
                .addAttribute(new Attribute("publicationStatus", datatype.getStatus().name()));
          }
          datatypeLibrarySummaryItemElement.addAttribute(new Attribute("publicationVersion",
              String.valueOf(datatype.getPublicationVersion())));
          if (datatype.getPurposeAndUse() != null) {
            datatypeLibrarySummaryItemElement
                .addAttribute(new Attribute("purposeAndUse", datatype.getPurposeAndUse()));
          }
          datatypeLibrarySummaryElement.appendChild(datatypeLibrarySummaryItemElement);
        }
      }
    }
    Element sectionElement = super.getSectionElement();
    sectionElement.appendChild(datatypeLibrarySummaryElement);
    return sectionElement;
  }

}
