package org.egov.stms.web.controller.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.stms.elasticSearch.entity.SewerageSearchResult;
import org.egov.stms.utils.constants.SewerageTaxConstants;

public  class SewerageActionDropDownUtil {
	
	public static final Map<String,String> ACTIONMETHODMAP = new LinkedHashMap<String,String>();
	
	 	public static final String VIEW = "View";
	    public static final String COLLECTDONATIONCHARHGES = "Collect Donation charges";
	    public static final String VIEWURL = "/stms/existing/sewerage/view/";
	    public static final String COLLECTDONATIONCHARHGESURL = "/stms/existing/sewerage/donationcharges/";
	    
	    public static final Map<String,String> statusCreatedActions() {
	    	ACTIONMETHODMAP.put(SewerageActionDropDownUtil.VIEWURL, SewerageActionDropDownUtil.VIEW);
	    	ACTIONMETHODMAP.put(SewerageActionDropDownUtil.COLLECTDONATIONCHARHGESURL, SewerageActionDropDownUtil.COLLECTDONATIONCHARHGES);
	    	return ACTIONMETHODMAP;
	    }
	    
	    public static final SewerageSearchResult getSearchResultWithActions(final String status) {
	    	 SewerageSearchResult searchActions = new SewerageSearchResult();
	    	if(status!=null && status.toUpperCase().equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED.toString().toUpperCase())) {
        		searchActions.setActions(statusCreatedActions());
        	
        		}
	    	return searchActions;
	    }

}
