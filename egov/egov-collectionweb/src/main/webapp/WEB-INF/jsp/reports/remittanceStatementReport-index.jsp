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

<html>
<head>
<title><s:text name="remittanceStatementReport.title" /></title>
<script>
function clearErrors()
{
	// First clear all error messages 
	dom.get("comparedatemessage").style.display="none";
	dom.get("mandatoryfromdate").style.display="none";
	dom.get("mandatorytodate").style.display="none";
}

function validate()
{
	var fromdate=dom.get("fromDate").value;
	var todate=dom.get("toDate").value;
	var valSuccess = true;

	clearErrors();

	if(fromdate == "")
	{
		dom.get("mandatoryfromdate").style.display="block";
		valSuccess = false;
	}
	
	if(todate == "")
	{
		dom.get("mandatorytodate").style.display="block";
		valSuccess = false;
	}
	
	if(fromdate!="" && todate!="" && fromdate!=todate)
	{
		if(!checkFdateTdate(fromdate,todate))
		{
			dom.get("comparedatemessage").style.display="block";
			valSuccess = false;
		}
	}

	return valSuccess;
}

function getBankBranchList(){
        var service=dom.get("serviceId");
        var fund=dom.get("fundId");
        populatebranchId({serviceId:service.options[service.selectedIndex].value,fundId:fund.options[fund.selectedIndex].value});
}

function getBankAccountList(branch)
{
  var service = dom.get("serviceId");
  var fund = dom.get("fundId");
  populatebankaccountId({branchId:branch.options[branch.selectedIndex].value,serviceId:service.options[service.selectedIndex].value,
  	fundId:fund.options[fund.selectedIndex].value});
    
    
	
}
</script>
</head>
<span align="center" style="display: none" id="mandatoryfromdate">
<li><font size="2" color="red"><b> <s:text
	name="common.datemandatory.fromdate" /> </b></font></li>
</span>
<span align="center" style="display: none" id="mandatorytodate">
<li><font size="2" color="red"><b> <s:text
	name="common.datemandatory.todate" /> </b></font></li>
</span>
<span align="center" style="display: none" id="invaliddateformat">
<li><font size="2" color="red"><b> <s:text
	name="common.dateformat.errormessage" /> </b></font></li>
</span>
<span align="center" style="display: none" id="comparedatemessage">
<li><font size="2" color="red"><b> <s:text
	name="common.comparedate.errormessage" /> </b></font></li>
