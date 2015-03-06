document.clearTags();
clearAssociations(document);
var legs = document.properties["charter:Routing"];
var legsAsJson = JSON.parse(legs, document);

function clearAssociations(document){
		var primaryParent = document.parent;
		var parents = document.parents;
		for(var parent in parents){
			 if(parents[parent].nodeRef + "" !== primaryParent.nodeRef + ""){
			          parents[parent].removeAssociation(document, "cm:contains");
					  parents[parent].removeNode(document);
			 }
		}
}

for (var leg in legsAsJson){
	SetTag(legsAsJson[leg], document)
	}

function SetTag(legAsJson, document){
	if(legAsJson.date + "" !== "undefined" && legAsJson.date +"" !== "" && legAsJson.date + "" !== "null"){
		 var legDateOriginal = JSON.stringify(legAsJson.date) + "" ;	  
		document.addTag(legAsJson.date.substring(5, 7) + "-" + legAsJson.date.substring(8, 10) + "-" + legAsJson.date.substring(0, 4));
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
    destination.createAssociation(document, "cm:contains");
    destination.addNode(document);

	document.save();

}


	