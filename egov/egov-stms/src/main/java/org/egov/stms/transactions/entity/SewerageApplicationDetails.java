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
package org.egov.stms.transactions.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.commons.EgwStatus;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.stms.masters.entity.SewerageApplicationType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "egswtax_applicationdetails")
@SequenceGenerator(name = SewerageApplicationDetails.SEQ_APPLICATIONDETAILS, sequenceName = SewerageApplicationDetails.SEQ_APPLICATIONDETAILS, allocationSize = 1)
public class SewerageApplicationDetails extends StateAware {

    private static final long serialVersionUID = 734228413287149091L;

    public static final String SEQ_APPLICATIONDETAILS = "SEQ_EGSWTAX_APPLICATIONDETAILS";

    public enum WorkFlowState {
        CREATED, CHECKED, APPROVED, REJECTED, CANCELLED;
    }

    @Id
    @GeneratedValue(generator = SEQ_APPLICATIONDETAILS, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "applicationtype", nullable = false)
    private SewerageApplicationType applicationType;

    @ManyToOne(cascade = CascadeType.ALL)
    @Valid
    @NotNull
    @JoinColumn(name = "connection", nullable = false)
    private SewerageConnection connection;

    @SafeHtml
    private String applicationNumber;

    @Temporal(value = TemporalType.DATE)
    private Date applicationDate;

    @Temporal(value = TemporalType.DATE)
    private Date disposalDate;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private EgwStatus status;

    @SafeHtml
    @Length(min = 3, max = 50)
    private String approvalNumber;

    @Temporal(value = TemporalType.DATE)
    private Date approvalDate;

    @SafeHtml
    private String workOrderNumber;

    @Temporal(value = TemporalType.DATE)
    private Date workOrderDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "filestoreid")
    private FileStoreMapper fileStore;

    private boolean isActive;

    @OrderBy("id desc")
    @OneToMany(mappedBy = "applicationDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SewerageConnectionEstimationDetails> estimationDetails = new ArrayList<SewerageConnectionEstimationDetails>(0);

    @OrderBy("id desc")
    @OneToMany(mappedBy = "applicationDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SewerageFieldInspectionDetails> fieldInspectionDetails = new ArrayList<SewerageFieldInspectionDetails>(0);
    
    @OrderBy("id desc")
    @OneToMany(mappedBy = "applicationDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SewerageConnectionFee> connectionFees = new ArrayList<SewerageConnectionFee>(0);

    
    @Transient
    private Long approvalDepartment;

    @Transient
    private String approvalComent;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public SewerageApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(final SewerageApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public SewerageConnection getConnection() {
        return connection;
    }

    public void setConnection(final SewerageConnection connection) {
        this.connection = connection;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(final String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(final Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Date getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(final Date disposalDate) {
        this.disposalDate = disposalDate;
    }

    public EgwStatus getStatus() {
        return status;
    }

    public void setStatus(final EgwStatus status) {
        this.status = status;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setApprovalNumber(final String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(final Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(final String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public Date getWorkOrderDate() {
        return workOrderDate;
    }

    public void setWorkOrderDate(final Date workOrderDate) {
        this.workOrderDate = workOrderDate;
    }

    public FileStoreMapper getFileStore() {
        return fileStore;
    }

    public void setFileStore(final FileStoreMapper fileStore) {
        this.fileStore = fileStore;
    }

    public List<SewerageConnectionEstimationDetails> getEstimationDetails() {
        return estimationDetails;
    }

    public void setEstimationDetails(final List<SewerageConnectionEstimationDetails> estimationDetails) {
        this.estimationDetails = estimationDetails;
    }

     
    public Long getApprovalDepartment() {
        return approvalDepartment;
    }

    public void setApprovalDepartment(Long approvalDepartment) {
        this.approvalDepartment = approvalDepartment;
    }

    public String getApprovalComent() {
        return approvalComent;
    }

    public void setApprovalComent(String approvalComent) {
        this.approvalComent = approvalComent;
    }

    @Override
    public String myLinkId() {
        return applicationNumber != null ? applicationNumber : connection.getDhscNumber();

    }

    @Override
    public String getStateDetails() {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return String.format("Sewerage Application Number %s with application date %s.",
                applicationNumber != null ? applicationNumber : connection.getDhscNumber(),
                applicationDate != null ? formatter.format(applicationDate) : formatter.format(new Date()));
    }

    public List<SewerageConnectionFee> getConnectionFees() {
        return connectionFees;
    }

    public void setConnectionFees(List<SewerageConnectionFee> connectionFees) {
        this.connectionFees = connectionFees;
    }
    public void addConnectionFees(SewerageConnectionFee connectionFees) {
        this.connectionFees.add(connectionFees);
    }

    
    
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<SewerageFieldInspectionDetails> getFieldInspectionDetails() {
        return fieldInspectionDetails;
    }

    public void setFieldInspectionDetails(List<SewerageFieldInspectionDetails> fieldInspectionDetails) {
        this.fieldInspectionDetails = fieldInspectionDetails;
    }

}