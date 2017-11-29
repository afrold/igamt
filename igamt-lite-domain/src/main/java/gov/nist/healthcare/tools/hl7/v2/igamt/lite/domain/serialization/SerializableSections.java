package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization.exception.SerializationException;
import nu.xom.Element;

import java.util.*;

/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 * <p>
 * Created by Maxence Lefort on 12/7/16.
 */
public class SerializableSections extends SerializableElement {

    private List<SerializableSection> serializableSections;
    private Element rootSections;

    public void addSection(SerializableSection serializableSection){
        this.serializableSections.add(serializableSection);
    }

    public SerializableSections() {
        this.serializableSections = new ArrayList<>();
        this.rootSections = new Element("Sections");
    }

    public Element getRootSections() {
        return rootSections;
    }

    @Override
    public Element serializeElement() throws SerializationException {
        for(SerializableSection serializableSection : serializableSections){
            rootSections.appendChild(serializableSection.serializeElement());
        }
        return rootSections;
    }
}
