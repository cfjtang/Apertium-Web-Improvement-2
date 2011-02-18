//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for text editing interface
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

var text_in_js_on;
var text_out_js_on;
var text_in_js_off;
var text_out_js_off;

function initJSEditors()
{
	document.mainform.onsubmit = prepareSubmit;
	
	text_in_js_on = document.getElementById('text_in_js_on');
	text_out_js_on = document.getElementById('text_out_js_on');
	text_in_js_off = document.getElementById('text_in_js_off');
	text_out_js_off = document.getElementById('text_out_js_off');
	
	text_in_js_on.style.display = '';
	text_out_js_on.style.display = '';
	text_in_js_off.style.display = 'none';
	text_out_js_off.style.display = 'none';
	
	text_in_js_on.setAttribute("spellcheck", "false");
	text_out_js_on.setAttribute("spellcheck", "false");
	
	//build words lists for logging
	text_in_js_on.text_object = buildWordList(text_in_js_on);
	text_out_js_on.text_object = buildWordList(text_out_js_on);
	
	//prepare text logs
	text_in_js_on.final_log = '';
	text_out_js_on.final_log = '';
	
	//display the log button
	document.getElementById('get_logs').style.display = '';
	
	//Set text fields to get their paste event intercepted
	addPasteEventListener(text_in_js_on, function(text){
		var selRange = window.getSelection().getRangeAt(0);
		var correctRange = getCorrectedRange(selRange, text_in_js_on);
		replaceCharacters(correctRange.startContainer, correctRange.startOffset, correctRange.endContainer, correctRange.endOffset, strip_tags(text), selRange);
	});
	
	addPasteEventListener(text_out_js_on, function(text){
		var selRange = window.getSelection().getRangeAt(0);
		var correctRange = getCorrectedRange(selRange, text_out_js_on);
		replaceCharacters(correctRange.startContainer, correctRange.startOffset, correctRange.endContainer, correctRange.endOffset, strip_tags(text), selRange);
	});
}

function prepareSubmit()
{
	/*
	text_in_js_on.removeChild(text_in_js_on.lastChild); //delete trailing BR
	text_in_js_off.value = strip_tags(text_in_js_on.innerHTML.replace(/<br(.*?)>/ig, '\n')); //convert all other BRs to true new lines
	
	text_out_js_on.removeChild(text_out_js_on.lastChild); 
	text_out_js_off.value = strip_tags(text_out_js_on.innerHTML.replace(/<br(.*?)>/ig, '\n'));
	*/
	
	text_in_js_off.value = getCleanValue(text_in_js_on);
	text_out_js_off.value = getCleanValue(text_out_js_on);
}

function getCleanValue(elt)
{
	elt.removeChild(elt.lastChild); //delete trailing BR
	var result = strip_tags(elt.innerHTML.replace(/<br(.*?)>/ig, '\n'), '<hr>'); //convert all other BRs to true new lines
	
	//reappend trailing BR
	var newChild = document.createElement("br");
	newChild.className = "nodelete";
	elt.appendChild(newChild);
	
	return result;
}

/*
FUNCTIONS FOR EVENT MANAGEMENT
*/

var TE_eventMode = 'idle';

