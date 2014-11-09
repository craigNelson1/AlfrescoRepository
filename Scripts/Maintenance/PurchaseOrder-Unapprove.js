var vendorName = document.properties["rwapo:POVendorName"];
var purchaseOrderNumber = document.properties["rwapo:PurchaseOrderNumberType"];

if(vendorName === "") throw "Vendor Name is required";

checkForDuplicates(vendorName, purchaseOrderNumber);

var filingLocation = ["Sites", "maintenance", "documentLibrary", "Inbox - Purchase Orders"];

setProjectFile(document, filingLocation);

function checkForDuplicates(vendorId, vendorName, purchaseOrderNumber){

 var def =
 {
  query: "rwapo:POVendorName:"+vendorName + " AND rwapo:PurchaseOrderNumberType:" + purchaseOrderNumber,
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