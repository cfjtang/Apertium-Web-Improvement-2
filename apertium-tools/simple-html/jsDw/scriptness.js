var pairs = new Array();
var curr_pair = new Object();
var srcLangs = new Array();
var dstLangs = new Array();
var grayedOuts = new Array();
var isDetecting = false;
var toLangCode = "";
var fromLangCode = "";
var winW = 800;
var mobile = false;

var probabilities_lang_code = [];
var probabilities = [];
var ordered_probs = [];
var highest = 0;
var sec_highest_lang_code = "";
var thr_highest_lang_code = "";
var highest_index = 0;
var highest_lang_code = "";
var from_drop_down = false;

var drop_down_show = false;
var drop_down_click = false;
var drop_down_zone = "";
var latest_drop_down_zone = "";
var drop_down_zone_change = false;

var FromOrTo = "";

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

var prevmsg = "", msg = "";
$(document).ready(function(){
	curr_pair.srcLang="";
	curr_pair.dstLang="";
	
	$("#textAreaId").keyup(function(event) {
		msg = $(this).val();
		if (msg == prevmsg) {
			return;
		}

		// finds the position of the last equal character
		length = (prevmsg.length < msg.length) ? prevmsg.length : msg.length;
		last_equal = -1;
		for (var i = 0; i <= length; i++) {
			if (i == length) {
				last_equal = i-1;
				break;
			}
			if (msg[i] != prevmsg[i]) {
				last_equal = i-1;
				break;
			}
		}

		if (last_equal != -1) {
			var found = false;
			var autoCharRegex = XRegExp("[\\p{P}\\s]");
			if (prevmsg.length < msg.length) {
				// char added
				for (var i = last_equal+1; i < msg.length; i++) {
					if (autoCharRegex.test(msg[i])) {
						found = true;
						break;
					}
				}
			}
			else {
				// char deleted
				for (var i = last_equal+1; i < prevmsg.length; i++) {
					if (autoCharRegex.test(prevmsg[i])) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				return;
			}
		}

		console.log(msg);
		console.log(prevmsg);
		prevmsg = msg;

		msg = $.trim(msg);

		try {
			if (curr_pair.srcLang.indexOf("Detect") != -1) {
				data = detectLanguage(msg);
				curr_pair.srcLang = findHighest(data);
				$('#selectFrom em').html(curr_pair.srcLang);
				detect_lang_interface(msg, data);
			}
		} catch(e) {
			console.log(e.message);
		}
		
		if (curr_pair.srcLang && curr_pair.dstLang && curr_pair.srcLang.indexOf("Detect") == -1) {
			translate(curr_pair, msg);
		}
		return false;
	});

	jQuery("#inputBox").submit(function(){
		try {
			if (curr_pair.srcLang.indexOf("Detect") != -1) {
				data = detectLanguage(msg);
				curr_pair.srcLang = findHighest(data);
				$('#selectFrom em').html(curr_pair.srcLang);
				detect_lang_interface(msg, data);
			}	
		} catch(e) {
			console.log(e.message);
		}
	
		if (curr_pair.srcLang && curr_pair.dstLang && curr_pair.srcLang.indexOf("Detect") == -1) {
			translate(curr_pair,$('#textAreaId').val());
		}
		return false;
	});

	$('#dropDownSub').hide();
	
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
			if(window.pairs[it].indexOf(loler) != -1){
				grayedOuts[aaa] = window.pairs[it].split('|')[1];
				aaa++;
			}	
		}
	});
	
	getPairs();

		$('.itemSelect').click(function(e){
		e.stopPropagation();
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
			drop_down_zone = "from";
			if (drop_down_zone != latest_drop_down_zone){
				latest_drop_down_zone = drop_down_zone;
				drop_down_zone_change = true;
			} else {
				drop_down_zone_change = false;
			}
			from_drop_down = true;
			populateTranslationList("#column-group-", srcLangs);
			
			FromOrTo="from";
			$('#dropDownSub').hide();
			$('#dropDownSub').addClass('selectFromSub');
			$('#dropDownSub').css('left','0');
		

		} else {
			drop_down_zone = "to";
			if (drop_down_zone != latest_drop_down_zone){
				latest_drop_down_zone = drop_down_zone;
				drop_down_zone_change = true;
			} else {
				drop_down_zone_change = false;
			}
			from_drop_down = false;
		$( window ).resize(function() {
		winW = window.innerWidth;
		if (FromOrTo == "to"){
			if (winW <= 750 || mobile) {
				$('#dropDownSub').css('left', '0');
			}
			if (winW > 750 && !mobile) {
				$('#dropDownSub').css('left', '366px');
			}
		}
		});
		
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

		drop_down_click = false;

		if (!drop_down_click && !drop_down_show) {
			$('#dropDownSub').show();
			drop_down_click = true;
			drop_down_show = true;
		}
		if (!drop_down_click && drop_down_show) {
			$('#dropDownSub').hide();
			drop_down_click = true;
			drop_down_show = false;
		}
		if (drop_down_zone_change) {
			$('#dropDownSub').show();
			drop_down_click = true;
			drop_down_show = true;
		}
			
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
				
				if($(this).text()!="Detect Language "){
					isDetecting = false;
					sec_highest_lang_code = "";
					thr_highest_lang_code = "";
					highest_lang_code = "";
				}
			
				if($(this).text() !="Detect Language "){
				$('#selectFrom em').html($(this).text());
				}else{
				$('#selectFrom em').html("Detect");	
				}
				curr_pair.srcLang = $(this).text();
				
			} else {
				if($(this).text() !="Detect Language "){
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
						curr_pair.srcLang = findHighest(detectLanguage($(this).val()));
						$('#selectFrom em').html(curr_pair.srcLang);
						
				}
					
				
				}catch(e){
					console.log(e.message);
				}
				
				if (curr_pair.srcLang.indexOf("Detect") == -1) {
					translate(curr_pair,$('#textAreaId').val());
				}
			}
			else jQuery('#translationTest').html("Translation not yet available!");
			
			$('#dropDownSub').hide();
			drop_down_show = false;
			
		});
	});
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
	langpairer = $.trim(langPair.srcLang) + "|" + $.trim(langPair.dstLang);

	jQuery.ajax({
		url:'http://localhost:2737/translate',
		type:"GET",
		data:{
			'langpair': langpairer,
			'q': text,
		},
		success : smth,
		dataType: 'jsonp',
		failure : trad_ko
	});
}

