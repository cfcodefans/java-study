<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Registreren</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="shared.js"></script>
    <script src="switchStyle.js"></script>
    <script>
      <!--
function doSub() {};

function handleIQRegister(jabber) {
	srcW.Debug.log(jabber,0);
}

var srcW;
function init() {
	srcW = opener.parent.srcW;

	var roster = srcW.roster;
	srcW.fCBLoad("iq","?sid="+srcW.sid+"&to="+msgEscape(opener.parent.jid)+"&ns=jabber:iq:register",roster.getUserByJID(opener.parent.jid).chatW.frames.groupchatBottom.rw.handleIQRegister);
}

onload = init;
      //-->
    </script>
  <body style="margin:8px;">
    <form name="sub" onsubmit="return doSub();">
    </form>
  </body>
</html>
