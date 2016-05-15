/* eGov suite of products aim to improve the internal efficiency,transparency,
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

package org.egov.mrs.application.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.egov.commons.dao.InstallmentDao;
import org.egov.demand.dao.DemandGenericDao;
import org.egov.demand.dao.EgDemandDao;
import org.egov.demand.dao.EgDemandDetailsDao;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.mrs.application.Constants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides abstraction for creating its Marriage Fee
 *
 * @author nayeem
 *
 */
public abstract class DemandService {

    @Autowired
    protected EgDemandDao egDemandDAO;

    @Autowired
    protected EgDemandDetailsDao egDemandDetailsDAO;

    @Autowired
    protected DemandGenericDao demandGenericDAO;

    @Autowired
    protected ModuleService moduleService;

    @Autowired
    protected InstallmentDao installmentDAO;

    public EgDemand createDemand(final BigDecimal amount) {
        final EgDemand egDemand = new EgDemand();
        egDemand.setBaseDemand(amount);
        egDemand.setEgDemandDetails(createDemandDetails(amount));
        egDemand.setCreateDate(new Date());
        egDemand.setModifiedDate(new Date());
        egDemand.setIsHistory("N");
        egDemand.setEgInstallmentMaster(installmentDAO
                .getInsatllmentByModuleForGivenDate(moduleService.getModuleByName(Constants.MODULE_NAME), new Date()));
        return egDemandDAO.create(egDemand);
    }

    public abstract Set<EgDemandDetails> createDemandDetails(BigDecimal amount);
}