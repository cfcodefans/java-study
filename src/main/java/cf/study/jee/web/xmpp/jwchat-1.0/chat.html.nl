<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Chat</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="shared.js"></script>
    <script src="browsercheck.js"></script>
    <script src="emoticons.js"></script>
    <script src="switchStyle.js"></script>
    <script src="statusLed.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--
var user;
var srcW;
var cFrame;

var scrollHeight=0;
function putMsgHTML(msg) {
  var msgHTML = '';
  
  var body = '';
  var err = false;
  if (msg.getType() == 'error') {
    var error = aJSJaCPacket.getNode().getElementsByTagName('error').item(0);
    if (error && error.getElementsByTagName('text').item(0))
      body = error.getElementsByTagName('text').item(0).firstChild.nodeValue;
    err = true;
  }	else
    body = msg.getBody();

  var now;
  if (msg.jwcTimestamp)
    now = msg.jwcTimestamp;
  else
    now = new Date();

  var mtime = (now.getHours()<10)? "0" + now.getHours() : now.getHours();
  mtime += ":";
  mtime += (now.getMinutes()<10)? "0" + now.getMinutes() : now.getMinutes();
  mtime += ":";
  mtime += (now.getSeconds()<10)? "0" + now.getSeconds() : now.getSeconds();

  if (msg.getTo() == user.jid){ // msg sent by me
    nick = htmlEnc(srcW.roster.nick);
    nickcolor = 'blue';
    dir = 'to';
  } else {
    nick = user.name;
    nickcolor = 'red';
    dir = 'from';
  }

  msgHTML += "<div title=\"@ "+mtime+"\" cDate=\""+now.getTime()+"\" dir=\""+dir+"\" body=\""+htmlEnc(body)+"\"><span class=time>["+mtime+"] </span>";
  body = msgFormat(body);
  if (err) {
    msgHTML += "<span style='color:red;'>&nbsp;";
  } else if (body.match(/^\/me /)) {
    body = body.replace(/^\/me /,"<span style=\"color:green;font-weight:bold;\" class=msgnick>*&nbsp;"+nick+"</span> ");
  } else {
    msgHTML += "<span style=\"color:"+nickcolor+";\" class=msgnick>&lt;" + nick + "&gt;</span>&nbsp;";
  }
  msgHTML += body;
  if (err)
    msgHTML += '</span>';
  msgHTML += "</div>";

  var auto_scroll = false;
  if (cFrame.body.scrollTop+cFrame.body.clientHeight >= cFrame.body.scrollHeight) // scrollbar at bottom
    auto_scroll = true;

  cFrame.body.innerHTML += msgHTML;

  if (auto_scroll)
    chat.scrollTo(0,cFrame.body.scrollHeight);
}

function popMsgs() {
  while (user.chatmsgs.length>0) {
    var msg;
    if (is.ie5||is.op) {
      msg = user.chatmsgs[0];
      user.chatmsgs = user.chatmsgs.slice(1,user.chatmsgs.length);
    } else
      msg = user.chatmsgs.shift();

    // calc date
  
    putMsgHTML(msg);
  }
  if (jwcMain.focusWindows)	{
    window.focus();
    document.forms.chatform.msgbox.focus(); 
  }
  if (user.lastsrc != null && user.messages.length == 0) {
    var images = srcW.roster.getUserIcons(user.jid);
    for (var i=0; i<images.length; i++)
      images[i].src = user.lastsrc;
    user.lastsrc = null;
    if (srcW.roster.usersHidden && user.status == 'unavailable') // remove user from roster if not available any more 
      srcW.roster.print();
  }
}

function openUserInfo() {
  return jwcMain.openUserInfo(user.jid);
}

function openUserHistory() {
  return jwcMain.openUserHistory(user.jid);
}

function updateUserPresence() {
  //	var user = srcW.roster.getUserByJID(jid);
  var awaymsg = document.getElementById('awaymsg');
  document.getElementById('user_name').innerHTML = user.name;
  if (user.statusMsg) {
    awaymsg.style.display = '';
    awaymsg.innerHTML = htmlEnc(user.statusMsg);
  } else
    awaymsg.style.display = 'none';

  var img = document.images['statusLed'];
  img.src = eval(user.status + "Led").src;
}

function submitClicked() {
  var body = document.forms[0].elements["msgbox"].value;
  if (body == '') // don't send empty message
    return false;

  var aMessage = new JSJaCMessage();
  aMessage.setType('chat');
  aMessage.setTo(user.jid);
  aMessage.setBody(body);

  jwcMain.con.send(aMessage);

  // insert into chat window
  putMsgHTML(aMessage);

  // add message to our message history
  jwcMain.addtoHistory(body);
  document.forms["chatform"].msgbox.value=''; // empty box
  document.forms["chatform"].msgbox.focus(); // set focus back on input field
	
  return false;
}

