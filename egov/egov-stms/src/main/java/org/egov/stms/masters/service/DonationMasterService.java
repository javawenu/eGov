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
package org.egov.stms.masters.service;

import java.util.Date;
import java.util.List;
import org.egov.stms.masters.entity.DonationMaster;
import org.egov.stms.masters.entity.enums.PropertyType;
import org.egov.stms.masters.repository.DonationMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DonationMasterService {

    private final DonationMasterRepository donationMasterRepository;

    @Autowired
    public DonationMasterService(final DonationMasterRepository donationMasterRepository) {
        this.donationMasterRepository = donationMasterRepository;
    }

    public DonationMaster findById(final Long id) {
        return donationMasterRepository.findOne(id);
    }

    @Transactional
    public DonationMaster create(final DonationMaster donationMaster) {
        return donationMasterRepository.save(donationMaster);
    }

    @Transactional
    public void update(final DonationMaster donationMaster) {
        donationMasterRepository.save(donationMaster);
    }

    // TODO : noofcloset removed as part of entity change - need to read from donationdetail
    public List<DonationMaster> findAll() {
        return donationMasterRepository.findAll(new Sort(Sort.Direction.DESC, "propertyType","fromDate"));
    }

    public List<DonationMaster> findAllByPropertyType(final PropertyType propertyType) {
        return donationMasterRepository.findAllByPropertyType(propertyType);
    }

    public DonationMaster load(final Long id) {
        return donationMasterRepository.getOne(id);
    }
    
 // TODO : noofcloset removed as part of entity change - need to read from donationdetail
    public DonationMaster findByPropertyTypeAndFromDateAndActive(final PropertyType propertyType,
            final Date fromDate, final boolean active) {
        return donationMasterRepository.findByPropertyTypeAndFromDateAndActive(propertyType, fromDate, active);
    }

    public DonationMaster findByPropertyTypeAndActive(final PropertyType propertyType,
            final boolean active) {
        return donationMasterRepository.findByPropertyTypeAndActive(propertyType, active);
    }
}