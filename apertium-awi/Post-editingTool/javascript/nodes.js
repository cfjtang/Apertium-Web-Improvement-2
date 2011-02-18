//coding: utf-8
/*
	Apertium Web Post Editing tool
	Auxiliary functions for node manipulations
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/


//redefine the non-standard contains method using the standard compareDocumentPosition
//http://www.quirksmode.org/blog/archives/2006/01/contains_for_mo.html
if(window.Node && Node.prototype && !Node.prototype.contains)
{
	Node.prototype.contains = function (arg) {
		return !!(this.compareDocumentPosition(arg) & 16)
	}
}

function nodeContains(container, contained)
{
	if(container.compareDocumentPosition)
	{
		return !!(container.compareDocumentPosition(contained) & 16);
	}
	else
	{
		return container.contains(contained);
	}
}

function getNextTextNode(node, container, include_self, not_recursive)
{
	if(!container || nodeContains(container, node))
	{
		if(include_self && node.nodeType == 3 && node.parent_text)
		{
			return node;
		}
		else if(!not_recursive && node.firstChild)
		{
			return getNextTextNode(node.firstChild, container, true);
		}
		else if(node.nextSibling)
		{
			return getNextTextNode(node.nextSibling, container, true);
		}
		else
		{
			return getNextTextNode(node.parentNode, container, false, true);
		}
	}
	else
	{
		return null;
	}
}

function getPreviousTextNode(node, container, include_self, not_recursive)
{
	if(!container || nodeContains(container, node))
	{
		if(include_self && node.nodeType == 3 && node.parent_text)
		{
			return node;
		}
		else if(!not_recursive && node.lastChild)
		{
			return getPreviousTextNode(node.lastChild, container, true);
		}
		else if(node.previousSibling)
		{
			return getPreviousTextNode(node.previousSibling, container, true);
		}
		else
		{
			return getPreviousTextNode(node.parentNode, container, false, true);
		}
	}
	else
	{
		return null;
	}
}

