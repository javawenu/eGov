package org.egov.stms.elasticSearch.service;

import java.util.Iterator;

import org.egov.config.search.Index;
import org.egov.config.search.IndexType;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.search.elastic.annotation.Indexing;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.OwnerName;
import org.egov.stms.elasticSearch.entity.SewarageSearch;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewarageIndexService {
	
	@Autowired
	private CityService cityService;
	
	@Indexing(name = Index.SEWARAGE, type = IndexType.SEWARAGESEARCH) 
	public SewarageSearch createSewarageIndex(final SewerageApplicationDetails sewerageApplicationDetails, final AssessmentDetails assessmentDetails){ 
		
		final City cityWebsite = cityService.getCityByURL(EgovThreadLocals.getDomainName());
		
		SewarageSearch sewarageSearch = new SewarageSearch(sewerageApplicationDetails.getApplicationNumber(),
				 cityWebsite.getName(),cityWebsite.getGrade(), sewerageApplicationDetails.getCreatedDate(), cityWebsite.getDistrictName(), cityWebsite.getRegionName(),
					cityWebsite.getGrade());
		
		sewarageSearch.setApplicationCreatedBy(sewerageApplicationDetails.getCreatedBy().getName());
		sewarageSearch.setApplicationDate(sewerageApplicationDetails.getApplicationDate());
		sewarageSearch.setApplicationNumber(sewerageApplicationDetails.getApplicationNumber());
		sewarageSearch.setApplicationStatus(sewerageApplicationDetails.getStatus()!=null?sewerageApplicationDetails.getStatus().getDescription():"");
		sewarageSearch.setApplicationType(sewerageApplicationDetails.getApplicationType()!=null?sewerageApplicationDetails.getApplicationType().getName():"");
		sewarageSearch.setApprovalDate(sewerageApplicationDetails.getApprovalDate());
		sewarageSearch.setApprovalNumber(sewerageApplicationDetails.getApplicationNumber());
		sewarageSearch.setConnectionStatus(sewerageApplicationDetails.getConnection().getConnectionStatus()!=null?sewerageApplicationDetails.getConnection().getConnectionStatus().name():"");
		sewarageSearch.setCreatedDate(sewerageApplicationDetails.getCreatedDate());
		sewarageSearch.setDhscNumber(sewerageApplicationDetails.getConnection().getDhscNumber()!=null?sewerageApplicationDetails.getConnection().getDhscNumber():"");
		sewarageSearch.setDisposalDate(sewerageApplicationDetails.getDisposalDate());
		sewarageSearch.setExecutionDate(sewerageApplicationDetails.getConnection().getExecutionDate());
		sewarageSearch.setIslegacy(sewerageApplicationDetails.getConnection().getLegacy());
		sewarageSearch.setNoOfClosets_nonResidential(sewerageApplicationDetails.getConnection().getNoOfClosetsNonResidential());
		sewarageSearch.setNoOfClosets_residential(sewerageApplicationDetails.getConnection().getNoOfClosetsResidential());
		sewarageSearch.setPropertyIdentifier(sewerageApplicationDetails.getConnection().getPropertyIdentifier()!=null?sewerageApplicationDetails.getConnection().getPropertyIdentifier():"");
		sewarageSearch.setPropertyType(sewerageApplicationDetails.getConnection().getPropertyType()!=null?sewerageApplicationDetails.getConnection().getPropertyType().name():"");
		sewarageSearch.setWorkOrderDate(sewerageApplicationDetails.getWorkOrderDate());
		sewarageSearch.setWorkOrderNumber(sewerageApplicationDetails.getWorkOrderNumber()!=null?sewerageApplicationDetails.getWorkOrderNumber():"");
		
		 Iterator<OwnerName> ownerNameItr = null;
	        if (null != assessmentDetails.getOwnerNames())
	            ownerNameItr = assessmentDetails.getOwnerNames().iterator();
	        final StringBuilder consumerName = new StringBuilder();
	        final StringBuilder mobileNumber = new StringBuilder();
	        if (null != ownerNameItr && ownerNameItr.hasNext()) {
	            final OwnerName primaryOwner = ownerNameItr.next();
	            consumerName.append(primaryOwner.getOwnerName() != null ? primaryOwner.getOwnerName() : "");
	            mobileNumber.append(primaryOwner.getMobileNumber() != null ? primaryOwner.getMobileNumber() : "");
	            while (ownerNameItr.hasNext()) {
	                final OwnerName secondaryOwner = ownerNameItr.next();
	                consumerName.append(",").append(secondaryOwner.getOwnerName() != null ? secondaryOwner.getOwnerName() : "");
	                mobileNumber.append(",").append(secondaryOwner.getMobileNumber() != null ? secondaryOwner.getMobileNumber() : "");
	            }

	        }
		sewarageSearch.setMobileNumber(mobileNumber.toString());
		sewarageSearch.setConsumerName(consumerName.toString());
		sewarageSearch.setDoorNo(assessmentDetails.getHouseNo()!=null?assessmentDetails.getHouseNo():"");
		sewarageSearch.setWard(assessmentDetails.getBoundaryDetails()!=null?assessmentDetails.getBoundaryDetails().getWardName():"");
		sewarageSearch.setAddress(assessmentDetails.getPropertyAddress()!=null?assessmentDetails.getPropertyAddress():"");
		return sewarageSearch;
	}
	
}