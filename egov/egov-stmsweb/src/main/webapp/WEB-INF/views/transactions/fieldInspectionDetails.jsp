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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
	<div class="panel-body">
		<div class="panel-heading">
				<div class="panel-title">
					<spring:message code="lbl.fieldinspection.details"/>
				</div>
			</div>
		
				<table class="table table-striped table-bordered" id="fieldInspectionDetails">
					<thead>
					      <tr>
							<th class="text-center"><spring:message code="lbl.noofpipes" /></th>
							<th class="text-center"><spring:message code="lbl.pipesize.inches" /></th>
							<th class="text-center"><spring:message code="lbl.pipelength" /></th>
							<th class="text-center"><spring:message code="lbl.noOfScrews" /></th>
							<th class="text-center"><spring:message code="lbl.roaddigging" /></th>
							<th class="text-center"><spring:message code="lbl.roadlength" /></th>
							<th class="text-center"><spring:message code="lbl.distance" /></th>
							<th class="text-center"><spring:message code="lbl.roadowner" /></th>
					      </tr>
				         </thead>
					<tbody>
						
								<c:forEach items="${sewerageApplicationDetails.fieldInspectionDetails}" var="fid"
									varStatus="counter">
							      <tr class="">
									<%-- <td class="text-center"><span id="slNo1">${counter.index+1}</span></td> --%>
									<td class="text-center"><c:out value="${fid.noOfPipes}" /></td>
									<td class="text-right"><c:out value="${fid.pipeSize}" /></td>
									<td class="text-right"><c:out value="${fid.pipeLength}"/></td>
									<td class="text-right"><c:out value="${fid.noOfScrews}"/></td>
									<c:choose> 
									  <c:when test="${fid.roadDigging}">
									    <td class="text-right"><c:out value="Yes"/></td>
									  </c:when>
									  <c:otherwise>
									    <td class="text-right"><c:out value="No"/></td>
									  </c:otherwise>
									</c:choose>
									<td class="text-right"><c:out value="${fid.roadLength}"/></td>
									<td class="text-right"><c:out value="${fid.distance}"/></td>
									<td class="text-right"><c:out value="${fid.roadOwner}"/></td>
									
							      </tr>
					       		</c:forEach>
					</tbody>
				</table>
		
	</div>

