<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Mostrar mensaje</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="emoticons.js"></script>
    <script src="shared.js"></script>
    <script src="switchStyle.js"></script>
    <script>
      <!--
var messages = new Array();
var curMSG = lastMSG = 0;
var jid;
var srcW;
		
function showMSG() {
  /*set subject */
  if (messages[curMSG].getSubject())
    document.getElementById('subject').innerHTML = messages[curMSG].getSubject();

  /* set date */
  var date;
	if (messages[curMSG].jwcTimestamp)
		date = messages[curMSG].jwcTimestamp;
	else
		date = new Date();

  document.getElementById('date').innerHTML = date.toLocaleString();

  /* set body */
	msgbox.document.body.innerHTML = msgFormat(messages[curMSG].getBody());

  /* set buttons */
  if (curMSG == 0)
    document.forms[0].elements["prevButton"].disabled = true;
  else 
    document.forms[0].elements["prevButton"].disabled = false;

  if (curMSG+1==lastMSG && opener.roster.getUserByJID(jid).messages.length == 0)
    document.forms[0].elements["nextButton"].disabled = true;
  else
    document.forms[0].elements["nextButton"].disabled = false;

	// disabled by now - sent message to cmeerw@jabber.at to clarify
	// format of this messages

// 	if (messages[curMSG].getType() == 'headline')
// 		document.getElementById('reply_buttons').style.display = 'none';
// 	else
// 		document.getElementById('reply_buttons').style.display = '';

  msgbox.scrollTo(0,0); // scroll to top
}
		
function getNextMSG() { // gets message from roster
  var user = srcW.roster.getUserByJID(jid);
  if (srcW.is.ie5||srcW.is.op) {
    messages[lastMSG++] = user.messages[0];
    user.messages = user.messages.slice(1,user.messages.length);
  } else
    messages[lastMSG++] = user.messages.shift();
  // remove blinking message icon if this was last messages
  if (user.messages.length == 0 && user.chatmsgs.length == 0) {
    var images = srcW.roster.getUserIcons(jid);
    for (var i=0; i<images.length; i++)
      images[i].src = user.lastsrc;
    user.lastsrc = null;
    if (srcW.usersHidden && user.status == 'unavailable') // remove user from roster if not available any more
      srcW.roster.print();
  }
}
		
function next() {
  curMSG++;
  if (curMSG == lastMSG) {
    if (srcW.roster.getUserByJID(jid).messages.length > 0)
      getNextMSG();
    else
      curMSG--;
  }
  
  showMSG();
  return false;
}

function prev() {
  curMSG--;
  if (curMSG < 0)
    curMSG = 0;
  showMSG();
  return false;
}
		
function reply(quote) {
  var url = 'send.html?jid='+escape(jid);
  if(quote)
    url += '&body=' + escape(messages[curMSG].getBody());
  open(url,'sw','width=320,height=200');
  return false;
}
		
function openChat() {
  srcW.roster.openChat(jid);
  return false;
}
		
function init() {
  srcW = opener.top;
  getArgs();
  jid = passedArgs['jid'];
  var user = srcW.roster.getUserByJID(jid);
  if (user.messages.length > 0) {
    // show messages
    document.title = "Mensaje de "+user.name; // set title
    getNextMSG();
    showMSG();
  }
}

onload = init;
      //-->
    </script>
  </head>
  <body style="margin:8px;">
    <form name="msg">
    <table width="100%" height="100%" border=0 cellpadding=0 cellspacing=0>
        <tr><td><b>Asunto:</b> <span id="subject"></span></td></tr>
        <tr><td><b>Fecha:</b> <span id="date"></span></td></tr>
        <tr height="100%"><td style="padding-top: 4px;"><iframe src="chat_iframe.html" id="msgbox" name="msgbox" scrolling="auto" style="width: 100%; height: 100%;"></iframe></td></tr>
        <tr><td><hr noshade size="1"></td></tr>

        <tr id="reply_buttons"><td align="right"><button onClick="return reply(true);">Citar</button>&nbsp;<button onClick="return reply();">Responder</button>&nbsp;<button onClick="return openChat();">Iniciar charla</button></td></tr>

        <tr><td align="right" style="padding-top: 4px;"><button onClick="return prev();" id="prevButton">anterior</button>&nbsp;<button onClick="return next();" id="nextButton">siguiente</button>&nbsp;<button onClick="window.close();">Cerrar</button></td></tr>
        
    </table>
    </form>
  </body>
</html>
