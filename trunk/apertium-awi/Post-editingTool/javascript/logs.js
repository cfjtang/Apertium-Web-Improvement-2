//coding: utf-8
/*
 *  Apertium Web Post Editing tool
 *  Functions for logging system
 *	
 *  Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
 *  Mentors : Luis Villarejo, Mireia Farrús
 *
 *  Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
 *  Mentors : Arnaud Vié, Luis Villarejo
 */

/* TextEditor */

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

function handleKeyPress(e)
{
	var event = getEvent(e);
	
	if(event.ctrlKey)
	{
		var character = false;
	}
	else
	{
		var character = event.charCode ? String.fromCharCode(event.charCode) : (event.data ? event.data : "");
	}

	//let the text editor lib handle it
	return TE_handleKeyPress(event, character);
}

function handleKeyDown(e)
{
	var event = getEvent(e);
	
	//let the text editor lib handle it
	return TE_handleKeyPress(event, false);
}

/* Functions to manage high level event logs */

//Data structures
function EditLog()
{
	//list of logs
	/*A log is an array of 2 or more elements :
	  - the type of the log (add word, delete word, add sentence, ...)
	  - the targets of the log
	*/
	this.logs = new Array();
	
	this.appendLog = function(element)
		{
			this.logs.push(new Array("", element));
			return this.logs.length - 1;
		}
	
	this.getLogAbout = function(element, do_not_add, not_only_first)
		{
			//search for a log about the give element
			for(var i =0; i < this.logs.length; i++)
			{
				if(this.logs[i] != false)
				{
					if(not_only_first)
					{
						for(var j = 1; j < this.logs[i].length; j++)
						{
							if(this.logs[i][j] == element)
							{
								return i;
							}
						}
					}
					else
					{
						if(this.logs[i][1] == element)
						{
							return i;
						}
					}
				}
			}
		
			//if none found
			if(do_not_add)
			{
				return -1;
			}
			else
			{
				//insert a new one
				return this.appendLog(element);
			}
		};
	
	this.removeLogsAbout = function(element)
		{
			for(var i = 0; i < this.logs.length; i++)
			{
				if(this.logs[i] != false)
				{
					for(var j = 1; j < this.logs[i].length; j++)
					{
						if(this.logs[i][j] == element)
						{
							this.logs[i] = false;
						}
						else if(element.constructor == Sentence && this.logs[i][j].sentence == element)
						{
							this.logs[i] = false;
						}
					}
				}
			}
		};
	
	//functions called by logging_lowlevel.js
	
	this.addWord = function(word)
		{
			var log;
			if(word.nextWord && word.nextWord.sentence == word.sentence && ((log = this.getLogAbout(word.nextWord, true, true)) >= 0) && this.logs[log][0] == "add_words") //if next word is part of an add log
			{
				//insert word before the next word
				var index = this.logs[log].indexOf(word.nextWord);
				this.logs[log].push();
				for(var i = this.logs[log].length - 1; i > index; i--)
				{
					this.logs[log][i] = this.logs[log][i-1];
				}
				this.logs[log][index] = word;
			}
			else if(word.previousWord && word.previousWord.sentence == word.sentence && ((log = this.getLogAbout(word.previousWord, true, true)) >= 0) && this.logs[log][0] == "add_words") //if previous word is part of an add log
			{
				//insert word after the previous word
				var index = this.logs[log].indexOf(word.previousWord);
				this.logs[log].push();
				for(var i = this.logs[log].length - 1; i > index + 1; i--)
				{
					this.logs[log][i] = this.logs[log][i-1];
				}
				this.logs[log][index + 1] = word;
			}
			else
			{
				//create a new add words log
				log = this.appendLog(word);
				this.logs[log][0] = "add_words";
			}
		}
	
	this.editWord = function(word)
		{
			if(word.original_word != word.word)
			{
				var log = this.getLogAbout(word, false, true);
				if(this.logs[log][0] != "add_words") 
				{
					this.logs[log][0] = "edit_word";
				}
			}
			else
			{
				var log = this.getLogAbout(word, false, true);
				if(this.logs[log][0] == "edit_word")
				{
					this.logs[log] = false;
				}
			}
		}
	
	this.delWord = function(word)
		{
			var log = this.getLogAbout(word, false, true);
			if(this.logs[log][0] == "add_words") //removing an added word
			{
				//remove the deleted word from the add words log
				var index = this.logs[log].indexOf(word);
				for(var i = index; i < this.logs[log].length - 1; i++)
				{
					this.logs[log][i] = this.logs[log][i+1];
				}
				this.logs[log].pop();
			}
			else
			{
				this.logs[log][0] = "del_word";
			}
		};
	
	this.mergeSentences = function(sentence1, sentence2)
		{
			if(sentence2.firstWord != sentence2.lastWord && sentence1.firstWord != sentence1.lastWord) //sentences aren't empty
			{
				var log;
				while((log = this.getLogAbout(sentence2, true, true)) >= 0)
				{
					if(this.logs[log][0] == "split_sentences" && this.logs[log][1] == sentence1 && this.logs[log][2] == sentence2) //remerging the result of a split : nothing to log
					{
						this.logs[log] = false;
						return;
					}
					else if(this.logs[log][0] == "merge_sentences") //it already had a right part merged to it
					{
						this.logs[log][1] = sentence1;
						this.logs[log][2] = sentence2.firstWord;
					
						return;
					}
				}
			
				log = this.appendLog(sentence1);
				this.logs[log][0] = "merge_sentences";
				this.logs[log][2] = sentence2.firstWord;
			}
		}
	
	this.splitSentences = function(sentence1, sentence2)
		{
			if(sentence2.firstWord != sentence2.lastWord && sentence1.firstWord != sentence1.lastWord) //sentences aren't empty
			{
				var log;
				while((log = this.getLogAbout(sentence1, true)) >= 0)
				{
					if(this.logs[log][0] == "merge_sentences" && this.logs[log][2] == sentence2.firstWord) //splitting merged sentences
					{
						this.logs[log] = false;
						return;
					}
					else if(this.logs[log][0] == "split_sentences")
					{
						var old_s2 = this.logs[log][2];
						this.logs[log][2] = sentence2;
					
						log = this.appendLog(sentence2);
						this.logs[log][0] = "split_sentences";
						this.logs[log][2] = old_s2;
						return;
					}
				}
			
			
				log = this.appendLog(sentence1);
				this.logs[log][0] = "split_sentences";
				this.logs[log][2] = sentence2;
			}
		}
	
	//Interface function : can be rewritten by the user to log the info the way he wants
	this.readLogs = function()
		{
			var output = '';
		
			for(var i =0; i < this.logs.length; i++)
			{
				if(this.logs[i] != false)
				{
					switch(this.logs[i][0])
					{
					case "add_words" :
					
						output += 'Added word(s) : ' + (this.logs[i][1].previousWord ? this.logs[i][1].previousWord.word : '') + ' <strong>';
						for(var j = 1; j < this.logs[i].length; j++)
						{
							output += this.logs[i][j].word + ' ';
						}
						output += '</strong>' +(this.logs[i][this.logs[i].length - 1].nextWord ? this.logs[i][this.logs[i].length - 1].nextWord.word : '') + '<br />\n';
						
						break;
					
					case "edit_word" :
					
						output += 'Edited word : ' + this.logs[i][1].original_word + ' to <strong>' + this.logs[i][1].word + '</strong><br />\n';
						
						break;
					
					case "del_word" :
					
						output += 'Deleted word : ' + (this.logs[i][1].previousWord ? this.logs[i][1].previousWord.word : '') + ' <strong>';
						output += this.logs[i][1].original_word;
						output += '</strong>' +(this.logs[i][1].nextWord ? this.logs[i][1].nextWord.word : '') + '<br />\n';
						
						break;
					
					case "split_sentences" :
						
						output += 'Splitted sentences : ';
						var currentWord = this.logs[i][1].firstWord;
						while(currentWord && currentWord != this.logs[i][1].lastWord)
						{
							output += currentWord.word + ' ';
							currentWord = currentWord.nextWord;
						}
						output += '<strong>'+this.logs[i][1].lastWord.word+'</strong> ' + this.logs[i][2].getText() + '<br />\n';
						
						break;
					
					case "merge_sentences" :
						
						output += 'Merged sentences : ';
						var currentWord = this.logs[i][1].firstWord;
						while(currentWord && currentWord.sentence == this.logs[i][1])
						{
							if(currentWord == this.logs[i][2])
							{
								output += '<strong>'+currentWord.word + '</strong> ';
							}
							else
							{
								output += currentWord.word + ' ';
							}
							currentWord = currentWord.nextWord;
						}
						
						output += '<br />\n';
						
						break;
					}
				}
			}
		
			return output;
		}
}


