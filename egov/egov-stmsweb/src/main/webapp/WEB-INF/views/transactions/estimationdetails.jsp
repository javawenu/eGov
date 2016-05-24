<!-- #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#------------------------------------------------------------------------------- -->
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> 

<div class="row">
	<div class="col-md-12">
	<%--  <form:hidden path="fieldInspectionDetails" id="fieldInspectionDetails" value="${sewerageApplicationDetails.id}"/>  --%> 
		<div class="panel panel-primary" data-collapsed="0">				
			<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.fieldinspection.details" />
				</div>					
			</div>
			
			<div class="panel-body">
				<div class="form-group">
					<div class="row">
						<label class="col-sm-3 control-label"><spring:message  code="lbl.inspectiondate"/></label> 
						<div class="col-sm-3 add-margin">
							<input class="form-control datepicker today" data-date-end-date="0d" data-inputmask="'mask': 'd/m/y'" id="inspectionDate" name="inspectionDate" required="required" /> 
						</div>
				
						<label class="col-sm-3 control-label text-right"><spring:message code="lbl.attachdocument"/></label>
						<div class="col-sm-3 add-margin">
							<input type="file" id="fileStoreId" name="files" class="file-ellipsis upload-file"> 
							<div class="add-margin error-msg" ><font size="2">
								<spring:message code="lbl.mesg.document"/>	
								</font></div>
						</div>
					</div>
			   </div>
			
			
				<div class="panel-heading">
					<div class="panel-title">
							<spring:message code="lbl.pipe.details" />
					</div>	
				</div>
				<table class="table table-striped table-bordered" id="inspectionDetails">
					<thead>
					      <tr>
							<th class="text-center"><spring:message code="lbl.slno" /></th>
							<th class="text-center"><spring:message code="lbl.noofpipes" /></th>
							<th class="text-center"><spring:message code="lbl.pipesize.inches" /></th>
							<th class="text-center"><spring:message code="lbl.pipelength" /></th>
							<th class="text-center"><spring:message code="lbl.screwsize.inches" /></th>
							<th class="text-center"><spring:message code="lbl.noOfScrews" /></th>
							<th class="text-center"><spring:message code="lbl.property.distance" /></th>
							<th class="text-center"><spring:message code="lbl.roaddigging" /></th>
							<th class="text-center"><spring:message code="lbl.roadlength" /></th>
							<th class="text-center"><spring:message code="lbl.roadowner" /></th>
							<th class="text-center"><spring:message code="lbl.actions" /></th>
					      </tr>
				         </thead> 
					<tbody>
					<c:choose>
						<c:when test="${!sewerageApplicationDetails.fieldInspections.isEmpty() &&
						 !sewerageApplicationDetails.fieldInspections[sewerageApplicationDetails.fieldInspections.size()].fieldInspectionDetails.isEmpty()}">
							<c:forEach items="${sewerageApplicationDetails.fieldInspections[sewerageApplicationDetails.fieldInspections.size()].fieldInspectionDetails}" var="var1"
								varStatus="counter"> 
							      <tr class="">
									<td class="text-center"><span id="slNoInsp1">${counter.index+1}</span></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].noOfPipes" id="fieldInspectionDetails${counter.index}noOfPipes" maxlength="8" value="${var1.noOfPipes}" /></td>
									<td class="text-center">
										<form:select path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].pipeSize" data-first-option="false" id="fieldInspectionDetails${counter.index}pipeSize"
											cssClass="form-control" required="required" >
											<form:option value="">
												<spring:message code="lbl.select" />
											</form:option>
											<c:forEach items="${pipeSize}" var="entry">
										        <option value="${entry.key}">${entry.value}</option>
										    </c:forEach>
										</form:select>		
									</td>						
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].pipeLength" id="fieldInspectionDetails${counter.index}pipeLength" maxlength="8" value="${var1.pipeLength}" /></td>
									<td class="text-right">
									<form:select path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].screwSize" data-first-option="false" id="fieldInspectionDetails${counter.index}screwSize"
											cssClass="form-control" required="required" >
											<form:option value="">
												<spring:message code="lbl.select" />
											</form:option>
											<c:forEach items="${pipeSize}" var="entry">
										        <option value="${entry.key}">${entry.value}</option> 
										    </c:forEach>
										</form:select>	 
									</td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].noOfScrews" id="fieldInspectionDetails${counter.index}noOfScrews" maxlength="8" value="${var1.noOfScrews}" /></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].distance" id="fieldInspectionDetails${counter.index}distance" maxlength="8" value="${var1.distance}" /></td>
									<td class="text-right"><form:checkbox cssClass="form-control" path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].roadDigging" id="fieldInspectionDetails${counter.index}roadDigging" maxlength="8" value="${var1.roadDigging}" /></td>
									<td class="text-right"><form:input type="text" disabled="true" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].roadLength" id="fieldInspectionDetails${counter.index}roadLength" maxlength="8" value="${var1.roadLength}" /></td>
							      	<td class="text-right">
									<form:select path="fieldInspections[${sewerageApplicationDetails.fieldInspections.size()+1}].fieldInspectionDetails[${counter.index}].roadOwner" disabled="true" data-first-option="false" id="fieldInspectionDetails${counter.index}roadOwner"
											cssClass="form-control" required="required" >
											<form:option value="">
												<spring:message code="lbl.select" />
											</form:option>
											<c:forEach items="${roadOwner}" var="entry">
										        <option value="${entry}">${entry}</option>  
										    </c:forEach>
										</form:select>	 
									</td>
									
									<c:if test="${counter.index==0}">
										<td class="text-center"><span style="cursor:pointer;" id="addInspctRowId"><i class="fa fa-plus"></i></span></td>
									</c:if>
									<c:if test="${counter.index!=0}">
										<td class="text-center"><span style="cursor:pointer;" id="delete_insp_row"><i class="fa fa-trash"></i></span></td>
									</c:if>
							      </tr>
				     		 </c:forEach>
						</c:when> 
						<c:otherwise> 
							      <tr class="">
									<td class="text-center"><span id="slNoInsp1">1</span></td>
									<td class="text-center"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[0].fieldInspectionDetails[0].noOfPipes" id="fieldInspectionDetails0noOfPipes" maxlength="8" value="0" /></td>
									<td class="text-right">
									<form:select path="fieldInspections[0].fieldInspectionDetails[0].pipeSize" data-first-option="false" id="fieldInspectionDetails0pipeSize"
											cssClass="form-control" required="required" >
											<form:option value="">
												<spring:message code="lbl.select" />
											</form:option>
											<c:forEach items="${pipeSize}" var="entry">
										        <option value="${entry.key}">${entry.key}</option> 
										    </c:forEach>
										</form:select>	 
									</td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[0].fieldInspectionDetails[0].pipeLength" id="fieldInspectionDetails0pipeLength" maxlength="8" value="0" /></td>
									<td class="text-right">
									<form:select path="fieldInspections[0].fieldInspectionDetails[0].screwSize" data-first-option="false" id="fieldInspectionDetails0screwSize"
											cssClass="form-control" required="required" >
											<form:option value="">
												<spring:message code="lbl.select" />
											</form:option>
											<c:forEach items="${pipeSize}" var="entry">
										        <option value="${entry.key}">${entry.value}</option> 
										    </c:forEach>
										</form:select>	 
									</td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[0].fieldInspectionDetails[0].noOfScrews" id="fieldInspectionDetails0noOfScrews" maxlength="8" value="0" /></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[0].fieldInspectionDetails[0].distance" id="fieldInspectionDetails0distance" maxlength="8" value="0" /></td>
									<td class="text-right"><form:checkbox cssClass="form-control" path="fieldInspections[0].fieldInspectionDetails[0].roadDigging" id="fieldInspectionDetails0roadDigging" value="false" checked=""   onchange="enableDisableRoadInfo(this);"/></td> 
									<td class="text-right"><form:input type="text" disabled="true" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="fieldInspections[0].fieldInspectionDetails[0].roadLength" id="fieldInspectionDetails0roadLength" maxlength="8" value="0" /></td>
							      	<td class="text-right">
									<form:select path="fieldInspections[0].fieldInspectionDetails[0].roadOwner" disabled="true" data-first-option="false" id="fieldInspectionDetails0roadOwner"
											cssClass="form-control" required="required" >
											<form:option value="">
												<spring:message code="lbl.select" /> 
											</form:option>
											<c:forEach items="${roadOwner}" var="entry">
										        <option value="${entry}">${entry}</option> 
										    </c:forEach>
										</form:select>	 
									</td>
									<td class="text-center"><span style="cursor:pointer;" id="addInspctRowId"><i class="fa fa-plus"></i></span></td>
							      </tr>
							</c:otherwise> 
						</c:choose>
					</tbody> 
				</table>
			
				<div class="panel-heading">
					<div class="panel-title">
							<spring:message code="lbl.estimation.details" />
					</div>	
				</div>
				<table class="table table-striped table-bordered" id="estimateDetails">
					<thead>
					      <tr>
							<th class="text-center"><spring:message code="lbl.slno" /></th>
							<th class="text-center"><spring:message code="lbl.material" /><span class="mandatory"></span></th>
							<th class="text-center"><spring:message code="lbl.quantity" /></th>
							<th class="text-center"><spring:message code="lbl.uom" /></th>
							<th class="text-center"><spring:message code="lbl.rate" /></th>
							<th class="text-center"><spring:message code="lbl.amount" /></th>
							<th class="text-center"><spring:message code="lbl.actions" /></th>
					      </tr>
				         </thead>
					<tbody>
						<c:choose>
							<c:when test="${!sewerageApplicationDetails.estimationDetails.isEmpty()}">
								<c:forEach items="${sewerageApplicationDetails.estimationDetails}" var="var1"
									varStatus="counter">
							      <tr class="">
									<td class="text-center"><span id="slNo1">${counter.index+1}</span></td>
									<td class="text-center"><form:textarea class="form-control table-input" path="estimationDetails[${counter.index}].itemDescription" id="estimationDetails${counter.index}itemDescription" required="required" maxlength="256" value="${var1.itemDescription}"></form:textarea></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="estimationDetails[${counter.index}].quantity" id="estimationDetails${counter.index}quantity" maxlength="8" onblur="calculateTotalAmount();" value="${var1.quantity}"/></td>
									<td class="text-right">
										<form:select path="estimationDetails[${counter.index}].unitOfMeasurement" data-first-option="false" id="estimationDetails${counter.index}unitOfMeasurement"
												cssClass="form-control" required="required" >
												<form:option value="">
													<spring:message code="lbl.select" /> 
												</form:option>
												<c:forEach items="${uomList}" var="entry">
											        <option value="${entry.id}">${entry.uom}</option> 
											    </c:forEach>
										</form:select>	
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation unitrate" data-pattern="decimalvalue" path="estimationDetails[${counter.index}].unitRate" id="estimationDetails${counter.index}unitRate" maxlength="8" onblur="calculateTotalAmount();" value="${var1.unitRate}" /></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="estimationDetails[${counter.index}].amount" id="estimationDetails${counter.index}amount" maxlength="8" onblur="calculateTotalAmount();" value="${var1.amount}" /></td>
									<c:if test="${counter.index==0}">
										<td class="text-center"><span style="cursor:pointer;" id="addRowId"><i class="fa fa-plus"></i></span></td>
									</c:if>
									<c:if test="${counter.index!=0}">
										<td class="text-center"><span style="cursor:pointer;" id="delete_row"><i class="fa fa-trash"></i></span></td>
									</c:if>
							      </tr>
					       		</c:forEach>
							</c:when> 
							<c:otherwise>
							      <tr class="">
									<td class="text-center"><span id="slNo1">1</span></td>
									<td class="text-center"><form:textarea class="form-control table-input" path="estimationDetails[0].itemDescription" id="estimationDetails0itemDescription" required="required" maxlength="256"></form:textarea></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="estimationDetails[0].quantity" id="estimationDetails0quantity" maxlength="8" onblur="calculateTotalAmount();" value="0"/></td>
									<td class="text-right">
										<form:select path="estimationDetails[0].unitOfMeasurement" data-first-option="false" id="estimationDetails0unitOfMeasurement"
												cssClass="form-control" required="required" >
												<form:option value="">
													<spring:message code="lbl.select" /> 
												</form:option>
												<c:forEach items="${uomList}" var="entry">
											        <option value="${entry.id}">${entry.uom}</option> 
											    </c:forEach>
										</form:select>	
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation unitrate" data-pattern="decimalvalue" path="estimationDetails[0].unitRate" id="estimationDetails0unitRate" maxlength="8" onblur="calculateTotalAmount();" value="0.00" /></td>
									<td class="text-right"><form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="estimationDetails[0].amount" id="estimationDetails0amount" maxlength="8" onblur="calculateTotalAmount();" value="0.00" /></td>
									<td class="text-center"><span style="cursor:pointer;" id="addRowId"><i class="fa fa-plus"></i></span></td>
							      </tr>
							</c:otherwise> 
						</c:choose>
					      <tr class="">
							<td class="text-center"></td>
							<td class="text-center"></td>
							<td class="text-right"></td>
							<td class="text-right"></td>
							<td class="text-right"><spring:message code="lbl.grandtotal" /></td>
							<td class="text-right"><input type="text" class="form-control text-right" id="grandTotal" disabled="true"></td>
							<td class="text-center"></td>
					      </tr>
					</tbody>
				</table>
				
				<div class="panel-heading">
					<div class="panel-title">
							<spring:message code="lbl.fees.details" />
					</div>	
				</div>
				<table class="table table-striped table-bordered" id="estimateDetails">
					<thead>
					      <tr>
							<th class="text-center"><spring:message code="lbl.slno" /></th>
							<th class="text-center"><spring:message code="lbl.feestype" /></th>
							<th class="text-center"><spring:message code="lbl.amount" /></th>
					      </tr>
			         </thead>
			         <tbody>
			         <c:choose>
							<c:when test="${!feesDetails.isEmpty()}">
								<c:forEach items="${feesDetails}" var="var1"
									varStatus="counter">
							      <tr class="">
									<td class="text-center"><span id="slNo1">${counter.index+1}</span></td>
									<td class="text-center">${var1.description}</td>
									<td class="text-right">
									<form:hidden path="connectionFees[${counter.index}].feesDetail" value="${var1.id}"/>
									<form:input type="text" class="form-control table-input text-right patternvalidation quantity" data-pattern="decimalvalue" path="connectionFees[${counter.index}].amount" id="connectionFees${counter.index}amount" maxlength="8" value="0.00" /></td>
								  </tr>
								</c:forEach>
							</c:when>
					 </c:choose>
			         </tbody>
				</table>
			</div>				
		</div>
	</div>
</div>
<script src="<c:url value='/resources/js/transactions/estimatedetails.js?rnd=${app_release_no}'/>"></script>
<script src="<c:url value='/resources/js/transactions/documentsupload.js?rnd=${app_release_no}'/>"></script>
<script src="<c:url value='/resources/javascript/helper.js' context='/stms'/>"></script>
<script	src="<c:url value='/resources/global/js/bootstrap/bootstrap-datepicker.js' context='/egi'/>"
	type="text/javascript"></script>