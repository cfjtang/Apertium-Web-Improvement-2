function translateto(langCode, listOfCodes, id) {
	document.getElementById('entry'+id).innerHTML=document.getElementById(langCode+id).innerHTML;
	document.getElementById('theTitle'+id).innerHTML=document.getElementById(langCode+'-title'+id).innerHTML;
	unselectButtons(id);
	hideApertiumNotes(listOfCodes,id);
	document.getElementById('note-'+langCode+id).className = "apertiumNote";
	var node = document.getElementById(langCode+'-button'+id).className = "selectedLang";
	return;
}

function unselectButtons(id) {
	var langs = document.getElementById('listOfLanguages'+id);
	for (var iNode = 0; iNode < langs.childNodes.length; iNode++ ) {
		var node = langs.childNodes[iNode];
		if( node.nodeName == "DIV") {
			node.className = "unselectedLang"; 
		}
	}
	return;
}

function showListOfLanguages(id) {
	document.getElementById('translateButton'+id).className="languages hidden";
	document.getElementById('listOfLanguages'+id).className="languages";
	return;
}

function hideApertiumNotes(listOfCodes, id) {
	var lcodes = listOfCodes.split(",");
	for(var i=0; i<lcodes.length; i++) {
		var thecode = lcodes[i];
		document.getElementById('note-'+thecode+id).className="apertiumNote hidden";
	}
	return;
}

function hideListOfLanguages(listOfCodes,id) {
	hideApertiumNotes(listOfCodes,id);	
	document.getElementById('translateButton'+id).className="languages";
	document.getElementById('listOfLanguages'+id).className="languages hidden";
	return;
}