function extractLogs(object)
{
	object.final_log += object.text_object.log.readLogs();
	object.text_object.log.logs = new Array();
	
	return object.final_log;
}

/* Interface between browser data and linguistic data */

//regexp classes
var letters_class = '-\\w\\u00C0-\\u00FF'; //latin letters
var eos_class = '.!?'; //end of sentence punctuation

var regexp_contains_letters = new RegExp('['+letters_class+']');
var regexp_contains_eos = new RegExp('['+eos_class+']');
var regexp_split_lexical = new RegExp('([\\s]+|(?:[^\\s'+letters_class+']+(?:\\s+[^\\s'+letters_class+']+)*)+)'); // /([\s]+|(?:[^\w\s]+(?:\s+[^\w\s]+)*)+)/
var regexp_is_space = /^[\s]*$/;

//Data structures

function Word(text, mother_sentence, mother_node, node_offset)
{
	this.constructor = Word;
	
	this.word = text; //this one will change as events occur
	this.original_word = text; //this one will keep its initial value
	this.sentence = mother_sentence;
	this.node = mother_node;
	this.node_offset = node_offset; //offset relative to the beginning of the containing node
	this.nextWord = null; //link to the next word if it exists
	this.previousWord = null;
}

function Sentence()
{
	this.constructor = Sentence;
	
	this.firstWord = null;
	this.lastWord = null;
	this.nextSentence = null;
	this.previousSentence = null;
	this.text_object = null;
	
	this.original_text = '';
	
	this.getText = function()
		{
			var text = '';
		
			currentWord = this.firstWord;
		
			while(currentWord && currentWord.sentence == this)
			{
				text += currentWord.word + ' ';
			
				currentWord = currentWord.nextWord;
			}
		
			return text;
		};
}

