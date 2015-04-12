<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="browsercheck.js"></script>
    <script src="shared.js"></script>
    <script src="switchStyle.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--

function submitClicked() {
  var body = document.forms[0].elements["msgbox"].value;

  if (body == '') // don't send empty message
    return false;

	var aMessage = new JSJaCMessage();
	aMessage.setTo(parent.group);
	
  /* handle commands */
  if (body.match(/^\/say (.+)/)) {

		/* *** say *** */

		body = RegExp.$1;
		aMessage.setBody(body);
		aMessage.setType('groupchat');
		parent.srcW.con.send(aMessage);
  } else if (body.match(/^\/clear/)) {

		/* *** clear *** */

		parent.cFrame.body.innerHTML = '';
  } else if (body.match(/^\/nick (.+)/)) {

		/* *** nick *** */

    var nick = body.replace(/^\/nick (.+)/,"$1");
		var aPresence = new JSJaCPresence();
		aPresence.setTo(parent.group+"/"+nick);
		parent.srcW.con.send(aPresence);
  } else if (body.match(/^\/topic (.+)/)) {

		/* *** topic *** */

    var topic = body.replace(/^\/topic (.+)/,"$1");
		aMessage.setType('groupchat');
		aMessage.setSubject(topic);
		parent.srcW.con.send(aMessage);
  } else if (body.match(/^\/ban (\S+)\s*(.*)/)) {

		/* *** ban *** */

		var nick = RegExp.$1;
		var reason = RegExp.$2;

		var jid = parent.roster.getFullJIDByNick(nick);
		if (jid == null) {
			parent.putMsgHTML("Ese apodo no existe"+": " + nick, new Date().toLocaleTimeString(), parent.jid);
		} else {
			parent.changeAffiliation(jid,'outcast',false,reason);
		}
  } else if (body.match(/^\/kick (\S+)\s*(.*)/)) {

		/* *** kick *** */

		var nick = RegExp.$1;
		var reason = RegExp.$2;

		var jid = parent.roster.getFullJIDByNick(nick);
		if (jid == null) {
			parent.putMsgHTML("Ese apodo no existe"+": " + nick, new Date().toLocaleTimeString(), parent.jid);
		} else {
			parent.changeRole(jid,'none',false,reason);
		}
  } else if (body.match(/^\/invite (\S+)\s*(.*)/)) { // [TODO]

		/* *** invite *** */

		var jid = RegExp.$1;
		var reason = RegExp.$2;

	var x = 
	  aMessage.appendNode('x', 
						  {'xmlns': 'http://jabber.org/protocol/muc#user'});
		var aNode = x.appendChild(aMessage.getDoc().createElement('invite'));
		aNode.setAttribute('to',jid);
		if (reason && reason != '')
			aNode.appendChild(aMessage.getDoc().createElement('reason')).appendChild(aMessage.getDoc().createTextNode(reason));
		parent.srcW.con.send(aMessage);
  } else if (body.match(/^\/join (\S+)\s*(.*)/)) {

		/* *** join *** */

		var room = RegExp.$1;
		var pass = RegExp.$2;
		parent.srcW.openGroupchat(room,parent.nick,pass);
  } else if (body.match(/^\/msg (\S+)\s*(.*)/)) {

		/* *** msg *** */

		var nick = RegExp.$1;
		var body = RegExp.$2;

		var jid = parent.roster.getFullJIDByNick(nick);
		if (jid == null)
			parent.putMsgHTML("Ese apodo no existe"+": " + nick, new Date().toLocaleTimeString(), parent.jid);
		else {
			aMessage.setType('chat');
			aMessage.setTo(jid);
			aMessage.setBody(body);
			parent.srcW.con.send(aMessage);
		}
  } else if (body.match(/^\/part\s*(.*)/)) {

		/* *** part *** */

		var msg = RegExp.$1;
		var aPresence = new JSJaCPresence();
		aPresence.setTo(parent.group);
		aPresence.setType('unavailable');
		if (msg && msg != '')
			aPresence.setStatus(msg);
		parent.srcW.con.send(aPresence);
  } else if (body.match(/^\/whois (\S+)/)) {

		/* *** whois *** */

		var nick = RegExp.$1;
		var jid = parent.roster.getFullJIDByNick(nick);
		if (jid == null)
			parent.putMsgHTML("Ese apodo no existe"+": " + nick, new Date().toLocaleTimeString(), parent.jid);
		else
			parent.srcW.openUserInfo(jid);
  } else if (body.match(/^\/help/)) {

		/* *** help *** */

		open("http://www.jabber.org/jeps/jep-0045.html#impl-client-irc");
  } else {
		aMessage.setType('groupchat');
		aMessage.setBody(body);
		parent.srcW.con.send(aMessage);
  }			

  // add message to our message history
  parent.srcW.addtoHistory(body);
  document.forms["chatform"].msgbox.value=''; // empty box
  document.forms["chatform"].msgbox.focus(); // set focus back on input field
  
  return false;
}

