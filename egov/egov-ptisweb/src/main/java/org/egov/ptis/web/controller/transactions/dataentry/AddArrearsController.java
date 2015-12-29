/*******************************************************************************
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
 * 	1) All versions of this program, verbatim or modified must carry this 
 * 	   Legal Notice.
 * 
 * 	2) Any misrepresentation of the origin of the material is prohibited. It 
 * 	   is required that all modified versions of this material be marked in 
 * 	   reasonable ways as different from the original version.
 * 
 * 	3) This license does not grant any rights to any user of the program 
 * 	   with regards to rights under trademark law for use of the trade names 
 * 	   or trademarks of eGovernments Foundation.
 * 
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org
 ******************************************************************************/
package org.egov.ptis.web.controller.transactions.dataentry;

import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_VALIDATION;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.commons.Installment;
import org.egov.commons.dao.InstallmentDao;
import org.egov.demand.dao.DemandGenericDao;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgDemandReason;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infstr.services.PersistenceService;
import org.egov.ptis.client.model.ArrearsInfo;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.demand.Ptdemand;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.service.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.egov.ptis.constants.PropertyTaxConstants.DEMANDRSN_CODE_GENERAL_TAX;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMANDRSN_CODE_LIBRARY_CESS;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMANDRSN_CODE_EDUCATIONAL_CESS;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMANDRSN_CODE_UNAUTHORIZED_PENALTY;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMANDRSN_CODE_PENALTY_FINES;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMANDRSN_CODE_VACANT_TAX;


@Controller
@RequestMapping(value = "/addarrears")
public class AddArrearsController {

	private static final String ADDARREARS_FORM = "addArrears-form";
	private static final String ADDARREARS_SUCCESS = "addArrears-success";
	
	@Autowired
    private PropertyService propertyService;
	
	@Autowired
    private BasicPropertyDAO basicPropertyDAO;
	
	@Autowired
	private InstallmentDao installmentDao;
	
	@Autowired
	private ModuleService moduleDao;
	
	@Autowired
	private DemandGenericDao demandGenericDao;
	
	@Autowired
    private PersistenceService<Ptdemand, Long> persistenceService;
	
	private Module module;

	private Installment installment;
	
	private Ptdemand ptDemand;
	
	@ModelAttribute
    public ArrearsInfo getArrearsInfo() {
        return new ArrearsInfo();
    }
	
	@RequestMapping(value = "/form/{assessmentNo}", method = RequestMethod.GET)
    public String newform(final Model model, @PathVariable final String assessmentNo, final HttpServletRequest request) {
		BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNo);
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
		Date installmentDate = null;
		module = moduleDao.getModuleByName(PropertyTaxConstants.PTMODULENAME);
		try{
			installmentDate = dateformat.parse("01/04/2015");
		}catch (Exception e) {
            e.printStackTrace();
        }

