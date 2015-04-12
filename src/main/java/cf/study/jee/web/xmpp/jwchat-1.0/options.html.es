<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
    <title>JWChat - Opciones</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="switchStyle.js"></script>
    <script src="version.js"></script>
    <script src="shared.js"></script>
    <script src="jabber_x_data.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--
function savePrefs() {
  var prefs = new Array('usersHidden','autoPopup','autoPopupAway','focusWindows','timestamps','playSounds','timerval','enableLog');
  
  if (!srcW.roster)
    return;

  var iq = new JSJaCIQ();
  iq.setType('set');
  var query = iq.setQuery('jabber:iq:private');
  var jNode = query.appendChild(
	iq.buildNode('jwchat', {'xmlns': 'jwchat:prefs'}));
  
  for (var i=0; i<prefs.length; i++) {
    if (prefs[i] == 'usersHidden')
	  jNode.appendChild(iq.buildNode(prefs[i], srcW.roster.usersHidden+""));
    else
	  jNode.appendChild(iq.buildNode(prefs[i], srcW[prefs[i]]+""));
  }

  srcW.Debug.log(iq.xml(),2);

  srcW.con.send(iq);
}

	
function displayTimestamps() {
  srcW.timestamps = form.timestamps.checked;
  /* set timestamp display in all open chat-windows */
  for (var i=0; i<srcW.roster.users.length; i++) {
    if ((srcW.roster.users[i].chatW) && (srcW.roster.users[i].chatW.closed == false)) {
      srcW.roster.users[i].chatW.top.displayTimestamp();
    }
    if (srcW.roster.users[i].roster) {
      for (var j=0; j<srcW.roster.users[i].roster.users.length; j++) {
        if (srcW.roster.users[i].roster.users[j].chatW && 
	    !srcW.roster.users[i].roster.users[j].chatW.closed)
          srcW.roster.users[i].roster.users[j].chatW.displayTimestamp();
         }
    }
  }
}

function toggleAutoPopupAway(el) {
  document.getElementById('autoPopupAway').disabled = !el.checked;
}

var form;
function getOptions() {
  form = document.forms['options'];
  if (srcW.roster.usersHidden)
    form.usersHidden.checked = true;
  if (srcW.autoPopup)
    form.autoPopup.checked = true;
  if (srcW.autoPopupAway)
    form.autoPopupAway.checked = true;
  if (srcW.playSounds == true)
    form.playSounds.checked = true;
  if (srcW.timestamps == true)
    form.timestamps.checked = true;
  if (srcW.focusWindows == true)
    form.focusWindows.checked = true;

  form.enableLog.checked = srcW.enableLog;

  if (typeof(srcW.loghost) == 'undefined')
    document.getElementById('tr_enableLog').style.display = 'none';

  if (srcW.con.isPolling()) {
    for (var i=0; i<form.timerval.options.length; i++) {
      if (form.timerval.options[i].value == srcW.timerval)
	form.timerval.selectedIndex = i;
    }
  }
  toggleAutoPopupAway(document.getElementById('autoPopup'));
}

var pane;
function service_selected(el) {
  pane = el.nextSibling;
  if (el.options[el.selectedIndex].value == '') {
    pane.innerHTML = '';
    return;
  }

  pane.innerHTML = "<div class='transport_pane'>"+"Consultando servicio ...<br>¡Por favor espere!"+"</div>";

  var iq = new JSJaCIQ();
  iq.setType('get');
  iq.setTo(el.options[el.selectedIndex].value);
  iq.setQuery('jabber:iq:register');
  
  me = this;
  srcW.con.send(iq,me.getRegisterForm);
}