var rw;
function openRegisterRoom() {
	if (!rw || rw.closed)
		rw = open("groupchat_register.html","rw"+makeWindowName(parent.jid),"width=300,height=400,resizable=yes");
	rw.focus();

	return false;
}

var iw;
function openInvite() {
	if (!iw || iw.closed)
		iw = open("groupchat_invite_dialog.html","iw"+makeWindowName(parent.group),"width=300,height=200");
	iw.focus();
	return false;
}

function cleanUp() {
	if (iw && !iw.closed)
		iw.close();
	if (rw && !rw.closed)
		rw.close();
}

function msgboxKeyPressed(el,e) {
	var keycode;
	if (window.event) { e  = window.event; keycode = window.event.keyCode; }
	else if (e) keycode = e.keyCode;
	else return true;
	
	switch (keycode) {
	case 9: // tab
         var txt = document.forms["chatform"].msgbox;
         var pos1, pos2;
         var part;
         var possibilities = new Array();
         if(is.ie)
           return false;
         else {
           pos1 = txt.selectionStart; // current cursor position
           // no selection, not at the beginning of the line and at the end of a word or at the end of the line
           if(pos1 == txt.selectionEnd && pos1 > 0 && (txt.value.substring(pos1, pos1+1) == ' ' || pos1 == txt.value.length)) { 
             part = txt.value.substring(0, pos1);
             pos2 = part.lastIndexOf(" ") + 1;
             if(pos2 != -1)
               part = part.substring(pos2, pos1);
           }
         }
         if(part) {
           for (i in top.roster.users) {
             if(top.roster.users[i].name.indexOf(part) == 0)
               possibilities.push(top.roster.users[i].name);
           }
           if(possibilities.length == 1) { // complete, if only one possibility has been found or enumerate possibilities
             if(pos2 == 0) //special case: beginning of line, add additional ":"
               txt.value = txt.value.substring(0, pos2) + possibilities.pop() + ": " + txt.value.substring(pos1, txt.value.length)
             else
               txt.value = txt.value.substring(0, pos2) + possibilities.pop() + " " + txt.value.substring(pos1, txt.value.length)
             return false;
           }
           else if(possibilities.length > 1) {
             var string = possibilities.join(" ");
             parent.putMsgHTML(string, new Date().toLocaleTimeString(), parent.jid);
             return false;
           }
           
         }
         return true;

	case 13:
		if (!e.shiftKey && !e.ctrlKey)
			return submitClicked();
		break;
	}
	return true;
}

function msgboxKeyDown(el,e) {
	var keycode;
	if (window.event) { e  = window.event; keycode = window.event.keyCode; }
	else if (e) keycode = e.which;
	else return true;

	switch (keycode) {

	case 38:				// shift+up
		if (e.ctrlKey) {
			el.value = parent.srcW.getHistory('up', el.value);
			el.focus(); el.select();
		}
		break;
	case 40:				// shift+down 
		if (e.ctrlKey) {
			el.value = parent.srcW.getHistory('down', el.value);
			el.focus(); el.select();
		}
		break;
	case 76:
		if (e.ctrlKey) {   // ctrl+l
			parent.cFrame.body.innerHTML = '';
			return false;
		}
		break;
	case 27:
		window.close();
		break;
	}
	return true;
}

onunload = cleanUp
      //-->
    </script>
  </head>
  <body style="margin:8px;">
    <form name="chatform">
      <table width="100%" height="100%" border=0 cellpadding=0 cellspacing=0>
          <tr height="100%">
            <td width="100%"><textarea id="msgbox" wrap="virtual" style="width:100%;height:100%;" tabindex=1 onKeyPress="return msgboxKeyPressed(this,event);" onKeyDown="return msgboxKeyDown(this,event);"></textarea></td></tr>
          <tr><td height=5></td></tr>
          <tr>
            <td align="right"><button id="config_chan_button" onClick="return parent.openConfig();" style="display:none;" tabindex=5>Configurar</button>&nbsp;<span id="register_room_span" style="display:none;"><button id="register_room_button" onClick="return openRegisterRoom();" tabindex=4>Registrarse</button>&nbsp;</span><button id="invite_button" onClick="return openInvite();" tabindex=3>Invitar</button>&nbsp;<button id='submit' name='submit' type='submit' onClick="return submitClicked();" tabindex=2>Enviar</button></td></tr>
      </table>
    </form>
  </body>
</html>
