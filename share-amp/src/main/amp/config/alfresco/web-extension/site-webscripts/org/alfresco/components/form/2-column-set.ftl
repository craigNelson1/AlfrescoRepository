<#if form.mode == "view">
<#list set.children as item>
   <#if item.kind != "set">
      <#if (item_index % 3) == 0>
      <div class="yui-gb"><div class="yui-u first" style="width: 33%">
      <#else>
      <div class="yui-u first" style="width: 10%">
      </#if>
      <@formLib.renderField field=form.fields[item.id] />
      </div>
      <#if ((item_index % 3) == 2) || !item_has_next></div></#if>
   </#if>
</#list>
<#else>

<style media="screen" type="text/css">
.form-container input{
width: 25em;
}

		</style>

<#list set.children as item>
      <div class="yui-g" style="width: 70%">
      <@formLib.renderField field=form.fields[item.id] />
      </div>
</#list>
</#if>
   <script type="text/javascript">//<![CDATA[
   if( document.getElementById("webpreviewID") != null){
    document.getElementById("webpreviewID").remove();
    document.getElementById("formManipulation").remove();
  	document.getElementById("MetaDataForm").removeAttribute("style");
  	document.getElementById("MetaDataForm").setAttribute("class", "share-form");
  	document.getElementById("MetaDataForm").setAttribute("style", "width: 90%");
  	
     }else{
      document.getElementById("template_x002e_folder-actions_x002e_folder-details").parentNode.setAttribute("style", "width: 100%");
		}
		   //]]></script>