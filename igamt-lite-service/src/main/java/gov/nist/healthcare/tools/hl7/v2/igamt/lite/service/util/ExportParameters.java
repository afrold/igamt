package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.util;

import java.util.HashMap;
import java.util.Map;

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
 * Created by Maxence Lefort on 10/5/16.
 */
public class ExportParameters {
    //Define parameters with a default value
    private boolean inlineConstraints = false;
    private boolean includeTOC = true;
    private String targetFormat = "html";
    private String documentTitle = "Implementation Guide";

    public ExportParameters(boolean inlineConstraints, boolean includeTOC, String targetFormat,
        String documentTitle) {
        this.inlineConstraints = inlineConstraints;
        this.includeTOC = includeTOC;
        this.targetFormat = targetFormat;
        this.documentTitle = documentTitle;
    }

    public ExportParameters() {
    }

    public boolean isInlineConstraints() {
        return inlineConstraints;
    }

    public void setInlineConstraints(boolean inlineConstraints) {
        this.inlineConstraints = inlineConstraints;
    }

    public boolean isIncludeTOC() {
        return includeTOC;
    }

    public void setIncludeTOC(boolean includeTOC) {
        this.includeTOC = includeTOC;
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public Map<String, String> toMap() {
        Map<String, String> params = new HashMap<>();
        params.put("includeTOC", String.valueOf(includeTOC));
        params.put("inlineConstraints", String.valueOf(inlineConstraints));
        params.put("targetFormat", targetFormat);
        params.put("documentTitle", documentTitle);
        return params;
    }
}
