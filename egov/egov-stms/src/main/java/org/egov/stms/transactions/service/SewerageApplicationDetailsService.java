/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 *
 * Copyright (C) <2015> eGovernments Foundation
 *
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 *
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 *
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 *
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 *
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 *
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.stms.transactions.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;

import org.egov.demand.model.EgDemand;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.search.elastic.entity.ApplicationIndex;
import org.egov.infra.search.elastic.entity.ApplicationIndexBuilder;
import org.egov.infra.search.elastic.entity.enums.ApprovalStatus;
import org.egov.infra.search.elastic.service.ApplicationIndexService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.ApplicationNumberGenerator;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.infstr.workflow.WorkFlowMatrix;
import org.egov.pims.commons.Position;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.OwnerName;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.stms.masters.entity.SewerageApplicationType;
import org.egov.stms.masters.entity.enums.SewerageConnectionStatus;
import org.egov.stms.masters.repository.SewerageApplicationTypeRepository;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.repository.SewerageApplicationDetailsRepository;
import org.egov.stms.transactions.workflow.ApplicationWorkflowCustomDefaultImpl;
import org.egov.stms.utils.SewerageTaxNumberGenerator;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewerageApplicationDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(SewerageApplicationDetailsService.class);

    @Autowired
    private SewerageTaxUtils sewerageTaxUtils;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    protected SewerageApplicationDetailsRepository sewerageApplicationDetailsRepository;

    @Autowired
    private SewerageApplicationTypeRepository sewerageApplicationTypeRepository;

    @Autowired
    private ApplicationNumberGenerator applicationNumberGenerator;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private ApplicationIndexService applicationIndexService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SimpleWorkflowService<SewerageApplicationDetails> sewerageApplicationWorkflowService;

    @Autowired
    private SewerageTaxNumberGenerator sewerageTaxNumberGenerator;

    @Autowired
    private EisCommonService eisCommonService;

    @Autowired
    public SewerageApplicationDetailsService(final SewerageApplicationDetailsRepository sewerageApplicationDetailsRepository) {
        this.sewerageApplicationDetailsRepository = sewerageApplicationDetailsRepository;
    }

    public SewerageApplicationDetails findBy(final Long id) {
        return sewerageApplicationDetailsRepository.findOne(id);
    }

    public SewerageApplicationDetails findByApplicationNumber(final String applicationNumber) {
        return sewerageApplicationDetailsRepository.findByApplicationNumber(applicationNumber);
    }
    
    public SewerageApplicationDetails findByApplicationNumberAndConnectionStatus(final String applicationNumber, final SewerageConnectionStatus status) {
        return sewerageApplicationDetailsRepository.findByApplicationNumberAndConnection_ConnectionStatus(applicationNumber, status);
    }
    
    public SewerageApplicationDetails findByApplicationNumberOrConnection_DhscNumber(String applicationNumber) {
        return sewerageApplicationDetailsRepository.findByApplicationNumberOrConnection_DhscNumber(applicationNumber, applicationNumber);
    }

    @Transactional
    public SewerageApplicationDetails createNewSewerageConnection(final SewerageApplicationDetails sewerageApplicationDetails,
            final Long approvalPosition, final String approvalComent, final String additionalRule,
            final String workFlowAction) {
        if (sewerageApplicationDetails.getApplicationNumber() == null)
            sewerageApplicationDetails.setApplicationNumber(applicationNumberGenerator.generate());
        sewerageApplicationDetails.setApplicationDate(new Date());

        SewerageApplicationType applicationType = sewerageApplicationTypeRepository
                .findByCode(SewerageTaxConstants.NEWSEWERAGECONNECTION);
        Date disposalDate = getDisposalDate(sewerageApplicationDetails, applicationType.getProcessingTime());
        sewerageApplicationDetails.setDisposalDate(disposalDate);

        final SewerageApplicationDetails savedSewerageApplicationDetails = sewerageApplicationDetailsRepository
                .save(sewerageApplicationDetails);

        final ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = getInitialisedWorkFlowBean();
        if (LOG.isDebugEnabled())
            LOG.debug("applicationWorkflowCustomDefaultImpl initialization is done");

        applicationWorkflowCustomDefaultImpl.createCommonWorkflowTransition(savedSewerageApplicationDetails,
                approvalPosition, approvalComent, additionalRule, workFlowAction);
        
        updateIndexes(savedSewerageApplicationDetails);

        return savedSewerageApplicationDetails;
    }

    @Transactional
    public void save(final SewerageApplicationDetails detail) {
        sewerageApplicationDetailsRepository.save(detail);
    }

    public Date getDisposalDate(final SewerageApplicationDetails sewerageApplicationDetails, final Integer appProcessTime) {
        final Calendar c = Calendar.getInstance();
        c.setTime(sewerageApplicationDetails.getApplicationDate());
        c.add(Calendar.DATE, appProcessTime);
        return c.getTime();
    }

    public SewerageApplicationDetails getSewerageConnectionDetailsByPropertyIDentifier(final String propertyIdentifier) {
        return sewerageApplicationDetailsRepository.getSewerageConnectionDetailsByPropertyID(propertyIdentifier);
    }

    public String checkValidPropertyAssessmentNumber(final String asessmentNumber) {
        String errorMessage = "";
        final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(asessmentNumber,
                PropertyExternalService.FLAG_FULL_DETAILS);
        errorMessage = validateProperty(assessmentDetails);
        if (errorMessage.isEmpty())
            errorMessage = validatePTDue(asessmentNumber, assessmentDetails);
        return errorMessage;
    }

    /**
     * @param assessmentDetails
     * @return ErrorMessage If PropertyId is Not Valid
     */
    private String validateProperty(final AssessmentDetails assessmentDetails) {
        String errorMessage = "";
        if (assessmentDetails.getErrorDetails() != null && assessmentDetails.getErrorDetails().getErrorCode() != null)
            errorMessage = assessmentDetails.getErrorDetails().getErrorMessage();
        return errorMessage;
    }

    private String validatePTDue(final String asessmentNumber, final AssessmentDetails assessmentDetails) {
        String errorMessage = "";
        if (assessmentDetails.getPropertyDetails() != null
                && assessmentDetails.getPropertyDetails().getTaxDue() != null
                && assessmentDetails.getPropertyDetails().getTaxDue().doubleValue() > 0)

            /**
             * If property tax due present and configuration value is 'NO' then restrict not to allow new water tap connection
             * application. If configuration value is 'YES' then new water tap connection can be created even though there is
             * Property Tax Due present.
             **/
            if (!sewerageTaxUtils.isNewConnectionAllowedIfPTDuePresent())
                errorMessage = messageSource.getMessage("err.validate.property.taxdue", new String[] {
                        assessmentDetails.getPropertyDetails().getTaxDue().toString(), asessmentNumber, "new" }, null);
        return errorMessage;
    }

    public String checkConnectionPresentForProperty(final String propertyID) {
        String validationMessage = "";
        final SewerageApplicationDetails sewerageApplicationDetails = getSewerageConnectionDetailsByPropertyIDentifier(propertyID);
        if (sewerageApplicationDetails != null)
            if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                    .equalsIgnoreCase(SewerageConnectionStatus.ACTIVE.toString()))
                validationMessage = messageSource.getMessage("err.validate.newconnection.active", new String[] {
                        sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
            else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                    .equalsIgnoreCase(SewerageConnectionStatus.INPROGRESS.toString()))
                validationMessage = messageSource.getMessage("err.validate.newconnection.application.inprocess",
                        new String[] { propertyID, sewerageApplicationDetails.getApplicationNumber() }, null);
            else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                    .equalsIgnoreCase(SewerageConnectionStatus.CLOSED.toString()))
                validationMessage = messageSource
                        .getMessage("err.validate.newconnection.closed", new String[] {
                                sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
            else if (sewerageApplicationDetails.getConnection().getConnectionStatus().toString()
                    .equalsIgnoreCase(SewerageConnectionStatus.INACTIVE.toString()))
                validationMessage = messageSource.getMessage("err.validate.newconnection.inactive", new String[] {
                        sewerageApplicationDetails.getConnection().getDhscNumber(), propertyID }, null);
        return validationMessage;
    }

    /**
     * @return Initialise Bean ApplicationWorkflowCustomDefaultImpl
     */
    public ApplicationWorkflowCustomDefaultImpl getInitialisedWorkFlowBean() {
        ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = null;
        if (null != context)
            applicationWorkflowCustomDefaultImpl = (ApplicationWorkflowCustomDefaultImpl) context
                    .getBean("seweargeApplicationWorkflowCustomDefaultImpl");
        return applicationWorkflowCustomDefaultImpl;
    }

    public void updateIndexes(final SewerageApplicationDetails sewerageApplicationDetails) {
        final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(
                sewerageApplicationDetails.getConnection().getPropertyIdentifier(), PropertyExternalService.FLAG_FULL_DETAILS);
        if (LOG.isDebugEnabled())
            LOG.debug(" updating Indexes Started... ");
        if (sewerageApplicationDetails.getConnection().getDhscNumber() != null)
            getTotalAmount(sewerageApplicationDetails);
        Iterator<OwnerName> ownerNameItr = null;
        if (null != assessmentDetails.getOwnerNames())
            ownerNameItr = assessmentDetails.getOwnerNames().iterator();
        final StringBuilder consumerName = new StringBuilder();
        final StringBuilder mobileNumber = new StringBuilder();
        Assignment assignment = null;
        User user = null;
        Integer elapsedDays = 0;
        final StringBuilder aadharNumber = new StringBuilder();
        if (null != ownerNameItr && ownerNameItr.hasNext()) {
            final OwnerName primaryOwner = ownerNameItr.next();
            consumerName.append(primaryOwner.getOwnerName() != null ? primaryOwner.getOwnerName() : "");
            mobileNumber.append(primaryOwner.getMobileNumber() != null ? primaryOwner.getMobileNumber() : "");
            aadharNumber.append(primaryOwner.getAadhaarNumber() != null ? primaryOwner.getAadhaarNumber() : "");
            while (ownerNameItr.hasNext()) {
                final OwnerName secondaryOwner = ownerNameItr.next();
                consumerName.append(",").append(secondaryOwner.getOwnerName() != null ? secondaryOwner.getOwnerName() : "");
                mobileNumber.append(",").append(secondaryOwner.getMobileNumber() != null ? secondaryOwner.getMobileNumber() : "");
                aadharNumber.append(",").append(
                        secondaryOwner.getAadhaarNumber() != null ? secondaryOwner.getAadhaarNumber() : "");
            }

        }
        List<Assignment> asignList = null;
        if (sewerageApplicationDetails.getState() != null && sewerageApplicationDetails.getState().getOwnerPosition() != null) {
            assignment = assignmentService.getPrimaryAssignmentForPositionAndDate(sewerageApplicationDetails.getState()
                    .getOwnerPosition()
                    .getId(), new Date());
            if (assignment != null) {
                asignList = new ArrayList<Assignment>();
                asignList.add(assignment);
            } else if (assignment == null)
                asignList = assignmentService
                        .getAssignmentsForPosition(sewerageApplicationDetails.getState().getOwnerPosition().getId(), new Date());
            if (!asignList.isEmpty())
                user = userService.getUserById(asignList.get(0).getEmployee().getId());
        } else
            user = securityUtils.getCurrentUser();
        ApplicationIndex applicationIndex = applicationIndexService
                .findByApplicationNumber(sewerageApplicationDetails.getApplicationNumber());
        if (applicationIndex != null && null != sewerageApplicationDetails.getId()
                && sewerageApplicationDetails.getStatus() != null
                && !sewerageApplicationDetails.getStatus().getCode().equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED)) {
            if (sewerageApplicationDetails.getStatus() != null
                    && (sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_APPROVED)
                            || sewerageApplicationDetails.getStatus().getCode()
                                    .equals(SewerageTaxConstants.APPLICATION_STATUS_VERIFIED)
                            || sewerageApplicationDetails.getStatus().getCode()
                                    .equals(SewerageTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN)
                            || sewerageApplicationDetails.getStatus().getCode()
                                    .equals(SewerageTaxConstants.APPLICATION_STATUS_FEEPAID)
                            || sewerageApplicationDetails.getStatus().getCode()
                                    .equals(SewerageTaxConstants.APPLICATION_STATUS_CANCELLED)
                            || sewerageApplicationDetails.getStatus().getCode()
                                    .equals(SewerageTaxConstants.APPLICATION_STATUS_WOGENERATED)
                            || sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_SANCTIONED))
                            || sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_CHECKED)) {
                applicationIndex.setStatus(sewerageApplicationDetails.getStatus().getDescription());
                applicationIndex.setApplicantAddress(assessmentDetails.getPropertyAddress());
                applicationIndex.setOwnername(user.getUsername() + "::" + user.getName());
                if (sewerageApplicationDetails.getConnection().getDhscNumber() != null)
                    applicationIndex.setConsumerCode(sewerageApplicationDetails.getConnection().getDhscNumber());
                if(sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_APPROVED) || sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_WOGENERATED))
                    applicationIndex.setApproved(ApprovalStatus.APPROVED);
                else if(sewerageApplicationDetails.getStatus().getCode()
                        .equals(SewerageTaxConstants.APPLICATION_STATUS_CANCELLED)) {
                    elapsedDays = (int) TimeUnit.DAYS.convert(new Date().getTime() - sewerageApplicationDetails.getApplicationDate().getTime(), TimeUnit.MILLISECONDS);
                    applicationIndex.setElapsedDays(elapsedDays);
                    applicationIndex.setApproved(ApprovalStatus.REJECTED);
                }
                else if(sewerageApplicationDetails.getStatus().getCode()
                        .equals(SewerageTaxConstants.APPLICATION_STATUS_SANCTIONED)) {
                    elapsedDays = (int) TimeUnit.DAYS.convert(new Date().getTime() - sewerageApplicationDetails.getApplicationDate().getTime(), TimeUnit.MILLISECONDS);
                    applicationIndex.setElapsedDays(elapsedDays);
                }
                else
                    applicationIndex.setApproved(ApprovalStatus.UNKNOWN);
                applicationIndexService.updateApplicationIndex(applicationIndex);
            }
            
         // making connection active only on Sanction
            if (sewerageApplicationDetails.getStatus().getCode().equals(SewerageTaxConstants.APPLICATION_STATUS_SANCTIONED))
                if (sewerageApplicationDetails.getConnection().getConnectionStatus().equals(SewerageConnectionStatus.INPROGRESS))
                    sewerageApplicationDetails.getConnection().setConnectionStatus(SewerageConnectionStatus.ACTIVE);
        } else {

            if (sewerageApplicationDetails.getApplicationDate() == null)
                sewerageApplicationDetails.setApplicationDate(new Date());
            if (applicationIndex == null) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Application Index creation Started... ");
                
                //TODO: TEMPORARILY COMMENTED TO FIX COMPILATION ISSUES. CHECK AGAIN.
                
                final ApplicationIndexBuilder applicationIndexBuilder = null;/*new ApplicationIndexBuilder(
                        SewerageTaxConstants.APPL_INDEX_MODULE_NAME, sewerageApplicationDetails.getApplicationNumber(),
                        sewerageApplicationDetails.getApplicationDate(), sewerageApplicationDetails.getApplicationType().getName(),
                        consumerName.toString(), sewerageApplicationDetails.getStatus().getDescription().toString(),
                        "/stms/application/view/" + sewerageApplicationDetails.getApplicationNumber(),
                        assessmentDetails.getPropertyAddress(), user.getUsername() + "::" + user.getName())*/;

                if (sewerageApplicationDetails.getDisposalDate() != null)
                    applicationIndexBuilder.disposalDate(sewerageApplicationDetails.getDisposalDate());
                applicationIndexBuilder.mobileNumber(mobileNumber.toString());
                applicationIndexBuilder.aadharNumber(aadharNumber.toString());

                applicationIndex = applicationIndexBuilder.build();
                if(!sewerageApplicationDetails.getStatus().getCode()
                        .equals(SewerageTaxConstants.APPLICATION_STATUS_SANCTIONED)) {
                    applicationIndex.setApproved(ApprovalStatus.UNKNOWN);
                    applicationIndexService.createApplicationIndex(applicationIndex);
                }    
            }
            if (LOG.isDebugEnabled())
                LOG.debug("Application Index creation completed...");
        
        }
    }

    public BigDecimal getTotalAmount(final SewerageApplicationDetails sewerageApplicationDetails) {
        final EgDemand currentDemand = sewerageApplicationDetails.getConnection().getDemand();
        BigDecimal balance = BigDecimal.ZERO;
        if (currentDemand != null) {
            /*
             * final List<Object> instVsAmt = connectionDemandService.getDmdCollAmtInstallmentWise(currentDemand); for (final
             * Object object : instVsAmt) { final Object[] ddObject = (Object[]) object; final BigDecimal dmdAmt = (BigDecimal)
             * ddObject[2]; BigDecimal collAmt = BigDecimal.ZERO; if (ddObject[2] != null) collAmt = new BigDecimal((Double)
             * ddObject[3]); balance = balance.add(dmdAmt.subtract(collAmt)); }
             */
        }
        return balance;
    }

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public Long getApprovalPositionByMatrixDesignation(final SewerageApplicationDetails sewerageApplicationDetails,
            Long approvalPosition, final String additionalRule, final String mode, final String workFlowAction) {
        final WorkFlowMatrix wfmatrix = sewerageApplicationWorkflowService.getWfMatrix(sewerageApplicationDetails
                .getStateType(), null, null, additionalRule, sewerageApplicationDetails.getCurrentState().getValue(), null);
        if (sewerageApplicationDetails.getStatus() != null && sewerageApplicationDetails.getStatus().getCode() != null)
            if (sewerageApplicationDetails.getStatus().getCode().equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED)
                    && sewerageApplicationDetails.getState() != null)
                if (mode.equals("edit"))
                    approvalPosition = sewerageApplicationDetails.getState().getOwnerPosition().getId();
                else
                    approvalPosition = sewerageTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                            sewerageApplicationDetails);
            else if (sewerageApplicationDetails.getStatus().getCode().equals(SewerageTaxConstants.APPLICATION_STATUS_APPROVED)
                    || SewerageTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN.equalsIgnoreCase(sewerageApplicationDetails
                            .getStatus().getCode())
                    || !"".equals(workFlowAction)
                    && workFlowAction.equals(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)
                    && sewerageApplicationDetails.getState().getValue().equals("Rejected"))
                approvalPosition = sewerageTaxUtils
                        .getApproverPosition(wfmatrix.getNextDesignation(), sewerageApplicationDetails);
            else if (wfmatrix.getNextDesignation() != null
                    && (sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_FEEPAID)
                    || workFlowAction.equals(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)))
                approvalPosition = sewerageTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                        sewerageApplicationDetails);
            else if (wfmatrix.getNextDesignation() != null
                    && (sewerageApplicationDetails.getStatus().getCode()
                            .equals(SewerageTaxConstants.APPLICATION_STATUS_VERIFIED)
                            || !workFlowAction.equals(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)
                            || !sewerageApplicationDetails
                            .getState().getValue().equals(SewerageTaxConstants.WF_STATE_REJECTED)
                            && !workFlowAction.equals(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT))) {
                final Position posobj = sewerageTaxUtils.getCityLevelExecutiveEngineerPosition(wfmatrix.getNextDesignation());
                if (posobj != null)
                    approvalPosition = posobj.getId();
            }
        if (wfmatrix.getNextDesignation() != null
                && wfmatrix.getNextDesignation().equalsIgnoreCase(SewerageTaxConstants.DESIGNATION_DEPUTY_EXE_ENGINEER)) {
            final Position posobj = sewerageTaxUtils.getCityLevelDeputyEngineerPosition(wfmatrix.getNextDesignation());
            if (posobj != null)
                approvalPosition = posobj.getId();
        }
        if (wfmatrix.getNextDesignation() != null
                && wfmatrix.getNextDesignation().equalsIgnoreCase(SewerageTaxConstants.DESIGNATION_EXE_ENGINEER)) {
            final Position posobj = sewerageTaxUtils.getCityLevelExecutiveEngineerPosition(wfmatrix.getNextDesignation());
            if (posobj != null)
                approvalPosition = posobj.getId();
        }
        if (sewerageApplicationDetails.getStatus().getCode()
                .equals(SewerageTaxConstants.APPLICATION_STATUS_APPROVED)
                || sewerageApplicationDetails.getStatus().getCode()
                        .equals(SewerageTaxConstants.APPLICATION_STATUS_CHECKED)) {
            approvalPosition = sewerageTaxUtils.getApproverPosition(wfmatrix.getNextDesignation(),
                    sewerageApplicationDetails);
        }
        if (workFlowAction.equals(SewerageTaxConstants.WFLOW_ACTION_STEP_REJECT)
                && wfmatrix.getNextState().equals(SewerageTaxConstants.WF_STATE_CLERK_APPROVED))
            approvalPosition = null;

        return approvalPosition;
    }

    @Transactional
    public SewerageApplicationDetails updateSewerageApplicationDetails(
            final SewerageApplicationDetails sewerageApplicationDetails,
            final Long approvalPosition, final String approvalComent, String additionalRule,
            final String workFlowAction, final String mode, final ReportOutput reportOutput) throws ValidationException {
        applicationStatusChange(sewerageApplicationDetails, workFlowAction, mode);

        SewerageApplicationDetails updatedSewerageApplicationDetails = sewerageApplicationDetailsRepository
                .save(sewerageApplicationDetails);
        final ApplicationWorkflowCustomDefaultImpl applicationWorkflowCustomDefaultImpl = getInitialisedWorkFlowBean();
        if (LOG.isDebugEnabled())
            LOG.debug("applicationWorkflowCustomDefaultImpl initialization is done");

        applicationWorkflowCustomDefaultImpl.createCommonWorkflowTransition(updatedSewerageApplicationDetails,
                approvalPosition, approvalComent, additionalRule, workFlowAction);

        updateIndexes(sewerageApplicationDetails);

        return updatedSewerageApplicationDetails;
    }

    public void applicationStatusChange(final SewerageApplicationDetails sewerageApplicationDetails, final String workFlowAction,
            final String mode) {
        if (null != sewerageApplicationDetails && null != sewerageApplicationDetails.getStatus()
                && null != sewerageApplicationDetails.getStatus().getCode())
            if (sewerageApplicationDetails.getStatus().getCode().equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED)
                    && sewerageApplicationDetails.getState() != null && workFlowAction.equals("Submit"))
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_VERIFIED, SewerageTaxConstants.MODULETYPE));
            else if (sewerageApplicationDetails.getStatus().getCode()
                    .equals(SewerageTaxConstants.APPLICATION_STATUS_VERIFIED))
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN, SewerageTaxConstants.MODULETYPE));
            else if (sewerageApplicationDetails.getStatus().getCode()
                    .equals(SewerageTaxConstants.APPLICATION_STATUS_ESTIMATENOTICEGEN))
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_FEEPAID, SewerageTaxConstants.MODULETYPE));
            else if (sewerageApplicationDetails.getStatus().getCode()
                    .equals(SewerageTaxConstants.APPLICATION_STATUS_FEEPAID)) {

                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_CHECKED, SewerageTaxConstants.MODULETYPE));

            }
            else if (sewerageApplicationDetails.getStatus().getCode()
                    .equals(SewerageTaxConstants.APPLICATION_STATUS_CHECKED)
                    && workFlowAction.equalsIgnoreCase(SewerageTaxConstants.APPROVEWORKFLOWACTION)) {
                if (sewerageApplicationDetails.getConnection().getDhscNumber() == null)
                    sewerageApplicationDetails.getConnection().setDhscNumber(
                            sewerageTaxNumberGenerator.generateDhscNumber());
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_APPROVED, SewerageTaxConstants.MODULETYPE));
            }
            else if (sewerageApplicationDetails.getStatus().getCode()
                    .equals(SewerageTaxConstants.APPLICATION_STATUS_APPROVED)) {
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_WOGENERATED, SewerageTaxConstants.MODULETYPE));
                if (sewerageApplicationDetails.getWorkOrderNumber() == null) {
                    sewerageApplicationDetails.setWorkOrderDate(new Date());
                    sewerageApplicationDetails.setWorkOrderNumber(sewerageTaxNumberGenerator.generateWorkOrderNumber());
                }
            }
            else if (SewerageTaxConstants.APPLICATION_STATUS_WOGENERATED.equalsIgnoreCase(sewerageApplicationDetails
                    .getStatus().getCode()))
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_SANCTIONED, SewerageTaxConstants.MODULETYPE));
            else if (workFlowAction.equals("Reject")) {
                sewerageApplicationDetails.setStatus(sewerageTaxUtils.getStatusByCodeAndModuleType(
                        SewerageTaxConstants.APPLICATION_STATUS_CREATED, SewerageTaxConstants.MODULETYPE));
            }
    }

    public List<Hashtable<String, Object>> getHistory(final SewerageApplicationDetails sewerageApplicationDetails) {
        User user = null;
        final List<Hashtable<String, Object>> historyTable = new ArrayList<Hashtable<String, Object>>();
        final State state = sewerageApplicationDetails.getState();
        final Hashtable<String, Object> map = new Hashtable<String, Object>(0);
        if (null != state) {
            map.put("date", state.getDateInfo());
            map.put("comments", state.getComments() != null ? state.getComments() : "");
            map.put("updatedBy", state.getLastModifiedBy().getUsername() + "::" + state.getLastModifiedBy().getName());
            map.put("status", state.getValue());
            final Position ownerPosition = state.getOwnerPosition();
            user = state.getOwnerUser();
            if (null != user) {
                map.put("user", user.getUsername() + "::" + user.getName());
                map.put("department", null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                        .getDepartmentForUser(user.getId()).getName() : "");
            } else if (null != ownerPosition && null != ownerPosition.getDeptDesig()) {
                user = eisCommonService.getUserForPosition(ownerPosition.getId(), new Date());
                map.put("user", null != user.getUsername() ? user.getUsername() + "::" + user.getName() : "");
                map.put("department", null != ownerPosition.getDeptDesig().getDepartment() ? ownerPosition
                        .getDeptDesig().getDepartment().getName() : "");
            }
            historyTable.add(map);
            if (!sewerageApplicationDetails.getStateHistory().isEmpty() && sewerageApplicationDetails.getStateHistory() != null)
                Collections.reverse(sewerageApplicationDetails.getStateHistory());
            for (final StateHistory stateHistory : sewerageApplicationDetails.getStateHistory()) {
                final Hashtable<String, Object> HistoryMap = new Hashtable<String, Object>(0);
                HistoryMap.put("date", stateHistory.getDateInfo());
                HistoryMap.put("comments", stateHistory.getComments());
                HistoryMap.put("updatedBy", stateHistory.getLastModifiedBy().getUsername() + "::"
                        + stateHistory.getLastModifiedBy().getName());
                HistoryMap.put("status", stateHistory.getValue());
                final Position owner = stateHistory.getOwnerPosition();
                user = stateHistory.getOwnerUser();
                if (null != user) {
                    HistoryMap.put("user", user.getUsername() + "::" + user.getName());
                    HistoryMap.put("department",
                            null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                                    .getDepartmentForUser(user.getId()).getName() : "");
                } else if (null != owner && null != owner.getDeptDesig()) {
                    user = eisCommonService.getUserForPosition(owner.getId(), new Date());
                    HistoryMap
                            .put("user", null != user.getUsername() ? user.getUsername() + "::" + user.getName() : "");
                    HistoryMap.put("department", null != owner.getDeptDesig().getDepartment() ? owner.getDeptDesig()
                            .getDepartment().getName() : "");
                }
                historyTable.add(HistoryMap);
            }
        }
        return historyTable;
    }
}