function TE_handleClick(event)
{
	switch(TE_eventMode)
	{
		case 'idle' :
		
			if(!window.getSelection().getRangeAt(0).collapsed) // if user is selecting text, forget about the event
			{
				return true;
			}
			
			if(event.target.className.lastIndexOf('_mistake') == event.target.className.length - 'mistake'.length - 1)
			{
				if(event.target.getAttribute('data-suggestions') != '')
				{
					displaySuggestionsList(event.target);
					
					TE_eventMode = 'suggListOpened';
				}
				return false;
			}
			else if(event.target.className.match(/\bdelete_row\b/))
			{
				event.target.parentNode.parentNode.removeChild(event.target.parentNode);
				//event.target.setAttribute('onclick' , 'return false;'); //disable the form submitting
				return false;
			}
			else if(event.target.id == 'pretrans_add')
			{
				var newElt = document.createElement('li');
				newElt.innerHTML = document.getElementById('list_elements_models').getElementsByTagName('li')[0].innerHTML;
				document.getElementById('pretrans_list').appendChild(newElt);
				//event.target.setAttribute('onclick' , 'return false;'); //disable the form submitting
				return false;
			}
			else if(event.target.id == 'posttrans_add')
			{
				var newElt = document.createElement('li');
				newElt.innerHTML = document.getElementById('list_elements_models').getElementsByTagName('li')[1].innerHTML;
				document.getElementById('posttrans_list').appendChild(newElt);
				//event.target.setAttribute('onclick', 'return false;'); //disable the form submitting
				return false;
			}
			
			break;
		
		case 'suggListOpened' :
			var sugList = document.getElementById('suggestions_list');
			
			if(event.target.className == 'suggestion_element')
			{
				//let's replace the mistake by the chosen correction
				var errorElement = sugList.parentErrorElement;
				errorElement.className = "";
				var textNode = getNextTextNode(errorElement);
				
				replaceCharacters(textNode, 0, textNode, textNode.nodeValue.length, event.target.textContent);
				/*/
				var textNode = document.createTextNode(event.target.textContent);
				
				var errorElement = sugList.parentErrorElement;
				errorElement.parentNode.replaceChild(textNode, errorElement);
				//*/
				
				//then close the suggestion list
				document.body.removeChild(sugList);
				TE_eventMode = 'idle';
				
				return false;
			}
			else if(event.target.className == 'suggestion_search_link')
			{
				//open new window/tab with the definition
				window.open(event.target.href, 'definition');
				
				//prevent the natural link from reacting
				return false;
			}
			else if(event.target.parentNode.className == 'suggestion_search_link') //click event triggers on the child img
			{
				//open new window/tab with the definition
				window.open(event.target.parentNode.href, 'definition');
				
				//prevent the natural link from reacting
				return false;
			}
			else
			{
				//click outside of the suggestion list : just close it
				document.body.removeChild(sugList);
				TE_eventMode = 'idle';
				
				return true;
			}
			
			break;
	}
}

function displaySuggestionsList(targetObject)
{
	var suggestions = targetObject.getAttribute('data-suggestions').split('#');
	
	//generate the list
	var sugList = document.createElement("ul");
	sugList.id = 'suggestions_list';
	sugList.parentErrorElement = targetObject; //remember the error that triggered the list
	
	for(var i = 0; i < suggestions.length; i++)
	{
		//Create a list element containing the suggestion
		var listElt = document.createElement("li");
		listElt.textContent = suggestions[i];
		listElt.className = 'suggestion_element';
		
		sugList.appendChild(listElt);
		
		//append to it a link for dictionary search, if on a spelling mistake
		if(targetObject.className.match(/\bspelling_mistake\b/g))
		{
			var field = (targetObject.parentNode.id == 'text_in_js_on') ? 'src' : 'dst';
			if(window["dictionary_"+field].value != '')
			{
				var dictLink = document.createElement("a");
				dictLink.innerHTML = '<img alt="Definition" src="images/search.png" />';
				dictLink.href = getDictionaryLink(suggestions[i], field);
				dictLink.className = 'suggestion_search_link';
				listElt.appendChild(dictLink);
			}
		}
	}
	
	//determine the position of the mistake on screen
	var x = y = 0;
	var currentObj = targetObject;
	
	do
	{
		x += currentObj.offsetLeft;
		if(currentObj.offsetParent && currentObj.offsetParent.scrollLeft) 
		{
			x -= currentObj.offsetParent.scrollLeft; 
		}

		y += currentObj.offsetTop;
		if(currentObj.offsetParent && currentObj.offsetParent.scrollTop) 
		{
			y -= currentObj.offsetParent.scrollTop; 
		}
	}
	while(currentObj = currentObj.offsetParent)
	
	//place the list there
	document.body.appendChild(sugList);
	sugList.style.position = 'absolute';
	sugList.style.left = x+'px';
	sugList.style.top = (y + targetObject.clientHeight)+'px';
}




