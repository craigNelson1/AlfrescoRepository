<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#if field.control.params.optionSeparator??>
<#assign optionSeparator=field.control.params.optionSeparator>
<#else>
<#assign optionSeparator=",">
</#if>
<#if field.control.params.labelSeparator??>
<#assign labelSeparator=field.control.params.labelSeparator>
<#else>
<#assign labelSeparator="|">
</#if>

<#assign fieldValue=field.value>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
<#if context.properties[field.control.params.defaultValueContextProperty]??>
   <#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
<#elseif args[field.control.params.defaultValueContextProperty]??>
   <#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
</#if>
</#if>

<script type="text/javascript">//<![CDATA[
(function(RWAD)
{
	<#if form.mode == "view">
		<#if field.control.params['loadLabel']??>
			new RWAD.LoadLabel("${fieldHtmlId}-value").setOptions(
			{
				picklistName:"${field.control.params['picklistName']}",
				initialValue:"${fieldValue}",
				itemId:"${(form.arguments.itemId!"")?js_string}"
			});
		</#if>
	<#else>
		new RWAD.DynamicDropdown.Event("${fieldHtmlId}").setOptions(
		{
			siteName:"${field.control.params['siteName']}",
			dataListName:"${field.control.params['dataListName']}",
			htmlId:"${fieldHtmlId}",
			fieldName:"${field.control.params['fieldname']}"
		});
	</#if>

})(window.RWAD = window.RWAD || {});
//]]></script>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if fieldValue?string == "">
            <#assign valueToShow=msg("form.control.novalue")>
         <#else>
            <#assign valueToShow=fieldValue>
            <#if field.control.params.options?? && field.control.params.options != "">
               <#list field.control.params.options?split(optionSeparator) as nameValue>
                  <#if nameValue?index_of(labelSeparator) == -1>
                     <#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)>
                        <#assign valueToShow=nameValue>
                        <#break>
                     </#if>
                  <#else>
                     <#assign choice=nameValue?split(labelSeparator)>
                     <#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])>
                        <#assign valueToShow=msgValue(choice[1])>
                        <#break>
                     </#if>
                  </#if>
               </#list>
            </#if>
         </#if>
         <span id="${fieldHtmlId}-value" class="viewmode-value">${valueToShow?html}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	     <select id="${fieldHtmlId}" name="${field.name}" tabindex="0"
	           <#if field.description??>title="${field.description}"</#if>
	           <#if field.control.params.size??>size="${field.control.params.size}"</#if> 
	           <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
	           <#if field.control.params.style??>style="${field.control.params.style}"</#if>
	           <#if field.disabled  && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>
	           <#if !field.control.params['loadLabel']?? >
	               <option value="${fieldValue?html}" selected="selected">${fieldValue?html}</option>
	           </#if>
	           <#--
	           <#list field.control.params.options?split(optionSeparator) as nameValue>
	              <#if nameValue?index_of(labelSeparator) == -1>
	                 <option value="${nameValue?html}"<#if nameValue == fieldValue?string || (fieldValue?is_number && fieldValue?c == nameValue)> selected="selected"</#if>>${nameValue?html}</option>
	              <#else>
	                 <#assign choice=nameValue?split(labelSeparator)>
	                 <option value="${choice[0]?html}"<#if choice[0] == fieldValue?string || (fieldValue?is_number && fieldValue?c == choice[0])> selected="selected"</#if>>${msgValue(choice[1])?html}</option>
	              </#if>
	           </#list>
	           -->
	     </select>
	     <@formLib.renderFieldHelp field=field />
   </#if>
</div>