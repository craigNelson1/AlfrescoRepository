<#if field.control.params.config?exists><#assign dtConfig = field.control.params.config><#else><#assign dtConfig = "false"></#if>
<#if field.control.params.tredebug?exists> <#assign debug = field.control.params.debug><#else>    <#assign debug = "true">   </#if>
<#if field.control.params.propertyName?exists> <#assign propertyName = field.control.params.propertyName><#else>    <#assign propertyName = "DTP">   </#if>
<#if field??>

<div class="form-field">
	<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
	<div id="${propertyName}dtContainer">
		<div class="" id="${propertyName}dtp-dt"></div>
	</div>
	<textarea id="${fieldHtmlId}" name="${field.name}"  rows="5" cols="250" 
		<#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>
		<#if debug == "false">style="visibility:hidden; height:0px;"<#else>type="text"</#if>
	 >${field.value} 
	</textarea>
</div>
</#if>


<script type="text/javascript">//<![CDATA[

if (typeof(${propertyName}) == "undefined") { var ${propertyName} = {}; }

 ${propertyName} = {
	jsonProp : null,
	columnDefinitions : new Array(),
	readOnly : false,
	label : "Datatable property",
	
	${propertyName}Init: function (fieldHtmlId, config, view, label) {
		this.${propertyName}dtConfig = config;
		this.label = label;
		if ( view == "view") { this.readOnly = true; }
		this.jsonProp = YAHOO.util.Dom.get(fieldHtmlId);
		
	    var jsonString = "${field.value?js_string}";
	    
		${propertyName}.drawDatatable( jsonString.trim() );	
		
		},
		
	drawDatatable: function (jsonString) {
		if (!jsonString || jsonString == "" || jsonString== "[]") jsonString = "{}";
		
		var jsonData = eval ("(" + jsonString + ")"); //TODO, eval is AlfrescoShare's way, probably best to replace with JSON.parse

		${propertyName}.getTableDefinition();

		/* datatable columns are retreived based on table definition */
		var getMyKeys = function() {
			var columnsList = [];
			for(var c in ${propertyName}.columnDefinitions ) {
				if (${propertyName}.columnDefinitions[c].type == "date") {
					columnsList.push({key : ${propertyName}.columnDefinitions[c].key, parser: "date"});
				} else {
					columnsList.push(${propertyName}.columnDefinitions[c].key);
				}
			}
			return columnsList; 
		};

        
		var jsonSource = new YAHOO.util.DataSource(jsonData);
		jsonSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
		jsonSource.responseSchema = { fields: getMyKeys() };
		
		this.dtpDatatable = new YAHOO.widget.DataTable("${propertyName}dtp-dt", ${propertyName}.columnDefinitions, jsonSource);

		${propertyName}.datatableEvents();
	},
	
	/*
		adds events to datatable, add row, remove row, highlight, open edit cell...
	*/
	datatableEvents: function(){ 

		if (!${propertyName}.readOnly) {
			/* create "add" button */
			//get last th cell, and attach 'add' event
			var lastCellNo = ${propertyName}.dtpDatatable.getTheadEl().rows[0].cells.length-1;
			this.newRowButton = ${propertyName}.dtpDatatable.getTheadEl().rows[0].cells[lastCellNo];
			var addButton = document.createElement('span');
			addButton.setAttribute("style", "font-size: 100%");
			//TODO: Find better way to center this, i know this is horrible so please don't tell me how awful of a job or programmer i am.
			addButton.innerHTML  = '<span style=\"visibility:hidden\">00</span>+<span style=\"visibility:hidden\">00</span>';
			addButton.title = 'Add new row';
			addButton.className = "addButton";
			YAHOO.util.Event.addListener(this.newRowButton,'click',function(e){
				var rowData = { delete: "-" };
				var record = YAHOO.widget.DataTable._cloneObject(rowData);
				${propertyName}.dtpDatatable.addRow(record);
			});
			this.newRowButton.appendChild(addButton);
			/* add "-" to each delete column */
			var elementsByClassName = YAHOO.util.Dom.getElementsByClassName('delButton', 'td');
		    for (e in elementsByClassName) { elementsByClassName[e].innerHTML = "<div class='yui-dt-liner'>-</div>"; }
		}

		this.highlightEditableCell = function (oArgs) {
			var elCell = oArgs.target;
			if ( YAHOO.util.Dom.hasClass(elCell, "yui-dt-editable") ) {
				this.highlightCell(elCell);
			} 
		};

		${propertyName}.dtpDatatable.subscribe("cellMouseoverEvent", this.highlightEditableCell);
		${propertyName}.dtpDatatable.subscribe("cellMouseoutEvent", ${propertyName}.dtpDatatable.onEventUnhighlightCell);

		/* save, update json string after save event */
		${propertyName}.dtpDatatable.subscribe("editorSaveEvent",
		 function (oArgs) { 
		${propertyName}.dtToJson(); 
		
		});

		/* delete row event, or, by default, open cell to edit */
		${propertyName}.dtpDatatable.subscribe('cellClickEvent',function (oArgs) {
			var target = oArgs.target;
			var column = ${propertyName}.dtpDatatable.getColumn(target);
			if (column.key == 'delete') ${propertyName}.handleConfirm(target);
			else ${propertyName}.dtpDatatable.onEventShowCellEditor(oArgs);
		});

		/* if in read mode, popup datatable on click */
		if (${propertyName}.readOnly) { 
			${propertyName}.dtpDatatable.subscribe('cellClickEvent', function(){ ${propertyName}.popupDT(); } );
			${propertyName}.tooltip = new YAHOO.widget.Tooltip("dtp-tooltip", { context:"${propertyName}dtp-dt", text:"Click on datagrid to popup!" });
		}
		},
		getTableDefinition: function() {
		var getFormatter = function (t) {
			switch (t) {
				case "currency"	: return YAHOO.widget.DataTable.formatCurrency; break;
				case "number"	: return YAHOO.widget.DataTable.formatNumber({numberOptions: {decimalPlaces: 1, decimalSeparator: '.', format: '{prefix}{number} [kn]'}}); break;
				case "date"	: return YAHOO.widget.DataTable.formatDate(new Date().format("yyyy-MM-dd")); break;
				default 	: return YAHOO.widget.DataTable.formatText; break;
				
			}
			return t;
		};

		var getEditor = function (t, options) {
			if (${propertyName}.readOnly == true) return null; 
			switch(t){
				case "currency"	: return new YAHOO.widget.TextboxCellEditor(); 	break;
				case "number"	: return new YAHOO.widget.TextboxCellEditor(); 	break;
				case "textArea" : return new YAHOO.widget.TextareaCellEditor(); break;
				case "date"	: return new YAHOO.widget.DateCellEditor();  	break;
				case "radio"	: return new YAHOO.widget.RadioCellEditor({radioOptions: options ,disableBtns : true}); break;
				case "checkbox"	: return new YAHOO.widget.CheckboxCellEditor({checkboxOptions: options}); break;
				case "dropdown"	: return new YAHOO.widget.DropdownCellEditor({multiple: false, dropdownOptions: options}); break;
				default 	: return new YAHOO.widget.TextboxCellEditor(); 	break;
			}
		}

		${propertyName}.${propertyName}dtConfig = ${propertyName}DTPtrim( ${propertyName}.${propertyName}dtConfig, ["&quot;"]); /* handles quotes if there's any */

		definition = ${propertyName}.${propertyName}dtConfig.split(";")

		for (var e = 0; e < definition.length; e++)
		{
			var columns = definition[e].split(",");
			if (columns && columns.length == 3) definition[e] = [ ${propertyName}DTPtrim(columns[0]), ${propertyName}DTPtrim(columns[1]), ${propertyName}DTPtrim(columns[2]), null ];
			else if (columns && columns.length > 3){
				var l = [];
				for (i = 3; i < columns.length; i++) l.push(${propertyName}DTPtrim(columns[i], ["'", "[", "]"]));
				definition[e] = [ ${propertyName}DTPtrim(columns[0]), ${propertyName}DTPtrim(columns[1]), ${propertyName}DTPtrim(columns[2]), l ];
			}
			else { // alert("Wrong datatable definition!" + (columns[0] ? " (at column)" + columns[0] : "" )); 
			}
		}

		function ${propertyName}DTPtrim(s, toRemove){ 
		if(s != null){
		for (var t in toRemove)
		 while (s.indexOf(toRemove[t]) != -1) { 
		 s = s.replace(toRemove[t],""); } 
		 }
		 return YAHOO.lang.trim(s);
		 
		 
		  }


		for (var c in definition)
		  if (definition[c][0] && definition[c][0] != "") this.columnDefinitions.push({
			"key" : definition[c][0], "label" : definition[c][1], "type" : definition[c][2], formatter: getFormatter(definition[c][2]), sortable: true, resizable: true, editor: getEditor(definition[c][2], definition[c][3])
		  });

		if (!this.readOnly) this.columnDefinitions.push({key:"delete", label:" ", className:"delButton"});
	},
	
/*
		confirm dialog when deleting row
	*/
	handleConfirm: function (toDelete) {

		var handleYes = function(){
			this.hide(); ${propertyName}.dtpDatatable.deleteRow(toDelete); ${propertyName}.dtToJson(); return true;
		};
		var handleNo = function(){
			this.hide(); return false;
		};

		var confirmDialog = new YAHOO.widget.SimpleDialog('DTPconfirmDialog',{
				width: '300px',
				fixedcenter: true,
				visible: false,
				draggable: false,
				close: true,
				text: "Are you sure you want to delete this row?",
				constraintoviewport: true,
				buttons: [{text:'Yes',handler:handleYes, isDefault:true },{text:'No',handler:handleNo}]
		});

		confirmDialog.setHeader('Delete row');
		confirmDialog.render(document.body);
		confirmDialog.show();
		
	},

	/*
		brings datatable in popup dialog
	*/
	popupDT: function(){

		var handleClose = function(){
			YAHOO.util.Dom.get("${propertyName}dtContainer").appendChild(YAHOO.util.Dom.get("${propertyName}dtp-dt"));
			${propertyName}.dtpDatatable.subscribe('cellClickEvent', function(){ ${propertyName}.popupDT(); } );
			this.hide(); 
			return true;
		};

		var confirmDialog = new YAHOO.widget.SimpleDialog('dtDialog', {
				fixedcenter: true,
				visible: false,
				draggable: true,
				close: false,
				constraintoviewport: true,
				modal:true,
				buttons: [{text:'Close',handler:handleClose, isDefault:true }]
		});

		${propertyName}.dtpDatatable.unsubscribe('cellClickEvent');
		confirmDialog.setHeader(${propertyName}.label);
		confirmDialog.setBody(YAHOO.util.Dom.get("${propertyName}dtp-dt"));
		confirmDialog.render(document.body);
		confirmDialog.show();
		
	},

	/*
		gets table definition from what was provided in XML configuration. Parses parameter string to get definition.
		Sets-up formattters and editors according to column definition.
	*/
	getTableDefinition: function() {
		var getFormatter = function (t) {
			switch (t) {
				case "currency"	: return YAHOO.widget.DataTable.formatCurrency; break;
				case "number"	: return YAHOO.widget.DataTable.formatNumber; break;
				case "date"	: return YAHOO.widget.DataTable.formatDate(new Date().format("yyyy-MM-dd")); break;
				default 	: return YAHOO.widget.DataTable.formatText; break;
			}
			return t;
		};

		var getEditor = function (t, options) {
			if (${propertyName}.readOnly == true) return null; 
			switch(t){
				case "currency"	: return new YAHOO.widget.TextboxCellEditor(); 	break;
				case "number"	: return new YAHOO.widget.TextboxCellEditor(); 	break;
				case "textArea" : return new YAHOO.widget.TextareaCellEditor(); break;					
				case "date"	: return new YAHOO.widget.DateCellEditor();  	break;
				case "radio"	: return new YAHOO.widget.RadioCellEditor({radioOptions: options ,disableBtns : true}); break;
				case "checkbox"	: return new YAHOO.widget.CheckboxCellEditor({checkboxOptions: options}); break;
				case "dropdown"	: return new YAHOO.widget.DropdownCellEditor({multiple: false, dropdownOptions: options}); break;
				case "readOnly" : return null;
				default 	: return new YAHOO.widget.TextboxCellEditor(); 	break;
			}
		}

		${propertyName}.${propertyName}dtConfig = ${propertyName}DTPtrim( ${propertyName}.${propertyName}dtConfig, ["&quot;"]); /* handles quotes if there's any */

		definition = ${propertyName}.${propertyName}dtConfig.split(";")

		for (var e = 0; e < definition.length; e++)
		{
			var columns = definition[e].split(",");
			if (columns && columns.length == 3) definition[e] = [ ${propertyName}DTPtrim(columns[0]), ${propertyName}DTPtrim(columns[1]), ${propertyName}DTPtrim(columns[2]), null ];
			else if (columns && columns.length > 3){
				var l = [];
				for (i = 3; i < columns.length; i++) l.push(${propertyName}DTPtrim(columns[i], ["'", "[", "]"]));
				definition[e] = [ ${propertyName}DTPtrim(columns[0]), ${propertyName}DTPtrim(columns[1]), ${propertyName}DTPtrim(columns[2]), l ];
			}
			else {  
			//alert("Wrong datatable definition!" + (columns[0] ? " (at column)" + columns[0] : "" )); 
			}
		}

		function ${propertyName}DTPtrim(s, toRemove){ 
		if(s != null){
		for (var t in toRemove)
		 while (s.indexOf(toRemove[t]) != -1) { 
		 s = s.replace(toRemove[t],""); } 
		 }
		 return YAHOO.lang.trim(s);
		  }

		for (var c in definition)
		  if (definition[c][0] && definition[c][0] != "") {
		  		         if(definition[c][2].toString() != "hidden"){
				  this.columnDefinitions.push({
					"key" : definition[c][0], "label" : definition[c][1], "type" : definition[c][2], formatter: getFormatter(definition[c][2]), sortable: true, resizable: true, editor: getEditor(definition[c][2], definition[c][3])
				  });
				  }else{
				   this.columnDefinitions.push({
					"key" : definition[c][0], "label" : definition[c][1], "type" : definition[c][2], formatter: getFormatter(definition[c][2]), sortable: true, hidden: true, resizable: true, editor: getEditor(definition[c][2], definition[c][3])
				  });
				  }
             }
		if (!this.readOnly) this.columnDefinitions.push({key:"delete", label:" ", className:"delButton"});
	},
	
		/*
		Gets datatable values in object, then converts object to JSON string
	*/
	dtToJson: function(){ 
		var records = ${propertyName}.dtpDatatable.getRecordSet().getRecords();
		var str = new Array();
		for (var i=0; i < records.length; i++) {
			var o = new Object();
			var keys = ${propertyName}.dtpDatatable.getColumnSet().keys;
			for (var j=0; j < keys.length; j++) o[keys[j].getKey()] = records[i].getData(keys[j].getKey());
			str.push(o);
		}
		${propertyName}.jsonProp.value = YAHOO.lang.JSON.stringify(str);
	}	
};


if("${form.mode}" === "edit"){
    ${propertyName}.${propertyName}Init("${fieldHtmlId}", "${dtConfig?html}", "${form.mode}", "${field.label?html}");
}else if("${form.mode}" === "view"){
	
	var ${propertyName}initilizeMethod = function(e){
						${propertyName}.${propertyName}Init("${fieldHtmlId}", "${dtConfig?html}", "${form.mode}", "${field.label?html}");
											}; 
	var ${propertyName}CheckExecutionOfInterval = function(e) 
	{ 
		if(document.getElementById("${fieldHtmlId}") != null) { 
 		      	${propertyName}.${propertyName}Init("${fieldHtmlId}", "${dtConfig?html}", "${form.mode}", "${field.label?html}");
 		        window.clearTimeout(${propertyName}Interval);
	 	} 
	};																		
			var ${propertyName}Interval = window.setInterval(${propertyName}CheckExecutionOfInterval, 10);
}

//]]></script>
