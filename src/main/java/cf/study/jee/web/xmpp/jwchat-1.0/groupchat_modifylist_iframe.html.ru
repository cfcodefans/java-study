<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>JWChat - Изменить список</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <script src="switchStyle.js"></script>
		<script>

var ie5 = document.all&&document.getElementById;

function init() {
	var modTable = document.getElementById('modTable');
	
	if (parent.top.srcW.is.ie) {
		modTable.cellSpacing = 0;
		modTable.cellPadding = 0;
		modTable.border = 0;
		modTable.width = '100%';	
	}

	var rows = modTable.getElementsByTagName("TBODY").item(0).childNodes;
	for (var i=0; i<rows.length; i++) {
		rows[i].onmouseover = highlightRow;
		rows[i].onmouseout = unhighlightRow;
		rows[i].onclick = rowClicked;
		rows[i].title = "щёлкните для выбора пользователя";
	}
}
function highlightRow(e) {
 	var row = ie5 ? event.srcElement.parentNode : e.target.parentNode;
	row.className = 'highlighted';
}
function unhighlightRow(e) {
 	var row = ie5 ? event.srcElement.parentNode : e.target.parentNode;
	if (row != selectedRow)
		row.className = '';
}

var selectedRow;
function rowClicked(e) {
	if (selectedRow)
		selectedRow.className = '';
	else { 
 		parent.document.getElementById('delete_button').disabled = false;
	}
 	selectedRow = ie5 ? event.srcElement.parentNode : e.target.parentNode;
	selectedRow.className = 'highlighted';
}
		</script>

		<style type="text/css">
			body { background-color: white; }
			th { 
			font-size: 0.8em; 
			border-bottom: 1px solid black;
			padding: 2px;
			}
			td {
			border-bottom: 1px solid black;
			padding: 2px;
			cursor: default;
			}
			tr.highlighted {
			color: highlighttext;
			background-color: highlight;
			}
		</style>
  </head>
  <body id="modifylist">
  </body>
</html>