function TE_handleKeyPress(event, charInsert)
{
	//given a keyboard or text editing event, and a character to insert. 
	//If charInsert is a string, insert it; otherwise, read the keycode of the event 
	var container;
	
	//In which text area did the event occur ?
	if(event.target == text_in_js_on || nodeContains(text_in_js_on, event.target))
	{
		container = text_in_js_on;
	}
	else if(event.target == text_out_js_on || nodeContains(text_out_js_on, event.target))
	{
		container = text_out_js_on;
	}
	else
	{
		//If it occurred somewhere else, we don't care : let the event run as it should
		return true;
	}
	
	
	//get the range of the current selection
	var sel = window.getSelection();
	var selRange = sel.getRangeAt(0);
	var finalRange = getCorrectedRange(selRange, container);
	
	//do character insertion if needed, react to keyCode otherwise
	if(charInsert)
	{
		if(finalRange.collapsed)
		{
			insertCharacter(finalRange.endContainer, finalRange.endOffset, charInsert, selRange);
		}
		else
		{
			replaceCharacters(finalRange.startContainer, finalRange.startOffset, finalRange.endContainer, finalRange.endOffset, charInsert, selRange);
		}
		
		//update the selection with the edited range
		sel.removeAllRanges();
		sel.addRange(selRange);
		
		//The event has been managed : do not trigger its default behaviour
		event.preventDefault();
		return false;
	}
	else if(charInsert === false)
	{
		//the pressed key didn't generate a character : special events to manage
		switch(event.keyCode)
		{
			case 46 : //delete key : can the element following the caret be deleted ?
				
				if(finalRange.collapsed)
				{
					if(finalRange.endOffset == finalRange.endContainer.nodeValue.length) //Right edge of the text node
					{
						var nextElt = finalRange.endContainer.nextSibling;
						if(nextElt && nextElt.nodeType == 1
							&& nextElt.tagName.toLowerCase() == "br" && nextElt.className != "nodelete") //followed by a br html element
						{
							//we can delete the html element
							deleteNode(nextElt);
						}
						else if( (nextElt = getNextTextNode(finalRange.endContainer, container)) && nextElt.nodeValue.length > 0 ) //followed by another non-empty text node
						{
							deleteCharacter(nextElt, 0, selRange); //delete the first char of that node
						}
					}
					else //caret is inside the node : delete the corresponding character
					{
						deleteCharacter(finalRange.endContainer, finalRange.endOffset, selRange);
					}
				}
				else
				{
					replaceCharacters(finalRange.startContainer, finalRange.startOffset, finalRange.endContainer, finalRange.endOffset, '', selRange);
				}
				
				//The event has been managed : do not trigger its default behaviour
				event.preventDefault();
				return false;
				
				break;
				
			case 8 : //backspace key : can the element before the caret be deleted ?
			
				if(selRange.collapsed)
				{
					if(finalRange.endOffset == 0) //Left edge of the text node
					{
						var prevElt = finalRange.endContainer.previousSibling;
						if(prevElt && prevElt.nodeType == 1
							&& prevElt.tagName.toLowerCase() == "br" && nextElt.className != "nodelete") //preceded by a br html element
						{
							//we can delete the html element
							deleteNode(prevElt);
						}
						else if( (prevElt = getPreviousTextNode(finalRange.endContainer, container)) && prevElt.nodeValue.length > 0) //preceded by another non-empty text node
						{
							deleteCharacter(prevElt, prevElt.nodeValue.length - 1, selRange); //delete the last char of that node
						}
					}
					else //caret is inside the node : delete the corresponding character
					{
						deleteCharacter(finalRange.endContainer, finalRange.endOffset - 1, selRange);
					}
				}
				else
				{
					replaceCharacters(finalRange.startContainer, finalRange.startOffset, finalRange.endContainer, finalRange.endOffset, '', selRange);
				}
				
				//The event has been managed : do not trigger its default behaviour
				event.preventDefault();
				return false;
			
				break;
			
			case 13 : //enter key : new line insertion
			
				if(finalRange.collapsed)
				{
					var cut_contents = finalRange.endContainer.nodeValue.substr(finalRange.endOffset);
					
					replaceCharacters(finalRange.endContainer, finalRange.endOffset, finalRange.endContainer, finalRange.endContainer.nodeValue.length, '');
					
					var newBr = document.createElement("br");
					var newNode = document.createTextNode("");
					
					finalRange.endContainer.parentNode.insertBefore(newBr, finalRange.endContainer.nextSibling);
					newBr.parentNode.insertBefore(newNode, newBr.nextSibling);
					container.text_object.insertNodeAfter(newNode, finalRange.endContainer);
					
					replaceCharacters(newNode, 0, newNode, 0, cut_contents);
					
					//set the cursor on the new text node
					selRange.setStart(newNode, 0);
				}
				
				//The event has been managed : do not trigger its default behaviour
				event.preventDefault();
				return false;
				
				break;
			
			default :
				
				return true;
				break;
				
		}
	}
	
	return true;
}



/*
FUNCTIONS FOR BASIC TEXT MANIPULATION
*/


function insertCharacter(node, offset, character, selRange)
{
	logInsertChar(node, offset, character);
	node.nodeValue = node.nodeValue.substr(0, offset) + character + node.nodeValue.substr(offset); //insert the character in the string
	
	if(selRange)
	{
		//if selrange given, restore it
		selRange.setStart(node, offset + character.length);
	}
}

function deleteCharacter(node, offset, selRange)
{
	logDeleteChar(node, offset);
	node.nodeValue = node.nodeValue.substr(0, offset) + node.nodeValue.substr(offset + 1); //delete char at position offset
	
	if(selRange)
	{
		//if selrange given, restore it
		selRange.setStart(node, offset);
	}
}

