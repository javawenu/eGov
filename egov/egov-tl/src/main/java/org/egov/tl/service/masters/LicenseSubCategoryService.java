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

package org.egov.tl.service.masters;

import org.egov.tl.entity.LicenseSubCategory;
import org.egov.tl.repository.LicenseSubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LicenseSubCategoryService {
    
    @Autowired
    private LicenseSubCategoryRepository licenseSubCategoryRepository;
    
    @Transactional
    public LicenseSubCategory create(LicenseSubCategory licenseSubCategory){
        return  licenseSubCategoryRepository.save(licenseSubCategory);
    }

    /**
     * @Description returns SubCategory object that matches param name
     * @param name
     * @return
     */
    public List<LicenseSubCategory> findAllSubCategoryByCategory(final Long categoryId) {
        return licenseSubCategoryRepository.findAllByCategoryId(categoryId);
    }
    
    
    /**
     * @Description returns subcategory object that matches param name
     * @param name
     * @return
     */
    public LicenseSubCategory findSubCategoryByName(final String name) {
        return licenseSubCategoryRepository.findByName(name);
    }

    /**
     * @Description returns subcategory object that matches param code
     * @param code
     * @return
     */
    public LicenseSubCategory findSubCategoryByCode(final String code) {
        return licenseSubCategoryRepository.findByCode(code);
    }
    
    public LicenseSubCategory findById(Long subCategoryId){
        return licenseSubCategoryRepository.findOne(subCategoryId);
    }
    
    public List<LicenseSubCategory> findAll(){
        return licenseSubCategoryRepository.findAll();
    }

}