function Text(parent)
{
	this.parent_element = parent;
	
	this.firstNode = null;
	this.lastNode = null;
	
	this.log = new EditLog();
	
	this.appendNode = function(node)
		{
			this.insertNodeAfter(node, this.lastNode);
		};
	
	this.insertNodeAfter = function(newNode, previousNode)
		{
			newNode.previousNode = previousNode;
			if(previousNode)
			{
				newNode.nextNode = previousNode.nextNode;
				previousNode.nextNode = newNode;
				if(newNode.nextNode)
				{
					newNode.nextNode.previousNode = newNode;
				}
			}
			else //insert as first node
			{
				newNode.nextNode = this.firstNode;
				if(newNode.nextNode)
				{
					newNode.nextNode.previousNode = newNode;
				}
				this.firstNode = newNode;
			}
		
			if(previousNode == this.lastNode)
			{
				this.lastNode = newNode;
			}
		
			newNode.parent_text = this;
		};
	
	this.firstSentence = null;
	this.lastSentence = null;
	
	this.appendSentence = function()
		{
			var newSentence = new Sentence();
		
			newSentence.text_object = this;
			newSentence.previousSentence = this.lastSentence;
		
			if(!this.firstSentence) //if there was no sentence before, set it as first sentence
			{
				this.firstSentence = newSentence;
			}
			else
			{
				this.lastSentence.nextSentence = newSentence;
			}
		
			this.lastSentence = newSentence;
		
			return newSentence;
		};
	
	//insert a new word at the end of the text
	this.appendWord = function(text, node, node_offset)
		{
			var newWord;
		
			if(!text.match(regexp_is_space)) //if it is not just spaces, save it
			{
				newWord = new Word(text, this.lastSentence, node, node_offset);
			
				newWord.previousWord = this.lastSentence.lastWord ? this.lastSentence.lastWord : (this.lastSentence.previousSentence ? this.lastSentence.previousSentence.lastWord : null);
				if(newWord.previousWord) //if there already were words before
				{
					newWord.previousWord.nextWord = newWord; //put this one as the next sibling of the previous one
				}
			
				if(!this.lastSentence.firstWord)
				{
					this.lastSentence.firstWord = newWord; //set it as first word if sentence is empty
				}
				this.lastSentence.lastWord = newWord; //set it as last word
			
				if(!newWord.node.firstWord) //if it's the first in that node
				{
					newWord.node.firstWord = newWord;
					newWord.node.lastWord = newWord;
				}
			}
		
			return newWord;
		};
	
	//insert a new word at the beginning of the text
	this.unshiftWord = function(newWord)
		{
			newWord.nextWord = this.firstSentence.firstWord;
		
			if(!newWord.word.match(regexp_is_space)) //if it is not just spaces, save it into the list
			{
				this.firstSentence.firstWord = newWord;
				if(!this.firstSentence.lastWord)
				{
					this.firstSentence.lastWord = newWord;
				}
			
				if(newWord.nextWord) //if there is a next word
				{
					newWord.nextWord.previousWord = newWord;
				}
			
				newWord.sentence = this.firstSentence;
			
				if(!newWord.node.firstWord) //if it's the first in that node
				{
					newWord.node.firstWord = newWord;
					newWord.node.lastWord = newWord;
				}
			}
		
			updateOffsets(newWord.node, newWord.nextWord, newWord.word.length);
		
			this.log.addWord(newWord);
		};
}

