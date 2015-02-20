document.clearTags();
var legs = document.properties["charter:Legs"];
var legsAsJson = JSON.parse(legs, document);

for (var leg in legsAsJson){
	SetTag(legsAsJson[leg], document)
	}

function SetTag(legAsJson, document){
   
if(legAsJson.date + "" !== "undefined" && legAsJson.date +"" !== "" && legAsJson.date + "" !== "null"){
	  var legDateOriginal = JSON.stringify(legAsJson.date) + "" ;	  
		//document.addTag(legAsJson.date.substring(5, 7) + "-" + legAsJson.date.substring(8, 10));
		var filingLocation = ["Sites", "charter", "documentLibrary", "History", legAsJson.date.substring(0,4), legAsJson.date.substring(5, 7), legAsJson.date.substring(8, 10) ];
		setContainsLocation(document, filingLocation);
	}
}


function setContainsLocation(document, filingLocation){

var arrayLength = filingLocation.length;
var destination = companyhome;
var location = companyhome;

for (var i =0; i < arrayLength; i++){
		destination = location.childByNamePath(filingLocation[i]);
		if (destination == null){
		   destination = location.createFolder(filingLocation[i]);
		   destination.save();
		}
		location = destination;

	}
	destination.addNode(document);
	//destination.createAssociation(document, "cm:contains");
	document.save();

}

function clearAssociations(document){
	var documentAssociations = document.parentAssocs["cm:contains"];

	for(var parentAssociation in documentAssociations){
		document.removeAssociation(parentAssociation, "cm:contains") 
	}
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