function getRegisterForm(iq) {
  if (!iq || iq.getType() != 'result' || iq.getQueryXMLNS() != 'jabber:iq:register') {
    pane.innerHTML = "<div class='transport_pane'>"+"Ha ocurrido un error ..."+"</div>";		
    return;
  }
  srcW.Debug.log(iq.getDoc().xml,2);

  var query = iq.getQuery();
	
  // check if it's jabber:x:data

  var html = '';
  if (iq.getNode().getElementsByTagName('x').length && iq.getNode().getElementsByTagName('x').item(0).getAttribute('xmlns') == 'jabber:x:data') {
    html += "<form>"+genJabberXDataTable(iq.getNode().getElementsByTagName('x').item(0))+"</form>";
  } else {
    if (query.getElementsByTagName('registered').item(0))
      html += "<div class='pane_el'><b style='color:red;'>"+"Ya estás registrado en este servicio"+"</b></div>";
    if (query.getElementsByTagName('instructions').item(0))
      html += "<div class='pane_el'>"+query.getElementsByTagName('instructions').item(0).firstChild.nodeValue+"</div>";
    html += "<form name='transport'><table>";
    for (var i=0; i<query.childNodes.length; i++) {
      var aItem = query.childNodes.item(i);
      switch (aItem.nodeName) {
      case 'instructions':
      case 'registered': 
	break;
      case 'key': // hide key
	html += "<tr><td colspan=2><input type=hidden name='"+aItem.nodeName+"' value='"+aItem.firstChild.nodeValue+"'></td></tr>";
	break;
      case 'password':
	if (aItem.firstChild)
	  html += "<tr><td>"+aItem.nodeName+"</td><td><input type='password' name='"+aItem.nodeName+"' value='"+aItem.firstChild.nodeValue+"'></td></tr>";
	else
	  html += "<tr><td>"+aItem.nodeName+"</td><td><input type='password' name='"+aItem.nodeName+"'></td></tr>";
	break;
      default:
	if (aItem.firstChild)
	  html += "<tr><td>"+aItem.nodeName+"</td><td><input type='text' name='"+aItem.nodeName+"' value='"+aItem.firstChild.nodeValue+"'></td></tr>";
	else
	  html += "<tr><td>"+aItem.nodeName+"</td><td><input type='text' name='"+aItem.nodeName+"'></td></tr>";
      }
    }
    html += "</table></form>";
  }
  html += "<div class='pane_el' align='right'>";
  if (query.getElementsByTagName('registered').item(0)) {
    html += "<button onClick=\"unregisterGateway('"+iq.getFrom()+"');\">"+"No registrado"+"</button>";
    html += "&nbsp;";
    html += "<button onClick=\"registerService('"+iq.getFrom()+"','"+pane.id+"');\">"+"Actualizar"+"</button>";
  } else
    html += "<button onClick=\"registerService('"+iq.getFrom()+"','"+pane.id+"');\">"+"Registrarse"+"</button>";
  html += "</div>";
  pane.innerHTML = html;

}

var ie5=document.all&&document.getElementById;
var oldactiveindex = 0;

function tabclicked(e) {
  // find index of clicked element
  var firingobj=ie5? event.srcElement : e.target;
  var tablabels = document.getElementById('tablabels');
  for (var i=0; i<tablabels.childNodes.length; i++)
    if (tablabels.childNodes[i] == firingobj) {
      if (oldactiveindex == i)
	return;
      var tabs = document.getElementById('tabs');
      tabs.childNodes[oldactiveindex].className = 'tabinactive';
      tabs.childNodes[i].className = 'tab';
      hide_error(tabs.childNodes[i].id);
      tablabels.childNodes[i].className = 'tablabelactive';
      tablabels.childNodes[oldactiveindex].className = 'tablabel';
      oldactiveindex = i;
    }
}

function hide_error(parent_id) {
  var el = getChildByClassName(parent_id, 'error_wrap');
  if (el) el.style.display = 'none';
}

function show_error(parent_id, text) {
  var el = getChildByClassName(parent_id, 'error_wrap');
  
  if (!el) return false; // well ... not our fault, stupid

  el.innerHTML = text;
  el.style.display = '';

  return false;
}

function getChildByClassName(parent, className) {
  if (document.getElementById(parent))
    var children = document.getElementById(parent).childNodes
  else return null;
  for (var i in children)
    if (children[i].className == className)
      return children[i]; // first occurrence
  return null; // nothing found
}

function changePassword(form) {
  hide_error('password_tab');

  if (form.password_new.value != form.password_repeat.value)
    return show_error('password_tab', "Passwords don't match");
    
  var aIQ = new JSJaCIQ();
  aIQ.setType("set");

  var aQuery = aIQ.setQuery("jabber:iq:register");

  aQuery.appendChild(aIQ.buildNode('username', srcW.nick));
  aQuery.appendChild(aIQ.buildNode('password', form.password_new.value));

  me = self;
  srcW.con.sendIQ(aIQ,{
  result_handler:
    function() {
      me.show_error('password_tab', "Password changed successfully");
    },
  error_handler:
    function() {
      me.show_error("An error occured");
    }
  });

  return false;
}

