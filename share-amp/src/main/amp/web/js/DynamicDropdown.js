if (typeof RWAD== "undefined" || !RWAD)
{
   var RWAD = {};
}

/**
 * SA FileUpload Namspace.
 * 
 * @namespace SA.fileupload
 */
if (typeof RWAD.DynamicDropdown== "undefined" || !RWAD.DynamicDropdown)
{
	RWAD.DynamicDropdown= {};
}

(function()
{
   /**
	 * YUI Library aliases
	 */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
   var  fileUpload;
   var dndUpload;
   /**
	 * Alfresco Slingshot aliases
	 */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;
   /**
	 * Dashboard constructor.
	 * 
	 * @param {String}
	 *            htmlId The HTML id of the parent element
	 * @return {SA.FileUpload.Event} The new component instance
	 * @constructor
	 */
   RWAD.DynamicDropdown.Event = function DynamicDropdown_constructor(htmlId)
   {
	   if(Alfresco.util.ComponentManager.findFirst("RWAD.DynamicDropdown.Event")!=null)
	   {   
		   var obj=Alfresco.util.ComponentManager.findFirst("RWAD.DynamicDropdown.Event");
		   obj.onReady();
		   return Alfresco.util.ComponentManager.findFirst("RWAD.DynamicDropdown.Event");
	   }
      return RWAD.DynamicDropdown.Event.superclass.constructor.call(this, "RWAD.DynamicDropdown.Event", htmlId);
   };

   /**
	 * Extend from Alfresco.component.Base and add class implementation
	 */
   YAHOO.extend(RWAD.DynamicDropdown.Event, Alfresco.component.Base,
   {
      /**
		 * Object container for initialization options
		 * 
		 * @property options
		 * @type object
		 */
	   
      options:
      {
          name:"",
          site:"",
          datalist:"",
          vendorName:"",
          vendotId:""
      },
      /**
		 * Fired by YUI when parent element is available for scripting
		 * 
		 * @method onReady
		 */
      onReady: function DynamicDropdown_onReady()
      {
		   if(this.options.name=="datalist")
		   {
		    	  YAHOO.util.Event.addListener(this.id.substr(0,this.id.length-9)+"datalist-datalist", "change", this.onSelectDatalist);
		    	  this.options.datalist=Dom.get(this.id.substr(0,this.id.length-9)+"datalist-datalist");
		    	  return;
		   }
		   if(this.options.name=="vendorId")
		   {
		    	  this.options.vendorId=Dom.get(this.id.substr(0,this.id.length-9)+"VendorId-vendorId");
		    	  return;
		   }
		   if(this.options.name=="vendorName")
		   {
		    	  this.options.vendorName=Dom.get(this.id.substr(0,this.id.length-9)+"VendorName-vendorName");
		    	  return;
		   }

    	  YAHOO.util.Event.addListener(this.id, "change", this.onSelectSite);
    	  this.options.site=Dom.get(this.id);
    	  this.onRenderSiteList();
    	  
      },
      onSelectSite: function DynamicDropdown_onSelectSite()
      {
    		var selectEl = Dom.get(dynamicDropdown.options.datalist);
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
			
          Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+dynamicDropdown.options.site.value+"&datalist_name="+dynamicDropdown.options.datalist.value,
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
      onSelectDatalist: function DynamicDropdown_onSelectDataList()
      {      
    	  console.log(this.value)
    	  dynamicDropdown.onRenderVendorId();
    	  dynamicDropdown.onRenderVendorName();
      },
      onRenderSiteList: function DynamicDropdown_onRenderSiteList()
      {

			var selectEl = Dom.get(this.options.site);
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
			
          Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+this.options.site.value+"&datalist_name="+this.options.datalist.value,
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
      onRenderVendorId: function DynamicDropdown_onRenderVendorId()
      {

    		var selectEl= Dom.get(dynamicDropdown.options.vendorId);
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
  		
        Alfresco.util.Ajax.request(
                {
                   url: Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+dynamicDropdown.options.site.value+"&datalist_name="+dynamicDropdown.options.datalist.value+"&field_name=VendorId",
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
      onRenderVendorName: function DynamicDropdown_onRenderVendorName()
      {

    	var selectEl= Dom.get(dynamicDropdown.options.vendorName);
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
  		
        Alfresco.util.Ajax.request(
                {
                   url: Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+dynamicDropdown.options.site.value+"&datalist_name="+dynamicDropdown.options.datalist.value+"&field_name=VendorName",
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

      }
   });
   })();