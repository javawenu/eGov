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

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div class="panel panel-primary" data-collapsed="0">
	<div class="panel-heading">
		<div class="panel-title" style="text-align:center;"><spring:message code="lbl.title.search.estimateabstractreportbydepartmentwise" /></div>
	</div>
	<div class="panel-body">
		<div class="form-group">
			<label class="col-sm-3 control-label text-right"><spring:message code="lbl.financialyear" /><span class="mandatory"></span></label>
			<div class="col-sm-3 add-margin">
				<form:select path="financialYear" data-first-option="false" id="financialYear" class="form-control" onchange="getFinancialYearDatesByFYId(this.value);" required="required" >
					<form:option value=""><spring:message code="lbl.select" /></form:option>
					<form:options items="${financialyears}" itemValue="id" itemLabel="finYearRange" />
				</form:select>
				<form:errors path="financialYear" cssClass="add-margin error-msg" />
			</div>
			<label class="col-sm-2 control-label text-right"><spring:message code="lbl.department" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="department" data-first-option="false" id="department" class="form-control">
					<form:option value=""><spring:message code="lbl.select" /></form:option>
					<form:options items="${departments}" itemValue="id" itemLabel="name" />
				</form:select>
				<form:errors path="department" cssClass="add-margin error-msg" />
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right"><spring:message code="lbl.lineestimateadminsanctionfromdate" /></label>
			<div class="col-sm-3 add-margin">
				<form:input path="adminSanctionFromDate" class="form-control datepicker" id="adminSanctionFromDate" data-inputmask="'mask': 'd/m/y'"  disabled="true"/>
				<form:errors path="adminSanctionFromDate" cssClass="add-margin error-msg" />
			</div>
			<label class="col-sm-2 control-label text-right"><spring:message code="lbl.lineestimateadminsanctiontodate" /></label>
			<div class="col-sm-3 add-margin">
				<form:input path="adminSanctionToDate" class="form-control datepicker"	id="adminSanctionToDate" disabled="true" data-date-end-date="0d" data-inputmask="'mask': 'd/m/y'" />
				<form:errors path="adminSanctionToDate" cssClass="add-margin error-msg" />
			</div>
		</div>
			<div class="form-group">
			<label class="col-sm-3 control-label text-right"><spring:message code="lbl.scheme" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="scheme" data-first-option="false" id="scheme" class="form-control" onchange="getSubSchemsBySchemeId(this.value);" >
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<c:if test="${!schemes.isEmpty()}">
						<form:options items="${schemes}" itemValue="id" itemLabel="name" />
					</c:if>
				</form:select>
			</div>
			<label class="col-sm-2 control-label text-right"><spring:message code="lbl.subscheme" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="subScheme" data-first-option="false"	id="subScheme" class="form-control">
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
				</form:select>
			</div>
		</div>
		<div class="form-group" id="radioValue" >
			<label class="col-sm-3 control-label text-right"><spring:message code="lbl.slum" /></label>
			<div class="col-sm-3 add-margin">
				<form:radiobutton path="workCategory" id="slum" value="SLUM_WORK" onclick="showSlumFields();" />
			</div>
			<label class="col-sm-2 control-label text-right"><spring:message code="lbl.nonslum" />
			</label>
			<div class="col-sm-3 add-margin">
				<form:radiobutton path="workCategory" id="nonslum" value="NON_SLUM_WORK" onclick="disableSlumFields()" />
			</div>
		</div>
		<div id="slumfields" style="display: none">
			<div class="form-group">
				<label class="col-sm-3 control-label text-right"> <spring:message code="lbl.typeofslum" />
				</label>
				<div class="col-sm-3 add-margin">
					<form:select path="typeOfSlum" data-first-option="false" id="typeOfSlum" cssClass="form-control" >
						<form:option value="">
							<spring:message code="lbl.select" />
						</form:option>
						<form:options items="${typeOfSlum}" />
					</form:select>
					<form:errors path="typeOfSlum" cssClass="add-margin error-msg" />
				</div>
				<div>
					<label class="col-sm-2 control-label text-right"><spring:message code="lbl.beneficiary" /></label>
					<div class="col-sm-3 add-margin">
						<form:select path="beneficiary" data-first-option="false" id="beneficiary" class="form-control">
							<form:option value="">
								<spring:message code="lbl.select" />
							</form:option>
							<form:options items="${beneficiary}" />
						</form:select>
						<form:errors path="beneficiary" cssClass="add-margin error-msg" />
					</div>
				</div>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label text-right"><spring:message code="lbl.natureofwork" /></label>
			<div class="col-sm-3 add-margin">
				<form:select path="natureOfWork" data-first-option="false" id="natureOfWork" class="form-control" >
					<form:option value="">
						<spring:message code="lbl.select" />
					</form:option>
					<form:options items="${natureOfWork}" itemLabel="name" itemValue="id" />
				</form:select>
				<form:errors path="natureOfWork" cssClass="add-margin error-msg" /> 
			</div>
			<label class="col-sm-2 control-label text-right"><spring:message code="lbl.spilloverworks" /></label>
			<div class="col-sm-3 add-margin">
			<form:checkbox path="spillOverFlag" id="spillOverFlag" />
			<form:hidden path="currentFinancialYearId" id = "currentFinancialYearId" />
			</div>
		</div>
	</div>
  </div>