</span>
<body>
<s:form theme="simple" name="receiptRegisterForm"
	action="remittanceStatementReport-report.action">
	<div class="formmainbox">
	<div class="subheadnew"><s:text name="remittanceStatementReport.head" /></div>
	<div class="subheadsmallnew"><span class="subheadnew"><s:text
		name="collectionReport.criteria" /></span></div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="4%" class="bluebox">&nbsp;</td>
			<td width="21%" class="bluebox"><s:text
				name="collectionReport.criteria.fromdate" /><span class="mandatory"></span></td>
			<s:date name="fromDate" var="cdFormat" format="dd/MM/yyyy" />
			<td width="24%" class="bluebox"><s:textfield id="fromDate"
				name="fromDate" value="%{cdFormat}"
				onfocus="javascript:vDateType='3';"
				onkeyup="DateFormat(this,this.value,event,false,'3')" /><a
				href="javascript:show_calendar('forms[0].fromDate');"
				onmouseover="window.status='Date Picker';return true;"
				onmouseout="window.status='';return true;"><img
				src="/egi/resources/erp2/images/calendaricon.gif"
				alt="Date" width="18" height="18" border="0" align="absmiddle" /></a>
			<div class="highlight2" style="width: 80px">DD/MM/YYYY</div>
			</td>
			<td width="21%" class="bluebox"><s:text
				name="collectionReport.criteria.todate" /><span class="mandatory"></span></td>
			<s:date name="toDate" var="cdFormat1" format="dd/MM/yyyy" />
			<td width="30%" class="bluebox"><s:textfield id="toDate"
				name="toDate" value="%{cdFormat1}"
				onfocus="javascript:vDateType='3';"
				onkeyup="DateFormat(this,this.value,event,false,'3')" /><a
				href="javascript:show_calendar('forms[0].toDate');"
				onmouseover="window.status='Date Picker';return true;"
				onmouseout="window.status='';return true;"><img
				src="/egi/resources/erp2/images/calendaricon.gif"
				alt="Date" width="18" height="18" border="0" align="absmiddle" /></a>
			<div class="highlight2" style="width: 80px">DD/MM/YYYY</div>
			</td>
		</tr>
		
		<tr>
			<td width="4%" class="bluebox2">&nbsp;</td>
			<td width="21%" class="bluebox2"><s:text
				name="collectionReport.criteria.fund" /></td>
			<td width="24%" class="bluebox2"><s:select headerKey="-1"
				headerValue="%{getText('collectionReport.fund.all')}" name="fundId" id="fundId" cssClass="selectwk"
				list="dropdownData.collectionFundList" listKey="id" listValue="name"
				value="%{fundId}" onChange="getBankBranchList();"/></td>
			<td width="21%" class="bluebox2"><s:text
				name="collectionReport.criteria.service" /></td>
			<td width="30%" class="bluebox2"><s:select headerKey="-1"
				headerValue="%{getText('collectionReport.service.all')}" name="serviceId" id="serviceId" cssClass="selectwk"
				list="dropdownData.collectionServiceList" listKey="id" listValue="name"
				value="%{serviceId}" onChange="getBankBranchList();"/></td>
		</tr>	
		<tr>
		<egov:ajaxdropdown id="branchIdDropdown" fields="['Text','Value']" dropdownId='branchId'
                url='/receipts/ajaxBankRemittance-bankBranchList.action' /> 
			<td width="4%" class="bluebox">&nbsp;</td>
			<td width="21%" class="bluebox"><s:text
				name="collectionReport.bank.name" /></td>
			<td width="24%" class="bluebox"><s:select headerKey="-1"
				headerValue="%{getText('collectionReport.bank.all')}" name="branchId" id="branchId" cssClass="selectwk"
				list="dropdownData.bankList" listKey="id" listValue="name"
				value="%{branchId}" onchange="getBankAccountList(this)"/></td>
			<egov:ajaxdropdown id="bankaccountIdDropdown" fields="['Text','Value']" dropdownId='bankaccountId'
                url='/receipts/ajaxBankRemittance-accountList.action' /> 
			<td width="21%" class="bluebox"><s:text
				name="collectionReport.criteria.bankaccount" /></td>
			<td width="30%" class="bluebox"><s:select headerKey="-1"
				headerValue="%{getText('collectionReport.bankaccount.all')}" name="bankaccountId" id="bankaccountId" cssClass="selectwk"
				list="dropdownData.bankAccountList" listKey="id" listValue="accountnumber"
				value="%{bankaccountId}" /></td>
		</tr>	
		
		<tr>
			<td width="4%" class="bluebox2">&nbsp;</td>
			<td width="21%" class="bluebox2">
				<s:text name="collectionReport.criteria.payment.mode"/></td>
	        	<td width="30%" class="bluebox2"><s:select headerKey="-1"
				headerValue="%{getText('collectionReport.payment.mode.all')}" 
				name="paymentMode" id="paymentMode" cssClass="selectwk" 
				list="paymentModes" value="%{paymentMode}" /> </td>
				
		   <td width="21%" class="bluebox2">
				<s:text name="collectionReport.criteria.collectionlocation"/></td>
	        	<td width="30%" class="bluebox2"><s:select headerKey="-1"
				headerValue="%{getText('collectionReport.collectionlocation.all')}" 
				name="deptId" id="deptId" cssClass="selectwk" 
				list="dropdownData.boundaryList" value="%{deptId}"  listKey="id" listValue="name"/> </td>
		</tr>
	
	
	</table>
<div align="left" class="mandatorycoll"><s:text name="common.mandatoryfields"/></div>
	</div>
	
	<div class="buttonbottom">
			<label>
				<s:submit type="submit" cssClass="buttonsubmit" id="button"
					value="%{getText('collectionReport.create')}"
					onclick="return validate();" />
			</label>&nbsp;
			<label>
				<s:reset type="submit" cssClass="button"
					value="%{getText('collectionReport.reset')}"
					onclick="return clearErrors();" />
			</label>&nbsp;
			<label>
				<input type="button" class="button" id="buttonClose"
					value="<s:text name='common.buttons.close'/>"
					onclick="window.close()" />
			</label>
		</div>
</s:form>
</body>
</html>
