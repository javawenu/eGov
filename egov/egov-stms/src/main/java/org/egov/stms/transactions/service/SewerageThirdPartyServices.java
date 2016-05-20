package org.egov.stms.transactions.service;

import javax.servlet.http.HttpServletRequest;

import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly = true)
public class SewerageThirdPartyServices {

	public AssessmentDetails getPropertyDetails(final SewerageApplicationDetails sewerageApplicationDetails, final String assessmentNumber, final HttpServletRequest request) {
    	RestTemplate restTemplate = new RestTemplate();
    	String url = "http://" + request.getServerName() +":"+ request.getServerPort() + "/ptis/rest/property/{assessmentNumber}";
        AssessmentDetails propertyOwnerDetails = restTemplate.getForObject(url, AssessmentDetails.class,assessmentNumber);
       return propertyOwnerDetails;
    }
}
