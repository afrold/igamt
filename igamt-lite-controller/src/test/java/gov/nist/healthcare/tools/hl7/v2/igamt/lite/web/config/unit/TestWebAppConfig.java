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
 */
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.unit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.AccountConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.Bootstrap;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.MongoConfig;
import gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config.WebAppConfig;

@WebAppConfiguration
@EnableWebMvc
@ComponentScan(
    basePackages = {"gov.nist.healthcare.tools.hl7.v2.igamt.lite", "gov.nist.healthcare.nht.acmgt"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {MongoConfig.class}),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {WebAppConfig.class}),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {AccountConfig.class}),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {Bootstrap.class})})
@Import({TestDbConfig.class})
public class TestWebAppConfig extends WebMvcConfigurerAdapter {

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  // @Override
  // public void configureMessageConverters(
  // List<HttpMessageConverter<?>> converters) {
  //
  // ObjectMapper mapper = new ObjectMapper();
  // mapper.disable(SerializationFeature.INDENT_OUTPUT);
  // mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  // mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
  // // Registering Hibernate4Module to support lazy objects
  // mapper.registerModule(new Hibernate4Module());
  // Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
  // builder.serializationInclusion(JsonInclude.Include.NON_NULL);
  // builder.configure(mapper);
  // converters
  // .add(new MappingJackson2HttpMessageConverter(builder.build()));
  // super.configureMessageConverters(converters);
  // }

  @Bean
  public MultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    return multipartResolver;
  }

}
