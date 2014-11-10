
var purchaseOrderNumber = document.properties["rwa:PurchaseOrderNumberType"];
var purchaseOrderDate = document.properties["rwa:PODate"];
var poVendorName = document.properties["rwa:POVendorName"];
var poAmount = document.properties["rwa:POAmount"];
var createdDate = document.properties["cm:created"];
var filingDate;

if(poVendorName === "") throw "Vendor Name is required";

if(purchaseOrderNumber != ""){
//checkForDuplicates(poVendorName, purchaseOrderNumber)
}

var vendorNameFirstLetter = poVendorName.substring(0,1).toUpperCase();
if(purchaseOrderDate !== null){
filingDate = purchaseOrderDate.getFullYear();
}else{
filingDate = createdDate.getFullYear();
purchaseOrderDate = createdDate;
}

var filingLocation = ["Sites", "maintenance", "documentLibrary", "Purchase Orders", vendorNameFirstLetter, poVendorName, filingDate];

renameFile(document, poVendorName, purchaseOrderNumber, purchaseOrderDate, poAmount);
setProjectFile(document, filingLocation);


function checkForDuplicates(vendorName, purchaseOrderNumber, poAmount){
 var def =
 {
  query: "rwa:POVendorName:"+vendorName + " AND rwa:PurchaseOrderNumberType:" + purchaseOrderNumber,
  language: "fts-alfresco"
 };
 var results = search.query(def);
 
 if(results.length >= 2) throw "Duplicate Purchase Order Found";
}

function setProjectFile(document, filingLocation){

var arrayLength = filingLocation.length;
var destination = companyhome;
var location = companyhome;

for (var i =0; i < arrayLength; i++){
		destination = location.childByNamePath(filingLocation[i]);
		if (destination == null){
		   destination = location.createFolder(filingLocation[i]);
		}
		location = destination;
	}
	
	document.move(destination);
	document.save();
}


function renameFile(document, poVendorName, purchaseOrderNumber, purchaseOrderDate, poAmount){
  var fileExtension = "." + document.properties.name.split('.').pop();
	if(purchaseOrderNumber !== ""){
			document.properties.name = poVendorName + "_" + purchaseOrderNumber + fileExtension;
		}else if(purchaseOrderDate != null){
		   var curr_date = purchaseOrderDate.getDate();
           var curr_month = purchaseOrderDate.getMonth() + 1; //Months are zero based
           var curr_year = purchaseOrderDate.getFullYear();
		   document.properties.name = poVendorName + "_" + poAmount +"_"+ curr_month +"-" + curr_date + "-" + curr_year + fileExtension;
		}else{
			document.properties.name = poVendorName + fileExtension;
		}
		document.save();
}
function log(varString){
var logFile = companyhome.childByNamePath("log.txt");

if (logFile == null)
{
   logFile = companyhome.createFile("log.txt");
}
if (logFile != null)
   {
     logFile.content +=  varString +"\n";
   }
}