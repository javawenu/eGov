/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.ptis.web.controller.demolition;

import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Designation;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.service.demolition.PropertyDemolitionService;
import org.egov.ptis.exceptions.TaxCalculatorExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.egov.ptis.constants.PropertyTaxConstants.*;

@Controller
@RequestMapping(value = "/demolition/update/{id}")
public class UpdatePropertyDemolitionController extends GenericWorkFlowController {

    protected static final String DEMOLITION_FORM = "demolition-form";
    protected static final String DEMOLITION_VIEW = "demolition-view";
    protected static final String DEMOLITION_SUCCESS = "demolition-success";
    public static final String EDIT = "edit";
    public static final String VIEW = "view";

    PropertyDemolitionService propertyDemolitionService;
    
    @Autowired
    private PersistenceService<Property, Long> persistenceService;
    
    @Autowired
    protected AssignmentService assignmentService;

    @Autowired
    public UpdatePropertyDemolitionController(PropertyDemolitionService propertyDemolitionService) {
        this.propertyDemolitionService = propertyDemolitionService;
    }

    @Autowired
    private PtDemandDao ptDemandDAO;

    @Autowired
    private PropertyTaxUtil propertyTaxUtil;

    @Autowired
    private SecurityUtils securityUtils;

    private PropertyImpl property;

    @ModelAttribute
    public Property propertyModel(@PathVariable String id) {
        property = propertyDemolitionService.findByNamedQuery(QUERY_WORKFLOW_PROPERTYIMPL_BYID, Long.valueOf(id));
        if (null == property) {
            property = propertyDemolitionService.findByNamedQuery(QUERY_PROPERTYIMPL_BYID, Long.valueOf(id));
        }
        return property;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final Long id, final HttpServletRequest request) {

        String userDesgn = "";
        final String currState = property.getState().getValue();
        final String nextAction = property.getState().getNextAction();
        final Designation designation = propertyTaxUtil.getDesignationForUser(securityUtils.getCurrentUser().getId());
        if (null != designation)
            userDesgn = designation.getName();
        model.addAttribute("stateType", property.getClass().getSimpleName());
        model.addAttribute("currentState", property.getCurrentState().getValue());
        model.addAttribute("userDesgn", userDesgn);
        prepareWorkflow(model, property, new WorkflowContainer());
        propertyDemolitionService.addModelAttributes(model, property.getBasicProperty());

        model.addAttribute("userDesgn", userDesgn);
        model.addAttribute("designation", COMMISSIONER_DESGN);
        if (currState.endsWith(WF_STATE_REJECTED) || nextAction.equalsIgnoreCase(WF_STATE_UD_REVENUE_INSPECTOR_APPROVAL_PENDING)
                || currState.endsWith(WFLOW_ACTION_NEW)) {
            model.addAttribute("mode", EDIT);
            return DEMOLITION_FORM;
        } else {
            model.addAttribute("mode", VIEW);
            return DEMOLITION_VIEW;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute Property property, final BindingResult errors,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request, final Model model,
            @RequestParam String workFlowAction) {

        propertyDemolitionService.validateProperty(property, errors, request);
        if (errors.hasErrors()) {
            prepareWorkflow(model, (PropertyImpl) property, new WorkflowContainer());
            model.addAttribute("stateType", property.getClass().getSimpleName());
            return DEMOLITION_FORM;
        } else {

            final Character status = STATUS_WORKFLOW;
            Long approvalPosition = 0l;
            String approvalComent = "";
            Property oldProperty = (PropertyImpl) property.getBasicProperty().getActiveProperty();

            if (request.getParameter("approvalComent") != null)
                approvalComent = request.getParameter("approvalComent");
            if (request.getParameter("workFlowAction") != null)
                workFlowAction = request.getParameter("workFlowAction");
            if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
                approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

            if (WFLOW_ACTION_STEP_SIGN.equalsIgnoreCase(workFlowAction) && property.getStatus().equals(STATUS_WORKFLOW)) {
                if (oldProperty.getStatus().equals(STATUS_ISACTIVE)) {
                    oldProperty.setStatus(STATUS_ISHISTORY);
                    persistenceService.persist(oldProperty);
                }
                if (property.getStatus().equals(STATUS_WORKFLOW)) {
                    property.setStatus(STATUS_ISACTIVE);
                    persistenceService.persist(property);
                }
            }

            if (workFlowAction.equalsIgnoreCase(WFLOW_ACTION_STEP_NOTICE_GENERATE) ||
                    WFLOW_ACTION_STEP_PREVIEW.equalsIgnoreCase(workFlowAction) ||
                    WFLOW_ACTION_STEP_SIGN.equalsIgnoreCase(workFlowAction)) {
                return "redirect:/notice/propertyTaxNotice-generateNotice.action?basicPropId="
                        + property.getBasicProperty().getId() + "&noticeType=" + NOTICE_TYPE_SPECIAL_NOTICE
                        + "&noticeMode=" + APPLICATION_TYPE_DEMOLITION + "&actionType="+workFlowAction;
            } else {

                if (request.getParameter("mode").equalsIgnoreCase(VIEW)) {
                    propertyDemolitionService.updateProperty(property, approvalComent, workFlowAction,
                            approvalPosition, DEMOLITION);
                } else {
                    try {
                        propertyDemolitionService.saveProperty(oldProperty, property, status, approvalComent,
                                workFlowAction, approvalPosition, DEMOLITION);
                    } catch (TaxCalculatorExeption e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Assignment assignment = new Assignment();
                if (workFlowAction.equalsIgnoreCase(WFLOW_ACTION_STEP_APPROVE)) {
                    assignment = assignmentService.getPrimaryAssignmentForUser(securityUtils.getCurrentUser().getId());
                    model.addAttribute("successMessage", "Property Demolition approved successfully and forwarded to  "
                            + assignment.getEmployee().getName().concat("~").concat(assignment.getPosition().getName()) + " with assessment number "
                            + property.getBasicProperty().getUpicNo());
                } else if (workFlowAction.equalsIgnoreCase(WFLOW_ACTION_STEP_REJECT)) {
                    assignment = assignmentService.getPrimaryAssignmentForUser(property.getCreatedBy().getId());
                    model.addAttribute(
                            "successMessage",
                            "Property Demolition rejected successfully and forwared to initiator "
                                    + assignment.getEmployee().getName().concat("~").concat(assignment.getPosition().getName())+ " with application number "
                                    + property.getApplicationNo());
                } else
                    model.addAttribute("successMessage",
                            "Successfully forwarded to " + propertyTaxUtil.getApproverUserName(approvalPosition)
                                    + " with application number " + property.getApplicationNo());
                return DEMOLITION_SUCCESS;
            }
        }
    }

}
