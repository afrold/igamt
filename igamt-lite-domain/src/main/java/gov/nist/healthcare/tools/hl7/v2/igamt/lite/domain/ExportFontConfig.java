package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import org.apache.xpath.operations.Bool;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
 * <p/>
 * Created by Maxence Lefort on 2/23/17.
 */
@Document(collection = "exportFontConfig")
public class ExportFontConfig {

    @Id
    private String id;
    private Long accountId;
    private ExportFont exportFont;
    private Integer fontSize;
    private Boolean defaultConfig;

    public ExportFontConfig() {
    }

    public ExportFontConfig(Long accountId, ExportFont exportFont, Integer fontSize) {
        this.accountId = accountId;
        this.exportFont = exportFont;
        this.fontSize = fontSize;
        this.defaultConfig = false;
    }

    public ExportFontConfig(ExportFont exportFont, Integer fontSize, Boolean defaultConfig) {
        this.exportFont = exportFont;
        this.fontSize = fontSize;
        this.defaultConfig = defaultConfig;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public ExportFont getExportFont() {
        return exportFont;
    }

    public void setExportFont(ExportFont exportFont) {
        this.exportFont = exportFont;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public Boolean getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(Boolean defaultConfig) {
        this.defaultConfig = defaultConfig;
    }
}
