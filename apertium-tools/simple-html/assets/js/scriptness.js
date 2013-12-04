var pairs = new Array();
var curr_pair = new Object();
var srcLangs = new Array();
var dstLangs = new Array();
var grayedOuts = new Array();
var isDetecting = false;

var abbreviations = {
    'Spanish': 'es',
    'Catalan': 'ca',
    'Catalan (Valencian)': 'ca_valencia',
    'Galician': 'gl',
    'Portuguese': 'pt',
    'Brazilian Portuguese': 'pt_BR',
    'Occitan': 'oc',
    'Aranese': 'oc_aran',
    'English': 'en',
    'French': 'fr',
    'Esperanto': 'eo',
    'Romanian': 'ro',
    'Welsh': 'cy',
    'Basque': 'eu',
    'Breton': 'br',
    'Norwegian Bokmål': 'nb',
    'Norwegian Nynorsk': 'nn',
    'Swedish': 'sv',
    'Danish': 'da',
    'Asturian': 'ast',
    'Icelandic': 'is',
    'Macedonian': 'mk',
    'Bulgarian': 'bg',
    'Italian': 'it',
    'Tatar': 'tat',
    'Kazakh': 'kaz'
}

function ajaxSend() { $("#loading-indicator").show(); }
function ajaxComplete() { $("#loading-indicator").hide(); }
$(document).ajaxSend(ajaxSend);
$(document).ajaxComplete(ajaxComplete);

$(document).ready(function () {
    curr_pair.srcLang = "";
    curr_pair.dstLang = "";

    $("#textAreaId").keyup(function (event) {
        if (event.keyCode == 32 || event.keyCode == 190 || event.keyCode == 191 || event.keyCode == 49 || event.keyCode == 59 || event.keyCode == 13) {
            try {
                if (curr_pair.srcLang.indexOf("Detect") != -1) {
                    isDetecting = true;
                }

                if (isDetecting) {
                    curr_pair.srcLang = detectLanguage($(this).val());
                    curr_pair.srcLang = abbreviations[curr_pair.srcLang];
                    $('#selectFrom em').html(curr_pair.srcLang);
                }


            } catch (e) {
                console.log(e.message);
            }

            translate(curr_pair, $('#textAreaId').val());

            //$(this).val($(this).val()+' ');
            //alert(detectLanguage($(this).val()));
            return false;
        }
/*
		if(event.keyCode==13){
			
			try{
				if(curr_pair.srcLang.indexOf("Detect") !=-1){
					curr_pair.srcLang = detectLanguage($(this).val());
					curr_pair.srcLang = abbreviations[curr_pair.srcLang];
					$('#selectFrom em').html(curr_pair.srcLang);
			}

			}catch(e){
				console.log(e.message);
			}
		
			translate(curr_pair,$('#textAreaId').val());
			//$(this).val($(this).val()+'\n'); ;
			
			return false;
		}
		//alert(event.keyCode);
		*/
    });

    $("#inputBox").submit(function () {
        try {
            try {
                if (curr_pair.srcLang.indexOf("Detect") != -1) {
                    curr_pair.srcLang = detectLanguage($(this).val());
                    curr_pair.srcLang = abbreviations[curr_pair.srcLang];
                    $('#selectFrom em').html(curr_pair.srcLang);
                }
            } catch (e) {
                console.log(e.message);
            }

            translate(curr_pair, $('#textAreaId').val());
            return false;
        } catch (e) {
            console.log(e.message);
        }
    });


    $('#dropDownSub').hide();

    var FromOrTo;

    $('#swapLanguages').click(function () {
        fromText = $('#selectFrom em').text();
        toText = $('#selectTo em').text();
        $('#selectTo em').html(fromText);
        $('#selectFrom em').html(toText);

        curr_pair.dstLang = fromText;
        curr_pair.srcLang = toText;
    });
/*
	$('.itemSelect').toggle(function(){
		if($(this).attr("id")=="selectFrom"){
			FromOrTo="from";
			$('#dropDownSub').hide();
			$('#dropDownSub').css('margin-left',00);
			
		} else {
			FromOrTo = "to";
			$('#dropDownSub').hide();
			$('#dropDownSub').css('margin-left',287);
		}
			$('#dropDownSub').show();
		
	}, function(){
		$('#dropDownSub').hide()	
	});
*/
/*
	$('#dropDownSub a').click(function(){
		
		
		$('#dropDownSub a').removeClass('language-selected');
		$(this).addClass('language-selected');
		
		if(FromOrTo=="from"){	
			$('#selectFrom em').html($(this).text());
			curr_pair.srcLang = $(this).text();
			
		} else {
			$('#selectTo em').html($(this).text());
			curr_pair.dstLang = $(this).text();
		}
		matchFound= false
			
		for(var it in window.pairs){	
			if(parsePair(curr_pair)==window.pairs[it])
				matchFound=true;
		}
		if(matchFound)
			$('#translationTest').html("Translation will be displayed here!");
		else $('#translationTest').html("Translation not yet available!");
		
	
	});
	*/

    $('#selectTo').click(function () {
        loler = curr_pair.srcLang + "|";
        aaa = 0;
        for (it in window.pairs) {
            //console.log(window.pairs, window.pairs[it], loler, window.pairs[it].indexOf(loler));
            if (window.pairs[it].indexOf(loler) != -1) {
                //grayedOuts[aaa] = window.pairs[it].substr(-3,3);
                grayedOuts[aaa] = window.pairs[it].split('|')[1];
                //console.log(grayedOuts[aaa]);
                aaa++;
            }
        }
    });

    getPairs();
});

