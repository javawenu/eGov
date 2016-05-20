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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.PropertyTaxDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.stms.masters.entity.SewerageApplicationType;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.entity.enums.SewerageConnectionStatus;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnection;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.egov.stms.transactions.service.SewerageConnectionService;
import org.egov.stms.transactions.service.SewerageThirdPartyServices;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/transactions")
public class SewerageConnectionController extends GenericWorkFlowController {

    private static final Logger LOG = LoggerFactory.getLogger(SewerageConnectionController.class);

    private static final int BUFFER_SIZE = 4096;

    private final SewerageTaxUtils sewerageTaxUtils;

    private final SewerageApplicationDetailsService sewerageApplicationDetailsService;

    @Autowired
    private SewerageConnectionService sewerageConnectionService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    protected AssignmentService assignmentService;

    
    @Autowired
    private PropertyExternalService propertyExternalService;

    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;

    @Autowired
    private SewerageThirdPartyServices sewerageThirdPartyServices;

    @Autowired
    public SewerageConnectionController(final SewerageTaxUtils sewerageTaxUtils,
            final SewerageApplicationDetailsService sewerageApplicationDetailsService) {
        this.sewerageTaxUtils = sewerageTaxUtils;
        this.sewerageApplicationDetailsService = sewerageApplicationDetailsService;
    }

    @RequestMapping(value = "/newConnection-newform", method = RequestMethod.GET)
    public String showNewApplicationForm(@ModelAttribute final SewerageApplicationDetails sewerageApplicationDetails,
            final Model model, final HttpServletRequest request) {
        LOG.debug("Inside showNewApplicationForm method");
        final SewerageApplicationType applicationType = new SewerageApplicationType();
        applicationType.setId(1L);
        sewerageApplicationDetails.setApplicationType(applicationType);
        sewerageApplicationDetails.setApplicationDate(new Date());
        final SewerageConnection connection = new SewerageConnection();
        connection.setStatus(SewerageConnectionStatus.INPROGRESS);
        sewerageApplicationDetails.setConnection(connection);

        model.addAttribute("allowIfPTDueExists", sewerageTaxUtils.isNewConnectionAllowedIfPTDuePresent());
        model.addAttribute("propertyTypes", PropertyType.values());

        model.addAttribute("additionalRule", sewerageApplicationDetails.getApplicationType().getCode());
        prepareWorkflow(model, sewerageApplicationDetails, new WorkflowContainer());
        model.addAttribute("currentUser", sewerageTaxUtils.getCurrentUserRole(securityUtils.getCurrentUser()));
        model.addAttribute("stateType", sewerageApplicationDetails.getClass().getSimpleName());
        model.addAttribute("typeOfConnection", SewerageTaxConstants.NEWSEWERAGECONNECTION);
        model.addAttribute("mode", null);
        return "newconnection-form";
    }

