/* eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.

        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.

        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.web.controller.application.reissue;

import javax.servlet.http.HttpServletRequest;

import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.mrs.application.Constants;
import org.egov.mrs.domain.entity.ReIssue;
import org.egov.mrs.domain.entity.Registration;
import org.egov.mrs.domain.service.ApplicantService;
import org.egov.mrs.domain.service.DocumentService;
import org.egov.mrs.domain.service.ReIssueService;
import org.egov.mrs.domain.service.RegistrationService;
import org.egov.mrs.masters.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles Marriage Registration ReIssue transaction
 * 
 * @author nayeem
 *
 */
@Controller
@RequestMapping(value = "/reissue")
public class NewReIssueController extends GenericWorkFlowController {

    @Autowired
    private ReIssueService reIssueService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ApplicantService applicantService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ReIssueService reissueService;

    @RequestMapping(value = "/create/{registrationId}", method = RequestMethod.GET)
    public String showReIssueForm(@PathVariable final Long registrationId, final Model model) {

        final Registration registration = registrationService.get(registrationId);

        registrationService.prepareDocumentsForView(registration);
        applicantService.prepareDocumentsForView(registration.getHusband());
        applicantService.prepareDocumentsForView(registration.getWife());

        /*
         * registration.getWitnesses() .stream() .filter(witness -> witness.getPhoto() != null && witness.getPhoto().length > 0)
         * .forEach(witness -> witness.setEncodedPhoto(Base64.getEncoder().encodeToString(witness.getPhoto())));
         */

        final ReIssue reIssue = new ReIssue();
        reIssue.setFeeCriteria(Constants.REISSUE_FEECRITERIA);
        reIssue.setFeePaid(feeService.getFeeForCriteria(Constants.REISSUE_FEECRITERIA).getFees());
        reIssue.setRegistration(registration);

        model.addAttribute("reIssue", reIssue);
        model.addAttribute("documents", documentService.getReIssueApplicantDocs());
        prepareWorkflow(model, reIssue, new WorkflowContainer());
        return "reissue-form";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createReIssue(@ModelAttribute final ReIssue reIssue,
            @ModelAttribute final WorkflowContainer workflowContainer,
            final Model model,
            final HttpServletRequest request,
            final BindingResult errors) {

        if (errors.hasErrors())
            return "reissue-form";
        reIssue.setRegistration(registrationService.get(reIssue.getRegistration().getId()));
        obtainWorkflowParameters(workflowContainer, request);
        final String appNo = reIssueService.createReIssueApplication(reIssue, workflowContainer);
        model.addAttribute("ackNumber", appNo);

        return "reissue-ack";
    }

    @RequestMapping(value = "/workflow", method = RequestMethod.POST)
    public String handleWorkflowAction(@RequestParam final Long id,
            @ModelAttribute final ReIssue reissue,
            @ModelAttribute final WorkflowContainer workflowContainer,
            final Model model,
            final HttpServletRequest request,
            final BindingResult errors) {

        if (errors.hasErrors())
            return "reissue-view";

        obtainWorkflowParameters(workflowContainer, request);
        ReIssue result = null;

        switch (workflowContainer.getWorkFlowAction()) {
        case "Forward":
            result = reissueService.forwardReIssue(id, reissue, workflowContainer);
            break;
        case "Approve":
            result = reissueService.approveReIssue(id, workflowContainer);
            break;
        case "Reject":
            result = reissueService.rejectReIssue(id, workflowContainer);
            break;
        case "Close ReIssue":
            result = reissueService.rejectReIssue(id, workflowContainer);
            break;
        }

        model.addAttribute("reissue", result);
        return "reissue-ack";
    }

    /**
     * Obtains the workflow paramaters from the HttpServletRequest
     *
     * @param workflowContainer
     * @param request
     */
    private void obtainWorkflowParameters(final WorkflowContainer workflowContainer, final HttpServletRequest request) {
        if (request.getParameter("approvalComent") != null)
            workflowContainer.setApproverComments(request.getParameter("approvalComent"));
        if (request.getParameter("workFlowAction") != null)
            workflowContainer.setWorkFlowAction(request.getParameter("workFlowAction"));
        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            workflowContainer.setApproverPositionId(Long.valueOf(request.getParameter("approvalPosition")));
    }
}