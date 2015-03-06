<script type="text/javascript">
   var charterList=new CHART.CharterExportListDashlet.Event("${args.htmlid}").setMessages(${messages});
</script>
<div class="dashlet" id="${args.htmlid}">
    <div class="title">Export Charter Informaiton</div>
    	<div style="padding:4%">
	    	Choose Folder To Backup : <select id="${args.htmlid}-folderList">
	    	   <option value="" selected="selected"></option>
	    	</select><br/>
    	<div><br>
    	<div>
			<form  method="post" id="${args.htmlid}-getRequest">
			    <div><input type="button" id="${args.htmlid}-button" value="Download"></div><br/>
	  		</form>
  		</div>
</div> 			 