function replaceCharacters(startNode, startOffset, endNode, endOffset, string, selRange)
{
	//delete all characters between the two positions
	var currentNode = endNode;
	var currentOffset = endOffset;
	while(currentNode != startNode || currentOffset != startOffset)
	{
		if(currentOffset <= 0)
		{
			currentNode = currentNode.previousNode;
			currentOffset = currentNode.nodeValue.length;
		}
		else
		{
			deleteCharacter(currentNode, currentOffset - 1, selRange); //delete the character
			
			currentOffset -= 1;
		}
	}
	
	//then insert the characters
	if(string != "")
	{
		var lexicalElts = string.split(regexp_split_lexical); //split the tring into its lexical elements
		
		currentOffset = startOffset;
		//Go through all lexical elements of the string and insert them at the start point
		for(var i = 0; i < lexicalElts.length; i++)
		{
			insertCharacter(startNode, currentOffset, lexicalElts[i], selRange);
			currentOffset += lexicalElts[i].length;
		}
	}
}

function deleteNode(node)
{
	node.parentNode.removeChild(node);
}


/*
FUNCTION TO GET PROPER START AND END NODES
*/
function getCorrectedRange(selRange, container)
{
	var resultRange = new Object();
	
	resultRange.collapsed = selRange.collapsed;
	
	//position the selection end on a text node
	if(selRange.endContainer == container)
	{
		resultRange.endContainer = getPreviousTextNode(selRange.endContainer.childNodes[selRange.endOffset], container, true);
	}
	else
	{
		resultRange.endContainer = getPreviousTextNode(selRange.endContainer, container, true);
	}
	resultRange.endOffset = selRange.endOffset;
	
	if(!resultRange.endContainer) //if no such node, insert a new one in front of the text
	{
		resultRange.endContainer = document.createTextNode('');
		container.insertBefore(resultRange.endContainer, container.childNodes[0]);
		container.text_object.insertNodeAfter(resultRange.endContainer, null);
	}
	if(resultRange.endContainer != selRange.endContainer)
	{
		resultRange.endOffset = resultRange.endContainer.nodeValue.length;
	}
	
	if(!selRange.collapsed)
	{
		//position the selection start on a text node
		if(selRange.startContainer == container)
		{
			resultRange.startContainer = getNextTextNode(selRange.startContainer.childNodes[selRange.startOffset], container, true);
		}
		else
		{
			resultRange.startContainer = getNextTextNode(selRange.startContainer, container, true);
		}
		resultRange.startOffset = selRange.startOffset;
		
		if(resultRange.startContainer != selRange.startContainer)
		{
			resultRange.startOffset = 0;
		}
	}
	else
	{
		resultRange.startContainer = resultRange.endContainer;
		resultRange.startOffset = resultRange.endOffset;
	}
	
	return resultRange;
}



//auxialiary functions

function strip_tags (str, allowed_tags) {
    // Strips HTML and PHP tags from a string  
    // version: 1006.1915
    // discuss at: http://phpjs.org/functions/strip_tags    // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    var key = '', allowed = false;
    var matches = [];    var allowed_array = [];
    var allowed_tag = '';
    var i = 0;
    var k = '';
    var html = ''; 
    var replacer = function (search, replace, str) {
        return str.split(search).join(replace);
    };
     // Build allowes tags associative array
    if (allowed_tags) {
        allowed_array = allowed_tags.match(/([a-zA-Z0-9]+)/gi);
    }
     str += '';
 
    // Match tags
    matches = str.match(/(<\/?[\S][^>]*>)/gi);
     // Go through all HTML tags
    for (key in matches) {
        if (isNaN(key)) {
            // IE7 Hack
            continue;        }
 
        // Save HTML tag
        html = matches[key].toString();
         // Is tag not in allowed list? Remove from str!
        allowed = false;
 
        // Go through all allowed tags
        for (k in allowed_array) {            // Init
            allowed_tag = allowed_array[k];
            i = -1;
 
            if (i != 0) { i = html.toLowerCase().indexOf('<'+allowed_tag+'>');}            if (i != 0) { i = html.toLowerCase().indexOf('<'+allowed_tag+' ');}
            if (i != 0) { i = html.toLowerCase().indexOf('</'+allowed_tag)   ;}
 
            // Determine
            if (i == 0) {                allowed = true;
                break;
            }
        }
         if (!allowed) {
            str = replacer(html, "", str); // Custom replace. No regexing
        }
    }
     return str;
}

