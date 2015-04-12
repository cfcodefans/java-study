<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Roster</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="switchStyle.js"></script>
    <script language="JavaScript1.2">
function toggleGrp(name) {
  parent.top.user.roster.toggleGrp(name);
}

function updateStyleIE() {
  try {
    if (parent.top.user && parent.top.user.roster)
      parent.top.user.roster.updateStyleIE();
  } catch(e) {}
}
onresize = updateStyleIE;
    </script>
    <style>
    body { background-color: white; }
    </style>
  </head>
  <body>
    <div id="ie5menu" class="skin0" onMouseover="highlightie5(event)" onMouseout="lowlightie5(event)" onClick="jumptoie5(event)" display:none>
      <nobr><div class="menuitems" func="user.roster.openChat">Chat</div></nobr>
    <hr noshade size=1>
    <nobr><div class="menuitems" func="changeRole" param="'none',1">Kicken</div></nobr>
    <nobr><div class="menuitems" func="changeAffiliation" param="'outcast',1">Bannen</div></nobr>
    <nobr><div class="menuitems" func="changeRole" param="'participant'">Medezeggenschap verlenen</div></nobr>
    <nobr><div class="menuitems" func="changeRole" param="'visitor'">Medezeggenschap intrekken</div></nobr>
    <nobr><div class="menuitems" func="changeAffiliation" param="'member'">Deelnemerschap verlenen</div></nobr>
    <nobr><div class="menuitems" func="changeAffiliation" param="'none'">Lidmaatschap intrekken</div></nobr>
    <nobr><div class="menuitems" func="changeRole" param="'moderator'">Moderatorsprivilege verlenen</div></nobr>
    <nobr><div class="menuitems" func="changeRole" param="'participant'">Moderatorsprivilege intrekken</div></nobr>
    <nobr><div class="menuitems" func="changeAffiliation" param="'admin'">Beheerdersprivilege verlenen</div></nobr>
    <nobr><div class="menuitems" func="changeAffiliation" param="'member'">Beheerdersprivilege intrekken</div></nobr>
    <hr noshade size=1>
    <nobr><div class="menuitems" func="srcW.openUserInfo">Info over contactpersoon</div></nobr>
  </div>
    
    <div id="roster" class="roster">
    </div>
    
    <script language="JavaScript1.2">
      
                                                                                                  //set this variable to 1 if you wish the URLs of the highlighted menu to be displayed in the status bar
var display_url=0;

var ie5=document.all&&document.getElementById;
var ns6=document.getElementById&&!document.all;
if (ie5||ns6)
  var menuobj=document.getElementById("ie5menu");
      
var selobj;
function showmenuie5(e){
  var firingobj=ie5? event.srcElement : e.target;
  while (firingobj && (!firingobj.className || (firingobj.className != "rosterUser" && firingobj.className != "rosterUserSelected")))
    firingobj = firingobj.parentNode;
  if (!firingobj || (firingobj.className != "rosterUser" && firingobj.className != "rosterUserSelected"))
    return false;
  
  selobj = firingobj;
  selectUser(selobj);
  //Find out how close the mouse is to the corner of the window
  var rightedge=ie5? document.body.clientWidth-event.clientX : window.innerWidth-e.clientX;
  var bottomedge=ie5? document.body.clientHeight-event.clientY : window.innerHeight-e.clientY;
  
  //if the horizontal distance isn't enough to accomodate the width of the context menu
  if (rightedge<menuobj.offsetWidth) {
    //move the horizontal position of the menu to the left by it's width
    menuobj.style.left=ie5? document.body.scrollLeft+event.clientX-menuobj.offsetWidth : window.pageXOffset+e.clientX-menuobj.offsetWidth;
    if (menuobj.style.left < "2px")
      menuobj.style.left = "2px";
  }
  else
    //position the horizontal position of the menu where the mouse was clicked
    menuobj.style.left=ie5? document.body.scrollLeft+event.clientX : window.pageXOffset+e.clientX
      
      //same concept with the vertical position
      if (bottomedge<menuobj.offsetHeight) {
        menuobj.style.top=ie5? document.body.scrollTop+event.clientY-menuobj.offsetHeight : window.pageYOffset+e.clientY-menuobj.offsetHeight
        if (menuobj.style.top < "2px")
          menuobj.style.top = "2px";
      }
      else
        menuobj.style.top=ie5? document.body.scrollTop+event.clientY : window.pageYOffset+e.clientY
          
          menuobj.style.visibility="visible"
          return false
          }

function hidemenuie5(e){
  menuobj.style.visibility="hidden"
    }

function highlightie5(e){
  var firingobj=ie5? event.srcElement : e.target;
  if (firingobj.className=="menuitems"||ns6&&firingobj.parentNode.className=="menuitems"){
    if (ns6&&firingobj.parentNode.className=="menuitems") firingobj=firingobj.parentNode; //up one node
    firingobj.style.backgroundColor="highlight";
    firingobj.style.color="white";
    if (display_url==1)
      window.status=event.srcElement.url;
  }
}

function lowlightie5(e){
  var firingobj=ie5? event.srcElement : e.target;
  if (firingobj.className=="menuitems"||ns6&&firingobj.parentNode.className=="menuitems"){
    if (ns6&&firingobj.parentNode.className=="menuitems") firingobj=firingobj.parentNode; //up one node
    firingobj.style.backgroundColor="";
    firingobj.style.color="black";
    window.status='';
  }
}

function jumptoie5(e){
  var firingobj=ie5? event.srcElement : e.target;
  if (firingobj.className=="menuitems"||ns6&&firingobj.parentNode.className=="menuitems"){
    if (ns6&&firingobj.parentNode.className=="menuitems") firingobj=firingobj.parentNode;
    if (firingobj.getAttribute("func")) {
      var jid = selobj.id.substring(0,selobj.id.lastIndexOf("/"));
      eval("parent.top."+firingobj.getAttribute("func")+"('"+jid+"',"+firingobj.getAttribute("param")+")");
    }
  }
}

var lastUserSelected;
function selectUser(el) {
  if(lastUserSelected)
    lastUserSelected.className = "rosterUser";
  el.className = "rosterUserSelected";
  lastUserSelected = el;
}

function userClicked(el,jid) {
  selectUser(el);
  return parent.top.user.roster.openChat(jid);
}
if (ie5||ns6){
  menuobj.style.display='';
  document.oncontextmenu=showmenuie5;
  document.onclick=hidemenuie5;
}
    </script>
  </body>
</html>
