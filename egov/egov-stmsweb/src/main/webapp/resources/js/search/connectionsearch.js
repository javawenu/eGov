/*#-------------------------------------------------------------------------------
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
#-------------------------------------------------------------------------------*/



var tableContainer;
jQuery(document).ready(function($) {
	
	
	tableContainer = $("#aplicationSearchResults");
	 document.onkeydown=function(evt){
		 var keyCode = evt ? (evt.which ? evt.which : evt.keyCode) : event.keyCode;
	if(keyCode == 13){
		submitButton();	
	}
	 }
});
	
$(".btn-primary").click(function(event){
	$('#searchSewerageapplication').show();
	var dhscNumber=$('#dhscNumber').val();
	var applicantName=$('#applicantName').val();
	var mobileNumber=$('#mobileNumber').val();
	var wardName=$('#app-mobno').val();
	var doorNo=$('#app-appcodo').val();
	
	if(dhscNumber ==''  && applicantName == ''  && mobileNumber == ''
			&& wardName == ''  && doorNo == '') {
				bootbox.alert("Please Enter Atleast One Input Value For Searching");
				return false;
			}
			else {
				submitButton();
				return true;
			}
		event.preventDefault();
	});



$(document).on('change', 'select.actiondropdown', function() {
	if($(this).find(":selected").index()>0){
		callurl($(this).val(), $(this).data('consumer-no'));
	}
});

function callurl(url, consumernumber){
	var url = url+ consumernumber;
	$('#sewerageSearchRequestForm').attr('method', 'get');
	$('#sewerageSearchRequestForm').attr('action', url);
	window.open(url,'window','scrollbars=yes,resizable=yes,height=700,width=800,status=yes');
}


function submitButton() {
	
	tableContainer = $("#aplicationSearchResults");
	$('#searchResultDiv').show();
	$.post("/stms/existing/sewerage/",$('#sewerageSearchRequestForm').serialize())
	.done(function(searchResult) {
	console.log(JSON.stringify(searchResult));
	tableContainer.dataTable({
	destroy : true,
	"sPaginationType" : "bootstrap",
	"sDom": "<'row'<'col-xs-12 hidden col-right'f>r>t<'row'<'col-md-3 col-xs-12'i><'col-md-3 col-xs-6 col-right'l><'col-xs-12 col-md-3 col-right'<'export-data'T>><'col-md-3 col-xs-6 text-right'p>>",
	"aLengthMenu" : [[10,25,50,-1 ],[10,25,50,"All" ] ],
	"autoWidth" : false,
	"oTableTools" : {
		"sSwfPath" : "../../../>${action}../../../egi/resources/global/swf/copy_csv_xls_pdf.swf",
		"aButtons" : [ 
		               {
			             "sExtends": "pdf",
	                     "sPdfMessage": "",
	                     "sTitle": "Sewerage Connection Report",
	                     "sPdfOrientation": "landscape"
		                },
		                {
				             "sExtends": "xls",
	                         "sPdfMessage": "Sewerage Connection Report",
	                         "sTitle": "Sewerage Connection Report"
			             },
			             {
				             "sExtends": "print",
	                         "sTitle": "Sewerage Connection Report"
			             }],
	},
	searchable : true,
	data : searchResult,
	columns : [{title : 'consumer number', data : "document.resource.searchable.consumernumber", "visible": false},
	           {title : 'Applicant Name',data : 'document.resource.searchable.consumername'},
	           {title : 'S.H.S.C Number',class : 'row-detail',data : 'document.resource.searchable.dhscnumber'},               
	           {title : 'Property type',data : 'document.resource.clauses.propertytype'},
	           {title : 'Revenue ward',data : 'document.resource.clauses.revwardname'},
	           {title : 'Address',data : 'document.resource.searchable.address'},
	           {"title" : "Actions","sortable":false,
	           render : function(data, type, row) {
	        	   console.log('data ->'+ JSON.stringify(row));
	        	   var option = "<option>Select from Below</option>";
	        	   $.each(row.actions, function(key, value){
	        		   option+= "<option value="+key+">"+value+"</option>";
	        	   });
				   return ('<select class="actiondropdown" data-consumer-no="'+ row.document.resource.searchable.consumernumber +'">'+ option +'</select>');
	           }}],
	           "aaSorting": [[5, 'asc']]
	});
	
	if(tableContainer.fnGetData().length > 1000) {
		$('#searchResultDiv').hide();
		$('#search-exceed-msg').show();
	} else {
		$('#search-exceed-msg').hide();
		$('#searchResultDiv').show();
	}
	})
}

