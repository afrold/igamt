package gov.nist.healthcare.tools.hl7.v2.igamt.lite.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/components")
public class ComponentController {
	static final Logger logger = LoggerFactory
			.getLogger(ComponentController.class);

	// @Autowired
	// private ComponentRepository componentRepository;
	//
	// @Autowired
	// private ComponentService componentService;
	//
	//
	// public ComponentRepository getComponentRepository() {
	// return componentRepository;
	// }
	//
	// public void setComponentRepository(ComponentRepository
	// componentRepository) {
	// this.componentRepository = componentRepository;
	// }
	//
	// public ComponentService getComponentService() {
	// return componentService;
	// }
	//
	// public void setComponentService(ComponentService componentService) {
	// this.componentService = componentService;
	// }
	//
	// @RequestMapping(value = "/component/create", method=RequestMethod.POST)
	// public Component createComponent(@RequestBody Component c) {
	// return componentService.save(c);
	// }
	//
	// @RequestMapping(value = "/component/change", method=RequestMethod.POST)
	// public Component changeeComponent(@RequestBody Component c) {
	// Component c1 = new Component();
	// if (c.getDatatype() != null)
	// {c1.setDatatype(c.getDatatype());
	// }
	//
	// if (c.getName() != null)
	// {c1.setName(c.getName());
	//
	// }
	//
	// if (c.getUsage() != null)
	// {c1.setUsage(c.getUsage());
	// }
	//
	// if (c.getMinLength() != null)
	// {c1.setMinLength(c.getMinLength());
	// }
	//
	// if (c.getMaxLength() != null)
	// {c1.setMaxLength(c.getMaxLength());
	// }
	//
	// if (c.getConfLength() != null)
	// {c1.setConfLength(c.getConfLength());
	// }
	//
	// if (c.getTable() != null)
	// {c1.setTable(c.getTable());
	// }
	//
	// if (c.getBindingStrength() != null)
	// {c1.setBindingStrength(c.getBindingStrength());
	// }
	//
	// if (c.getBindingLocation() != null)
	// {c1.setBindingLocation(c.getBindingLocation());
	// }
	//
	// return componentService.save(c1);
	// }
	//
	// @RequestMapping(value = "/component/{componentId}", method =
	// RequestMethod.GET)
	// @ResponseBody
	// public Component component(final Long componentId) {
	// return componentService.findOne(componentId);
	// }
	//
	// @RequestMapping(value="/component/update", method=RequestMethod.PUT)
	// public Component update(@RequestBody @Valid Component component) {
	// return componentService.save(component);
	// }
	//
	// @RequestMapping(value = "/component/delete/{componentId}",
	// method=RequestMethod.DELETE)
	// public ResponseEntity<Boolean> delete(final Long componentId){
	// componentService.delete(componentId);
	// return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	//
	// }

}