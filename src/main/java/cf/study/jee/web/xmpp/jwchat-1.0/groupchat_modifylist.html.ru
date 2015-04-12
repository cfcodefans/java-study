<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Изменить</title>
    <script src="switchStyle.js"></script>
    <script src="shared.js"></script>
    <script src="jsjac.js"></script>
		<script>
var deltaItems = new Array(); // contains table rows

function add2deltaItems(row,state) {
	var item = new Object();
	if (row.childNodes.item(0).innerHTML == '' && row.childNodes.item(1).innerHTML == '') // neither nick nor jid supplied
		return;
	if (row.childNodes.item(0).innerHTML != '')
		item.nick = row.childNodes.item(0).innerHTML;
	if (row.childNodes.item(1).innerHTML != '')
		item.jid = row.childNodes.item(1).innerHTML;
	if (row.childNodes.item(2).innerHTML != '')
		item.reason = row.childNodes.item(2).innerHTML;
	
	if (queryType == 'role_based')
		item.state = "role="+state;
	else
		item.state = "affiliation="+state;
	
	deltaItems[deltaItems.length] = item;
}

function deltaAddItem(row,state) {
	var statExp = /(.+)=(.+)/;
	statExp.exec(state);
	add2deltaItems(row,RegExp.$2);
}

function deltaDelItem(row) {
	add2deltaItems(row,'none');
}

function handleIQSetList(iq) {
	// check for errors

	if (iq && iq.getType() == 'result') { // sucess
		srcW.Debug.log(iq.getDoc().xml,2);
		window.close();
		return true;
	}

	var html = '';
	if (iq && iq.getType() == 'error') {
		srcW.Debug.log(iq.getDoc().xml,1);
		for (var i=0; i<iq.getNode().getElementsByTagName('item').length; i++) {
			var item = iq.getNode().getElementsByTagName('item').item(i);
			if (item.getAttribute('jid'))
				html += "Item: "+item.getAttribute('jid')+"<br>";
			else if (item.getAttribute('nick'))
				html += "Nick: "+item.getAttribute('nick')+"<br>";
		}
		for (var j=0; j<iq.getNode().getElementsByTagName('error').length; j++)
			var error = iq.getNode().getElementsByTagName('error').item(j);
		html += "Ошибка" + " " + error.getAttribute('code');
		if (error.getElementsByTagName('text').item(0))
			html += ": " + error.getElementsByTagName('text').item(0).firstChild.nodeValue;
	}	
	
	document.getElementById('hint').innerHTML = '';
	document.getElementById('hint').innerHTML = html;
	
	deltaItems = new Array();
	
	updateListFrame(modItems);
}

function doSub() {
	var iq = new JSJaCIQ();
	iq.setType('set');
	iq.setTo(opener.parent.jid);
	var query = iq.setQuery(args[1]);
	
	for (var i=0; i<deltaItems.length; i++) {
		var item = query.appendChild(iq.getDoc().createElement('item'));
		item.setAttribute(deltaItems[i].state.split('=')[0],deltaItems[i].state.split('=')[1]);
		if (deltaItems[i].nick)
			item.setAttribute('nick',deltaItems[i].nick);
		if (deltaItems[i].jid)
			item.setAttribute('jid',deltaItems[i].jid);
		if (deltaItems[i].reason)
			item.appendChild(iq.getDoc().createElement('reason')).appendChild(iq.getDoc().createTextNode(deltaItems[i].reason));
	}

	srcW.Debug.log(iq.getDoc().xml,2);
	srcW.con.send(iq,me.handleIQSetList);
	
	return false;
}

function deleteItem() {
	if (!modifylistF.selectedRow) {
		alert("Nothing selected");
		return;
	}
	
	deltaDelItem(modifylistF.selectedRow);
	
	var modTableBody = modifylistF.document.getElementById('modTable').getElementsByTagName("TBODY").item(0);
	modTableBody.removeChild(modifylistF.selectedRow);
	
	// reset form
	modifylistF.selectedRow = null;
	document.getElementById('delete_button').disabled = true;
	return false;
}

function addItem() {
	var row = modifylistF.document.createElement("TR");
	
	var cell = modifylistF.document.createElement("TD");
	var textN = modifylistF.document.createTextNode(document.forms['add_item'].nick.value);
	cell.appendChild(textN);
	row.appendChild(cell);
	
	cell = modifylistF.document.createElement("TD");
	textN = modifylistF.document.createTextNode(document.forms['add_item'].jid.value);
	cell.appendChild(textN);
	row.appendChild(cell);
	
	cell = modifylistF.document.createElement("TD");
	textN = modifylistF.document.createTextNode(document.forms['add_item'].reason.value);
	cell.appendChild(textN);
	row.appendChild(cell);
	
	deltaAddItem(row,args[0]);
	
	modifylistF.document.getElementById('modTable').getElementsByTagName('TBODY').item(0).appendChild(row);
	
	// tell frame about it
	modifylistF.init();
	
	// clear form
	document.forms['add_item'].reset();
	document.getElementById('add_button').disabled = true;
	return false;
}

function activateAddButton() {
	document.getElementById('add_button').disabled = false;
}

