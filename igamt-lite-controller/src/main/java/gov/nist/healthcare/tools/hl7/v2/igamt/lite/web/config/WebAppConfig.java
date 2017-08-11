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
package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.config;


import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.base.Predicate;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@ComponentScan({"gov.nist.healthcare.tools.hl7.v2.igamt.lite", "gov.nist.healthcare.nht.acmgt"})
@EnableSwagger2
@EnableWebMvc
@Import({MongoConfig.class, AccountConfig.class})
public class WebAppConfig extends WebMvcConfigurerAdapter {

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  private Predicate<String> swaggerPaths() {
    return or(regex("/search.*"), regex("/export.*"));
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
        .paths(swaggerPaths()).build();
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/api", "/api/swagger-ui.html");
    registry.addRedirectViewController("/api/", "/api/swagger-ui.html");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/api/**").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
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
  // builder.featuresToDisable(SerializationFeature.INDENT_OUTPUT,
  // SerializationFeature.FAIL_ON_EMPTY_BEANS);
  // builder.configure(mapper);
  //
  // MappingJackson2HttpMessageConverter converter = new
  // MappingJackson2HttpMessageConverter(
  // builder.build());
  // converter.setPrettyPrint(false);
  // converters.add(converter);
  // super.configureMessageConverters(converters);
  // }

  @Bean
  public MultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    return multipartResolver;
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }


  // @Override
  // public void configureMessageConverters(
  // List<HttpMessageConverter<?>> converters) {
  // converters.add(byteArrayHttpMessageConverter());
  // }
  //
  // @Bean
  // public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
  // ByteArrayHttpMessageConverter arrayHttpMessageConverter = new
  // ByteArrayHttpMessageConverter();
  // arrayHttpMessageConverter
  // .setSupportedMediaTypes(getSupportedMediaTypes());
  // return arrayHttpMessageConverter;
  // }
  //
  // private List<MediaType> getSupportedMediaTypes() {
  // List<MediaType> list = new ArrayList<MediaType>();
  // list.add(MediaType.IMAGE_JPEG);
  // list.add(MediaType.IMAGE_PNG);
  // list.add(MediaType.IMAGE_GIF);
  // list.add(MediaType.APPLICATION_OCTET_STREAM);
  // return list;
  // }

}
