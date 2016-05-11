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
package org.egov.stms.transactions.service;

import java.math.BigDecimal;
import java.util.List;

import org.egov.demand.model.EgDemand;
import org.egov.stms.transactions.entity.SewerageConnection;
import org.egov.stms.transactions.repository.SewerageConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SewerageConnectionService {

    private final SewerageConnectionRepository sewerageConnectionRepository;

    @Autowired
    public SewerageConnectionService(final SewerageConnectionRepository sewerageConnectionRepository) {
        this.sewerageConnectionRepository = sewerageConnectionRepository;
    }

    public SewerageConnection findBy(final Long id) {
        return sewerageConnectionRepository.findOne(id);
    }

    public List<SewerageConnection> findAll() {
        return sewerageConnectionRepository.findAll(new Sort(Sort.Direction.ASC, "dhscNumber"));
    }

    public SewerageConnection findByDhscNumber(final String dhscNumber) {
        return sewerageConnectionRepository.findByDhscNumber(dhscNumber);
    }

    public SewerageConnection load(final Long id) {
        return sewerageConnectionRepository.getOne(id);
    }

    public Page<SewerageConnection> getListSewerageConnections(final Integer pageNumber, final Integer pageSize) {
        final Pageable pageable = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "dhscNumber");
        return sewerageConnectionRepository.findAll(pageable);
    }

    public List<SewerageConnection> findByPropertyIdentifier(final String propertyIdentifier) {
        //TODO : commented as part of design change. propertyIdentifier moved to connectiondetail
        //return sewerageConnectionRepository.findByPropertyIdentifier(propertyIdentifier);
        return null;
    }

    public BigDecimal getTotalAmount(final SewerageConnection sewerageConnection) {
        final EgDemand currentDemand = sewerageConnection.getDemand();
        BigDecimal balance = BigDecimal.ZERO;
        /*
         * if (currentDemand != null) { final List<Object> instVsAmt =
         * connectionDemandService.getDmdCollAmtInstallmentWise(currentDemand); for (final Object object : instVsAmt) { final
         * Object[] ddObject = (Object[]) object; final BigDecimal dmdAmt = (BigDecimal) ddObject[2]; BigDecimal collAmt =
         * BigDecimal.ZERO; if (ddObject[2] != null) collAmt = new BigDecimal((Double) ddObject[3]); balance =
         * balance.add(dmdAmt.subtract(collAmt)); } }
         */
        return balance;
    }
}