/*
  INITIALISATION FUNCTIONS
*/

function buildWordList(node, text_contents)
{
	//Go through the node list to store the sentence and word positions for each
	
	if(!text_contents)
	{
		var text_contents = new Text(node);
		text_contents.appendSentence(); //insert new sentence at start
	}
	
	switch(node.nodeType)
	{
	case 3 : //text node to parse
			
		scanWordsFromTextNode(text_contents, node);
			
		break;
			
			
	case 1 : //html element : build from child elements
		
		for(var i = 0; i < node.childNodes.length; i++)
		{
			buildWordList(node.childNodes[i], text_contents);
		}
			
		break;
	}
	
	return text_contents;
}


function scanWordsFromTextNode(text_contents, node)
{
	//Append all words contained in the text node to the text_contents
	//And save the ids of seen sentences and position of words in the node's properties
	
	var contents = node.nodeValue;
	
	
	//Create the list of contained words in the node
	node.firstWord = null;
	node.lastWord = null;
	
	text_contents.appendNode(node);
	
	//break the node contents into lexical elements (separate spaces, words and punctuation)
	var lexicalElts = contents.split(regexp_split_lexical);
	
	//Go through all lexical elements of the node and put them in sentences
	var nodeOffset = 0;
	var latestAddedWord = null;
	
	for(var i = 0; i < lexicalElts.length; i++)
	{
		var newWord = text_contents.appendWord(lexicalElts[i], node, nodeOffset); //append to the current sentence
		if(newWord) //if a new word was created
		{
			latestAddedWord = newWord;
		}
		
		nodeOffset += lexicalElts[i].length; //update the offset of the element in that node
		
		if(lexicalElts[i].match(regexp_contains_eos)) //if it's an end of sentence
		{
			text_contents.lastSentence.original_text = text_contents.lastSentence.getText(); //generate the original contents of the sentence
			text_contents.appendSentence(); //start a new sentence
		}
	}
	
	node.lastWord = latestAddedWord; //end word is the last added one here, be it null
}



/*
  WORD MANIPULATION FUNCTIONS
*/