function smth(dt){
	if (dt.responseStatus == 200) {
		jQuery('#translationTest').html(dt.responseData.translatedText);
	} else {
		trad_ko();
    }
}

function getPairs(){
	jQuery.ajax({
			url:'http://localhost:2737/listPairs',
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

		if (from_drop_down){
			if (isDetecting){
				$('#dropDownSub a').addClass('detecting_improbable');
			}
			$( "#dropDownSub span a:contains('"+highest_lang_code+"')" ).addClass("detect_choice detect_choice_1");
			$( "#dropDownSub span a:contains('"+sec_highest_lang_code+"')" ).addClass("detect_choice detect_choice_2");
			$( "#dropDownSub span a:contains('"+thr_highest_lang_code+"')" ).addClass("detect_choice detect_choice_3");
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
			
			if (curr_pair.srcLang.indexOf("Detect") == -1) {
				translate(curr_pair,$('#textAreaId').val());
			}
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

function detect_lang_interface(probabilities) {
	highest = 0;
	scd_highest = 0;
	thr_highest = 0;

	for (var i = 0; i < probabilities.length; i++) {
		if (probabilities[i] > highest) {
			thr_highest = scd_highest;
			scd_highest = highest;
			highest = probabilities[i];

			thr_highest_lang_code = scd_highest_lang_code;
			scd_highest_lang_code = highest_lang_code;
			highest_lang_code = probabilities_lang_code[i];
		}
		else if (probabilities[i] > scd_highest) {
			thr_highest = scd_highest;
			scd_highest = probabilities[i];

			thr_highest_lang_code = scd_highest_lang_code;
			scd_highest_lang_code = probabilities_lang_code[i];
		}
		else if (probabilities[i] > thr_highest) {
			thr_highest = probabilities[i];
			thr_highest_lang_code = probabilities_lang_code[i];
		}
	}

	winW = window.innerWidth;
	if (isDetecting){
		if (winW <= 750 || mobile) {
			$('#selectFrom em').html(" "+highest_lang_code+" ");
		}
		else {
			$('#selectFrom em').html(" "+highest_lang_code+"-detected ");
		}
	}
	$( window ).resize(function() {
		winW = window.innerWidth;
		if (isDetecting){
			if (winW <= 750 || mobile) {
				$('#selectFrom em').html(" "+highest_lang_code+" ");
			}
			else {
				$('#selectFrom em').html(" "+highest_lang_code+"-detected ");
			}
		}
	});
	fromLangCode = " "+highest_lang_code+" ";
	curr_pair.srcLang = " "+highest_lang_code+" ";
}

// test query:
//{"en": 0.99843828, "ca": 0.234241, "fr": 0.323123, "zh": 0.0}
function detectLanguage(text) {
	jQuery.ajax({
		url: "http://localhost:2737/identifyLang",
		type: "GET",
		data: {
			'q': text,
		},
		success : function(data) {
			return data;
		},
		dataType: "jsonp"
	});
	return undefined;
}

function findHighest(probabilities) {
	if (probabilities != undefined) {
		topLang = "";
		topProbability = -1.0;
		for (var lang in probabilities) {
			if (probabilities[lang] > topProbability) {
				topLang = lang;
				topProbability = probabilities[lang];
			}
		}
		return topLang;
	}
	return "Detect language";
}