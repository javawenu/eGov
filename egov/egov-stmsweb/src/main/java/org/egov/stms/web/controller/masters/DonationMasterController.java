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
package org.egov.stms.web.controller.masters;

import javax.validation.Valid;

import org.egov.stms.masters.entity.DonationMaster;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.service.DonationMasterService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/masters")
public class DonationMasterController {

    private final DonationMasterService donationMasterService;

    @Autowired
    public DonationMasterController(final DonationMasterService donationMasterService) {
        this.donationMasterService = donationMasterService;
    }

    @RequestMapping(value = "/donationmaster", method = RequestMethod.GET)
    public String showForm(
            @ModelAttribute DonationMaster donationMaster,
            final Model model) {
        donationMaster = new DonationMaster();
        model.addAttribute("donationmaster", donationMaster);
        model.addAttribute("propertyTypes", PropertyType.values());
        return "donation-master";
    }

    @RequestMapping(value = "/donationmaster ", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final DonationMaster donationMaster,
            final RedirectAttributes redirectAttrs, final Model model,
            final BindingResult resultBinder) {
        if (resultBinder.hasErrors()) {
            model.addAttribute("donationmaster", donationMaster);
            model.addAttribute("propertyTypes", PropertyType.values());
            return "donation-master";
        }

        DonationMaster donationMasterExist = new DonationMaster();
     // TODO : noofclosets removed as part of entity change - need to read from donationdetail
        donationMasterExist = donationMasterService.findByPropertyTypeAndFromDateAndActive
                (donationMaster.getPropertyType(), donationMaster.getFromDate(), true);
        if (donationMasterExist != null) {
            donationMasterExist.setActive(false);
            donationMasterExist.setToDate(donationMaster.getFromDate());
            donationMasterService.update(donationMasterExist);

            donationMaster.setActive(true);
            donationMasterService.create(donationMaster);
        } else {
            DonationMaster donationMasterOld = null;
         // TODO : amount removed as part of entity change - need to read from donationdetail
            donationMasterOld = donationMasterService.findByPropertyTypeAndActive(donationMaster.getPropertyType(),
             true);
            if (donationMasterOld != null) {
                donationMasterOld.setActive(false);
                donationMasterOld.setToDate(new DateTime(donationMaster.getFromDate()).minusDays(1).toDate());
                donationMasterService.update(donationMasterOld);
            }
            donationMaster.setActive(true);
            donationMasterService.create(donationMaster);
            redirectAttrs.addAttribute("donationMaster", donationMaster);
        }
        redirectAttrs.addAttribute("donationMaster", donationMaster);
        return "redirect:/masters/donationmastersuccess?id=" + donationMaster.getId();
    }

    @RequestMapping(value = "/donationmastersuccess", method = RequestMethod.GET)
    public String getSeweragerates(@RequestParam("id") Long id, Model model) {
        DonationMaster donationMaster = donationMasterService.findById(id);
        model.addAttribute("donationMaster", donationMaster);
        model.addAttribute("message", "msg.donationrate.creation.success");
        return "donation-master-success";
    }
}