//returns the word at a given position, if any. If include_end, then a word ending just before the given offset will be accepted as well
function getWord(node, offset, include_end)
{
	if(node.lastWord && 
	   offset >= node.lastWord.node_offset && offset < node.lastWord.node_offset + node.lastWord.word.length + (include_end ? 1 : 0))
	{
		return node.lastWord;
	}
	else
	{
		var word = node.firstWord;
		
		while(word && word != node.lastWord)
		{
			if(offset >= word.node_offset && offset < word.node_offset + word.word.length + (include_end ? 1 : 0))
			{
				return word;
			}
			
			word = word.nextWord;
		}
		
		return null;
	}
}

//Returns the last word before the given position
function getWordBefore(node, offset)
{
	if(!node.firstWord || node.firstWord.node_offset > offset)
	{
		return null;
	}
	else
	{
		var word = node.lastWord;
		
		while(word != node.firstWord && word.node_offset > offset)
		{
			word = word.previousWord;
		}
		
		return word;
	}
}

//Returns the first word after the given position
function getWordAfter(node, offset)
{
	if(!node.lastWord || node.lastWord.node_offset < offset)
	{
		return null;
	}
	else
	{
		var word = node.firstWord;
		
		while(word != node.lastWord && word.node_offset < offset)
		{
			word = word.nextWord;
		}
		
		return word;
	}
}

//insert a Word object, that should already contain proper values for node, node_offset, word and original_word, after a given word
function insertAfter(sourceWord, newWord)
{
	if(!newWord.word.match(regexp_is_space)) //if it's not just spaces, insert it
	{
		//insert into words list structure
		newWord.nextWord = sourceWord.nextWord;
		sourceWord.nextWord = newWord;
		
		newWord.previousWord = sourceWord;
		if(newWord.nextWord)
		{
			newWord.nextWord.previousWord = newWord;
		}
		
		//determine the right sentence
		if(sourceWord.word.match(regexp_contains_eos)) //if the sourceWord is an end of sentence
		{
			newWord.sentence = sourceWord.sentence.nextSentence;
			newWord.sentence.firstWord = newWord;
			if(!newWord.sentence.lastWord) //if the sentence was empty, it's also the last word
			{
				newWord.sentence.lastWord = newWord;
			}
		}
		else
		{
			newWord.sentence = sourceWord.sentence;
			if(sourceWord.sentence.lastWord == sourceWord) //if sourceWord was the last of its sentence
			{
				sourceWord.sentence.lastWord = newWord;
			}
		}
		
		//edit the node start and end points if needed
		if(newWord.node != sourceWord.node) //if not the same node as the previous word, it's the first of its node
		{
			newWord.node.firstWord = newWord;
		}
		if(!newWord.nextWord || newWord.node != newWord.nextWord.node) 
		{
			newWord.node.lastWord = newWord;
		}
		
		//update offsets of the following words
		updateOffsets(newWord.node, newWord.nextWord, newWord.word.length);
		
		//if we just inserted end of sentence punctuation, what follows becomes a new sentence
		if(newWord.word.match(regexp_contains_eos)) 
		{
			splitSentenceFrom(newWord);
		}
		
		newWord.node.parent_text.log.addWord(newWord);
		
		return newWord;
	}
	else	//just update the offsets
	{
		updateOffsets(newWord.node, sourceWord.nextWord, newWord.word.length);
		return null;
	}
}

function removeWord(targetWord)
{
	if(targetWord.nextWord)
	{
		targetWord.nextWord.previousWord = targetWord.previousWord;
	}
	if(targetWord.previousWord)
	{
		targetWord.previousWord.nextWord = targetWord.nextWord;
	}
	
	if(targetWord.node.firstWord == targetWord && targetWord.node.lastWord == targetWord)
	{
		targetWord.node.firstWord = null;
		targetWord.node.lastWord = null;
	}
	else if(targetWord.node.firstWord == targetWord)
	{
		targetWord.node.firstWord = targetWord.nextWord;
	}
	else if(targetWord.node.lastWord == targetWord)
	{
		targetWord.node.lastWord = targetWord.previousWord;
	}
	
	if(targetWord.sentence.firstWord == targetWord && targetWord.sentence.lastWord == targetWord)
	{
		targetWord.sentence.firstWord = null;
		targetWord.sentence.lastWord = null;
	}
	else if(targetWord.sentence.firstWord == targetWord)
	{
		targetWord.sentence.firstWord = targetWord.nextWord;
	}
	else if(targetWord.sentence.lastWord == targetWord)
	{
		targetWord.sentence.lastWord = targetWord.previousWord;
	}
	
	targetWord.node.parent_text.log.delWord(targetWord);
}

