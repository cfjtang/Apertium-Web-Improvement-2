//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions to manage high level event logs
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/


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