package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class DynamicTable0396Util {

    private static final String TABLE_0396_URL = "https://www.hl7.org/documentcenter/public/wg/vocab/Tbl0396.xls";

    static final Logger logger = LoggerFactory.getLogger(DynamicTable0396Util.class);

    public static InputStream downloadExcelFile() {
	logger.info("Fetching Table 0396 from " + TABLE_0396_URL);
	RestTemplate restTemplate = new RestTemplate();
	// download csv file
	RequestCallback requestCallback = request -> request.getHeaders()
		.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
	// Streams the response instead of loading it all in memory
	ResponseExtractor<InputStream> responseExtractor = response -> {
	    return response.getBody();
	};
	return restTemplate.execute(URI.create(TABLE_0396_URL), HttpMethod.GET, requestCallback, responseExtractor);
    }

}