function registerService(sJid,paneid) {
  var aForm;
  
  var pane = document.getElementById(paneid);

  for (var i=0; i<pane.childNodes.length; i++) {
    if (pane.childNodes[i].tagName == 'FORM') {
      aForm = pane.childNodes[i];
      break; // found
    }
  }

  var setxml = '';
	
  var jabberXData = (aForm.elements['jwchat_form_type'] && aForm.elements['jwchat_form_type'].value == 'jabber:x:data');
  
  if (jabberXData) {
    setxml = genJabberXDataReply(aForm);
  }	else {
    for (var i=0; i<aForm.elements.length; i++) {
      var el = aForm.elements[i];
      setxml += "<"+el.name+">"+el.value+"</"+el.name+">";
    }
  }

  var iq = new JSJaCIQ();
  iq.setType('set');
  iq.setTo(sJid);
  
  var query = iq.setQuery('jabber:iq:register');
  
  var xmldoc = XmlDocument.create('body','foo');
  xmldoc.loadXML('<body>'+setxml+'</body>');
  
  for (var i=0; i<xmldoc.firstChild.childNodes.length; i++)
    query.appendChild(xmldoc.firstChild.childNodes.item(i).cloneNode(true));
  
  srcW.Debug.log(iq.getDoc().xml,2);
  
  me = this;
  srcW.con.send(iq,me.handleRegisterService,sJid);
  
  pane.innerHTML = "<strong>"+"Request sent to "+sJid+".."+"</strong><br>";
}

/* callback from registering a service */
function handleRegisterService(iq,sJid) {
  if (iq.getType() == 'error') {
		
    var html = "<strong style='color:red;'>"+"Error";
    if (iq.getNode().getElementsByTagName('error').item(0)) {
      var aErr = iq.getNode().getElementsByTagName('error').item(0);
      if (aErr.getAttribute('code'))
	html += " ("+aErr.getAttribute('code')+")";
      html += ": ";
      if (aErr.firstChild && aErr.firstChild.nodeValue)
	html += aErr.firstChild.nodeValue;
      else if (aErr.firstChild) {
	switch (aErr.firstChild.nodeName) {
	case 'bad-request': html += "Bad Request";
	  break;
	default: html += aErr.firstChild.nodeName;
	  break;
	}
      }
    }
    html += "</strong><br/>";
    pane.innerHTML += html;
    return;
  }
  pane.innerHTML += "<strong>"+"Registration successful."+"</strong><br/>";
	
  // send presence
  pane.innerHTML += "<strong>"+"Sending presence..."+"</strong><br />";
  var aPresence = new JSJaCPresence();
  aPresence.setTo(sJid);
  aPresence.setShow(srcW.onlstat);
  aPresence.setStatus(srcW.onlmsg);
  srcW.con.send(aPresence);
  pane.innerHTML += "<strong>"+"Done."+"</strong><br />";
}

function unregisterGateway(sJid) {
  if (srcW.roster.getUserByJID(sJid) != null)
    sJid = srcW.roster.getUserByJID(sJid).fulljid;

  pane.innerHTML = "<strong>"+"Unregistering from service "+sJid+" ..."+"</strong><br />";
  var iq = new JSJaCIQ();
  iq.setType('set');
  iq.setTo(sJid);
  var query = iq.setQuery('jabber:iq:register');
  var item = query.appendChild(iq.getDoc().createElement('remove'));
  me = this;
  srcW.con.send(iq,me.handleUnregisterService,sJid);
}

