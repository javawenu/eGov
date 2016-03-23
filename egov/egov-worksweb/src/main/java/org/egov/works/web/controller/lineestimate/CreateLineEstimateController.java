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
package org.egov.works.web.controller.lineestimate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.dao.EgwTypeOfWorkHibernateDAO;
import org.egov.commons.dao.FunctionHibernateDAO;
import org.egov.commons.dao.FundHibernateDAO;
import org.egov.dao.budget.BudgetGroupDAO;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.exception.ApplicationException;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.services.masters.SchemeService;
import org.egov.works.lineestimate.entity.Beneficiary;
import org.egov.works.lineestimate.entity.DocumentDetails;
import org.egov.works.lineestimate.entity.LineEstimate;
import org.egov.works.lineestimate.entity.ModeOfAllotment;
import org.egov.works.lineestimate.entity.TypeOfSlum;
import org.egov.works.lineestimate.entity.WorkCategory;
import org.egov.works.lineestimate.entity.enums.LineEstimateStatus;
import org.egov.works.lineestimate.service.LineEstimateService;
import org.egov.works.master.services.NatureOfWorkService;
import org.egov.works.utils.WorksConstants;
import org.egov.works.utils.WorksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
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
@RequestMapping(value = "/lineestimate")
public class CreateLineEstimateController extends GenericWorkFlowController{
    
    private static final int BUFFER_SIZE = 4096;

    @Autowired
    private LineEstimateService lineEstimateService;

    @Autowired
    private FundHibernateDAO fundHibernateDAO;

    @Autowired
    private FunctionHibernateDAO functionHibernateDAO;

    @Autowired
    private BudgetGroupDAO budgetGroupDAO;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private WorksUtils worksUtils;

    @Autowired
    private NatureOfWorkService natureOfWorkService;

    @Autowired
    private EgwTypeOfWorkHibernateDAO egwTypeOfWorkHibernateDAO;

    @Autowired
    private BoundaryService boundaryService;
    
    @Autowired
    private SecurityUtils securityUtils;
    
    @Autowired
    protected AssignmentService assignmentService;
    
    @Autowired
    private ResourceBundleMessageSource messageSource;
    
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;

