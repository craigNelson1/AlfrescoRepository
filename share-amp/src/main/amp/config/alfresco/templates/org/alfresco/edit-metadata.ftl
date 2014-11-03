<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="document-details"/>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/document-details/document-details-panel.css" group="document-details"/>
   <@templateHtmlEditorAssets />
</@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <div class="share-form">
        <div class="yui-gc">
                        <@region id="edit-metadata-mgr" scope="template" />
          
          <div class="yui-u first" style="width: 60%; height: 70%; position: fixed;">
          <div>
               <#if (config.scoped['DocumentDetails']['document-details'].getChildValue('display-web-preview') == "true")>
                        <@region id="web-preview" scope="template" protected=true />
                </#if>
                </div>
          </div>
          <style media="screen" type="text/css">
			.share-form .form-container .form-fields
			{
			   width: 80%;
			}
			.share-form .form-container .caption{
			text-align: left;
			}
			.form-container input, .form-container input[type="file"], .form-container input[type="text"], .form-container input[type="password"] 
			{
				width: 80%;
              }
            .alfresco-share .sticky-wrapper select, .yui-overlay select, .alfresco-share .sticky-wrapper input, .yui-overlay input, .alfresco-share .sticky-wrapper textarea, .yui-overlay textarea, .alfresco-share .sticky-wrapper button, .yui-overlay button{
              	width: 80%;   
              }
		</style>
          <div class="yui-u" style="width: 38%">
               <div>
               <@region id="edit-metadata" scope="template"/>
               </div>
             </div>
          </div>
       </div>
   </div>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
