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
package org.egov.ptis.client.util;

import org.egov.ptis.domain.model.calculator.UnitTaxCalculationInfo;

import java.util.Comparator;

public enum UnitTaxInfoComparator implements Comparator<UnitTaxCalculationInfo> {
        UNIT_SORT {
            public int compare(UnitTaxCalculationInfo o1, UnitTaxCalculationInfo o2) {
                return o1.getFloorNumber().compareTo(o2.getFloorNumber());
            }},
        FLOOR_SORT {
            public int compare(UnitTaxCalculationInfo o1, UnitTaxCalculationInfo o2) {
                return o1.getFloorNumber().compareTo(o2.getFloorNumber());
            }},
        INST_SORT {
                public int compare(UnitTaxCalculationInfo o1, UnitTaxCalculationInfo o2) {
                    return o1.getOccpancyDate().compareTo(o2.getOccpancyDate());
                }};
        

    
    public static Comparator<UnitTaxCalculationInfo> getUnitComparator(final UnitTaxInfoComparator... multipleOptions) {
        return new Comparator<UnitTaxCalculationInfo>() {
            public int compare(UnitTaxCalculationInfo o1, UnitTaxCalculationInfo o2) {
                for (UnitTaxInfoComparator option : multipleOptions) {
                    int result = option.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }
}

