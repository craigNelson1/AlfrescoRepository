var filingLocation = ["Sites", "charter", "documentLibrary", "Completed"];

setProjectFile(document, filingLocation);


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
