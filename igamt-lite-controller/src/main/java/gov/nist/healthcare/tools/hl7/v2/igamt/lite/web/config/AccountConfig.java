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

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Harold Affo (harold.affo@nist.gov) Apr 7, 2015
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories("gov.nist.healthcare.nht.acmgt.repo")
@PropertySource(value = { "classpath:app-auth-config.properties",
		"classpath:app-web-config.properties" })
@ImportResource({ "classpath:app-security-config.xml" })
public class AccountConfig {

	@Autowired
	private Environment env;

	@Qualifier("iglDataSource")
	@Bean
	public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource("jdbc/igl_jndi");
		return dataSource;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource m = new ResourceBundleMessageSource();
		m.setBasename("messages");
		return m;
	}

	@Bean
	public org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
		lef.setDataSource(dataSource);
		lef.setPersistenceUnitName(env.getProperty("jpa.persistenceUnitName"));
		lef.setJpaVendorAdapter(jpaVendorAdapter);
		lef.setJpaProperties(jpaProperties());
		// gov.nist.healthcare.nht.acmgt.web.CustomPersistenceUnitPostProcessor
		// postProcessors = new
		// gov.nist.healthcare.nht.acmgt.web.CustomPersistenceUnitPostProcessor();
		// Properties persistenceProperties = new Properties();
		// persistenceProperties.setProperty("eclipselink.logging.file",
		// env.getProperty("eclipselink.logging.file"));
		// persistenceProperties.setProperty("com.acme.persistence.sql-logging",
		// env.getProperty("com.acme.persistence.sql-logging"));
		// persistenceProperties.setProperty("eclipselink.logging.level",
		// env.getProperty("eclipselink.logging.level"));
		// postProcessors.setPersistenceProperties(persistenceProperties);
		// lef.setPersistenceUnitPostProcessors(postProcessors);
		lef.setPackagesToScan("gov.nist.healthcare.nht.acmgt.dto.domain");
		// lef.setJpaProperties(jpaProperties());
		// lef.setPersistenceUnitManager(persistenceUnitManager);
		lef.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
		return lef;
	}

	// @Bean
	// public JpaVendorAdapter jpaVendorAdapter() {
	// EclipseLinkJpaVendorAdapter jpaVendorAdapter = new
	// EclipseLinkJpaVendorAdapter();
	// jpaVendorAdapter.setShowSql(Boolean.getBoolean(env
	// .getProperty("jpa.showSql")));
	// jpaVendorAdapter.setGenerateDdl(Boolean.getBoolean(env
	// .getProperty("jpa.generateDdl")));
	// jpaVendorAdapter.setDatabase(Database.MYSQL);
	// jpaVendorAdapter.setDatabasePlatform(env
	// .getProperty("jpa.databasePlatform"));
	// return jpaVendorAdapter;
	// }

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setShowSql(Boolean.getBoolean(env
				.getProperty("jpa.showSql")));
		jpaVendorAdapter.setGenerateDdl(Boolean.getBoolean(env
				.getProperty("jpa.generateDdl")));
		jpaVendorAdapter.setDatabase(Database.MYSQL);
		jpaVendorAdapter.setDatabasePlatform(env
				.getProperty("jpa.databasePlatform"));

		return jpaVendorAdapter;
	}

	private Properties jpaProperties() {
		Properties properties = new Properties();
		// properties.put("hibernate.cache.use_second_level_cache",
		// env.getProperty("hibernate.cache.use_second_level_cache"));
		// properties.put("hibernate.cache.region.factory_class",
		// env.getProperty("hibernate.cache.region.factory_class"));
		// properties.put("hibernate.cache.use_query_cache",
		// env.getProperty("hibernate.cache.use_query_cache"));
		properties.put("hibernate.hbm2ddl.auto",
				env.getProperty("hibernate.hbm2ddl.auto"));
		// properties.put("hibernate.dialect",
		// env.getProperty("hibernate.dialect"));
		properties.put("hibernate.globally_quoted_identifiers",
				env.getProperty("hibernate.globally_quoted_identifiers"));
		return properties;
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		transactionManager.setJpaDialect(new HibernateJpaDialect());
		return transactionManager;
	}

	// @Bean
	// public org.springframework.orm.jpa.JpaTransactionManager
	// transactionManager(
	// EntityManagerFactory entityManagerFactory) {
	// JpaTransactionManager transactionManager = new JpaTransactionManager();
	// transactionManager.setEntityManagerFactory(entityManagerFactory);
	// // transactionManager.setJpaDialect(new HibernateJpaDialect());
	// return transactionManager;
	// }

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		// mailSender.setHost(env.getProperty("mail.host"));
		// mailSender.setPort(Integer.valueOf(env.getProperty("mail.port")));
		// mailSender.setProtocol(env.getProperty("mail.protocol"));
		// mailSender.setUsername(env.getProperty("mail.username"));
		// mailSender.setPassword(env.getProperty("mail.password"));
		// Properties javaMailProperties = new Properties();
		// javaMailProperties.setProperty("mail.smtps.auth", "false");
		// javaMailProperties.setProperty("mail.debug", "true");

		mailSender.setHost(env.getProperty("mail.host"));
		mailSender.setPort(Integer.valueOf(env.getProperty("mail.port")));
		mailSender.setProtocol(env.getProperty("mail.protocol"));
		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.auth",
				env.getProperty("mail.auth"));
		javaMailProperties.setProperty("mail.debug",
				env.getProperty("mail.debug"));

		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}

	@Bean
	PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public org.springframework.mail.SimpleMailMessage templateMessage() {
		org.springframework.mail.SimpleMailMessage templateMessage = new org.springframework.mail.SimpleMailMessage();
		templateMessage.setFrom(env.getProperty("mail.from"));
		templateMessage.setSubject(env.getProperty("mail.subject"));
		return templateMessage;
	}
}
