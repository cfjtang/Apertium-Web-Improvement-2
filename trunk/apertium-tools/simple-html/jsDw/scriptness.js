﻿var pairs = new Array();
var curr_pair = new Object();
var srcLangs = new Array();
var dstLangs = new Array();
var grayedOuts = new Array();
var isDetecting = false;
var toLangCode = "";
var fromLangCode = "";
var winW = 800;
var mobile = false;

var abbreviations = {
	'Spanish':'es',
	'Catalan':'ca',
	'Catalan (Valencian)':'ca_valencia',
	'Galician':'gl',
	'Portuguese':'pt',
	'Brazilian Portuguese':'pt_BR',
	'Occitan':'oc',
	'Aranese':'oc_aran',
	'English':'en',
	'French':'fr',
	'Esperanto':'eo',
	'Romanian':'ro',
	'Welsh':'cy',
	'Basque':'eu',
	'Breton':'br',
	'Norwegian Bokmål':'nb',
	'Norwegian Nynorsk':'nn',
	'Swedish':'sv',
	'Danish':'da',
	'Asturian':'ast',
	'Icelandic':'is',
	'Macedonian':'mk',
	'Bulgarian':'bg',
	'Italian':'it',
	'Tatar':'tat',
	'Kazakh':'kaz'
}

// something better needs to be developed
// that actually works across browsers
// preferably an external library
var keyCodes = {
	"space": 32,
	".": 190,
	"/": 191,
	"1": 49,
	"enter": 13,
	";": 59, // applicable only to FF and Opera, IE, Safari and Chrome use 186.
}

$(document).ready(function(){
	curr_pair.srcLang="";
	curr_pair.dstLang="";
	
	$("#textAreaId").keyup(function(event) {
		if (event.keyCode == keyCodes["space"] ||
			event.keyCode == keyCodes["."] ||
			event.keyCode == keyCodes["/"] ||
			event.keyCode == keyCodes["1"] ||
			event.keyCode == keycodes[";"] ||
			event.keyCode == keyCodes["enter"] ) {
			// automatically translate
			// when one of these keys are pressed

			try {
				if (curr_pair.srcLang.indexOf("Detect") != -1) {		
					isDetecting = true;
				}
			
				if (isDetecting) {
					curr_pair.srcLang = detectLanguage($(this).val());
					$('#selectFrom em').html(curr_pair.srcLang);
				}
			} catch(e) {
				console.log(e.message);
			}
			
			translate(curr_pair,$('#textAreaId').val());
			return false;
		}
	});


	jQuery("#inputBox").submit(function(){
		try {
			try {
				if (curr_pair.srcLang.indexOf("Detect") != -1) {
					curr_pair.srcLang = detectLanguage($(this).val());
					$('#selectFrom em').html(curr_pair.srcLang);
				}	
			} catch(e) {
				console.log(e.message);
			}
		
			translate(curr_pair, $('#textAreaId').val());
			return false;
		} catch(e) {
			console.log(e.message);
		}
	});
    

	$('#dropDownSub').hide();
	
	var FromOrTo;
	
	$('#swapLanguages').click(function(){
		fromText = $('#selectFrom em').text();
		toText = $('#selectTo em').text();
		$('#selectTo em').html(fromText);
		$('#selectFrom em').html(toText);
		
		curr_pair.dstLang = fromText;
		curr_pair.srcLang = toText;
		
		var langHolder = "";
		langHolder = toLangCode;
		toLangCode = fromLangCode;
		fromLangCode = langHolder;
		
	});
	
	jQuery('#selectTo').click(function(){
		loler = curr_pair.srcLang + "|";
		aaa=0;
		for(it in window.pairs){
			//console.log(window.pairs, window.pairs[it], loler, window.pairs[it].indexOf(loler));
			if(window.pairs[it].indexOf(loler) != -1){
				//grayedOuts[aaa] = window.pairs[it].substr(-3,3);
				grayedOuts[aaa] = window.pairs[it].split('|')[1];
				//console.log(grayedOuts[aaa]);
				aaa++;
			}	
		}
	});
	
	getPairs();
});

