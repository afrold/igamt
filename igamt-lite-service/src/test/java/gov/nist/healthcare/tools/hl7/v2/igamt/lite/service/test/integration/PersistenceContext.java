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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.service.test.integration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@PropertySource("classpath:db-test-config.properties")
@EnableMongoRepositories("gov.nist.healthcare.tools")
// @EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan("gov.nist.healthcare.tools.hl7.v2.igamt.lite")
public class PersistenceContext {

	@Autowired
	private Environment env;

	// @Bean
	// public BasicDataSource dataSource() {
	// BasicDataSource dataSource = new BasicDataSource();
	// dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
	// dataSource.setUrl(env.getProperty("jdbc.url"));
	// dataSource.setUsername(env.getProperty("jdbc.username"));
	// dataSource.setPassword(env.getProperty("jdbc.password"));
	// dataSource.setTestOnBorrow(Boolean.getBoolean(env
	// .getProperty("jdbc.testOnBorrow")));
	// dataSource.setTestWhileIdle(Boolean.getBoolean(env
	// .getProperty("jdbc.testWhileIdle")));
	// return dataSource;
	// }
	//
	// @Bean
	// public LocalContainerEntityManagerFactoryBean entityManagerFactory(
	// DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
	// LocalContainerEntityManagerFactoryBean lef = new
	// LocalContainerEntityManagerFactoryBean();
	// lef.setDataSource(dataSource);
	// lef.setJpaVendorAdapter(jpaVendorAdapter);
	// lef.setPackagesToScan("gov.nist.healthcare.tools");
	// lef.setJpaProperties(jpaProperties());
	// lef.setPersistenceUnitName(env.getProperty("jpa.persistenceUnitName"));
	// lef.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
	// return lef;
	// }
	//
	// @Bean
	// public JpaVendorAdapter jpaVendorAdapter() {
	// HibernateJpaVendorAdapter jpaVendorAdapter = new
	// HibernateJpaVendorAdapter();
	// jpaVendorAdapter.setShowSql(Boolean.getBoolean(env
	// .getProperty("jpa.showSql")));
	// jpaVendorAdapter.setGenerateDdl(Boolean.getBoolean(env
	// .getProperty("jpa.generateDdl")));
	// jpaVendorAdapter.setDatabase(Database.MYSQL);
	// jpaVendorAdapter.setDatabasePlatform(env
	// .getProperty("jpa.databasePlatform"));
	// return jpaVendorAdapter;
	// }
	//
	// private Properties jpaProperties() {
	// Properties properties = new Properties();
	// // properties.put("hibernate.cache.use_second_level_cache",
	// // env.getProperty("hibernate.cache.use_second_level_cache"));
	// // properties.put("hibernate.cache.region.factory_class",
	// // env.getProperty("hibernate.cache.region.factory_class"));
	// // properties.put("hibernate.cache.use_query_cache",
	// // env.getProperty("hibernate.cache.use_query_cache"));
	// properties.put("hibernate.hbm2ddl.auto",
	// env.getProperty("hibernate.hbm2ddl.auto"));
	// properties.put("hibernate.dialect",
	// env.getProperty("hibernate.dialect"));
	// properties.put("hibernate.globally_quoted_identifiers",
	// env.getProperty("hibernate.globally_quoted_identifiers"));
	//
	// properties.put("hibernate.connection.autocommit", true);
	//
	// return properties;
	// }
	//
	// @Bean
	// public PlatformTransactionManager transactionManager(
	// EntityManagerFactory entityManagerFactory) {
	// JpaTransactionManager transactionManager = new JpaTransactionManager();
	// transactionManager.setEntityManagerFactory(entityManagerFactory);
	// transactionManager.setJpaDialect(new HibernateJpaDialect());
	// return transactionManager;
	// }
	//
	// @Bean
	// PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
	// return new PersistenceExceptionTranslationPostProcessor();
	// }
	//
	// @Bean
	// PersistenceUnitManager persistenceUnitManager() {
	// return new DefaultPersistenceUnitManager();
	// }

	@Bean
	public Mongo mongo() throws Exception {
		MongoCredential credential = MongoCredential.createMongoCRCredential(
				env.getProperty("mongo.username"),
				env.getProperty("mongo.dbname"),
				env.getProperty("mongo.password").toCharArray());
		return new MongoClient(new ServerAddress(env.getProperty("mongo.host"),
				Integer.valueOf(env.getProperty("mongo.port"))),
				Arrays.asList(credential));
	}

	@Bean
	public MongoOperations mongoTemplate(Mongo mongo) {
		return new MongoTemplate(mongo, env.getProperty("mongo.dbname"));
	}
}
