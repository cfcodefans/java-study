<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Поиск</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="shared.js"></script>
    <script src="jabber_x_data.js"></script>
    <script src="switchStyle.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--

function back() {
  document.getElementById('search_form').style.display = '';
  document.getElementById('search_results').style.display = 'none';

  //	reset form
  document.getElementById('add_button').disabled = true;
  document.getElementById('info_button').disabled = true;
  resultF.selectedRow = null;

  resultF.document.getElementById('results').innerHTML = '';

  return false;
}

function searchUser() {
  document.getElementById('search_results').style.display = '';
  document.getElementById('search_form').style.display = 'none';

  document.getElementById('results_header').innerHTML = "Выполняется запрос...";
  // send query
  var searchstring = '';
  var jabberXData = (document.forms['searchfields'].elements['jwchat_form_type'] && document.forms['searchfields'].elements['jwchat_form_type'].value == 'jabber:x:data');

  if (jabberXData)
    searchstring = genJabberXDataReply(document.forms['searchfields']);
  else {
    for (var i=0; i<document.forms['searchfields'].elements.length; i++) 
      if (document.forms['searchfields'].elements[i].name != '' && document.forms['searchfields'].elements[i].value != '') 
	searchstring += "<"+document.forms['searchfields'].elements[i].name+">"+document.forms['searchfields'].elements[i].value+"</"+document.forms['searchfields'].elements[i].name+">";
  }

  var to = document.forms['service_select'].elements['service'].options[document.forms['service_select'].elements['service'].selectedIndex].value;

  srcW.Debug.log("searchstring: "+ searchstring, 3);

  var iq = new JSJaCIQ();

  if (searchstring == '') {
    iq.setTo(to+"/users");
    iq.setType('get');
    iq.setQuery('jabber:iq:browse');
  } else {
    iq.setTo(to);
    iq.setType('set');
    var query = iq.setQuery('jabber:iq:search');

    var xmldoc = XmlDocument.create('','');
    xmldoc.loadXML(searchstring);

    query.appendChild(xmldoc.documentElement.cloneNode(true));
  }

  srcW.Debug.log("created query:" + iq.xml(),2);
  
  srcW.con.send(iq, getQueryResult);
  return false;
}

