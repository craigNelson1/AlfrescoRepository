
var vendorId = document.properties["rwa:VendorId"];
var vendorName = document.properties["rwa:VendorName"];
var invoiceNumber = document.properties["rwa:InvoiceNumber"];

if(vendorId === "") throw "Vendor Id is required";
if(vendorName === "") throw "Vendor Name is required";
if(invoiceNumber === "") throw "Invoice Number is required";

checkForDuplicates(vendorId, vendorName, invoiceNumber);

var vendorNameFirstLetter = vendorName.substring(0,1).toUpperCase();
var filingLocation = ["Sites", "accounting", "documentLibrary", "Invoices", vendorNameFirstLetter, vendorName];

setProjectFile(document, filingLocation);


function checkForDuplicates(vendorId, vendorName, invoiceNumber){

 var def =
 {
  query: "rwa:VendorId:" + vendorId + " AND rwa:VendorName:"+vendorName + " AND rwa:InvoiceNumber:" + invoiceNumber,
  language: "fts-alfresco"
 };
 var results = search.query(def);
 
 if(results.length >= 2) throw "Duplicate Invoice Found";
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

