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

package org.egov.stms.web.controller.elasticSearch;

import static java.util.Arrays.asList;
import static org.egov.ptis.constants.PropertyTaxConstants.REVENUE_HIERARCHY_TYPE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.config.search.Index;
import org.egov.config.search.IndexType;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.utils.EgovThreadLocals;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.search.domain.Document;
import org.egov.search.domain.Page;
import org.egov.search.domain.SearchResult;
import org.egov.search.domain.Sort;
import org.egov.search.service.SearchService;
import org.egov.stms.elasticSearch.entity.SewerageSearchResult;
import org.egov.stms.elasticSearch.entity.SewerageConnSearchRequest;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.service.SewerageApplicationDetailsService;
import org.egov.stms.transactions.service.SewerageConnectionService;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.egov.stms.web.controller.utils.SewerageActionDropDownUtil;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/existing/sewerage")
public class ApplicationSewerageSearchController {

	@Autowired
	private SearchService searchService;
	@Autowired
	private CityService cityService;
	@Autowired
	private SewerageApplicationDetailsService sewerageApplicationDetailsService;
	@Autowired
	private BoundaryService boundaryService;

	@Autowired
	private SewerageTaxUtils sewerageTaxUtils;

	@Autowired
	protected BasicPropertyDAO basicPropertyDAO;

	@Autowired
	private SewerageConnectionService sewerageConnectionService;


    private static final Logger LOGGER = Logger.getLogger(ApplicationSewerageSearchController.class);


    @ModelAttribute
    public SewerageConnSearchRequest searchRequest() {
        return new SewerageConnSearchRequest();
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String newSearchForm(final Model model) {
        return "sewerageTaxSearch-form"; 
    }
    
    public @ModelAttribute("revenueWards") List<Boundary> revenueWardList() {
        return boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(SewerageTaxConstants.REVENUE_WARD,
                REVENUE_HIERARCHY_TYPE);
    }
    @RequestMapping(value = "/view/{consumernumber}",method = RequestMethod.GET)
    public ModelAndView view(@PathVariable final String consumernumber,final Model model, final ModelMap modelMap,@ModelAttribute SewerageApplicationDetails sewerageApplicationDetails) {

        if (consumernumber != null)
            sewerageApplicationDetails = sewerageApplicationDetailsService
                    .findByApplicationNumber(consumernumber);
        setCommonDetails(sewerageApplicationDetails, modelMap);
        return new ModelAndView("viewseweragedetails", "sewerageApplicationDetails", sewerageApplicationDetails);
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public List<SewerageSearchResult> searchApplication(@ModelAttribute final SewerageConnSearchRequest searchRequest) {
        final City cityWebsite = cityService.getCityByURL(EgovThreadLocals.getDomainName());
        searchRequest.setUlbName(cityWebsite.getName());   
        final Sort sort = Sort.by().field("clauses.applicationdate", SortOrder.DESC); //TODO: MOVE THIS CONSTANT TO COMMON FILE.
        final SearchResult searchResult = searchService.search(asList(Index.SEWARAGE.toString()),
                asList(IndexType.SEWARAGESEARCH.toString()), searchRequest.searchQuery(),
                searchRequest.searchFilters(), sort, Page.NULL);

        List<SewerageSearchResult> searchResultFomatted = new ArrayList<SewerageSearchResult>(0);

		for (final Document document : searchResult.getDocuments()) {

			Map<String, String> searchableObjects = (Map<String, String>) document.getResource().get("searchable");
			if (searchableObjects != null) {
				SewerageSearchResult searchActions = SewerageActionDropDownUtil
						.getSearchResultWithActions(searchableObjects.get("status"));
				if (searchActions != null) {
					searchActions.setDocument(document);
					searchResultFomatted.add(searchActions);
				}
			}
		}
        return searchResultFomatted;
    }
    
    
    
    private void setCommonDetails(final SewerageApplicationDetails sewerageApplicationDetails, final ModelMap modelMap) {
        String assessmentNumber = sewerageApplicationDetails.getConnection().getConnectionDetail().getPropertyIdentifier();
        final BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNumber);
       //TODO: DO NOT INJECT  	basicPropertyDAO HERE. USE REST API TO GET BASIC PROPERTY INFORMATION.
      if(basicProperty!=null) {
        modelMap.addAttribute("propertyAddress", basicProperty.getAddress().toString());

        final PropertyOwnerInfo propertyOwnerInfo = basicProperty.getPropertyOwnerInfo().get(0);
        modelMap.addAttribute("mobileNumber", propertyOwnerInfo.getOwner().getMobileNumber());
        modelMap.addAttribute("emailAddress", propertyOwnerInfo.getOwner().getEmailId());
        modelMap.addAttribute("applicantName", propertyOwnerInfo.getOwner().getName());
        modelMap.addAttribute("aadhaarNumber", propertyOwnerInfo.getOwner().getAadhaarNumber());
      }
        final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(assessmentNumber,
                PropertyExternalService.FLAG_FULL_DETAILS);
        if(assessmentDetails!=null){
        modelMap.addAttribute("locality", assessmentDetails.getBoundaryDetails().getLocalityName());
        modelMap.addAttribute("zoneWardBlockDetails", getZoneWardBlockDetails(assessmentDetails));
        }

        final BigDecimal sewerageTaxDue = sewerageConnectionService.getTotalAmount(sewerageApplicationDetails.getConnection());
        modelMap.addAttribute("sewerageTaxDue", sewerageTaxDue);
    }

    private String getZoneWardBlockDetails(final AssessmentDetails assessmentDetails) {
        final StringBuffer zoneWardBlockName = new StringBuffer();
        if(assessmentDetails!=null){
        zoneWardBlockName.append(assessmentDetails.getBoundaryDetails().getZoneName()).append("/");
        zoneWardBlockName.append(assessmentDetails.getBoundaryDetails().getWardName()).append("/");
        zoneWardBlockName.append(assessmentDetails.getBoundaryDetails().getBlockName());
        }
        return zoneWardBlockName.toString();
    }
    
    
}