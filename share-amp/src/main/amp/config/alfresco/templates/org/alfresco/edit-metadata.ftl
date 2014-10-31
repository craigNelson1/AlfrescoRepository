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
          
          <div class="yui-u first" style="width: 50%">
          <div>
               <#if (config.scoped['DocumentDetails']['document-details'].getChildValue('display-web-preview') == "true")>
                        <@region id="web-preview" scope="template" protected=true />
                </#if>
                </div>
          </div>
          <div class="yui-u" style="width: 48%">
               <div>
               <@region id="edit-metadata" scope="template" />
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
