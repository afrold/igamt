package gov.nist.healthcare.hl7tools.igmatlite.domain;

import java.io.Serializable;

public enum ConstraintType implements Serializable {
    ByName("ByName"), ByID("ByID");
    
    private String value;

    ConstraintType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