//update the node_offset attribute of all words following sourceWord in the given node, by a given value
function updateOffsets(node, sourceWord, shift_value)
{
	var word = sourceWord;
	
	while(word && word.node == node)
	{
		//if the word is still inside the same node, update its node_offset
		word.node_offset += shift_value;
		word = word.nextWord;
	}
}

//split a sentence in two at a given delimiter and update the sentence list accordingly
function splitSentenceFrom(delimiterWord)
{
	originalSentence = delimiterWord.sentence;
	
	var newSentence = new Sentence();
	newSentence.text_object = originalSentence.text_object;
	
	originalSentence.lastWord = delimiterWord; //delimiter becomes the end of its current sentence
	
	//put all following words of the original sentence into the new one
	var currentWord = delimiterWord.nextWord;
	while(currentWord && currentWord.sentence == originalSentence)
	{
		if(!newSentence.firstWord)
		{
			newSentence.firstWord = currentWord;
		}
		currentWord.sentence = newSentence;
		newSentence.lastWord = currentWord;
		
		currentWord = currentWord.nextWord;
	}
	
	//rebuild the sentence list links correctly
	newSentence.nextSentence = originalSentence.nextSentence;
	if(newSentence.nextSentence)
	{
		newSentence.nextSentence.previousSentence = newSentence;
	}
	originalSentence.nextSentence = newSentence;
	newSentence.previousSentence = originalSentence;
	
	delimiterWord.node.parent_text.log.splitSentences(originalSentence, newSentence);
}

//merge two sentences by extracting the delimiterWord from the words list (delimiterWord must be the last word of a sentence)
function mergeSentencesFrom(delimiterWord)
{
	//merge the two sentences
	var leftSentence = delimiterWord.sentence;
	var rightSentence = leftSentence.nextSentence;
	
	delimiterWord.node.parent_text.log.mergeSentences(leftSentence, rightSentence);
	
	leftSentence.lastWord = rightSentence.lastWord;
	leftSentence.nextSentence = rightSentence.nextSentence;
	
	if(leftSentence.nextSentence) //if there is another sentence after these two
	{
		leftSentence.nextSentence.previousSentence = leftSentence;
	}
	else //else, the merge result is the last sentence
	{
		leftSentence.text_object.lastSentence = leftSentence;
	}
	
	var currentWord = rightSentence.firstWord;
	while(currentWord && currentWord.sentence == rightSentence)
	{
		currentWord.sentence = leftSentence;
		currentWord = currentWord.nextWord;
	}
}

/*
  LOW LEVEL LOGGING FUNCTIONS
*/