    @RequestMapping(value = "/newConnection-create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final SewerageApplicationDetails sewerageApplicationDetails,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Model model, @RequestParam String workFlowAction,
            final BindingResult errors, @RequestParam("files") final MultipartFile[] files) {

        validatePropertyID(sewerageApplicationDetails, resultBinder);
        if (sewerageApplicationDetails.getState() == null)
            sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                    SewerageTaxConstants.APPLICATION_STATUS_CREATED, SewerageTaxConstants.MODULETYPE));
        if (LOG.isDebugEnabled())
            LOG.error("Model Level Validation occurs = " + resultBinder);

        if (resultBinder.hasErrors()) {
            validatePropertyID(sewerageApplicationDetails, resultBinder);
            sewerageApplicationDetails.setApplicationDate(new Date());
            model.addAttribute("validateIfPTDueExists", sewerageTaxUtils.isNewConnectionAllowedIfPTDuePresent());
            model.addAttribute("propertyTypes", PropertyType.values());

            prepareWorkflow(model, sewerageApplicationDetails, new WorkflowContainer());
            model.addAttribute("additionalRule", sewerageApplicationDetails.getApplicationType().getCode());
            model.addAttribute("currentUser", sewerageTaxUtils.getCurrentUserRole(securityUtils.getCurrentUser()));
            model.addAttribute("approvalPosOnValidate", request.getParameter("approvalPosition"));
            model.addAttribute("stateType", sewerageApplicationDetails.getClass().getSimpleName());

            return "newconnection-form";
        }

        final Set<FileStoreMapper> fileStoreSet = addToFileStore(files);
        Iterator<FileStoreMapper> fsIterator = null;
        if (fileStoreSet != null && !fileStoreSet.isEmpty())
            fsIterator = fileStoreSet.iterator();
        if (fsIterator != null && fsIterator.hasNext())
            sewerageApplicationDetails.setFileStore(fsIterator.next());

        Long approvalPosition = 0l;
        String approvalComment = "";
        if (request.getParameter("approvalComment") != null)
            approvalComment = request.getParameter("approvalComent");
        if (request.getParameter("workFlowAction") != null)
            workFlowAction = request.getParameter("workFlowAction");
        if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
            approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));
        sewerageTaxUtils.getCurrentUserRole(securityUtils.getCurrentUser());

        final SewerageApplicationDetails newSewerageApplicationDetails = sewerageApplicationDetailsService
                .createNewSewerageConnection(sewerageApplicationDetails, approvalPosition, approvalComment,
                        sewerageApplicationDetails.getApplicationType().getCode(), workFlowAction);

        final Assignment currentUserAssignment = assignmentService.getPrimaryAssignmentForGivenRange(securityUtils
                .getCurrentUser().getId(), new Date(), new Date());

        Assignment assignObj = null;
        List<Assignment> asignList = null;
        if (approvalPosition != null)
            assignObj = assignmentService.getPrimaryAssignmentForPositon(approvalPosition);

        if (assignObj != null) {
            asignList = new ArrayList<Assignment>();
            asignList.add(assignObj);
        } else if (assignObj == null && approvalPosition != null)
            asignList = assignmentService.getAssignmentsForPosition(approvalPosition, new Date());

        final String nextDesign = !asignList.isEmpty() ? asignList.get(0).getDesignation().getName() : "";

        final String pathVars = newSewerageApplicationDetails.getApplicationNumber() + ","
                + sewerageTaxUtils.getApproverName(approvalPosition) + ","
                + (currentUserAssignment != null ? currentUserAssignment.getDesignation().getName() : "") + ","
                + (nextDesign != null ? nextDesign : "");

        return "redirect:/transactions/application-success?pathVars=" + pathVars;
    }

    @RequestMapping(value = "/application-success", method = RequestMethod.GET)
    public ModelAndView successView(@ModelAttribute SewerageApplicationDetails sewerageApplicationDetails,
            final HttpServletRequest request, final Model model, final ModelMap modelMap) {

        final String[] keyNameArray = request.getParameter("pathVars").split(",");
        String applicationNumber = "";
        String approverName = "";
        String currentUserDesgn = "";
        String nextDesign = "";
        if (keyNameArray.length != 0 && keyNameArray.length > 0)
            if (keyNameArray.length == 1)
                applicationNumber = keyNameArray[0];
            else if (keyNameArray.length == 3) {
                applicationNumber = keyNameArray[0];
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
            } else {
                applicationNumber = keyNameArray[0];
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
                nextDesign = keyNameArray[3];
            }

        if (applicationNumber != null)
            sewerageApplicationDetails = sewerageApplicationDetailsService.findByApplicationNumber(applicationNumber);
        model.addAttribute("approverName", approverName);
        model.addAttribute("currentUserDesgn", currentUserDesgn);
        model.addAttribute("nextDesign", nextDesign);

        model.addAttribute("cityName", EgovThreadLocals.getCityName());
        model.addAttribute("mode", "ack");
        setCommonDetails(sewerageApplicationDetails, modelMap, request);
        return new ModelAndView("application-success", "sewerageApplicationDetails", sewerageApplicationDetails);
    }

    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void generateEstimationNotice(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final SewerageApplicationDetails sewerageApplicationDetails = sewerageApplicationDetailsService
                .findByApplicationNumber(request.getParameter("applicationNumber"));
        ServletContext context = request.getServletContext();
        File downloadFile = fileStoreService.fetch(sewerageApplicationDetails.getFileStore().getFileStoreId(),
                SewerageTaxConstants.FILESTORE_MODULECODE);
        FileInputStream inputStream = new FileInputStream(downloadFile);

        // get MIME type of the file
        String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);

        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", sewerageApplicationDetails.getFileStore()
                .getFileName());
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        OutputStream outStream = response.getOutputStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();
    }

    private void setCommonDetails(final SewerageApplicationDetails sewerageApplicationDetails, final ModelMap modelMap,
            final HttpServletRequest request) {
        String assessmentNumber = sewerageApplicationDetails.getConnection().getConnectionDetail()
                .getPropertyIdentifier();

        AssessmentDetails propertyOwnerDetails = sewerageThirdPartyServices.getPropertyDetails(sewerageApplicationDetails,
                assessmentNumber, request);
        if (propertyOwnerDetails != null) {
            modelMap.addAttribute("propertyOwnerDetails", propertyOwnerDetails);
        }
        final PropertyTaxDetails propertyTaxDetails = propertyExternalService.getPropertyTaxDetails(assessmentNumber);
        modelMap.addAttribute("propertyTax", propertyTaxDetails.getTotalTaxAmt());

        final BigDecimal sewerageTaxDue = sewerageConnectionService.getTotalAmount(sewerageApplicationDetails
                .getConnection());
        modelMap.addAttribute("sewerageTaxDue", sewerageTaxDue);
    }

    private void validatePropertyID(final SewerageApplicationDetails sewerageApplicationDetails,
            final BindingResult errors) {
        if (sewerageApplicationDetails.getConnection() != null
                && sewerageApplicationDetails.getConnection().getConnectionDetail().getPropertyIdentifier() != null
                && !sewerageApplicationDetails.getConnection().getConnectionDetail().getPropertyIdentifier().equals("")) {
            String errorMessage = sewerageApplicationDetailsService
                    .checkValidPropertyAssessmentNumber(sewerageApplicationDetails.getConnection()
                            .getConnectionDetail().getPropertyIdentifier());
            if (errorMessage != null && !errorMessage.equals(""))
                errors.rejectValue("connection.propertyIdentifier", errorMessage, errorMessage);
            else {
                errorMessage = sewerageApplicationDetailsService
                        .checkConnectionPresentForProperty(sewerageApplicationDetails.getConnection()
                                .getConnectionDetail().getPropertyIdentifier());
                if (errorMessage != null && !errorMessage.equals(""))
                    errors.rejectValue("connection.propertyIdentifier", errorMessage, errorMessage);
            }
        }
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
