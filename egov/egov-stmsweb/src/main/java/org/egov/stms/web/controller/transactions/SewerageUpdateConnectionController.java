/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
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
package org.egov.stms.web.controller.transactions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.apache.commons.lang3.ArrayUtils;
import org.egov.commons.service.UOMService;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.stms.masters.entity.enums.OwnerOfTheRoad;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.service.FeesDetailMasterService;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnectionEstimationDetails;
import org.egov.stms.transactions.entity.SewerageConnectionFee;
import org.egov.stms.transactions.entity.SewerageFieldInspection;
import org.egov.stms.transactions.entity.SewerageFieldInspectionDetails;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/transactions")
public class SewerageUpdateConnectionController extends GenericWorkFlowController {

    private final SewerageApplicationDetailsService sewerageApplicationDetailsService;

    private final DepartmentService departmentService;

    @Autowired
    private SewerageTaxUtils sewerageTaxUtils;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;

    @Autowired
    private FeesDetailMasterService feesDetailMasterService;

    @Autowired
    private UOMService uOMService;

    @Autowired
    public SewerageUpdateConnectionController(
            final SewerageApplicationDetailsService sewerageApplicationDetailsService,
            final DepartmentService departmentService, final SmartValidator validator) {
        this.sewerageApplicationDetailsService = sewerageApplicationDetailsService;
        this.departmentService = departmentService;
    }

    @ModelAttribute
    public SewerageApplicationDetails getSewerageApplicationDetails(@PathVariable final String applicationNumber) {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(applicationNumber);
        return sewerageApplicationDetails;
    }

    @RequestMapping(value = "/update/{applicationNumber}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final String applicationNumber, final HttpServletRequest request) {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(applicationNumber);
        return loadViewData(model, request, sewerageApplicationDetails);
    }

    private String loadViewData(final Model model, final HttpServletRequest request,
            final SewerageApplicationDetails sewerageApplicationDetails) {
        model.addAttribute("stateType", sewerageApplicationDetails.getClass().getSimpleName());

        model.addAttribute("additionalRule", sewerageApplicationDetails.getApplicationType().getCode());
        model.addAttribute("currentUser", sewerageTaxUtils.getCurrentUserRole(securityUtils.getCurrentUser()));
        model.addAttribute("currentState", sewerageApplicationDetails.getCurrentState().getValue());

        prepareWorkflow(model, sewerageApplicationDetails, new WorkflowContainer());

        model.addAttribute("sewerageApplicationDetails", sewerageApplicationDetails);
        model.addAttribute("applicationHistory",
                sewerageApplicationDetailsService.getHistory(sewerageApplicationDetails));
        model.addAttribute("approvalDepartmentList", departmentService.getAllDepartments());

        model.addAttribute("pipeSize", SewerageTaxConstants.PIPE_SCREW_SIZE);
        model.addAttribute("roadOwner", OwnerOfTheRoad.values());
        model.addAttribute("feesDetails",
                feesDetailMasterService.findAllFeesDetailByFeesCode(SewerageTaxConstants.FEES_ESTIMATIONCHARGES_CODE));
        model.addAttribute("uomList", uOMService.findAllOrderByCategory());
        appendModeBasedOnApplicationCreator(model, request, sewerageApplicationDetails);

        final BigDecimal sewerageTaxDue = sewerageApplicationDetailsService.getTotalAmount(sewerageApplicationDetails);
        model.addAttribute("sewerageTaxDue", sewerageTaxDue);
        model.addAttribute("propertyTypes", PropertyType.values());
        return "newconnection-edit";
    }

