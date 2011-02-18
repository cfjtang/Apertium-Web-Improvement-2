//coding: utf-8
/*
	Apertium Web Post Editing tool
	Small module to intercept paste events
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

var paste_interceptor;

function initPasteEvent()
{
	//Setup paste event interceptor
	paste_interceptor = document.createElement("div");
	paste_interceptor.contentEditable = 'true';
	paste_interceptor.style.opacity = 0;
	paste_interceptor.style.width = 0;
	paste_interceptor.style.overflow = 'hidden';
	document.body.appendChild(paste_interceptor);
	paste_interceptor.caller = null;
	
	paste_interceptor.addEventListener('blur', function(e)
	{
		var text = paste_interceptor.innerHTML;
		
		paste_interceptor.innerHTML = '';
		paste_interceptor.caller.focus();
		
		//restore selection
		var selection = window.getSelection().getRangeAt(0);
		selection.setStart(paste_interceptor.saveStartContainer, paste_interceptor.saveStartOffset);
		selection.setEnd(paste_interceptor.saveEndContainer, paste_interceptor.saveEndOffset);
		
		paste_interceptor.caller.pasteHandler(text);
	}, false);
	
}

function addPasteEventListener(elt, pasteHandler)
{
	//setup paste event watcher on an element
	
	elt.pasteHandler = pasteHandler;
	
	elt.addEventListener('paste', function(){
		paste_interceptor.caller = elt;
		
		//save current selection
		var selection = window.getSelection().getRangeAt(0);
		paste_interceptor.saveStartContainer = selection.startContainer;
		paste_interceptor.saveStartOffset = selection.startOffset;
		paste_interceptor.saveEndContainer = selection.endContainer;
		paste_interceptor.saveEndOffset = selection.endOffset;
		
		//bring focus to the paste interceptor to get the pasted text and force blur immediately after
		paste_interceptor.focus();
		window.setTimeout('paste_interceptor.blur();', 1);
	}, false);
}