function updateListFrame(modItems) {
	// clear frame
	modifylistF.document.body.innerHTML = '';
	
	var myTable = modifylistF.document.createElement("TABLE");
	
	var myTableHead = modifylistF.document.createElement("THEAD");
	var myTableBody = modifylistF.document.createElement("TBODY");
	
	var row = modifylistF.document.createElement("TR");
	var header = new Array("Ник","JID","Причина");
	var cell; 
	for (var i=0; i<header.length; i++) {
		cell = modifylistF.document.createElement("TH");
		cell.appendChild(modifylistF.document.createTextNode(header[i]));
		row.appendChild(cell);
	}
	myTableHead.appendChild(row);
	myTable.appendChild(myTableHead);
	
	for (var i=0; i<modItems.length; i++) {
		var item = modItems.item(i);
		row = modifylistF.document.createElement("TR");
			
		cell = modifylistF.document.createElement("TD");
		textN = modifylistF.document.createTextNode((typeof(item.getAttribute('nick')) != 'undefined')?item.getAttribute('nick'):'');
		cell.appendChild(textN);
		row.appendChild(cell);
		
		cell = modifylistF.document.createElement("TD");
		textN = modifylistF.document.createTextNode((typeof(item.getAttribute('jid')) != 'undefined')?item.getAttribute('jid'):'');
		cell.appendChild(textN);
		row.appendChild(cell);
			
		cell = modifylistF.document.createElement("TD");
		textN = modifylistF.document.createTextNode((item.firstChild && item.firstChild.nodeName == 'reason' && item.firstChild.firstChild)?item.firstChild.firstChild.nodeValue:'');
		cell.appendChild(textN);
		row.appendChild(cell);
		
		myTableBody.appendChild(row);
	}
	
	myTable.appendChild(myTableBody);
	
	myTable.setAttribute("id","modTable");
	myTable.setAttribute("WIDTH","100%");
	myTable.setAttribute("BORDER","0");
	myTable.setAttribute("CELLSPACING","0");
	myTable.setAttribute("CELLPADDING","0");
	myTable.setAttribute("RULES","rows");
	
	// add table
	modifylistF.document.body.appendChild(myTable);
	
	// tell frame about it
	modifylistF.init();
}

var modItems;
function handleIQGetList(iq) {
	if (!iq || iq.getType() == 'error') {
		document.getElementById('hint').innerHTML = "Произошла ошибка";
		if (iq)
			srcW.Debug.log(iq.getDoc().xml,1);
		return;
	}

	srcW.Debug.log(iq.getDoc().xml,2);
	
	if (!iq.getQuery().childNodes.length) {
		document.getElementById('hint').innerHTML = "Не найдено";
	}
	
	modItems = iq.getQuery().getElementsByTagName('item');
	
	updateListFrame(modItems);
	
}

var srcW;
var queryType;
function init() {
	srcW = opener.parent.top.srcW;
	
	// get args
	search = self.location.href;
	search = search.split('?');
	
	if(search.length>1){
		args = search[1];
		args = args.split('&');
		args[2] = unescape(args[2]);
	} else
		return;
	
	if (args[0].indexOf("role") != 0 && args[0].indexOf("affiliation") != 0)
		return; // it's your fault
	
	
	if (args[0].indexOf("role") == 0)
		queryType = 'role_based';
	else
		queryType = 'affiliation_based';
	
	roster = srcW.roster;

	var iq = new JSJaCIQ();
	iq.setType('get');
	iq.setTo(opener.parent.jid);
	var query = iq.setQuery(args[1]);
	query.appendChild(iq.getDoc().createElement('item')).setAttribute(args[0].split('=')[0],args[0].split('=')[1]);
	srcW.Debug.log(iq.getDoc().xml,2);
	me = this;
	srcW.con.send(iq,me.handleIQGetList);
	
	document.getElementById('title').innerHTML = "Изменить "+args[2];
	document.title += " " + args[2];
	
	// reset selector
	opener.document.getElementById('list_selector').selectedIndex = 0;
	
	// disable buttons on start
	document.getElementById('add_button').disabled = true;
	document.getElementById('delete_button').disabled = true;
}
onload = init;
function keyPressed(e) {
	if (e.keyCode == 13)
		return doSub();
	if (e.keyCode == 27)
		window.close();
	return true;
}

onload = init;
onkeydown = keyPressed;
    </script>
    <script for="document" event="onkeydown()" language="JScript">
      <!--
      return keyPressed(window.event);
      //-->
    </script>
		<style type="text/css">
			h1 { font-size: 1.5em; }
			th { font-size: 0.8em; text-align: right; }
		</style>
  </head>
  <body style="margin:8px;">
		<table width="100%" height="100%">
		<tr><td><h1 id="title">&nbsp;</h1></td></tr>
		<tr><td><span id="hint">&nbsp;</span></td></tr>
		<tr><td width="100%" height="100%"><iframe src="groupchat_modifylist_iframe.html" id="modifylistF" name="modifylistF" scrolling="auto" style="width: 100%; height: 100%"></iframe></td></tr>
		<tr><td><form name="add_item">
		<table>
		<tr><th>Nick</th><td><input type="text" name="nick" onkeydown="activateAddButton();"></td></tr>
		<tr><th>JID</th><td><input type="text" name="jid" style="width: 100%;" onkeydown="activateAddButton();"></td></tr>
		<tr><th>Reason</th><td><input type="text" name="reason" style="width: 100%;" onkeydown="activateAddButton();"></td></tr>
		</table></form>
		</td></tr>
		<tr><td><hr noshade size=1></td></tr>
		<tr><td align="right">
		<button id="add_button" onClick="return addItem();">Добавить</button>&nbsp;<button id="delete_button" onClick="return deleteItem();">Удалить</button>&nbsp;<button type="submit" id="save_button" onClick="return doSub();">Сохранить</button>
		</td>
		</tr>
		</table>
  </body>
</html>
