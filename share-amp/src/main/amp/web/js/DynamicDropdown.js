(function(RWAD) {
	var Dom = YAHOO.util.Dom,
	Event = YAHOO.util.Event;
	var $html = Alfresco.util.encodeHTML;
	

	RWAD.DynamicDropdown = function(htmlId)
	{
		var componentName = "RWAD.DynamicDropdown";
		var returnObject = RWAD.DynamicDropdown.superclass.constructor.call(this, componentName, htmlId, "RWAD.DynamicDropdown"); 
		return returnObject;
	};
	
	YAHOO.extend(RWAD.DynamicDropdown, Alfresco.component.Base,
	{
		options : {
			siteName: "",
			dataListName : "",
			htmlId:"",
			fieldName:""
		},
		onReady : function() {
			
			console.log("Parmas are ");
			console.log(this.options.dataListName);
			console.log(this.options.fieldName);
			console.log(this.options.siteName);
			
			var selectEl = Dom.get(this.id);
			// success handler, populate the dropdown
            var onSuccess = function (response)
            {
            	console.log(response);
            	//clear selected item
            	selectEl.selectedIndex = -1;
            	
            	//clear the current dropdown options
            	if (response.json.result && response.json.result.picklist)
            	{
            		var picklist = response.json.result.picklist;
					for (var i=0; i<picklist.length;i++)
					{
						var optionElement = document.createElement("option");
						optionElement.innerHTML = picklist[i].label;
						optionElement.value = picklist[i].value;
						if (this.options.initialValue instanceof Array && this.options.initialValue.indexOf(picklist[i].value) !== -1)
						{
							optionElement.selected = "selected";
						}
						else if (this.options.initialValue === picklist[i].value)
						{
							optionElement.selected = "selected";
						}
						selectEl.appendChild(optionElement);
					}
            	}
            	
				YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
            };
			
            // failure handler, display alert
            var onFailure = function (response)
            {
            	//clear selected item
            	selectEl.selectedIndex = -1;
            	
            	//clear the current dropdown options
            	while (selectEl.hasChildNodes())
            	{
            		selectEl.removeChild(selectEl.lastChild);
            	}
               // hide the whole field so incorrect content does not get re-submitted
               this._hideField();
               
               if (Alfresco.logger.isDebugEnabled())
                  Alfresco.logger.debug("Hidden field '" + this.id + "' as content retrieval failed");
            };
			
         
			console.log(Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+this.options.siteName+"&datalist_name="+this.options.dataListName+"&fieldname="+this.options.fieldName);
			
            Alfresco.util.Ajax.request(
                    {
                       url: Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+this.options.siteName+"&datalist_name="+this.options.dataListName+"&field_name="+this.options.fieldName,
                       method: "GET",
                       responseContentType : "application/json",
                       successCallback:
                       {
                          fn: onSuccess,
                          scope: this
                       },
                       failureCallback:
                       {
                          fn: onFailure,
                          scope: this
                       }
                    });

		},
   });
	
})(window.RWAD = window.RWAD || {});