    private void appendModeBasedOnApplicationCreator(final Model model, final HttpServletRequest request,
            final SewerageApplicationDetails sewerageApplicationDetails) {
        final Boolean recordCreatedBYNonEmployee = sewerageTaxUtils.getCurrentUserRole(sewerageApplicationDetails
                .getCreatedBy());

        if ((recordCreatedBYNonEmployee || userService.getUserById(sewerageApplicationDetails.getCreatedBy().getId())
                .getUsername().equals("anonymous"))
                && null == request.getAttribute("mode") && sewerageApplicationDetails.getState().getHistory().isEmpty()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("approvalPositionExist", sewerageApplicationDetailsService
                    .getApprovalPositionByMatrixDesignation(sewerageApplicationDetails, 0l, sewerageApplicationDetails
                            .getApplicationType().getCode(), "edit", ""));
        }

        if (recordCreatedBYNonEmployee
                && request.getAttribute("mode") == null
                && sewerageApplicationDetails.getStatus().getCode()
                .equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED)
                && sewerageApplicationDetails.getState().getHistory() != null
                || !recordCreatedBYNonEmployee
                && sewerageApplicationDetails.getStatus() != null
                && sewerageApplicationDetails.getStatus().getCode()
                .equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED)) {
            model.addAttribute("mode", "fieldInspection");
            model.addAttribute("approvalPositionExist", sewerageApplicationDetailsService
                    .getApprovalPositionByMatrixDesignation(sewerageApplicationDetails, 0l, sewerageApplicationDetails
                            .getApplicationType().getCode(), "fieldInspection", ""));
        } else
            model.addAttribute("approvalPositionExist", sewerageApplicationDetailsService
                    .getApprovalPositionByMatrixDesignation(sewerageApplicationDetails, 0l, sewerageApplicationDetails
                            .getApplicationType().getCode(), "", ""));
        if (sewerageApplicationDetails.getCurrentState().getValue().equals(SewerageTaxConstants.WF_STATE_REJECTED)
                || sewerageApplicationDetails.getCurrentState().getValue()
                .equals(SewerageTaxConstants.WF_STATE_DEPUTY_EXE_APPROVED))
            model.addAttribute("mode", "");
    }

    @RequestMapping(value = "/update/{applicationNumber}", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute SewerageApplicationDetails sewerageApplicationDetails,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Model model, @RequestParam("files") final MultipartFile[] files) {
        String mode = "";
        String workFlowAction = "";

        if (request.getParameter("mode") != null)
            mode = request.getParameter("mode");

        if (request.getParameter("workFlowAction") != null)
            workFlowAction = request.getParameter("workFlowAction");

        request.getSession().setAttribute(SewerageTaxConstants.WORKFLOW_ACTION, workFlowAction);

        if (sewerageApplicationDetails.getStatus().getCode()
                .equalsIgnoreCase(SewerageTaxConstants.APPLICATION_STATUS_CREATED)
                && mode.equalsIgnoreCase("fieldInspection"))
            if (workFlowAction.equalsIgnoreCase(SewerageTaxConstants.SUBMITWORKFLOWACTION)) {
                populateEstimationDetails(sewerageApplicationDetails);
                populateInspectionDetails(sewerageApplicationDetails, request, files);
                populateFeesDetails(sewerageApplicationDetails);
                sewerageApplicationDetailsService.save(sewerageApplicationDetails);
            } else if (workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)) {
                sewerageApplicationDetailsService.getCurrentSession().evict(sewerageApplicationDetails);
                sewerageApplicationDetails = sewerageApplicationDetailsService.findBy(sewerageApplicationDetails
                        .getId());
            }
        Long approvalPosition = 0l;
        String approvalComment = "";

        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");

        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        // For Get Configured ApprovalPosition from workflow history
        if (approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
            approvalPosition = sewerageApplicationDetailsService.getApprovalPositionByMatrixDesignation(
                    sewerageApplicationDetails, approvalPosition, sewerageApplicationDetails.getApplicationType()
                    .getCode(), mode, workFlowAction);

        // To get modes to hide and show details in every user inbox
        request.getSession().setAttribute("APPROVAL_POSITION", approvalPosition);

        appendModeBasedOnApplicationCreator(model, request, sewerageApplicationDetails);
        if ((approvalPosition == null || approvalPosition.equals(Long.valueOf(0)))
                && request.getParameter("approvalPosition") != null
                && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

        if (!resultBinder.hasErrors()) {
            try {
                if (null != workFlowAction)
                    if (workFlowAction.equalsIgnoreCase(SewerageTaxConstants.PREVIEWWORKFLOWACTION)
                            && sewerageApplicationDetails.getApplicationType().getCode()
                            .equals(SewerageTaxConstants.NEWSEWERAGECONNECTION))
                        return "redirect:/transactions/workorder?pathVar="
                        + sewerageApplicationDetails.getApplicationNumber();

                sewerageApplicationDetailsService.updateSewerageApplicationDetails(sewerageApplicationDetails,
                        approvalPosition, approvalComment, sewerageApplicationDetails.getApplicationType().getCode(),
                        workFlowAction, mode, null);
            } catch (final ValidationException e) {
                throw new ValidationException(e.getMessage());
            }
            if (workFlowAction != null && !workFlowAction.isEmpty()
                    && workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WF_ESTIMATION_NOTICE_BUTTON))
                return "redirect:/transactions/estimationnotice?pathVar="
                + sewerageApplicationDetails.getApplicationNumber();
            if (null != workFlowAction && !workFlowAction.isEmpty()
                    && workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WF_WORKORDER_BUTTON))
                return "redirect:/transactions/workordernotice?pathVar="
                + sewerageApplicationDetails.getApplicationNumber();
            if (workFlowAction != null && !workFlowAction.isEmpty()
                    && workFlowAction.equalsIgnoreCase(SewerageTaxConstants.WF_CLOSERACKNOWLDGEENT_BUTTON))
                return "redirect:/applications/acknowlgementNotice?pathVar="
                + sewerageApplicationDetails.getApplicationNumber();
            final Assignment currentUserAssignment = assignmentService.getPrimaryAssignmentForGivenRange(securityUtils
                    .getCurrentUser().getId(), new Date(), new Date());
            String nextDesign = "";
            Assignment assignObj = null;
            List<Assignment> asignList = null;
            if (approvalPosition != null)
                assignObj = assignmentService.getPrimaryAssignmentForPositon(approvalPosition);
            if (assignObj != null) {
                asignList = new ArrayList<Assignment>();
                asignList.add(assignObj);
            } else if (assignObj == null && approvalPosition != null)
                asignList = assignmentService.getAssignmentsForPosition(approvalPosition, new Date());
            nextDesign = asignList != null && !asignList.isEmpty() ? asignList.get(0).getDesignation().getName() : "";

            final String pathVars = sewerageApplicationDetails.getApplicationNumber() + ","
                    + sewerageTaxUtils.getApproverName(approvalPosition) + ","
                    + (currentUserAssignment != null ? currentUserAssignment.getDesignation().getName() : "") + ","
                    + (nextDesign != null ? nextDesign : "");
            return "redirect:/transactions/application-success?pathVars=" + pathVars;
        } else
            return loadViewData(model, request, sewerageApplicationDetails);
    }

    private void populateEstimationDetails(final SewerageApplicationDetails sewerageApplicationDetails) {
        final List<SewerageConnectionEstimationDetails> sewerageConnectionEstimationDetailList = new ArrayList<SewerageConnectionEstimationDetails>();
        if (!sewerageApplicationDetails.getEstimationDetails().isEmpty())
            for (final SewerageConnectionEstimationDetails sewerageConnectionEstimationDetails : sewerageApplicationDetails
                    .getEstimationDetails()) {
                if (validSewerageConnectioEstimationDetail(sewerageConnectionEstimationDetails)) {
                    sewerageConnectionEstimationDetails.setApplicationDetails(sewerageApplicationDetails);
                    sewerageConnectionEstimationDetailList.add(sewerageConnectionEstimationDetails);
                }
            }
        sewerageApplicationDetails.getEstimationDetails().clear();
        sewerageApplicationDetails.setEstimationDetails(sewerageConnectionEstimationDetailList);
    }

    private void populateFeesDetails(final SewerageApplicationDetails sewerageApplicationDetails) {
        final List<SewerageConnectionFee> sewerageConnectionFeeList = new ArrayList<SewerageConnectionFee>();
        if (!sewerageApplicationDetails.getConnectionFees().isEmpty())
            for (final SewerageConnectionFee scf : sewerageApplicationDetails.getConnectionFees()) {
                scf.setApplicationDetails(sewerageApplicationDetails);
                sewerageConnectionFeeList.add(scf);
            }
        sewerageApplicationDetails.getConnectionFees().clear();
        sewerageApplicationDetails.setConnectionFees(sewerageConnectionFeeList);
    }

    private void populateInspectionDetails(final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpServletRequest request, final MultipartFile[] files) {
        new ArrayList<SewerageFieldInspectionDetails>();
        final String inspectionDate = request.getParameter("inspectionDate");
        if (!sewerageApplicationDetails.getFieldInspections().isEmpty())
            for (final SewerageFieldInspection sewerageFieldInspection : sewerageApplicationDetails
                    .getFieldInspections()) {
                sewerageFieldInspection.setApplicationDetails(sewerageApplicationDetails);
                sewerageFieldInspection.setInspectionDate(new Date(inspectionDate));
                final Set<FileStoreMapper> fileStoreSet = addToFileStore(files);
                Iterator<FileStoreMapper> fsIterator = null;
                if (fileStoreSet != null && !fileStoreSet.isEmpty())
                    fsIterator = fileStoreSet.iterator();

                if (fsIterator != null && fsIterator.hasNext())
                    sewerageFieldInspection.setFileStore(fsIterator.next());

                if (!sewerageApplicationDetails.getFieldInspections().isEmpty())
                    for (final SewerageFieldInspectionDetails sewerageFieldInspectionDtl : sewerageFieldInspection
                            .getFieldInspectionDetails())
                        sewerageFieldInspectionDtl.setFieldInspection(sewerageFieldInspection);
            }
    }

    private boolean validSewerageConnectioEstimationDetail(
            final SewerageConnectionEstimationDetails sewerageConnectionEstimationDetails) {
        if (sewerageConnectionEstimationDetails == null || sewerageConnectionEstimationDetails != null
                && sewerageConnectionEstimationDetails.getItemDescription() == null)
            return false;
        return true;
    }

    private Set<FileStoreMapper> addToFileStore(final MultipartFile[] files) {
        if (ArrayUtils.isNotEmpty(files))
            return Arrays
                    .asList(files)
                    .stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        try {
                            return fileStoreService.store(file.getInputStream(), file.getOriginalFilename(),
                                    file.getContentType(), SewerageTaxConstants.FILESTORE_MODULECODE);
                        } catch (final Exception e) {
                            throw new ApplicationRuntimeException("Error occurred while getting inputstream", e);
                        }
                    }).collect(Collectors.toSet());
        else
            return null;
    }
}
