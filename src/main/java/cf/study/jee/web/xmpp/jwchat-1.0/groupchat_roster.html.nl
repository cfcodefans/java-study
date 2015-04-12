<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <script src="switchStyle.js"></script>
    <script src="shared.js"></script>
		<script>

var editListW = new Array();
function listSelected(selector) {
	if (selector.selectedIndex == 0)
		return;

	var opt = selector.options[selector.selectedIndex];
	if (!editListW[opt.value] || editListW[opt.value].closed)
		editListW[opt.value] = open("groupchat_modifylist.html?"+opt.value,makeWindowName(opt.value+parent.top.jid),"width=400,height=400,resizable=yes,scrollbars=yes");
	editListW[opt.value].focus();
}

function updateMe() {
	var role = parent.top.roster.me.role;
	var affiliation = parent.top.roster.me.affiliation;

	if (role == 'moderator' || affiliation == 'admin' || affiliation == 'owner') {
		/* build list selector */
		var list_selector = document.getElementById('list_selector');
		
		// clear list
		for (var i=1; i<list_selector.options.length; i++)
			list_selector.options[i] = null;
		
		// add items based on affiliation
		var optidx = 1;
		list_selector.options[optidx++] = new Option("Medezeggenschap","role=participant&http://jabber.org/protocol/muc#admin&"+msgEscape("Medezeggenschaplijst"));
		if (affiliation == 'admin' || affiliation == 'owner') {
			list_selector.options[optidx++] = new Option("Bannen","affiliation=outcast&http://jabber.org/protocol/muc#admin&"+msgEscape("Lijst met gebande chatters"));
			list_selector.options[optidx++] = new Option("Lid","affiliation=member&http://jabber.org/protocol/muc#admin&"+msgEscape("Ledenlijst"));
			list_selector.options[optidx++] = new Option("Moderator","role=moderator&http://jabber.org/protocol/muc#admin&"+msgEscape("Lijst met moderators"));
		}
		// add even more
		if (affiliation == 'owner'){
			list_selector.options[optidx++] = new Option("Beheerder","affiliation=admin&http://jabber.org/protocol/muc#owner&"+msgEscape("Beheerderslijst"));
			list_selector.options[optidx++] = new Option("Eigenaar","affiliation=owner&http://jabber.org/protocol/muc#owner&"+msgEscape("Lijst met eigenaars"));
		}
		// show list
		document.getElementById('list_editor').style.display = '';
	}
	else
		document.getElementById('list_editor').style.display = 'none';

}

function cleanUp() {
	// close windows
	for (var i=0;i<editListW.length; i++)
		if (editListW[i] && !editListW[i].closed)
			editListW[i].close();
} 

onunload = cleanUp;
		</script>
  </head>
  <body style="margin:8px;">
		<table width="100%" height="100%" style="margin: 0px; padding: 0px; border: 0;">
				<tr>
					<td height="100%" width="100%"><iframe src="groupchat_iroster.html" id="groupchatIRoster" name="groupchatIRoster" scrolling="auto" style="width:100%;height:100%;border:2px groove;" frameborder=0></iframe></td></tr>
				<tr id="list_editor" style="display:none;">
					<td width="100%"><div style="font-size: 0.8em;">Lijst met veranderingen</div>
						<select id="list_selector" style="width: 100%; font-size: 0.8em;" onChange="listSelected(this);">
							<option value=''>&nbsp;</option></td></tr>
			</table>
  </body>
</html>