var jwcMain;
function init() {
  getArgs();
  
  jid = passedArgs['jid'];
  
  if (opener.top.roster) {
    srcW = opener.top;
    if (srcW.srcW)
      jwcMain = srcW.srcW;
    else
      jwcMain = srcW;
  } else {
    alert("fout tijdens het beginnen van de chat");
    window.close();
  }

  cDate = new Date();

  cFrame = chat.document;
  user = srcW.roster.getUserByJID(jid);
  document.title = "Chat met "+user.name;

  document.getElementById('user_name').innerHTML = user.name;

  if (typeof(srcW.loghost) == 'undefined')
    document.getElementById('hist_button').style.display = 'none';

  updateUserPresence();

  popMsgs();
  displayTimestamp();
}

function displayTimestamp() {
  var tstyle;
  if (is.ie) {
    tstyle = cFrame.styleSheets('timestampstyle');
    tstyle.disabled = jwcMain.timestamps;
  } else {
    tstyle = cFrame.getElementById("timestampstyle");
    tstyle.sheet.disabled = jwcMain.timestamps;
  }
}


var group_open = new Image();
group_open.src = 'images/group_open.gif';
var group_close = new Image();
group_close.src = 'images/group_close.gif';
var msgbox_toggled = false;
function toggle_msgbox(el) {
  if (msgbox_toggled) {
    document.getElementById('msgbox').style.height = '1.4em';
    document.getElementById('chat').style.height = '100%';
    document.getElementById('submitbutton').style.display = 'none';
    el.src = group_close.src;
  } else {
    document.getElementById('msgbox').style.height = '4.2em';
    document.getElementById('chat').style.height = '99%';
    document.getElementById('submitbutton').style.display = '';
    el.src = group_open.src;
  }
  msgbox_toggled = !msgbox_toggled;
}

function msgboxKeyPressed(el,e) {
  var keycode;
  if (window.event) { e  = window.event; keycode = window.event.keyCode; }
  else if (e) keycode = e.which;
  else return true;
	
  switch (keycode) {
  case 13:
    if (e.shiftKey) {
      if (!msgbox_toggled) {
        toggle_msgbox(document.getElementById('toggle_icon'));
        return false;
      }
    } else
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
      el.value = jwcMain.getHistory('up', el.value);
      el.focus(); el.select();
    }
    break;
  case 40:				// shift+down 
    if (e.ctrlKey) {
      el.value = jwcMain.getHistory('down', el.value);
      el.focus(); el.select();
    }
    break;
  case 76:
    if (e.ctrlKey) {   // ctrl+l
      chat.document.body.innerHTML = '';
      return false;
    }
    break;
  case 27:
    window.close();
    break;
  }
  return true;
}

function cleanUp() {
  if (!srcW.enableLog || typeof(srcW.loghost) == 'undefined')
    return;

  var nodes = cFrame.body.getElementsByTagName("div");
  if (nodes.length == 0)
    return;
	
  var aIQ = new JSJaCIQ();
  aIQ.setType('set');
  aIQ.setTo(jwcMain.loghost);
  var aStore = 
	aIQ.appendNode(
	  'store', 
	  {'xmlns': 'http://jabber.org/protocol/archive',
	   'with': user.jid,
	   'start': jabberDate(cDate)});
	
  for (var i=0; i<nodes.length; i++) {
    var node = nodes.item(i);

    var aItem = aStore.appendChild(aIQ.getDoc().createElement(node.getAttribute('dir')));
    aItem.setAttribute('secs',Math.round((node.getAttribute('cDate')-cDate.getTime())/1000));
    aItem.appendChild(aIQ.getDoc().createElement("body")).appendChild(aIQ.getDoc().createTextNode(node.getAttribute('body')));
  }

  jwcMain.Debug.log(aIQ.xml(),2);
  jwcMain.con.send(aIQ);
}

onload = init;
onunload = cleanUp;
      //-->
    </script>
  </head>
  <body style="margin:8px;">
  <table width="100%" height="100%">
		<tr><td colspan=2>
		<table border=0 cellspacing=0 cellpadding=0 width="100%">
		<tr>
		<td width="100%" valign=top><img src="images/unavailable.gif" name="statusLed" width=16 height=16 border=0 align=left><span id="user_name" class="link" onClick="return openUserInfo();" style="padding:2px;" title="Klik hier om de vCard van de contactpersoon te tonen"></span><br clear=all>
		<span id="awaymsg" class="statusMsg"></span></td>
		<td align=right valign=top><button id='hist_button' onClick="return openUserHistory();">Geschiedenis</button></td></table>
		</td></tr>
    <tr><td width="100%" height="100%" colspan=2><iframe src="chat_iframe.html" id="chat" name="chat" scrolling="auto"></iframe></td></tr>
 		<form name="chatform" style="border:0px;margin:0px;padding:0px;">
    <tr>
      <td valign=top><img id="toggle_icon" src="images/group_close.gif" width="14" height="14" onClick="toggle_msgbox(this);"></td>
      <td width="100%">
          <textarea id="msgbox" wrap="virtual" style="width:100%;height:1.4em;" onKeyPress="return msgboxKeyPressed(this,event);" onKeyDown="return msgboxKeyDown(this,event);"></textarea>
      </td>
    </tr>
    <tr id="submitbutton" style="display:none;"><td colspan=2 align=right><button onClick="submitClicked(); return false;">Verzenden</button></td></tr>
	  </form>
  </table>
  </body>
</html>
