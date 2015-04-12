<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
    <title>Invite Users</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="switchStyle.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--

function doSub() {
	var users_selector = document.getElementById('users_selector');

	for (var i=0; i < users_selector.options.length; i++) {
		if (users_selector.options[i].selected) {
		  var aMessage = new JSJaCMessage();
		  aMessage.setTo(opener.parent.jid);
		  var x = aMessage.appendNode('x', {'xmlns': 'http://jabber.org/protocol/muc#user'});
		  var aNode = x.appendChild(aMessage.getDoc().createElement('invite'));
		  aNode.setAttribute('to',users_selector.options[i].value);
		  if (document.forms[0].reason.value != '')
			aNode.appendChild(aMessage.getDoc().createElement('reason')).appendChild(aMessage.getDoc().createTextNode(document.forms[0].reason.value));
		  srcW.Debug.log(aMessage.getDoc().xml);
		  srcW.con.send(aMessage);
		}
	}

	window.close();
}

var srcW;
function init() {
	srcW = opener.parent.srcW;

	// fill selector with users from main roster
	var users_selector = document.getElementById('users_selector');
	var optidx = 0;
	for (var i=0; i<srcW.roster.users.length; i++) {
		if (typeof(srcW.roster.users[i].roster) != 'undefined' || srcW.isGateway(srcW.roster.users[i].jid)) // skip groupchats and gateways
			continue;
		users_selector[optidx++] = new Option(srcW.roster.users[i].name,srcW.roster.users[i].jid);
	}
	
	document.title = "Пригласить пользователей в "+opener.parent.jid;
}

function keyPressed(e) {
  if (e.ctrlKey && e.keyCode == 13)
    return doSub();
  else if (e.keyCode == 27)
    close();
}

onkeydown = keyPressed;
onload = init;
      //-->
    </script>
    <script for="document" event="onkeydown()" language="JScript">
      <!--
       if (window.event.ctrlKey && window.event.keyCode == 13)
         return doSub();
       if (window.event.keyCode == 27)
          window.close();
      //-->
    </script>
  </head>
  <body style="margin: 8px;">
		<table width="100%" height="100%">
			<form name="invite_form">
				<tr>
					<td valign=top nowrap>Выбрать пользователей:</td><td align=left width="100%"><select name="users_selector" id="users_selector" size=3 multiple style="width:100%;"></select></td>
				</tr>
				<tr><td colspan=2>Причина:</td></tr>
				<tr><td width="100%" height="100%" colspan=2><textarea name="reason" style="width:100%;height:100%"></textarea></td></tr>
				<tr><td colspan=2><hr noshade size=1></td></tr>
				<tr>
					<td align=right colspan=2>
						<button type=submit onClick="return doSub();">Пригласить</button>
					</td>
				</tr>
			</form>
		</table>
  </body>
</html>
