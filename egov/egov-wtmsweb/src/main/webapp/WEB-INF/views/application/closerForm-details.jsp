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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="panel panel-primary" data-collapsed="0">
								<div class="panel-heading">
									<div class="panel-title">
									<spring:message  code="lbl.closureheader"/> 
									</div>
									
								</div>
								<input type="hidden" id="closeConnectionType" name="closeConnectionType" value="${closeConnectionType}"/>  
					<div class="panel-body">
					<div class="form-group">
										<label class="col-sm-3 col-xs-12 control-label text-right"><spring:message  code="lbl.connectiontype"/>
                                        </label>
						<div class="col-sm-6 add-margin dynamic-span capitalize">
							<form:radiobuttons path="closeConnectionType" items="${radioButtonMap}"   name="closeConnectionType" element="span" /> 
						</div>
					</div>
					</div>
                    <div class="form-group">
										<label class="col-sm-3 col-xs-12 control-label text-right"><spring:message  code="lbl.closerReason"/>
                                        </label>
                                        <div class="col-sm-8 col-xs-12 add-margin">
                                           <form:input class="form-control" id="closeconnectionreason" path="closeconnectionreason" name="closeconnectionreason" />
	
                    </div>
				</div>
                                    
</div>
<script>

var mode =$('#mode').val();
if(mode !='closeredit'){
	closureConnectionType($('#closeConnectionType').val());
	$("input[name=closeConnectionType]").prop("disabled", true);
$('#closeconnectionreason').attr('disabled', true);
}
if(mode =='closereditForAE')
	{
	closureConnectionType($('#closeConnectionType').val());
	$('#closeconnectionreason').attr('disabled', false);
	$("input[name=closeConnectionType]").prop("disabled", false);
	}
else
{
	closureConnectionType($('#closeConnectionType').val());
	$('#closeconnectionreason').attr('disabled', false);
	$("input[name=closeConnectionType]").prop("disabled", false);
	//$(':radio:not(:checked)').attr('disabled', false);
}
if(mode == "" ){
	closureConnectionType($('#closeConnectionType').val());
	//$(':radio:not(:checked)').attr('disabled', true);
	$("input[name=closeConnectionType]").prop("disabled", true);
	$('#closeconnectionreason').attr('disabled', true);
	}
	
	function closureConnectionType(conType)
	{
		if(conType == 'T'){
		$("input[name=closeConnectionType][value='Temporary']").prop("checked", true)
	}else{
		$("input[name=closeConnectionType][value='Permanent']").prop("checked", true);
	}
     }
	
</script>

