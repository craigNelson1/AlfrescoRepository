<#if form.mode == "view">
<#list set.children as item>
      <div class="viewmode-field">
      <@formLib.renderField field=form.fields[item.id] />
      </div>
</#list>
<#else>

<style media="screen" type="text/css">
.form-container input{
width: 25em;
}

.share-form .form-container .form-fields {
border: 0px solid #fff !important;
background-color: #fff;
margin-left: 10px;
width: 1000px;
}

		</style>

<#list set.children as item>
      <div class="share-form">
      <@formLib.renderField field=form.fields[item.id] />
      </div>
</#list>

</#if>
   <script type="text/javascript">

   if( document.getElementById("webpreviewID") != null){
    document.getElementById("webpreviewID").remove();
    document.getElementById("formManipulation").remove();
  	document.getElementById("MetaDataForm").removeAttribute("style");
  	document.getElementById("MetaDataForm").setAttribute("class", "share-form");
  	document.getElementById("MetaDataForm").setAttribute("style", "width: 90%");
  	
     }else{
 window.a =  document.getElementById("template_x002e_folder-actions_x002e_folder-details").parentNode.setAttribute("style", "width: 100%; border: none");
		}
	
		</script>