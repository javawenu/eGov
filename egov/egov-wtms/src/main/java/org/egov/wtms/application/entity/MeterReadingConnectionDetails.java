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

package org.egov.wtms.application.entity;

import org.egov.infra.persistence.entity.AbstractAuditable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "egwtr_meter_connection_details")
@SequenceGenerator(name = MeterReadingConnectionDetails.SEQ, sequenceName = MeterReadingConnectionDetails.SEQ, allocationSize = 1)
public class MeterReadingConnectionDetails extends AbstractAuditable {

    private static final long serialVersionUID = 25508399800297413L;

    public static final String SEQ = "seq_egwtr_meter_connection_details";

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connectiondetailsid", nullable = false)
    private WaterConnectionDetails waterConnectionDetails;

    private Long currentReading;

    private Date currentReadingDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public WaterConnectionDetails getWaterConnectionDetails() {
        return waterConnectionDetails;
    }

    public void setWaterConnectionDetails(final WaterConnectionDetails waterConnectionDetails) {
        this.waterConnectionDetails = waterConnectionDetails;
    }

    public Long getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(final Long currentReading) {
        this.currentReading = currentReading;
    }

    public Date getCurrentReadingDate() {
        return currentReadingDate;
    }

    public void setCurrentReadingDate(final Date currentReadingDate) {
        this.currentReadingDate = currentReadingDate;
    }

}
