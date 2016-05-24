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

package org.egov.stms.transactions.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.commons.Installment;
import org.egov.commons.dao.InstallmentDao;
import org.egov.demand.dao.DemandGenericDao;
import org.egov.demand.dao.EgDemandDao;
import org.egov.demand.model.BillReceipt;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgDemandReason;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnection;
import org.egov.stms.transactions.entity.SewerageConnectionFee;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewerageDemandService {
    private static final Logger LOGGER = Logger.getLogger(SewerageDemandService.class);
    @Autowired
    private InstallmentDao installmentDao;

    @Autowired
    private DemandGenericDao demandGenericDao;

    @Autowired
    private EgDemandDao egDemandDao;

     @Autowired
    private ModuleService moduleService;

    @PersistenceContext
    private EntityManager entityManager;

    
    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }
  
/**
 * 
 * @param demandReason
 * @param installment
 * @return
 */
    public EgDemandReason getDemandReasonByCodeAndInstallment(final String demandReason, final Installment installment) {
        final Query demandQuery = getCurrentSession().getNamedQuery("DEMANDREASONBY_CODE_AND_INSTALLMENTID");
        demandQuery.setParameter(0, demandReason);
        demandQuery.setParameter(1, installment.getId());
        final EgDemandReason demandReasonObj = (EgDemandReason) demandQuery.uniqueResult();
        return demandReasonObj;
    }
/**
 * 
 * @param demandDetailSet
 * @param installment
 * @param totalDemandAmount
 * @return
 */
    private EgDemand createDemand(final Set<EgDemandDetails> demandDetailSet, final Installment installment,
            final BigDecimal totalDemandAmount) {
        final EgDemand egDemand = new EgDemand();
        egDemand.setEgInstallmentMaster(installment);
        egDemand.getEgDemandDetails().addAll(demandDetailSet);
        egDemand.setIsHistory("N");
        egDemand.setCreateDate(new Date());
        egDemand.setBaseDemand(totalDemandAmount.setScale(0, BigDecimal.ROUND_HALF_UP));
        egDemand.setModifiedDate(new Date());
        return egDemand;
    }

/**
 * 
 * @return
 */
    public Installment getCurrentInstallment() {
        return installmentDao.getInsatllmentByModuleForGivenDate(
                moduleService.getModuleByName(SewerageTaxConstants.MODULE_NAME), new Date());

    }
    public Installment getInstallmentByDescription(String description) {
        return installmentDao.getInsatllmentByModuleAndDescription(
                moduleService.getModuleByName(SewerageTaxConstants.MODULE_NAME),description);

    }
 
  
 public Installment getInsatllmentByModuleForGivenDate(final Date installmentDate) {
        return installmentDao.getInsatllmentByModuleForGivenDate(
                moduleService.getModuleByName(SewerageTaxConstants.MODULE_NAME), installmentDate
                 );

    }
    

    public List<Installment > getPreviousInstallment(final Date curentInstalmentEndate) {
           return installmentDao.fetchPreviousInstallmentsInDescendingOrderByModuleAndDate(
                   moduleService.getModuleByName(SewerageTaxConstants.MODULE_NAME), curentInstalmentEndate,1);

       }
    
/**
 * 
 * @param dmdAmount
 * @param egDemandReason
 * @param amtCollected
 * @return
 */
    public EgDemandDetails createDemandDetails(final BigDecimal dmdAmount, final EgDemandReason egDemandReason,
            final BigDecimal amtCollected) {
        return EgDemandDetails.fromReasonAndAmounts(dmdAmount.setScale(0, BigDecimal.ROUND_HALF_UP), egDemandReason, amtCollected);
    }
/**
 * 
 * @param sewerageConnection
 * @return
 */
    public Boolean checkAnyTaxIsPendingToCollect(SewerageConnection sewerageConnection) {
        Boolean pendingTaxCollection = false;

        if (sewerageConnection != null && sewerageConnection.getDemand() != null)
            for (final EgDemandDetails demandDtl : sewerageConnection.getDemand().getEgDemandDetails())
                if (demandDtl.getAmount().subtract(demandDtl.getAmtCollected()).compareTo(BigDecimal.ZERO) > 0) {
                    pendingTaxCollection = true;
                    break;

                }

        return pendingTaxCollection;

    }
    /**
     * Check any tax pay pending for selected advertisement in selected installment
     * @param advertisement
     * @param installment
     * @return
     */
    public Boolean checkAnyTaxPendingForSelectedFinancialYear(final SewerageConnection sewerageConnection, Installment installment) {
        Boolean pendingTaxCollection = false;

        if (sewerageConnection != null && sewerageConnection.getDemand() != null)
            for (final EgDemandDetails demandDtl : sewerageConnection.getDemand().getEgDemandDetails())
                if (demandDtl.getEgDemandReason().getEgInstallmentMaster().getId().equals(installment.getId()) &&
                        demandDtl.getAmount().subtract(demandDtl.getAmtCollected()).compareTo(BigDecimal.ZERO) > 0) {
                    pendingTaxCollection = true;
                    break;

                }

        return pendingTaxCollection;

    }
  
   
    /**
     * 
     * @param sewerageConnection
     * @return
     */
    public boolean anyDemandPendingForCollection(final SewerageConnection sewerageConnection) {
        return checkAnyTaxIsPendingToCollect(sewerageConnection);
    }
 
    
    public List<EgDemandDetails> getDemandDetailByPassingDemandDemandReason(EgDemand demand , EgDemandReason demandReason) {

        return  demandGenericDao.getDemandDetailsForDemandAndReasons(
            demand, Arrays.asList(demandReason));
 
    }
   
    public List<BillReceipt> getBilReceiptsByDemand(EgDemand demand) {
        List<BillReceipt> billReceiptList = new ArrayList<BillReceipt>();
        billReceiptList = demandGenericDao.getBillReceipts(demand);
        return billReceiptList;
    }
    
    public EgDemand createDemandOnNewConnection( List<SewerageConnectionFee> connectionFees ,final SewerageApplicationDetails sewerageApplicationDetail) throws ApplicationRuntimeException {

        EgDemand demand = null;
        final Set<EgDemandDetails> demandDetailSet = new HashSet<EgDemandDetails>();
        BigDecimal totalDemandAmount = BigDecimal.ZERO;
        if (sewerageApplicationDetail != null && sewerageApplicationDetail.getConnection().getDemand() == null) {
            final Installment installment = getCurrentInstallment();

            for (SewerageConnectionFee fees : connectionFees) {
                EgDemandReason pendingTaxReason = getDemandReasonByCodeAndInstallment(fees.getFeesDetail().getCode(),
                        installment); // TODO: CHECK CURRENT INSTALLMENT
                                      // REQUIRED?
                if(pendingTaxReason!=null){
                demandDetailSet.add(createDemandDetails((BigDecimal.valueOf(fees.getAmount())), pendingTaxReason,
                        (BigDecimal.ZERO)));
                totalDemandAmount = totalDemandAmount.add(BigDecimal.valueOf(fees.getAmount()));
                }else
                    throw new ApplicationRuntimeException("SEWERAGE.001");
            }

            demand = createDemand(demandDetailSet, installment, totalDemandAmount);
        }

        return demand;
    
    }
}

    
   
    
    