function getQueryResult(iq) {
  if (!iq || iq.getType() != 'result') {
    document.getElementById('results_header').innerHTML = "Произошла ошибка";
    return false;
  }

  srcW.Debug.log("got result: " + iq.getDoc().xml,2);

  var html = "<table width='100%' border=0 cellspacing=0 cellpadding=0>";

  // check if it's jabber:x:data
  if (iq.getNode().getElementsByTagName('x').length && iq.getNode().getElementsByTagName('x').item(0).getAttribute('xmlns') == 'jabber:x:data') {

    var x = iq.getNode().getElementsByTagName('x').item(0);

    // get title
    if (x.getElementsByTagName('title').item(0))
      document.getElementById('results_header').innerHTML = x.getElementsByTagName('title').item(0).firstChild.nodeValue;
    else
      document.getElementById('results_header').innerHTML = "Результаты поиска для "+iq.getFrom();

    if (x.getElementsByTagName('reported').item(0) && x.getElementsByTagName('reported').item(0).getElementsByTagName('field').length) { // well - this should be there always

      // get header
      html += "<tr>";
      for (var i=0 ; i<x.getElementsByTagName('reported').item(0).getElementsByTagName('field').length; i++) {
	var aField = x.getElementsByTagName('reported').item(0).getElementsByTagName('field').item(i);
	html += "<th var='"+aField.getAttribute('var')+"' nowrap>"+aField.getAttribute('label')+"</th>";
      }
      html += "</tr>";

      // get items
      for (var i=0; i<x.getElementsByTagName('item').length; i++) {
	var item = x.getElementsByTagName('item').item(i);
	
	html += "<tr>";
	for (var j=0 ; j<x.getElementsByTagName('reported').item(0).getElementsByTagName('field').length; j++) {
	  var rField = x.getElementsByTagName('reported').item(0).getElementsByTagName('field').item(j);
	  for (var k=0; k<item.getElementsByTagName('field').length; k++) {
	    var iField = item.getElementsByTagName('field').item(k);
	    if (rField.getAttribute('var') == iField.getAttribute('var')) { // matched
	      if (!iField.firstChild.firstChild)
		html += "<td>&nbsp;</td>";
	      else
		html += "<td nowrap>"+iField.firstChild.firstChild.nodeValue+"</td>";
	    }
	  }
	}
	
	html += "</tr>";
      }
    }
  } else if (iq.getNode().firstChild.getAttribute('xmlns') == 'jabber:iq:browse') {
    document.getElementById('results_header').innerHTML = "Результаты поиска для "+iq.getFrom();

    // set header
    html += "<tr><th>JID</th><th>Name</th></tr>";

    // fill in items
    for (var i=0; i<iq.getNode().firstChild.childNodes.length; i++) {
      var item = iq.getNode().firstChild.childNodes.item(i);
      html += "<tr>";
      html += "<td>"+item.getAttribute('jid')+"</td>";
      var val = item.getAttribute('name');
      if (!val || val == '')
	val = '&nbsp;';
      html += "<td nowrap>"+val+"</td>";
      html += "</tr>";
    }
  } else if (iq.getQueryXMLNS() == 'jabber:iq:search') {
    document.getElementById('results_header').innerHTML = "Результаты поиска для "+iq.getFrom();

    // set header
    html += "<tr><th>JID</th>";
    for (var j=0; j<document.forms['searchfields'].elements.length; j++)
      html += "<th>"+document.forms['searchfields'].elements[j].name+"</th>";
    html += '<tr>';

    // fill in items
    for (var i=0; i<iq.getQuery().childNodes.length; i++) {
      var item = iq.getQuery().childNodes.item(i);
      html += "<tr>";
      html += "<td>"+item.getAttribute('jid')+"</td>";
      for (var j=0; j<document.forms['searchfields'].elements.length; j++) {
        if (document.forms['searchfields'].elements[j].name != '' && item.getElementsByTagName(document.forms['searchfields'].elements[j].name).length > 0 && item.getElementsByTagName(document.forms['searchfields'].elements[j].name).item(0).firstChild)
          html += "<td nowrap>"+item.getElementsByTagName(document.forms['searchfields'].elements[j].name).item(0).firstChild.nodeValue+"</td>";
        else
          html += "<td nowrap>&nbsp;</td>";
      }
    }
  }
  
  html += "</table>";
  resultF.document.getElementById('results').innerHTML = html;
  resultF.init();
  
  return false;
}

function serviceSelected(selbox) {
  var el = selbox.options[selbox.selectedIndex];

  // clear box
  document.getElementById('searchfields').innerHTML = '';

  if (el.value == '')
    return;

  // query service

  var iq = new JSJaCIQ();
  iq.setType('get');
  iq.setTo(el.value);
  iq.setQuery('jabber:iq:search');

  me = this;
  srcW.con.send(iq,me.getSearchForm);
  return false;
}

