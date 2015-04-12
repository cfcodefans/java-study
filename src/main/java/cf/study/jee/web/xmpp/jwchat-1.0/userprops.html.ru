<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Свойства пользователя</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="shared.js"></script>
    <script src="switchStyle.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--
var jid; 
var srcW;
var user; 
var allgrpbox; 
var curgrpbox;

function submitClicked() {
  var iq = new JSJaCIQ();
  iq.setType('set');
  var query = iq.setQuery('jabber:iq:roster');

  var aItem = query.appendChild(iq.getDoc().createElement('item'));
  aItem.setAttribute('jid',jid);
  aItem.setAttribute('name',document.userprops.nickname.value);

  if (curgrpbox.length > 0) {
    for (var i=0; i<curgrpbox.length; i++)
      aItem.appendChild(iq.getDoc().createElement('group')).appendChild(iq.getDoc().createTextNode(curgrpbox.options[i].value));
  }

  srcW.con.send(iq);
  window.close();
}

function addgroup() {
  if (document.userprops.newgrp.value != '') {
    for (var i=0; i<curgrpbox.length; i++) {
      if (curgrpbox.options[i].value == document.userprops.newgrp.value)
        return false; // nothin to do
    }
    curgrpbox.options[curgrpbox.length] = new Option(document.userprops.newgrp.value,document.userprops.newgrp.value);
  }
  return false;
}

function remgroup() {
  if(curgrpbox.selectedIndex < 0)
    return false;
  curgrpbox.options[curgrpbox.selectedIndex] = null;
  return false;
}

function setNewGrp(idx) {
  document.userprops.newgrp.value = allgrpbox.options[idx].value;
}

function init() {
  // determine source window
  if (opener.roster)
    srcW = opener.top;
  else
    srcW = opener.opener.top;
  
  getArgs();
  jid = passedArgs['jid'];
  document.title = "Редактировать свойства "+jid;
  document.getElementById('nickjid').innerHTML = jid;
  document.getElementById('groupjid').innerHTML = jid;
  user = srcW.roster.getUserByJID(jid);
  curgrpbox = document.userprops.curgrps;
  allgrpbox = document.userprops.allgrps;
  for (var i=0; i<user.groups.length; i++) {
    if (user.groups[i] != '') {
      curgrpbox.options[curgrpbox.length] = new Option(user.groups[i],user.groups[i]);
    }
  }
  
  for (var i=0; i<srcW.roster.groups.length; i++) {
    if (srcW.roster.groups[i].name != "Не в группе") {
      allgrpbox.options[allgrpbox.length] = new Option(srcW.roster.groups[i].name,srcW.roster.groups[i].name);
    }
  }

  document.userprops.nickname.value = user.name;
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
  </head>

  <body style="margin: 8px;">
    <form name="userprops">
      <fieldset>
        <legend>Редактировать ник для <span id="nickjid"></span></legend>
        <table border=0 cellspacing=0 cellpadding=0 width="100%">
            <tr>
              <td nowrap>Ник:&nbsp;</td><td width="100%"><input type="text" name="nickname" style="width:100%"></td></tr>
        </table>
      </fieldset>
      <br>
      <fieldset>
        <legend>Редактировать группы для <span id="groupjid"></span></legend>
        <table>
            <tr>
              <td>
                <fieldset>
                  <legend>Доступные группы</legend>
                  <table border=0 cellspacing=0 cellpadding=0 width="100%">
                      <tr>
                        <tr><td>Группа:&nbsp;</td><td width="100%"><input type="text" name="newgrp"></td></tr>
                        <tr><td colspan=2 height=10></td></tr>
                        <tr>
                          <td colspan=2 width="100%;"><select size="9" name="allgrps" style="width:100%" onChange="setNewGrp(this.selectedIndex);"></select></td></tr>
                  </table>
                </fieldset>
              </td>
              <td><button onClick="return addgroup();">&gt;</button><br><button onClick="return remgroup();">&lt;</button></td>
              <td>
                <fieldset>
                  <legend>Текущие группы</legend>
                  <select size="11" name="curgrps" style="width:165px"></select>
                </fieldset>
              </td>
            </tr>
        </table>
      </fieldset>
      
      <hr noshade size="1" size="100%">
      <div align="right" id="buttonbox">
        <button onClick="window.close();">Отменить</button>&nbsp;<button onClick="return submitClicked();">ОК</button>
      </div>
    </form>
  </body>
</html>
