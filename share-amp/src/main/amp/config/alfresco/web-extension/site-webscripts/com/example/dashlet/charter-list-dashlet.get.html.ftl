<script type="text/javascript">
   var charterList=new RWAD.CharterListDashlet.Event("${args.htmlid}").setMessages(${messages});
</script>
<div class="dashlet" id="${args.htmlid}">
    <div class="title">Charter List    </div>
    	<div style="padding:4%">
	    	Choose Datalist : <select id="${args.htmlid}--folderList">
	    	   <option value="" selected="selected"></option>
	    	</select><br/>
    	<div><br>
    	<div>
			<form  method="post" id="${args.htmlid}-uploadform">
	    		<div>File: <input type="file" name="file"><br><br/></div>
			    <div><input type="button" id="${args.htmlid}-button" value="Upload"></div><br/>
	  		</form>
  		</div>
</div> 			 