$(document).click(function(){
	$('#dropDownSub').hide();
});

function getLangByCode(code) {
	language = code
	//FIXME: currently not able to parse abbreviations
	for (abbv in abbreviations) {
		if (abbv==code) {
			language = abbreviations[abbv];
		}
	}
	return language;
}

function translate(langPair, text){

	langpairer = $.trim(langPair.srcLang) +"|" + $.trim(langPair.dstLang);
	//alert(langpairer);

	jQuery.ajax({
		url:'http://api.apertium.org/json/translate',
		type:"GET",
		data:{
			'langpair':langpairer,
			'q':text,
		},
		success : smth,
		dataType: 'jsonp',
		failure : trad_ko
	});
        
}

function smth(dt){
	if(dt.responseStatus==200) {
		jQuery('#translationTest').html(dt.responseData.translatedText);
	} else {
		trad_ko();
    }
}

function getPairs(){
	jQuery.ajax({
			url:'http://api.apertium.org/json/listPairs',
			type:"GET",
			success : trad_ok,
			dataType: 'jsonp',
			failure : trad_ko
		});
}

function trad_ko() {
	jQuery('#translationTest').html("Translation not yet available!");
}

function trad_ok(dt) {

	if(dt.responseStatus==200) {
		
		jQuery('#translationTest').html(" ");
		all = dt.responseData;

		
		for(var i in all) {
				l = all[i].sourceLanguage+'|'+all[i].targetLanguage;
				window.pairs[i]=l;
				srcLangs[i] = all[i].sourceLanguage;		
				srcLangs = jQuery.unique(srcLangs);
				
				dstLangs[i] = all[i].targetLanguage;
				dstLangs = jQuery.unique(dstLangs);
				
			//jQuery('#translationTest').append(l+'\n');
		}
		
		populateTranslationList("#column-group-", srcLangs);
		
	}else {
		trad_ko();
	}
}


function parsePair(pr){
	parsedPair = null;	
	pr.srcLang = jQuery.trim(pr.srcLang);
	pr.dstLang = jQuery.trim(pr.dstLang);
	
	parsedPair = abbreviations[pr.srcLang] + "|" + abbreviations[pr.dstLang];
	return parsedPair;
}

