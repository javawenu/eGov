package org.egov.stms.web.controller.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.stms.elasticSearch.entity.SewerageSearchResult;
import org.egov.stms.utils.constants.SewerageTaxConstants;

public  class SewerageActionDropDownUtil {
	
	public static final Map<String,String> BOTHACTIONMETHODMAP = new LinkedHashMap<String,String>();
	public static final Map<String,String> VIEWACTIONMETHODMAP = new LinkedHashMap<String,String>();
	public static final Map<String,String> COLLECTACTIONMETHODMAP = new LinkedHashMap<String,String>();
	public static final Map<String,Map<String, String>> map = new HashMap<String, Map<String, String>>();
	
	 	
	    
	    public static final Map<String,String> statusCreatedActions() {
	    	BOTHACTIONMETHODMAP.put(SewerageUrlConstants.VIEWURL, SewerageUrlConstants.VIEW);
	    	BOTHACTIONMETHODMAP.put(SewerageUrlConstants.COLLECTDONATIONCHARHGESURL, SewerageUrlConstants.COLLECTDONATIONCHARHGES);
	    	VIEWACTIONMETHODMAP.put(SewerageUrlConstants.VIEWURL, SewerageUrlConstants.VIEW);
	    	COLLECTACTIONMETHODMAP.put(SewerageUrlConstants.COLLECTDONATIONCHARHGESURL, SewerageUrlConstants.COLLECTDONATIONCHARHGES);
	    	    	
	    	
	    	map.put("CLERK", BOTHACTIONMETHODMAP);
	    	map.put("ASSISTANTENGINEER", VIEWACTIONMETHODMAP);
	    	map.put("COLLECTIONOFFICER", COLLECTACTIONMETHODMAP);
	    	
	    	return BOTHACTIONMETHODMAP;
	    }
	    
	   private Map getActionMap(String role){
		   if(map.containsKey(role)){
			  return map.get(role);
		   }
		   return null;
	    }
	    
	    public static final SewerageSearchResult getSearchResultWithActions(final String status) {
	    	 SewerageSearchResult searchActions = new SewerageSearchResult();
	    	if(status!=null && status.toUpperCase().equals(SewerageTaxConstants.APPLICATION_STATUS_CREATED.toString().toUpperCase())) {
        		searchActions.setActions(statusCreatedActions());
        	
        		}
	    	if(status!=null && status.toUpperCase().equals(SewerageTaxConstants.APPLICATION_STATUS_FIELDINSPECTED.toString().toUpperCase())) {
        		searchActions.setActions(statusCreatedActions());
        	
        		}
	    	return searchActions;
	    }

}
