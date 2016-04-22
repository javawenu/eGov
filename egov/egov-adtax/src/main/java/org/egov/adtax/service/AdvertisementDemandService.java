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
package org.egov.adtax.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.egov.adtax.entity.Advertisement;
import org.egov.adtax.entity.AdvertisementPermitDetail;
import org.egov.adtax.service.penalty.AdvertisementPenaltyCalculator;
import org.egov.adtax.utils.constants.AdvertisementTaxConstants;
import org.egov.commons.Installment;
import org.egov.commons.dao.InstallmentDao;
import org.egov.demand.dao.DemandGenericDao;
import org.egov.demand.dao.EgDemandDao;
import org.egov.demand.model.BillReceipt;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgDemandReason;
import org.egov.infra.admin.master.service.ModuleService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdvertisementDemandService {
    private static final Logger LOGGER = Logger.getLogger(AdvertisementDemandService.class);
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
    
    @Autowired
    AdvertisementPenaltyRatesService advertisementPenaltyRatesService;

    @Autowired
    private AdvertisementPenaltyCalculator advertisementPenaltyCalculator;
    
    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }
/**
 * 
 * @param advertisementPermitDetail
 * @return
 */
    public EgDemand createDemand(final Advertisement advertisement) {

        EgDemand demand = null;
        final Set<EgDemandDetails> demandDetailSet = new HashSet<EgDemandDetails>();
        final Installment installment = getCurrentInstallment();
        BigDecimal totalDemandAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        if (advertisement != null && advertisement.getDemandId() == null) {
        /*    if (advertisement.getCurrentTaxAmount() != null || advertisement.getPendingTax()!=null) {
                
                if( advertisement.getPendingTax()!=null)
                    taxAmount=taxAmount.add( advertisement.getPendingTax());
                if( advertisement.getCurrentTaxAmount()!=null)
                    taxAmount=taxAmount.add(advertisement.getCurrentTaxAmount());
                
                demandDetailSet.add(createDemandDetails(
                        (taxAmount),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX,
                                installment), BigDecimal.ZERO));
                totalDemandAmount=  totalDemandAmount.add((taxAmount));
            }
            if (advertisement.getCurrentEncroachmentFee() != null) {
                demandDetailSet.add(createDemandDetails(
                        (advertisement.getCurrentEncroachmentFee()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE,
                                installment), BigDecimal.ZERO));
                totalDemandAmount= totalDemandAmount.add((advertisement.getCurrentEncroachmentFee()));
            }
*/            demand = createDemand(demandDetailSet, installment, totalDemandAmount);
        }

        return demand;
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
 * @param advertisement
 * @param demand
 * @return
 */
    public EgDemand updateDemand(final Advertisement advertisement,EgDemand demand) {
        final Installment installment = getCurrentInstallment();
        BigDecimal totalDemandAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        // Boolean calculateTax=true;
        Boolean enchroachmentFeeAlreadyExistInDemand = false;

        //EgDemand demand = advertisement.getDemandId();
        if (demand == null) {
            demand = createDemand(advertisement);
        } else {
            EgDemandReason pendingTaxReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX, installment);
            EgDemandReason encroachmentFeeReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE, installment);

      /*      if (advertisement.getCurrentTaxAmount() != null || advertisement.getPendingTax() != null) {

                if (advertisement.getPendingTax() != null)
                    taxAmount = taxAmount.add(advertisement.getPendingTax());
                if (advertisement.getCurrentTaxAmount() != null)
                    taxAmount = taxAmount.add(advertisement.getCurrentTaxAmount());

            }*/
            for (EgDemandDetails dmdDtl : demand.getEgDemandDetails()) {
                // Assumption: tax amount is mandatory.
                if (dmdDtl.getEgDemandReason().getId() == pendingTaxReason.getId()
                        && taxAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // TODO: Also check whether fully collected ?
                    totalDemandAmount = totalDemandAmount.add(taxAmount.subtract(dmdDtl.getAmount()));
                    dmdDtl.setAmount(taxAmount.setScale(0, BigDecimal.ROUND_HALF_UP));

                }
                // Encroachment fee may not mandatory. If already part of demand
                // then
/*                if (dmdDtl.getEgDemandReason().getId() == encroachmentFeeReason.getId()) {
                    enchroachmentFeeAlreadyExistInDemand = true;
                    if (advertisement.getCurrentEncroachmentFee() != null
                            && advertisement.getCurrentEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                        totalDemandAmount = totalDemandAmount.add(advertisement.getCurrentEncroachmentFee().subtract(
                                dmdDtl.getAmount()));
                        dmdDtl.setAmount(advertisement.getCurrentEncroachmentFee().setScale(0, BigDecimal.ROUND_HALF_UP));
                        // update encroachment fee..
                    } else {
                        totalDemandAmount = totalDemandAmount.subtract(dmdDtl.getAmount());
                        demand.removeEgDemandDetails(dmdDtl);
                        // delete demand detail
                    }

                }
*/            }

         /*   if (!enchroachmentFeeAlreadyExistInDemand && advertisement.getCurrentEncroachmentFee() != null
                    && advertisement.getCurrentEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                demand.addEgDemandDetails(createDemandDetails(
                        (advertisement.getCurrentEncroachmentFee()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE,
                                installment), BigDecimal.ZERO));
                totalDemandAmount = totalDemandAmount.add(advertisement.getCurrentEncroachmentFee());
            }
         */   demand.addBaseDemand(totalDemandAmount.setScale(0, BigDecimal.ROUND_HALF_UP));

        }
        return demand;

    }
