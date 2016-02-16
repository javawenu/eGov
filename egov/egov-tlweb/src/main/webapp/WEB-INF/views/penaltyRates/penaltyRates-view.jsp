<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>
  <div class="row">
    <div class="col-md-12">
      <div class="panel panel-primary" data-collapsed="0">
        <div class="panel-heading">
          <div class="panel-title"><spring:message code="title.penaltyRate"/></div>
        </div>
        <div class="panel-body custom">
          <div class="row add-border">
            <div class="col-xs-3 add-margin">
              <spring:message code="lbl.licenseAppType" />
            </div>
            <div class="col-sm-3 add-margin view-content">${penaltyRates.licenseAppType.name}</div>
          </div>
          <div class="row add-border">
            <div class="col-xs-3 add-margin">
              <spring:message code="lbl.from" />
            </div>
            <div class="col-sm-3 add-margin view-content">${penaltyRates.fromRange}</div>
            <div class="col-xs-3 add-margin">
              <spring:message code="lbl.to" />
            </div>
            <div class="col-sm-3 add-margin view-content">${penaltyRates.toRange}</div>
          </div>
          <div class="row add-border">
            <div class="col-xs-3 add-margin">
              <spring:message code="lbl.rate" />
            </div>
            <div class="col-sm-3 add-margin view-content">${penaltyRates.rate}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="row text-center">
    <div class="add-margin">
      <a href="javascript:void(0)" class="btn btn-default" onclick="self.close()"><spring:message code='lbl.close' /></a>
    </div>
  </div>