if (typeof CHART== "undefined" || !CHART)
{
   var CHART = {};
}


if (typeof RWAD.CharterExportListDashlet== "undefined" || !RWAD.CharterExportListDashlet)
{
	CHART.CharterExportListDashlet= {};
	
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
		   CHART.CharterExportListDashlet.Event = function CharterExportListDashlet_constructor(htmlId)
		   {
		      return CHART.CharterExportListDashlet.Event.superclass.constructor.call(this, "CHART.CharterExportListDashlet.Event", htmlId);
		   };

		   /**
			 * Extend from Alfresco.component.Base and add class implementation
			 */
		   YAHOO.extend(CHART.CharterExportListDashlet.Event, Alfresco.component.Base,
		   {
		      /**
				 * Object container for initialization options
				 * 
				 * @property options
				 * @type object
				 */
			   
		      options:
		      {
		    	  downloadZip:"",
		    	  downloadButton:"",		    		  
		      },
		      /**
				 * Fired by YUI when parent element is available for scripting
				 * 
				 * @method onReady
				 */
		      onReady: function CharterExportListDashlet_onReady()
		      {
		    	  console.log("ready");
		    	  this.options.downloadButton=Alfresco.util.createYUIButton(this, this.id+"-button", this.onDownloadUserClick);    	  
		    	  this.options.downloadZip=Dom.get(this.id+"-uploadfile");
		    	  this.options.downloadButton=Dom.get(this.id+"-button");

		    	  YAHOO.util.Event.addListener(this.options.downloadButton, "click", this.onDownloadUserClick);
		    	  this.options.folderList=Dom.get(this.id+"-folderList");
		    	  this.onLoadList();
		      },
		      onDownloadUserClick: function ConsoleUsers_onDownloadUserClick(e, args)
		      {
		    	  console.log("Downloading Item");
		    	  var callback = { 
		    			  upload: function(o){
		    				  alert(o.responseText);}   
		    	  }; 
		    	  console.log(form);
		    	 // YAHOO.util.Connect.setForm(form,true);
		    	  var urlValue = "https://docs.redwingaero.com/alfresco/service/com/redwingaero/charterstatusinfo/streamcharterstatusinfo?path="+Dom.get(Alfresco.util.ComponentManager.findFirst("CHART.CharterExportListDashlet.Event").id+"-folderList").value;
		    	  console.log("https://docs.redwingaero.com/alfresco/service/com/redwingaero/charterstatusinfo/streamcharterstatusinfo?path="+Dom.get(Alfresco.util.ComponentManager.findFirst("CHART.CharterExportListDashlet.Event").id+"-folderList").value);
		    	  window.open(urlValue);  	    
		      },
		      onLoadList: function DynamicDropdown_onOnLoadDataList()
		      {
		    		var selectEl = this.options.folderList;
					var onSuccess = function (response)
		           {
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
		                     url: Alfresco.constants.PROXY_URI + 'com/redwingaero/charter/export/picklist',
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