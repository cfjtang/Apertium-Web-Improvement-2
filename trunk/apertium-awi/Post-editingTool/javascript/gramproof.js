//coding: utf-8
/* Apertium Web Post Editing Tool
 * Function for Spell checking and Grammar checking module
 *
 * Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 * Mentors : Arnaud Vié, Luis Villarejo
 *
 * Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 * Mentors : Luis Villarejo, Mireia Farrús
 */

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