function getSearchForm(iq) {
  if (!iq || iq.getType() != 'result') {
    document.getElementById('searchfields').innerHTML = "Произошла ошибка\nПрерывание операции...";
    srcW.Debug.log("empty result",1);
    return false;
  }
  srcW.Debug.log(iq.getDoc().xml,2);

  // check if it's jabber:x:data
  var html = '';
  if (iq.getNode().getElementsByTagName('x').length && iq.getNode().getElementsByTagName('x').item(0).getAttribute('xmlns') == 'jabber:x:data') {
    html += genJabberXDataTable(iq.getNode().getElementsByTagName('x').item(0));
  } else {

    html = "<table>";
    if (iq.getNode().getElementsByTagName('instructions').item(0))
      html += "<tr><th colspan=2>"+iq.getNode().getElementsByTagName('instructions').item(0).firstChild.nodeValue+"</th></tr>";
    
    for (var i=0; i<iq.getQuery().childNodes.length; i++) {
      var aNode = iq.getQuery().childNodes.item(i);
      if (aNode.nodeName == 'instructions')
	continue;
      if (aNode.nodeName == 'key')
	html += "<tr><td colspan=2><input type=hidden value=\""+aNode.firstChild.nodeValue+"\"></td></tr>";
      else {
	if (aNode.firstChild)
	  html += "<tr><td>"+aNode.nodeName+"&nbsp;</td><td><input type=\"text\" name=\""+aNode.nodeName+"\" value=\""+aNode.firstChild.nodeValue+"\"></td></tr>";
	else
	  html += "<tr><td>"+aNode.nodeName+"&nbsp;</td><td><input type=\"text\" name=\""+aNode.nodeName+"\"></td></tr>";
      }

    }
    html += "</table>";
  }
  html += "<hr noshade size=1><div align=right><button onClick='return searchUser();'>"+"Искать"+"</button></div>";

  var searchfields = document.getElementById('searchfields');
  searchfields.innerHTML = html;

  return false;
}

var searchW = window;
var srcW;
function init() {
  // determine source window
  if (opener.srcW)
    srcW = opener.srcW;
  else
    srcW = opener;

  // init elements ...
  document.getElementById('search_results').style.display = 'none';
  document.getElementById('add_button').disabled = true;
  document.getElementById('info_button').disabled = true;
  // detect services
  var service = document.forms['service_select'].elements['service'];
  var optidx = 1;
  for (var i in srcW.disco) {
    if (!srcW.disco[i].getNode) continue;
    for (var j=0; j<srcW.disco[i].getNode().getElementsByTagName('feature').length; j++) {
      if (srcW.disco[i].getNode().getElementsByTagName('feature').item(j).getAttribute('var') == 'jabber:iq:search') {
	service.options[optidx++] = new Option(srcW.disco[i].getNode().getElementsByTagName('identity').item(0).getAttribute('name'),srcW.disco[i].getFrom());
	break;
      }
    }
  }
}

function keyPressed(e) {
  if (e.ctrlKey && e.keyCode == 13)
    submitClicked();
  else if (e.keyCode == 27)
    window.close();
}
onkeydown = keyPressed;
onload = init;
      //-->
    </script>
    <script for="document" event="onkeydown()" language="JScript">
      <!--
      if (window.event.ctrlKey && window.event.keyCode == 13)
      submitClicked();
      if (window.event.keyCode == 27)
      window.close();
      //-->
    </script>
		<style type="text/css">
		th { 
			font-size: 12px; 
			text-align: left;
		}
		.searchfield { padding: 4px; }
		</style>
  </head>

  <body style="margin: 8px;">
		<div id="search_form">
		<form name="service_select">
		Искать в <select name="service" onChange="return serviceSelected(this);"><option value=''>- Выберите директорию -</option></select>
		</form>
		<form name="searchfields" id="searchfields">
		</form>
		</div>

		<div id="search_results">
		<table height="100%" width="100%">
		<tr><td id="results_header">Выполняется запрос...</td></tr>
		<tr><td height="100%" width="100%">
		<iframe src="search_iframe.html" id="resultF" name="resultF" scrolling="auto" style="width: 100%; height: 100%"></iframe>
		</td></tr>
		<tr><td align=right>
		<hr noshade size="1">
		<button id="info_button" onClick='return srcW.openUserInfo(resultF.selectedRow.childNodes[0].innerHTML);'><l>User Info<l></button>&nbsp;<button id="add_button" onClick='return srcW.openSubscription(resultF.selectedRow.childNodes[0].innerHTML);'><l>Add Contact<l></button>&nbsp;<button onClick='return back();'><l>Back<l></button>
		</td>
		</tr>
		<table>
		</div>
  </body>
</html>