/**
 * 
 * @return
 */
    public Installment getCurrentInstallment() {
        return installmentDao.getInsatllmentByModuleForGivenDateAndInstallmentType(
                moduleService.getModuleByName(AdvertisementTaxConstants.MODULE_NAME), new Date(),
                AdvertisementTaxConstants.YEARLY);

    }
    public Installment getInstallmentByDescription(String description) {
        return installmentDao.getInsatllmentByModuleAndDescription(
                moduleService.getModuleByName(AdvertisementTaxConstants.MODULE_NAME),description);

    }
 
    @Transactional
 public Installment getInsatllmentByModuleForGivenDate(final Date installmentDate) {
        return installmentDao.getInsatllmentByModuleForGivenDate(
                moduleService.getModuleByName(AdvertisementTaxConstants.MODULE_NAME), installmentDate
                 );

    }
    
 @Transactional
    public List<Installment > getPreviousInstallment(final Date curentInstalmentEndate) {
           return installmentDao.fetchPreviousInstallmentsInDescendingOrderByModuleAndDate(
                   moduleService.getModuleByName(AdvertisementTaxConstants.MODULE_NAME), curentInstalmentEndate,1);

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
 * @param advertisement
 * @return
 */
    public Boolean checkAnyTaxIsPendingToCollect(final Advertisement advertisement) {
        Boolean pendingTaxCollection = false;

        if (advertisement != null && advertisement.getDemandId() != null)
            for (final EgDemandDetails demandDtl : advertisement.getDemandId().getEgDemandDetails())
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
    public Boolean checkAnyTaxPendingForSelectedFinancialYear(final Advertisement advertisement, Installment installment) {
        Boolean pendingTaxCollection = false;

        if (advertisement != null && advertisement.getDemandId() != null)
            for (final EgDemandDetails demandDtl : advertisement.getDemandId().getEgDemandDetails())
                if (demandDtl.getEgDemandReason().getEgInstallmentMaster().getId().equals(installment.getId()) &&
                        demandDtl.getAmount().subtract(demandDtl.getAmtCollected()).compareTo(BigDecimal.ZERO) > 0) {
                    pendingTaxCollection = true;
                    break;

                }

        return pendingTaxCollection;

    }
    /**
     * @param demand
     * @param penaltyCalculationDate 
     * @return Penalty Amount and PendingAmount
     */
    public Map<String, BigDecimal> checkPedingAmountByDemand(AdvertisementPermitDetail advPermitDetail) {

        final Map<String, BigDecimal> demandFeeType = new LinkedHashMap<String, BigDecimal>();

        BigDecimal penaltyAmt = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        /**
         * Assumption: We are calculating penalty for total pending amount. If penalty is part of demand, then also we are considering that amount for penalty calculation.
         */
        if (advPermitDetail!=null && advPermitDetail.getAdvertisement()!=null && advPermitDetail.getAdvertisement().getDemandId() != null) {
            for (final EgDemandDetails demandDtl :  advPermitDetail.getAdvertisement().getDemandId().getEgDemandDetails()) {

                if (demandDtl.getAmount().subtract(demandDtl.getAmtCollected()).compareTo(BigDecimal.ZERO) > 0) {
                   pendingAmount = pendingAmount.add(demandDtl.getAmount().subtract(demandDtl.getAmtCollected()));
                 }
            }
            penaltyAmt= advertisementPenaltyCalculator.calculatePenalty(advPermitDetail);
        }
        demandFeeType.put(AdvertisementTaxConstants.PENALTYAMOUNT, penaltyAmt);
        demandFeeType.put(AdvertisementTaxConstants.PENDINGDEMANDAMOUNT, pendingAmount);

        return demandFeeType;

    }

    private int noOfMonths(final Date startDate, final Date endDate) {
        DateTime sDate = new DateTime(startDate);
        DateTime eDate = new DateTime(endDate);
        final int yearDiff = eDate.getYear() - sDate.getYear();
        int noOfMonths = yearDiff * 12 + eDate.getMonthOfYear() - sDate.getMonthOfYear();
        return noOfMonths;

    }
   
    /**
     * 
     * @param advertisementPermitDetail
     * @return
     */
    public boolean anyDemandPendingForCollection(final Advertisement advertisementPermitDetail) {
        return checkAnyTaxIsPendingToCollect(advertisementPermitDetail);
    }
    /**
     * 
     * @param advertisementPermitDetail
     * @return
     */
    public boolean anyDemandPendingForCollection(final AdvertisementPermitDetail advertisementPermitDetail) {
        return checkAnyTaxIsPendingToCollect(advertisementPermitDetail);
    }

    private boolean checkAnyTaxIsPendingToCollect(AdvertisementPermitDetail advertisementPermitDetail) {
        Boolean amountCollectionPendingInCurrentYear = false;
        if (advertisementPermitDetail != null && advertisementPermitDetail.getAdvertisement().getDemandId() != null) {
            final Installment currentInstallment = advertisementPermitDetail.getAdvertisement().getDemandId().getEgInstallmentMaster();

            if (currentInstallment != null) {
                for (final EgDemandDetails demandDtl : advertisementPermitDetail.getAdvertisement().getDemandId().getEgDemandDetails())
                {
                    if (demandDtl.getAmount().subtract(demandDtl.getAmtCollected()).compareTo(BigDecimal.ZERO) > 0
                                   && currentInstallment.getId() == demandDtl.getEgDemandReason().getEgInstallmentMaster()
                                    .getId()) {
                        amountCollectionPendingInCurrentYear = true;
                        break;
                    }
                }
            }
        }
        return amountCollectionPendingInCurrentYear;
    }
    /*
     * Check any amount collected in the current financial year or not.
     */
    public boolean collectionDoneForThisYear(final Advertisement advertisement) {
        Boolean amountCollectedInCurrentYear = false;
        if (advertisement != null && advertisement.getDemandId() != null) {
            final Installment currentInstallment = getCurrentInstallment();

            if (currentInstallment != null) {
                for (final EgDemandDetails demandDtl : advertisement.getDemandId().getEgDemandDetails())
                {
                    if (demandDtl.getAmtCollected().compareTo(BigDecimal.ZERO) > 0
                            && currentInstallment.getId() == demandDtl.getEgDemandReason().getEgInstallmentMaster()
                                    .getId()) {
                        amountCollectedInCurrentYear = true;
                        break;
                    }
                }
            }
        }
        return amountCollectedInCurrentYear;
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
    public EgDemand createDemand(AdvertisementPermitDetail advertisementPermitDetail) {

    //TODO: Arrears advertisement tax will be captured as separate reason ?
        
        EgDemand demand = null;
        final Set<EgDemandDetails> demandDetailSet = new HashSet<EgDemandDetails>();
        final Installment installment = getCurrentInstallment();
        BigDecimal totalDemandAmount = BigDecimal.ZERO;
        Boolean taxFullyPaidForCurrentYear=false;
        if (advertisementPermitDetail != null && advertisementPermitDetail.getAdvertisement().getDemandId() == null) {
            
            if( advertisementPermitDetail.getAdvertisement()!=null && advertisementPermitDetail.getAdvertisement().getLegacy()
                    && advertisementPermitDetail.getAdvertisement().getTaxPaidForCurrentYear())
            {
                taxFullyPaidForCurrentYear=true; //Tax and encroachment fee is fully paid. Arrears will not be considered as paid.
            }
            
            if (advertisementPermitDetail.getTaxAmount() != null || advertisementPermitDetail.getAdvertisement().getPendingTax()!=null) {
                
                if( advertisementPermitDetail.getAdvertisement().getPendingTax()!=null){
                    demandDetailSet.add(createDemandDetails(
                            ( advertisementPermitDetail.getAdvertisement().getPendingTax()),
                            getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX,
                                    installment),(BigDecimal.ZERO)));
                    totalDemandAmount=  totalDemandAmount.add( advertisementPermitDetail.getAdvertisement().getPendingTax());
                }
                if( advertisementPermitDetail.getTaxAmount()!=null){
                   demandDetailSet.add(createDemandDetails(
                        (advertisementPermitDetail.getTaxAmount()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX,
                                installment),(taxFullyPaidForCurrentYear ? advertisementPermitDetail.getTaxAmount(): BigDecimal.ZERO)));
                totalDemandAmount=  totalDemandAmount.add((advertisementPermitDetail.getTaxAmount()));
                }
            }
            if (advertisementPermitDetail.getEncroachmentFee() != null) {
                demandDetailSet.add(createDemandDetails(
                        (advertisementPermitDetail.getEncroachmentFee()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE,
                                installment), (taxFullyPaidForCurrentYear ? advertisementPermitDetail.getEncroachmentFee(): BigDecimal.ZERO)));
                totalDemandAmount= totalDemandAmount.add((advertisementPermitDetail.getEncroachmentFee()));
            }
            demand = createDemand(demandDetailSet, installment, totalDemandAmount);
        }

        return demand;
    
    }
    public EgDemand updateDemand(AdvertisementPermitDetail advertisementPermitDetail, EgDemand demand) {
        final Installment installment = getCurrentInstallment();
        BigDecimal totalDemandAmount = BigDecimal.ZERO;
        
        // Boolean calculateTax=true;
        Boolean enchroachmentFeeAlreadyExistInDemand = false;
        List<EgDemandDetails> removableDemandDetailList= new ArrayList<EgDemandDetails>();
        
        //EgDemand demand = advertisement.getDemandId();
        if (demand == null) {
            demand = createDemand(advertisementPermitDetail);
        } else {
            EgDemandReason pendingTaxReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX, installment);
            EgDemandReason encroachmentFeeReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE, installment);
            EgDemandReason taxReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX, installment);
           /* 
            if (advertisementPermitDetail.getTaxAmount() != null || advertisementPermitDetail.getAdvertisement().getPendingTax() != null) {

                if (advertisementPermitDetail.getAdvertisement().getPendingTax() != null)
                    taxAmount = taxAmount.add(advertisementPermitDetail.getAdvertisement().getPendingTax());
                
                if (advertisementPermitDetail.getTaxAmount() != null)
                    taxAmount = taxAmount.add(advertisementPermitDetail.getTaxAmount());

            }*/
            for (EgDemandDetails dmdDtl : demand.getEgDemandDetails()) {
                // Assumption: tax amount is mandatory.
                if (dmdDtl.getEgDemandReason().getId() == taxReason.getId()
                        && advertisementPermitDetail.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                    // TODO: Also check whether fully collected ?
                    totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getTaxAmount().subtract(dmdDtl.getAmount()));
                    dmdDtl.setAmount(advertisementPermitDetail.getTaxAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                }
                if (dmdDtl.getEgDemandReason().getId() == pendingTaxReason.getId()
                        && advertisementPermitDetail.getAdvertisement().getPendingTax()!=null && advertisementPermitDetail.getAdvertisement().getPendingTax().compareTo(BigDecimal.ZERO) > 0) {
                    // TODO: Also check whether fully collected ?
                    totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getAdvertisement().getPendingTax().subtract(dmdDtl.getAmount()));
                    dmdDtl.setAmount(advertisementPermitDetail.getAdvertisement().getPendingTax().setScale(0, BigDecimal.ROUND_HALF_UP));
 
                
                }
                // Encroachment fee may not mandatory. If already part of demand
                // then
                if (dmdDtl.getEgDemandReason().getId() == encroachmentFeeReason.getId()) {
                    enchroachmentFeeAlreadyExistInDemand = true;
                    if (advertisementPermitDetail.getEncroachmentFee() != null
                            && advertisementPermitDetail.getEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                        totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getEncroachmentFee().subtract(
                                dmdDtl.getAmount()));
                        dmdDtl.setAmount(advertisementPermitDetail.getEncroachmentFee().setScale(0, BigDecimal.ROUND_HALF_UP));
                       
                        // update encroachment fee..
                    } else {
                        totalDemandAmount = totalDemandAmount.subtract(dmdDtl.getAmount());
                      //  demand.removeEgDemandDetails(dmdDtl);
                        removableDemandDetailList.add(dmdDtl);
                        // delete demand detail
                    }

                   
                }
            }
            for (EgDemandDetails removableDmdDtl : removableDemandDetailList) {
                demand.removeEgDemandDetails(removableDmdDtl);

            }
            if (!enchroachmentFeeAlreadyExistInDemand && advertisementPermitDetail.getEncroachmentFee() != null
                    && advertisementPermitDetail.getEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                demand.addEgDemandDetails(createDemandDetails(
                        (advertisementPermitDetail.getEncroachmentFee()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE,
                                installment), BigDecimal.ZERO));
                totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getEncroachmentFee());
                //TODO: CHECK WHETHER FULLY PAID IN LEGACY HANDLED.
            }
            demand.addBaseDemand(totalDemandAmount.setScale(0, BigDecimal.ROUND_HALF_UP));

        }
        return demand;

    }