    @RequestMapping(value = "/newform", method = RequestMethod.GET)
    public String showNewLineEstimateForm(@ModelAttribute("lineEstimate") final LineEstimate lineEstimate,
            final Model model) throws ApplicationException {
        setDropDownValues(model);
        model.addAttribute("lineEstimate", lineEstimate);
        
        model.addAttribute("stateType", lineEstimate.getClass().getSimpleName());

        model.addAttribute("additionalRule", WorksConstants.NEWLINEESTIMATE);

        prepareWorkflow(model, lineEstimate, new WorkflowContainer());
        
        model.addAttribute("mode", null);
        
        return "newLineEstimate-form";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@ModelAttribute("lineEstimate") final LineEstimate lineEstimate,

            final Model model, final BindingResult errors, @RequestParam("file") final MultipartFile[] files,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request,
            @RequestParam String workFlowAction, final BindingResult resultBinder)
                    throws ApplicationException, IOException {
        setDropDownValues(model);
        
        if (errors.hasErrors()) {
            model.addAttribute("stateType", lineEstimate.getClass().getSimpleName());

            model.addAttribute("additionalRule", WorksConstants.NEWLINEESTIMATE);

            prepareWorkflow(model, lineEstimate, new WorkflowContainer());
            
            model.addAttribute("mode", null);
            
            return "newLineEstimate-form";
        }
        else {
            if (lineEstimate.getState() == null)
                lineEstimate.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(WorksConstants.MODULETYPE, 
                        LineEstimateStatus.CREATED.toString()));
            
            Long approvalPosition = 0l;
            String approvalComment = "";
            if (request.getParameter("approvalComment") != null)
                approvalComment = request.getParameter("approvalComent");
            if (request.getParameter("workFlowAction") != null)
                workFlowAction = request.getParameter("workFlowAction");
            if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
                approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));
            
            final LineEstimate newLineEstimate = lineEstimateService.create(lineEstimate, files, approvalPosition,
                    approvalComment, WorksConstants.NEWLINEESTIMATE, workFlowAction);
            model.addAttribute("lineEstimate", newLineEstimate);
            
            String pathVars = worksUtils.getPathVars(newLineEstimate, approvalPosition);

            return "redirect:/lineestimate/lineestimate-success?pathVars=" + pathVars;
        }
    }

    private void setDropDownValues(final Model model ) {
        model.addAttribute("funds", fundHibernateDAO.findAllActiveFunds());
        model.addAttribute("functions", functionHibernateDAO.getAllActiveFunctions());
        model.addAttribute("budgetHeads", budgetGroupDAO.getBudgetGroupList());
        model.addAttribute("schemes", schemeService.findAll());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("workCategory",WorkCategory.values());
        model.addAttribute("typeOfSlum", TypeOfSlum.values());
        model.addAttribute("beneficiary", Beneficiary.values());
        model.addAttribute("modeOfAllotment", ModeOfAllotment.values());
        model.addAttribute("typeOfWork", egwTypeOfWorkHibernateDAO.getTypeOfWorkForPartyTypeContractor());
        model.addAttribute("natureOfWork", natureOfWorkService.findAll());
        
    }
    
    @RequestMapping(value = "/downloadLineEstimateDoc", method = RequestMethod.GET)
    public void getLineEstimateDoc(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        final ServletContext context = request.getServletContext();
        final String fileStoreId = request.getParameter("fileStoreId");
        String fileName = "";
        final File downloadFile = fileStoreService.fetch(fileStoreId,
                WorksConstants.FILESTORE_MODULECODE);
        final FileInputStream inputStream = new FileInputStream(downloadFile);
        LineEstimate lineEstimate = lineEstimateService
                .getLineEstimateById(Long.parseLong(request.getParameter("lineEstimateId")));
        lineEstimate = getEstimateDocuments(lineEstimate);

        for (final DocumentDetails doc : lineEstimate.getDocumentDetails())
            if (doc.getFileStore().getFileStoreId().equalsIgnoreCase(fileStoreId))
                fileName = doc.getFileStore().getFileName();

        // get MIME type of the file
        String mimeType = context.getMimeType(downloadFile.getAbsolutePath());
        if (mimeType == null)
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";

        // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());

        // set headers for the response
        final String headerKey = "Content-Disposition";
        final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        final OutputStream outStream = response.getOutputStream();

        final byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;

        // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1)
            outStream.write(buffer, 0, bytesRead);

        inputStream.close();
        outStream.close();
    }

    private LineEstimate getEstimateDocuments(final LineEstimate lineEstimate) {
        List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
        documentDetailsList = worksUtils.findByObjectIdAndObjectType(lineEstimate.getId(),
                WorksConstants.MODULE_NAME_LINEESTIMATE);
        lineEstimate.setDocumentDetails(documentDetailsList);
        return lineEstimate;
    }
    
    @RequestMapping(value = "/lineestimate-success", method = RequestMethod.GET)
    public ModelAndView successView(@ModelAttribute LineEstimate lineEstimate,
            final HttpServletRequest request, final Model model, final ModelMap modelMap) {

        final String[] keyNameArray = request.getParameter("pathVars").split(",");
        Long id = 0L;
        String approverName = "";
        String currentUserDesgn = "";
        String nextDesign = "";
        if (keyNameArray.length != 0 && keyNameArray.length > 0)
            if (keyNameArray.length == 1)
                id = Long.parseLong(keyNameArray[0]);
            else if (keyNameArray.length == 3) {
                id = Long.parseLong(keyNameArray[0]);
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
            } else {
                id = Long.parseLong(keyNameArray[0]);
                approverName = keyNameArray[1];
                currentUserDesgn = keyNameArray[2];
                nextDesign = keyNameArray[3];
            }

        if (id != null)
            lineEstimate = lineEstimateService
                    .getLineEstimateById(id);
        model.addAttribute("approverName", approverName);
        model.addAttribute("currentUserDesgn", currentUserDesgn);
        model.addAttribute("nextDesign", nextDesign);

        String message = getMessageByStatus(lineEstimate, approverName, nextDesign);;
        
        model.addAttribute("message", message);
        
        return new ModelAndView("lineestimate-success", "lineEstimate", lineEstimate);
    }

    private String getMessageByStatus(LineEstimate lineEstimate, String approverName, String nextDesign) {
        String message = "";
        
        if(lineEstimate.getStatus().getCode().equals(LineEstimateStatus.CREATED.toString()) && !lineEstimate.getState().getValue().equals(WorksConstants.WF_STATE_REJECTED)) {
            message = messageSource.getMessage("msg.lineestimate.create.success",
                    new String[] { approverName, nextDesign, lineEstimate.getLineEstimateNumber() }, null);
        }
        else if(lineEstimate.getStatus().getCode().equals(LineEstimateStatus.CHECKED.toString())) {
            message = messageSource.getMessage("msg.lineestimate.check.success",
                    new String[] { lineEstimate.getLineEstimateNumber(), approverName, nextDesign }, null);
        }
        else if(lineEstimate.getStatus().getCode().equals(LineEstimateStatus.BUDGET_SANCTIONED.toString())) {
            message = messageSource.getMessage("msg.lineestimate.budgetsanction.success",
                    new String[] { lineEstimate.getLineEstimateNumber(), approverName, nextDesign }, null);
        }
        else if(lineEstimate.getStatus().getCode().equals(LineEstimateStatus.ADMINISTRATIVE_SANCTIONED.toString())) {
            message = messageSource.getMessage("msg.lineestimate.adminsanction.success",
                    new String[] { lineEstimate.getLineEstimateNumber(), approverName, nextDesign, lineEstimate.getAdminSanctionNumber() }, null);
        }
        else if(lineEstimate.getStatus().getCode().equals(LineEstimateStatus.TECHNICAL_SANCTIONED.toString())) {
            message = messageSource.getMessage("msg.lineestimate.techsanction.success",
                    new String[] { lineEstimate.getLineEstimateNumber(), lineEstimate.getTechnicalSanctionNumber() }, null);
        }
        else if(lineEstimate.getState().getValue().equals(WorksConstants.WF_STATE_REJECTED)) {
            message = messageSource.getMessage("msg.lineestimate.reject",
                    new String[] { lineEstimate.getLineEstimateNumber(), approverName, nextDesign }, null);
        }
        else if(lineEstimate.getStatus().getCode().equals(LineEstimateStatus.CANCELLED.toString())) {
            message = messageSource.getMessage("msg.lineestimate.cancel",
                    new String[] { lineEstimate.getLineEstimateNumber() }, null);
        }
        
        return message;
    }
}