function handleUnregisterService(iq,sJid) {
  if (iq.getType() == 'error') {
    var html = "<strong style='color:red;'>"+"Error";
    if (iq.getNode().getElementsByTagName('error').item(0)) {
      var aErr = iq.getNode().getElementsByTagName('error').item(0);
      if (aErr.getAttribute('code'))
	html += " ("+aErr.getAttribute('code')+")";
      html += ": ";
      if (aErr.firstChild && aErr.firstChild.nodeValue)
	html += aErr.firstChild.nodeValue;
      else if (aErr.firstChild) {
	switch (aErr.firstChild.nodeName) {
	case 'bad-request': html += "Bad Request";
	  break;
	default: html += aErr.firstChild.nodeName;
	  break;
	}
      }
    }
    html += "</strong><br/>";
    pane.innerHTML += html;
    return;
  }
  pane.innerHTML += "<strong>"+"Done."+"</strong><br />";
  pane.innerHTML += "<strong>"+"Removing "+sJid+" from roster..."+"</strong><br />";
  
  var iq = new JSJaCIQ();
  iq.setType('set');
  var query = iq.setQuery('jabber:iq:roster');
  var item = query.appendChild(iq.getDoc().createElement('item'));
  item.setAttribute('jid',sJid);
  item.setAttribute('subscription','remove');
  
  srcW.con.send(iq);
  
  if (confirm("Remove all associated contacts?")) {
    pane.innerHTML += "<strong>"+"Removing contacts..."+"</strong><br />";
    var iq = new JSJaCIQ();
    iq.setType('set');
    var query = iq.setQuery('jabber:iq:roster');
    for (var i=0; i<srcW.roster.users.length; i++) {
      if (cutResource(srcW.roster.users[i].jid.substring(srcW.roster.users[i].jid.indexOf('@')+1)) == cutResource(sJid) &&
	  srcW.roster.users[i].jid != cutResource(sJid)) {
	var item = query.appendChild(iq.getDoc().createElement('item'));
	item.setAttribute('jid',srcW.roster.users[i].fulljid);
	item.setAttribute('subscription','remove');
	pane.innerHTML += srcW.roster.users[i].name+"<br />";
      }
    }
    srcW.con.send(iq);
  }
  pane.innerHTML += "<strong>"+"Done."+"</strong><br />";
}

var srcW;
function init() {
  srcW = opener.parent;

  getOptions();
  
  // initialise handlers for tabs
  var tablabels = document.getElementById('tablabels');
  for (var i=0; i<tablabels.childNodes.length; i++)
    tablabels.childNodes[i].onclick = tabclicked;
  
  // set version
  var jwchat_version = document.getElementById('jwchat_version');
  jwchat_version.innerHTML = VERSION;
  if (VERSION == "CVS")
    jwchat_version.innerHTML += "<br>(" + document.lastModified + ")";
  
  var transports_selector = document.getElementById('transports_selector');
  var transport_optidx = 1;
  var directory_selector = document.getElementById('directory_selector');
  var directory_optidx = 1;
  for (var i in srcW.disco) {
    if (!srcW.disco[i].getNode) continue;
    var item = srcW.disco[i];
    if (item.getNode().getElementsByTagName('identity').item(0)) {
      if (item.getNode().getElementsByTagName('identity').item(0).getAttribute('category') == 'gateway') {
	for (var j=0; j<item.getNode().getElementsByTagName('feature').length; j++) {
	  if (item.getNode().getElementsByTagName('feature').item(j).getAttribute('var') == 'jabber:iq:register') {
	    transports_selector.options[transport_optidx++] = new Option(item.getNode().getElementsByTagName('identity').item(0).getAttribute('name'),item.getFrom());
	    break;
	  }
	}
      } else if (item.getNode().getElementsByTagName('identity').item(0).getAttribute('category') == 'directory') {
	for (var j=0; j<item.getNode().getElementsByTagName('feature').length; j++) {
	  if (item.getNode().getElementsByTagName('feature').item(j).getAttribute('var') == 'jabber:iq:register') {
	    directory_selector.options[directory_optidx++] = new Option(item.getNode().getElementsByTagName('identity').item(0).getAttribute('name'),item.getFrom());
	    break;
	  }
	}
      }
    }
  }
  
  if (directory_optidx == 1)
    document.getElementById('dir_label').style.display = 'none';
  if (transport_optidx == 1)
    document.getElementById('gw_label').style.display = 'none';
}

