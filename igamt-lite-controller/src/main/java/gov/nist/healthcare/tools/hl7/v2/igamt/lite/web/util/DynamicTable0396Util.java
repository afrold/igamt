package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RestTemplate;

public class DynamicTable0396Util {

    public static final String TABLE_0396_URL = "https://www.hl7.org/documentcenter/public/wg/vocab/Tbl0396.xls";

    static final Logger logger = LoggerFactory.getLogger(DynamicTable0396Util.class);

    public static InputStream downloadExcelFile() throws IOException {
	logger.info("Fetching Table 0396 from " + TABLE_0396_URL);
	File outputFile = File.createTempFile("Dynamic-Table-0396", ".xsl");
	FileOutputStream stream = new FileOutputStream(outputFile);
	RestTemplate restTemplate = new RestTemplate();

	restTemplate.execute(TABLE_0396_URL, HttpMethod.GET, (ClientHttpRequest requestCallback) -> {
	}, responseExtractor -> {
	    IOUtils.copy(responseExtractor.getBody(), stream);
	    return null;
	});

	InputStream targetStream = new FileInputStream(outputFile);
	return targetStream;
    }

}
