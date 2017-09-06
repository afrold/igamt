/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

/**
 * 
 * @author Olivier MARIE-ROSE
 * 
 */

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
// @EnableTransactionManagement(proxyTargetClass = true)
@PropertySource(value = { "classpath:db-test-config.properties",
		"classpath:igl-test-log4j.properties" })
@EnableMongoRepositories(basePackages = "gov.nist.healthcare.tools")
public class TestDbConfig extends AbstractMongoConfiguration {

	@Autowired
	private Environment env;

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		MongoCredential credential = MongoCredential.createMongoCRCredential(
				env.getProperty("mongo.username"),
				env.getProperty("mongo.dbname"),
				env.getProperty("mongo.password").toCharArray());
		return new MongoClient(new ServerAddress("localhost",
				Integer.valueOf(env.getProperty("mongo.port"))),
				Arrays.asList(credential));
	}

	@Override
	@Bean
	public CustomConversions customConversions() {
		List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
		return new CustomConversions(converterList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.mongodb.config.AbstractMongoConfiguration#
	 * getDatabaseName()
	 */
	@Override
	protected String getDatabaseName() {
		return env.getProperty("mongo.dbname");
	}

	@Override
	public String getMappingBasePackage() {
		return "gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain";
	}

}
