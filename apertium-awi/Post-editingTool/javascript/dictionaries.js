//coding: utf-8
/*
  Apertium Web Post Editing tool
  Functions for handling of external dictionaries
	
  Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
  Mentors : Luis Villarejo, Mireia Farrús
*/

var dictionary_src;
var dictionary_dst;
var dict_refresh_script;

function initDictionaries()
{
	dictionary_src = document.getElementById('dictionary_src');
	dictionary_dst = document.getElementById('dictionary_dst');
	
	refreshDictionaryLists();
	
	dictionary_src.parentNode.style.display = '';
	dictionary_dst.parentNode.style.display = '';
	
	document.mainform.language_pair.onchange = refreshDictionaryLists;
}

function refreshDictionaryLists()
{
	var language_pair = document.mainform.language_pair.value;
	var src_language = language_pair.split('-');
	var dst_language = src_language[1];
	src_language = src_language[0];
	
	var load_script = document.createElement('script');
	load_script.type = 'text/javascript';
	load_script.src = 'dictionaries.php?src_language='+src_language+'&dst_language='+dst_language;
	
	if(dict_refresh_script)
	{
		document.getElementsByTagName("head")[0].replaceChild(load_script, dict_refresh_script);
	}
	else
	{
		document.getElementsByTagName("head")[0].appendChild(load_script);
	}
	dict_refresh_script = load_script;
}

function getDictionaryLink(word, field)
{
	return window["dictionary_"+field].value.replace(/\{searchTerms\}/g, encodeURIComponent(word));
}

