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
$(document).ready(function()
{	
	showHideRoadInfo();
	//addRow();
	//add_Inspection_Row();
	if($('#pipelineDistance') != null && $('#pipelineDistance').val() == 0.0)
		$('#pipelineDistance').val('');	
	
	if($('#estimationCharges') != null && $('#estimationCharges').val() == 0.0)
		$('#estimationCharges').val('');
	
	loadConnectionCategories();
	
	$('#connectionCategorie').change(function () {
		loadConnectionCategories();
    });
	
	function loadConnectionCategories(){
	    if ($('#connectionCategorie :selected').text().localeCompare("BPL") == 0)  {
	    	$("#cardHolderDiv").show();
	    	$("#bplCardHolderName").attr('required', 'required');
	    }
	    else{
	    	$("#cardHolderDiv").hide();
	    	$("#bplCardHolderName").removeAttr('required');
	    }
	}
	
	$("#addRowId").click(function(){	
		addRow();
	});	
			
	function addRow() {
        var table = document.getElementById('estimateDetails');
        var rowCount = table.rows.length;
        if((rowCount-2) >= 5) {
        	bootbox.alert("Maximum of only 5 rows are allowed!!");
        	return;
        }
        var row = table.insertRow(rowCount-1);
        var counts = rowCount - 1;

 		elementIndex = counts - 1;
        var newRow = document.createElement("tr");
    	var newCol = document.createElement("td");
		newRow.appendChild(newCol);
		 
        var cell1 = row.insertCell(0);
        cell1.className="text-center";
        var slno = document.createElement("span");
        slno.setAttribute("id","slNo"+counts);
        slno.innerHTML=counts;
        cell1.appendChild(slno); 
        
        newCol = document.createElement("td");        
        newRow.appendChild(newCol);
        var cell2 = row.insertCell(1);
        cell2.className = "text-center";
        var material = document.createElement("textarea");
        var att = document.createAttribute("class");
 		att.value = "form-control table-input";
 		material.setAttributeNode(att); 
 		material.type = "textarea";
 		material.setAttribute("required", "required");
 		material.setAttribute("maxlength", "256");
 		material.setAttribute("name", "estimationDetails[" + elementIndex + "].itemDescription");
 		material.setAttribute("id", "estimationDetails"+elementIndex+"itemDescription");
        cell2.appendChild(material);
        
        newCol = document.createElement("td");
		newRow.appendChild(newCol);
        var cell3 = row.insertCell(2);
        cell3.className = "text-right";
        var quantity = document.createElement("input");
        quantity.setAttribute("class","form-control table-input text-right patternvalidation");
        quantity.setAttribute("data-pattern","decimalvalue"); 
        quantity.type = "text";
        quantity.setAttribute("maxlength", "8");
        quantity.setAttribute("name", "estimationDetails[" + elementIndex + "].quantity");
        quantity.setAttribute("id", "estimationDetails"+elementIndex+"quantity");
        quantity.setAttribute("value", 0);
        quantity.setAttribute("onblur", "calculateTotalAmount();");
        cell3.appendChild(quantity);  
        
        newCol = document.createElement("td");
		newRow.appendChild(newCol);
        var cell4 = row.insertCell(3);
       // cell4.className="text-center";
        var uom = document.createElement("select");
        uom.setAttribute("class","form-control");
       // uom.setAttribute("data-pattern","alphanumerichyphenbackslash");  
       // uom.type = "text";
       // uom.setAttribute("maxlength", "50");
        uom.setAttribute("name", "estimationDetails[" + elementIndex + "].unitOfMeasurement");
        uom.setAttribute("id", "estimationDetails"+elementIndex+"unitOfMeasurement");
        cell4.appendChild(uom);  
        $('#estimationDetails'+elementIndex+'unitOfMeasurement').html($("#estimationDetails0unitOfMeasurement").html());
        
        newCol = document.createElement("td");
		newRow.appendChild(newCol);
        var cell5 = row.insertCell(4);
        cell5.className = "text-right";
        var unitRate = document.createElement("input");
        unitRate.setAttribute("class","form-control table-input text-right patternvalidation");
        unitRate.setAttribute("data-pattern","decimalvalue"); 
        unitRate.type = "text";
        unitRate.setAttribute("maxlength", "8");
        unitRate.setAttribute("name", "estimationDetails[" + elementIndex + "].unitRate");
        unitRate.setAttribute("id", "estimationDetails"+elementIndex+"unitRate");  
        unitRate.setAttribute("value", 0.00); 
        unitRate.setAttribute("onblur", "calculateTotalAmount();");
        cell5.appendChild(unitRate);  
       
        newCol = document.createElement("td");
		newRow.appendChild(newCol);
        var cell6 = row.insertCell(5); 
        cell6.className = "text-right";
        var total = document.createElement("input");
        total.setAttribute("class","form-control table-input text-right patternvalidation");
        total.setAttribute("data-pattern","decimalvalue")
        total.type = "text";
        total.setAttribute("name", "estimationDetails[" + elementIndex + "].amount");
        total.setAttribute("id", "estimationDetails"+elementIndex+"amount");
        total.setAttribute("value", 0.00);
        cell6.appendChild(total);  
        
        newCol = document.createElement("td");
		newRow.appendChild(newCol); 
        var cell7 = row.insertCell(6);
        cell7.className = "text-center";
        var actions = document.createElement("span");
        actions.setAttribute("style","cursor:pointer");
       	actions.innerHTML = '<i class="fa fa-trash" id="delete_row" ></i>';
        cell7.appendChild(actions); 
        patternvalidation();
	}
	
	$(document).on('click',"#delete_row",function (){
		var table = document.getElementById('estimateDetails');
        var rowCount = table.rows.length;
		$(this).closest('tr').remove();		
        var counts = rowCount - 1;
        var j = 2;
        var i;
        for(i=2;i<=counts-1;i++){ 
        	var serialNo = '#slNo'+i;
        	var prevIndex = i-1;
        	var currentIndex = j-1; 
        	var itemDesc = '#estimationDetails'+prevIndex+'itemDescription';
        	var quantity = '#estimationDetails'+prevIndex+'quantity';
        	var unitOfMeasurement = '#estimationDetails'+prevIndex+'unitOfMeasurement';
        	var unitRate = '#estimationDetails'+prevIndex+'unitRate';
        	var amount = '#estimationDetails'+prevIndex+'amount';
        	if($(serialNo) != null && $(serialNo).html() != '' && $(serialNo).html() != undefined ) {
	        	$(serialNo).html(j);
	        	$(serialNo).attr("id", 'slNo'+j); 
	        	$(itemDesc).attr("id", 'estimationDetails'+currentIndex+'itemDescription'); 
	        	$(quantity).attr("id", 'estimationDetails'+currentIndex+'quantity'); 
	        	$(unitOfMeasurement).attr("id", 'estimationDetails'+currentIndex+'unitOfMeasurement'); 
	        	$(unitRate).attr("id", 'estimationDetails'+currentIndex+'unitRate'); 
	        	$(amount).attr("id", 'estimationDetails'+currentIndex+'amount');
	        	j++;
        	}
        }	
        calculateTotalAmount();
	});	
	
});

