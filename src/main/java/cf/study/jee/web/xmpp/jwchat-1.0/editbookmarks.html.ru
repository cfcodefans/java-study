<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Редактировать закладки</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="shared.js"></script>
    <script src="switchStyle.js"></script>
    <script src="jsjac.js"></script>
    <script>
      <!--
var jid;
var srcW; // the source window with necessary data

/* doSub
 * send bookmarks to server for storage 
 */
function doSub() {
  var iq = new JSJaCIQ();
  iq.setType('set');
  var query = iq.setQuery('jabber:iq:private');
  var storage = query.appendChild(
	iq.buildNode('storage', {'xmlns': 'storage:bookmarks'}));

  for (var i=0; i<srcW.bookmarks.length; i++) {
    var item = storage.appendChild(iq.getDoc().createElement('conference'));
    item.setAttribute('name',srcW.bookmarks[i].name);
    item.setAttribute('jid',srcW.bookmarks[i].jid);
    if (srcW.bookmarks[i].autojoin && srcW.bookmarks[i].autojoin == '1')
      item.setAttribute('autojoin',srcW.bookmarks[i].autojoin);
    item.appendChild(iq.getDoc().createElement('nick')).appendChild(iq.getDoc().createTextNode(srcW.bookmarks[i].nick));
    if (typeof(srcW.bookmarks[i].pass) != 'undefined' && srcW.bookmarks[i].pass != '')
      item.appendChild(iq.getDoc().createElement('pass')).appendChild(iq.getDoc().createTextNode(srcW.bookmarks[i].pass));
  }	
  
  srcW.Debug.log(iq.getDoc().xml,2);
  
  srcW.con.send(iq);
  window.close();
}

/* saveBookmark
 * updates or saves bookmark to global bookmark array
 */
function saveBookmark() {
  var bookmark = new Object();

  if (document.forms[0].save_bookmark_as.value == '') {
    alert("Пожалуйста укажите имя для этой закладки");
    return false;
  }
  bookmark.name = document.forms[0].save_bookmark_as.value;

  bookmark.nick = (document.forms[0].nick.value == '') ? srcW.jid.substring(0,srcW.jid.indexOf('@')) : document.forms[0].nick.value;
  
  if (document.forms[0].group.value == '') {
    alert("Пожалуйста укажите название комнаты");
    return false;
  }

  if (document.forms[0].server.value == '') {
    alert("Пожалуйста укажите имя сервера");
    return false;
  }
  bookmark.jid = document.forms[0].group.value+"@"+document.forms[0].server.value;
  
  bookmark.autojoin = (document.forms[0].autojoin.checked) ? '1' : '0';
  
  bookmark.pass = document.forms[0].pass.value;
  
  if (document.forms[0].bookmark_selector.selectedIndex == 0) { // add new bookmark
    srcW.bookmarks[srcW.bookmarks.length] = bookmark;
  } else { // update bookmark
    srcW.bookmarks[document.forms[0].bookmark_selector.options[document.forms[0].bookmark_selector.selectedIndex].value] = bookmark;
  }
  
  updateBookmarks();
  return false;
}

function deleteBookmark() {
  var bOpts = document.forms[0].bookmark_selector.options;
  var bIdx = document.forms[0].bookmark_selector.selectedIndex;
  if (bIdx == 0)
    return;

  srcW.bookmarks = srcW.bookmarks.slice(0,bOpts[bIdx].value).concat(srcW.bookmarks.slice(bOpts[bIdx].value+1,bOpts.length));
	
  updateBookmarks();
  return false;
}


/* fill in form */
function bookmark_selected(el) {
  if (el.selectedIndex == 0) { // clear form
    document.forms[0].reset();
    return false;
  } 

  var bookmark= srcW.bookmarks[el.options[el.selectedIndex].value];
  
  document.forms[0].save_bookmark_as.value = bookmark.name;
  
  document.forms[0].nick.value = bookmark.nick;
	
  document.forms[0].group.value = bookmark.jid.substring(0,bookmark.jid.indexOf('@'));

  document.forms[0].server.value = bookmark.jid.substring(bookmark.jid.indexOf('@')+1);

  if (typeof(bookmark.pass) != 'undefined')
    document.forms[0].pass.value = bookmark.pass;
  else
    document.forms[0].pass.value = '';

  document.forms[0].autojoin.checked = (bookmark.autojoin == '1');

  return false;
}

/* get global bookmarks */
function updateBookmarks() {
  var bookmark_selector = document.getElementById('bookmark_selector');
  var optidx = 1;
  // clear list - don't remove first
  for (var i=1; i<bookmark_selector.options.length; i++)
    bookmark_selector.options[i] = null;

  // fill from global arr
  for (var i=0; i<srcW.bookmarks.length; i++)
    bookmark_selector.options[optidx++] = new Option(srcW.bookmarks[i].name,i);

  document.forms[0].reset();

  // update bookmarks in joingroupchat too
  if (srcW.frames['jwc_main'].groupw && srcW.frames['jwc_main'].groupw.updateBookmarks)
    srcW.frames['jwc_main'].groupw.updateBookmarks();
}


function init() {
  // determine source window
  if (opener.top.roster)
    srcW = opener.top;

  updateBookmarks();
}

function keyPressed(e) {
  if (e.keyCode == 13)
    return doSub();
  if (e.keyCode == 27)
    window.close();
  return true;
}

onkeydown = keyPressed;
onload = init;
      //-->
    </script>
    <script for="document" event="onkeydown()" language="JScript">
      <!--
      return keyPressed(window.event);
      //-->
    </script>
  </head>
  <body style="margin:8px;">
    <form name="sub" onsubmit="return doSub();">
      <table border="0" width="100%">
          <tr><td>Закладка</td><td><select id="bookmark_selector" onChange="return bookmark_selected(this);"><option value=''> - Выберите закладку - </option></select></td></tr>
          <tr><td colspan="2"><hr noshade size="1"></td></tr>
          <tr><td>Имя</td><td><input type="text" name="save_bookmark_as" size="32" style="width: 100%;"></td></tr>
          <tr><td colspan="2"><hr noshade size="1"></td></tr>
          <tr><td nowrap>Ник</td><td width="100%"><input type="text" name="nick" size="32" style="width:100%;"></td></tr>
          <tr><td nowrap>Комната</td><td width="100%"><input type="text" name="group" size="32" style="width:100%;"></td></tr>
          <tr><td nowrap>Сервер</td><td width="100%"><input type="text" name="server" size="32" style="width:100%;"></td></tr>
          <tr><td nowrap>Пароль</td><td width="100%"><input type="text" name="pass" size="32" style="width:100%;"></td></tr>
          <tr><td nowrap>Автоматически входить</td><td><input type="checkbox" name="autojoin"></td></tr>
      </table>
      <div align="right" id="buttonbox">
        <button onClick="return saveBookmark();">Add/Update</button>&nbsp;<button onClick="return deleteBookmark();">Delete</button>
      </div>
      <hr noshade size="1" size="100%">
      <div align="right" id="buttonbox">
        <button type="button" onClick="window.close();">Отменить</button>&nbsp;<button type="submit">Сохранить</button>
      </div>
    </form>
  </body>
</html>