/**
 *  Update demand details of current or latest year data on renewal. Assumption: There is no partial payment collected for selected year.        
 * @param advertisementPermitDetail
 * @param demand
 * @return
 */
    public EgDemand updateDemandOnRenewal(AdvertisementPermitDetail advertisementPermitDetail, EgDemand demand) {
        
         if(demand!=null) {
                
             List<EgDemandDetails> removableDemandDetailList= new ArrayList<EgDemandDetails>();
             final Installment installment = demand.getEgInstallmentMaster();
                
                BigDecimal totalDemandAmount = BigDecimal.ZERO;
     
                Boolean enchroachmentFeeAlreadyExistInDemand = false;
        
                   /*EgDemandReason pendingTaxReason = getDemandReasonByCodeAndInstallment(
                            AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX, installment);
                 */ 
                    EgDemandReason encroachmentFeeReason = getDemandReasonByCodeAndInstallment(
                            AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE, installment);
                    EgDemandReason taxReason = getDemandReasonByCodeAndInstallment(
                            AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX, installment);
                  
                    for (EgDemandDetails dmdDtl : demand.getEgDemandDetails()) {
                        // Assumption: tax amount is mandatory.
                        if (dmdDtl.getEgDemandReason().getId() == taxReason.getId()
                                && advertisementPermitDetail.getTaxAmount().compareTo(BigDecimal.ZERO) >= 0) {
                            totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getTaxAmount().subtract(dmdDtl.getAmount()));
                            dmdDtl.setAmount(advertisementPermitDetail.getTaxAmount().setScale(0, BigDecimal.ROUND_HALF_UP));
                        }
                       /* if (dmdDtl.getEgDemandReason().getId() == pendingTaxReason.getId()
                                && advertisementPermitDetail.getAdvertisement().getPendingTax()!=null && advertisementPermitDetail.getAdvertisement().getPendingTax().compareTo(BigDecimal.ZERO) > 0) {
                            // TODO: Also check whether fully collected ?
                            totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getAdvertisement().getPendingTax().subtract(dmdDtl.getAmount()));
                            dmdDtl.setAmount(advertisementPermitDetail.getAdvertisement().getPendingTax().setScale(0, BigDecimal.ROUND_HALF_UP));
        
                        
                        }*/
                        // Encroachment fee may not mandatory. If already part of demand
                        if (dmdDtl.getEgDemandReason().getId() == encroachmentFeeReason.getId()) {
                            enchroachmentFeeAlreadyExistInDemand = true;
                            if (advertisementPermitDetail.getEncroachmentFee() != null
                                    && advertisementPermitDetail.getEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                                totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getEncroachmentFee().subtract(
                                        dmdDtl.getAmount()));
                                dmdDtl.setAmount(advertisementPermitDetail.getEncroachmentFee().setScale(0, BigDecimal.ROUND_HALF_UP));
                               
                                // update encroachment fee..
                            } else {
                                totalDemandAmount = totalDemandAmount.subtract(dmdDtl.getAmount());
                                //demand.removeEgDemandDetails(dmdDtl);
                                removableDemandDetailList.add(dmdDtl);
                                // delete demand detail
                            }
        
                           
                        }
                    }
        
                    if (!enchroachmentFeeAlreadyExistInDemand && advertisementPermitDetail.getEncroachmentFee() != null
                            && advertisementPermitDetail.getEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                        demand.addEgDemandDetails(createDemandDetails(
                                (advertisementPermitDetail.getEncroachmentFee()),
                                getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE,
                                        installment), BigDecimal.ZERO));
                        totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getEncroachmentFee());
                      }
            for (EgDemandDetails removableDmdDtl : removableDemandDetailList) {
                demand.removeEgDemandDetails(removableDmdDtl);
            }
                    demand.addBaseDemand(totalDemandAmount.setScale(0, BigDecimal.ROUND_HALF_UP));
        
                        
                }
        return demand;

    }
    public EgDemand updateDemandForLegacyEntry(AdvertisementPermitDetail advertisementPermitDetail, EgDemand demand) {
        final Installment installment = getCurrentInstallment();
        BigDecimal totalDemandAmount = BigDecimal.ZERO;

        Boolean taxFullyPaidForCurrentYear = false;
 
        if (advertisementPermitDetail.getAdvertisement() != null
                && advertisementPermitDetail.getAdvertisement().getLegacy()
                && advertisementPermitDetail.getAdvertisement().getTaxPaidForCurrentYear()) {
            taxFullyPaidForCurrentYear = true;
        }

        Boolean enchroachmentFeeAlreadyExistInDemand = false;
        Boolean arrearsTaxalreadyExistInDemand = false;
        Boolean taxalreadyExistInDemand = false;
        List <EgDemandDetails> deleteDmdDtl= new ArrayList<EgDemandDetails>();
        if (demand == null) {
            demand = createDemand(advertisementPermitDetail);
        } else {
            EgDemandReason pendingTaxReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX, installment);
            EgDemandReason encroachmentFeeReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE, installment);
            EgDemandReason taxReason = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX, installment);

            for (EgDemandDetails dmdDtl : demand.getEgDemandDetails()) {
                if (dmdDtl.getEgDemandReason().getId() == taxReason.getId()) {
                    taxalreadyExistInDemand = true;
                    if (advertisementPermitDetail.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {

                        totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getTaxAmount().subtract(
                                dmdDtl.getAmount()));
                        dmdDtl.setAmount(advertisementPermitDetail.getTaxAmount().setScale(0, BigDecimal.ROUND_HALF_UP));

                        if (taxFullyPaidForCurrentYear) {
                            dmdDtl.setAmtCollected((advertisementPermitDetail.getTaxAmount() != null ? (advertisementPermitDetail
                                    .getTaxAmount()) : BigDecimal.ZERO));
                        } else
                            dmdDtl.setAmtCollected(BigDecimal.ZERO);
                    } else {
                        totalDemandAmount = totalDemandAmount.subtract(dmdDtl.getAmount());
                        deleteDmdDtl.add(dmdDtl);
                      //  demand.removeEgDemandDetails(dmdDtl);
                    }
                }
                else if (dmdDtl.getEgDemandReason().getId() == pendingTaxReason.getId()) {
                    arrearsTaxalreadyExistInDemand = true;
                    if (advertisementPermitDetail.getAdvertisement().getPendingTax() != null
                            && advertisementPermitDetail.getAdvertisement().getPendingTax().compareTo(BigDecimal.ZERO) > 0) {

                        totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getAdvertisement()
                                .getPendingTax().subtract(dmdDtl.getAmount()));
                        dmdDtl.setAmount(advertisementPermitDetail.getAdvertisement().getPendingTax()
                                .setScale(0, BigDecimal.ROUND_HALF_UP));

                     /*   if (taxFullyPaidForCurrentYear) {
                            dmdDtl.setAmtCollected((advertisementPermitDetail.getAdvertisement().getPendingTax() != null ? (advertisementPermitDetail
                                    .getAdvertisement().getPendingTax()) : BigDecimal.ZERO));
                        } else*/
                            dmdDtl.setAmtCollected(BigDecimal.ZERO);
                    } else {
                        totalDemandAmount = totalDemandAmount.subtract(dmdDtl.getAmount());
                        deleteDmdDtl.add(dmdDtl);
                    }
                }
                  else if (dmdDtl.getEgDemandReason().getId() == encroachmentFeeReason.getId()) {
                    enchroachmentFeeAlreadyExistInDemand = true;
                    if (advertisementPermitDetail.getEncroachmentFee() != null
                            && advertisementPermitDetail.getEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                        totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getEncroachmentFee()
                                .subtract(dmdDtl.getAmount()));
                        dmdDtl.setAmount(advertisementPermitDetail.getEncroachmentFee().setScale(0,
                                BigDecimal.ROUND_HALF_UP));

                        if (taxFullyPaidForCurrentYear) {
                            dmdDtl.setAmtCollected((advertisementPermitDetail.getEncroachmentFee() != null ? (advertisementPermitDetail
                                    .getEncroachmentFee()) : BigDecimal.ZERO));
                        } else
                            dmdDtl.setAmtCollected(BigDecimal.ZERO);

                        // update encroachment fee..     // Encroachment fee may not mandatory. If already part of demand
                     } else {
                        totalDemandAmount = totalDemandAmount.subtract(dmdDtl.getAmount());
                        deleteDmdDtl.add(dmdDtl);
                        // delete demand detail
                    }

                }
            }

            for (EgDemandDetails dmdDtls : deleteDmdDtl)  
            {
                demand.removeEgDemandDetails(dmdDtls);
            }
                
            if (!enchroachmentFeeAlreadyExistInDemand && advertisementPermitDetail.getEncroachmentFee() != null
                    && advertisementPermitDetail.getEncroachmentFee().compareTo(BigDecimal.ZERO) > 0) {
                demand.addEgDemandDetails(createDemandDetails(
                        (advertisementPermitDetail.getEncroachmentFee()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE,
                                installment), taxFullyPaidForCurrentYear?advertisementPermitDetail.getEncroachmentFee():BigDecimal.ZERO));
                totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getEncroachmentFee());
             }
            if (!arrearsTaxalreadyExistInDemand && advertisementPermitDetail.getAdvertisement().getPendingTax() != null
                    && advertisementPermitDetail.getAdvertisement().getPendingTax().compareTo(BigDecimal.ZERO) > 0) {
                demand.addEgDemandDetails(createDemandDetails(
                        (advertisementPermitDetail.getAdvertisement().getPendingTax()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX,
                                installment),  BigDecimal.ZERO));
                totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getAdvertisement().getPendingTax());
              }
            if (!taxalreadyExistInDemand && advertisementPermitDetail.getTaxAmount() != null
                    && advertisementPermitDetail.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
                demand.addEgDemandDetails(createDemandDetails(
                        (advertisementPermitDetail.getTaxAmount()),
                        getDemandReasonByCodeAndInstallment(AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX,
                                installment), taxFullyPaidForCurrentYear?advertisementPermitDetail.getTaxAmount(): BigDecimal.ZERO));
                totalDemandAmount = totalDemandAmount.add(advertisementPermitDetail.getTaxAmount());
             }
            demand.addBaseDemand(totalDemandAmount.setScale(0, BigDecimal.ROUND_HALF_UP));

        }
        return demand;

    }

