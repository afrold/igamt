package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DynTableDownloadServiceImpl implements DynTableDownloadService {

    public static final String TABLE_0396_URL = "http://www.hl7.org/documentcenter/public/wg/vocab/Tbl0396.xls";

    static final Logger logger = LoggerFactory.getLogger(DynTableDownloadServiceImpl.class);

    private RestTemplate restTemplate;

    @Override
    public InputStream downloadExcelFile() throws Exception {
	logger.info("Fetching Table 0396 from " + TABLE_0396_URL);

	File outputFile = File.createTempFile("Dynamic-Table-0396", ".xsl");
	FileOutputStream stream = new FileOutputStream(outputFile);

	restTemplate.execute(TABLE_0396_URL, HttpMethod.GET, (ClientHttpRequest requestCallback) -> {
	}, responseExtractor -> {
	    IOUtils.copy(responseExtractor.getBody(), stream);
	    return null;
	});

	InputStream targetStream = new FileInputStream(outputFile);
	return targetStream;
    }

    @PostConstruct
    @SuppressWarnings("deprecation")
    public void init() throws Exception {
	try {
	    SSLContextBuilder builder = new SSLContextBuilder();
	    builder.loadTrustMaterial(null, new TrustAllStrategy());
	    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build(),
		    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
		    .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
	    HttpComponentsClientHttpRequestFactory fct = new HttpComponentsClientHttpRequestFactory(httpClient);
	    this.restTemplate = new RestTemplate(fct);
	} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
	    throw new Exception(e);
	}
    }

    public class TrustAllStrategy implements TrustStrategy {
	@Override
	public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	    return true;
	}
    }

}
