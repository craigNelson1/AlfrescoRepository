	var routingProperties = document.properties["charter:Routing"];
	if(routingProperties != ""){
		var routingPropertiesOld = document.properties["charter:RoutingAudited"]; 	
		document.properties["charter:Routing"] = JSONSetAndCompare(routingProperties, routingPropertiesOld) + "";  
	}
	document.properties["charter:RoutingAudited"] = document.properties["charter:Routing"];
 
	var brokerInformation = document.properties["charter:BrokerInformation"];
	if(brokerInformation != ""){
		var brokerInformationOld = document.properties["charter:BrokerInformationAudited"];
		document.properties["charter:BrokerInformation"] = JSONSetAndCompare(brokerInformation, brokerInformationOld) + "";  
	}
	document.properties["charter:BrokerInformationAudited"] = document.properties["charter:BrokerInformation"];
  
    var legs = document.properties["charter:Legs"];
	if(legs != ""){
		var legsOld = document.properties["charter:LegsAudited"];
		document.properties["charter:Legs"] = JSONSetAndCompare(legs, legsOld) + "";  
	}
 	document.properties["charter:LegsAudited"] = document.properties["charter:Legs"];

	var itinerary = document.properties["charter:Itinerary"];
	if(itinerary != ""){
		var itineraryOld = document.properties["charter:ItineraryAudited"];
		document.properties["charter:Itinerary"] = JSONSetAndCompare(itinerary, itineraryOld) + "";  
	}
	document.properties["charter:ItineraryAudited"] = document.properties["charter:Itinerary"];
 
	var creditCardAuthorization = document.properties["charter:CreditCardAuthorization"]; 
	if(creditCardAuthorization != ""){
		var creditCardAuthorizationOld = document.properties["charter:CreditCardAuthorizationAudited"];
		document.properties["charter:CreditCardAuthorization"] = JSONSetAndCompare(creditCardAuthorization, creditCardAuthorizationOld) + "";  
	} 
	document.properties["charter:CreditCardAuthorizationAudited"] = document.properties["charter:CreditCardAuthorization"];
 
	var catering = document.properties["charter:Catering"]; 
	if(catering != ""){
		var cateringOld = document.properties["charter:CateringAudited"];
		document.properties["charter:Catering"] = JSONSetAndCompare(catering, cateringOld) + "";  
	}
	document.properties["charter:CateringAudited"] = document.properties["charter:Catering"];

	var additionalCharges = document.properties["charter:AdditionalCharges"];
	if(additionalCharges != ""){
		var additionalChargesOld = document.properties["charter:AdditionalChargesAudit"];
		document.properties["charter:AdditionalCharges"] = JSONSetAndCompare(additionalCharges, additionalChargesOld) + "";  
	}
	document.properties["charter:AdditionalChargesAudit"] = document.properties["charter:AdditionalCharges"];
 
	var notes = document.properties["charter:Notes"];
	if(notes != ""){
		var notesOld = document.properties["charter:NotesAudited"];
		document.properties["charter:Notes"] = JSONSetAndCompare(notes, notesOld) + "";  
	}
	document.properties["charter:NotesAudited"] = document.properties["charter:Notes"];
 
	document.save();
 
 function JSONSetAndCompare(jsonStringNew, jsonStringOld){
 if(jsonStringNew !== "undefined"  || jsonStringNew !== "[]" || jsonStringNew !== ""){
       if (!jsonStringNew || jsonStringNew == "" || jsonStringNew== "[]") 	   
		 var jsonStringNew = jsonStringNew +"";
		 var jsonStringOld = jsonStringOld + "";
		 
		  if(jsonStringNew + "" === "undefined" || jsonStringNew + ""  === "" || jsonStringNew + ""  === null){
		  jsonStringNew = "[]";
		 }
		 
		 if(jsonStringOld + "" === "undefined" || jsonStringOld + "" === "" || jsonStringOld + ""  === null){
		  jsonStringOld = "[]";
		 }
		 
		 try{
	     var jsonObjectNew = JSON.parse(jsonStringNew);
		 var jsonObjectOld = JSON.parse(jsonStringOld);
		 var newJson = CompareJSON(jsonObjectNew, jsonObjectOld);	
		 return JSON.stringify(newJson) + "";  
		 }catch(exception){
		  return "[]";
		 }

 } else {
		return "[]";
 }
 
 }
function CompareJSON(JSONNew, JSONAudited){
	for (var newJson in JSONNew){
		  if(JSON.stringify(JSONNew[newJson].key)+"" == "undefined"){
		  JSONNew[newJson].key = guid();
		  setModifiedBy(JSONNew[newJson]);
		} else  if(JSON.stringify(JSONNew[newJson].key)+"" == "null") {
		 var didNotFindMatch = false;
           for(var old in JSONAudited){
				if(JSON.stringify(JSONAudited[old]) +"" == JSON.stringify(JSONNew[newJson]) + ""){
					JSONNew[newJson].key = guid();
					didNotFindMatch = true;
				}
			  }
			  if(didNotFindMatch){
			   JSONNew[newJson].key = guid();
		        setModifiedBy(JSONNew[newJson]);
			  }
			}else{
	   for(var old in JSONAudited){
			if(JSONAudited[old].key === JSONNew[newJson].key){
				if(JSON.stringify(JSONAudited[old]) +"" != JSON.stringify(JSONNew[newJson]) + ""){
					 setModifiedBy(JSONNew[newJson]);

				}
			  }
			} 
		}
	  }
	  return JSONNew;
	}

	
function setModifiedBy(jsonArray){
    var d = new Date();
    var curr_date = d.getDate();
    var curr_month = d.getMonth() + 1; //Months are zero based
    var curr_year = d.getFullYear();
	jsonArray.modifiedBy = person.properties.userName + " " + curr_month  + "/" +  curr_date + "/" + curr_year + " " + formatAMPM(Date.now());
}

function guid() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
      .toString(16)
      .substring(1);
  }
  return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}
function formatTimeStamp(unixTimestamp) {
    var dt = new Date(unixTimestamp * 1000);

    var hours = dt.getHours();
    var minutes = dt.getMinutes();
    var seconds = dt.getSeconds();

    return hours + ":" + minutes + ":" + seconds;
}   

function formatAMPM(dateOriginal) {
  var date = new Date(dateOriginal);
  var hours = date.getHours();
  
  var minutes = date.getMinutes();
  if(minutes < 10){
   minutes = "0" + minutes;
  }
  var strTime = hours + ':' + minutes;
  return strTime;
}
    