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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@PropertySource(value = "classpath:app-web-config.properties")
public class WebAppInitializer implements WebApplicationInitializer

{

	@Override
	public void onStartup(final ServletContext servletContext)
			throws ServletException {

		final AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
		root.setServletContext(servletContext);
		root.scan("gov.nist.healthcare.tools.hl7.v2.igamt.lite",
				"gov.nist.healthcare.nht.acmgt");
		// web app servlet
		servletContext.addListener(new ContextLoaderListener(root));
		Dynamic servlet = servletContext.addServlet("igamt",
				new DispatcherServlet(root));
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/api/*");
		servlet.setAsyncSupported(true);
		try {
			ConfigurableEnvironment environment = root.getEnvironment();
			environment.getPropertySources().addFirst(
					new ResourcePropertySource(
							"classpath:app-web-config.properties"));
			String version = environment.getProperty("app.version");
			servletContext.setInitParameter("version", version);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
