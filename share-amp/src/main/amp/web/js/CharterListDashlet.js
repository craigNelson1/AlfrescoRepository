if (typeof RWAD== "undefined" || !RWAD)
{
   var RWAD = {};
}

/**
 * SA FileUpload Namspace.
 * 
 * @namespace SA.fileupload
 */
if (typeof RWAD.CharterListDashlet== "undefined" || !RWAD.CharterListDashlet)
{
	RWAD.CharterListDashlet= {};
	
}

(function()
{
   /**
	 * YUI Library aliases
	 */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
   /**
	 * Alfresco Slingshot aliases
	 */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,form;
   /**
	 * Dashboard constructor.
	 * 
	 * @param {String}
	 *            htmlId The HTML id of the parent element
	 * @return {SA.FileUpload.Event} The new component instance
	 * @constructor
	 */
   RWAD.CharterListDashlet.Event = function CharterListDashlet_constructor(htmlId)
   {
      return RWAD.CharterListDashlet.Event.superclass.constructor.call(this, "RWAD.CharterListDashlet.Event", htmlId);
   };

   /**
	 * Extend from Alfresco.component.Base and add class implementation
	 */
   YAHOO.extend(RWAD.CharterListDashlet.Event, Alfresco.component.Base,
   {
      /**
		 * Object container for initialization options
		 * 
		 * @property options
		 * @type object
		 */
	   
      options:
      {
    	  importButton:"",
    	  uploadFile:"",
    	  uploadButton:"",
    	  uploadForm:""
    		  
      },
      /**
		 * Fired by YUI when parent element is available for scripting
		 * 
		 * @method onReady
		 */
      onReady: function CharterListDashlet_onReady()
      {
    	  console.log("ready");
    	  this.options.importButton=Alfresco.util.createYUIButton(this, "import-button", this.onUploadUsersClick);    	  
    	  this.options.uploadFile=Dom.get(this.id+"-uploadfile");
    	  form=Dom.get(this.id+"-uploadform");
    	  this.options.uploadButton=Dom.get(this.id+"-button");
    	  this.options.datalist=Dom.get(this.id+"-datalist");
    	  YAHOO.util.Event.addListener(this.options.uploadButton, "click", this.onUploadUsersClick);
    	  this.onLoadDatalist();
      },
      onUploadUsersClick: function ConsoleUsers_onUploadUsersClick(e, args)
      {
    	  console.log("uploading form");
    	  var callback = { 
    			  upload: function(o){
    				  alert(o.responseText);}   
    	  }; 
    	  console.log(form);
    	  YAHOO.util.Connect.setForm(form,true);
    	  console.log("https://docs.redwingaero.com/alfresco/service/com/redwingaero/import/charterList?site="+Alfresco.constants.SITE+"&datalistName="+Dom.get(Alfresco.util.ComponentManager.findFirst("RWAD.CharterListDashlet.Event").id+"-datalist").value);
    	  var request = YAHOO.util.Connect.asyncRequest("POST","https://docs.redwingaero.com/alfresco/service/com/redwingaero/import/charterList?site="+Alfresco.constants.SITE+"&datalist="+Dom.get(Alfresco.util.ComponentManager.findFirst("RWAD.CharterListDashlet.Event").id+"-datalist").value, callback); 
      },
      onLoadDatalist: function DynamicDropdown_onOnLoadDataList()
      {
    		var selectEl = this.options.datalist;
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
                     url: Alfresco.constants.PROXY_URI + 'com/redwingaero/picklist/picklist?site_name='+Alfresco.constants.SITE,
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