$(document).click(function () {
    $('#dropDownSub').hide();
});

function getLangByCode(code) {
    language = code
    //FIXME: currently not able to parse abbreviations
    for (abbv in abbreviations) {
        if (abbv == code) {
            language = abbreviations[abbv];
        }
    }
    return language;
}

function translate(langPair, text) {
    langpairer = $.trim(langPair.srcLang) + "|" + $.trim(langPair.dstLang);

    $.ajax({
        url: 'http://localhost:2737/translate',
        type: "GET",
        data: {
            'langpair': langpairer,
            'q': text,
        },
        success: smth,
        dataType: 'jsonp',
        failure: trad_ko
    });

}

function smth(dt) {
    if (dt.responseStatus == 200) {
        $('#translationTest').html(dt.responseData.translatedText);
    } else {
        trad_ko();
    }
}

function getPairs() {
    $.ajax({
        url: 'http://localhost:2737/listPairs',
        type: "GET",
        success: trad_ok,
        dataType: 'jsonp',
        failure: trad_ko
    });
}


function trad_ko() {
    $('#translationTest').html("Translation not yet available!");
}

function trad_ok(dt) {

    if (dt.responseStatus == 200) {

        $('#translationTest').html(" ");
        all = dt.responseData;

        for (var i in all) {
            l = all[i].sourceLanguage + '|' + all[i].targetLanguage;
            window.pairs[i] = l;
            srcLangs[i] = all[i].sourceLanguage;
            srcLangs = $.unique(srcLangs);

            dstLangs[i] = all[i].targetLanguage;
            dstLangs = $.unique(dstLangs);

            //$('#translationTest').append(l+'\n');
        }

        populateTranslationList("#column-group-", srcLangs);

    } else {
        trad_ko();
    }
}

function parsePair(pr) {
    parsedPair = null;
    pr.srcLang = $.trim(pr.srcLang);
    pr.dstLang = $.trim(pr.dstLang);

    if (pr.srcLang == "English") parsedPair = "en";
    else if (pr.srcLang == "Spanish") parsedPair = "es";
    else if (pr.srcLang == "Portuguese") parsedPair = "pt";
    else if (pr.srcLang == "Catalan") parsedPair = "ca";
    else if (pr.srcLang == "French") parsedPair = "fr";

    if (pr.dstLang == "Catalan") parsedPair += "|ca";
    else if (pr.dstLang == "English") parsedPair += "|en";
    else if (pr.dstLang == "Spanish") parsedPair += "|es";
    else if (pr.dstLang == "French") parsedPair += "|fr";
    else if (pr.dstLang == "Portuguese") parsedPair += "|pt";

    return parsedPair;
}

