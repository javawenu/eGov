package org.egov.stms.web.controller.utils;

import javax.servlet.http.HttpServletRequest;

import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Controller
public class ThirdPartyServices {

	public AssessmentDetails getPropertyDetails(final SewerageApplicationDetails sewerageApplicationDetails, final String assessmentNumber, final HttpServletRequest request) {
    	RestTemplate restTemplate = new RestTemplate();
    	String url = "http://" + request.getServerName() +":"+ request.getServerPort() + "/ptis/rest/property/{assessmentNumber}";
        AssessmentDetails propertyOwnerDetails = restTemplate.getForObject(url, AssessmentDetails.class,assessmentNumber);
       return propertyOwnerDetails;
    }
}
