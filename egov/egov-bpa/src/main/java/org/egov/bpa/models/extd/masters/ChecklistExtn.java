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
package org.egov.bpa.models.extd.masters;

// Generated 13 Nov, 2012 12:35:05 PM by Hibernate Tools 3.4.0.CR1

import org.egov.infstr.models.BaseModel;

import java.util.HashSet;
import java.util.Set;

/**
 * Checklist generated by hbm2java
 */
public class ChecklistExtn extends BaseModel {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 1L;
	private ServiceTypeExtn serviceType;
	private String checklistType;
	private Set<CheckListDetailsExtn> checkListDetailsSet = new HashSet<CheckListDetailsExtn>(0);

	public Set<CheckListDetailsExtn> getCheckListDetailsSet() {
		return checkListDetailsSet;
	}

	public void setCheckListDetailsSet(Set<CheckListDetailsExtn> checkListDetailsSet) {
		this.checkListDetailsSet = checkListDetailsSet;
	}

	public ServiceTypeExtn getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceTypeExtn serviceType) {
		this.serviceType = serviceType;
	}

	public String getChecklistType() {
		return checklistType;
	}

	public void setChecklistType(String checklistType) {
		this.checklistType = checklistType;
	}

}