		if(basicProperty != null){
			Property property = basicProperty.getActiveProperty();
			if(property != null){
				installment = installmentDao.getInsatllmentByModuleForGivenDate(module, installmentDate);
				ptDemand = persistenceService.find("from  Ptdemand ptd where ptd.egptProperty =? and ptd.egInstallmentMaster.id =? ", 
						property,PropertyTaxUtil.getCurrentInstallment().getId());
	            Boolean arrearDemandExists = checkDemandExistsForInstallment();
				model.addAttribute("propertyType", property.getPropertyDetail().getPropertyTypeMaster().getCode());
				model.addAttribute("arrearsMessage", "Arrears as on "+installment.getDescription());
				if(arrearDemandExists){
					model.addAttribute("errorMsg", "Arrears are already added for the installment "+installment.getDescription());
                    return PROPERTY_VALIDATION;
				}
			}
		}
        return ADDARREARS_FORM;
    }

	private Boolean checkDemandExistsForInstallment(){
		Boolean demandDetailsExist = false;
		for(EgDemandDetails demandDetails : ptDemand.getEgDemandDetails()){
			if(demandDetails.getEgDemandReason().getEgInstallmentMaster().equals(installment)){
				demandDetailsExist = true;
				break;
			}
		}
		return demandDetailsExist;
	}
	
	@RequestMapping(value = "/form/{assessmentNo}", method = RequestMethod.POST)
	public String saveArrears(@Valid @ModelAttribute ArrearsInfo arrearsInfo, final BindingResult errors, RedirectAttributes redirectAttrs,
			final Model model) {
    	
        if (errors.hasErrors())
            return ADDARREARS_FORM;
        
        addDemandDetails(arrearsInfo);
        persistenceService.update(ptDemand);
        model.addAttribute("successMessage", "Arrears are added successfully!");
        return ADDARREARS_SUCCESS;
    }

	/**
	 * This method adds the new demand details to the existing set
	 * @param arrearsInfo
	 */
	private void addDemandDetails(ArrearsInfo arrearsInfo) {
		EgDemandDetails demandDetails;
		if(arrearsInfo.getGeneralTax()!=null && arrearsInfo.getGeneralTax().compareTo(BigDecimal.ZERO)==1){
        	EgDemandReason demandReason = demandGenericDao.getEgDemandReasonByCodeInstallmentModule(DEMANDRSN_CODE_GENERAL_TAX, installment, module, null);
        	demandDetails = propertyService.createDemandDetails(arrearsInfo.getGeneralTax(), null, demandReason, installment);
        	ptDemand.getEgDemandDetails().add(demandDetails);
        }
		if(arrearsInfo.getVacantLandTax()!=null && arrearsInfo.getVacantLandTax().compareTo(BigDecimal.ZERO)==1){
        	EgDemandReason demandReason = demandGenericDao.getEgDemandReasonByCodeInstallmentModule(DEMANDRSN_CODE_VACANT_TAX, installment, module, null);
        	demandDetails = propertyService.createDemandDetails(arrearsInfo.getVacantLandTax(), null, demandReason, installment);
        	ptDemand.getEgDemandDetails().add(demandDetails);
        }
        if(arrearsInfo.getLibraryCess()!=null && arrearsInfo.getLibraryCess().compareTo(BigDecimal.ZERO)==1){
        	EgDemandReason demandReason = demandGenericDao.getEgDemandReasonByCodeInstallmentModule(DEMANDRSN_CODE_LIBRARY_CESS, installment, module, null);
        	demandDetails = propertyService.createDemandDetails(arrearsInfo.getLibraryCess(), null, demandReason, installment);
        	ptDemand.getEgDemandDetails().add(demandDetails);
        }
        if(arrearsInfo.getEducationCess()!=null && arrearsInfo.getEducationCess().compareTo(BigDecimal.ZERO)==1){
        	EgDemandReason demandReason = demandGenericDao.getEgDemandReasonByCodeInstallmentModule(DEMANDRSN_CODE_EDUCATIONAL_CESS, installment, module, null);
        	demandDetails = propertyService.createDemandDetails(arrearsInfo.getEducationCess(), null, demandReason, installment);
        	ptDemand.getEgDemandDetails().add(demandDetails);
        }
        if(arrearsInfo.getUnauthorizedPenalty()!=null && arrearsInfo.getUnauthorizedPenalty().compareTo(BigDecimal.ZERO)==1){
        	EgDemandReason demandReason = demandGenericDao.getEgDemandReasonByCodeInstallmentModule(DEMANDRSN_CODE_UNAUTHORIZED_PENALTY, installment, module, null);
        	demandDetails = propertyService.createDemandDetails(arrearsInfo.getUnauthorizedPenalty(), null, demandReason, installment);
        	ptDemand.getEgDemandDetails().add(demandDetails);
        }
        if(arrearsInfo.getLatePaymentPenalty()!=null && arrearsInfo.getLatePaymentPenalty().compareTo(BigDecimal.ZERO)==1){
        	EgDemandReason demandReason = demandGenericDao.getEgDemandReasonByCodeInstallmentModule(DEMANDRSN_CODE_PENALTY_FINES, installment, module, null);
        	demandDetails = propertyService.createDemandDetails(arrearsInfo.getLatePaymentPenalty(), null, demandReason, installment);
        	ptDemand.getEgDemandDetails().add(demandDetails);
        }
	}
}