onload = init;
onunload = savePrefs;
      //-->
    </script>
		<style type="text/css">
			th { font-size: 80%; text-align: right; font-weight: normal; }
			#transport_pane input { 
			border: 1px solid black;
			}
			.tablabels {
			padding-top: 8px;
			margin-bottom: 4px;
			cursor: default;
			}
			.tablabel {
			border: 1px solid black;
			border-bottom: 0px solid white;
			padding: 4px;
			background-color: lightgrey;
			}
			.tablabelactive {
			border: 1px solid black;
			border-bottom: 1px solid white;
			padding: 4px;
			padding-top: 6px;
			background-color: white;
			}
			td.tabs {
			padding: 4px;
			border: 1px solid black;
			background-color: white;
			}
			.tab { display: block; }
			.tabinactive { display: none;	}
			.pane_el { margin: 4px; }
            .error_wrap {
            padding: 8px;
              margin: 8px;
            font-weight: bold;
            background-color: #ffc2c2;
            border: 1px solid red;
            }
		</style>
  </head>
  <body style="margin: 8px;">

	<table border=0 cellpadding=0 cellspacing=0 height="100%" width="100%">
		<tr>
			<td>
			<div id="tablabels" class="tablabels"><span class="tablabelactive">General</span><span class='tablabel' id='pw_label'>Change Password</span><span class="tablabel" id='gw_label'>Transportes</span><span class="tablabel" id='dir_label'>vJUD</span><span class="tablabel">Acerca de</span></div>
			</td></tr>
		<tr><td height="100%" class="tabs" valign="top">
			<div id="tabs"><div id="tab1" class="tab">
				<form name="options">
					<fieldset>
						<legend>Opciones</legend>
						<table border="0">
								<tr>
									<td><input id="usersHidden" type="checkbox" onChange="srcW.roster.toggleHide();"></td>
									<td><label for="usersHidden">Ocultar usuarios desconectados</label></td>
								</tr>
								<tr>
									<td><input id="autoPopup" type="checkbox" onChange="srcW.autoPopup=this.checked;toggleAutoPopupAway(this);"></td>
									<td><label for="autoPopup">Abrir mensajes y charlas automáticamente</label></td>
								</tr>
								<tr>
									<td><input id="autoPopupAway" type="checkbox" onChange="srcW.autoPopupAway=this.checked"></td>
									<td><label for="autoPopupAway">Mostrar ventana incluso si ausente</label></td>
								</tr>
								<tr>
									<td><input id="playSounds" type="checkbox" onChange="srcW.playSounds=this.checked;"></td>
									<td><label for="playSounds">Reproducir sonidos</label></td>
								</tr>
								<tr>
									<td><input id="focusWindows" type="checkbox" onChange="srcW.focusWindows=this.checked;"></td>
									<td><label for="focusWindows">Mostrar ventana si se recibe un nuevo mensaje</label></td>
								</tr>
								<tr>
									<td><input id="timestamps" type="checkbox" onClick="displayTimestamps();"></td>
									<td><label for="timestamps">Mostrar horas en la ventana de charla</label></td>
								</tr>
								<tr id='tr_enableLog'>
									<td><input id="enableLog" type="checkbox" onChange="srcW.enableLog=this.checked;"></td>
									<td><label for="enableLog">Guardar historial de mensajes</label></td>
								</tr>
<script language="JavaScript">
									if (opener.parent.con.isPolling()) 
										document.write(' \
								<tr> \
									<td colspan="2"> \
										Intervalo de actualización: \
										<select name="timerval" onChange="srcW.con.setPollInterval(this.value);srcW.timerval=this.value;"> \
											<option value="2000">2 seg.</option> \
											<option value="5000">5 seg.</option> \
											<option value="10000">10 seg.</option> \
											<option value="30000">30 seg.</option> \
										</select> \
									</td> \
								</tr>');
</script>
						</table>
					</fieldset>
				</form>
            </div><div class="tabinactive" id="password_tab">
                <div class="error_wrap"></div>
                <form name="password_form" onSubmit="return changePassword(this);">
                  <table border="0">
                    <tr><th>New Password</th>
                      <td><input type="password" name="password_new" value=""></td></tr>
                    <tr><th>Retype Password</th>
                      <td><input type="password" name="password_repeat" value=""></td></tr>
                    <tr><td></td><td><input type="submit"></td></tr>
                    </table></form>
			</div><div class="tabinactive" id="transports_tab">Registrarse en <select id="transports_selector" onchange="service_selected(this);"><option value=""> - Escoger Transporte - </option></select><div id="transport_pane"></div></div><div class="tabinactive" id="directory_tab">Registrarse en  <select id="directory_selector" onchange="service_selected(this);"><option value=""> - Escoger directorio - </option></select><div id="directory_pane"></div></div><div class="tabinactive" id="about_tab" align="center">
								<p>
								<img src="images/jwchat.jpg" style="border:1px solid black;">
								<h1 style="margin-bottom: 0px;">JWChat</h1>
								Version <span id="jwchat_version"></span>
								<p>
								<nobr>&copy 2003-2004 by <a href="mailto:steve@zeank.in-berlin.de">Stefan Strigler</a></nobr>
								<p>
									<img src="images/jumpto.gif" align="middle"><a href="http://jwchat.sourceforge.net" target="_new">http://jwchat.sourceforge.net</a>
							</div></div>
			</td></tr>
	</table>
  </body>
</html>