function calculateTotalAmount() {
	var table = document.getElementById('estimateDetails');
    var rowCount = table.rows.length;     
    var i;
    var grandTotal=0;
    for(i=1;i<=rowCount-2;i++){  
    	currentIndex = i-1; 
    	var quantity = $('#estimationDetails'+currentIndex+'quantity').val();
    	var unitRate = $('#estimationDetails'+currentIndex+'unitRate').val();
    	var total = 0;
    	if(quantity!='' && unitRate!='') {
    		total = quantity*unitRate;
    		$('#estimationDetails'+currentIndex+'amount').val(total);
    		grandTotal = grandTotal + total;
    	}
    }
    $('#grandTotal').val(grandTotal);
}

$("#addInspctRowId").click(function(){	
	add_Inspection_Row();
});	
		
function add_Inspection_Row() {
    var table = document.getElementById('inspectionDetails');
    var rowCount = table.rows.length+1;
    if((rowCount-2) >= 5) {
    	bootbox.alert("Maximum of only 5 rows are allowed!!");
    	return;
    }
    
    var row = table.insertRow(rowCount-1);
    var counts = rowCount - 1;
	elementIndex = counts - 1;
    var newRow = document.createElement("tr");
	var newCol = document.createElement("td");
	newRow.appendChild(newCol);
	 
    var cell1 = row.insertCell(0);
    cell1.className="text-center";
    var slno = document.createElement("span");
    slno.setAttribute("id","slNoInsp"+counts);
    slno.innerHTML=counts;
    cell1.appendChild(slno); 
    
    newCol = document.createElement("td");        
    newRow.appendChild(newCol);
    var cell2 = row.insertCell(1);
    cell2.className = "text-right";
    var pipe = document.createElement("input");
    pipe.setAttribute("class","form-control table-input text-right patternvalidation");
    pipe.setAttribute("data-pattern","decimalvalue"); 
    pipe.type = "text";
    pipe.setAttribute("maxlength", "8");
    pipe.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].noOfPipes");
    pipe.setAttribute("id", "fieldInspectionDetails"+elementIndex+"noOfPipes");
    cell2.appendChild(pipe);
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell3 = row.insertCell(2);
   // cell3.className = "text-right";
    var pipeSize = document.createElement("select");
    pipeSize.setAttribute("class","form-control");
    pipeSize.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].pipeSize");
    pipeSize.setAttribute("id", "fieldInspectionDetails"+elementIndex+"pipeSize");
    cell3.appendChild(pipeSize); 
    $('#fieldInspectionDetails'+elementIndex+'pipeSize').html($("#fieldInspectionDetails0pipeSize").html());
    
    newCol = document.createElement("td");        
    newRow.appendChild(newCol);
    var cell4 = row.insertCell(3);
    cell4.className = "text-right";
    var pipeLength = document.createElement("input");
    pipeLength.setAttribute("class","form-control table-input text-right patternvalidation");
    pipeLength.setAttribute("data-pattern","decimalvalue"); 
    pipeLength.type = "text";
    pipeLength.setAttribute("maxlength", "8");
    pipeLength.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].pipeLength");
    pipeLength.setAttribute("id", "fieldInspectionDetails"+elementIndex+"pipeLength");
    cell4.appendChild(pipeLength);
    
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell5 = row.insertCell(4);
    //cell5.className = "text-right";
    var screwSize = document.createElement("select");
    screwSize.setAttribute("class","form-control");
    screwSize.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].screwSize");
    screwSize.setAttribute("id", "fieldInspectionDetails"+elementIndex+"screwSize");
    cell5.appendChild(screwSize); 
    $('#fieldInspectionDetails'+elementIndex+'screwSize').html($("#fieldInspectionDetails0screwSize").html());
  
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell6 = row.insertCell(5);
    cell6.className="text-right";
    var screw = document.createElement("input");
    screw.setAttribute("class","form-control table-input text-right patternvalidation");
    screw.setAttribute("data-pattern","decimalvalue");  
    screw.type = "text";
    screw.setAttribute("maxlength", "8");
    screw.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].noOfScrews");
    screw.setAttribute("id", "fieldInspectionDetails"+elementIndex+"noOfScrews");
    cell6.appendChild(screw); 
    
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell7 = row.insertCell(6);
    cell7.className="text-right";
    var distance = document.createElement("input");
    distance.setAttribute("class","form-control table-input text-right patternvalidation");
    distance.setAttribute("data-pattern","decimalvalue");  
    distance.type = "text";
    distance.setAttribute("maxlength", "8");
    distance.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].distance");
    distance.setAttribute("id", "fieldInspectionDetails"+elementIndex+"distance");
    cell7.appendChild(distance);
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell8 = row.insertCell(7);
    //cell8.className="text-right";
    var roadDigging = document.createElement("input");
    roadDigging.type= 'checkbox';
    roadDigging.checked='';
    roadDigging.value='false';
   /* roadDigging.setAttribute("class","form-control table-input text-right patternvalidation");
    roadDigging.setAttribute("data-pattern","decimalvalue");  
    roadDigging.type = "text";
    roadDigging.setAttribute("maxlength", "8");*/
    roadDigging.setAttribute("class","form-control");
    roadDigging.setAttribute("onclick","enableDisableRoadInfo(this)");
    roadDigging.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].roadDigging");
    roadDigging.setAttribute("id", "fieldInspectionDetails"+elementIndex+"roadDigging");
    cell8.appendChild(roadDigging);
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell9 = row.insertCell(8);
    cell9.className="text-right";
    var roadLength = document.createElement("input");
    roadLength.setAttribute("class","form-control table-input text-right patternvalidation"); 
    roadLength.setAttribute("data-pattern","decimalvalue");  
    roadLength.type = "text";
    roadLength.setAttribute("maxlength", "8");
    roadLength.setAttribute("disabled","true");
    roadLength.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].roadLength");
    roadLength.setAttribute("id", "fieldInspectionDetails"+elementIndex+"roadLength");
    cell9.appendChild(roadLength);
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol);
    var cell10 = row.insertCell(9);
    //cell10.className = "text-right";
    var roadOwner = document.createElement("select");
    roadOwner.setAttribute("class","form-control");
    roadOwner.setAttribute("disabled","true");
    roadOwner.setAttribute("name", "fieldInspections[0].fieldInspectionDetails[" + elementIndex + "].roadOwner");
    roadOwner.setAttribute("id", "fieldInspectionDetails"+elementIndex+"roadOwner");
    cell10.appendChild(roadOwner); 
    $('#fieldInspectionDetails'+elementIndex+'roadOwner').html($("#fieldInspectionDetails0roadOwner").html());
    
    newCol = document.createElement("td");
	newRow.appendChild(newCol); 
    var cell11 = row.insertCell(10);
    cell11.className = "text-center";
    var actions = document.createElement("span");
    actions.setAttribute("style","cursor:pointer");
   	actions.innerHTML = '<i class="fa fa-trash" id="delete_insp_row" ></i>';
    cell11.appendChild(actions); 
    patternvalidation();
}