function populateTranslationList(elementClass, langArr) {

    $(".column-group").html("");
    $("#column-group-1").append("<span> <a href='#' class='language-selected' > Detect Language </a></span>");

    column_num = 1;
    //console.log(langArr);
    for (it in langArr) {

        $(elementClass + column_num).append("<span> <a href='#' class='language-selected' > " + getLangByCode(langArr[it]) + " </a></span>");

        if ($(elementClass + column_num).children().length > 5) {
            column_num++;
        }

    }

    for (it in grayedOuts)
    $("a:contains( " + grayedOuts[it] + " )").removeClass('language-selected');

    $('.itemSelect').toggle(function () {
        $('.column-group').removeClass('language-selected');

        if ($(this).attr("id") == "selectFrom") {

            populateTranslationList("#column-group-", srcLangs);

            FromOrTo = "from";
            $('#dropDownSub').hide();
            $('#dropDownSub').addClass('selectFromSub');
            $('#dropDownSub').css('margin-left', 00);

        } else {

            populateTranslationList("#column-group-", dstLangs);

            FromOrTo = "to";
            $('#dropDownSub').hide();
            $('#dropDownSub').css('margin-left', 287);

            $('#dropDownSub').removeClass('selectFromSub');
            //find_smth(curr_pair.srcLang);
            //$('#dropDownSub a').addClass('language-selected');
        }

        $('#dropDownSub').show();

    }, function () {
        $('#dropDownSub').hide()
    });

    $('#dropDownSub a').click(function () {

        $('#dropDownSub a').removeClass('language-selected');
        $(this).addClass('language-selected');

        if (FromOrTo == "from") {

            if ($(this).text() != " Detect Language ") isDetecting = false;

            $('#selectFrom em').html($(this).text());
            curr_pair.srcLang = $(this).text();

        } else {
            $('#selectTo em').html($(this).text());
            curr_pair.dstLang = $(this).text();
        }
        matchFound = false;

        //FIXME: if (curr_pair in window.pairs) ??
        for (var it in window.pairs) {
            if (parsePair_lol(curr_pair) == window.pairs[it]) matchFound = true;
        }

        if (matchFound) {

            try {
                if (curr_pair.srcLang.indexOf("Detect") != -1) {
                    curr_pair.srcLang = detectLanguage($(this).val());

                    curr_pair.srcLang = abbreviations[curr_pair.srcLang];

                    $('#selectFrom em').html(curr_pair.srcLang);

                }

            } catch (e) {
                console.log(e.message);
            }

            translate(curr_pair, $('#textAreaId').val());
        } else $('#translationTest').html("Translation not yet available!");
    });
}

function strcmp(a, b) {
    return (a < b ? -1 : (a > b ? 1 : 0));
}

function parsePair_lol(pr) {

    parsedPair = null;
    pr.srcLang = $.trim(pr.srcLang);
    pr.dstLang = $.trim(pr.dstLang);

    parsedPair = pr.srcLang;
    parsedPair += "|" + pr.dstLang;
    return parsedPair;
}

function find_smth(lol) {
    aaa = 0;
    loler = "|" + lol;

    for (it in window.pairs) {
        if (window.pairs[it].indexOf(loler) != -1) {
            aaa++;
        }
    }
}

$(document).ready(function() {
    $('#analyze').click(function () {
        analyze();
    });

    $('#generate').click(function () {
        generate();
    });

    $('#analysisForm, #generateForm').submit(function () {
        return false;
    });

    $('.nav a').click(function () {
        $('.nav li').removeClass('active');
        $(this).parent('li').addClass('active');
        $('.modeContainer:not(#' + $(this).prop('id') + 'Container)').hide({ queue: false });
        $('#' + $(this).prop('id') + 'Container').show({ queue: false }); 
    });
    
    $.ajax({
        url: 'http://localhost:2737/listAnalyzers',
        type: 'GET',
        success: function (data) {
            data = formatModes(data);
            for(var i = 0; i < data.length; i++)
                $('#analyzerMode').append($('<option></option').val(data[i][0]).text(data[i][1]));
        },
        dataType: 'jsonp',
        beforeSend: ajaxSend,
        complete: ajaxComplete
    });

    $.ajax({
        url: 'http://localhost:2737/listGenerators',
        type: 'GET',
        success: function (data) {
            data = formatModes(data);
            for(var i = 0; i < data.length; i++)
                $('#generatorMode').append($('<option></option').val(data[i][0]).text(data[i][1]));
        },
        dataType: 'jsonp',
        beforeSend: ajaxSend,
        complete: ajaxComplete
    });
});


function formatModes(modes) {
    var modesArr = [], toReturn = []
    for(var val in modes)
        modesArr.push(val);
    for(var val in modes) {
        if(val.indexOf('-') === -1)
            toReturn.push([val, val]);
        else {
            var mode = val.split('-')[0];
            if(modesArr.indexOf(mode) === -1)
                toReturn.push([val, mode]);
            else
                toReturn.push([val, mode + ' (' + val + ')']);
        }   
    }
    return toReturn;    
}

