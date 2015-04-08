/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgment if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */
package gov.nist.healthcare.nht.acmgt.web;

import gov.nist.healthcare.nht.acmgt.dto.ResponseMessage;
import gov.nist.healthcare.nht.acmgt.dto.domain.Account;
import gov.nist.healthcare.nht.acmgt.dto.domain.Issue;
import gov.nist.healthcare.nht.acmgt.general.CustomSortHandler;
import gov.nist.healthcare.nht.acmgt.repo.AccountRepository;
import gov.nist.healthcare.nht.acmgt.repo.IssueRepository;
import gov.nist.healthcare.nht.acmgt.repo.IssueSpecsHelper;
import gov.nist.healthcare.nht.acmgt.service.UserService;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fdevaulx
 * 
 */
@Controller
public class IssueController {

	static final Logger logger = LoggerFactory.getLogger(IssueController.class);

	public final String DEFAULT_PAGE_SIZE = "0";

	@Inject
	IssueRepository issueRepository;

	@Autowired
	UserService userService;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private SimpleMailMessage templateMessage;

	@RequestMapping(value = "/sooa/issues", method = RequestMethod.POST)
	@ResponseBody
	public ResponseMessage addIssue(@Valid @RequestBody Issue issue,
			HttpServletRequest request) {

		Issue is = new Issue();
		is.setTimeStamp(new Date());
		is.setSenderAgent(request.getHeader("user-agent"));
		is.setTitle(issue.getTitle());
		is.setDescription(issue.getDescription());
		is.setEmail(issue.getEmail());

		issueRepository.save(is);

		this.sendIssueNotification(is);

		return new ResponseMessage(ResponseMessage.Type.success, "issueAdded",
				is.getId().toString());
	}

	@PreAuthorize("hasRole('supervisor') or hasRole('admin')")
	@RequestMapping(value = "/issues/page", method = RequestMethod.GET)
	@ResponseBody
	public Page<Issue> getIssuesPage(
			@RequestParam(required = false, defaultValue = "0") int value,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
			@RequestParam(required = false) List<String> sort,
			@RequestParam(required = false) List<String> filter) {

		IssueSpecsHelper issueH = new IssueSpecsHelper();

		return issueRepository.findAll(
				issueH.getSpecification(filter),
				new PageRequest(value, size, (new CustomSortHandler(sort))
						.getSort()));

	}

	private void sendIssueNotification(Issue is) {
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		User authU = userService.getCurrentUser();
		Account acc = null;
		if (authU != null) {
			acc = accountRepository.findByTheAccountsUsername(authU
					.getUsername());
		}

		msg.setTo("randomizer@hhs.gov");
		msg.setSubject("NIST EHR Randomizer Issue Notification ");
		msg.setText("An issue was reported \n\n"
				+ (authU != null ? "[ username: " + acc.getUsername()
						+ " - user email: " + acc.getEmail() + " ]\n\n" : "")
				+ "given email: " + is.getEmail() + " \n\n" + "title: "
				+ is.getTitle() + " \n" + "description: " + is.getDescription());

		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

}
