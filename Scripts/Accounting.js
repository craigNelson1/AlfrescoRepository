
var vendorId = document.properties["rwa:VendorId"];
var vendorName = document.properties["rwa:VendorName"];
var invoiceNumber = document.properties["rwa:InvoiceNumber"];
var invoiceDate = document.properties["rwa:StatementDate"];
var createdDate = document.properties["cm:created"];
var filingDate;

if(vendorId === "") throw "Vendor Id is required";
if(vendorName === "") throw "Vendor Name is required";
if(invoiceNumber === "") throw "Invoice Number is required";
checkForDuplicates(vendorId, vendorName, invoiceNumber);

var vendorNameFirstLetter = vendorName.substring(0,1).toUpperCase();
if(invoiceDate !== null){
filingDate = invoiceDate.getFullYear();
}else{
filingDate = createdDate.getFullYear();

}
var filingLocation = ["Sites", "accounting", "documentLibrary", "Invoices", vendorNameFirstLetter, vendorName, filingDate];

renameFile(document, invoiceNumber, invoiceDate);
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


function renameFile(document, invoiceNumber, invoiceDate){
  var fileExtension = "." + document.properties.name.split('.').pop();
	if(invoiceNumber != null){
			document.properties.name = vendorName + "_" + invoiceNumber + fileExtension;
		}else if(invoiceDate != null){
		   var curr_date = invoiceDate.getDate();
           var curr_month = invoiceDate.getMonth() + 1; //Months are zero based
           var curr_year = invoiceDate.getFullYear();
		   document.properties.name = vendorName + "_" + curr_month +"-" + curr_date + "-" + curr_year + fileExtension;
		}else{
			document.properties.name = vendorName + fileExtension;
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