function analyze () {
    $("#morphAnalyzerOutput").animate({ opacity: 0.5 });
    $.ajax({
        url: 'http://localhost:2737/analyze',
        type: 'POST',
        data: {
            'mode': $('#analyzerMode').val(),
            'q': $('#morphAnalyzerInput').val()
        },
        success: function (data) {
            var regex = /([^<]*)((<[^>]+>)*)/g;
            $('#morphAnalyzerOutput').empty();
            for(var i = 0; i < data.length; i++) {
                var leftTD = $('<td class="left-part"></td>');
                var strong = $('<strong></strong>').text(data[i][1].trim());
                var arrow = $('<span></span>').html('&nbsp;&nbsp;&#8620;');
                leftTD.append(strong).append(arrow)
                
                var rightTD = $('<td class="right-part"></td>');
                var splitUnit = data[i][0].split('/');
                var span = $('<span></span>').html(formatUnit(splitUnit[1]));
                rightTD.append(span);
                
                if(data[i][0][0] === '*')
                    span.css({color: 'darkred'})
                
                var tr = $('<tr></tr>').append(leftTD).append(rightTD);
                $('#morphAnalyzerOutput').append(tr);
                
                joinedMorphemes = {}
                unitsWithMorphemes = []
                for(var j = 2; j < splitUnit.length; j++) {
                    var unit = splitUnit[j];
                    if(unit.match(regex).length > 2) {
                        var matches = unit.match(regex);
                        for(var k = 1; k < matches.length - 1; k++) {
                            if(joinedMorphemes[matches[k]])
                                joinedMorphemes[matches[k]].push(unit);
                            else
                                joinedMorphemes[matches[k]] = [unit];
                        }
                    }
                    else {
                        var unitDiv = $('<div></div>').html(formatUnit(unit));
                        span.append(unitDiv);
                    }
                }
                $.each(joinedMorphemes, function (joinedMorpheme, units) {
                    var morphemeDiv = $('<div></div>').html(formatUnit(joinedMorpheme));
                    span.append(morphemeDiv);
                    for(var j = 0; j < units.length; j++) {
                        var unitDiv = $('<div style="margin-left: 30px;"></div>').html(formatUnit(units[j].match(regex)[0]));
                        span.append(unitDiv);
                    }
                });
                $("#morphAnalyzerOutput").animate({ opacity: 1 });
            }
        },
        dataType: 'jsonp',
        failure: function (xhr, textStatus, error) {
            $('#morphGenOutput').text(error);
            $("#morphAnalyzerOutput").animate({ opacity: 1 });
        },
        beforeSend: ajaxSend,
        complete: ajaxComplete
    });
}

function formatUnit (unit) {
    var tagRegex = /<([^>]+)>/g;
    var tags = [];
    var tagMatch = tagRegex.exec(unit);
    while(tagMatch != null) {
        tags.push(tagMatch[1]);
        tagMatch = tagRegex.exec(unit);
    }
    var tagStartLoc = unit.indexOf('<');
    return unit.substring(0, tagStartLoc != -1 ? tagStartLoc : unit.length) + (tags.length > 0 ? '&nbsp;&nbsp;&#8612;&nbsp;&nbsp;' + tags.join(' &#8901; ') : '');
}

function generate () {
    $("#morphGenOutput").animate({ opacity: 0.5 });
    $.ajax({
        url: 'http://localhost:2737/generate',
        type: 'POST',
        data: {
            'mode': $('#generatorMode').val(),
            'q': $('#morphGeneratorInput').val()
        },
        success: function (data) {
            $('#morphGenOutput').empty();
            for(var i = 0; i < data.length; i++) {
                var div = $('<div class="generation" data-toggle="tooltip" data-placement="auto" data-html="true"></div>');
                var strong = $('<strong></strong>').text(data[i][1].trim());
                var span = $('<span></span>').html('&nbsp;&nbsp;&#8620;&nbsp;&nbsp;' + data[i][0]);
                div.append(strong).append(span);
                $('#morphGenOutput').append(div);
            }
            $("#morphGenOutput").animate({ opacity: 1 });
        },
        dataType: 'jsonp',
        failure: function (xhr, textStatus, error) {
            $('#morphGenOutput').text(error);
            $("#morphGenOutput").animate({ opacity: 1 });
        },
        beforeSend: ajaxSend,
        complete: ajaxComplete
    });
}
