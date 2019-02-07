package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.InputStream;

public interface DynTableDownloadService {

    public InputStream downloadExcelFile() throws Exception;

}
