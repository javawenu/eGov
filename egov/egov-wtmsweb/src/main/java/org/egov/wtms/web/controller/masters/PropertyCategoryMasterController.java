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

package org.egov.wtms.web.controller.masters;

import org.egov.wtms.masters.entity.PropertyCategory;
import org.egov.wtms.masters.service.ConnectionCategoryService;
import org.egov.wtms.masters.service.PropertyCategoryService;
import org.egov.wtms.masters.service.PropertyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/masters")
public class PropertyCategoryMasterController {

    private final PropertyTypeService propertyTypeService;

    private final ConnectionCategoryService connectionCategoryService;

    private final PropertyCategoryService propertyCategoryService;

    @Autowired
    public PropertyCategoryMasterController(final PropertyTypeService propertyTypeService,
            final ConnectionCategoryService connectionCategoryService,
            final PropertyCategoryService propertyCategoryService) {
        this.propertyTypeService = propertyTypeService;
        this.connectionCategoryService = connectionCategoryService;
        this.propertyCategoryService = propertyCategoryService;

    }

    @RequestMapping(value = "/propertyCategoryMaster", method = GET)
    public String viewForm(final Model model) {
        final PropertyCategory propertyCategory = new PropertyCategory();
        model.addAttribute("propertyCategory", propertyCategory);
        model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
        model.addAttribute("connectionCategory", connectionCategoryService.getAllActiveConnectionCategory());
        model.addAttribute("reqAttr", "false");
        return "property-category-master";
    }

    @RequestMapping(value = "/propertyCategoryMaster", method = RequestMethod.POST)
    public String addPropertyCategoryMasterData(@Valid @ModelAttribute final PropertyCategory propertyCategory,
            final BindingResult errors, final Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
            model.addAttribute("connectionCategory", connectionCategoryService.getAllActiveConnectionCategory());
            return "property-category-master";
        } else
            propertyCategoryService.createPropertyCategory(propertyCategory);
        return getPropertyCategoryMasterList(model);
    }

    @RequestMapping(value = "/propertyCategoryMaster/list", method = GET)
    public String getPropertyCategoryMasterList(final Model model) {
        final List<PropertyCategory> propertyCategoryList = propertyCategoryService.findAll();
        model.addAttribute("propertyCategoryList", propertyCategoryList);
        return "property-category-master-list";
    }

    @RequestMapping(value = "/propertyCategoryMaster/{propertyCategoryId}", method = GET)
    public String getPropertyCategoryMasterDetails(final Model model, @PathVariable final String propertyCategoryId) {
        final PropertyCategory propertyCategory = propertyCategoryService.findOne(Long.parseLong(propertyCategoryId));
        model.addAttribute("propertyCategory", propertyCategory);
        model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
        model.addAttribute("connectionCategory", connectionCategoryService.getAllActiveConnectionCategory());
        model.addAttribute("reqAttr", "true");
        return "property-category-master";
    }

    @RequestMapping(value = "/propertyCategoryMaster/{propertyCategoryId}", method = RequestMethod.POST)
    public String editPropertyCategoryMasterData(@Valid @ModelAttribute final PropertyCategory propertyCategory,
            final BindingResult errors, final Model model, @PathVariable final long propertyCategoryId) {
        if (errors.hasErrors()) {
            model.addAttribute("propertyType", propertyTypeService.getAllActivePropertyTypes());
            model.addAttribute("connectionCategory", connectionCategoryService.getAllActiveConnectionCategory());
            return "property-category-master";
        } else
            propertyCategoryService.updatePropertyCategory(propertyCategory);
        return getPropertyCategoryMasterList(model);
    }

}