function logInsertChar(node, offset, character)
{
	var targetWord;
	if( (targetWord = getWord(node, offset, true)) //if there is a word at given offset
	    && (!!targetWord.word.match(regexp_contains_letters) == !!character.match(regexp_contains_letters) )) //and the character is of the same type (letter or punctuation) as the word  
	{
		var word_offset = offset - targetWord.node_offset;
		
		//insert the new character in the word
		targetWord.word = targetWord.word.substr(0, word_offset) + character + targetWord.word.substr(word_offset);
		
		//update the positioning information of all following words
		updateOffsets(targetWord.node, targetWord.nextWord, character.length);
		
		targetWord.node.parent_text.log.editWord(targetWord);
		
		//if we just inserted end of sentence punctuation, what follows becomes a new sentence
		if(character.match(regexp_contains_eos)) 
		{
			splitSentenceFrom(targetWord);
		}
	}
	else if(targetWord) //There is a word at given offset, but of different type : start a new word
	{
		var word_offset = offset - targetWord.node_offset;
		
		var newWord = new Word();
		newWord.word = character;
		newWord.original_word = '';
		newWord.node = node;
		newWord.node_offset = offset;
		
		if(word_offset == 0) //insert a new word before
		{
			insertAfter(targetWord.previousWord, newWord);
		}
		else if(word_offset == targetWord.length) //insert a new word after
		{
			insertAfter(targetWord, newWord);
		}
		else //insert a new word in the middle
		{
			//generate a new word for the right part of the target word
			var targetWordRight = new Word();
			targetWordRight.word = targetWord.word.substr(word_offset);
			targetWordRight.original_word = '';
			targetWordRight.node = node;
			targetWordRight.node_offset = offset + character.length;
			
			//update the target word to keep only its left part
			targetWord.word = targetWord.word.substr(0, word_offset);
			updateOffsets(node, targetWord.nextWord, - targetWordRight.word.length);
			targetWord.node.parent_text.log.editWord(targetWord);
			
			//insert the two new words one after the other
			if(insertAfter(targetWord, newWord))
			{
				insertAfter(newWord, targetWordRight);
			}
			else
			{
				insertAfter(targetWord, targetWordRight);
			}
		}
		
		
	}
	else //there is no word yet, start one
	{
		var previousWord = getWordBefore(node, offset);
		if(!previousWord) //if the previous word isn't in this node
		{
			var currentNode = node.previousNode;
			while(currentNode && previousWord)
			{
				if(currentNode.lastWord)
				{
					previousWord = currentNode.lastWord;
				}
			}
		}
		
		var newWord = new Word();
		newWord.word = character;
		newWord.original_word = '';
		newWord.node = node;
		newWord.node_offset = offset;
		
		if(previousWord) //if a previous word exists
		{
			insertAfter(previousWord, newWord);
		}
		else
		{
			node.parent_text.unshiftWord(newWord);
		}
		
	}
}

function logDeleteChar(node, offset)
{
	var targetWord, followingWord, precedingWord;
	
	if(targetWord = getWord(node, offset)) //if there is a word character at given offset
	{
		var word_offset = offset - targetWord.node_offset;
		
		var word_before_deletion = targetWord.word;
		
		//delete the character from the word
		targetWord.word = targetWord.word.substr(0, word_offset) + targetWord.word.substr(word_offset + 1);
		
		targetWord.node.parent_text.log.editWord(targetWord);
		
		//if word contained end of sentence punctuation, but no longer does, merge two sentences
		if(word_before_deletion.match(regexp_contains_eos) && !targetWord.word.match(regexp_contains_eos))
		{
			mergeSentencesFrom(targetWord); //merge the sentences before and after
		}
		
		//if word is empty, delete it
		if(targetWord.word.match(regexp_is_space)) 
		{
			removeWord(targetWord);
		}
		
		//update the offsets of all following words to reflect character deletion
		updateOffsets(targetWord.node, targetWord.nextWord, -1);
	}
	else //just delete the space : no modification 
	{
		var nextWord = getWordAfter(node, offset);
		if(nextWord)
		{
			updateOffsets(nextWord.node, nextWord, -1);
		}
	}
	
	//merge thes two words around deletion position if possible
	if(!targetWord || targetWord.word.match(regexp_is_space)) //if there is no word left where we deleted
	{
		if( (precedingWord = getWord(node, offset - 1)) && (followingWord = getWord(node, offset)) //and the deletion occurs between two words
		    && (!!precedingWord.word.match(regexp_contains_letters) == !!followingWord.word.match(regexp_contains_letters) ) ) //and those two words are of same type
		{
			//append new text to the first word
			precedingWord.word += followingWord.word;
			
			precedingWord.nextWord = followingWord.nextWord;
			//remove the followingWord from the words list
			removeWord(followingWord);
		}
	}
}

/* Small module to intercept paste events */
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
