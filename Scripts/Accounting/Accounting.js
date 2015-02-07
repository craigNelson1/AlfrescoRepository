
var vendorName = document.properties["rwa:VendorName"];
var invoiceNumber = document.properties["rwa:InvoiceNumber"];
var invoiceDate = document.properties["rwa:InvoiceDate"];
var createdDate = document.properties["cm:created"];
var filingDate;

if(vendorName === "") throw "Vendor Name is required";
if(invoiceNumber === "") throw "Invoice Number is required";
//checkForDuplicates(vendorId, vendorName, invoiceNumber);

var vendorNameFirstLetter = vendorName.substring(0,1).toUpperCase();
if(invoiceDate !== null){
filingDate = invoiceDate.getFullYear();
}else{
filingDate = createdDate.getFullYear();

}
var filingLocation = ["Sites", "accounting", "documentLibrary", "Invoices", vendorNameFirstLetter, vendorName, filingDate];

setProjectFile(document, filingLocation);


function checkForDuplicates(vendorId, vendorName, invoiceNumber){

 var def =
 {
  query: "rwa:VendorName:"+vendorName + " AND rwa:InvoiceNumber:" + invoiceNumber,
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