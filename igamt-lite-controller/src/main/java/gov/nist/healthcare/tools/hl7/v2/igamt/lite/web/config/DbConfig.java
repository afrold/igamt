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

package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;

import java.util.Arrays;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Harold Affo (NIST)
 * 
 */

@Configuration
@EnableMongoRepositories(basePackages = "gov.nist.healthcare.tools")
@PropertySource(value = "classpath:igl-mongo.properties")
@EnableTransactionManagement(proxyTargetClass = true)
public class DbConfig {

	@Autowired
	private Environment env;

	//
	// @Bean
	// public DataSource dataSource() {
	// final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
	// dsLookup.setResourceRef(true);
	// DataSource dataSource = dsLookup.getDataSource("jdbc/igl_jndi");
	// return dataSource;
	// }

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		return (MongoTemplate) envCtx.lookup("jdbc/igl_mongo");
	}

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
	//
	// return jpaVendorAdapter;
	// }

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
	// // properties.put("hibernate.dialect",
	// // env.getProperty("hibernate.dialect"));
	// properties.put("hibernate.globally_quoted_identifiers",
	// env.getProperty("hibernate.globally_quoted_identifiers"));
	// properties.put("hibernate.enable_lazy_load_no_trans",
	// env.getProperty("hibernate.enable_lazy_load_no_trans"));
	//
	// return properties;
	// }

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

	@Bean
	public MongoOperations mongoTemplate(Mongo mongo) {
		return new MongoTemplate(mongo, env.getProperty("mongo.dbname"));
	}
}