@Transactional
public int generateDemandForNextInstallment(final List<Advertisement> advertisements,
            List<Installment> previousInstallment, Installment advDmdGenerationInstallment) {
        int totalRecordsProcessed = 0;

        LOGGER.info("*************************************** total records " + advertisements.size());

        if (advertisements.size() > 0) {
            EgDemandReason encroachmentFeeReasonOldInstallment = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE, previousInstallment.get(0));
            EgDemandReason taxReasonOldInstallment = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX, previousInstallment.get(0));

            EgDemandReason encroachmentFeeReasonNewInstallment = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ENCROCHMENTFEE, advDmdGenerationInstallment);
            EgDemandReason taxReasonNewInstallment = getDemandReasonByCodeAndInstallment(
                    AdvertisementTaxConstants.DEMANDREASON_ADVERTISEMENTTAX, advDmdGenerationInstallment);

            for (Advertisement advertisement : advertisements) {
                LOGGER.info("*************************************** demand id " + advertisement.getDemandId().getId());
                totalRecordsProcessed++;
                // get last year demand and add as current year.
                generateNextYearDemandForAdvertisement(advertisement, encroachmentFeeReasonOldInstallment,
                        taxReasonOldInstallment, encroachmentFeeReasonNewInstallment, taxReasonNewInstallment);
            }
        }
        return totalRecordsProcessed;
    }
    /*
     * Copy last year tax and encroachment fee details into next financial year
     * data.
     */
  
    private EgDemand generateNextYearDemandForAdvertisement(Advertisement advertisement,
            EgDemandReason oldencroachmentFeeReasonInstallment, EgDemandReason oldtaxReasonInstallment,
            EgDemandReason newencroachmentFeeReasonInstallment, EgDemandReason newtaxReasonInstallment) {

        BigDecimal totalDemandAmount = BigDecimal.ZERO;
        EgDemand demand = advertisement.getDemandId();

        Boolean enchroachmentFeeAlreadyExistInDemand = false;
        Boolean taxFeeAlreadyExistInDemand = false;

        EgDemandDetails oldEncroachmentDetail = null;
        EgDemandDetails oldTaxDemandDetail = null;
        Set<EgDemandDetails> dmadDtl = demand.getEgDemandDetails();

        LOGGER.info("Demand Detail size" + dmadDtl.size());

        for (EgDemandDetails dmdDtl : dmadDtl) {
            // Assumption: tax amount is mandatory.
            if (dmdDtl.getEgDemandReason().getId() == oldtaxReasonInstallment.getId()) {
                oldTaxDemandDetail = dmdDtl;
            }
            if (dmdDtl.getEgDemandReason().getId() == oldencroachmentFeeReasonInstallment.getId()) {
                oldEncroachmentDetail = dmdDtl;
            }
            if (dmdDtl.getEgDemandReason().getId() == newtaxReasonInstallment.getId()) {
                enchroachmentFeeAlreadyExistInDemand = true;
            }
            if (dmdDtl.getEgDemandReason().getId() == newencroachmentFeeReasonInstallment.getId()) {
                taxFeeAlreadyExistInDemand = true;
            }
        }
        // Copy last financial year tax and encroachment details to new
        // installment
        // if tax and encroachment fee already present in new installment, then
        // we are not updating.

        if (!enchroachmentFeeAlreadyExistInDemand && oldEncroachmentDetail != null) {
            demand.addEgDemandDetails(createDemandDetails((oldEncroachmentDetail.getAmount()),
                    newencroachmentFeeReasonInstallment, BigDecimal.ZERO));
            totalDemandAmount = totalDemandAmount.add(oldEncroachmentDetail.getAmount());
            demand.setEgInstallmentMaster(newencroachmentFeeReasonInstallment.getEgInstallmentMaster());
        }
        if (!taxFeeAlreadyExistInDemand && oldTaxDemandDetail != null) {
            demand.addEgDemandDetails(createDemandDetails((oldTaxDemandDetail.getAmount()), newtaxReasonInstallment,
                    BigDecimal.ZERO));
            totalDemandAmount = totalDemandAmount.add(oldTaxDemandDetail.getAmount());
            demand.setEgInstallmentMaster(newencroachmentFeeReasonInstallment.getEgInstallmentMaster());
        }

        demand.addBaseDemand(totalDemandAmount.setScale(0, BigDecimal.ROUND_HALF_UP));

        return demand;
    }
    
    public Map<String, BigDecimal> checkPendingAmountByDemand(AdvertisementPermitDetail advPermitDetail) {
        final Map<String, BigDecimal> demandFeeType = new LinkedHashMap<String, BigDecimal>();
        BigDecimal totalDemand = BigDecimal.ZERO;
        BigDecimal totalCollection = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        BigDecimal penaltyAmount = BigDecimal.ZERO;
        if (advPermitDetail!=null && advPermitDetail.getAdvertisement()!=null && advPermitDetail.getAdvertisement().getDemandId() != null) {
            for (final EgDemandDetails demandDtl : advPermitDetail.getAdvertisement().getDemandId().getEgDemandDetails()) {
                totalDemand = totalDemand.add(demandDtl.getAmount());
                totalCollection = totalCollection.add(demandDtl.getAmtCollected());
                totalPending= totalPending.add(demandDtl.getAmount().subtract(demandDtl.getAmtCollected()));
              }
            penaltyAmount= advertisementPenaltyCalculator.calculatePenalty(advPermitDetail);
        }
        totalDemand = totalDemand.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        totalCollection = totalCollection.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        totalPending = totalPending.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        penaltyAmount = penaltyAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        demandFeeType.put(AdvertisementTaxConstants.PENDINGDEMANDAMOUNT, totalPending);
        demandFeeType.put(AdvertisementTaxConstants.TOTAL_DEMAND, totalDemand);
        demandFeeType.put(AdvertisementTaxConstants.TOTALCOLLECTION, totalCollection);
        demandFeeType.put(AdvertisementTaxConstants.PENALTYAMOUNT, penaltyAmount);
        return demandFeeType;
    }
    
    
    public BigDecimal getPendingArrearsTax(final AdvertisementPermitDetail advertisementPermitDetail)
    {
        BigDecimal arrearTax=BigDecimal.ZERO;
        if(advertisementPermitDetail.getAdvertisement()!=null && advertisementPermitDetail.getAdvertisement().getDemandId()!=null)
        {
            for(EgDemandDetails egDemandDetails : advertisementPermitDetail.getAdvertisement().getDemandId().getEgDemandDetails()){
                if (AdvertisementTaxConstants.DEMANDREASON_ARREAR_ADVERTISEMENTTAX.equalsIgnoreCase(egDemandDetails.getEgDemandReason().getEgDemandReasonMaster().getCode()))
              arrearTax=arrearTax.add(egDemandDetails.getAmount().subtract(egDemandDetails.getAmtCollected()!=null?egDemandDetails.getAmtCollected():BigDecimal.ZERO));
            }
        }
        return arrearTax;
    }
    
    /**
     * @description returns reasonwise demand and collected amount
     * @param advPermitDetail
     * @return
     */
    public Map<String, Map<String, BigDecimal>> getReasonWiseDemandAndCollection(AdvertisementPermitDetail advPermitDetail) {
        final Map<String, Map<String, BigDecimal>> reasonwiseDmnd_CollDtls = new HashMap<String, Map<String, BigDecimal>>();
        Map<String, BigDecimal> demandCollectionSum = null;
        BigDecimal totalAmount = BigDecimal.ZERO;  // Holds sum of all demand detail reason base amount
        BigDecimal totalAmountCollected = BigDecimal.ZERO; // Holds sum of all demand detail reason collected amount
        if (advPermitDetail!=null && advPermitDetail.getAdvertisement()!=null && advPermitDetail.getAdvertisement().getDemandId() != null) {
        	  for (EgDemandDetails demandDtl : advPermitDetail.getAdvertisement().getDemandId().getEgDemandDetails()) {
        		  demandCollectionSum = new HashMap<String, BigDecimal>();
        		  if(reasonwiseDmnd_CollDtls.containsKey(demandDtl.getEgDemandReason().getEgDemandReasonMaster().getCode())){
        			  Map<String, BigDecimal> temp = reasonwiseDmnd_CollDtls.get(demandDtl.getEgDemandReason().getEgDemandReasonMaster().getCode());
        			  demandCollectionSum.put("demandAmount", temp.get("demandAmount").add(demandDtl.getAmount()!=null?demandDtl.getAmount():BigDecimal.ZERO));
        			  demandCollectionSum.put("collectedAmount", temp.get("collectedAmount").add(demandDtl.getAmtCollected()!=null?demandDtl.getAmtCollected():BigDecimal.ZERO));
        			  totalAmount=totalAmount.add(demandDtl.getAmount()!=null?demandDtl.getAmount():BigDecimal.ZERO);
        			  totalAmountCollected=totalAmountCollected.add(demandDtl.getAmtCollected()!=null?demandDtl.getAmtCollected():BigDecimal.ZERO);
        			  reasonwiseDmnd_CollDtls.put(demandDtl.getEgDemandReason().getEgDemandReasonMaster().getCode(), demandCollectionSum);
        		  }else{
        			  // New Entry
        			  demandCollectionSum.put("demandAmount", demandDtl.getAmount());
        			  demandCollectionSum.put("collectedAmount", demandDtl.getAmtCollected());
        			  totalAmount=totalAmount.add(demandDtl.getAmount());
        			  totalAmountCollected=totalAmountCollected.add(demandDtl.getAmtCollected());
        			  reasonwiseDmnd_CollDtls.put(demandDtl.getEgDemandReason().getEgDemandReasonMaster().getCode(), demandCollectionSum);
        		  }
        	  }
        	  demandCollectionSum = new HashMap<String, BigDecimal>();
        	  demandCollectionSum.put("totalAmount", totalAmount);
        	  demandCollectionSum.put("totalAmountCollected", totalAmountCollected);
        	  reasonwiseDmnd_CollDtls.put("Total", demandCollectionSum);
        }
        return reasonwiseDmnd_CollDtls;
    }
  }
