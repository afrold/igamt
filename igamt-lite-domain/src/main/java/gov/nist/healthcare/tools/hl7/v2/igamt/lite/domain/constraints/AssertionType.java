package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain.constraints;

import java.io.Serializable;

public enum AssertionType implements Serializable {
    Presence("Presence"), PathValue("PathValue")
    , PlainText("PlainText"), Format("Format")
    , NumberList("NumberList"), StringList("StringList")
    , SimpleValue("SimpleValue")
    
    ,NOT("NOT"), AND("AND"), OR("OR"), XOR("XOR"), IMPLY("IMPLY"), FORALL("FORALL"), EXIST("EXIST");
    
    private String value;

    AssertionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

