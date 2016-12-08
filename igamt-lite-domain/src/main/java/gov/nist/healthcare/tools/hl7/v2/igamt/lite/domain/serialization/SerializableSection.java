package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.serialization;

import nu.xom.Element;

import java.util.Set;
import java.util.TreeSet;

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
public abstract class SerializableSection extends SerializableElement {

    private Set<SerializableSection> serializableSectionSet;

    public void addSection(SerializableSection serializableSection){
        this.serializableSectionSet.add(serializableSection);
    }

    public SerializableSection() {
        this.serializableSectionSet = new TreeSet<>();
    }

}