function populateTranslationList(elementClass, langArr){
	
	jQuery(".column-group").html("");
	jQuery("#column-group-1").append("<span> <a href='#' class='language-selected' > Detect Language </a></span>");
		
	column_num=1;
	//console.log(langArr);
	for(it in langArr){
		var compareLang = " "+getLangByCode(langArr[it])+" ";
		
		if (toLangCode == compareLang) {
			jQuery(elementClass+column_num).append("<span> <a href='#' class='current-language-selected-to' > " + getLangByCode(langArr[it]) + " </a></span>");
		}
		if (fromLangCode == compareLang) {
			jQuery(elementClass+column_num).append("<span> <a href='#' class='current-language-selected-from' > " + getLangByCode(langArr[it]) + " </a></span>");
		}
		if (fromLangCode != compareLang && toLangCode != compareLang) {
			jQuery(elementClass+column_num).append("<span> <a href='#' class='language-selected' > " + getLangByCode(langArr[it]) + " </a></span>");
		}
		
		
		if(jQuery(elementClass+column_num).children().length>5){
			column_num++;
		}
		
	}
	
		for(it in grayedOuts)
			$("a:contains( " +grayedOuts[it]+" )").removeClass('language-selected');

	
	
	
	
	$('.itemSelect').toggle(function(){
		if( navigator.userAgent.match(/Android/i)
 			|| navigator.userAgent.match(/webOS/i)
 			|| navigator.userAgent.match(/iPhone/i)
 			|| navigator.userAgent.match(/iPad/i)
 			|| navigator.userAgent.match(/iPod/i)
 			|| navigator.userAgent.match(/BlackBerry/i)
 			|| navigator.userAgent.match(/Windows Phone/i)
 		){
 			mobile = true;
		}
		jQuery('.column-group').removeClass('language-selected');
		
		if($(this).attr("id")=="selectFrom"){
			populateTranslationList("#column-group-", srcLangs);
			
			FromOrTo="from";
			$('#dropDownSub').hide();
			$('#dropDownSub').addClass('selectFromSub');
			$('#dropDownSub').css('left','0');
		

		} else {
		var interval = setInterval(function(){
		winW = window.innerWidth;
		if (FromOrTo == "to"){
			if (winW < 750 || mobile) {
				$('#dropDownSub').css('left', '0');
			}
			if (winW >= 750 && !mobile) {
				$('#dropDownSub').css('left', '366px');
			}
		}
		},1);
		
		populateTranslationList("#column-group-", dstLangs);
			winW = window.innerWidth;

			FromOrTo = "to";
			$('#dropDownSub').hide();
			if (winW>750 && !mobile){
				$('#dropDownSub').css('left','366px');
			}else {
				$('#dropDownSub').css('left', '0');
			}
			
			$('#dropDownSub').removeClass('selectFromSub');
			//find_smth(curr_pair.srcLang);
			//$('#dropDownSub a').addClass('language-selected');
		}
			
			$('#dropDownSub').show();
	}, function(){
		$('#dropDownSub').hide()	
	});

	
	$('#dropDownSub a').click(function(){
		$('#dropDownSub a').removeClass('language-selected');
		if (FromOrTo == "from"){
			$('#dropDownSub a').removeClass('current-language-selected-from');
			fromLangCode = $(this).text();
		}
		if (FromOrTo == "to"){
			$('#dropDownSub a').removeClass('current-language-selected-to');
			toLangCode = $(this).text();
		}
		
		if(FromOrTo=="from"){

			
			if($(this).text()!=" Detect Language ")
				isDetecting = false;
		
			if($(this).text() !=" Detect Language "){
			$('#selectFrom em').html($(this).text());
			}else{
			$('#selectFrom em').html("Detect");	
			}
			curr_pair.srcLang = $(this).text();
			
		} else {
			if($(this).text() !=" Detect Language "){
			$('#selectTo em').html($(this).text());
			}else{
			$('#selectTo em').html("Detect");	
			}
			curr_pair.dstLang = $(this).text();
		}
		matchFound= false;
	
		//FIXME: if (curr_pair in window.pairs) ??
		for(var it in window.pairs){	
			if(parsePair_lol(curr_pair)==window.pairs[it])
				matchFound=true;
		}
		
		
		if(matchFound){
		
			try{
				if(curr_pair.srcLang.indexOf("Detect") !=-1){
					curr_pair.srcLang = detectLanguage($(this).val());
			
					$('#selectFrom em').html(curr_pair.srcLang);
					
			}
				
			
			}catch(e){
				console.log(e.message);
			}
			
			translate(curr_pair,$('#textAreaId').val());
		}
		else jQuery('#translationTest').html("Translation not yet available!");
		
		
	});
	
}

function strcmp(a, b){   
    return (a<b?-1:(a>b?1:0));
}
	

function parsePair_lol(pr){

	parsedPair = null;	
	pr.srcLang = jQuery.trim(pr.srcLang);
	pr.dstLang = jQuery.trim(pr.dstLang);

	parsedPair = pr.srcLang;
	parsedPair +="|" +pr.dstLang;
	return parsedPair;
}

function find_smth(lol){
	aaa=0;
	loler = "|" + lol;
	
	
	for(it in window.pairs){
		if(window.pairs[it].indexOf(loler) != -1){
			aaa++;
		}	
	}
}

function detectLanguage(text) {
	jQuery.get("//localhost:2737/identifyLang", {q: text}, function(data) {
		topLang = "";
		topProbability = -1.0;
		for (var lang in data) {
			if (data[lang] > topProbability) {
				topLang = lang;
				topProbability = data[lang];
			}
		}
		return topLang;
	}, "json");
}