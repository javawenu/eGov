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
package org.egov.ptis.bean;

import org.egov.commons.Installment;

import java.math.BigDecimal;


/**
 * 
 * @author nayeem
 *
 */
public class DemandDetail {

	private Installment installment;
	private String reasonMaster;
	private BigDecimal actualAmount;
	private BigDecimal revisedAmount;
	private BigDecimal actualCollection;
	private BigDecimal revisedCollection;	
	private Boolean isCollectionEditable;
	private Boolean isNew;

	public DemandDetail() {
	}

	public DemandDetail(String reasonMaster, BigDecimal actualAmount, BigDecimal revisedAmount,
			BigDecimal actualCollection, BigDecimal revisedCollection, Installment installment,
			Boolean isCollectionEditable, Boolean isNew) {
		this.reasonMaster = reasonMaster;
		this.actualAmount = actualAmount;
		this.revisedAmount = revisedAmount;
		this.actualCollection = actualCollection;
		this.revisedCollection = revisedCollection;
		this.installment = installment;
		this.isCollectionEditable = isCollectionEditable;
		this.isNew = isNew;
	}

	

	public String getReasonMaster() {
		return reasonMaster;
	}

	public void setReasonMaster(String reasonMaster) {
		this.reasonMaster = reasonMaster;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getRevisedAmount() {
		return revisedAmount;
	}

	public void setRevisedAmount(BigDecimal revisedAmount) {
		this.revisedAmount = revisedAmount;
	}

	public BigDecimal getActualCollection() {
		return actualCollection;
	}

	public void setActualCollection(BigDecimal actualCollection) {
		this.actualCollection = actualCollection;
	}

	public BigDecimal getRevisedCollection() {
		return revisedCollection;
	}

	public void setRevisedCollection(BigDecimal revisedCollection) {
		this.revisedCollection = revisedCollection;
	}

	public Installment getInstallment() {
		return installment;
	}

	public void setInstallment(Installment installment) {
		this.installment = installment;
	}
	
	public Boolean getIsCollectionEditable() {
		return isCollectionEditable;
	}

	public void setIsCollectionEditable(Boolean isCollectionEditable) {
		this.isCollectionEditable = isCollectionEditable;
	}
	
	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public String toString() {
		return new StringBuilder(200).append("DemandDetail ")
				.append("[")
				.append("installment=").append(installment)
				.append(", reasonMaster=").append(reasonMaster)
				.append(", actualAmount=").append(actualAmount)
				.append(", revisedAmount=").append(revisedAmount)
				.append(", actualCollection=").append(actualCollection)
				.append(", revisedCollection=").append(revisedCollection)
				.append(", isCollectionEditable=").append(isCollectionEditable)
				.append(", isNew=").append(isNew)
				.append("]").toString();
	}
}