$(document).on('click',"#delete_insp_row",function (){
	var table = document.getElementById('inspectionDetails');
    var rowCount = table.rows.length;
	$(this).closest('tr').remove();		
    var counts = rowCount - 1;
    var j = 2;
    var i;
    for(i=2;i<=counts;i++){ 
    	var serialNo = '#slNoInsp'+i;
    	var prevIndex = i-1;
    	var currentIndex = j-1; 
    	var noOfPipes = '#fieldInspectionDetails'+prevIndex+'noOfPipes';
    	var pipeSize = '#fieldInspectionDetails'+prevIndex+'pipeSize'; 
    	var pipeLength = '#fieldInspectionDetails'+prevIndex+'pipeLength';
    	var screwSize = '#fieldInspectionDetails'+prevIndex+'screwSize';
    	var noOfScrews = '#fieldInspectionDetails'+prevIndex+'noOfScrews';
    	var distance = '#fieldInspectionDetails'+prevIndex+'distance';
    	var roadDigging = '#fieldInspectionDetails'+prevIndex+'roadDigging'; 
    	var roadLength = '#fieldInspectionDetails'+prevIndex+'roadLength';
    	var roadOwner = '#fieldInspectionDetails'+prevIndex+'roadOwner';
    	if($(serialNo) != null && $(serialNo).html() != '' && $(serialNo).html() != undefined ) {
        	$(serialNo).html(j);
        	$(serialNo).attr("id", 'slNoInsp'+j); 
        	$(noOfPipes).attr("id", 'fieldInspectionDetails'+currentIndex+'noOfPipes'); 
        	$(pipeSize).attr("id", 'fieldInspectionDetails'+currentIndex+'pipeSize');
        	$(pipeLength).attr("id", 'fieldInspectionDetails'+currentIndex+'pipeLength'); 
        	$(screwSize).attr("id", 'fieldInspectionDetails'+currentIndex+'screwSize'); 
        	$(noOfScrews).attr("id", 'fieldInspectionDetails'+currentIndex+'noOfScrews'); 
        	$(distance).attr("id", 'fieldInspectionDetails'+currentIndex+'distance'); 
        	$(roadDigging).attr("id", 'fieldInspectionDetails'+currentIndex+'roadDigging'); 
        	$(roadLength).attr("id", 'fieldInspectionDetails'+currentIndex+'roadLength'); 
          	$(roadOwner).attr("id", 'fieldInspectionDetails'+currentIndex+'roadOwner'); 
        	j++;
    	}
    }	
});	


function enableDisableRoadInfo(obj){ 
	var rIndex = getRow(obj).rowIndex;
	var tbl = document.getElementById('inspectionDetails');
	var firmval=getControlInBranch(tbl.rows[rIndex],'fieldInspectionDetails'+(rIndex-1)+'roadDigging'); 
	var roadLength=getControlInBranch(tbl.rows[rIndex],'fieldInspectionDetails'+(rIndex-1)+'roadLength');
	var roadOwner=getControlInBranch(tbl.rows[rIndex],'fieldInspectionDetails'+(rIndex-1)+'roadOwner');
	if(firmval.checked==true){
		roadLength.disabled=false;
		roadOwner.disabled=false;
	} else {
		roadLength.disabled=true;
		roadOwner.disabled=true; 
		roadLength.value="";
		roadOwner.value=""; 
	}
}  

function showHideRoadInfo(){
	var tbl=document.getElementById("inspectionDetails");
    var tabLength = (tbl.rows.length)-1;
    for(var i=1;i<=tabLength;i++){
    	enableDisableRoadInfo(getControlInBranch(tbl.rows[i],'fieldInspectionDetails'+(i-1)+'roadDigging'));
    }
}